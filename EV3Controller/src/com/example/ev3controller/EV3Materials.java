package com.example.ev3controller;

import android.app.Application;
import android.widget.ImageView;

public class EV3Materials extends Application {
	public EV3 ev3;
	public ImageView[] blocks = new ImageView[8];
	public static int Threadstatus = 0; //0がoff,1がon
	public static int Functionstatus = 0; //初期値0(top),ラジコン1,プログラミング2
	
	
	public static int getThreadstatus(){
		return Threadstatus;
	}
	public static void setThreadstatus(int n){
		Threadstatus = n;
	}
	
	public static int getFunctionstatus(){
		return Functionstatus;
	}
	public static void setFunctionstatus(int n){
		Functionstatus = n;
	}
	
}