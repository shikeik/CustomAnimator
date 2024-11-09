package com.goldsprite.util;

import java.io.*;
import com.goldsprite.customanimator.*;

public class Log
{
	
	public static void log(String log){
		AppLog.saveLog(log);
	}
	public static void logErr(String msg, Exception e){
		String log = msg+"\n"+getStackTraceStr(e);
		AppLog.toast(log);
		AppLog.saveLog(log);
	}

	public static String getStackTraceStr(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
	
}
