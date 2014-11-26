package com.example.ev3controller;

public class EV3ProgramCommand {
	
	//コマンドごとの値
	public static int START = 0;
	
	public static int FF = 1;
	public static int FLF = 2;
	public static int FB = 3;
	public static int BLB = 4;
	public static int BB = 5;
	public static int LBB = 6;
	public static int BF = 7;
	public static int LFF = 8;
	
	public static int ELSE = 10;
	public static int IRSWT = 11;
	public static int ILSWT = 12;
	public static int IBSWT = 13;
	public static int IEND = 19;
	
	public static int FBASE = 100;
	public static int FEND = 101;
	public static int FMIN = 102;
	public static int FMAX = 199;
	
	public static int URSWT = 21;
	public static int ULSWT = 22;
	public static int UBSWT = 23;
	public static int UEND = 29;
	
	public static int END = 200;
	
	//コマンド事の値の範囲
	
	public static int MOVE_RANGE_MIN = 1;
	public static int MOVE_RANGE_MAX = 8;
	
	public static int IF_RANGE_MIN = 10;
	public static int IF_RANGE_MAX = 19;
	
	public static int UNTIL_RANGE_MIN = 20;
	public static int UNTIL_RANGE_MAX = 29;
	
	//コマンドごとのイメージの番号
	
	public static int START_IMAGE_NUM = 0;
	public static int MOEV_IMAGE_NUM = 1;
	public static int IF_IMAGE_NUM = 2;
	public static int FOR_IMAGE_NUM = 3;
	public static int UNTIL_IMAGE_NUM = 4;
	public static int END_IMAGE_NUM = 5;
	
	//コマンドの種類
	public static int COMMAND_TYPE_NUM = 6;
	
	//コマンドに対応するイメージの番号を返す
	public static int getImageNum(int type){
		if(type == START) return START_IMAGE_NUM;
		else if(MOVE_RANGE_MIN <= type && type <= MOVE_RANGE_MAX) return MOEV_IMAGE_NUM;
		else if(IF_RANGE_MIN <= type && type <= IF_RANGE_MAX) return IF_IMAGE_NUM;
		else if(FMIN <= type && type <= FMAX) return FOR_IMAGE_NUM;
		else if(UNTIL_RANGE_MIN <= type && type <= UNTIL_RANGE_MAX) return UNTIL_IMAGE_NUM;
		else if(type == END) return END_IMAGE_NUM;
		else return -1;
	}
}
