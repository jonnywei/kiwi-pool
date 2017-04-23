package com.wei.kiwi.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 线程池内部connection对象
 * Created by wjj on 4/8/17.
 */
public class InnerPooledConnection {

    private Logger logger = LoggerFactory.getLogger(InnerPooledConnection.class);


    private Connection connection;

    private int status;

    private ConnectionPool parent;

    public InnerPooledConnection(ConnectionPool parent) {
        this.parent = parent;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public boolean validate(){

        String sql = parent.getPoolProperties().getValidationQuery();
        int timeout = parent.getPoolProperties().getValidationQueryTimeout();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            if(timeout >0){
                statement.setQueryTimeout(timeout);
            }
            statement.executeQuery(sql);
            statement.close();
            return true;
        } catch (SQLException e) {
            logger.warn("sql validation error ", e);
           if(statement !=null){
               try {
                   statement.close();
               } catch (SQLException ex) {
//                   e.printStackTrace();
               }
           }
        }
        return false;


    }


    public boolean release() {
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException ignored) {

            }
        }
        return true;
    }
}
