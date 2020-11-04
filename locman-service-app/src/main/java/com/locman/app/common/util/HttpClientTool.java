package com.locman.app.common.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

@SuppressWarnings({ "resource", "deprecation" })
public class HttpClientTool {
	/**
	 * JSON参数以Post方法访问
	 * 
	 * @param url
	 *            请求地址
	 * @param argsMap
	 *            携带的参数
	 * @param content
	 *            内容
	 * @return String 返回结果
	 * @throws Exception
	 */
	public static String POSTMethodByJSON(String url, String jsonObject, String content,String token) {
		String body = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			if (httpPost != null) {
				try {
					httpPost.addHeader("Content-type", "application/json; charset=utf-8");
					httpPost.setHeader("Accept", "application/json");
					httpPost.setHeader("token", token);
					httpPost.setEntity(new StringEntity(jsonObject, Charset.forName("UTF-8")));
					HttpResponse response = httpClient.execute(httpPost);
					int stateCode = response.getStatusLine().getStatusCode();
					if (stateCode == HttpStatus.SC_OK) {
						HttpEntity httpEntity = response.getEntity();
						body = EntityUtils.toString(httpEntity);
					}else if (stateCode == HttpStatus.SC_MOVED_PERMANENTLY){
						HttpEntity httpEntity = response.getEntity();
						body = EntityUtils.toString(httpEntity);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;
	}



	/**
	 * 以Get方法访问
	 * 
	 * @param url
	 *            请求地址
	 * @param argsMap
	 *            请求携带参数
	 * @return String
	 * @throws Exception
	 */
	public static String GETMethodByJson(String url, JSONObject jsonObject, String token) {
		String result = null;
		byte[] dataByte = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			// 为GET请求链接构造参数
			url = formatGetParameterByJson(url, jsonObject);
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("token", token);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				byte[] responseBytes = getData(httpEntity);
				dataByte = responseBytes;
				httpGet.abort();
			}
			// 将字节数组转换成为字符串
			result = bytesToString(dataByte);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}



	/**
	 * 以Post方法访问
	 * 
	 * @param url
	 *            请求地址
	 * @param argsMap
	 *            携带的参数
	 * @param content
	 *            内容
	 * @return String 返回结果
	 * @throws Exception
	 */
	public static String POSTMethod(String url, Map<String, Object> argsMap, String content) throws Exception {
		byte[] dataByte = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		if (MapUtils.isNotEmpty(argsMap)) {
			// 设置参数
			UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(setHttpParams(argsMap), "UTF-8");
			httpPost.setEntity(encodedFormEntity);
		}
		if (StringUtils.isNotEmpty(content)) {
			httpPost.setEntity(new ByteArrayEntity(content.getBytes()));
		}
		// 执行请求
		HttpResponse httpResponse = httpClient.execute(httpPost);
		// 获取返回的数据
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			byte[] responseBytes = getData(httpEntity);
			dataByte = responseBytes;
			httpPost.abort();
		}
		// 将字节数组转换成为字符串
		String result = bytesToString(dataByte);
		return result;
	}



	/**
	 * 以Get方法访问
	 * 
	 * @param url
	 *            请求地址
	 * @param argsMap
	 *            请求携带参数
	 * @return String
	 * @throws Exception
	 */
	public static String GETMethod(String url, Map<String, Object> argsMap) throws Exception {
		byte[] dataByte = null;
		HttpClient httpClient = new DefaultHttpClient();
		// 为GET请求链接构造参数
		url = formatGetParameter(url, argsMap);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			byte[] responseBytes = getData(httpEntity);
			dataByte = responseBytes;
			httpGet.abort();
		}
		// 将字节数组转换成为字符串
		String result = bytesToString(dataByte);
		return result;
	}



