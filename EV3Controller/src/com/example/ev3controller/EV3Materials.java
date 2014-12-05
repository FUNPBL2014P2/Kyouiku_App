package com.example.ev3controller;

import android.app.Application;
import android.widget.ImageView;

public class EV3Materials extends Application {
	public EV3 ev3;
	public ImageView[] blocks = new ImageView[8];
	private int Threadstatus = 0; //0がoff,1がon
	
	
	int getThreadstatus(){
		return Threadstatus;
	}
	public void setThreadstatus(int n){
		Threadstatus = n;
	}
	
}