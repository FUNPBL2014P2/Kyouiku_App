package com.example.ev3controller;

import ev3command.ev3.UnidentifiedSensor;
import java.util.TimerTask;
import android.widget.ImageView;


public class WatchingSensor extends TimerTask{
	StringBuffer[] sb;
	private UnidentifiedSensor[] Sensors = new UnidentifiedSensor[4];
	ImageView[] blocks;


	public WatchingSensor(UnidentifiedSensor[] mSensors, StringBuffer[] str, ImageView[] block){
		this.Sensors = mSensors;
		this.sb = str;
		this.blocks = block;
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
