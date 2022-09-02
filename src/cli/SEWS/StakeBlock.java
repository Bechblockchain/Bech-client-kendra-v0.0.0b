package cli.SEWS;

import java.security.Security;

import Blocks.db.Block_db;
import Util.WindowsSetSystemTime;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command( name = "stakeBlockData", mixinStandardHelpOptions = true, requiredOptionMarker = '*', description = "\nStake block data")
public class StakeBlock implements Runnable{

	@Option(names = { "-b", "--block" }, required = true, description = "Hash or Num of stake block")
	String blockhashnum;
	

	private void getStakeBlockData() {
	
		String[] array = blockhashnum.split("[-]");
		String first = array[0];
		String command = array[1];
		if(first.equals("num")) {
		
			Blocks.mod.StakeBlock block = Block_db.getSingleStakeNumBlockData(command);
			printStakeBlockdata(block);
		
		}else if(first.equals("hash")) {
			
			Blocks.mod.StakeBlock block = Block_db.getSingleStakeBlockData(command);
			printStakeBlockdata(block);
		
		}else {
			System.out.println("Block query requires 'hash-' for block Hashes or 'num-' for block numbers prefix");
		}
			
	}
	

	public void printStakeBlockdata(Blocks.mod.StakeBlock block) {
		System.out.println("\nStake Block Data: ");
		System.out.println(" Version- " + block.getVersion());
		System.out.println(" Created at Epoch- " + block.getEpochCreated());
		System.out.println(" Created for Epoch- " + block.getEpochCreatedFor());
		System.out.println(" Block Hash- " + block.getHash());
		System.out.println(" Block Number- " + block.getVersion());
		System.out.println(" Previous Block Hash- " + block.getPrev_hash());
		System.out.println(" Validator Sig- " + block.getValidatorSig());
		System.out.println(" Weights Merkle Root- " + block.getMerkleRootProcessedStakes());
		System.out.println(" Txs Merkle Root- " + block.getMerkleRootStakeTx());
		System.out.println(" Timestamp- " + block.getTimestamp() + " -vf " + WindowsSetSystemTime.convertTime(block.getTimestamp()));
		System.out.println(" Weights size- " + block.getProcessedStakes().size());
		System.out.println(" Num of Epoch Stake Txs- " + block.getEpochStakeTx().size());
		
	}

	
	@Override
	public void run() {
		getStakeBlockData();
	}

	
	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		new CommandLine(new StakeBlock()).execute(args);
		
	}
	
}
