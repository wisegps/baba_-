package com.wise.baba.newairapi;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
/**
 * 本类目的是全局共用一个RequestQueue，统一网络请求入口 
 * 不同实例对象用不同的handler处理返回结果
 * @author wu
 */
public class BaseVolley {
	
	public static RequestQueue mQueue;
	public Handler handler;

	
	public BaseVolley(){
		super();
	}
	/**
	 * 构造方法，传入handler
	 * 
	 * @param handler
	 */
	public BaseVolley(Handler handler) {
		super();
		this.handler = handler;
		
	}

	/**
	 * 在 Activity 中 初始化一下
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		if (mQueue == null) {
			mQueue = Volley.newRequestQueue(context.getApplicationContext());
		}
	}

	public void request(String url, int what) {
		if(mQueue==null){
			Log.i("BaseVolley", "RequestQueue还木有初始化，请先调用init方法");
			return ;
		}
		ResponseListerer listener = new ResponseListerer(what);
		Request request = new StringRequest(url, listener.success,
				listener.error);
		request.setShouldCache(false);
		mQueue.add(request);
	}
	
	
	/**
	 * @param url 根据Wistorm API请求规则拼接好的url
	 * @param onSuccess 连接成功回调
	 * @param onError   连接失败回调
	 */
	public void request(String url, Listener<String> onSuccess,ErrorListener onError) {
		if(mQueue==null){
			Log.i("BaseVolley", "RequestQueue还木有初始化，请先调用init方法");
			return ;
		}
		Request request = new StringRequest(url, onSuccess, onError);
		request.setShouldCache(false);
		mQueue.add(request);
	}
	
	/**
	 * Volley 请求返回的监听器
	 * 
	 * @author c
	 */
	class ResponseListerer {
		private int msgWhat = -1;
		public Listener<String> success;
		public ErrorListener error;

		/**
		 * 实例化响应监听器
		 * 
		 * @param what
		 */
		public ResponseListerer(int what) {
			super();
			this.msgWhat = what;
			initSuccessListener();
			initErrorListener();

		}

		/**
		 * 注册返回成功监听器
		 */
		public void initSuccessListener() {
			success = new Response.Listener<String>() {
				public void onResponse(String response) {
					Message msg = handler.obtainMessage();
					msg.what = msgWhat;
					msg.arg1 = 0;
					msg.obj = response;
					handler.sendMessage(msg);
				}
			};
		}

		/**
		 * 注册返回失败监听器
		 */
		public void initErrorListener() {
			error = new ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {
					Message msg = handler.obtainMessage();
					msg.what = msgWhat;
					msg.arg1 = -1;
					msg.obj = error;
					handler.sendMessage(msg);
				}
			};

		}

	}
}
