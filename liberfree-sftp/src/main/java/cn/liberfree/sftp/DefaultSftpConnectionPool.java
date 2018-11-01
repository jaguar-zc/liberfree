package cn.liberfree.sftp;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author: zhangchao
 **/
public class DefaultSftpConnectionPool implements SftpConnectionPool {

    public static Logger logger  = LoggerFactory.getLogger(DefaultSftpConnectionPool.class.getName());


    private SftpConfigration configration;
    private Boolean isActive = false;
    private Integer contActive = 0; //创建记录总数

    private List<ChannelSftp> freeConnections = new Vector<ChannelSftp>();    //空闲链接
    private List<ChannelSftp> activeConnections = new Vector<ChannelSftp>();  //活动连接
    private static ThreadLocal<ChannelSftp> threadLocal = new ThreadLocal<ChannelSftp>();

    public DefaultSftpConnectionPool(SftpConfigration configration) {
        this.configration = configration;
        if(logger.isDebugEnabled()){
            logger.debug("==========================================================");
            logger.debug("username:{}",this.configration.getUsername());
            logger.debug("password:{}",this.configration.getPassword());
            logger.debug("host:{}",this.configration.getHost());
            logger.debug("port:{}",this.configration.getPort());
            logger.debug("privateKey:{}",this.configration.getPrivateKey());
            logger.debug("poolName:{}",this.configration.getPoolName());
            logger.debug("minConnections:{}",this.configration.getMinConnections());
            logger.debug("maxConnections:{}",this.configration.getMaxConnections());
            logger.debug("initConnection:{}",this.configration.getInitConnection());
            logger.debug("connTimeOut:{}",this.configration.getConnTimeOut());
            logger.debug("maxActiveConnections:{}",this.configration.getMaxActiveConnections());
            logger.debug("ConnectionTimeOut:{}",this.configration.getConnectionTimeOut());
            logger.debug("isCurrentConnection:{}",this.configration.getCurrentConnection());
            logger.debug("isCheckPool:{}",this.configration.getCheckPool());
            logger.debug("lazyCheck:{}",this.configration.getLazyCheck());
            logger.debug("periodCheck:{}",this.configration.getPeriodCheck());
            logger.debug("==========================================================");
        }
        init();
        checkPool();
    }
//
//    private static SftpConnectionPool pool;
//    public static SftpConnectionPool getPool(){
//        if(pool == null){
//            pool = new DefaultSftpConnectionPool(configration);
//            return pool;
//        }else{
//            return pool;
//        }
//    }
    public ChannelSftp newChannelSftp() throws JSchException {
        JSch jsch = new JSch();
        if (this.configration.getPrivateKey() != null) {
            jsch.addIdentity(this.configration.getPrivateKey());// 设置私钥
        }
        Session session = jsch.getSession(this.configration.getUsername(), this.configration.getHost(), this.configration.getPort());
        if (this.configration.getPassword() != null) {
            session.setPassword(this.configration.getPassword());
        }
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");

        session.setConfig(config);
        session.connect();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        return (ChannelSftp) channel;
    }
    private void init() {
        try{
            for(int i = 0;i < this.configration.getInitConnection();i++){
                ChannelSftp conn = newChannelSftp();
                if(conn!=null){
                    this.freeConnections.add(conn);
                    this.contActive++;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public ChannelSftp getConnection() {
        ChannelSftp conn = null;
        try {
            if(this.contActive < this.configration.getMaxActiveConnections()){
                if(this.freeConnections.size() > 0){
                    conn = this.freeConnections.get(0);
                    if(conn!=null){
                        this.threadLocal.set(conn);
                    }
                    this.freeConnections.remove(0);
                }else{
                    conn = newChannelSftp();
                }
            }else{
                wait(this.configration.getConnTimeOut());
                conn = getConnection();
            }
            if(isVaild(conn)){
                this.activeConnections.add(conn);
                this.contActive++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
    public Boolean isVaild(ChannelSftp conn){
        if(conn == null || conn.isClosed())
        {
            return false;
        }
        return true;
    }

    public  ChannelSftp getCurrentConnection() {
        ChannelSftp conn = threadLocal.get();
        if(!isVaild(conn)){
            conn = getConnection();
        }
        return conn;
    }

    public void close(ChannelSftp conn) throws SftpException {
        if(isVaild(conn) && !(freeConnections.size()> configration.getMaxConnections())){
            freeConnections.add(conn);
            activeConnections.remove(conn);
            contActive--;
            threadLocal.remove();
            try {
                notifyAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void destroy() {
        for (ChannelSftp conn : this.freeConnections) {
            try {
                if(isVaild(conn)) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for(ChannelSftp conn : activeConnections){
            try {
                if(isVaild(conn)){
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isActive = false;
        contActive = 0;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void checkPool() {
        if(configration.getCheckPool()){
            new Timer().schedule(new TimerTask() {
                public void run() {
                    logger.debug("空闲连接数"+freeConnections.size());
                    logger.debug("活动连接数"+activeConnections.size());
                    logger.debug("总连接数"+contActive);
                }
            }, configration.getLazyCheck(),configration.getPeriodCheck());
        }
    }
}
