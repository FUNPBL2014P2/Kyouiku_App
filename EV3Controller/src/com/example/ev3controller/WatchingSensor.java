package com.example.ev3controller;

import ev3command.ev3.UnidentifiedSensor;

public class WatchingSensor implements Runnable{
	StringBuffer[] sb;
	private UnidentifiedSensor[] Sensors = new UnidentifiedSensor[4];

	public WatchingSensor(UnidentifiedSensor[] mSensors, StringBuffer[] str){
		this.Sensors = mSensors;
		this.sb = str;
		for(int i=0;i<4;i++){
		System.out.println("センサーの種類："+Sensors[i].getName());
		}
	}

	public void run(){
		for(int i=0;i<4;i++){
			if(Sensors[i].getPercentValue()>=50){
				sb[i].append('p');
				System.out.println("センサー"+i+"="+sb[i].charAt(sb[i].length()-1));
			}
			else{
				sb[i].append('g');
				System.out.println("センサー"+i+"="+sb[i].charAt(sb[i].length()-1));
			}
		}
	}

}
