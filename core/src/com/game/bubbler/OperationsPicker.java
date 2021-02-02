package com.game.bubbler;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class OperationsPicker {

	public static Operations[] pickOperations(Options options){			
		Array<Operations> operations = new Array<Operations>();
		if(options.additionD){
			operations.add(Operations.addition);
		}
		if(options.subtractionD){
			operations.add(Operations.subtraction);
		}
		if(options.multiplicationD){
			operations.add(Operations.multiplication);
		}
		if(options.divisionD){
			operations.add(Operations.division);
		}
		
		Operations[] temp = new Operations[MathUtils.random(1, options.factorsD - 1)];
		for(int i = 0; i < temp.length; i++){
			temp[i] = operations.random();
		}
		
		for(int i = 1; i < temp.length; i++){
			if((temp[i-1] == Operations.division || temp[i-1] == Operations.multiplication) && (temp[i] == Operations.division || temp[i] == Operations.multiplication)){
				if(MathUtils.randomBoolean()){
					temp[i] = Operations.addition;
				}else{
					temp[i] = Operations.subtraction;
				}
			}	
		}
		return temp;
	}
	
	protected static String getOperationsString(Operations operation){
		switch(operation){
		case addition:
			return "+";
		case subtraction:
			return "-";
		case multiplication:
			return "*";
		case division:
			return "/";
		}
		return null;
	}
	
	public static Results resultBuilder(Operations[] operations, Options options){
		double[] numbers = new double[operations.length+1];	
		
		options.difficulty = 0;
		for(int i = 0; i < operations.length; i++){
			if(i == 0){
				numbers[i] = (int) MathUtils.random(options.lowerLimitD, options.upperLimitD);
			}
			
			switch(operations[i]){
			case addition:
				numbers[i+1] = (int) MathUtils.random(options.lowerLimitD, options.upperLimitD);
				options.difficulty += 1d;
				break;
			case subtraction:
				numbers[i+1] = (int) MathUtils.random(options.lowerLimitD, options.upperLimitD);
				if(numbers[i] - numbers[i+1] < 0){
					numbers[i+1] -= (((numbers[i] - numbers[i+1])*(-1)) + MathUtils.random(options.lowerLimitD, options.upperLimitD));
				}
				options.difficulty += 1.1d;
				break;
			case multiplication:
				numbers[i+1] = (int) MathUtils.random(options.lowerLimitD, options.upperLimitD);
				options.difficulty += 1.25d;
				break;
			case division:
				int temp1 = (int) MathUtils.random(options.lowerLimitD, options.upperLimitD);
				int temp2 = (int) MathUtils.random(options.lowerLimitD, options.upperLimitD);
				int tempResult = temp1*temp2;
					numbers[i] = tempResult;
					if(MathUtils.randomBoolean()){
						numbers[i + 1] = temp1;
					}else{
						numbers[i + 1] = temp2;
					}
					options.difficulty += 1.5f;
				break;
			}
		}

		double down = 0;
		double up = 0;	
		for(int i = 0; i < numbers.length; i++){
			if(numbers[i] < down){
				down = (int) numbers[i];
			}else if(numbers[i] > up){
				up = (int) numbers[i]; 
			}	
		}

		options.difficulty = ((options.difficulty*(double)(operations.length) * (1d/(options.upperLimit*options.upperLimit + options.lowerLimit*options.lowerLimit)*(up*up + down*down)))*0.5d);

		String temp = "";
		
		for(int i = 0; i < operations.length; i++){ 
			if(numbers[i] <  0){
				temp = temp + " (" + (int)numbers[i] + ") " + getOperationsString(operations[i]);
			}else{
				temp = temp + " " + (int)numbers[i] + " " + getOperationsString(operations[i]);
			}
		}
		if(numbers[operations.length] <  0){
			temp = temp + " (" + (int)numbers[operations.length] + ")";
		}else{
			temp = temp + " " + (int)numbers[operations.length];
		}
		
		
		double result = calculateResult(operations, numbers);
		long points = calculatePoints(numbers);
		
		return new Results(result, temp, options.difficulty, points);
	}
	
	public static long calculatePoints(double[] numbers){
		long temp = 0;
		for(int i = 0; i < numbers.length; i++){
			if(numbers[i] < 0){
				temp -= numbers[i];
			}else{
				temp += numbers[i];
			}		
		}
		return temp;
	}
	
	public static double calculateResult(Operations[] operations, double[] numbers){		
		Calculation first = new Calculation(null, numbers[0]);
		Calculation last = first;
		for(int i = 0; i < operations.length; i++){		
			Calculation temp = new Calculation(operations[i], numbers[i+1]);
			temp.setBack(last);
			last.setNext(temp);
			last = temp;
		}		
		last = first;
		while(last != null){	
			if(last.op == Operations.multiplication){		
				last.back.value = last.back.value * last.value;
				last.back.setNext(last.next);
				if(last.next != null){
					last.next.setBack(last.back);
				}	
				last = last.back;
			}else if(last.op == Operations.division){
				last.back.value = last.back.value / last.value;
				last.back.setNext(last.next);
				if(last.next != null){
					last.next.setBack(last.back);
				}	
				last = last.back;
			}
			last = last.next;
		}
		
		last = first;
		double result = 0;
		while(last != null){	
			result += last.value;
			last = last.next;
		}		
		return result;
	}
	
	
	
	
	public enum Operations{
		addition, subtraction, multiplication, division
	}
}
