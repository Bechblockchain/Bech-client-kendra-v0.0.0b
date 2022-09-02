package KryoMod;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;


public class InetAddressListReg implements Serializable{

List<InetAddress> list = null;
	
	public InetAddressListReg(List<InetAddress> list){
		this.list =list;
	}
	
	public List<InetAddress> getInetAddressList(){
		return list;
	}
	
}
