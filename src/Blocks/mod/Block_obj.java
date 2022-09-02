package Blocks.mod;

import java.io.Serializable;
import java.util.List;

import transc.mod.Ctx;
import transc.mod.Ptx;

public class Block_obj implements Serializable{

	String  hash;
	 String  prev_hash;
	 String  Block_num;
	 String  reward;
	 String  validator_Addr;
	 long  timestamp;
	 String  Merkle_root_hash_sum;
	 List<Ctx> transactionsList;
	 List<Ptx> packTransactionsList;
	 String stakeReward;
	 String isEpoch = "EPOCH_ULTIMO_GRADU";
	 boolean isfreeFall = false;
	 long valPosition;
	 String version;
	 byte [] bytes;
	
		
		public Block_obj(String version,String Hash, String Block_num, String Prev_hash ,String Reward, String stakeRewards, String validator_Addr, long timestamp, String Merkle_root_hash_sum, List<Ctx> transactionsList, List <Ptx> packTransactionsList, String isEpoch,boolean isfreeFall,long valPosition,byte[]bytes) {
			this.version = version;
			this.hash = Hash;
			this.prev_hash = Prev_hash;
			this.Block_num = Block_num;
			this.reward = Reward;
			this.stakeReward = stakeRewards;
			this.validator_Addr = validator_Addr;
			this.timestamp = timestamp;
			this.Merkle_root_hash_sum = Merkle_root_hash_sum;	
			this.transactionsList = transactionsList;
			this.packTransactionsList = packTransactionsList;
			this.isEpoch = isEpoch;
			this.isfreeFall = isfreeFall;
			this.valPosition = valPosition;
			this.bytes = bytes;
		} 
		
	
		
		public String getHash() {
		    return hash;
		}
		
		public boolean getIsFreeFall() {
		    return isfreeFall;
		}
		
		public long getValPosition() {
		    return valPosition;
		}
		
		public void setHash(String hash) {
			this.hash = hash;
		}
		
		public String getPrev_hash() {
		    return prev_hash;
		}
		
		public void setPrev_hash(String prev_hash) {
			this.prev_hash = prev_hash;
		}
		
		public String getBlock_num() {
		    return Block_num;
		}
		
		public void setBlock_num(String Block_num) {
			this.Block_num = Block_num;
		}
		
		public String getReward() {
		    return reward;
		}
		
		public void setReward(String reward) {
			this.reward = reward;
		}

		public String getStakesReward() {
			return stakeReward;
		}
	
		public void setStakesReward(String stakeReward) {
			this.stakeReward = stakeReward;
		}
		
		public String getValidator_Addr() {
		    return validator_Addr;
		}
		
		public void setValidator_Addr(String validator_Addr) {
			this.validator_Addr = validator_Addr;
		}
		
		public long getTimestamp() {
		    return timestamp;
		}
		
		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}
		
		public void setMerkle_root_hash_sum(String Merkle_root_hash_sum) {
			this.Merkle_root_hash_sum = Merkle_root_hash_sum;
		}
		
		public String getMerkle_root_hash() {
		    return Merkle_root_hash_sum;
		}
		
		public List<Ctx> getTransactionList() {
		    return transactionsList;
		}

		public List<Ptx> getPackTransactionList() {
			// TODO Auto-generated method stub
			 return packTransactionsList;
		}

		public String getEpochStatus() {
			// TODO Auto-generated method stub
			return isEpoch;
		}

		public String getVersion() {
			// TODO Auto-generated method stub
			return version;
		}
		

}
