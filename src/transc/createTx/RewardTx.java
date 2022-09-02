package transc.createTx;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;


import Blocks.db.Block_db;
import Blocks.mod.StakeBlock;
import crypto.BytesToFro;
import temp.Static;
import transc.crypto.Hasher;
import transc.mod.Ctx;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

public class RewardTx {

	public static Ctx returnRewardTx(BigDecimal validatorTotalEpochReward, BigDecimal coinRewardAfterPartition, BigDecimal stakeRewardAfterPartition,BigDecimal maintenaceVaultRatio, String ToCoinAddress) throws IOException {
		String FromCoinAddress = "reward_tx";
		Ctx transaction = null;
		long timestamp = System.currentTimeMillis();
		Acc_obj accData = Retrie.retrieveAccData(Static.NATIVE_VALIDATOR_ADDRESS);
  		PublicKey validatorPublicKey = accData.getPubkey();
  	   
       	PrivateKey validatorPrivateKey = Retrie.retrievePrivateKeyWithPublicKey(validatorPublicKey);
     	
     	byte[] totalEpochRewardBytes = BytesToFro.convertBigDecimalToByteArray(validatorTotalEpochReward);
     	byte[] coinRewardAfterPartitionBytes = BytesToFro.convertBigDecimalToByteArray(coinRewardAfterPartition);
     	byte[] stakeRewardAfterPartitionBytes = BytesToFro.convertBigDecimalToByteArray(stakeRewardAfterPartition);
     	byte[] vaultRatioBytes = BytesToFro.convertBigDecimalToByteArray(maintenaceVaultRatio);
     	long rewardTxNonce = 0L;
     	byte[] rewardTxNonceBytes = BytesToFro.convertLongToBytes(rewardTxNonce);
		StakeBlock stakeblock = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash());
		String previousEpochHashToBeAwarded = stakeblock.getPrev_hash();
		long previousEpochNumberToBeAwarded = Long.valueOf(stakeblock.getBlock_num()) - 1;
		 
			byte[] TxSig = Hasher.generateSenderCoinSignature(validatorPrivateKey, FromCoinAddress, ToCoinAddress, validatorPublicKey, validatorTotalEpochReward, coinRewardAfterPartition,stakeRewardAfterPartition, maintenaceVaultRatio, rewardTxNonce, timestamp, previousEpochNumberToBeAwarded, previousEpochHashToBeAwarded,Static.REWARD_RANGE);
			
			byte[] senderAddress = BytesToFro.convertStringToByteArray(FromCoinAddress);
			byte[] receiverAddress = BytesToFro.convertStringToByteArray(ToCoinAddress);
			byte[] txtimestamp = BytesToFro.convertLongToBytes(timestamp);
			byte[] previousEpochHashToBeAwardedBytes = BytesToFro.convertStringToByteArray(stakeblock.getPrev_hash());
			byte[] previousEpochNumberToBeAwardedBytes = BytesToFro.convertLongToBytes(Long.valueOf(stakeblock.getBlock_num()) - 1);
			 
			
			byte[] range = BytesToFro.convertStringToByteArray(Static.REWARD_RANGE);
			
			
			transaction = new Ctx(senderAddress, receiverAddress, totalEpochRewardBytes,coinRewardAfterPartitionBytes,stakeRewardAfterPartitionBytes,vaultRatioBytes, txtimestamp, rewardTxNonceBytes, TxSig, previousEpochNumberToBeAwardedBytes, previousEpochHashToBeAwardedBytes, range);
		
		return transaction;
	}
	
	public static Ctx returnStaker50RewardTx(BigDecimal validatorTotalEpochReward, BigDecimal coinRewardAfterPartition, BigDecimal stakeRewardAfterPartition,BigDecimal maintenaceVaultRatio,BigDecimal stakers50Partition,BigDecimal eachStaker50Partition,String staker50Address) throws IOException {
		
		String FromCoinAddress = "reward_50_tx";
		Ctx transaction = null;
		long timestamp = System.currentTimeMillis();
		Acc_obj accData = Retrie.retrieveAccData(Static.NATIVE_VALIDATOR_ADDRESS);
  		PublicKey validatorPublicKey = accData.getPubkey();
  	   
       	PrivateKey validatorPrivateKey = Retrie.retrievePrivateKeyWithPublicKey(validatorPublicKey);
     	
     	byte[] totalEpochRewardBytes = BytesToFro.convertBigDecimalToByteArray(validatorTotalEpochReward);
     	byte[] coinRewardAfterPartitionBytes = BytesToFro.convertBigDecimalToByteArray(coinRewardAfterPartition);
     	byte[] stakeRewardAfterPartitionBytes = BytesToFro.convertBigDecimalToByteArray(stakeRewardAfterPartition);
     	byte[] vaultRatioBytes = BytesToFro.convertBigDecimalToByteArray(maintenaceVaultRatio);
     	long rewardTxNonce = 0L;
     	byte[] rewardTxNonceBytes = BytesToFro.convertLongToBytes(rewardTxNonce);
		StakeBlock stakeblock = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash());
		String previousEpochHashToBeAwarded = stakeblock.getPrev_hash();
		long previousEpochNumberToBeAwarded = Long.valueOf(stakeblock.getBlock_num()) - 1;
		 
			byte[] TxSig = Hasher.generateSenderCoinSignature(validatorPrivateKey, FromCoinAddress, staker50Address, validatorPublicKey, validatorTotalEpochReward, coinRewardAfterPartition,stakeRewardAfterPartition, maintenaceVaultRatio,eachStaker50Partition, rewardTxNonce, timestamp, previousEpochNumberToBeAwarded, previousEpochHashToBeAwarded,Static.REWARD_RANGE);
			
			byte[] senderAddress = BytesToFro.convertStringToByteArray(FromCoinAddress);
			byte[] receiverAddress = BytesToFro.convertStringToByteArray(staker50Address);
			byte[] txtimestamp = BytesToFro.convertLongToBytes(timestamp);
			byte[] previousEpochHashToBeAwardedBytes = BytesToFro.convertStringToByteArray(stakeblock.getPrev_hash());
			byte[] previousEpochNumberToBeAwardedBytes = BytesToFro.convertLongToBytes(Long.valueOf(stakeblock.getBlock_num()) - 1);
			byte[] eachStaker50PartitionBytes = BytesToFro.convertBigDecimalToByteArray(eachStaker50Partition);
			byte[] stakers50Partitionbytes = BytesToFro.convertBigDecimalToByteArray(stakers50Partition); 
			byte[] range = BytesToFro.convertStringToByteArray(Static.REWARD_RANGE);
			
			
			transaction = new Ctx(senderAddress, receiverAddress, totalEpochRewardBytes,coinRewardAfterPartitionBytes,stakeRewardAfterPartitionBytes,vaultRatioBytes,stakers50Partitionbytes,eachStaker50PartitionBytes, txtimestamp, rewardTxNonceBytes, TxSig, previousEpochNumberToBeAwardedBytes, previousEpochHashToBeAwardedBytes, range);
		
		return transaction;
		
	}
	
}
