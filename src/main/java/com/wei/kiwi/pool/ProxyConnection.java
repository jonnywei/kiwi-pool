package com.wei.kiwi.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理正常的Connection
 * Created by wjj on 4/8/17.
 */
public class ProxyConnection implements InvocationHandler {

    private Logger logger = LoggerFactory.getLogger(ProxyConnection.class);

    private final String METHOD_NAME_CLOSE ="close";

    private InnerPooledConnection innerPooledConnection;

    private ConnectionPool connectionPool;


    public ProxyConnection(ConnectionPool connectionPool ,InnerPooledConnection innerPooledConnection) {
        this.connectionPool = connectionPool;
        this.innerPooledConnection = innerPooledConnection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().equals(METHOD_NAME_CLOSE)){
             connectionPool.returnConnection(innerPooledConnection);
        }else {
            return method.invoke(innerPooledConnection.getConnection(),args);
        }
        return null;
    }
}
