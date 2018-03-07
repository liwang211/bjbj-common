package com.bjbj.common.service;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HttpClientService {

	@Autowired(required = false)
	private CloseableHttpClient httpClient;

	@Autowired(required = false) // 请求参数
	private RequestConfig requestConfig;

	/**
	 * 编辑工具类的思路 1. 编辑get()工具类 1.1 get请求如何添加参数??? 1.2 get请求如何解决获取参数后的乱码问题 设定字符集 1.3
	 * 应该重构多个get方法满足不同的需求 2.编辑post()工具类 2.1 post请求如何传递参数 表单提交时采用post请求 2.2 post乱码
	 * 相对而言比较好解决 2.3 满足不同的post需求
	 * 
	 * @throws URISyntaxException
	 * 
	 */

	public String doGet(String uri, Map<String, String> params, String encode) throws URISyntaxException {

		// 判断是否含有参数
		if (params != null) {
			// 定义拼接参数的工具类
			URIBuilder builder = new URIBuilder(uri);

			// 循环遍历map 获取key 和 value
			for (Map.Entry<String, String> entry : params.entrySet()) {

				builder.setParameter(entry.getKey(), entry.getValue());

			}
			uri = builder.build().toString();
		}

		// 2.定义字符集编码
		if (null == encode) {
			encode = "UTF-8";
		}

		// 3.定义get请求
		HttpGet httpGet = new HttpGet(uri);
		httpGet.setConfig(requestConfig);
		// 3.准备发出请求
		CloseableHttpResponse response = null;
		try {
		
			response = httpClient.execute(httpGet);
			

			if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity(), encode);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String doGet(String uri, Map<String, String> params) throws URISyntaxException {
		return doGet(uri, params, null);
	}

	public String doGet(String uri) throws URISyntaxException {
		return doGet(uri, null, null);
	}

	// 定义POST提交 post提交不能拼接字符串
	public String doPost(String uri, Map<String, String> params, String encode) throws UnsupportedEncodingException {

		// 1.定义POST提交方式
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setConfig(requestConfig);
		
		// 设定字符集
		if (null == encode) {
			encode = "UTF-8";
		}
		// 2.如果有提交参数则进行处理
		if (params != null) {
			// 3.定义数据封装的集合
			List<NameValuePair> nameValuePairsList = new ArrayList<>();

			// 4.为集合赋值
			for (Map.Entry<String, String> entry : params.entrySet()) {
				nameValuePairsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}

			// 5.定义form表单的对象
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairsList,encode);
			

			// 6.将form表单对象添加到POST对象中
			httpPost.setEntity(formEntity);
			
			
		}
		

		CloseableHttpResponse httpResponse = null;
		
		try {

			httpResponse = httpClient.execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(httpResponse.getEntity(), encode);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String doPost(String uri, Map<String, String> params) throws UnsupportedEncodingException {
		return doPost(uri, params, null);
	}

	public String doPost(String uri) throws UnsupportedEncodingException {
		return doPost(uri,null,null);
	}
}
