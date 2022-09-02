package Blocks.epoch;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.PrivateKey;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveAction;

import Blocks.db.Block_db;
import Blocks.mod.Block_obj;
import Blocks.mod.PreviousBlockObj;
import Blocks.mod.StakeBlock;
import SEWS_Protocol.StakeObj;
import SEWS_Protocol.StakeProcessor;
import SEWS_Protocol.weightResults;
import Transc_Util.Merkle_tree;
import Transc_Util.Verify_Tx;
import connect.Network;
import connect.Util.NetworkTime;
import crypto.BytesToFro;
import crypto.HashUtil;
import temp.Holder;
import temp.Static;
import transc.createTx.ClearObsoleteStakeAcc;
import transc.createTx.PenaltyTx;
import transc.createTx.RewardTx;
import transc.crypto.Hasher;
import transc.mod.Ctx;
import transc.mod.Ptx;
import wallets.db.Retrie;

public class ProcessBlocks extends RecursiveAction{

	private static final long serialVersionUID = -2819775112527190193L;
	 String  block_num;
	  String  prev_hash;
	  String  block_Height;
	  String  num_Transactions;
	  String  reward;
	  String  miner_Addr;
	  static long  timestamp;
	  static BigDecimal CalcRewards;
	  static BigDecimal CalcStakeRewards;
	  static String isLast = Static.EPOCH_ULTIMO_GRADU;
	  static String epochStatus;
	  static boolean isFreeFall = false;
	//  static boolean stakeProcessingEnded = false;
	  private static PrivateKey privKey ;
	  weightResults results;boolean isCleared;
	  
	  public ProcessBlocks (weightResults results,boolean isCleared) {
			this.results = results;
			this.isCleared = isCleared;
		}
	  
		public static Block_obj formNewBlock(long valFallPosition,long epochConfirmationTime,boolean startProcessingStakes,String isEpochStart,BigDecimal validatortotalEpochReward, BigDecimal coinRewardAfterPartition, BigDecimal stakeRewardAfterPartition, BigDecimal maintenanceVaultRatio, BigDecimal stakers50Partition,String prevValidatorAddress,boolean isEpochLastBlock) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException {
			timestamp = System.currentTimeMillis();
			List<Ctx> tx =  getCTx(startProcessingStakes,epochConfirmationTime,isEpochStart,validatortotalEpochReward,coinRewardAfterPartition,stakeRewardAfterPartition,maintenanceVaultRatio,stakers50Partition,prevValidatorAddress);	
			String merkle_root_hash = Merkle_tree.getCtxMerkleRoot(tx);
			List<Ptx> ptx =  getPTx();	
			String pack_merkle_root_hash = Merkle_tree.getPtxMerkleRoot(ptx);
			PreviousBlockObj parentBlock = PreviousBlockObj.getLatestBlockData();
			String parentHash = parentBlock.getHash();
			String blockNumber = (parentBlock.getBlockNum().add(BigInteger.valueOf(1))).toString();
			String merkle_Root_Sum = BytesToFro.convertByteArrayToString(HashUtil.sha3((HashUtil.sha3(BytesToFro.convertStringToByteArray(pack_merkle_root_hash + merkle_root_hash))))); 
			
			if(isEpochLastBlock) {
				epochStatus = Static.EPOCH_COMPLETE;
			}
			
			if(isEpochStart != null) {
				epochStatus = Static.EPOCH;
				if(isEpochStart.equals(Static.PUNISH_PREV_VALIDATOR)) {
					isFreeFall = true;
				}
			}
			
			byte[] hashbytes =  HashUtil.sha3((HashUtil.sha3(BytesToFro.convertStringToByteArray(String.valueOf(blockNumber) + parentHash + String.valueOf(CalcRewards) + String.valueOf(CalcStakeRewards) + Static.NATIVE_VALIDATOR_ADDRESS + String.valueOf(timestamp) + merkle_Root_Sum + String.valueOf(epochStatus) + String.valueOf(isFreeFall) + String.valueOf(valFallPosition)))));
			Block_obj block = new Block_obj(Static.VERSION,BytesToFro.convertByteArrayToString(hashbytes), blockNumber, parentHash, String.valueOf(CalcRewards),String.valueOf(CalcStakeRewards),Static.NATIVE_VALIDATOR_ADDRESS,timestamp,merkle_Root_Sum,tx,ptx,epochStatus,isFreeFall,valFallPosition,null);
			return block;
		}
		
