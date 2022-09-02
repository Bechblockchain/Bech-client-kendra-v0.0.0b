package KryoMod;

import java.io.Serializable;
import java.util.List;
import transc.mod.Ptx;

public class PtxListReg implements Serializable{

	List<Ptx> list = null;
	
	public PtxListReg(List<Ptx> list){
		this.list =list;
	}
	
	public List<Ptx> getPtxList(){
		return list;
	}
	
}
