package KryoMod;

import java.io.Serializable;
import java.util.List;
import SEWS_Protocol.StakeObj;

public class StakeObjListReg implements Serializable{


List<StakeObj> list = null;
	
	public StakeObjListReg(List<StakeObj> list){
		this.list = list;
	}
	
	public List<StakeObj> getStakeList(){
		return list;
	}
	
	
}
