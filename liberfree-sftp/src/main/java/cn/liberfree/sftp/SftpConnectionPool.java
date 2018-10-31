package cn.liberfree.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

/**
 * @author: zhangchao
 * @time: 2018-10-31 10:24
 **/
public interface SftpConnectionPool {

     ChannelSftp getConnection();  //获得连接

     ChannelSftp getCurrentConnection();   //获得当前连接

     void close(ChannelSftp conn) throws SftpException; //回收连接

     void destroy();  //销毁连接池


     Boolean isActive();  //获取连接池状态

     void checkPool();    //检查连接池状态
}
