package com.run.base.test.cases;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @Description: 测试类
 * @author: 王胜
 * @version: 1.0, 2019年2月22日
 */
public class PackageTest {

	/*@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
					new String[] { "spring-test-invoker.xml" });
			context.start();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}*/
	public static void main(String[] args) {
		String aString =  "{\"qqq\":\"车行道\",\"wellIn\":[\"http://193.168.0.90:8090/groupA/M00/00/00/wagAWlyhqZCIXapoAACh_wN1ABwAAAADQPgk4kAAKIX025.jpg\"],\"csdx1_a\":true,\"code\":\"11\",\"count\":\"11\",\"csdx2_bb\":true,\"csdx2_cc\":true,\"comment\":\"11\",\"sizel\":\"15\",\"lat\":\"104.371287,30.705713\",\"csdx1_b\":true,\"direction\":[\"http://193.168.0.90:8090/groupA/M00/00/00/wagAWl0lSMWIB4opAAAgE8j5kxsAAAAAQBPe3QAACAr882.jpg\"]}";
		JSONObject aJsonObject = JSONObject.parseObject(aString);
		System.out.println(aJsonObject);
	}

}