		public static StakeBlock formNewStakeBlock(List<weightResults> results) throws IOException {
			long timestamp = System.currentTimeMillis();
			String stakeResults_merkle_root_hash = Merkle_tree.getStxResultsMerkleRoot(results);
			String StakeTx_merkle_root_hash = Merkle_tree.getCtxMerkleRoot(Holder.packagedStakeTx);
			List<Ctx> stakeTx = Holder.packagedStakeTx;
			String parentStakeHash = Block_db.getLatestStakeBlockHash();
			long stakeblockNumber = Long.parseLong(Block_db.getSingleStakeBlockData(parentStakeHash).getBlock_num() + 1);
			String createdFor = String.valueOf(0); 
			//String createdFor = String.valueOf(Long.parseLong(Static.EPOCH_HEIGHT)+1); 
			byte[] hashbytes =  HashUtil.sha3((HashUtil.sha3(BytesToFro.convertStringToByteArray(Static.EPOCH_HEIGHT + createdFor + parentStakeHash + String.valueOf(stakeblockNumber) + String.valueOf(timestamp) + stakeResults_merkle_root_hash + StakeTx_merkle_root_hash ))));
			String data = BytesToFro.convertByteArrayToString(hashbytes) + parentStakeHash + String.valueOf(stakeblockNumber) + String.valueOf(timestamp) + stakeResults_merkle_root_hash + StakeTx_merkle_root_hash;
					
			byte[] ValidatorSig = Hasher.generateValidatorSignature(privKey,BytesToFro.convertStringToByteArray(data));
			
			StakeBlock block = new StakeBlock(Static.VERSION,Static.EPOCH_HEIGHT,createdFor,BytesToFro.convertByteArrayToString(hashbytes),parentStakeHash,String.valueOf(stakeblockNumber),timestamp,stakeResults_merkle_root_hash,StakeTx_merkle_root_hash,BytesToFro.bytesToHex(ValidatorSig),stakeTx,results);
			return block;
		}
		
		public static List<Ctx> getCTx(boolean StakePacking,long epochConfirmationTime, String isEpochStart,BigDecimal validatortotalEpochReward, BigDecimal coinRewardAfterPartition, BigDecimal stakeRewardAfterPartition, BigDecimal maintenanceVaultRatio, BigDecimal stakers50Partition, String prevValidatorAddress) throws IOException, ClassNotFoundException, InterruptedException, ExecutionException {
			
			List<Ctx> listToAdd = new CopyOnWriteArrayList<>(); 
			CalcRewards = new BigDecimal("0");
			CalcStakeRewards = new BigDecimal("0");
			
			   
				//add coin Txs
				BigDecimal staker50num = null;
				List<weightResults> prevEpochResults = null;
				if(isEpochStart != null && isEpochStart.equals(Static.REWARD_PREV_VALIDATOR)) {
					
					String stakeblockHash = Static.TEMP_STAKE_BLOCK.getPrev_hash();
					prevEpochResults = Block_db.getSingleStakeBlockData(stakeblockHash).getProcessedStakes();
					prevEpochResults.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
					
					int counter = -1;
					
					for(int k = 0; k < prevEpochResults.size();k++) {
						counter++;
						if(prevEpochResults.get(k).getStakeobj().getAddress().equals(prevValidatorAddress)) {
							prevEpochResults.remove(k);
							if(k != 0) {
								for(int j = 0; j < counter;j++) {
									prevEpochResults.remove(j);
								}
							}
							break;
						}
						
					}
					staker50num = new BigDecimal(prevEpochResults.size()*0.5).setScale(0, RoundingMode.DOWN);
				
					BigDecimal eachStaker50Partition = stakers50Partition.divide(staker50num).setScale(2, RoundingMode.HALF_EVEN);
					for(int l = 0;l < staker50num.intValue();l++) {
						listToAdd.add(RewardTx.returnStaker50RewardTx(validatortotalEpochReward,coinRewardAfterPartition,stakeRewardAfterPartition,maintenanceVaultRatio,stakers50Partition,eachStaker50Partition,prevEpochResults.get(l).getStakeobj().getAddress()));
					}
					listToAdd.add(RewardTx.returnRewardTx(validatortotalEpochReward,coinRewardAfterPartition,stakeRewardAfterPartition,maintenanceVaultRatio,prevValidatorAddress));
				
				}else if(isEpochStart != null && isEpochStart.equals(Static.PUNISH_PREV_VALIDATOR)){
								
					BigDecimal confiscatedRewards = Static.CTX_REWARD.add(Static.STAKE_REWARD);
					if(Holder.myReports.size() == 1 ) {
						listToAdd.add(PenaltyTx.createPenaltyTx(Holder.myReports.get(0),confiscatedRewards));
					}else {
						listToAdd.add(PenaltyTx.createPenaltyTx(Holder.myReports.get(0),confiscatedRewards));
						listToAdd.add(PenaltyTx.createPenaltyTx(Holder.myReports.get(1),confiscatedRewards));
					}
				}
				
				long start = System.nanoTime();
				long end = start + 90000000;
				while (System.nanoTime() < end) {
				
					//Verify and add coin transactions
					if( !Holder.returnTxInMem().isEmpty()) {
						Ctx tx = Holder.returnTxInMem().remove(0);
						CalcRewards = CalcRewards.add(Static.FEE).setScale(2, RoundingMode.HALF_EVEN);
						listToAdd.add(tx);
					}
				}
					
			
				//Add obsolete stake account clearance Tx 
				if(!Holder.ObsoleteStakeAcc.isEmpty()) {
					StakeObj stakeobj = Holder.ObsoleteStakeAcc.remove(0);
					Ctx ctx = ClearObsoleteStakeAcc.createObsoleteStakeAccClearanceTx(stakeobj,Static.NATIVE_VALIDATOR_ADDRESS);
					
					if(Verify_Tx.verifyCtx(ctx,epochConfirmationTime)) {
						CalcStakeRewards = CalcStakeRewards.add(Static.FEE).setScale(2, RoundingMode.HALF_EVEN);
						listToAdd.add(ctx);
						Holder.ObsoleteStakeAcc.remove(stakeobj);
					}
				}
				
				
				//Verify and add coin transactions
			
					if(!StakePacking) {
						
						if(!Holder.returnSTxInMem().isEmpty()) {
							Ctx stx = Holder.returnSTxInMem().remove(0);
							String toAddress = BytesToFro.convertByteArrayToString(stx.getToAddress());
						    
						    if(toAddress.equals(Static.STAKE_OBJ)) {
								String address = BytesToFro.convertByteArrayToString(stx.getFromAddress());
								if(address.equals(Static.NATIVE_VALIDATOR_ADDRESS) || Holder.gehennaList.contains(address)) {
									
									Holder.returnSTxInMem().remove(stx);
										
								}else {
									if(Verify_Tx.verifyCtx(stx,epochConfirmationTime)) {
										Holder.packagedStakeTx.add(stx);
										CalcStakeRewards = CalcStakeRewards.add(Static.FEE).setScale(2, RoundingMode.HALF_EVEN);
										listToAdd.add(stx);
										Holder.returnSTxInMem().remove(stx);
									
									}
									
								}
						    }
							
						}
						
					}
			
			
			return listToAdd;
		}
		
