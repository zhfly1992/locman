package com.locman.app.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class JSONObjectUtiles {

	public JSONObject getJson(Resource resource) {
		BufferedReader br;
		JSONObject jsonObject=null;
		try {
			br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
			StringBuffer message = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				message.append(line);
			}
			String defaultString = new String(message.toString().getBytes(),"UTF-8");
			String defaultMenu = defaultString.replace("\r\n", "").replaceAll(" +", "");
			jsonObject=JSONObject.parseObject(defaultMenu);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

}
