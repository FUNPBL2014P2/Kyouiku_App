package com.example.ev3controller;

import ev3command.ev3.Motor;
import ev3command.ev3.SensorPort;
import ev3command.ev3.UnidentifiedSensor;
import ev3command.ev3.comm.AndroidComm;
import ev3command.ev3.comm.EV3Command;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.graphics.Rect;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

	//private SeekBar[] mMotorSeekBars = new SeekBar[4];
	//private Button[] mForwardButtons = new Button[4];
	//private Button[] mBackwardButtons = new Button[4];
	//private Button[] mStopButtons = new Button[4];
	private Motor[] mMotors = new Motor[4];

	//private TextView[] mSensorNameTexts = new TextView[4];
	//private TextView[] mSensorValueTexts = new TextView[4];
	//private Button[] mGetPercentValueButtons = new Button[4];
	//private Button[] mGetSiUnitValueButtons = new Button[4];
	private UnidentifiedSensor[] mSensors = new UnidentifiedSensor[4];

	private Button mConnectButton;
	private BluetoothAdapter mBtAdapter = null;

	private ImageView[] arrows = new ImageView[8];
	private ImageView[] blocks = new ImageView[4];

	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_CONNECT_DEVICE = 2;

	private ProgressDialog mProgressDialog;

	private static final int FAILED_TO_CONNECT = 1;
	private static final int SUCCEEDED_CONNECTING = 2;

	private int arrayNum;

	StringBuffer[] Str = new StringBuffer[4];//センサ値
	Timer timer = new Timer();
	//TimerTask timertask = new TimerTask();

	private Rect[] rect = new Rect[8];
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Preparation
		setUpEV3();
		findViews();
		setUpButtons();
		for(int i=0;i<8;i++){
			setUpViews(arrows[i], i);
		}

		for(int i=0;i<4;i++){
			Str[i] = new StringBuffer();
		}

		//setUpSeekBars();

		// UI should be disabled until this device connects to EV3
		setUiEnabled(false);

		// Get default adapter
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	private void setUiEnabled(boolean enabled) {
		for (int i = 0; i < 4; i++) {
			//mMotorSeekBars[i].setEnabled(enabled);
			//mForwardButtons[i].setEnabled(enabled);
			//mBackwardButtons[i].setEnabled(enabled);
			//mStopButtons[i].setEnabled(enabled);
			//mGetPercentValueButtons[i].setEnabled(enabled);
			//mGetSiUnitValueButtons[i].setEnabled(enabled);
		}
	}

	private void setUpEV3() {
		mMotors[0] = Motor.A;
		mMotors[1] = Motor.B;
		mMotors[2] = Motor.C;
		mMotors[3] = Motor.D;

		// If you know specified sensors are connected, you can use
		// TouchSensor, SoundSensor etc. instead of UnidentifiedSensor.
		mSensors[0] = new UnidentifiedSensor(SensorPort.S1);
		mSensors[1] = new UnidentifiedSensor(SensorPort.S2);
		mSensors[2] = new UnidentifiedSensor(SensorPort.S3);
		mSensors[3] = new UnidentifiedSensor(SensorPort.S4);
	}

	private void findViews() {
		/*for (int i = 0; i < 4; i++) {
			mMotorSeekBars[i] = (SeekBar) findViewById(getResources().getIdentifier("sb.motor" + (i + 1), "id", getPackageName()));
			mForwardButtons[i] = (Button) findViewById(getResources().getIdentifier("bt.forward" + (i + 1), "id", getPackageName()));
			mBackwardButtons[i] = (Button) findViewById(getResources().getIdentifier("bt.backward" + (i + 1), "id", getPackageName()));
			mStopButtons[i] = (Button) findViewById(getResources().getIdentifier("bt.stop" + (i + 1)	, "id", getPackageName()));

			mSensorNameTexts[i] = (TextView) findViewById(getResources().getIdentifier("tv.sensorName" + (i + 1), "id", getPackageName()));
			mSensorValueTexts[i] = (TextView) findViewById(getResources().getIdentifier("tv.sensor" + (i + 1), "id", getPackageName()));
			mGetPercentValueButtons[i] = (Button) findViewById(getResources().getIdentifier("bt.percent" + (i + 1), "id", getPackageName()));
			mGetSiUnitValueButtons[i] = (Button) findViewById(getResources().getIdentifier("bt.si" + (i + 1), "id", getPackageName()));
		}*/
		/*for(int i=0;i<8;i++){
			arrows[i] = (ImageView) findViewById(getResources().getIdentifier("imgaeView"+(i+1), "id", getPackageName()));
		}
		for(int i=0;i<4;i++){
			blocks[i] = (ImageView) findViewById(getResources().getIdentifier("imageView"+(i+9), "id", getPackageName()));
		}*/
		arrows[0] = (ImageView) findViewById(R.id.imageView1);
		arrows[1] = (ImageView) findViewById(R.id.imageView2);
		arrows[2] = (ImageView) findViewById(R.id.imageView3);
		arrows[3] = (ImageView) findViewById(R.id.imageView4);
		arrows[4] = (ImageView) findViewById(R.id.imageView5);
		arrows[5] = (ImageView) findViewById(R.id.imageView6);
		arrows[6] = (ImageView) findViewById(R.id.imageView7);
		arrows[7] = (ImageView) findViewById(R.id.imageView8);

		blocks[0] = (ImageView) findViewById(R.id.imageView9);
		blocks[1] = (ImageView) findViewById(R.id.imageView10);
		blocks[2] = (ImageView) findViewById(R.id.imageView11);
		blocks[3] = (ImageView) findViewById(R.id.imageView12);
		mConnectButton = (Button) findViewById(R.id.bt_connect);

	}

	private int getIndex(View v, View[] vs) {
		for (int i = 0; i < 4; i++) {
			if (v.equals(vs[i])) return i;
		}
		return -1;
	}

	/*private void setUpSeekBars() {
		for (int i = 0; i < 4; i++) {
			mMotorSeekBars[i].setMax(100);
			mMotorSeekBars[i].setProgress(50); // Set the initial value
			mMotorSeekBars[i].setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					int power = seekBar.getProgress();
					int index = getIndex(seekBar, mMotorSeekBars);

					// This method only sets the power on Android.
					mMotors[index].setSpeed(power);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				}
			});
		}
	}*/

	private void setUpButtons() {
		/*for (int i = 0; i < 4; i++) {
			mForwardButtons[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mMotors[getIndex(v, mForwardButtons)].forward(); // forward
				}
			});
			mBackwardButtons[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mMotors[getIndex(v, mBackwardButtons)].backward(); // backward
				}
			});
			mStopButtons[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mMotors[getIndex(v, mStopButtons)].stop(); // stop
				}
			});

			mGetPercentValueButtons[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int index = getIndex(v, mGetPercentValueButtons);
					int result = mSensors[index].getPercentValue();
					mSensorValueTexts[index].setText("Percent: " + result);
				}
			});
			mGetSiUnitValueButtons[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int index = getIndex(v, mGetSiUnitValueButtons);
					float result = mSensors[index].getSiValue();
					mSensorValueTexts[index].setText("Si unit: " + result);
				}
			});
		}*/

		mConnectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findEV3Device(); // Show the device list
			}
		});
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
			mMotors[0].stop();
			mMotors[3].stop();

		/*case MotionEvent.ACTION_MOVE:
			if(mConnectButton.getText()=="Disconnect"){
				for(int i=0;i<4;i++){
					if(Str[i].charAt(Str[i].length()-1)=='p')
						blocks[i].setImageResource(R.drawable.pinkblock);
					else
						blocks[i].setImageResource(R.drawable.grayblock);
				}
			}
			break;*/
		}
		return true;
	}

	private boolean isTouchImage(int x,int y) {
		for(int i=0;i<8;i++){
			int imgX = rect[i].left;
			int imgY = rect[i].top;
			if( x-imgX >= 0 && x-imgX < rect[i].width() && y-imgY >= 0 && y-imgY < rect[i].height())
				return true;
		}
		return false;
	}

	private void moveEV3(int number){
		switch(number){
		case 0:
			mMotors[0].setSpeed(100);
			mMotors[3].setSpeed(100);
			mMotors[0].forward();
			mMotors[3].forward();
			break;
		case 1:
			mMotors[0].setSpeed(50);
			mMotors[3].setSpeed(100);
			mMotors[0].forward();
			mMotors[3].forward();
			break;
		case 2:
			mMotors[0].setSpeed(0);
			mMotors[3].setSpeed(100);
			mMotors[0].forward();
			mMotors[3].forward();
			break;
		case 3:
			mMotors[0].setSpeed(50);
			mMotors[3].setSpeed(100);
			mMotors[0].backward();
			mMotors[3].backward();
			break;
		case 4:
			mMotors[0].setSpeed(100);
			mMotors[3].setSpeed(100);
			mMotors[0].backward();
			mMotors[3].backward();
			break;
		case 5:
			mMotors[0].setSpeed(100);
			mMotors[3].setSpeed(50);
			mMotors[0].backward();
			mMotors[3].backward();
			break;
		case 6:
			mMotors[0].setSpeed(100);
			mMotors[3].setSpeed(0);
			mMotors[0].forward();
			mMotors[3].forward();
			break;
		case 7:
			mMotors[0].setSpeed(100);
			mMotors[3].setSpeed(50);
			mMotors[0].forward();
			mMotors[3].forward();
			break;

		}
	}


	private void findEV3Device() {
		// Turns on Bluetooth
		if (!mBtAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			return;
		}

		// Starts DeviceListActivity
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	private void foundEV3Device(BluetoothDevice device) {
		AndroidComm.getInstance().setDevice(device); // Set device

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("接続中");
		mProgressDialog.setMessage("しばらくお待ちください");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();

		// Connect to EV3
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					EV3Command.open();
					mConnectingHandler.sendEmptyMessage(SUCCEEDED_CONNECTING);
				}
				catch (Exception e) {
					// This exception also occurs when this device hasn't
					// finished paring
					mConnectingHandler.sendEmptyMessage(FAILED_TO_CONNECT);
				}
				mProgressDialog.dismiss(); // Dismiss the dialog
			}
		}).start();
	}

	private void connected() {
		mConnectButton.setText("Disconnect");
		mConnectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disconnect();
			}
		});
		setUiEnabled(true);
		Toast.makeText(this, "EV3 Connected", Toast.LENGTH_SHORT).show();


		for (int i = 0; i < 4; i++) {
			String name = mSensors[i].getName();
			Log.d("MainActivity", "Name: " + name);
		}

		WatchingSensor wSensor = new WatchingSensor(mSensors, Str, blocks);
		timer.schedule(wSensor, 0,1);
		//Thread thread = new Thread(wSensor);
		//thread.start();
	}

	private void disconnect() {
		// Close connection
		try {
			EV3Command.close();
		}
		catch (RuntimeException e) {
		}

		mConnectButton.setText("Connect");
		mConnectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findEV3Device();
			}
		});
		setUiEnabled(false);
		Toast.makeText(this, "EV3 Disconnected", Toast.LENGTH_SHORT).show();
	}

	private Handler mConnectingHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			switch (message.what) {
			case FAILED_TO_CONNECT:
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("Bluetooth connection error")
				.setMessage("Bluetooth デバイスとの接続に失敗しました")
				.setPositiveButton("OK", null)
				.show();
				return true;

			case SUCCEEDED_CONNECTING:
				connected();
				return true;
			}
			return false;
		}
	});

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
			}
			break;

		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BluetoothDevice object
				BluetoothDevice device = mBtAdapter
						.getRemoteDevice(address);
				// Attempt to connect to the device
				foundEV3Device(device);
			}
			break;
		}
	}
}
