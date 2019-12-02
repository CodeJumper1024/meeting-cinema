package com.stylefeng.guns.rest.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionUtils {

	public static String readFileToString(String uri) {

		String s = null;
		try {
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoOutput(true); // 设置该连接是可以输出的
			connection.setRequestMethod("GET"); // 设置请求方式
			connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) { // 读取数据
				sb.append(line);
			}
			connection.disconnect();
			s = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
}
