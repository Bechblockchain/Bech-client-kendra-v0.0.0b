package KryoMod;

import java.io.Serializable;
import java.util.List;

import transc.mod.Ctx;

public class CtxListReg implements Serializable{

	List<Ctx> list = null;
	
	public CtxListReg(List<Ctx> list){
		this.list =list;
	}
	
	public List<Ctx> getCtxList(){
		return list;
	}
	
}
