package com.example.ev3controller;

public class EV3ProgramCommand {
	
	//コマンドごとの値
	public static final int START = 0;
	public static final int FF = 1;
	public static final int FLF = 2;
	public static final int FB = 3;
	public static final int BLB = 4;
	public static final int BB = 5;
	public static final int LBB = 6;
	public static final int BF = 7;
	public static final int LFF = 8;
	
	public static final int ELSE = 10;
	public static final int IRSWT = 11;
	public static final int ILSWT = 12;
	public static final int IBSWT = 13;
	public static final int IEND = 19;
	
	public static final int FBASE = 100;
	public static final int FEND = 101;
	public static final int FMIN = 102;
	public static final int FMAX = 199;
	
	public static final int URSWT = 21;
	public static final int ULSWT = 22;
	public static final int UBSWT = 23;
	public static final int UEND = 29;
	
	//コマンド事の値の範囲
	
	public static final int MOVE_RANGE_MIN = 1;
	public static final int MOVE_RANGE_MAX = 8;
	
	public static final int IF_RANGE_MIN = 10;
	public static final int IF_RANGE_MAX = 19;
	
	public static final int UNTIL_RANGE_MIN = 20;
	public static final int UNTIL_RANGE_MAX = 29;
	
	//コマンドごとのイメージの番号
	
	public static final int START_IMAGE_NUM = 0;
	public static final int MOEV_IMAGE_NUM = 1;
	public static final int IF_IMAGE_NUM = 2;
	public static final int FOR_IMAGE_NUM = 3;
	public static final int UNTIL_IMAGE_NUM = 4;
	public static final int END_IMAGE_NUM = 5;
	
	//コマンドの種類
	public static final int COMMAND_TYPE_NUM = 6;
	
	//コマンドに対応するイメージの番号を返す
	public static final int getImageNum(final int type){
		if(type == START) return START_IMAGE_NUM;
		else if(MOVE_RANGE_MIN <= type && type <= MOVE_RANGE_MAX) return MOEV_IMAGE_NUM;
		else if(IF_RANGE_MIN <= type && type <= IF_RANGE_MAX) return IF_IMAGE_NUM;
		else if(FMIN <= type && type <= FMAX) return FOR_IMAGE_NUM;
		else if(UNTIL_RANGE_MIN <= type && type <= UNTIL_RANGE_MAX) return UNTIL_IMAGE_NUM;
		else return -1;
	}
}
