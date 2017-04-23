package com.wei.kiwi.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 创建一个pool对象
 * Created by wjj on 4/8/17.
 */
public class ConnectionPool {

    private Logger logger = LoggerFactory.getLogger(ConnectionPool.class);


    /**
     * the size of pool
     */
    private AtomicInteger size = new AtomicInteger(0);



    private PoolProperties poolProperties;

    /**
     * 忙队列
     */
    private LinkedBlockingQueue<InnerPooledConnection> busy;

    /**
     * 空闲队列
     */
    private LinkedBlockingQueue<InnerPooledConnection> idle;


    private ScheduledExecutorService cleanService;




    /**
     * Pool closed flag
     */
    private volatile boolean closed = false;



    public ConnectionPool(PoolProperties poolProperties) {
        init(poolProperties);
    }


    /**
     * 初始化函数,初始化initialSize connection
     * @param p
     */
    private void init(PoolProperties p){
        this.poolProperties = p;
        this.busy = new LinkedBlockingQueue<InnerPooledConnection>();
        this.idle = new LinkedBlockingQueue<InnerPooledConnection>();


        InnerPooledConnection [] init = new InnerPooledConnection[this.getPoolProperties().getInitialSize()];
        try {
            for(int i=0; i < this.getPoolProperties().getInitialSize(); i++){
                InnerPooledConnection ipc = createInnerPooledConnection();
                init[i] = ipc;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            for(int i=0; i< init.length; i++){
                returnConnection(init[i]);
            }
        }

        //start clean thread
        cleanService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r,"wei pool cleaner thread");
                t.setDaemon(true);
                return t;
            }

        });
        long timeSleep = this.poolProperties.getTimeBetweenEvictionRunsMillis();
        cleanService.scheduleWithFixedDelay(new PoolCleaner( this),
                timeSleep,timeSleep,TimeUnit.MILLISECONDS);
    }


    public Connection getConnection() throws SQLException {
        int maxWait = this.getPoolProperties().getMaxWait();
        InnerPooledConnection pooledConnection =  borrowConnection(maxWait);
        return setupConnection(pooledConnection);

    }

    /**
     * 向池借一个connection
     * @param maxWait - time to wait, overrides the maxWait from the properties,
     * set to -1 if you wish to use maxWait, 0 if you wish no wait time.
     * @return
     * @throws SQLException
     */
    protected InnerPooledConnection borrowConnection(int maxWait) throws SQLException {
        if(isClosed()){
            throw  new SQLException("Connection Pool closed");
        }
        InnerPooledConnection pooledConnection = null;
        long startTime = System.currentTimeMillis();
        while (true){
            pooledConnection = idle.poll();

            if(pooledConnection != null){ //从空闲队列拿一个
                busy.add(pooledConnection);
                return pooledConnection;
            }
            //如果当前大小小于最大大小 //不是很精确
            if(size.get() < this.getPoolProperties().getMaxActive() ){
                //增加大小
                size.incrementAndGet();
                pooledConnection = createInnerPooledConnection();
                return pooledConnection;
            }
            if(maxWait == 0){
                throw new SQLException("exceed maxActive "+ this.getPoolProperties().getMaxActive()+" count");
            }
            long poolTime =Math.max(0,maxWait - (System.currentTimeMillis()- startTime)) ;
            try {
                pooledConnection = idle.poll(poolTime,TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(pooledConnection == null){
                if(System.currentTimeMillis()- startTime > maxWait){
                    throw new SQLException(
                            "[" + Thread.currentThread().getName()+"] " +
                                    "Timeout: Pool empty. Unable to fetch a connection in " + (maxWait / 1000) +
                                    " seconds, none available[size:"+size.get() +"; busy:"+busy.size()+"; idle:"+idle.size()+"; lastwait:"+poolTime+"]."
                    );
                }
                continue;
            }
            return pooledConnection;
        }
    }



    protected Connection setupConnection(InnerPooledConnection con) {

        ProxyConnection proxyConnection = new ProxyConnection(this,con);

        return (Connection) Proxy.newProxyInstance(ConnectionPool.class.getClassLoader(),
                new Class[]{Connection.class},proxyConnection);


    }

    private InnerPooledConnection createInnerPooledConnection() throws SQLException {

        InnerPooledConnection innerPooledConnection = new InnerPooledConnection(this);

        Connection connection =  DriverManager.getConnection(poolProperties.getUrl(),poolProperties.getUser(),
                poolProperties.getPassword());

        logger.info("create connection");

        innerPooledConnection.setConnection(connection);

        return innerPooledConnection;


    }
    protected void returnConnection(InnerPooledConnection con) {
        busy.remove(con);
        idle.add(con);
    }

    public void close(){
        if(closed){
            return;
        }
        closed = true;
        cleanService.shutdown();

//        for(Connection c: busy){
//            try {
//                c.close();
//            } catch (SQLException e) {
//
//            }
//        }
    }


    public boolean isClosed(){
        return this.closed;
    }

    private void testWhileIdle(){
        if(idle.size() ==0){
            return;
        }
        Iterator<InnerPooledConnection> iterator = idle.iterator();
        while (iterator.hasNext()){
            InnerPooledConnection ipc = iterator.next();
            if(!ipc.validate()){
                //探测失败之后需要释放连接
                release(ipc);
                logger.info("delete InnerPooledConnection "+ ipc);
                iterator.remove();
            }
        }
        logger.info("testWhileIdle ");
    }


    private void checkAbandoned(){
        logger.info("check checkAbandoned");
    }

    public PoolProperties getPoolProperties() {
        return poolProperties;
    }

    public void setPoolProperties(PoolProperties poolProperties) {
        this.poolProperties = poolProperties;
    }


    /**
     * thread safe way to release a connection
     * @param con PooledConnection
     */
    protected void release(InnerPooledConnection con) {
        if (con == null)
            return;
        con.release();
        size.decrementAndGet();
    }



    /**
     * 清理任务
     */
    protected    class PoolCleaner implements Runnable{
//
        ConnectionPool pool;

        public PoolCleaner( ConnectionPool pool) {
            this.pool = pool;
        }

        @Override
        public void run() {

            if(pool.getPoolProperties().isRemoveAbandoned()){
                pool.checkAbandoned();
            }
            if(pool.getPoolProperties().isTestWhileIdle()){
                pool.testWhileIdle();
            }
        }
    }
}
