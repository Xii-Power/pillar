package com.xii.pillar.utils;

import java.security.MessageDigest;

/**
 * MD加密组件
 */
public abstract class MDCoder {

	/**
	 * MD2加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static byte[] encodeMD2(byte[] data) throws Exception {
		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("MD2");
		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * MD4加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static byte[] encodeMD4(byte[] data) throws Exception {
		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("MD4");
		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * MD5加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static byte[] encodeMD5(byte[] data) throws Exception {
		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("MD5");
		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * 功能描述:MD5加密，生成32位的字符串
	 * 
	 * @param sourceStr
	 * @return
	 * @throws Exception
	 */
	public static String encodeMD5(String sourceStr) throws Exception {
		String result = "";
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		md.update(sourceStr.getBytes("UTF-8"));
		byte b[] = md.digest();
		int i;
		StringBuilder buf = new StringBuilder("");
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0) {
				i += 256;
			}
			if (i < 16) {
				buf.append("0");
			}
			buf.append(Integer.toHexString(i));
		}
		result = buf.toString();
		return result;
	}

	/**
	 * Tiger加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static byte[] encodeTiger(byte[] data) throws Exception {
		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("Tiger");
		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * TigerHex加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static String encodeTigerHex(byte[] data) throws Exception {
		// 执行消息摘要
		byte[] b = encodeTiger(data);
		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}

	/**
	 * Whirlpool加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static byte[] encodeWhirlpool(byte[] data) throws Exception {
		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("Whirlpool");
		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * WhirlpoolHex加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static String encodeWhirlpoolHex(byte[] data) throws Exception {
		// 执行消息摘要
		byte[] b = encodeWhirlpool(data);
		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}

	/**
	 * GOST3411加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static byte[] encodeGOST3411(byte[] data) throws Exception {
		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("GOST3411");
		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * GOST3411Hex加密
	 * 
	 * @param data
	 *            待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static String encodeGOST3411Hex(byte[] data) throws Exception {
		// 执行消息摘要
		byte[] b = encodeGOST3411(data);
		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}
}
