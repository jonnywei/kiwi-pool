package com.wei.kiwi.pool;

import org.junit.After;
import org.junit.Before;

/**
 * Created by wjj on 4/23/17.
 */
public class BaseDataSourceSuite {

    protected PoolProperties poolProperties = new PoolProperties();
    protected final WeiDataSource weiDataSource = new WeiDataSource();

    @Before
    public void begin(){
        createDefaultDataSource();
    }

    public void createDefaultDataSource(){
        poolProperties.setDriverClassName("com.mysql.jdbc.Driver");
        poolProperties.setUrl("jdbc:mysql://localhost:3306/testdb");
        poolProperties.setUser("root");
        poolProperties.setPassword("root");

        poolProperties.setInitialSize(3);
        poolProperties.setMaxActive(10);
        weiDataSource.setPoolProperties(poolProperties);
    }


    @After
    public void tearDown(){
        weiDataSource.close();
    }
}
