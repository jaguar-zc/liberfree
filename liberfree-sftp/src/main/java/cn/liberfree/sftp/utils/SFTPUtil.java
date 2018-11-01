package cn.liberfree.sftp.utils;
import cn.liberfree.sftp.DefaultSftpConnectionPool;
import cn.liberfree.sftp.SftpConfigration;
import cn.liberfree.sftp.SftpConnectionPool;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class SFTPUtil {
	public static Logger logger  = LoggerFactory.getLogger(SFTPUtil.class);
	private static String ROOT = "/";


	public static void upload(SftpConnectionPool connectionPool,String path, String sftpFileName, InputStream input) throws SftpException {
		ChannelSftp sftp = connectionPool.getCurrentConnection();
		try {
			sftp.cd(path);
		} catch (SftpException e) {
			// dir no exist, create folder
			String[] dirs = path.split(ROOT);
			String tempPath = "";
			for (String dir : dirs) {
				if (null == dir || "".equals(dir))
					continue;
				tempPath += ROOT + dir;
				try {
					sftp.cd(tempPath);
				} catch (SftpException ex) {
					sftp.mkdir(tempPath);
					sftp.cd(tempPath);
				}
			}
		}
		try {
			sftp.put(input, sftpFileName);
			sftp.cd(ROOT);
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			connectionPool.close(sftp);
		}
	}

	public static byte[] download(SftpConnectionPool connectionPool,String downloadFile) throws SftpException, IOException {
		ChannelSftp sftp = connectionPool.getCurrentConnection();
		InputStream is = null;
		try{
			is = sftp.get(downloadFile);
			return IOUtils.toByteArray(is);
		} catch (IOException ex) {
			throw ex;
		}finally {
			if(is != null){
				is.close();
			}
			connectionPool.close(sftp);
		}
	}

	public static void delete(SftpConnectionPool connectionPool,String directory, String deleteFile) throws SftpException {
		ChannelSftp sftp = connectionPool.getCurrentConnection();
		try {
			sftp.cd(directory);
			sftp.rm(deleteFile);
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			connectionPool.close(sftp);
		}
	}

	public static boolean exist(SftpConnectionPool connectionPool,String path){
		ChannelSftp sftp = connectionPool.getCurrentConnection();
		InputStream inputStream = null;
		try {
			logger.debug(sftp.pwd());
			sftp.cd(ROOT);
			inputStream = sftp.get(path);
			byte[] bytes = new byte[1];
			inputStream.read(bytes, 0, bytes.length);
			return true;
		}catch (Exception e){
			return false;
		}finally {
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				connectionPool.close(sftp);
			} catch (SftpException e) {
				e.printStackTrace();
			}
		}
	}


	public static String getSuffix(String name){
		int i = name.lastIndexOf(".");
		if(i != -1){
			return name.substring(i + 1, name.length());
		}
		return null;
	}

	public static String getFileNameByUrl(String path){
		int i = path.lastIndexOf(ROOT);
		if(i == -1){
			return path;
		}
		return path.substring(i+1,path.length());
	}

	public Vector<?> listFiles(ChannelSftp sftp,String directory) throws SftpException {
		return sftp.ls(directory);
	}
//
	public static void main(String[] args) throws Exception {

		SftpConfigration sftpConfigration = new SftpConfigration("test", "123456","47.75.127.229", 22,null);
		SftpConnectionPool connectionPool = new DefaultSftpConnectionPool(sftpConfigration);
		System.out.println(SFTPUtil.exist(connectionPool, "/dz/images/8bbd933d-c724-4429-897e-547837e9040a.jpg"));
	}
}
