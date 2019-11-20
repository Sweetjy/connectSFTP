package com.cn.ac.iie.crawl.data.utils;

import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SftpUtil {
	
    /** SFTP 登录用户名*/ 
    private final static String USERNAME = "xgs";
    
    /** SFTP 登录密码*/   
	private final static String PASSWORD = "rzx@123";
	
    /** 私钥 */    
    private final static String PRIVATE_KEY = null; 
    
    /** SFTP 服务器地址IP地址*/ 
	private final static String HOST = "159.138.48.92";
	
    /** SFTP 端口*/  
	private final static Integer PORT = 22;

	public static ChannelSftp builConnection() {
		ChannelSftp sftp = null;
        try {  
            JSch jsch = new JSch();  
            if (PRIVATE_KEY != null) {  
                jsch.addIdentity(PRIVATE_KEY);// 设置私钥  
            }  
    
            Session session = jsch.getSession(USERNAME, HOST, PORT);  
           
            if (PASSWORD != null) {  
                session.setPassword(PASSWORD);    
            }  
            Properties config = new Properties();  
            config.put("StrictHostKeyChecking", "no");  
            
            session.setConfig(config);  
            session.connect();  
              
            Channel channel = session.openChannel("sftp");  
            channel.connect();  
    
            sftp = (ChannelSftp) channel;  
        } catch (JSchException e) {  
            e.printStackTrace();
        }
		return sftp;  
	}	
}
