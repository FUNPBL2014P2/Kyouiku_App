package com.example.ev3controller;

import android.widget.ImageView;
import ev3command.ev3.Motor;
import ev3command.ev3.SensorPort;
import ev3command.ev3.UnidentifiedSensor;
import ev3command.ev3.comm.EV3Command;

public class EV3 {
	private UnidentifiedSensor[] mSensors = new UnidentifiedSensor[4];
	private Motor[] mMotors = new Motor[4];
	Thread thread;
	StringBuffer[] Str;
	public EV3(ImageView[] blocks, StringBuffer[] Str){
		mMotors[0] = Motor.A;
		mMotors[1] = Motor.B;
		mMotors[2] = Motor.C;
		mMotors[3] = Motor.D;

		mSensors[0] = new UnidentifiedSensor(SensorPort.S1);
		mSensors[1] = new UnidentifiedSensor(SensorPort.S2);
		mSensors[2] = new UnidentifiedSensor(SensorPort.S3);
		mSensors[3] = new UnidentifiedSensor(SensorPort.S4);

		WatchingSensor wSensor = new WatchingSensor(mSensors, Str, blocks);
		this.Str = Str;
		//timer.schedule(wSensor, 0,1);
		System.out.println("スレッド");
		thread = new Thread(wSensor);
		//thread.start();

	}

	public boolean getLeftTouchSensor(){
		if(Str[0].toString().equals("g"))
			return false;
		else
			return true;
	}

	public boolean getRightTouchSensor(){
		if(Str[3].toString().equals("g"))
			return false;
		else
			return true;
	}

	public void threadstart(){
			thread.start();
	}
	
	public void threadstop(){
		thread.interrupt();
	}

	public void move(int right, int left){
		mMotors[0].setSpeed(Math.abs(right));
		mMotors[3].setSpeed(Math.abs(left));

		if(right>0)
			mMotors[0].forward();
		else
			mMotors[0].backward();
		if(left>0)
			mMotors[3].forward();
		else
			mMotors[3].backward();
	}

	public void stop(){
		mMotors[0].stop();
		mMotors[3].stop();
	}


}
