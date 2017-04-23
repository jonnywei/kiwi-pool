package com.wei.kiwi.pool;

import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wjj on 4/23/17.
 */

public class MaxWaitTimeTest extends BaseDataSourceSuite{




    @Override
    public void createDefaultDataSource() {
        poolProperties.setDriverClassName("com.mysql.jdbc.Driver");
        poolProperties.setUrl("jdbc:mysql://localhost:3306/testdb");
        poolProperties.setUser("root");
        poolProperties.setPassword("root");
        poolProperties.setMaxWait(10000);
        poolProperties.setInitialSize(3);
        poolProperties.setMaxActive(5);
        poolProperties.setTestWhileIdle(true);

        weiDataSource.setPoolProperties(poolProperties);
    }



    @Test
    public void testMaxWait(  ) throws InterruptedException, SQLException {

        ExecutorService es = Executors.newFixedThreadPool(100);

        for(int i=0; i< 40; i++){
            es.submit(new Runnable() {
                @Override
                public void run() {
                    getUserFromDb();
                }
            });
        }


        for(int i=0; i< 30; i++){
            es.submit(new Runnable() {
                @Override
                public void run() {
                    getUserFromDb();
                }
            });
        }

        System.out.println( "Hello World!" );

        es.awaitTermination(60,TimeUnit.SECONDS);
    }



    private  void getUserFromDb() {
        Connection connection = null;
        try {
            connection =  weiDataSource.getConnection();
            try {
                Thread.sleep(1000* 3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Statement statement =  connection.createStatement();
            ResultSet rs =statement.executeQuery("select * from user");
            while (rs.next()){
//               System.out.println(rs.getString(1));
            }
            System.out.println("get user over ");

            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(connection != null){
                try {
                    connection.close();
                } catch (SQLException e) {

                }
            }
        }
    }
}
