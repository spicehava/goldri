package club.goldri.core.util;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

/**
 * 封装各种生成唯一性ID算法的工具类.
 */
@Service
@Lazy(false)
public class IdGenUtil {

	private static SecureRandom random = new SecureRandom();
	
	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String uuidRandom() {
		return UUID.randomUUID().toString();
	}
	
	/**
	 * 使用SecureRandom随机生成Long. 
	 */
	public static long randomLong() {
		return Math.abs(random.nextLong());
	}

	public static void main(String[] args) {
		System.out.println(IdGenUtil.uuid());
		System.out.println(IdGenUtil.uuid().length());
		System.out.println(IdGenUtil.randomLong());
		System.out.println((IdGenUtil.randomLong() + "").length());
	}

	/**
	 * 生成16位编号 GZ+时间戳+4位随机数
	 * @param ZG 字轨
	 * @return
	 */
	public static String getGZ(String ZG) throws ParseException {
		StringBuffer str=new StringBuffer();
		SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date=String.valueOf((System.currentTimeMillis() / 1000));
		String random=String.valueOf((Math.random()*9+1)*1000);
		String random1=random.substring(0,random.indexOf("."));
		str.append(ZG);
		str.append(date);
		str.append(random1);
		return str.toString();
	}
}
