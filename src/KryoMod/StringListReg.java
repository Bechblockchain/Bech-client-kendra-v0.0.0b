package KryoMod;

import java.io.Serializable;
import java.util.List;


public class StringListReg implements Serializable{

	List<String> list = null;
	
	public StringListReg(List<String> list){
		this.list =list;
	}
	
	public List<String> getStringList(){
		return list;
	}
	
}
