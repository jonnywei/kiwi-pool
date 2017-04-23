package com.wei.kiwi.pool;

/**
 * Created by wjj on 4/8/17.
 */
public class PoolProperties {

    String host;
    String port;
    String user;
    String password;

    String driverClassName;

    String url;

    /**
     * (int) The maximum number of active connections that can be allocated from this pool at the same time. The default value is 100
     */
    private int maxActive = 100;


    private int initialSize =10 ;

    /**
     * (int) The maximum number of milliseconds that the pool will wait (when there are no available connections) for
     * a connection to be returned before throwing an exception. Default value is 30000 (30 seconds)
     */

    private int maxWait = 30000;


    /**
     * (int) The minimum number of established connections that should be kept in the pool at all times.
     * The connection pool can shrink below this number if validation queries fail.
     * Default value is derived from initialSize:10 (also see testWhileIdle)

     */
    private int  minIdle = initialSize;



    private volatile int timeBetweenEvictionRunsMillis = 5000;


    private volatile int minEvictableIdleTimeMillis = 60000;


    private volatile String validationQuery;


    private volatile int validationQueryTimeout = -1;


    private volatile boolean removeAbandoned = false;


    private volatile int removeAbandonedTimeout = 60;

    /**
     * (boolean) The indication of whether objects will be validated by the idle object evictor (if any).
     * If an object fails to validate, it will be dropped from the pool.
     * The default value is false and this property has to be set in order for
     * the pool cleaner/test thread is to run (also see timeBetweenEvictionRunsMillis)
     */
    private volatile boolean testWhileIdle = false;




    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }


    public int getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }


    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public int getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public int getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }
}
