package com.example.ev3controller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;

public class ProgrammingView extends View{
	int InstanceBlocksNum = 8;//インスタンスブロックの数
	Bitmap genreImage[] = new Bitmap[3];//ブロックイメージの読み込み
	Bitmap instanceImage[] = new Bitmap[InstanceBlocksNum];
	Bitmap startImage;
	boolean instanceFlag = false;

	private Point dispSize = new Point();//線を描写するのに必要な変数

	private int maxHeight;//作業スペースの高さの最大

	private static final double BLOCK_SCALE = 0.6;

	//コンストラクタ
	public ProgrammingView(Context context){
		super(context);

		//背景の色を設定
		this.setBackgroundColor(Color.WHITE);

		//ジャンルブロックの画像を設定
		Resources r = context.getResources();
		genreImage[0] = BitmapFactory.decodeResource(r, R.drawable.movebuton);
		genreImage[1] = BitmapFactory.decodeResource(r, R.drawable.ifbutton);
		genreImage[2] = BitmapFactory.decodeResource(r, R.drawable.forbutton);

		//スタートブロックの画像の設定
		r = context.getResources();
		startImage = BitmapFactory.decodeResource(r, R.drawable.start);

		startImage = Bitmap.createScaledBitmap(startImage, (int)(startImage.getWidth()*BLOCK_SCALE), (int)(startImage.getHeight()*BLOCK_SCALE), false);

		maxHeight=0;
	}

	@Override
	protected void onDraw(Canvas canvas){
		//インスタンスブロック領域線
		Paint paint = new Paint();

		paint.setAntiAlias(true);
		paint.setStrokeWidth(3.0f);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);

		float x1 = dispSize.x/5;
		float y = dispSize.y;

		//ジャンルエリアとインスタンスエリアの境界線
		canvas.drawLine(x1, 0, x1, y, paint);

		//インスタンスエリアとワークスペースの境界線
		float x2 = x1;
		if(instanceFlag == true)
			x2 = x1 + instanceImage[0].getWidth() + 60;
		canvas.drawLine(x2, 0, x2, y, paint);

		//ジャンルボタン
		for(int i=0; i<3; i++){
			canvas.drawBitmap(genreImage[i], 25, 25+200*i, null);
		}

		//スタートボタン
		canvas.drawBitmap(startImage, x2+30, 25, null);

