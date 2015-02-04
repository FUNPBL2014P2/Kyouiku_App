package com.example.ev3controller;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.MotionEvent;

public class ProgramBlock extends Block{
	private ProgramBlock next, prev;//接続されている前と後ろのブロック
	private int indent;
	
	public ProgramBlock(int type, int x, int y, int Width, int Height) {
		super(type, x, y, Width, Height);
		next = null;
		prev = null;
		indent = 0;
	}
	
	//コンストラクタ
	public ProgramBlock(int type, Point position, Bitmap image){
		this(type, position.x, position.y, image.getWidth(), image.getHeight());
	}
	
	//コンストラクタ
	public ProgramBlock(int type, int x, int y, Bitmap image){
		this(type, x, y, image.getWidth(), image.getHeight());
	}
	
	//コンストラクタ
	public ProgramBlock(int type, Point position, int width, int height){
		this(type, position.x, position.y, width, height);
	}
	
	//コンストラクタ(サブ)
	public ProgramBlock(Block block){
		this(block.getBlockType(), block.getPosition().x, block.getPosition().y, block.getHeight(), block.getWidth());
	}

	//ブロックタイプを設定するメソッド
	public void setBlockType(int value) {
		blockType = value;
	}

	//繋がっている次のプログラミングブロックを設定する
	public void setNextBlock(ProgramBlock nextBlock){
		next = nextBlock;
	}

	//繋がっている前のプログラミングブロックを設定する
	public void setPrevBlock(ProgramBlock prevBlock){
		prev = prevBlock;
	}

	//繋がっている次のプログラミングブロックを取得する
	public ProgramBlock getNextBlock(){
		return next;
	}

	//繋がっている前のプログラミングブロックを取得する
	public ProgramBlock getPrevBlock(){
		return prev;
	}
	
	//インデントレベルを設定する
	public void setIndentLevel(int level){
		indent = level;
	}
	
	//インデントレベルを取得する
	public int getIndentLevel(){
		return indent;
	}
	
	//ブロックがタッチされたかを判定するメソッド(flaot, float, flaot)
	public boolean isTouch(float x, float y, float indentW){
		if(point.x + indentW * indent <= x && x <= point.x + width  + indentW * indent &&
				point.y <= y && y <= point.y + height){
			return true;
		}else{
			return false;
		}
	}
	
	//ブロックがタッチされたかを判定するメソッド(Events)
	public boolean isTouch(MotionEvent event, float indentW){
		return isTouch(event.getX(), event.getY(), indentW);
	}
}