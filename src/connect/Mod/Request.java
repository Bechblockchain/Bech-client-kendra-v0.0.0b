package connect.Mod;

import java.io.Serializable;
import java.util.List;

import Blocks.mod.Block_obj;
import SEWS_Protocol.weightResults;
import wallets.mod.Acc_obj;

public class Request implements Serializable{

	String command,chainHeght;
	String item,item2;
	Acc_obj acc;
	weightResults weight; List<weightResults> weightlist;
	boolean isclear;
	Block_obj coinblock;
	
	public Request(String command, String item,Acc_obj acc,weightResults weight,boolean isclear) {
		this.command = command;
		this.item = item;
		this.acc = acc;
		this.weight = weight;
		this.isclear = isclear;
	}
	
	public Request(String command, String item, String item2) {
		this.command = command;
		this.item = item;
		this.item2 =item2;
	}
	
	public Request(String command, Block_obj coinblock) {
		this.command = command;
		this.coinblock = coinblock;
	}
	
	public Request(String command, Acc_obj acc) {
		this.command = command;
		this.acc = acc;
	}
	
	public Request(List<weightResults> weightlist,boolean isclear) {
		this.weightlist = weightlist;
		this.isclear = isclear;
	}
	
	public Request(weightResults weight,boolean isclear) {
		this.weight = weight;
		this.isclear = isclear;
	}
	
	public Request(String command, String item) {
		this.command = command;
		this.item = item;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getItem() {
		return item;
	}
	
	public Acc_obj getAcc() {
		return acc;
	}
	
	public weightResults getWeightResults() {
		return weight;
	}
	
	public List<weightResults> getWeightResultsList() {
		return weightlist;
	}
	
	public boolean getIsclear() {
		return isclear;
	}
	
	public void setCommand(String command) {
        this.command=command;
	}
	
	public void setItem(String item) {
        this.item=item;
	}

	public Block_obj getCoinBlock() {
		
		// TODO Auto-generated method stub
		return coinblock;
	}

	public String getItem2() {
		// TODO Auto-generated method stub
		return item2;
	}
}
