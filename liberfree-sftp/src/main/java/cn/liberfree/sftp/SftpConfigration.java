package cn.liberfree.sftp;

/**
 * @author: zhangchao
 * @time: 2018-10-31 10:27
 **/
public class SftpConfigration {

    private String username;
    private String password;
    private String host;
    private int port;
    private String privateKey;


    public SftpConfigration(String username, String password, String host, int port, String privateKey) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
    }

    private String poolName = "SftpConfigrationPool";   //连接池名字
    private Integer minConnections = 3; //空闲时最小连接数
    private Integer maxConnections = 10;    //空闲时最大连接数
    private Integer initConnection = 5; //初始化连接数量
    private Long connTimeOut = 1L;   //重复获得连接的频率
    private Integer maxActiveConnections = 100; //最大允许的连接数
    private Long ConnectionTimeOut = 1000*60*20L;   //连接超时时间
    private Boolean isCurrentConnection = true;//是否获得当前连接
    private Boolean isCheckPool = true;//是否定时检查连接池
    private Long lazyCheck = 1000L;   //延迟多少时间后开始检查
    private Long periodCheck = 1000*60L; //检查频率


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    public Integer getMinConnections() {
        return minConnections;
    }

    public void setMinConnections(Integer minConnections) {
        this.minConnections = minConnections;
    }

    public Integer getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(Integer maxConnections) {
        this.maxConnections = maxConnections;
    }

    public Integer getInitConnection() {
        return initConnection;
    }

    public void setInitConnection(Integer initConnection) {
        this.initConnection = initConnection;
    }

    public Long getConnTimeOut() {
        return connTimeOut;
    }

    public void setConnTimeOut(Long connTimeOut) {
        this.connTimeOut = connTimeOut;
    }

    public Integer getMaxActiveConnections() {
        return maxActiveConnections;
    }

    public void setMaxActiveConnections(Integer maxActiveConnections) {
        this.maxActiveConnections = maxActiveConnections;
    }

    public Long getConnectionTimeOut() {
        return ConnectionTimeOut;
    }

    public void setConnectionTimeOut(Long connectionTimeOut) {
        ConnectionTimeOut = connectionTimeOut;
    }

    public Boolean getCurrentConnection() {
        return isCurrentConnection;
    }

    public void setCurrentConnection(Boolean currentConnection) {
        isCurrentConnection = currentConnection;
    }

    public Boolean getCheckPool() {
        return isCheckPool;
    }

    public void setCheckPool(Boolean checkPool) {
        isCheckPool = checkPool;
    }

    public Long getLazyCheck() {
        return lazyCheck;
    }

    public void setLazyCheck(Long lazyCheck) {
        this.lazyCheck = lazyCheck;
    }

    public Long getPeriodCheck() {
        return periodCheck;
    }

    public void setPeriodCheck(Long periodCheck) {
        this.periodCheck = periodCheck;
    }
}
