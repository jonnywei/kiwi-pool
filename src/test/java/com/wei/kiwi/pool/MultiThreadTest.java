package com.wei.kiwi.pool;

 import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */

public class MultiThreadTest extends BaseDataSourceSuite
{

    @Override
    public void createDefaultDataSource() {
        super.createDefaultDataSource();
    }

    @Test
    public void testGetConnection(  ) throws InterruptedException, SQLException {

         weiDataSource.getConnection();

        ExecutorService es = Executors.newFixedThreadPool(100);

        for(int i=0; i< 40; i++){
            es.submit(new Runnable() {
                @Override
                public void run() {
                    getUserFromDb();
                }
            });
        }

        for(int i=0; i< 100; i++){
            getUserFromDb();

        }
        System.out.println( "senc run complete" );
        Thread.sleep(1000* 60);


        for(int i=0; i< 30; i++){
            es.submit(new Runnable() {
                @Override
                public void run() {
                    getUserFromDb();
                }
            });
        }

        System.out.println( "Hello World!" );
        es.shutdown();
    }

    private  void getUserFromDb() {
        Connection connection = null;
        try {
            connection =  weiDataSource.getConnection();
            try {
                Thread.sleep(100* 3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Statement statement =  connection.createStatement();
           ResultSet rs =statement.executeQuery("select * from user");
           while (rs.next()){
//               System.out.println(rs.getString(1));
           }
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
