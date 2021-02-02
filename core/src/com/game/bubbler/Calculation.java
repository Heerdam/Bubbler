package com.game.bubbler;

import com.game.bubbler.OperationsPicker.Operations;

public class Calculation{
	public Operations op;
	public double value;
	public double n1;
	public Calculation next;
	public Calculation back;

	public Calculation(Operations op, double n1){
		if(op == null){
			op = Operations.addition;
		}else{
			this.op = op;
		}	
		this.n1 = n1;
		if(op == Operations.subtraction){
			value = n1*-1;
		}else{
			value = n1;
		}
		this.next = null;
		this.back  = null;
	}
	
	public Calculation setNext(Calculation next){
		this.next = next;
		return this;
	}
	
	public Calculation setBack(Calculation back){
		this.back = back;
		return this;
	}
}

