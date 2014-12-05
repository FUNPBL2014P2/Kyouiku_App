package com.example.ev3controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import ev3command.ev3.comm.AndroidComm;
import ev3command.ev3.comm.EV3Command;

public class RemoteActivity extends Activity {
	EV3Materials ev3mt;

		
	public ImageView[] arrows = new ImageView[8];
	public Rect[] rect = new Rect[8];
	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ev3mt = (EV3Materials)this.getApplication() ;
		// Preparation
		findViews();
		for(int i=0;i<8;i++){
			setUpViews(arrows[i], i);
		}
		ev3mt.ev3.threadstart();
		
	}

	
 	private void findViews() {
//		top.arrows[0] = (ImageView) findViewById(R.id.imageView1);
//		top.arrows[1] = (ImageView) findViewById(R.id.imageView2);
//		top.arrows[2] = (ImageView) findViewById(R.id.imageView3);
//		top.arrows[3] = (ImageView) findViewById(R.id.imageView4);
//		top.arrows[4] = (ImageView) findViewById(R.id.imageView5);
//		top.arrows[5] = (ImageView) findViewById(R.id.imageView6);
//		top.arrows[6] = (ImageView) findViewById(R.id.imageView7);
//		top.arrows[7] = (ImageView) findViewById(R.id.imageView8);
 		
 		arrows[0] = (ImageView) findViewById(R.id.imageView1);
		arrows[1] = (ImageView) findViewById(R.id.imageView2);
		arrows[2] = (ImageView) findViewById(R.id.imageView3);
		arrows[3] = (ImageView) findViewById(R.id.imageView4);
		arrows[4] = (ImageView) findViewById(R.id.imageView5);
		arrows[5] = (ImageView) findViewById(R.id.imageView6);
		arrows[6] = (ImageView) findViewById(R.id.imageView7);
		arrows[7] = (ImageView) findViewById(R.id.imageView8);

		ev3mt.blocks[0] = (ImageView) findViewById(R.id.imageView9);
		ev3mt.blocks[1] = (ImageView) findViewById(R.id.imageView10);
		ev3mt.blocks[2] = (ImageView) findViewById(R.id.imageView11);
		ev3mt.blocks[3] = (ImageView) findViewById(R.id.imageView12);
	}

	private void setUpViews(ImageView view, int i) {
		if(view == null) return;
		rect[i] = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:
				//矢印の向きに合わせて進む
				int number=8;
				for(int i=0;i<8;i++){
					if(arrows[i].getX() <= event.getX() && event.getX() <= arrows[i].getX() + arrows[i].getWidth() &&
							arrows[i].getY() + arrows[i].getHeight() <= event.getY() &&
							event.getY() <= arrows[i].getY() + arrows[i].getHeight() * 2)
						number=i;
				}
				if(number==8){
					System.out.println("DON'T TOUCH ARROWS");
					System.out.println(event.getX()+":"+event.getY());
				}else{
					System.out.println("YOU TOUCH NUMBER "+number+" ARROW");
					System.out.println(arrows[number].getX()+":"+arrows[number].getWidth()+":"+arrows[number].getY()+":"+
							arrows[number].getHeight()+":"+event.getX()+":"+event.getY());
				}
				if(number<8) moveEV3(number);
				break;
			case MotionEvent.ACTION_UP:
				//ストップ
				System.out.println("UP");
				ev3mt.ev3.stop();
			}
		
		return true;
	}

	private void moveEV3(int number){
		switch(number){
		case 0:
			ev3mt.ev3.move(100,100);
			break;
		case 1:
			ev3mt.ev3.move(50,100);
			break;
		case 2:
			ev3mt.ev3.move(-100,100);
			break;
		case 3:
			ev3mt.ev3.move(-50,-100);
			break;
		case 4:
			ev3mt.ev3.move(-100,-100);
			break;
		case 5:
			ev3mt.ev3.move(-100,-50);
			break;
		case 6:
			ev3mt.ev3.move(100,-100);
			break;
		case 7:
			ev3mt.ev3.move(100,50);
			break;

		}
	}

}