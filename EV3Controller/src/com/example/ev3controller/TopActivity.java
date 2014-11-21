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


public class TopActivity extends Activity {

	//TODO
	private EV3 ev3;
	private Button mConnectButton;
	private BluetoothAdapter mBtAdapter = null;

	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_CONNECT_DEVICE = 2;

	private ProgressDialog mProgressDialog;

	private static final int FAILED_TO_CONNECT = 1;
	private static final int SUCCEEDED_CONNECTING = 2;

	StringBuffer[] Str = new StringBuffer[4];//センサ値

	public ImageView[] arrows = new ImageView[8];
	public ImageView[] blocks = new ImageView[4];

	public boolean BTstate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get default adapter
		//センサー値を取ってくれる
		for(int i=0;i<4;i++){
			Str[i] = new StringBuffer();
		}
		setContentView(R.layout.activity_top);
		findViews();
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		setUpButtons();
	}

	private void findViews() {

		mConnectButton = (Button) findViewById(R.id.bt_connect);

	}

	private void setUpButtons() {
		mConnectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findEV3Device(); // Show the device list
			}
		});
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
				//timer.cancel();
				ev3.threadstop();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				disconnect();
			}
		});
		Toast.makeText(this, "EV3 Connected", Toast.LENGTH_SHORT).show();


		ev3 = new EV3(blocks,Str);
		for (int i = 0; i < 4; i++) {
			//String name = mSensors[i].getName();
			//Log.d("MainActivity", "Name: " + name);
		}
		
		BTstate=true;
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
		Toast.makeText(this, "EV3 Disconnected", Toast.LENGTH_SHORT).show();
		BTstate=false;
	}

	private Handler mConnectingHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message message) {
			switch (message.what) {
			case FAILED_TO_CONNECT:
				new AlertDialog.Builder(TopActivity.this)
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
