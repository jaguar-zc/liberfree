package cn.liberfree.sftp;

import com.jcraft.jsch.*;

import java.util.*;

/**
 * @author: zhangchao
 * @time: 2018-10-31 10:26
 **/
public class DefaultSftpConnectionPool implements SftpConnectionPool {
    private SftpConfigration configration;
    private Boolean isActive = false;
    private Integer contActive = 0; //创建记录总数

    private List<ChannelSftp> freeConnections = new Vector<ChannelSftp>();    //空闲链接
    private List<ChannelSftp> activeConnections = new Vector<ChannelSftp>();  //活动连接
    private static ThreadLocal<ChannelSftp> threadLocal = new ThreadLocal<ChannelSftp>();

    public DefaultSftpConnectionPool(SftpConfigration configration) {
        this.configration = configration;
        init();
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
        // TODO Auto-generated method stub
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
            // TODO: handle exception
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
        // TODO Auto-generated method stub
        if(isVaild(conn) && !(freeConnections.size()> configration.getMaxConnections())){
            freeConnections.add(conn);
            activeConnections.remove(conn);
            contActive--;
            threadLocal.remove();
            notifyAll();
        }
    }

    public void destroy() {
        // TODO Auto-generated method stub
        for (ChannelSftp conn : this.freeConnections) {
            try {
                if(isVaild(conn)) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
        }

        for(ChannelSftp conn : activeConnections){
            try {
                if(isVaild(conn)){
                    conn.disconnect();
                }
            } catch (Exception e) {
                // TODO: handle exception
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
// TODO Auto-generated method stub
        if(configration.getCheckPool()){
            new Timer().schedule(new TimerTask() {
                public void run() {
                    System.out.println("空闲连接数"+freeConnections.size());
                    System.out.println("活动连接数"+activeConnections.size());
                    System.out.println("总连接数"+contActive);
                }
            }, configration.getLazyCheck(),configration.getPeriodCheck());
        }
    }
}
