package node.Mod;

import java.io.Serializable;

public class CheqIn implements Serializable{

	NodeData nodedata;
	
	public CheqIn(NodeData nodedata) {
		this.nodedata = nodedata;
	}
	
	public NodeData getNodeData() {
	    return nodedata;
	}
	
	
}
