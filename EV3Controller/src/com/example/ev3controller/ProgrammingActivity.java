package com.example.ev3controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ProgrammingActivity extends Activity {
	private ProgrammingView programmingview;
	private View buttonView;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//メインビューの設定
		programmingview = new ProgrammingView(this);
		setContentView(programmingview);
		
		//サブビュー(ボタンを配置するレイアウト)の設定
		buttonView = getLayoutInflater().inflate(R.layout.programming_button, null);
		addContentView(buttonView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}
	
	//画面が切り替わった時、または、完全に生成された時に呼ばれるメソッド
	@Override
	public void onWindowFocusChanged(boolean hasFocus){
		super.onWindowFocusChanged(hasFocus);
		WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
		Display dp = wm.getDefaultDisplay();
		programmingview.setDisplaySize(dp.getWidth(), dp.getHeight());
	}

}