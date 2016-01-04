package com.ganxin.zeromusic.common.util;

import android.text.TextUtils;

/**
 * 
 * @Description 字符串操作类，如截取、合并等操作
 * @author ganxin
 * @date Mar 26, 2015
 * @email ganxinvip@163.com
 */
public class StringHelper {

	/**
	 * 截取图片名称
	 * 
	 * @param filePath
	 * @return
	 */
	public static String spiltImageName(String imageurl) {
		if (TextUtils.isEmpty(imageurl)) {
			return null;
		}
		imageurl = imageurl.toLowerCase();
		int i = imageurl.lastIndexOf("/");
		String substring = imageurl.substring(i + 1, imageurl.length());
		return substring;
	}

	/**
	 * 根据路径获取文件名
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileName(final String path) {
		String fileName = path.substring(path.lastIndexOf("/") + 1,
				path.lastIndexOf("."));
		return fileName;
	}

	/**
	 * 解析文件所在的文件夹
	 * 
	 * @param filePath
	 *            文件路径
	 * @return 文件所在的文件夹路径
	 */
	public static String getFileFolderPath(final String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return null;
		}
		int last = filePath.lastIndexOf("/");
		if (last == -1) {
			return null;
		}
		return filePath.substring(0, last + 1);
	}

}
