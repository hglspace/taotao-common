package com.taotao.common.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.bean.HttpResult;


/**
 * @author hgl
 * @data 2018年7月3日
 * @description 实现BeanFactoryAware接口，需要实现setBeanFactory()方法
 * 通过BeanFactory来获取httpClient对象，所以无需注入
 * 
 * ApiService是单例的，如何在单例中使用多例？就是实现BeanFactoryAware接口，获取beanFactory,
 * 每次从beanFactory中获取实例
 */
@Service
public class ApiService implements BeanFactoryAware{

	@Autowired(required = false)
	private RequestConfig requestConfig;
	
	private BeanFactory beanFactory;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

		this.beanFactory = beanFactory;
	}

	/**
	 * CloseableHttpClient
	 * @return
	 * description:通过该方法来从benaFactory工厂来获取bena，而CloseableHttpClient在容器中是多例的，
	 * 所以每次获取的都是一个新的实例
	 */
	private CloseableHttpClient getHttpClient(){
		
		return this.beanFactory.getBean(CloseableHttpClient.class);
	}
	
	/**
	 * POST和GET请求的区别
	 * 1 get方式是把数据放到地址栏中明文的形式发送，
	 *   post方式是把数据放在请求体中的形式发送
	 *   
	 * 2 post表示发送数据到服务器，但是因为服务器可以响应回来数据的接收情况，
	 * 所以post也能获得服务器的数据。
	 *   get表示从服务器端获得数据，但是因为在从服务器获得数据的时候可以发送相应的
	 *   关键字作为获得条件，所以get也能发送数据到服务器
	 */
	
	
	/**
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @category 不带参数的GET请求
	 */
	public String doGet(String url) throws ClientProtocolException, IOException{
		// 创建http GET请求
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		CloseableHttpResponse response = null;
		try {
			// 执行请求
			response = getHttpClient().execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} finally {
			if (response != null) {
				response.close();
			}
		}
        return null;
	}
	
	
	/**
	 * String
	 * @param url
	 * @param params
	 * @return
	 * @throws URISyntaxException
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @description 带参数的GET请求
	 */
	public String doGet(String url,Map<String,String> params) throws URISyntaxException, ClientProtocolException, IOException{
		URIBuilder builder = new URIBuilder(url);
		if (params != null) {
			for (String key : params.keySet()) {
				builder.setParameter(key, params.get(key));
			}
		}
		return this.doGet(builder.build().toString());
	}
	
	
	/**
	 * HttpResult
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @description:带参数的POST请求
	 */
	public HttpResult doPost(String url,Map<String,String> params) throws ClientProtocolException, IOException{
		// 创建http POST请求
		HttpPost httpPost = new HttpPost(url);
        if(null!=params){
        	List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
			for (String key : params.keySet()) {
				parameters.add(new BasicNameValuePair(key, params.get(key))); // 每一个输入框，name
			}
			// 构造一个form表单式的实体
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					parameters);
			// 将请求实体设置到httpPost对象中
			httpPost.setEntity(formEntity);
        }
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
		try {
			// 执行请求
			response = getHttpClient().execute(httpPost);
			if(response.getStatusLine().getStatusCode()==200){
				return new HttpResult(response.getStatusLine().getStatusCode(),
						EntityUtils.toString(response.getEntity(), "UTF-8"));
			}
			return new HttpResult(response.getStatusLine().getStatusCode(),
					null);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	
	/**
	 * HttpResult
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @description:不带参数的post请求
	 */
	public HttpResult doPost(String url) throws ClientProtocolException, IOException{
		return doPost(url,null);
	}
}
