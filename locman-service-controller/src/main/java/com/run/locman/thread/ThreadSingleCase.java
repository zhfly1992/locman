/*
 * File name: ThreadSingleCase.java
 *
 * Purpose:
 *
 * Functions used and called: Name Purpose ... ...
 *
 * Additional Information:
 *
 * Development History: Revision No. Author Date 1.0 zhaoweizhi 2018年11月16日 ...
 * ... ...
 *
 ***************************************************/

package com.run.locman.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: thread单例模式
 * @author: zhaoweizhi
 * @version: 1.0, 2018年11月16日
 */

public class ThreadSingleCase {
	/**
	 * 懒汉式单例模式 比较懒，在类加载时，不创建实例，因此类加载速度快，但运行时获取对象的速度慢
	 */
	private static ExecutorService instance = null;



	private ThreadSingleCase() {
		// 私有构造函数
	}



	/**
	 * 
	 * @Description:静态，同步，公开访问点
	 * @return
	 */
	public static synchronized ExecutorService getInstance() {
		if (instance == null) {
			instance = new ThreadPoolExecutor(10, 100, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100000));
		}
		return instance;
	}

}
