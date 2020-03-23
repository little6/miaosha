package com.geekq.miaosha.utils;

import org.apache.commons.codec.digest.DigestUtils;

//md5加密工具类
public class MD5Util {

	//md5加密
	public static String md5(String src) {
		return DigestUtils.md5Hex(src);
	}

	//加密固定盐
	private static final String salt = "1a2b3c4d";

	//第一次md5加密+固定盐，基于用户输入明文
	public static String inputPassToFormPass(String inputPass) {
		String str = ""+salt.charAt(0)+salt.charAt(2) + inputPass +salt.charAt(5) + salt.charAt(4);
		System.out.println(str);
		return md5(str);
	}

	//第二次md5加密+随机盐，基于第一次加密后的密文，加密后成为保存到数据库的密码
	public static String formPassToDBPass(String formPass, String salt) {
		String str = ""+salt.charAt(0)+salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
		return md5(str);
	}

	//基于用户输入的明文密码，进行两次md5加密后的密文
	public static String inputPassToDbPass(String inputPass, String saltDB) {
		String formPass = inputPassToFormPass(inputPass);
		String dbPass = formPassToDBPass(formPass, saltDB);
		return dbPass;
	}
	
	public static void main(String[] args) {
		System.out.println(inputPassToFormPass("123456"));//d3b1294a61a07da9b49b6e22b2cbd7f9
//		System.out.println(formPassToDBPass(inputPassToFormPass("123456"), "1a2b3c4d"));
//		System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));//b7797cce01b4b131b433b6acf4add449
	}
	
}