		public static List<Ptx> getPTx() throws IOException {
			
			 List<Ptx> packlist = new CopyOnWriteArrayList<>(); 
			 if(Holder.returnPackTxInMem().isEmpty()) {
				 return packlist;
			 }else {
				 
				 int counter = -1;
				 do {
					 counter++;
					 
				Ptx tx = Holder.returnPackTxInMem().get(counter);
				String fromCoinAddress = BytesToFro.convertByteArrayToString(tx.getFromCoinAddress());
				String toCoinAddress = BytesToFro.convertByteArrayToString(tx.getToCoinAddress());
					if(!Holder.packagedPtxAddr.contains(fromCoinAddress) && !Holder.gehennaList.contains(fromCoinAddress) && !Holder.packagedPtxAddr.contains(toCoinAddress) && !Holder.gehennaList.contains(toCoinAddress)) {
						if(Verify_Tx.verifyPtx(tx)) {
						
							packlist.add(tx);
							Holder.packagedPtxAddr.add(fromCoinAddress);
							Holder.packagedPtxAddr.add(toCoinAddress);
							Holder.returnPackTxInMem().remove(tx);
						}
					}
				 }while(packlist.size() < 9);
				 				
				
			 }	
			return packlist;
		}
		
		public static void startValidation(weightResults results,boolean isCleared) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException  {
			
			privKey = Retrie.retrievePrivateKeyWithPublicKey(Retrie.retrieveAccData(Static.NATIVE_VALIDATOR_ADDRESS).getPubkey());
			if(!Holder.myReports.isEmpty()) {
				Holder.myReports.clear();
			}
			if(!Holder.receivedReports.isEmpty()) {
				Holder.receivedReports.clear();
			}
			
			long operationTime = 0;
			long epochStartTime = System.currentTimeMillis();

			long expectedEpochTime = results.getEpoch().longValue() + epochStartTime;
			long stakeProcessingTime = results.getProcessingEpoch().longValue() + epochStartTime;
			long nextValidatorConfirmation = results.getValidatorConfirmationEpoch().longValue() + epochStartTime;
			long epochAllowance = results.getEpochAllowance().longValue() + epochStartTime;// - 800000;
			long valFallPosition = results.getFallPosition().longValue();
			
			String stakeblockHash = null;
			BigDecimal validatorTotalEpochReward = null;
			BigDecimal coinRewardAfterPartition = null;
			BigDecimal stakeRewardAfterPartition = null;
			BigDecimal maintenanceVaultRatio = null;
			BigDecimal stakers50Partition = null;
			String prevValidatorAddress = null;
			
			if(isCleared) {
				stakeblockHash = Static.TEMP_STAKE_BLOCK.getPrev_hash();
			}else {
				stakeblockHash = Static.TEMP_STAKE_BLOCK.getHash();
			}
			
			List<weightResults> prevEpochResults = Block_db.getSingleStakeBlockData(stakeblockHash).getProcessedStakes();
			prevEpochResults.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
				for(weightResults prevWeight : prevEpochResults) {
					if(prevWeight.getStakeobj().getAddress().equals(Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS)) {
						prevValidatorAddress = prevWeight.getStakeobj().getAddress();				
						stakeRewardAfterPartition = Static.STAKE_REWARD.setScale(2, RoundingMode.HALF_EVEN);
						coinRewardAfterPartition = Static.CTX_REWARD.multiply(new BigDecimal(0.5)).setScale(2, RoundingMode.HALF_EVEN);
						stakers50Partition = Static.CTX_REWARD.multiply(new BigDecimal(0.4)).setScale(2, RoundingMode.HALF_EVEN);
						maintenanceVaultRatio = Static.CTX_REWARD.multiply(new BigDecimal(0.1)).setScale(2, RoundingMode.HALF_EVEN);
						break;
					}
					
				}
				
			
			String isEpochStart = null;
			int isEpochBlockCounter = 0;
			boolean sendStakeblock = true;
			boolean isEpochLastBlock = false;
			
			do {//check for islast packblock
				isEpochBlockCounter++;
				if(!Holder.myReports.isEmpty()) {
					break;
				}
				if(!Holder.receivedReports.isEmpty()) {
					break;
				}
				boolean startProcessingStakes = true;
				//Validate Tx and form coin blocks
				if(isEpochBlockCounter == 1) {
					if(isCleared) {
						isEpochStart = Static.REWARD_PREV_VALIDATOR;
					}else {
						isEpochStart = Static.PUNISH_PREV_VALIDATOR;
					}
				}
			
				operationTime = System.currentTimeMillis();
				if(operationTime > epochAllowance ) {
					isEpochLastBlock = true;
				}else {
					isEpochLastBlock = false;
				}
				
				if(operationTime < stakeProcessingTime) {
					startProcessingStakes = false;
				}
				
				Network.broadcastCoinBlock(formNewBlock(valFallPosition,nextValidatorConfirmation,startProcessingStakes,isEpochStart,validatorTotalEpochReward,coinRewardAfterPartition,stakeRewardAfterPartition,maintenanceVaultRatio,stakers50Partition,prevValidatorAddress,isEpochLastBlock));
				
				// Time to process each stake and weigh up the results against each other
				if(operationTime >= stakeProcessingTime && Holder.STAKE_RESULTS.isEmpty()) {
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							List<weightResults> tempNextValidators = StakeProcessor.weighStakes();
							Network.broadcastweightResults(tempNextValidators);
							Holder.STAKE_RESULTS = tempNextValidators;
						}
					};runnable.run();
					
				}else if(operationTime >= nextValidatorConfirmation && sendStakeblock) {
					///Time to Send Stake Block to Confirm the next validator
					sendStakeblock = false;
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
						
							try {
								if(!Holder.STAKE_RESULTS.isEmpty()) {
									System.out.println("Stake block: " + Holder.STAKE_RESULTS.get(0).getStakeobj().getStakeCoins());
									Network.broadcastStakeBlock(formNewStakeBlock(Holder.STAKE_RESULTS));
									Holder.STAKE_RESULTS.clear();
									if(!Holder.packagedStakeTx.isEmpty()) {
										Holder.packagedStakeTx.clear();
									}
								}Holder.STAKE_RESULTS.clear();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
								
						}
					};runnable.run();
				}
				
			}while(operationTime < epochAllowance);
		}
	
	
	@Override
	protected void compute() {
		// TODO Auto-generated method stub
		try {
			startValidation(results,isCleared);
		} catch (ClassNotFoundException | IOException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
