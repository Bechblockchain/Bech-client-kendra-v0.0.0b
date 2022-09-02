package cli.Txs;

import java.io.IOException;
import java.security.Security;

import Blocks.db.Block_db;
import Blocks.mod.Block_obj;
import Util.WindowsSetSystemTime;
import connect.Network;
import connect.Mod.Request;
import crypto.BytesToFro;
import db.db_retrie;
import penalty.Report;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import temp.Static;
import transc.mod.Ctx;


@Command( name = "coinBlockTx", mixinStandardHelpOptions = true, requiredOptionMarker = '*' ,description = "\nTransaction and Block data")
public class BlockTxData implements Runnable{


	@Option(names = { "-t", "--txHash" } ,description = "Hex of transaction signature")
	String txHash;
	
	@Option(names = { "-b", "--block" } ,description = "Hash or Num of coin block")
	String blockhashnum;
	
	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		new CommandLine(new BlockTxData()).execute(args);
	}
	
	public void getTxBlockData() throws ClassNotFoundException, IOException {
		if(txHash == null && blockhashnum != null) {
			blockdata();
		}else if(blockhashnum == null && txHash != null) {
			txdata();
		}else if (blockhashnum != null && txHash != null) {
			blockdata();
			txdata();
		}
		
	}

	

	@Override
	public void run() {
		try {
			getTxBlockData();
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void blockdata() {

		String[] array = blockhashnum.split("[-]");
		String first = array[0];
		String command = array[1];
		if(first.equals("num")) {
		
				Block_obj block = Block_db.getSingleNumBlockData(command);
				displayBlockdata(block);
			
		}else if(first.equals("hash")) {
			
				Block_obj block = Block_db.getSingleBlockData(command);
				displayBlockdata(block);
			
		}else {
			System.out.println("Block query requires 'hash-' for block Hashes or 'num-' for block numbers prefix");
		}
		
	}
	
	private void txdata() throws IOException, ClassNotFoundException {
			String blocknum = db_retrie.getCoinTxIndex(txHash);
			if(blocknum != null) {
				Block_obj block = Block_db.getSingleNumBlockData(blocknum);
				if(block != null){
					displayCtxdata(block);
				}else {
					System.out.println(" \nBlock containing transaction is not available on native node...request form network!");
				}
			}else {
				System.out.println(" \nTransaction is not available on native node...request form network!");
			}
		
	}
	
	private void displayCtxdata(Block_obj block) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		System.out.println("\nTransaction Block Data:" + block.getVersion());
		System.out.println(" Version- " + block.getVersion());
		System.out.println(" Block Hash- " + block.getHash());
		System.out.println(" Block Number- " + block.getVersion());
		System.out.println(" Validator- " + block.getValidator_Addr());
		System.out.println(" Validator SEWS pos- " + block.getValPosition());
		System.out.println(" Block Timestamp- " + block.getTimestamp() + " -vf " + WindowsSetSystemTime.convertTime(block.getTimestamp()));
	
		for(Ctx ctx : block.getTransactionList()) {
			if(txHash.equals(BytesToFro.convertByteArrayToString(ctx.getTxSig()))) {
				String range = BytesToFro.convertByteArrayToString(ctx.getRange());
				
				System.out.println("Coin Tx Data:");
				
				if(range.equals(Static.REWARD_RANGE)) {
					System.out.println(" Executor Address- " + BytesToFro.convertByteArrayToString(ctx.getFromAddress()));
					System.out.println(" Total Epoch Reward- " + BytesToFro.convertBytesToBigDecimal(ctx.getValue()));
					System.out.println(" Epoch Validator Coin Reward- " + BytesToFro.convertBytesToBigDecimal(ctx.getCoinRewardAfterPartition()));
					System.out.println(" Epoch Validator Stake Reward- " + BytesToFro.convertBytesToBigDecimal(ctx.getStakeRewardAfterPartition()));
					System.out.println(" Vault Ratio- " + BytesToFro.convertBytesToBigDecimal(ctx.getVaultRatio()));
					System.out.println(" Reward Recipient Address- " + BytesToFro.convertByteArrayToString(ctx.getToAddress()));
					
				}else if(range.equals(Static.PENALTY_RANGE)) {
					
					Report report = BytesToFro.convertByteArrayToReport(ctx.getReport());
					System.out.println(" Reporter Address- "+ report.getReporterAddress());
					System.out.println(" Confiscated Coins- "+ ctx.getValue());
					System.out.println(" Felon Address- "+ report.getSuspectAddress());
					System.out.println(" Crime- "+ report.getCrime());
					System.out.println(" Punishment- "+ report.getRequest());
					System.out.println(" Report ID- "+ report.getReportID());
					
					
				}else if(range.equals(Static.OBSOLETE_STAKE_CLEARANCE)){
					System.out.println(" Executor Address- " + BytesToFro.convertByteArrayToString(ctx.getFromAddress()));
					System.out.println(" Reversed Coins- " + BytesToFro.convertBytesToBigDecimal(ctx.getValue()));
					System.out.println(" Obsolete SEWS Object- " + BytesToFro.convertByteArrayToString(ctx.getToAddress()));
				}else{
					System.out.println(" From Address- " + BytesToFro.convertByteArrayToString(ctx.getFromAddress()));
					System.out.println(" Amount- " + BytesToFro.convertBytesToBigDecimal(ctx.getValue()));
					System.out.println(" To Address- " + BytesToFro.convertByteArrayToString(ctx.getToAddress()));
				}
				
				System.out.println(" Fee- " + Static.FEE);
				System.out.println(" Tx Signature- " + BytesToFro.bytesToHex(ctx.getTxSig()));
				System.out.println(" S Tx Nonce- " + BytesToFro.convertBytesToLong(ctx.getNonce()));
				System.out.println(" Range- " + range);
				System.out.println(" Transaction timestamp- " + BytesToFro.convertBytesToLong(ctx.getTimestamp()) + " -vf " + WindowsSetSystemTime.convertTime(BytesToFro.convertBytesToLong(ctx.getTimestamp())) + "\n");
			 
				String blocknum = db_retrie.getCoinTxIndex(txHash);
				if(blocknum == null) {
					Request req = new Request("getTxFinality",txHash); 
					Network.sendNetworkRequest(req);
				}else {
					if(Long.parseLong(Static.PREV_BLOCK_NUM) > Long.parseLong(blocknum) + 5) {
						System.out.println("Transaction has reached finality");
					}else{
						System.out.println("Transaction is waiting to reach finality");
					}
					
				}
				
				break;
			}
		}
		
		
	}

	private void displayBlockdata(Block_obj block) {
		// TODO Auto-generated method stub
		
		System.out.println(" \nVersion- " + block.getVersion());
		System.out.println(" Block Hash- " + block.getHash());
		System.out.println(" Block Number- " + block.getVersion());
		System.out.println(" Previous Block Hash- " + block.getPrev_hash());
		System.out.println(" Block Coin Reward- " + block.getReward());
		System.out.println(" Block stake Reward- " + block.getStakesReward());
		System.out.println(" Validator- " + block.getValidator_Addr());
		System.out.println(" Validator SEWS pos- " + block.getValPosition());
		System.out.println(" Timestamp- " + block.getTimestamp() + " -vf " + WindowsSetSystemTime.convertTime(block.getTimestamp()));
		System.out.println(" Merkle Root- " + block.getMerkle_root_hash());
		System.out.println(" Block Epoch State- " + block.getEpochStatus());
		System.out.println(" IsFreeFall- " + block.getIsFreeFall());
		System.out.println(" Num of Ptxs- " + block.getPackTransactionList().size());
		System.out.println(" Num of Ctxs- " + block.getTransactionList().size());
		
		if(block.getTransactionList() != null) {
			
			int count = 0;
			for(Ctx tx: block.getTransactionList()) {
				count++;
				System.out.println(" Ctx" + count +"- " + BytesToFro.bytesToHex(tx.getTxSig()));
			}
		
		}
	
		
	}

	
	
	
}
