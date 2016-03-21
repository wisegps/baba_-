package com.wise.baba.newairapi;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;


public class AirApi extends WiStormAPI{
	
	public String Method_Access_Token = "wicare.user.access_token";
	public String Method_Set_Command = "wicare.command.create";
	
	public HashMap<String, String> hashParam = new HashMap<String, String>();
	public Context context;
	
	private Handler uiHandler;// UI线程
	private Handler workHandler;// 工作线程，非UI线程
	private BaseVolley volley;
	
	
	public AirApi(Handler uiHandler){
		this.uiHandler = uiHandler;
		init();
	}
	
	/**
	 * 初始化数据
	 * 
	 * @param uiHandler
	 */
	public void init() {
		this.workHandler = super.initWorkHandler(handleCallBack);
		volley = new BaseVolley(workHandler);
	}
	
	
	/**
	 * 工作子线程回调函数,负责接收网络返回数据，并交给解析函数进一步处理
	 */
	private Callback handleCallBack = new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			
			case AirMsg.GET_NEW_API_TOKEN:
				parseToken(msg);
				break;		
			case AirMsg.SET_AIR_SPEED_COMMAND:
				parseSetAirSpeed(msg);
				break;
			case AirMsg.SET_AIR_SWITCH_COMMAND:
				Log.i("FragmentHomeAir", "控制开关返回信息: " + msg.toString());
				break;
			case AirMsg.SET_AIR_MODEL_COMMAND:
				Log.i("FragmentHomeAir", "模式控制返回信息: " + msg.toString());
				break;
			}
			return false;
		}
	};
	
	
	
	/***************************************************** 解析网络返回的数据 ********************************/
	/**
	 * 解析返回的token数据
	 * 
	 * @param msg
	 */
	private void parseToken(Message msg) {
		try {
			JSONObject json = new JSONObject(msg.obj.toString());
			Message uimsg = uiHandler.obtainMessage();
			uimsg.what = msg.what;
			Bundle bundle = new Bundle();
			bundle.putString("access_token", json.getString("access_token"));
			bundle.putString("valid_time", json.getString("valid_time"));
			uimsg.setData(bundle);
			uiHandler.sendMessage(uimsg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param msg
	 */
	private void parseSetAirSpeed(Message msg){
		try {
			JSONObject json = new JSONObject(msg.obj.toString());
			Message uimsg = uiHandler.obtainMessage();
			uimsg.what = msg.what;
			Bundle bundle = new Bundle();
			bundle.putString("status_code", json.getString("status_code"));
			uimsg.setData(bundle);
			uiHandler.sendMessage(uimsg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/***************************************************** 请求业务 ********************************/

	/**
	 * 获取令牌
	 * 
	 * @param account
	 *            手机号码或者邮箱地址
	 * @param password
	 *            登陆密码
	 */
	public void getToken(String account, String password) {
		password = WEncrypt.MD5(password);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("account", account);
		params.put("password", password);
		String url = super.getUrl(Method_Access_Token, "", params);
		volley.request(url, AirMsg.GET_NEW_API_TOKEN);
	}
	
	
	/**
	 * @param token
	 * @param device_id
	 * @param command 
	 * @param model   //16452（控制模式）//16453 速度
	 */
	public void setAirCommand(String token,String device_id,String command,String model){
	
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("access_token", token);
		params.put("device_id", device_id);
		params.put("params", command);
		params.put("cmd_type", model);
		String url = super.getUrl(Method_Set_Command, "", params);
		
		if(model.equals(AirCommand.SPEED_COMMAND_MODEL)){
			volley.request(url, AirMsg.SET_AIR_SPEED_COMMAND);
		}else if(model.equals(AirCommand.SWITCH_COMMAND_MODEL)){
			volley.request(url, AirMsg.SET_AIR_SWITCH_COMMAND);
		}else if(model.equals(AirCommand.MODEL_SET_COMMAND_MODEL)){
			volley.request(url, AirMsg.SET_AIR_MODEL_COMMAND);
		}
	}
	

}
