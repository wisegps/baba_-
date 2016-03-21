package com.wise.baba.newairapi;

public class AirCommand {

	
	public final static String AIR_POWER_ON  = "{switch:1}";
	public final static String AIR_POWER_OFF = "{switch:0}";
	
	
	public final static String AIR_NORMAL_MODEL = "{air_mode:0}";
	public final static String AIR_SMART_MODEL  = "{air_mode:1}";
	public final static String AIR_TIMER_MODEL  = "{air_mode:2}";
	
	
	public final static String LOW_SPEED    = "{air_speed:1}";
	public final static String MIDDLE_SPEED = "{air_speed:2}";
	public final static String HIGHT_SPEED  = "{air_speed:3}";
	
	public final static String SPEED_COMMAND_MODEL      = "16453";//调节速度   低、中、高
	public final static String MODEL_SET_COMMAND_MODEL  = "16452";//设置模式   手动、智能、定时
	public final static String SWITCH_COMMAND_MODEL     = "16451";//设置开关   启动 、停止
	
	
}
