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

import com.example.ev3controller.EV3ProgramCommand;

public class ProgrammingView extends View{
	Bitmap genreImage[] = new Bitmap[3];//ブロックイメージの読み込み
	int genreLineX;
	
	int InstanceBlocksNum = 19;//インスタンスブロックの数
	Bitmap instanceImage[] = new Bitmap[InstanceBlocksNum];
	Block insBlock[] = new Block[InstanceBlocksNum];
	int insHead,insRange;
	int instanceLineX;
	
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
		genreImage[0] = BitmapFactory.decodeResource(r, R.drawable.movebutton);
		genreImage[1] = BitmapFactory.decodeResource(r, R.drawable.ifbutton);
		genreImage[2] = BitmapFactory.decodeResource(r, R.drawable.forbutton);

		//スタートブロックの画像の設定
		r = context.getResources();
		startImage = BitmapFactory.decodeResource(r, R.drawable.start);

		startImage = Bitmap.createScaledBitmap(startImage, (int)(startImage.getWidth()*BLOCK_SCALE), (int)(startImage.getHeight()*BLOCK_SCALE), false);

		maxHeight=0;
		
		insHead=0;
		insRange=0;
		
		//インスタンスブロックの初期化
		setInctanceBlock();
	}

	@Override
	protected void onDraw(Canvas canvas){
		//インスタンスブロック領域線
		Paint paint = new Paint();

		paint.setAntiAlias(true);
		paint.setStrokeWidth(3.0f);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);

		float y = dispSize.y;

		//ジャンルエリアとインスタンスエリアの境界線
		canvas.drawLine(genreLineX, 0, genreLineX, y, paint);

		//インスタンスエリアとワークスペースの境界線
		canvas.drawLine(instanceLineX, 0, instanceLineX, y, paint);

		//ジャンルボタン
		for(int i=0; i<3; i++){
			canvas.drawBitmap(genreImage[i], 25, 25+200*i, null);
		}

		//スタートボタン
		int startx;
		if(insRange==0) startx= genreLineX;
		else startx = instanceLineX;
		canvas.drawBitmap(startImage, startx+30, 25, null);

		//インスタンスブロック
		if(insRange != 0){
			for(int i=insHead; i<insHead+insRange; i++){
				canvas.drawBitmap(instanceImage[i], insBlock[i].getPosition().x, insBlock[i].getPosition().y, paint);
			}
		}
	}

	//タッチイベント
	@Override
	public boolean onTouchEvent(MotionEvent event){
		int action = event.getAction();
		int blocktype;
		switch(action & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			if(event.getX() <= genreLineX){//もしジャンルエリアをタッチされたら
				int genre = JudgeTouchGenreBlock(event.getX(), event.getY());
				if(genre==1){//「うごき」
					instanceFlag = true;
					insHead=0;
					insRange=8;
				}
				else if(genre==2){//「じょうけん」
					instanceFlag = true;
					insHead=8;
					insRange=5;
				}
				else if(genre==3){//「くりかえし」
					instanceFlag = true;
					insHead=13;
					insRange=6;
				}
				else{//その他の白い部分
					instanceFlag=false;
					insRange=0;
				}
				instanceLineX = getMaxInstanceBlockWidth();
			}else if(genreLineX < event.getX() && event.getX() < instanceLineX){
				int blockType = JudgeTouchInstanceBlock(event);
				if(blockType!=-1){
					
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
			//ジャンルエリアの境界線のX座標
			genreLineX = dispSize.x / 5;
			//インスタンスブロックの初期化
			setInctanceBlock();

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
	
	public int JudgeTouchInstanceBlock(MotionEvent event){
		for(int i=insHead; i<insHead+insRange; i++){
			if(insBlock[i].isTouch(event) == true){
				if(insBlock[i].getBlockType()==100) return EV3ProgramCommand.FMIN;//for文用
				return i;
			}
		}
		return -1;
	}

	public void setInctanceBlock(){
		Resources r = getResources();
		int i;
		
		//動き 0~7
		instanceImage[0] = BitmapFactory.decodeResource(r, R.drawable.go0block);
		instanceImage[1] = BitmapFactory.decodeResource(r, R.drawable.go1block);
		instanceImage[2] = BitmapFactory.decodeResource(r, R.drawable.go2block);
		instanceImage[3] = BitmapFactory.decodeResource(r, R.drawable.go3block);
		instanceImage[4] = BitmapFactory.decodeResource(r, R.drawable.go4block);
		instanceImage[5] = BitmapFactory.decodeResource(r, R.drawable.go5block);
		instanceImage[6] = BitmapFactory.decodeResource(r, R.drawable.go6block);
		instanceImage[7] = BitmapFactory.decodeResource(r, R.drawable.go7block);
		
		//条件 8~12
		instanceImage[8] = BitmapFactory.decodeResource(r, R.drawable.ibswt);
		instanceImage[9] = BitmapFactory.decodeResource(r, R.drawable.irswt);
		instanceImage[10] = BitmapFactory.decodeResource(r, R.drawable.ilswt);
		instanceImage[11] = BitmapFactory.decodeResource(r, R.drawable.ilswt);//TODO あとで修正
		instanceImage[12] = BitmapFactory.decodeResource(r, R.drawable.ilswt);//TODO あとで修正
				
		//繰り返し(for) 13,14
		instanceImage[13] = BitmapFactory.decodeResource(r, R.drawable.ilswt);//TODO あとで修正
		instanceImage[14] = BitmapFactory.decodeResource(r, R.drawable.ilswt);//TODO あとで修正
		
		//繰り返し(until) 15~18
		instanceImage[15] = BitmapFactory.decodeResource(r, R.drawable.ubswt);
		instanceImage[16] = BitmapFactory.decodeResource(r, R.drawable.urswt);
		instanceImage[17] = BitmapFactory.decodeResource(r, R.drawable.ulswt);
		instanceImage[18] = BitmapFactory.decodeResource(r, R.drawable.ulswt);//TODO あとで修正
		
		//画像の縮小
		for(i=0; i<InstanceBlocksNum; i++){
			instanceImage[i] = Bitmap.createScaledBitmap(instanceImage[i], (int)(instanceImage[i].getWidth()*BLOCK_SCALE), (int)(instanceImage[i].getHeight()*BLOCK_SCALE), false);
		}
		
		//動き 0~7
		i=0;
		insBlock[0] = new Block(EV3ProgramCommand.FF, genreLineX+25, 25+200*i++, instanceImage[0]);
		insBlock[1] = new Block(EV3ProgramCommand.FLF, genreLineX+25, 25+200*i++, instanceImage[1]);
		insBlock[2] = new Block(EV3ProgramCommand.FB, genreLineX+25, 25+200*i++, instanceImage[2]);
		insBlock[3] = new Block(EV3ProgramCommand.BLB, genreLineX+25, 25+200*i++, instanceImage[3]);
		insBlock[4] = new Block(EV3ProgramCommand.BB, genreLineX+25, 25+200*i++, instanceImage[4]);
		insBlock[5] = new Block(EV3ProgramCommand.LBB, genreLineX+25, 25+200*i++, instanceImage[5]);
		insBlock[6] = new Block(EV3ProgramCommand.BF, genreLineX+25, 25+200*i++, instanceImage[6]);
		insBlock[7] = new Block(EV3ProgramCommand.LFF, genreLineX+25, 25+200*i++, instanceImage[7]);
		
		//条件 8~12
		i=0;
		insBlock[8] = new Block(EV3ProgramCommand.IBSWT, genreLineX+25, 25+200*i++, instanceImage[8]);
		insBlock[9] = new Block(EV3ProgramCommand.IRSWT, genreLineX+25, 25+200*i++, instanceImage[9]);
		insBlock[10] = new Block(EV3ProgramCommand.ILSWT, genreLineX+25, 25+200*i++, instanceImage[10]);
		insBlock[11] = new Block(EV3ProgramCommand.ELSE, genreLineX+25, 25+200*i++, instanceImage[11]);
		insBlock[12] = new Block(EV3ProgramCommand.IEND, genreLineX+25, 25+200*i++, instanceImage[12]);
		
		//繰り返し(for) 13,14
		i=0;
		insBlock[13] = new Block(EV3ProgramCommand.FBASE, genreLineX+25, 25+200*i++, instanceImage[13]);
		insBlock[14] = new Block(EV3ProgramCommand.FEND, genreLineX+25, 25+200*i++, instanceImage[14]);
		
		//繰り返し(until) 15~18
		insBlock[15] = new Block(EV3ProgramCommand.UBSWT, genreLineX+25, 25+200*i++, instanceImage[15]);
		insBlock[16] = new Block(EV3ProgramCommand.URSWT, genreLineX+25, 25+200*i++, instanceImage[16]);
		insBlock[17] = new Block(EV3ProgramCommand.ULSWT, genreLineX+25, 25+200*i++, instanceImage[17]);
		insBlock[18] = new Block(EV3ProgramCommand.UEND, genreLineX+25, 25+200*i++, instanceImage[18]);
	}
	
	public int getMaxInstanceBlockWidth(){
		int maxWidth;
		
		if(insRange == 0) return 0;
		else maxWidth = insBlock[insHead].getWidth();
		
		for(int i=insHead; i<insHead+insRange; i++){
			if(maxWidth < insBlock[i].getWidth()) maxWidth = insBlock[i].getWidth();
		}
		return genreLineX + maxWidth + 60;
	}
}
