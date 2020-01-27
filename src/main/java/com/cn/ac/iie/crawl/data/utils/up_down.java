package com.cn.ac.iie.crawl.data.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.redis.RedisUtil;

import redis.clients.jedis.Jedis;

public class up_down extends TimerTask{ 
	private static Jedis jedis;
	
	private static 	ChannelSftp sftp;
	
	//判断目录是否存在
	public static boolean isDirExist(String directory) {
		boolean isDirExistFlag = false;
		try {
			SftpATTRS sftpATTRS = sftp.lstat(directory);
			isDirExistFlag = true;
			return sftpATTRS.isDir();
		} catch (Exception e) {
			if (e.getMessage().toLowerCase().equals("no such file")) {
			isDirExistFlag = false;
			}
		}
		return isDirExistFlag;
	}
	//创建目录
	public static boolean createDir(String createpath) {
		try {
			if (isDirExist(createpath)) {
			sftp.cd(createpath);
			return true;
		}
		String pathArry[] = createpath.split("/");
		StringBuffer filePath = new StringBuffer("/");
		for (String path : pathArry) {
			if (path.equals("")) {
			continue;
		}
		filePath.append(path + "/");
		if (isDirExist(filePath.toString())) {
			sftp.cd(filePath.toString());
		} else {
			sftp.mkdir(filePath.toString());
			sftp.cd(filePath.toString());
			}
		}
		sftp.cd(createpath);
		return true;
		} catch (SftpException e) {
			e.printStackTrace();
		}
			return false;
	}
	
	public static void disconnect() throws JSchException {
		if (sftp != null) {
		if (sftp.isConnected()) {
			sftp.disconnect();
			}
		}
		if (sftp.getSession()!= null) {
		if (sftp.getSession().isConnected()) {
			sftp.getSession().disconnect();
			}
		}
	}
	//上传单个文件
	public static void uploadFile(String remotePath,String localPath, String FileName) {
		FileInputStream in = null;
			try {
				createDir(remotePath);
				File file = new File(localPath + "/"+FileName);
				in = new FileInputStream(file);
				sftp.put(in, FileName+".tmp");//上传中加后缀
				sftp.rename(FileName+".tmp",FileName);//上传结束后重命名
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SftpException e) {
				e.printStackTrace();
			} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	//批量上传
	public static void bacthUploadFile(String remotePath, String localPath){
		sftp=SftpUtil.builConnection();
		jedis=RedisUtil.getJedis();//去重
		try{
			File file=new File(localPath);
			File[] files=file.listFiles();
			for(int i=0;i<files.length;i++){
				String filename=files[i].getName();
				if(files[i].isFile() && filename.contains(".json")){
					if(!jedis.sismember("upload",filename)){
						jedis.sadd("upload",filename);
						uploadFile(remotePath,localPath, filename);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			try {
				disconnect();
			} catch (JSchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		RedisUtil.returnResource(jedis);
	}
	
	//@Override
	public void run() {
		String localPath = "/root/jyreptile/trump/data";
//		String localPath = "D:\\eclipse\\project\\trump\\data";
		String remotePath = "/data/news/newslookup/";
//		String remotePath = "/data/news/baidu/";
		bacthUploadFile(remotePath,localPath);
	}	
	
	public static void main(String[] args) {
		Timer timer=new Timer();
		timer.schedule(new up_down(), 1*1000,1*3600*1000);  //定时任务
	}	
}
