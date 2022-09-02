package Transc_Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import SEWS_Protocol.weightResults;
import crypto.BytesToFro;
import crypto.HashUtil;
import transc.mod.Ctx;
import transc.mod.Ptx;

public class Merkle_tree {

	
	public static String getCtxMerkleRoot(List<Ctx> transactions) throws IOException {
		int count = transactions.size();
		
		List<byte[]> previousTreeLayer = new ArrayList<byte[]>();
		for(Ctx transaction : transactions) {
			previousTreeLayer.add(HashUtil.sha3(HashUtil.sha3(BytesToFro.convertObjToByteArray(transaction))));
		}
		List<byte[]> treeLayer = previousTreeLayer;
		
		while(count > 1) {
			treeLayer = new ArrayList<byte[]>();
			for(int i=1; i < previousTreeLayer.size(); i+=2) {
				treeLayer.add(HashUtil.sha3(HashUtil.sha3(BytesToFro.convertStringToByteArray(BytesToFro.convertByteArrayToString(previousTreeLayer.get(i-1)) + BytesToFro.convertByteArrayToString(previousTreeLayer.get(i))))));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? BytesToFro.bytesToHex(treeLayer.get(0)) : "";
		return merkleRoot;
	}
	
	
	public static String getStringMerkleRoot(List<String> transactions) throws IOException {
		int count = transactions.size();
		
		List<byte[]> previousTreeLayer = new ArrayList<byte[]>();
		for(String transaction : transactions) {
			previousTreeLayer.add(HashUtil.sha3(HashUtil.sha3(BytesToFro.convertStringToByteArray(transaction))));
		}
		List<byte[]> treeLayer = previousTreeLayer;
		
		while(count > 1) {
			treeLayer = new ArrayList<byte[]>();
			for(int i=1; i < previousTreeLayer.size(); i+=2) {
				treeLayer.add(HashUtil.sha3(HashUtil.sha3(BytesToFro.convertStringToByteArray(BytesToFro.convertByteArrayToString(previousTreeLayer.get(i-1)) + BytesToFro.convertByteArrayToString(previousTreeLayer.get(i))))));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? BytesToFro.bytesToHex(treeLayer.get(0)) : "";
		return merkleRoot;
	}
	
	
	public static String getPtxMerkleRoot(List<Ptx> transactions) throws IOException {
	int count = transactions.size();
		
		List<byte[]> previousTreeLayer = new ArrayList<byte[]>();
		for(Ptx transaction : transactions) {
			previousTreeLayer.add(HashUtil.sha3(HashUtil.sha3(BytesToFro.convertObjToByteArray(transaction))));
		}
		List<byte[]> treeLayer = previousTreeLayer;
		
		while(count > 1) {
			treeLayer = new ArrayList<byte[]>();
			for(int i=1; i < previousTreeLayer.size(); i+=2) {
				treeLayer.add(HashUtil.sha3(HashUtil.sha3(BytesToFro.convertStringToByteArray(BytesToFro.convertByteArrayToString(previousTreeLayer.get(i-1)) + BytesToFro.convertByteArrayToString(previousTreeLayer.get(i))))));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? BytesToFro.bytesToHex(treeLayer.get(0)) : "";
		return merkleRoot;
	}
	
	
	public static String getStxResultsMerkleRoot(List<weightResults> stakes) throws IOException {
		List<weightResults> newStakes = new ArrayList<>();
		for(weightResults weight : stakes) {
			weightResults newWeight = new weightResults(weight.getStakeobj(),weight.getResults(),weight.getEpoch(),weight.getProcessingEpoch(),weight.getValidatorConfirmationEpoch(),weight.getEpochAllowance(),weight.getFallPosition(),weight.getTimestamp());
					
			newStakes.add(newWeight);
		} 
		
		newStakes.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition())); 
		
		int count = newStakes.size();
			
			List<byte[]> previousTreeLayer = new ArrayList<byte[]>();
			for(weightResults weight : newStakes) {
				previousTreeLayer.add(HashUtil.sha3(HashUtil.sha3(BytesToFro.convertObjToByteArray(weight))));
			}
			List<byte[]> treeLayer = previousTreeLayer;
			
			while(count > 1) {
				treeLayer = new ArrayList<byte[]>();
				for(int i=1; i < previousTreeLayer.size(); i+=2) {
					treeLayer.add(HashUtil.sha3(HashUtil.sha3(BytesToFro.convertStringToByteArray(BytesToFro.convertByteArrayToString(previousTreeLayer.get(i-1)) + BytesToFro.convertByteArrayToString(previousTreeLayer.get(i))))));
				}
				count = treeLayer.size();
				previousTreeLayer = treeLayer;
			}
			String merkleRoot = (treeLayer.size() == 1) ? BytesToFro.bytesToHex(treeLayer.get(0)) : "";
			return merkleRoot;
		}
	
}
