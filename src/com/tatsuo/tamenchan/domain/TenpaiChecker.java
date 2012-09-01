package com.tatsuo.tamenchan.domain;

import java.util.ArrayList;
import java.util.List;

public class TenpaiChecker {
	
	public static final int LOG_LEVEL_DEBUG = 1;
	public static final int LOG_LEVEL_INFO = 5;
	
	private int logLevel = LOG_LEVEL_INFO;	
	
	public boolean[] checkMachihai(Tehai tehai){
		return checkMachihai(tehai, LOG_LEVEL_INFO);
	}
	
	public boolean[] checkMachihai(Tehai tehai, int logLevel){
		this.logLevel = logLevel;
		boolean[] machi = new boolean[10];
		
		for(int i=1;i<=9;i++){
			log(i+"が待ち牌かをチェック");
			if(tehai.hai[i] < 4){
				Tehai checkTehai = tehai.copyTehai();
				checkTehai.hai[i]++;
				machi[i] = checkAgari(checkTehai);
			}
			log(i+"が待ち牌かのチェック結果 : "+machi[i]);
		}
		
		return machi;
	}
	
	public boolean checkAgari(Tehai tehai){
		boolean result = false;
		
		List<Integer> kotsuList = new ArrayList<Integer>();
		for(int i=1;i<=9;i++){
			if(tehai.hai[i]>=3){
				kotsuList.add(Integer.valueOf(i));
			}
		}
		
		log(" 刻子の数 : "+kotsuList.size());
		
		Tehai tempTehai;
		
		switch (kotsuList.size()) {
		case 0:
			if(checkToitsu(tehai) == true){result = true;}
			break;
		case 1:
			// 刻子無視
			if(checkToitsu(tehai) == true){result = true;}
			// 刻子１つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			break;
		case 2:
			// 刻子無視
			if(checkToitsu(tehai) == true){result = true;}
			// 刻子１つ（１つ目の刻子を取る）
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子１つ（２つ目の刻子を取る）			
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子２つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			break;
		case 3:
			// 刻子無視
			if(checkToitsu(tehai) == true){result = true;}
			// 刻子１つ（１つ目の刻子を取る）
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子１つ（２つ目の刻子を取る）			
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子１つ（３つ目の刻子を取る）			
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(2).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子２つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子２つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			tempTehai.hai[kotsuList.get(2).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子２つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			tempTehai.hai[kotsuList.get(2).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子３つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			tempTehai.hai[kotsuList.get(2).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			break;
		case 4:
			// 刻子無視
			if(checkToitsu(tehai) == true){result = true;}
			// 刻子１つ（１つ目の刻子を取る）
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子１つ（２つ目の刻子を取る）			
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子１つ（３つ目の刻子を取る）			
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(2).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子１つ（３つ目の刻子を取る）
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(3).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子２つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子２つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			tempTehai.hai[kotsuList.get(2).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子２つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			tempTehai.hai[kotsuList.get(3).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子２つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			tempTehai.hai[kotsuList.get(2).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子２つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			tempTehai.hai[kotsuList.get(3).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子２つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(2).intValue()]-=3;
			tempTehai.hai[kotsuList.get(3).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子３つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			tempTehai.hai[kotsuList.get(2).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子３つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			tempTehai.hai[kotsuList.get(3).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子３つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			tempTehai.hai[kotsuList.get(2).intValue()]-=3;
			tempTehai.hai[kotsuList.get(3).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子３つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			tempTehai.hai[kotsuList.get(2).intValue()]-=3;
			tempTehai.hai[kotsuList.get(3).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			// 刻子４つ
			tempTehai = tehai.copyTehai();
			tempTehai.hai[kotsuList.get(0).intValue()]-=3;
			tempTehai.hai[kotsuList.get(1).intValue()]-=3;
			tempTehai.hai[kotsuList.get(2).intValue()]-=3;
			tempTehai.hai[kotsuList.get(3).intValue()]-=3;
			if(checkToitsu(tempTehai) == true){result = true;}
			break;
		default:
			break;
		}
		
		return result;
		
	}
	
	public boolean checkToitsu(Tehai tehai){
		List<Integer> toitsuList = new ArrayList<Integer>();
		for(int i=1;i<=9;i++){
			if(tehai.hai[i]>=2){
				toitsuList.add(Integer.valueOf(i));
			}
		}
		
		log("  対子の数 : "+toitsuList.size());
		
		for(Integer toitsu : toitsuList){
			Tehai tempTehai = tehai.copyTehai();
			int num = toitsu.intValue();
			tempTehai.hai[num] -=2;

			log("   "+toitsu.intValue()+"を頭としたとき");

			boolean bool = checkShuntsu(tempTehai);
			if(bool == true){return true;}
		}
		
		return false;
	}
	
	public boolean checkShuntsu(Tehai tehai){
		log("    順子チェック : "+tehai.toString());
		logNNL("     ");
		for(int i=1;i<=7;i++){
			while(tehai.hai[i]>=1 && tehai.hai[i+1]>=1 && tehai.hai[i+2]>=1){
				tehai.hai[i]--;
				tehai.hai[i+1]--;
				tehai.hai[i+2]--;
				logNNL(i+""+(i+1)+""+(i+2)+" ");
			}
		}
		
		log("");		
		log("      残り牌 : "+tehai.toString());		

		boolean result = true;
		for(int i=1;i<=9;i++){
			if(tehai.hai[i]>=1){
				result = false;
			}			
		}

		log("   チェック結果 : "+result);	
		
		return result;
	}
	
	private void log(String str){
		logOutput(str, logLevel);
	}
	
	private void logNNL(String str){
		logOutputNoNewLine(str, logLevel);
	}
	
	private void logOutputNoNewLine(String str, int logLevel){
		if( logLevel == LOG_LEVEL_DEBUG ){
			System.out.print(str);
		}
	}
	
	private static void logOutput(String str, int logLevel){
		if( logLevel == LOG_LEVEL_DEBUG ){
			System.out.println(str);
		}
	}

}
