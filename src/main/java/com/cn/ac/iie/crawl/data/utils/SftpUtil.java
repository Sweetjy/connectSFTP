package com.cn.ac.iie.crawl.data.utils;

import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SftpUtil {
	
    /** SFTP ��¼�û���*/ 
    private final static String USERNAME = "xgs";
    
    /** SFTP ��¼����*/   
	private final static String PASSWORD = "rzx@123";
	
    /** ˽Կ */    
    private final static String PRIVATE_KEY = null; 
    
    /** SFTP ��������ַIP��ַ*/ 
	private final static String HOST = "159.138.48.92";
	
    /** SFTP �˿�*/  
	private final static Integer PORT = 22;

	public static ChannelSftp builConnection() {
		ChannelSftp sftp = null;
        try {  
            JSch jsch = new JSch();  
            if (PRIVATE_KEY != null) {  
                jsch.addIdentity(PRIVATE_KEY);// ����˽Կ  
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
