package cn.liberfree.sftp.utils;

import cn.liberfree.sftp.DefaultSftpConnectionPool;
import cn.liberfree.sftp.SftpConfigration;
import cn.liberfree.sftp.SftpConnectionPool;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class SFTPUtil {

	public static void upload(ChannelSftp sftp,String path, String sftpFileName, InputStream input) throws SftpException {
		try {
			sftp.cd(path);
		} catch (SftpException e) {
			// dir no exist, create folder
			String[] dirs = path.split("/");
			String tempPath = "";
			for (String dir : dirs) {
				if (null == dir || "".equals(dir))
					continue;
				tempPath += "/" + dir;
				try {
					sftp.cd(tempPath);
				} catch (SftpException ex) {
					sftp.mkdir(tempPath);
					sftp.cd(tempPath);
				}
			}
		}
		sftp.put(input, sftpFileName);
		try {
			sftp.cd("/");
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static byte[] download(ChannelSftp sftp,String downloadFile) throws SftpException, IOException {
		try (InputStream is = sftp.get(downloadFile)) {
			return IOUtils.toByteArray(is);
		} catch (IOException ex) {
			throw ex;
		}
	}

	public static void delete(ChannelSftp sftp,String directory, String deleteFile) throws SftpException {
		sftp.cd(directory);
		sftp.rm(deleteFile);
	}

	public static boolean exist(ChannelSftp sftp,String path){
		InputStream inputStream = null;
		try {
			System.out.println(sftp.pwd());
			sftp.cd("/");
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
		int i = path.lastIndexOf("/");
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
		ChannelSftp currentConnection = connectionPool.getCurrentConnection();
		System.out.println(SFTPUtil.exist(currentConnection, "/dz/images/8bbd933d-c724-4429-897e-547837e9040a.jpg"));

////		SFTPUtil u = new SFTPUtil("192.168.4.201", 22, "test", "test");
//		SFTPUtil u = new SFTPUtil("47.75.127.229", 22, "test", "123456");
//		u.login();
//		try {
//			InputStream inputStream = u.sftp.get("/dz/images/8bbd933d-c724-4429-897e-547837e9040a.jpg");
//			byte[] bytes = new byte[1];
//			int read = inputStream.read(bytes, 0, bytes.length);
//			System.out.println(read);
//			System.out.println("有文件");
//		}catch (Exception e){
//			System.out.println("无文件");
//		}

//		u.upload("/dz/file","git-常用命令.txt",new FileInputStream(new File("D:/git-常用命令.txt")));


//		u.login();
//		byte[] download = u.download("/dz/images/a8a80337-6133-47c3-991c-9ec779163ec9.jpg");
//		byte[] download = u.download("/dz/images", "23JEQjv-ZKvMXBTe");

//		u.upload("/upload","/images",RandomStringUtils.randomAlphanumeric(10),new FileInputStream(new File("C:\\Users\\Administrator\\Desktop\\1.jpg")));

//		for (int i = 0; i < 10; i++) {
//			u.upload("/Users/liuguanghua/temp", "/images", RandomStringUtils.randomAlphanumeric(10),
//					new FileInputStream(new File("/Users/liuguanghua/Pictures/WechatIMG4.jpeg")));
//		}

//		u.download("/Users/liuguanghua/temp/images", "yS75jq5-vM1gDNDe");
//		u.download("/Users/liuguanghua/temp/images", "nwmh3XD-x8ERofhC");
//		u.download("/Users/liuguanghua/temp/images", "FZ41M80-6CEXS11q");
//		u.download("/Users/liuguanghua/temp/images", "0xo9Bi0-PffXLNFM");
//		u.logout();
	}
}
