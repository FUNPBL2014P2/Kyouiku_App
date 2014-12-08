package com.example.ev3controller;

import ev3command.ev3.UnidentifiedSensor;

import java.util.TimerTask;

import android.widget.ImageView;
import android.os.Handler;
import android.os.Message;



public class WatchingSensor extends Thread{
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
				sb[i].delete(0, sb[i].length()-1);
				//Handlerに通知する
				//	Message msg = new Message();//こいつがあると、エラーが出る
				//msgのwhatにselectedIndexの値を格納
				//	msg.what = i;//こいつがあると、エラーが出る
				//handlerにmsgをsendする。
				//	handler.sendMessage(msg);//こいつがあると、エラーが出る

			}
		}
	}

	class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg){
			int index = msg.what;
			if(sb[index].charAt(sb[index].length()-1)=='p'){
				blocks[index].setImageResource(R.drawable.pinkblock);
			}
			else{
				//TODO
				//ブロックのビューが使えるようになったらこのコメントアウトを消す
				blocks[index].setImageResource(R.drawable.grayblock);
			}
		}
	}

}
