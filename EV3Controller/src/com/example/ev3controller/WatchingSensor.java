package com.example.ev3controller;

import ev3command.ev3.UnidentifiedSensor;

import java.util.TimerTask;

import android.widget.ImageView;
import android.os.Handler;
import android.os.Message;



public class WatchingSensor extends Thread{
	public EV3Materials ev3mt;
	
	StringBuffer[] sb;
	private UnidentifiedSensor[] Sensors = new UnidentifiedSensor[4];
	ImageView[] blocks;
	private MyHandler handler = new MyHandler();
	

	public WatchingSensor(UnidentifiedSensor[] mSensors, StringBuffer[] str, ImageView[] block){
		this.Sensors = mSensors;
		this.sb = str;
		this.blocks = block;
	}

	public void run(){
		while(!Thread.currentThread().isInterrupted()){
			for(int i=0;i<4;i++){
				if(Sensors[i].getPercentValue()>=50){
					sb[i].append('p');
					//System.out.println("センサー"+i+"="+sb[i]);
				}
				else{
					sb[i].append('g');
					//System.out.println("センサー"+i+"="+sb[i]);
				}
				//Handlerに通知する
				Message msg = new Message();
				//msgのwhatにselectedIndexの値を格納
				msg.what = i;
				//handlerにmsgをsendする。
				handler.sendMessage(msg);
			}
		}
	}

	class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			if(ev3mt.getFunctionstatus() == 1){
				int index = msg.what;
				if(sb[index].charAt(sb[index].length()-1)=='p'){
					blocks[index].setImageResource(R.drawable.pinkblock);
					sb[index].delete(0, sb[index].length()-1);
				}
				else{
					//TODO
					//ブロックのビューが使えるようになったらこのコメントアウトを消す
					blocks[index].setImageResource(R.drawable.grayblock);
					sb[index].delete(0, sb[index].length()-1);
				}
			}
		}
	}

}
