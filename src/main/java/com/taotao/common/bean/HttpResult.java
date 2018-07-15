package com.taotao.common.bean;

/**
 * @author hgl
 * @data 2018年7月3日
 * @description HttpResult是为了做POST请求的时候，对返回的数据进行封装
 * 有可能返回的只有状态码，有可能返回的既有状态码又有内容，所以用这个封装
 */
public class HttpResult {
	//响应状态码
     private Integer code;
     //响应数据
     private String data;
     
     public HttpResult(){
    	 
     }
     
     public HttpResult(Integer code,String data){
    	     this.code = code;
    	     this.data = data;
     }

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
