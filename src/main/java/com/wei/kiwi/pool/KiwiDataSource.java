package com.wei.kiwi.pool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 连接池管理
 * Created by wjj on 4/6/17.
 */
public class KiwiDataSource implements DataSource {


    private PoolProperties poolProperties;

    private volatile ConnectionPool pool;



    public PoolProperties getPoolProperties() {
        return poolProperties;
    }

    public void setPoolProperties(PoolProperties poolProperties) {
        this.poolProperties = poolProperties;
    }


    public Connection getConnection() throws SQLException {
        if(pool == null){
            createPool();
        }
        return pool.getConnection();
    }

    public Connection getConnection(String username, String password)
            throws SQLException {

        return getConnection();

    }

    private  synchronized void createPool(){
        if(pool != null){
            return;
        }
        pool = new ConnectionPool(poolProperties);
    }


    public void close(){
        if(pool == null){
            return;
        }
        pool.close();
    }





    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public void setLoginTimeout(int seconds) throws SQLException {

    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}
