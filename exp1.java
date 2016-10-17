// package exp1;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator; 

//change this line to create a commit


//changed on B1
class Expression implements Cloneable  {
	public int coef = 1;
	public int[] powers;
	public Expression(int num){
		this.powers = new int[num];
	}
	@Override  
	protected Object clone(){  
	    Expression e = new Expression(powers.length);
	    e.coef = coef;
	    for(int i=0;i<powers.length;i++)
	    	e.powers[i] = powers[i];
	    return e;
	}

}

public class exp1 {
	
	public static HashSet<Character> set = new HashSet<Character>();
	
	public static ArrayList<Expression> exprlist = new ArrayList<Expression>(); //存放各子式
	
	public static void main(String []args){
		Scanner scan = new Scanner(System.in); 
		do{
			System.out.print("$ ");
	        if(scan.hasNextLine()){   
	        	String line = scan.nextLine();
	        	
	        	if(line.equals("")) continue;
	        	if(line.equals("over")) break;
	        	if(line.charAt(0)!='!') line = line.replace(" ","");
	        	if(judge(line)) {
	        		analysis(line);
	        		print(merge(exprlist));
	        	}
	        }  
		}while(true);
		scan.close();
	}
	
	public static void analysis (String line){
		set.clear(); //清空set
		exprlist.clear(); //清空
		for(int i = 0;i<line.length();i++){
			if(Character.isLetter(line.charAt(i)) ){
				set.add(line.charAt(i));
			}
		}
		ArrayList<Character> tempList = new ArrayList<Character>(set);
		String[] linelist = line.split("[+-]"); // 用+号划分表达式
		int[] flag = new int[linelist.length+1];
		flag[0] = 1;
		int i=0,j=0;
		while(i<line.length()){
			if(line.charAt(i) == '+') flag[++j] = 1;
			if(line.charAt(i) == '-') flag[++j] = 0;
			i++;
		}
		i=0;
		for(String s : linelist){ 
			//遍历子式
			String[] item = s.split("\\*"); //
			Expression expr = new Expression(set.size());
			for(String x : item){
				if(x.matches("[a-z]")) expr.powers[tempList.indexOf(x.charAt(0))]++;
				else {
					expr.coef = expr.coef * Integer.parseInt(x) ;
				}
			}
			if(flag[i]==0) expr.coef = expr.coef*(-1);
				i++;
			exprlist.add(expr);
		}
	}
	
	public static void print(ArrayList<Expression> exprlist){
		String res = "";
		for (Expression s : exprlist)
		{
			Iterator<Character> it = set.iterator();
			if(res.length() == 0 || s.coef>0)
				res = res + s.coef;
			else
				res = res.substring(0,res.length()-1) + s.coef;
			//res = res + (s.coef>0?s.coef:("-"+(0-s.coef)));
			for(int i : s.powers)
			{
				char var = it.next();
				if(i != 0){
					res = res + "*"+var+"^"+i;
				}
			}
			res = res + "+";
		}
		System.out.println(res.substring(0,res.length()-1));
	}
	
	public static ArrayList<Expression> merge(ArrayList<Expression> exprlist){
		ArrayList<Expression> newExprlist = new ArrayList<Expression>();
		for (Expression s : exprlist){
			boolean flag = true;
			for (Expression k : newExprlist){
				if(Arrays.equals(s.powers,k.powers)){
					flag = false;
					k.coef += s.coef;
				}
			}
			if(flag){
				newExprlist.add(s);
			}
		}
		return newExprlist;
	}
	
	public static ArrayList<Expression> sip(String vars){
		ArrayList<Character> tempList = new ArrayList<Character>(set);
		ArrayList<Expression> tempExprList = new ArrayList<Expression>();
		for(Expression e : exprlist)
		{
			tempExprList.add((Expression)e.clone());
		}
		String[] item = vars.split("\\ ");
		for(String s : item){
			String[] vv = s.split("\\=");
			String var = vv[0];
			int value = Integer.parseInt(vv[1]);
			if(var.length()==1&&set.contains(var.charAt(0))) {
				for (Expression expr : tempExprList)
				{
					int temp = expr.powers[tempList.indexOf(var.charAt(0))];
					if(temp!=0){
						expr.coef *= Math.pow(value, temp);
						expr.powers[tempList.indexOf(var.charAt(0))] = 0;
					}
				}
			}
			else{
				System.out.println("Error, no variable");
			}
		}
		return tempExprList;
	}
	public static ArrayList<Expression> dd(char var){  //求导
		ArrayList<Character> tempList = new ArrayList<Character>(set);
		ArrayList<Expression> tempExprList = new ArrayList<Expression>();
		for(Expression e : exprlist)
		{
			tempExprList.add((Expression)e.clone());
		}
		
		for (Expression expr : tempExprList)
		{
			int temp = expr.powers[tempList.indexOf(var)];
			if(temp != 0){
				expr.coef *= temp;
				expr.powers[tempList.indexOf(var)] = temp-1;	
			}
			else{
				expr.coef = 0;
				for(int i=0; i<expr.powers.length;i++){
					expr.powers[i] = 0;
				}
			}
		}
		return tempExprList;
	}
	
	public static ArrayList<Expression> command(String line){
		try {
			if(line.startsWith("d/d")) {
				char x = line.charAt(3);
				if (set.contains(x) && line.length()==4){
					return dd(x);
				}
				else{
					System.out.println("Error, no variable");
				}
			}
			else{//不是求导的情况下
					if(line.substring(0, 9).equals("simplify ")) return sip(line.substring(9));
					else System.out.println("command "+line+" not find");
				}
		}
		catch (Exception e)
	    {
	        // 异常处理代码
			e.printStackTrace();
			System.out.println("command "+line+" not find");
	    }
		return null;
	}
	
	public static boolean judge(String line)
	{
		char FirstChar,LastChar;
		FirstChar = line.charAt(0);
		LastChar = line.charAt(line.length()-1);
		if(line.charAt(0) == '!' ){
			print(merge(command(line.substring(1))));
			return false;
		}
		else{
			if (line.matches("[\\da-z\\+\\*\\-]*") && FirstChar != '+' && FirstChar != '*' && FirstChar != '-' && LastChar != '+' && LastChar != '*' && LastChar != '-')
			{
				for(int i = 0;i<line.length();i++){
					if(line.charAt(i)=='*'||line.charAt(i)=='+'||line.charAt(i)=='-'){
						if(!Character.isLetterOrDigit(line.charAt(i-1))||!Character.isLetterOrDigit(line.charAt(i+1))){
							System.out.println("input vaild");
							return false;
						}
					}
					else if(Character.isLetter(line.charAt(i))){
						if(i>0 && line.charAt(i-1)!='+' &&line.charAt(i-1)!='*' && line.charAt(i-1)!='-' ){
							System.out.println("input vaild");
							return false;							
						}
						if(i<line.length()-1 && line.charAt(i+1)!='*'&&line.charAt(i+1)!='+'&&line.charAt(i+1)!='-') {
							System.out.println("input vaild");
							return false;					
						}
					}
				}
				return true;
			}
			else{
				System.out.println("input vaild");
				return false;
			}
		}
	}
}
