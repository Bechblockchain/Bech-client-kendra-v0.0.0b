package cli.SEWS;

import java.security.Security;
import java.util.List;

import Blocks.db.Block_db;
import SEWS_Protocol.weightResults;
import Util.WindowsSetSystemTime;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command( name = "getWeights", mixinStandardHelpOptions = true, requiredOptionMarker = '*' ,description = "\nGet weight results from SEWS algo")
public class Weights implements Runnable{

	@Option(names = { "-w", "--weight" }, required = true, description = "weight results for epoch number...?")
	long weight;
	
	@Option(names = { "-p", "--fallPosition" } ,description = "position of weight on fall list")
	String position;
	
	@Option(names = { "-l", "--limit" } , description = "Upper(U) and Lower(L) limit of num of weights to return from fall list '--limit U to L --list' ")
	String limit;
	
	public void getData() {
		
		Blocks.mod.StakeBlock block = Block_db.getSingleStakeNumBlockData(String.valueOf(weight));
		List<weightResults> results = block.getProcessedStakes();
		
		if(limit != null && limit.endsWith(" --list") && limit.contains(" to ")) {
			printAllweights(results);
		
		}else {
			System.out.println("Upper(U) and Lower(L) limit of num of weights to return from fall list '--limit U to L --list'");
		}
		
		if(position != null) {
			printSingleWeight(results,Integer.parseInt(position));
		}else {
			System.out.println("Enter position of weight on fall list");
		}
		
	}
		
	public void printAllweights(List<weightResults> results) {
	
		System.out.println("\nWeight results: ");
		if(!limit.contains(" to ")) {
			String[] array = limit.split(" to ");
			String value1 = array[0];
			String value2 = array[1];
			String[] array2 = value2.split(" ");
			String value3 = array2[0];
			
			if(value1 != null && value3 != null) {
				int upperLimit = Integer.parseInt(value1);
				int lowerLimit = Integer.parseInt(value3);
				
				if(lowerLimit >= results.size()) {
					lowerLimit = results.size()-1;
				}
				for(int i = upperLimit; i <= lowerLimit; i++) {
					
					System.out.println(" Stake Obj Address- " + results.get(i).getStakeobj().getAddress());
					System.out.println(" Weight- " + results.get(i).getResults());
					System.out.println(" Weight Fall Position- " + results.get(i).getFallPosition());
					System.out.println(" Staked Coins- " + results.get(i).getStakeobj().getStakeCoins());
					System.out.println(" Stake Timestamp- " + results.get(i).getStakeobj().getTimestamp() + " -vf " +  WindowsSetSystemTime.convertTime( results.get(i).getStakeobj().getTimestamp()));
					System.out.println(" Expected Epoch Start Time- " + results.get(i).getEpoch() + "ms");
					System.out.println(" Epoch Time Allowance- " + results.get(i).getEpochAllowance() + "ms");
					System.out.println(" Next Epoch Weight Processing time- " + results.get(i).getProcessingEpoch() + "ms");
					System.out.println(" Next Epoch Weight Validation time- " + results.get(i).getValidatorConfirmationEpoch() + "ms");
					
					
				}
			}else {
					System.out.println("\"Upper(U) and Lower(L) limit of num of weights to return from fall list '--limit U to L --list'");
			}
			
		}
	}

	
	public void printSingleWeight(List<weightResults> results, int i) {
		System.out.println("\nSingle Weight results: ");
		
		System.out.println(" Stake Obj Address- " + results.get(i).getStakeobj().getAddress());
		System.out.println(" Weight- " + results.get(i).getResults());
		System.out.println(" Weight Fall Position- " + results.get(i).getFallPosition());
		System.out.println(" Staked Coins- " + results.get(i).getStakeobj().getStakeCoins());
		System.out.println(" Stake Timestamp- " + results.get(i).getStakeobj().getTimestamp() + " -vf " +  WindowsSetSystemTime.convertTime( results.get(i).getStakeobj().getTimestamp()));
		System.out.println(" Expected Epoch Start Time- " + results.get(i).getEpoch() + "ms");
		System.out.println(" Epoch Time Allowance- " + results.get(i).getEpochAllowance() + "ms");
		System.out.println(" Next Epoch Weight Processing time- " + results.get(i).getProcessingEpoch() + "ms");
		System.out.println(" Next Epoch Weight Validation time- " + results.get(i).getValidatorConfirmationEpoch() + "ms");
		
	}
	
	
	@Override
	public void run() {
		getData();
	}

	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		new CommandLine(new Weights()).execute(args);
	}
	
}