	/**
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static InputStream GETMethodDownloadFile(String url) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		// 为GET请求链接构造参数
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = httpClient.execute(httpGet);
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			InputStream inputStream = httpEntity.getContent();
			return inputStream;
		}
		// 将字节数组转换成为字符串
		return null;
	}



	/**
	 * PUT方法
	 * 
	 * @param url
	 *            请求地址
	 * @param argsMap
	 *            携带地址
	 * @param cookies
	 *            cookies
	 * @param content
	 *            内容
	 * @return
	 * @throws Exception
	 */
	public static String PUTMethod(String url, Map<String, Object> argsMap, String cookies, String content)
			throws Exception {
		byte[] dataByte = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPut httpPut = new HttpPut(url);
		// 设置内容
		if (StringUtils.isNotEmpty(content)) {
			httpPut.setEntity(new ByteArrayEntity(content.getBytes()));
		}
		// 设置Cookies
		if (StringUtils.isNotEmpty(cookies)) {
			httpPut.setHeader("Cookie", cookies);
			httpPut.setHeader("Accept", "application/json");
			httpPut.setHeader("Content-Type", "application/json");
		}
		// 设置参数
		if (MapUtils.isNotEmpty(argsMap)) {
			UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(setHttpParams(argsMap), "UTF-8");
			httpPut.setEntity(encodedFormEntity);
		}
		// 执行请求
		HttpResponse httpResponse = httpClient.execute(httpPut);
		// 获取返回的数据
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			byte[] responseBytes = getData(httpEntity);
			dataByte = responseBytes;
			httpPut.abort();
		}
		// 将字节数组转换成为字符串
		String result = bytesToString(dataByte);
		return result;
	}


	/**
	 * JSON参数以PUT方法访问
	 * 
	 * @param url
	 *            请求地址
	 * @param argsMap
	 *            携带的参数
	 * @param content
	 *            内容
	 * @return String 返回结果
	 * @throws Exception
	 */
	public static String PUTMethodByJSON(String url, String jsonObject, String content,String token) {
		String body = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPut httpPut = new HttpPut(url);
			if (httpPut != null) {
				try {
					httpPut.addHeader("Content-type", "application/json; charset=utf-8");
					httpPut.setHeader("Accept", "application/json");
					httpPut.setHeader("token", token);
					httpPut.setEntity(new StringEntity(jsonObject, Charset.forName("UTF-8")));
					HttpResponse response = httpClient.execute(httpPut);
					int stateCode = response.getStatusLine().getStatusCode();
					if (stateCode == HttpStatus.SC_OK) {
						HttpEntity httpEntity = response.getEntity();
						body = EntityUtils.toString(httpEntity);
					}else if (stateCode == HttpStatus.SC_MOVED_PERMANENTLY){
						HttpEntity httpEntity = response.getEntity();
						body = EntityUtils.toString(httpEntity);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;
	}

	
	
	/**
	 * PUT请求方法
	 * 
	 * @param url
	 *            请求地址
	 * @param argsMap
	 *            携带参数
	 * @param headerParam
	 *            header参数
	 * @param content
	 *            内容
	 * @return
	 * @throws Exception
	 */
	public static String PUTMethod(String url, Map<String, Object> argsMap, Map<String, String> headerParam,
			String content) throws Exception {
		byte[] dataByte = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPut httpPut = new HttpPut(url);
		// 设置内容
		if (StringUtils.isNotEmpty(content)) {
			httpPut.setEntity(new ByteArrayEntity(content.getBytes()));
		}
		// 设置Cookies
		if (MapUtils.isNotEmpty(headerParam)) {
			Set<Entry<String, String>> entrySet = headerParam.entrySet();
			Iterator<Entry<String, String>> entryIter = entrySet.iterator();
			while (entryIter.hasNext()) {
				Entry<String, String> entry = entryIter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				httpPut.setHeader(key, value);
			}
		}
		// 设置参数
		if (MapUtils.isNotEmpty(argsMap)) {
			UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(setHttpParams(argsMap), "UTF-8");
			httpPut.setEntity(encodedFormEntity);
		}
		// 执行请求
		HttpResponse httpResponse = httpClient.execute(httpPut);
		// 获取返回的数据
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			byte[] responseBytes = getData(httpEntity);
			dataByte = responseBytes;
			httpPut.abort();
		}
		// 将字节数组转换成为字符串
		String result = bytesToString(dataByte);
		return result;
	}


	
	
	

	public static String DELETEMethod(String url, Map<String, Object> argsMap, Map<String, String> headerParam)
			throws Exception {
		byte[] dataByte = null;
		url = formatGetParameter(url, argsMap);
		HttpClient httpClient = new DefaultHttpClient();
		HttpDelete httpDelete = new HttpDelete(url);
		// 设置Cookies
		if (MapUtils.isNotEmpty(headerParam)) {
			Set<Entry<String, String>> entrySet = headerParam.entrySet();
			Iterator<Entry<String, String>> entryIter = entrySet.iterator();
			while (entryIter.hasNext()) {
				Entry<String, String> entry = entryIter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				httpDelete.setHeader(key, value);
			}
		}
		// 执行请求
		HttpResponse httpResponse = httpClient.execute(httpDelete);
		// 获取返回的数据
		HttpEntity httpEntity = httpResponse.getEntity();
		if (httpEntity != null) {
			byte[] responseBytes = getData(httpEntity);
			dataByte = responseBytes;
			httpDelete.abort();
		}
		// 将字节数组转换成为字符串
		String result = bytesToString(dataByte);
		return result;
	}



	/**
	 * 构造GET请求地址的参数拼接
	 * 
	 * @param url
	 *            地址
	 * @param argsMap
	 *            参数
	 * @return String
	 */
	public static String formatGetParameterByJson(String url, JSONObject jsonObject) throws Exception {
		if (url != null && url.length() > 0 && MapUtils.isNotEmpty(jsonObject)) {
			if (!url.endsWith("?")) {
				url = url + "?";
			}
			if (jsonObject != null && !jsonObject.isEmpty()) {
				Set<Entry<String, Object>> entrySet = jsonObject.entrySet();
				Iterator<Entry<String, Object>> iterator = entrySet.iterator();
				while (iterator.hasNext()) {
					Entry<String, Object> entry = iterator.next();
					if (entry != null) {
						String key = entry.getKey();
						Object value = entry.getValue();
						// Object value =
						// URLEncoder.encode(entry.getValue().toString(),
						// "UTF-8");
						url = url + key + "=" + value;
						if (iterator.hasNext()) {
							url = url + "&";
						}
					}
				}
			}
		}
		return url;
	}



	/**
	 * 构造GET请求地址的参数拼接
	 * 
	 * @param url
	 *            地址
	 * @param argsMap
	 *            参数
	 * @return String
	 */
	public static String formatGetParameter(String url, Map<String, Object> argsMap) throws Exception {
		if (url != null && url.length() > 0 && MapUtils.isNotEmpty(argsMap)) {
			if (!url.endsWith("?")) {
				url = url + "?";
			}
			if (argsMap != null && !argsMap.isEmpty()) {
				Set<Entry<String, Object>> entrySet = argsMap.entrySet();
				Iterator<Entry<String, Object>> iterator = entrySet.iterator();
				while (iterator.hasNext()) {
					Entry<String, Object> entry = iterator.next();
					if (entry != null) {
						String key = entry.getKey();
						Object value = entry.getValue();
						// Object value =
						// URLEncoder.encode(entry.getValue().toString(),
						// "UTF-8");
						url = url + key + "=" + value;
						if (iterator.hasNext()) {
							url = url + "&";
						}
					}
				}
			}
		}
		return url;
	}



	/**
	 * 获取Entity中数据
	 * 
	 * @param httpEntity
	 * @return
	 * @throws Exception
	 */
	public static byte[] getData(HttpEntity httpEntity) throws Exception {
		BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(httpEntity);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bufferedHttpEntity.writeTo(byteArrayOutputStream);
		byte[] responseBytes = byteArrayOutputStream.toByteArray();
		return responseBytes;
	}



	/**
	 * 设置HttpPost请求参数 解析json参数
	 * 
	 * @param argsMap
	 * @return BasicHttpParams
	 */
	@SuppressWarnings("unused")
	private static List<BasicNameValuePair> setHttpParamsJSON(JSONObject jsonObject) {
		List<BasicNameValuePair> nameValuePairList = Lists.newArrayList();
		// 设置请求参数
		if (jsonObject != null && !jsonObject.isEmpty()) {
			Set<Entry<String, Object>> set = jsonObject.entrySet();
			Iterator<Entry<String, Object>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(),
						entry.getValue().toString());
				nameValuePairList.add(basicNameValuePair);
			}
		}
		return nameValuePairList;
	}



	/**
	 * 设置HttpPost请求参数
	 * 
	 * @param argsMap
	 * @return BasicHttpParams
	 */
	private static List<BasicNameValuePair> setHttpParams(Map<String, Object> argsMap) {
		List<BasicNameValuePair> nameValuePairList = Lists.newArrayList();
		// 设置请求参数
		if (argsMap != null && !argsMap.isEmpty()) {
			Set<Entry<String, Object>> set = argsMap.entrySet();
			Iterator<Entry<String, Object>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(),
						entry.getValue().toString());
				nameValuePairList.add(basicNameValuePair);
			}
		}
		return nameValuePairList;
	}



	/**
	 * 将字节数组转换成字符串
	 * 
	 * @param bytes
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String bytesToString(byte[] bytes) throws UnsupportedEncodingException {
		if (bytes != null) {
			String returnStr = new String(bytes, "utf-8");
			returnStr = StringUtils.trim(returnStr);
			return returnStr;
		}
		return null;
	}
}