		//インスタンスブロック
		for(int i=0;i<InstanceBlocksNum;i++){
			if(instanceImage[i]==null)
				break;
			canvas.drawBitmap(instanceImage[i], x1+25, 25+200*i, null);
		}
	}

	//タッチイベント
	@Override
	public boolean onTouchEvent(MotionEvent event){
		int action = event.getAction();
		int blocktype;
		switch(action & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			if(event.getX() <= dispSize.x/5){//もしジャンルエリアをタッチされたら
				Resources r = getResources();
				int genre = JudgeTouchGenreBlock(event.getX(), event.getY());
				if(genre==1){//「うごき」
					instanceFlag = true;
					for(int i=0;i<InstanceBlocksNum;i++){
						instanceImage[i]=null;
					}
					instanceImage[0] = BitmapFactory.decodeResource(r, R.drawable.go0block);
					instanceImage[0] = Bitmap.createScaledBitmap(instanceImage[0], (int)(instanceImage[0].getWidth()*BLOCK_SCALE), (int)(instanceImage[0].getHeight()*BLOCK_SCALE), false);
					instanceImage[1] = BitmapFactory.decodeResource(r, R.drawable.go1block);
					instanceImage[1] = Bitmap.createScaledBitmap(instanceImage[1], (int)(instanceImage[1].getWidth()*BLOCK_SCALE), (int)(instanceImage[1].getHeight()*BLOCK_SCALE), false);
					instanceImage[2] = BitmapFactory.decodeResource(r, R.drawable.go2block);
					instanceImage[2] = Bitmap.createScaledBitmap(instanceImage[2], (int)(instanceImage[2].getWidth()*BLOCK_SCALE), (int)(instanceImage[2].getHeight()*BLOCK_SCALE), false);
					instanceImage[3] = BitmapFactory.decodeResource(r, R.drawable.go3block);
					instanceImage[3] = Bitmap.createScaledBitmap(instanceImage[3], (int)(instanceImage[3].getWidth()*BLOCK_SCALE), (int)(instanceImage[3].getHeight()*BLOCK_SCALE), false);
					instanceImage[4] = BitmapFactory.decodeResource(r, R.drawable.go4block);
					instanceImage[4] = Bitmap.createScaledBitmap(instanceImage[4], (int)(instanceImage[4].getWidth()*BLOCK_SCALE), (int)(instanceImage[4].getHeight()*BLOCK_SCALE), false);
					instanceImage[5] = BitmapFactory.decodeResource(r, R.drawable.go5block);
					instanceImage[5] = Bitmap.createScaledBitmap(instanceImage[5], (int)(instanceImage[5].getWidth()*BLOCK_SCALE), (int)(instanceImage[5].getHeight()*BLOCK_SCALE), false);
					instanceImage[6] = BitmapFactory.decodeResource(r, R.drawable.go6block);
					instanceImage[6] = Bitmap.createScaledBitmap(instanceImage[6], (int)(instanceImage[6].getWidth()*BLOCK_SCALE), (int)(instanceImage[6].getHeight()*BLOCK_SCALE), false);
					instanceImage[7] = BitmapFactory.decodeResource(r, R.drawable.go7block);
					instanceImage[7] = Bitmap.createScaledBitmap(instanceImage[7], (int)(instanceImage[7].getWidth()*BLOCK_SCALE), (int)(instanceImage[7].getHeight()*BLOCK_SCALE), false);

				}
				else if(genre==2){//「じょうけん」
					instanceFlag = false;
					for(int i=0;i<InstanceBlocksNum;i++){
						instanceImage[i]=null;
					}
				}
				else if(genre==3){//「くりかえし」
					instanceFlag = true;
					for(int i=0;i<InstanceBlocksNum;i++){
						instanceImage[i]=null;
					}
					instanceImage[0] = BitmapFactory.decodeResource(r, R.drawable.ubswt);
					instanceImage[0] = Bitmap.createScaledBitmap(instanceImage[0], (int)(instanceImage[0].getWidth()*BLOCK_SCALE), (int)(instanceImage[0].getHeight()*BLOCK_SCALE), false);
					instanceImage[1] = BitmapFactory.decodeResource(r, R.drawable.urswt);
					instanceImage[1] = Bitmap.createScaledBitmap(instanceImage[1], (int)(instanceImage[1].getWidth()*BLOCK_SCALE), (int)(instanceImage[1].getHeight()*BLOCK_SCALE), false);
					instanceImage[2] = BitmapFactory.decodeResource(r, R.drawable.urslt);
					instanceImage[2] = Bitmap.createScaledBitmap(instanceImage[2], (int)(instanceImage[2].getWidth()*BLOCK_SCALE), (int)(instanceImage[2].getHeight()*BLOCK_SCALE), false);
				}
				else{//その他の白い部分
					instanceFlag=false;
					for(int i=0;i<InstanceBlocksNum;i++){
						instanceImage[i]=null;
					}
				}
			}

			invalidate();
			break;
		}
		return true;
	}

	//View生成時にViewの大きさを設定するためのメソッド
	public void setDisplaySize(int width, int height){
		if(maxHeight == 0){
			//Viewの画面サイズの取得
			dispSize.x = width;
			dispSize.y = height;

			invalidate();
		}
	}

	//どのジャンルブロックをタッチしたか判定するメソッド
	public int JudgeTouchGenreBlock(float x, float y){
		//TODO引き数のx座標、y座標がrectのどれかの中に入っていたらそのレクトに対応した番号を返す
		if(25<x && x<genreImage[0].getWidth()+25 && 25<y && y<genreImage[0].getHeight()+25)
			return 1;//「うごき」
		else if(25<x && x<genreImage[1].getWidth()+25 && 225<y && y<genreImage[1].getHeight()+225)
			return 2;//「じょうけん」
		else if(25<x && x<genreImage[2].getWidth()+25 && 425<y && y<genreImage[2].getHeight()+425)
			return 3;//「くりかえし」
		return 0;
	}

}
