package cli;

import java.security.Security;

import SEWS_Protocol.db.retrieve;
import Util.WindowsSetSystemTime;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import temp.Static;

@Command( name = "epoch", mixinStandardHelpOptions = true, requiredOptionMarker = '*' ,description = "\nepoch info")
public class Epoch implements Runnable{

	@Option(names = { "-e", "--epoch" }, required = true,description = "enter epoch height number or 'current' or 'next' for current and next epoch info respectively")
	String command;
	
	
	public void command() {
		
		if(command.equals("current")) {
			System.out.println("\nCurrent Epoch Info:");
			
			System.out.println(" Started At- " + Static.CURRENT_EPOCH_START_TIME + " -vf " + WindowsSetSystemTime.convertTime(Long.parseLong(Static.CURRENT_EPOCH_START_TIME)));
			System.out.println(" Ends At- " + Static.CURRENT_EPOCH_END_TIME + " -vf " +  WindowsSetSystemTime.convertTime(Long.parseLong(Static.CURRENT_EPOCH_END_TIME)));
			System.out.println(" Validator- " + Static.EPOCH_VALIDATOR_ADDRESS);
			System.out.println(" Validator Fall Position- " + Static.CURRENT_VALIDATOR_FALL_POSITION);
			System.out.println(" Height- " + Static.CURENT_EPOCH_HEIGHT);
			System.out.println(" Next Stake Weighing Begins At- " + Static.CURRENT_NEXT_STAKE_PROCESSING + " -vf " + WindowsSetSystemTime.convertTime(Static.CURRENT_NEXT_STAKE_PROCESSING.longValue()));
			
		}else if(command.equals("next")) {
			
			System.out.println("\nNext Epoch Info:");
			
			System.out.println(" Next Epoch Starts At- " + Static.NEXT_EPOCH_START_TIME + " -vf " +  WindowsSetSystemTime.convertTime(Long.parseLong(Static.NEXT_EPOCH_START_TIME)));
			System.out.println(" Next Epoch Ends At- " + Static.NEXT_EPOCH_END_TIME + " -vf " +  WindowsSetSystemTime.convertTime(Long.parseLong(Static.NEXT_EPOCH_END_TIME)));
			System.out.println(" Next Epoch Validator- " + Static.NEXT_EPOCH_VALIDATOR_ADDRESS);
			System.out.println(" Next Epoch Validator Fall Position- " + Static.NEXT_VALIDATOR_FALL_POSITION);
			
		}else {
			
			Blocks.mod.Epoch epoch = retrieve.retrieveEpochData(Long.parseLong(command));
			if(epoch == null) {
				System.out.println("\nEnter Epoch Height number-");
			}else {
				
				System.out.println("\nEpoch Height " + epoch.getHeight() + " Info:");
				
				System.out.println(" Started At- " + epoch.getEpochStart() + " -vf " + WindowsSetSystemTime.convertTime(epoch.getEpochStart()));
				System.out.println(" Ended At- " + epoch.getEpochEnd() + " -vf " +  WindowsSetSystemTime.convertTime(epoch.getEpochEnd()));
				System.out.println(" Validator- " + epoch.getAddress());
				System.out.println(" Validator Fall Position- " + epoch.getValPosition());
				System.out.println(" Height- " + epoch.getHeight());
				System.out.println(" Next Stake Weighing Begins At- " + epoch.getNextStakeProcesingTime() + " -vf " + WindowsSetSystemTime.convertTime(epoch.getNextStakeProcesingTime()));
				
			}
			
		}
		
	}
	
	@Override
	public void run() {
		command();
	}
	
	public static void main(String args[]) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		new CommandLine(new Epoch()).execute(args);

	}
	
}
