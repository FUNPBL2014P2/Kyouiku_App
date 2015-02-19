package com.example.ev3controller;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class EV3Interpreter{
	private List<ProgramBlock> program;
	
	private int ifflag;//ifが真の時elseがあったらendに飛ぶフラグを管理
	
	private int ERROR_CODE = 0;//バグ発見用
	
	private EV3Materials ev3mt;
	
	private ArrayList<Integer> countlist = new ArrayList<Integer>();
	private boolean loop_now;
	
	public void EV3Interpreter(){
	}
	
	public void interprete(List<ProgramBlock> source){
		initialize();
		setCode(source);
		if(ev3mt != null){
			interprete_run();
		}
		destroy();
	}
	
	public void initialize(){
		program = new ArrayList<ProgramBlock>();
		if(ev3mt.getThreadstatus() == 0){
			ev3mt.ev3.threadstart();
			ev3mt.setThreadstatus(1);
		}
		
		loop_now=false;
	}
	
	public void setEV3material(Activity activity){
		ev3mt = (EV3Materials)activity.getApplication();
	}
	
	public void setCode(List<ProgramBlock> source){
		ProgramBlock cur = null;
		
		for(int i=0; i<source.size(); i++){
			if(source.get(i).getBlockType() == EV3ProgramCommand.START){
				cur = source.get(i);
				break;
			}
		}
		
		while(cur != null){
			program.add(cur);
			cur = cur.getNextBlock();
		}
	}
	
	public void destroy(){
		ev3mt.ev3.threadstop();
		program = null;
		ev3mt.ev3.stop();
		ev3mt = null;
		
	}
	
	public void interprete_run(){
		ProgramBlock cur = program.get(0);
		int count = 0;
		while(cur != null && ERROR_CODE == 0){
			System.out.println(cur.getBlockType());
			switch(cur.getBlockType()){
			case EV3ProgramCommand.START:
				cur = cur.getNextBlock();
				break;
			case EV3ProgramCommand.FF:
				ev3SecondMoeve(100, 100);
				cur = cur.getNextBlock();
				break;
			case EV3ProgramCommand.FLF:
				ev3SecondMoeve(50, 100);
				cur = cur.getNextBlock();
				break;
			case EV3ProgramCommand.FB:
				ev3SecondMoeve(-100, 100);
				cur = cur.getNextBlock();
				break;
			case EV3ProgramCommand.BLB:
				ev3SecondMoeve(-50, -100);
				cur = cur.getNextBlock();
				break;
			case EV3ProgramCommand.BB:
				ev3SecondMoeve(-100, -100);
				cur = cur.getNextBlock();
				break;
			case EV3ProgramCommand.LBB:
				ev3SecondMoeve(-100, -50);
				cur = cur.getNextBlock();
				break;
			case EV3ProgramCommand.BF:
				ev3SecondMoeve(100, -100);
				cur = cur.getNextBlock();
				break;
			case EV3ProgramCommand.LFF:
				ev3SecondMoeve(100, 50);
				cur = cur.getNextBlock();
				break;
			case EV3ProgramCommand.IRSWT:
				if(ev3mt.ev3.getRightTouchSensor() == true){
					ifflag = 1;
					cur = cur.getNextBlock();
				}else{
					cur = if_go_end(cur);
				}
				break;
			case EV3ProgramCommand.ILSWT:
				if(ev3mt.ev3.getLeftTouchSensor() == true){
					ifflag = 1;
					cur = cur.getNextBlock();
				}else{
					cur = if_go_end(cur);
				}
				break;
			case EV3ProgramCommand.IBSWT:
				if(ev3mt.ev3.getRightTouchSensor() == true
				&& ev3mt.ev3.getLeftTouchSensor() == true){
					ifflag = 1;
					cur = cur.getNextBlock();
				}else{
					cur = if_go_end(cur);
				}
				break;
			case EV3ProgramCommand.ELSE:
				if(ifflag == 1) cur = if_go_end(cur);
				else cur = cur.getNextBlock();
				break;
			case EV3ProgramCommand.IEND:
				ifflag = 0;
				cur = cur.getNextBlock();
				break;
			case EV3ProgramCommand.URSWT:
				if(ev3mt.ev3.getLeftTouchSensor() == false)
					cur = cur.getNextBlock();
				else{
					cur = until_go_end(cur);
				}
				break;
			case EV3ProgramCommand.ULSWT:
				if(ev3mt.ev3.getRightTouchSensor() == false)
					cur = cur.getNextBlock();
				else{
					cur = until_go_end(cur);
				}
				break;
			case EV3ProgramCommand.UBSWT:
				if(ev3mt.ev3.getRightTouchSensor() == false || ev3mt.ev3.getLeftTouchSensor() == false)
					cur = cur.getNextBlock();
				else{
					cur = until_go_end(cur);
				}
				break;
			case EV3ProgramCommand.FEND:
			case EV3ProgramCommand.UEND:
				count = 0;
				cur = cur.getPrevBlock();
				while(!(((EV3ProgramCommand.URSWT <= cur.getBlockType() && cur.getBlockType() <= EV3ProgramCommand.UBSWT)
						|| (EV3ProgramCommand.FMIN <= cur.getBlockType() && cur.getBlockType() <= EV3ProgramCommand.FMAX))
						&& count == 0)){
					if((EV3ProgramCommand.URSWT <= cur.getBlockType() && cur.getBlockType() <= EV3ProgramCommand.UBSWT)
							|| (EV3ProgramCommand.FMIN <= cur.getBlockType() && cur.getBlockType() <= EV3ProgramCommand.FMAX))
						count--;
					else if(cur.getBlockType() == EV3ProgramCommand.UEND || cur.getBlockType() == EV3ProgramCommand.FEND)
						count++;
					cur = cur.getPrevBlock();
				}
				loop_now = true;
				break;
			default:
				if(EV3ProgramCommand.FMIN <= cur.getBlockType() 
				&& cur.getBlockType() <= EV3ProgramCommand.FMAX){
					if(loop_now == true){
						countlist.set(countlist.size()-1, countlist.get(countlist.size()-1)-1);
						loop_now = false;
					}else{
						countlist.add(cur.getBlockType()-100);
					}
					
					if(countlist.get(countlist.size()-1) != 0)
						cur = cur.getNextBlock();
					else{
						count = 0;
						countlist.remove(countlist.size()-1);
						cur = cur.getNextBlock();
						while(!((cur.getBlockType() == EV3ProgramCommand.UEND || cur.getBlockType() == EV3ProgramCommand.FEND) && count == 0)){
							if((EV3ProgramCommand.URSWT <= cur.getBlockType() && cur.getBlockType() <= EV3ProgramCommand.UBSWT)
									|| (EV3ProgramCommand.FMIN <= cur.getBlockType() && cur.getBlockType() <= EV3ProgramCommand.FMAX))
								count++;
							else if(cur.getBlockType() == EV3ProgramCommand.UEND || cur.getBlockType() == EV3ProgramCommand.FEND)
								count--;
							cur = cur.getNextBlock();
						}
						cur = cur.getNextBlock();
					}
				}
				break;
			}
		}
		ev3SecondMoeve(0, 0);//モーターの停止
	}
	
	public void ev3SecondMoeve(int right, int left){
		long startTime = System.currentTimeMillis();
		ev3mt.ev3.move(right, left);
		while(startTime + 1000 > System.currentTimeMillis()){}
		ev3mt.ev3.stop();
	}
	
	public ProgramBlock if_go_end(ProgramBlock cur){
		int count = 0;
		cur = cur.getNextBlock();
		while(!((cur.getBlockType() == EV3ProgramCommand.ELSE || cur.getBlockType() == EV3ProgramCommand.IEND)
				&& count == 0)){
			if(EV3ProgramCommand.IRSWT <= cur.getBlockType() && cur.getBlockType() <= EV3ProgramCommand.IBSWT)
				count++;
			else if(cur.getBlockType() == EV3ProgramCommand.ELSE || cur.getBlockType() == EV3ProgramCommand.IEND)
				count--;
			cur = cur.getNextBlock();
		}
		return cur;
	}
	
	public ProgramBlock until_go_end(ProgramBlock cur){
		int count = 0;
		cur = cur.getNextBlock();
		while(!((cur.getBlockType() == EV3ProgramCommand.UEND || cur.getBlockType() == EV3ProgramCommand.FEND) && count == 0)){
			if((EV3ProgramCommand.URSWT <= cur.getBlockType() && cur.getBlockType() <= EV3ProgramCommand.UBSWT)
					|| (EV3ProgramCommand.FMIN <= cur.getBlockType() && cur.getBlockType() <= EV3ProgramCommand.FMAX))
				count++;
			else if(cur.getBlockType() == EV3ProgramCommand.UEND)
				count--;
			cur = cur.getNextBlock();
		}
		cur = cur.getNextBlock();
		loop_now = false;
		return cur;
	}
}
