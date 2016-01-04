package com.ganxin.zeromusic.common.bean;
/**
 * 
 * @Description 服务端返回基类
 * @author ganxin
 * @date 2014-12-15
 * @email ganxinvip@163.com
 */
public class BaseResponseBean {
	private int error_code; //错误码

	public int getError_code() {
		return error_code;
	}

	public void setError_code(int error_code) {
		this.error_code = error_code;
	}
}
