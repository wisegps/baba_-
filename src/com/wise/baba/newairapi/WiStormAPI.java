package com.wise.baba.newairapi;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Handler.Callback;
import android.util.Log;

/**
 * WToolKit
 * 
 * @author c
 * @date 2015-10-10
 * @desc API采用 REST风格，只需将所需 系统参数 和 应用参数 拼装成http请求，即可调用
 */
/**
 * <pre>
 * 除了customer表有一些比较特殊的操作,
 * 比如登陆,注册,重置密码之外,
 * 大部分的数据表都具有create,update,delete,list,get五个通用操作,
 * 根据数据表,传入字段名key及字段值value即可实现相应操作.
 * create接口参数格式: 新增参数:key=value,比如cust_name=测试&address=测试 
 * update接口参数格式: 条件参数: _key=value,
 * 比如_obj_id=1 更新参数:key=value, 
 * 比如obj_name=修改 
 * 
 * delete接口参数格式: 条件参数: key=value,
 * 比如obj_id=1 
 * 
 * get接口参数格式: 条件参数: key=value, 
 * 比如obj_id=1 fields: 返回字段,格式为key1,key2,key3, 比如cust_id,cust_name 
 * 
 * list接口参数格式: 查询参数: 一般格式: key=value
 * 模糊搜索: key=^value, 比如obj_name=^粤B1234 时间段: key=begin_time@end_time,
 * 比如create_time=2015-11-01@2015-12-01 fields: 返回字段, 格式为key1,key2,key3,
 * 比如cust_id,cust_name sorts: 排序字段, 格式为key1,key2,key3, 如果为倒序在字段名称前加-,
 * 比如-key1,key2 page: 分页字段, 一般为数据表的唯一ID min_id: 本页最小分页ID max_id: 本页最大分页ID limit:
 * 返回数量
 * 
 * 访问信令access_token: 除了个别接口, 大部分的接口是需要传入access_token, 开发者需要在登录之后保存access_token,
 * 之后在调用其他接口的时候传入, access_token的有效期为24小时, 过期之后需要重新获取.
 * 
 * <pre>
 */
public class WiStormAPI{
	public String basePath = "http://o.bibibaba.cn/router/rest?method=";
	public HashMap<String, String> hashParam = new HashMap<String, String>();

	/*----------------------------系统参数--------------------------------*/
	// WOP分配给应用的AppKey ，创建应用时可获得
	private String appSecret = "21fb644e20c93b72773bf0f8d0905052";
	private String appKey = "9410bc1cbfa8f44ee5f8a331ba8dd3fc";
	// 时间戳，格式为yyyy-mm-dd HH:mm:ss，例如：2013-05-06 13:52:03。
	private String timestamp = "";
	// 可选，指定响应格式。目前支持格式为json
	private String format = "json";
	// API协议版本，可选值: 2.0。
	private String v = "1.0";
	// 对 API 调用参数（除sign外）进行 md5 加密获得
	private String sign = "";
	// 参数的加密方法选择，可选值是：目前为md5
	private String signMethod = "md5";
	// 通过用户登陆获取或者访问获取Access Token的授权接口获取
	private String session = "";

	/*----------------------------系统参数--------------------------------*/

	public WiStormAPI() {
		super();

	}

	/**
	 * 新建一个网络请求子线程的handler
	 * 
	 * @param handleCallBack
	 *            网络请求的回调函数
	 */
	public Handler initWorkHandler(Callback handleCallBack) {
		HandlerThread handlerThread = new HandlerThread("WiStormAPI");
		handlerThread.start();
		Looper looper = handlerThread.getLooper();
		Handler workHandler = new Handler(looper, handleCallBack);
		return workHandler;
	}

	/**
	 * 根据方法，请求字段，和参数生成http请求
	 * 
	 * @param method
	 *            调用的方法名
	 * @param fields
	 *            请求返回的字段
	 * @param params
	 *            需要传入的参数
	 * @return
	 */
	public String getUrl(String method, String fields,
			HashMap<String, String> params) {
		sign = generateSign(method, fields, params);

		StringBuffer buffer = new StringBuffer();

		// 无返回字段
		if (fields != null && fields.length() > 0) {
			buffer.append("&fields=" + fields);
		}

		// 把params变成字符串参数
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = params.get(key);
			value = encodeUTF(value);
			buffer.append("&" + key + "=" + value);
		}

		String url = basePath + method + "&timestamp=" + timestamp + "&format="
				+ format + "&app_key=" + appKey + "&v=" + v + "&sign_method="
				+ signMethod + buffer.toString() + "&sign=" + sign;
		Log.i("FragmentHomeAir", url);
		return url;
	}

	/**
	 * 生成签名
	 * 
	 * @param method
	 *            调用的方法名
	 * @param fields
	 *            请求返回的字段
	 * @param params
	 *            需要传入的参数
	 * @return
	 */
	public String generateSign(String method, String fields,
			HashMap<String, String> params) {
		hashParam.clear();
		// 方法名称
		hashParam.put("method", method);
		// 时间戳yyyy-mm-dd hh:nn:ss

		timestamp = getCurrentTime();
	
		timestamp = timestamp.replace(" ", "20%");
		Log.i("WiStormAPI", timestamp);
//		timestamp =  encodeUTF(timestamp);
//		;
		
		hashParam.put("timestamp", timestamp);
		// 返回数据格式
		hashParam.put("format", format);
		// app key
		hashParam.put("app_key", appKey);
		// 接口版本
		hashParam.put("v", v);
		// 签名方式
		hashParam.put("sign_method", signMethod);
		// 需要返回的字段]
		if (fields != null && fields.length() > 0) {
			hashParam.put("fields", fields);
		}

		// 参数字段放进去
		Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = params.get(key);
			value = encodeUTF(value);
			Log.i("LoginTest", key + ": " + value);
			hashParam.put(key, value);

		}
		// 把参数排序并进行拼接
		String s = raw(hashParam);
		Log.i("LoginTest",s);
		String sign = WEncrypt.MD5(appSecret + s + appSecret).toUpperCase();
	
		Log.i("LoginTest", "sign:==" + sign);
		return sign;
	}

	private String encodeUTF(String value) {
		
		if(value.contains("@")){//如果没有这个 ，当帐号是邮箱的时候会出现 签名错误
			return value;
		}else{
			value = Uri.encode(value);
			return value;
		}
		
		
	}

	/**
	 * raw 把参数排序并进行拼接
	 * 
	 * @param param
	 */
	public String raw(HashMap<String, String> param) {
		List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(
				param.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,
					Map.Entry<String, String> o2) {
				return (o1.getKey()).toString().compareTo(o2.getKey());
			}
		});

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			Map.Entry<String, String> entry = list.get(i);
			buffer.append(entry.getKey());
			buffer.append(entry.getValue());
		}

		return buffer.toString();
	}

	/**
	 * 
	 * getCurrentTime 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = sdf.format(date);
		return str;
	}
	
	
	/**
	 * @param info
	 * @return
	 */
	public String getMD5(String info){
		try{
		    MessageDigest md5 = MessageDigest.getInstance("MD5");
		    md5.update(info.getBytes("UTF-8"));
		    byte[] encryption = md5.digest();
		    StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < encryption.length; i++){
				if (Integer.toHexString(0xff & encryption[i]).length() == 1){
					strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
			     }else{
			        strBuf.append(Integer.toHexString(0xff & encryption[i]));
			     }
			 }  
		    return strBuf.toString();
		}catch (NoSuchAlgorithmException e){
		    return "";
		}catch (UnsupportedEncodingException e){
		    return "";
		}
	}

}
