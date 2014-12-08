package com.example.ev3controller;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.ev3controller.EV3ProgramCommand;

public class ProgrammingView extends View
implements GestureDetector.OnGestureListener{
	Bitmap genreImage[] = new Bitmap[3];//ブロックイメージの読み込み

	int genreLineX;//ジャンルエリアとインスタンスエリアの境界線のX座標
	
	int InstanceBlocksNum = 20;//インスタンスブロックの数
	Bitmap blockImage[] = new Bitmap[InstanceBlocksNum];//各プログラムブロックのイメージを格納する変数
	Block insBlock[] = new Block[InstanceBlocksNum];//インスタンスブロックの管理する変数
	int insHead,insRange;//インスタンスエリアに表示するブロックのイメージのblockImage[]上の最初のインデックスと、そこから表示する数
	int instanceLineX;//インスタンスエリアの境界線と作業スペースののX座標
	
	boolean instanceFlag = false;//インスタンスエリアを表示しているのかを保持する

	private List<ProgramBlock> blockList = new ArrayList<ProgramBlock>();//生成したブロックを格納する変数
	private boolean touchProgramBlockFlag = false;//ブロックがタッチされている状態かを記憶する変数

	private Point dispSize = new Point();//線を描写するのに必要な変数

	private int maxHeight=0;//作業スペースの高さの最大
	private int minHeight=0;//作業スペースの高さの最小
	private int nowHeight=0;//現在の作業スペース
	private int maxInstanceHeight=0;//インスタンスエリアの高さの最大
	private int minInstanceHeight=0;//インスタンスエリアの高さの最小
	private int nowInstanceHeight=0;//現在のインスタンスエリア
	private int leftWorkX=0;//作業スペースの左端
	private int nowWorkX=0;//現在の作業スペースのX座標
	private int rightWorkX=0;
	private ProgrammingActivity activity;//親アクティビティを格納する変数

	private GestureDetector gestureDetector;//イベントからジェスチャーイベントを飛びたすクラス
	
	private static final double BLOCK_SCALE = 0.6;//元のブロックの画像の拡大・縮小比率
	
	private ProgramBlock startBlock;//スタートブロックを管理する変数
	
	private int indentWidth;//インデントを下げる表現をする時、どれくらいずらすかを保持する

	//コンストラクタ
	public ProgrammingView(Context context){
		super(context);

		//背景の色を設定
		//this.setBackgroundColor(Color.WHITE);

		//ジェスチャーディテクターの生成
		gestureDetector = new GestureDetector(context, this);

		//ジャンルブロックの画像を設定
		Resources r = context.getResources();
		genreImage[0] = BitmapFactory.decodeResource(r, R.drawable.movebutton);
		genreImage[1] = BitmapFactory.decodeResource(r, R.drawable.ifbutton);
		genreImage[2] = BitmapFactory.decodeResource(r, R.drawable.forbutton);

		maxHeight=0;
		maxInstanceHeight=0;

		insHead=0;
		insRange=0;

		//インスタンスブロックの初期化
		setInctanceBlock();

		//スタートブロックの追加
		blockList.add(new ProgramBlock(EV3ProgramCommand.START, 30, 25, blockImage[getBlockImageIndex(EV3ProgramCommand.START)]));
		startBlock = blockList.get(0);
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

		//作業スペースの上限、下限
		paint.setStrokeWidth(10.0f);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.RED);
		if(nowHeight == minHeight)
			canvas.drawLine(instanceLineX, 5, dispSize.x, 5, paint);
		else if(nowHeight == maxHeight){
			canvas.drawLine(instanceLineX, dispSize.y-165, dispSize.x, dispSize.y-165, paint);
		}


		//作業スペースの左端
		if(nowWorkX == leftWorkX)
			canvas.drawLine(instanceLineX, 0, instanceLineX, dispSize.y, paint);
		else if(nowWorkX == rightWorkX)
			canvas.drawLine(dispSize.x-5, 0, dispSize.x-5, dispSize.y, paint);

		//インスタンスエリアの上限、下限
		paint.setStrokeWidth(20.0f);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.GREEN);
		if(nowInstanceHeight == minInstanceHeight)
			canvas.drawLine(genreLineX, 0, instanceLineX, 0, paint);
		else if(nowInstanceHeight == maxInstanceHeight)
			canvas.drawLine(genreLineX, dispSize.y-165, instanceLineX, dispSize.y-165, paint);

		//ジャンルボタン
		for(int i=0; i<3; i++){
			canvas.drawBitmap(genreImage[i], 25, 25+200*i, null);
		}

		//インスタンスブロック
		if(insRange != 0){
			for(int i=insHead; i<insHead+insRange; i++){
				canvas.drawBitmap(blockImage[i], insBlock[i].getPosition().x, insBlock[i].getPosition().y, paint);
			}
		}

		//TODO プロブラムブロックを表示
		for(int i=0; i<blockList.size(); i++){
			drawBlock(canvas, blockList.get(i));
			if(EV3ProgramCommand.FMIN <= blockList.get(i).getBlockType() && blockList.get(i).getBlockType() <= EV3ProgramCommand.FMAX){
				paint.setTextSize(48);
				paint.setStyle(Paint.Style.FILL);
				paint.setColor(Color.BLACK);
				canvas.drawText(Integer.toString(blockList.get(i).getBlockType() - EV3ProgramCommand.FBASE),
						blockList.get(i).getPosition().x + blockList.get(i).getIndentLevel() * indentWidth + 100, blockList.get(i).getPosition().y + 85, paint);
			}
		}
	}

	//タッチイベント
	@Override
	public boolean onTouchEvent(MotionEvent event){
		int action = event.getAction();
		int blocktype;

		gestureDetector.onTouchEvent(event);
		switch(action & MotionEvent.ACTION_MASK){
		case MotionEvent.ACTION_DOWN:
			if(event.getX() <= genreLineX){//もしジャンルエリアをタッチされたら
				int genre = JudgeTouchGenreBlock(event.getX(), event.getY());
				if(genre==1){//「うごき」
					nowInstanceHeight=0;
					setInctanceBlock();
					instanceFlag = true;
					insHead=0;
					insRange=8;
				}
				else if(genre==2){//「じょうけん」
					nowInstanceHeight=0;
					setInctanceBlock();
					instanceFlag = true;
					insHead=8;
					insRange=5;
				}
				else if(genre==3){//「くりかえし」
					nowInstanceHeight=0;
					setInctanceBlock();
					instanceFlag = true;
					insHead=13;
					insRange=6;
				}
				else{//その他の白い部分
					instanceFlag=false;
					insRange=0;
				}
				int basePositionX = instanceLineX;
				instanceLineX = getMaxInstanceBlockWidth();
				for(int i=0; i<blockList.size(); i++){
					ProgramBlock block = blockList.get(i);
					Point position = new Point(block.getPosition().x + instanceLineX - basePositionX, block.getPosition().y);
					block.setPosition(position);
				}
			}else if(genreLineX < event.getX() && event.getX() < instanceLineX){//もしインスタンスブロックがタッチされたら
				int blockType = JudgeTouchInstanceBlock(event);
				if(blockType!=-1){
					blockList.add(new ProgramBlock(blockType, getTouchBlockCentor(event,blockType),
							blockImage[getBlockImageIndex(blockType)]));
					touchProgramBlockFlag = true;
					instanceFlag=false;
					insRange=0;
					int basePositionX = instanceLineX;
					instanceLineX = getMaxInstanceBlockWidth();
					for(int i=0; i<blockList.size()-1; i++){
						ProgramBlock block = blockList.get(i);
						Point position = new Point(block.getPosition().x + instanceLineX - basePositionX, block.getPosition().y);
						block.setPosition(position);
					}
				}
			}else if(judTouchProgramBlock(event) != -1){//もしプログラミングブロックをタッチされたら
				touchProgramBlockFlag = true;
				int blockType = blockList.get(blockList.size()-1).getBlockType();
				Point centor = getTouchBlockCentor(event, blockType);
				disconnectPrevBlock();
				blockList.get(blockList.size()-1).setPosition(new Point(centor.x,centor.y));
			}
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			if(touchProgramBlockFlag == true){//プログラミングブロックをドラックされたら
				int connectBlockNum = judAutoConnectBlock(event);
				if(connectBlockNum != -1){//もし他のブロックとの接続範囲に入ったら、接続する
					connectPrevBlock(connectBlockNum);
					Point prevBlockPosition = new Point(blockList.get(connectBlockNum).getPosition().x,
							blockList.get(connectBlockNum).getPosition().y + blockList.get(connectBlockNum).getHeight()-38);
					blockList.get(blockList.size()-1).setPosition(prevBlockPosition);
				}else{//違えば、ブロックはタッチされている位置になる
					disconnectPrevBlock();
					Point centorPosition = this.getTouchBlockCentor(event, blockList.get(blockList.size()-1).getBlockType());
					blockList.get(blockList.size()-1).setPosition(new Point(centorPosition.x,centorPosition.y));
				}
				//移動ブロックとつながっているブロックの座標の変更
				for(ProgramBlock block = blockList.get(blockList.size()-1).getNextBlock(); block != null; block = block.getNextBlock()){
					Point blocksize = getBlockSize(block.getPrevBlock().getBlockType());
					block.setPosition(new Point(block.getPrevBlock().getPosition().x, block.getPrevBlock().getPosition().y + blocksize.y-38));
				}
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			//TODO
			if(touchProgramBlockFlag == true){//プログラミングブロックがインスタンスエリアで離されたら、ブロックを消す
				for(int i=0; i<blockList.size(); i++){
					if(blockList.get(i).getPosition().x < leftWorkX){
						blockList.remove(i--);
					}
				}
			}
			touchProgramBlockFlag = false;
			invalidate();
			break;
		}
		return true;
	}

	//View生成時にViewの大きさを設定するためのメソッド
	public void setDisplaySize(int width, int height){
		if(maxHeight==0){
		//Viewの画面サイズの取得
		dispSize.x = width;
		dispSize.y = height;
		//ジャンルエリアの境界線のX座標
		genreLineX = dispSize.x / 5;
		//インスタンスエリアの境界線のX座標
		instanceLineX = dispSize.x / 5;
		//ワークスペースの高さの最大
		maxHeight = dispSize.y * 5;
		//インスタンスエリアの高さの最大
		maxInstanceHeight = dispSize.y * 2;
		//作業スペースの左端
		leftWorkX = instanceLineX;
		//現在の作業エリアのx座標
		nowWorkX  = instanceLineX;
		//作業スペースの右端
		rightWorkX = dispSize.x * 3;
		//インスタンスブロックの初期化
		setInctanceBlock();
		//スタートブロックの位置の初期化
		startBlock.setPosition(new Point(genreLineX + 60, 25));

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

	//どのインスタンスブロックをタッチしたか判定する
	public int JudgeTouchInstanceBlock(MotionEvent event){
		for(int i=insHead; i<insHead+insRange; i++){
			if(insBlock[i].isTouch(event) == true){
				if(insBlock[i].getBlockType()==100) return EV3ProgramCommand.FMIN;//for文用
				return insBlock[i].getBlockType();
			}
		}
		return -1;
	}

	//インスタンスブロックの初期化とブロックイメージの初期化を行う
	public void setInctanceBlock(){
		Resources r = getResources();
		int i;

		//動き 0~7
		blockImage[0] = BitmapFactory.decodeResource(r, R.drawable.go0block);
		blockImage[1] = BitmapFactory.decodeResource(r, R.drawable.go1block);
		blockImage[2] = BitmapFactory.decodeResource(r, R.drawable.go2block);
		blockImage[3] = BitmapFactory.decodeResource(r, R.drawable.go3block);
		blockImage[4] = BitmapFactory.decodeResource(r, R.drawable.go4block);
		blockImage[5] = BitmapFactory.decodeResource(r, R.drawable.go5block);
		blockImage[6] = BitmapFactory.decodeResource(r, R.drawable.go6block);
		blockImage[7] = BitmapFactory.decodeResource(r, R.drawable.go7block);

		//条件 8~12
		blockImage[8] = BitmapFactory.decodeResource(r, R.drawable.ibswt);
		blockImage[9] = BitmapFactory.decodeResource(r, R.drawable.irswt);
		blockImage[10] = BitmapFactory.decodeResource(r, R.drawable.ilswt);
		blockImage[11] = BitmapFactory.decodeResource(r, R.drawable.ielse);
		blockImage[12] = BitmapFactory.decodeResource(r, R.drawable.iend);
				
		//繰り返し(for) 13,14
		blockImage[13] = BitmapFactory.decodeResource(r, R.drawable.ffor);
		blockImage[14] = BitmapFactory.decodeResource(r, R.drawable.uend);
		
		//繰り返し(until) 15~18
		blockImage[15] = BitmapFactory.decodeResource(r, R.drawable.ubswt);
		blockImage[16] = BitmapFactory.decodeResource(r, R.drawable.urswt);
		blockImage[17] = BitmapFactory.decodeResource(r, R.drawable.ulswt);
		blockImage[18] = BitmapFactory.decodeResource(r, R.drawable.uend);
		
		//スタート 19
		blockImage[19] = BitmapFactory.decodeResource(r, R.drawable.start);

		//画像の縮小
		for(i=0; i<InstanceBlocksNum; i++){
			blockImage[i] = Bitmap.createScaledBitmap(blockImage[i], (int)(blockImage[i].getWidth()*BLOCK_SCALE), (int)(blockImage[i].getHeight()*BLOCK_SCALE), false);
		}
		
		indentWidth = blockImage[getBlockImageIndex(EV3ProgramCommand.FF)].getWidth() / 4
				- blockImage[getBlockImageIndex(EV3ProgramCommand.FF)].getWidth() / 20;

		//動き 0~7
		i=0;
		insBlock[0] = new Block(EV3ProgramCommand.FF, genreLineX+25, 25+200*i++, blockImage[0]);
		insBlock[1] = new Block(EV3ProgramCommand.FLF, genreLineX+25, 25+200*i++, blockImage[1]);
		insBlock[2] = new Block(EV3ProgramCommand.FB, genreLineX+25, 25+200*i++, blockImage[2]);
		insBlock[3] = new Block(EV3ProgramCommand.BLB, genreLineX+25, 25+200*i++, blockImage[3]);
		insBlock[4] = new Block(EV3ProgramCommand.BB, genreLineX+25, 25+200*i++, blockImage[4]);
		insBlock[5] = new Block(EV3ProgramCommand.LBB, genreLineX+25, 25+200*i++, blockImage[5]);
		insBlock[6] = new Block(EV3ProgramCommand.BF, genreLineX+25, 25+200*i++, blockImage[6]);
		insBlock[7] = new Block(EV3ProgramCommand.LFF, genreLineX+25, 25+200*i++, blockImage[7]);

		//条件 8~12
		i=0;
		insBlock[8] = new Block(EV3ProgramCommand.IBSWT, genreLineX+25, 25+200*i++, blockImage[8]);
		insBlock[9] = new Block(EV3ProgramCommand.IRSWT, genreLineX+25, 25+200*i++, blockImage[9]);
		insBlock[10] = new Block(EV3ProgramCommand.ILSWT, genreLineX+25, 25+200*i++, blockImage[10]);
		insBlock[11] = new Block(EV3ProgramCommand.ELSE, genreLineX+25, 25+200*i++, blockImage[11]);
		insBlock[12] = new Block(EV3ProgramCommand.IEND, genreLineX+25, 25+200*i++, blockImage[12]);

		//繰り返し(for) 13,14
		i=0;
		insBlock[13] = new Block(EV3ProgramCommand.FBASE, genreLineX+25, 25+200*i++, blockImage[13]);
		insBlock[14] = new Block(EV3ProgramCommand.FEND, genreLineX+25, 25+200*i++, blockImage[14]);

		//繰り返し(until) 15~18
		insBlock[15] = new Block(EV3ProgramCommand.UBSWT, genreLineX+25, 25+200*i++, blockImage[15]);
		insBlock[16] = new Block(EV3ProgramCommand.URSWT, genreLineX+25, 25+200*i++, blockImage[16]);
		insBlock[17] = new Block(EV3ProgramCommand.ULSWT, genreLineX+25, 25+200*i++, blockImage[17]);
		insBlock[18] = new Block(EV3ProgramCommand.UEND, genreLineX+25, 25+200*i++, blockImage[18]);
	}
	
	//インスタンスエリアに表示されるブロックのうち、最大の幅を持つブロックの横幅の値を返す
	public int getMaxInstanceBlockWidth(){
		int maxWidth;

		if(insRange == 0) return genreLineX;
		else maxWidth = insBlock[insHead].getWidth();

		for(int i=insHead; i<insHead+insRange; i++){
			if(maxWidth < insBlock[i].getWidth()) maxWidth = insBlock[i].getWidth();
		}
		return genreLineX + maxWidth + 130;
	}

	//ブロックタイプから対応するイメージのインデックスを取得する
	public int getBlockImageIndex(int blockType){
		int index = -1;
		switch(blockType){
		case EV3ProgramCommand.FF:
			index=0;
			break;
		case EV3ProgramCommand.FLF:
			index=1;
			break;
		case EV3ProgramCommand.FB:
			index=2;
			break;
		case EV3ProgramCommand.BLB:
			index=3;
			break;
		case EV3ProgramCommand.BB:
			index=4;
			break;
		case EV3ProgramCommand.LBB:
			index=5;
			break;
		case EV3ProgramCommand.BF:
			index=6;
			break;
		case EV3ProgramCommand.LFF:
			index=7;
			break;
		case EV3ProgramCommand.IBSWT:
			index=8;
			break;
		case EV3ProgramCommand.IRSWT:
			index=9;
			break;
		case EV3ProgramCommand.ILSWT:
			index=10;
			break;
		case EV3ProgramCommand.ELSE:
			index=11;
			break;
		case EV3ProgramCommand.IEND:
			index=12;
			break;
		case EV3ProgramCommand.FEND:
			index=14;
			break;
		case EV3ProgramCommand.UBSWT:
			index=15;
			break;
		case EV3ProgramCommand.URSWT:
			index=16;
			break;
		case EV3ProgramCommand.ULSWT:
			index=17;
			break;
		case EV3ProgramCommand.UEND:
			index=18;
			break;
		case EV3ProgramCommand.START:
			index=19;
			break;
		default:
			if(EV3ProgramCommand.FMIN <= blockType && blockType <= EV3ProgramCommand.FMAX){
				index=13;
			}
			break;
		}
		return index;
	}

	//タッチされたプログラムブロックの中心を求めるためのメソッド
	public Point getTouchBlockCentor(MotionEvent event, int blockType){
		Point blockSize = getBlockSize(blockType);
		int x = (int)event.getX() - (int)(blockSize.x / 2);
		int y = (int)event.getY() - (int)(blockSize.y / 2);
		return new Point(x, y);
	}

	//ブロックの表示寸法を得るためのメソッド
	public Point getBlockSize(int blockType){
		Bitmap img = blockImage[getBlockImageIndex(blockType)];
		return new Point(img.getWidth(), img.getHeight());
	}

	//プログラミングブロックの接続判定と関連付けを行うメソッド
	public int judAutoConnectBlock(MotionEvent event){
		for(int i=blockList.size()-2; i>=0; i--){
			if(blockList.get(i).getNextBlock() == null || blockList.get(i).getNextBlock() == blockList.get(blockList.size()-1)){
				if(blockList.get(i).getPosition().x - blockList.get(i).getHeight() / 2 <= event.getX() + blockList.get(i).getIndentLevel() * indentWidth
						&& event.getX() <= blockList.get(i).getPosition().x + blockList.get(i).getWidth() + blockList.get(i).getHeight() / 2 + blockList.get(i).getIndentLevel() * indentWidth
						&& blockList.get(i).getPosition().y + blockList.get(i).getHeight() / 2 <= event.getY()
						&& event.getY() <= blockList.get(i).getPosition().y + blockList.get(i).getHeight() + blockList.get(i).getHeight() / 2){
					return i;
				}
			}
		}
		return -1;
	}

	//タッチしているブロックの上にあるブロックと関係を繋げるメソッド
	public void connectPrevBlock(int i){
		blockList.get(i).setNextBlock(blockList.get(blockList.size()-1));
		blockList.get(blockList.size()-1).setPrevBlock(blockList.get(i));
		arrangeIndent(blockList.get(blockList.size()-1));
	}

	//タッチしているブロックと繋がっている前のブロックとの関係を無くすメソッド
	public void disconnectPrevBlock(){
		if(blockList.get(blockList.size()-1).getPrevBlock() != null)
			blockList.get(blockList.size()-1).getPrevBlock().setNextBlock(null);
		blockList.get(blockList.size()-1).setPrevBlock(null);
		blockList.get(blockList.size()-1).setIndentLevel(0);
		arrangeIndent(blockList.get(blockList.size()-1));
	}

	//どのプログラムブロックにタッチしたかを判定し順番をソートするメソッド
	public int judTouchProgramBlock(MotionEvent event){
		for(int i=blockList.size()-1; i>=0; i--){
			if(blockList.get(i).isTouch(event) && blockList.get(i).getBlockType() != EV3ProgramCommand.START){
				blockList.add(blockList.get(i));
				blockList.remove(i);
				return blockList.size()-1;
			}
		}
		return -1;
	}

	//プログラムブロックの表示の簡略化のためのメソッド
	public void drawBlock(Canvas canvas, ProgramBlock block){
		canvas.drawBitmap(blockImage[getBlockImageIndex(block.getBlockType())], block.getPosition().x + block.getIndentLevel() * indentWidth, block.getPosition().y, null);
	}

	//現在使用していない
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	//現在使用していない
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	//長押しした時
	@Override
	public void onLongPress(MotionEvent e) {
		//長押ししたのが繰り返し(For)ブロックだったら、ダイアログを表示する
		if(judTouchProgramBlock(e) != -1){
			if(EV3ProgramCommand.FMIN <= blockList.get(blockList.size()-1).getBlockType()
					&& blockList.get(blockList.size()-1).getBlockType() <= EV3ProgramCommand.FMAX){
				SetLoopTimesDialog dialog = new SetLoopTimesDialog();
				dialog.setForNum(blockList.get(blockList.size()-1));
				dialog.show(activity.getFragmentManager(), "span_setting_dialog");
			}
		}
	}

	//現在使用していない
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO 自動生成されたメソッド・スタブ
		if(touchProgramBlockFlag == false && e1.getX() > instanceLineX){//ワークスペースのスクロール処理
			if(nowHeight + (int)distanceY < 0){
				distanceY = distanceY - (nowHeight + distanceY + minHeight);
			}else if(maxHeight < nowHeight + (int)distanceY){
				distanceY = distanceY - (nowHeight + distanceY - maxHeight);
			}
			if(nowWorkX + (int)distanceX < leftWorkX){
				distanceX = distanceX - (nowWorkX + distanceX - leftWorkX);
			}else if(rightWorkX < nowWorkX + (int)distanceX){
				distanceX = distanceX - (nowWorkX + distanceX - rightWorkX);
			}
			nowHeight = nowHeight + (int)distanceY;
			nowWorkX = nowWorkX + (int)distanceX;
			for(int i=0; i<blockList.size(); i++){//縦スクロール
				blockList.get(i).setPosition(new Point(blockList.get(i).getPosition().x, blockList.get(i).getPosition().y - (int)distanceY));
			}
			for(int i=0; i<blockList.size(); i++){//横スクロール
				blockList.get(i).setPosition(new Point(blockList.get(i).getPosition().x - (int)distanceX, blockList.get(i).getPosition().y));
			}
		}
		else if(JudgeTouchInstanceBlock(e1) == -1 && e1.getX() < instanceLineX && e1.getX()>genreLineX){//インスタンスエリアのスクロール処理
			if(nowInstanceHeight + (int)distanceY < 0){
				distanceY = distanceY - (nowInstanceHeight + distanceY + minInstanceHeight);
			}else if(maxInstanceHeight < nowInstanceHeight + (int)distanceY){
				distanceY = distanceY - (nowInstanceHeight + distanceY - maxInstanceHeight);
			}
			nowInstanceHeight = nowInstanceHeight + (int)distanceY;
			for(int i=0; i<insHead+insRange; i++){//縦スクロール
				insBlock[i].setPosition(new Point(insBlock[i].getPosition().x, insBlock[i].getPosition().y - (int)distanceY));
			}
		}
		invalidate();
		return false;
	}

	//現在使用していない
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO 自動生成されたメソッド・スタブ

	}

	//現在使用していない
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	//Forブロックの繰り返す回数を設定する
	public void setForNum(Integer value) {
		blockList.get(blockList.size()-1).setBlockType(value.intValue() + EV3ProgramCommand.FBASE);
		invalidate();
	}
	
	//繰り返し(For)の値をダイアログから設定するときに必要
	public void setActivity(ProgrammingActivity mainactivity){
		activity = mainactivity;
	}
	
	//インデントの整理
	public void arrangeIndent(ProgramBlock Sblock){
		for(ProgramBlock block = Sblock; block != null; block = block.getNextBlock()){
			if(block.getPrevBlock() == null) continue;
			block.setIndentLevel(block.getPrevBlock().getIndentLevel()
				+ EV3ProgramCommand.getOutputIndentValue(block.getPrevBlock().getBlockType())
				+ EV3ProgramCommand.getInputIndentValue(block.getBlockType()));
		}
	}
}
