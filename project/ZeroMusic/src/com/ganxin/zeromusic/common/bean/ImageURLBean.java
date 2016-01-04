package com.ganxin.zeromusic.common.bean;

import java.util.List;

/**
 * 
 * @Description 查询图片URL接口实体
 * @author ganxin
 * @date Apr 5, 2015
 * @email ganxinvip@163.com
 */
@Deprecated
public class ImageURLBean {
	// 返回的数据
	private List<ImageData> data;

	public List<ImageData> getData() {
		return data;
	}

	public void setData(List<ImageData> data) {
		this.data = data;
	}

	public class ImageData {
		// 目标url
		private String objURL;

		public String getObjURL() {
			return objURL;
		}

		public void setObjURL(String objURL) {
			this.objURL = objURL;
		}
	}
}
