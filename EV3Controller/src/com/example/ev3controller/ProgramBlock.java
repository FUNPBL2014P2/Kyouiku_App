package com.example.ev3controller;

import android.graphics.Bitmap;
import android.graphics.Point;

public class ProgramBlock extends Block{
	private Block next, prev;//接続されている前と後ろのブロック
	
	public ProgramBlock(int type, int x, int y, int Width, int Height) {
		super(type, x, y, Width, Height);
		next = null;
		prev = null;
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
	public void setNextBlock(Block nextBlock){
		next = nextBlock;
	}

	//繋がっている前のプログラミングブロックを設定する
	public void setPrevBlock(Block prevBlock){
		prev = prevBlock;
	}

	//繋がっている次のプログラミングブロックを取得する
	public Block getNextBlock(){
		return next;
	}

	//繋がっている前のプログラミングブロックを取得する
	public Block getPrevBlock(){
		return prev;
	}
}