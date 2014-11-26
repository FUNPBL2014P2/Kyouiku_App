package com.example.ev3controller;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.MotionEvent;

public class Block {
	protected int blockType;//ブロックの種類
	protected Point point = new Point();//画像左上の座標
	protected int height;//ブロックの高さ
	protected int width;//ブロックの幅

	//コンストラクタ
	public Block(int type, int x, int y, int Width, int Height){
		blockType = type;
		point.x = x;
		point.y = y;
		width = Width;
		height = Height;
	}

	//コンストラクタ
	public Block(int type, Point position, Bitmap image){
		this(type, position.x, position.y, image.getWidth(), image.getHeight());
	}
	
	//コンストラクタ
	public Block(int type, int x, int y, Bitmap image){
		this(type, x, y, image.getWidth(), image.getHeight());
	}
	
	//コンストラクタ
	public Block(int type, Point position, int width, int height){
		this(type, position.x, position.y, width, height);
	}
	
	//コンストラクタ(サブ)
	public Block(Block block){
		this(block.getBlockType(), block.getPosition().x, block.getPosition().y, block.getHeight(), block.getWidth());
	}

	//ブロックの座標を設定するメソッド
	public void setPosition(Point position){
		point = position;
	}

	//ブロックの座標を得るメソッド
	public Point getPosition(){
		return point;
	}

	//ブロックの種類を得るメソッド
	public int getBlockType(){
		return blockType;
	}
	
	//他のブロックでも使う
	//ブロックの高さを得るメソッド
	public int getHeight(){
		return height;
	}

	//ブロックの幅を得るメソッド
	public int getWidth(){
		return width;
	}

	//ブロックがタッチされたかを判定するメソッド(flaot, float)
	public boolean isTouch(float x, float y){
		if(point.x <= x && x <= point.x + width &&
				point.y <= y && y <= point.y + height){
			return true;
		}else{
			return false;
		}
	}

	//ブロックがタッチされたかを判定するメソッド(Events)
	public boolean isTouch(MotionEvent event){
		return isTouch(event.getX(), event.getY());
	}
}