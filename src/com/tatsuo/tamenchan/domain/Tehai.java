package com.tatsuo.tamenchan.domain;

public class Tehai {
	int[] hai = new int[10];
	private boolean[] used = new boolean[36];

	public Tehai(){
		
	}
		
	public void haipai(){
		shihai();
		int num;
		for(int i=0;i<13;i++){
			do {
				num = (int)(Math.random()*36);
			} while (used[num]==true);
			used[num] = true;
			hai[(num/4)+1]++;
		}
	}
	
	public void shihai(){
		for(int i=0;i<35;i++){
			used[i] = false;
		}
		for(int i=0;i<10;i++){
			hai[i] = 0;
		}
	}
	
	public void printTehai(){
		System.out.println("手牌 : "+toString());
	}
	
	public String toString(){
		String str = "";
		for(int i=1;i<=9;i++){
			for(int j=0;j<hai[i];j++){
				str += i;
			}			
		}	
		return str;		
	}
	
	public int[] getTehai(){
		return hai;		
	}
	
	public void setTehai(int[] setHai){
		for(int i=0;i<10;i++){
			hai[i] = setHai[i];
			for(int j=0;j<hai[i];j++){
//				System.out.print((i-1)*4+j+" ");
				used[(i-1)*4+j] = true;
			}
//			System.out.println("");
		}		
	}	
	
	public Tehai copyTehai(){
		Tehai tehai = new Tehai();
		tehai.setTehai(hai);
		return tehai;
	}

}
