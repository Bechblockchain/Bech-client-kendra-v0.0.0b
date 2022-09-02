package KryoMod;

import java.io.Serializable;
import java.util.List;
import Blocks.mod.Block_obj;
public class BlockListReg implements Serializable{

List<Block_obj> list = null;
	
	public BlockListReg(List<Block_obj> list){
		this.list = list;
	}
	
	public List<Block_obj> getBlockList(){
		return list;
	}
	
}
