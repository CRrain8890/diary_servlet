package com.wishwzp.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * properties文件读取类
 * @author wzp
 *
 */
public class PropertiesUtil {
	private static final Logger logger = LogManager.getLogger(PropertiesUtil.class);

	public static String getValue(String key){
		Properties propEnvironment=new Properties();
		InputStream inEnvironment=new PropertiesUtil().getClass().getResourceAsStream("/diary.properties");
		try {
			propEnvironment.load(inEnvironment);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String environment = (String)propEnvironment.get("app.environment");
		if (environment == null || environment.isEmpty()) {
			environment = "dev"; // 默认使用开发环境
		}
		logger.debug("当前环境："+environment);
		Properties prop=new Properties();
		InputStream in=new PropertiesUtil().getClass().getResourceAsStream("/diary-"+environment+".properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return (String)prop.get(key);
	}
}
