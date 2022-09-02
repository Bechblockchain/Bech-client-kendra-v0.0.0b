package cli.SEWS;

import java.math.BigDecimal;
import java.security.Security;

import SEWS_Protocol.db.retrieve;
import Util.WindowsSetSystemTime;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command( name = "getStakeObj", mixinStandardHelpOptions = true, requiredOptionMarker = '*' ,description = "\nGet stake obj from SEWS protocol")
public class GetStakeObj implements Runnable{

	@Option(names = { "-o", "--object" }, required = true, description = "Address of stake object")
	static
	String param;
	
	@Option(names = { "-p", "--param" }, description = "Parameter to return")
	static
	String request;

	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		new CommandLine(new GetStakeObj()).execute(args);
	}
	
	public void getStakeObj() {
		
		if(request.equals("stakeCoins")) {
			BigDecimal stakeCoins = retrieve.retrieveSingleStakeData(param).getStakeCoins();
			System.out.println(" Coins in stake object- " + stakeCoins);
			
		}else if(request.equals("stakeTimestamp")) {
			long lastStakingTimestamp =retrieve.retrieveSingleStakeData(param).getTimestamp();
			System.out.println(" Last stake timestamp- " + lastStakingTimestamp + " -vf " + WindowsSetSystemTime.convertTime(lastStakingTimestamp));
			
		}else if(request.equals("lastValTime")) {
			long lastValTimestamp =retrieve.retrieveSingleStakeData(param).getLastValidationTimestamp();
			System.out.println(" Last epoch validation timestamp- " + lastValTimestamp + " -vf " + WindowsSetSystemTime.convertTime(lastValTimestamp));
			
		}else if(request.equals("lastValStatus")) {
			String  lastValStatus = retrieve.retrieveSingleStakeData(param).getLastValidationStatus();
			System.out.println(" Last epoch validation status- " + lastValStatus);
			
		}else {
			System.out.println("\nStake Object: ");
			
			String  address = retrieve.retrieveSingleStakeData(param).getAddress();
			BigDecimal stakeCoins = retrieve.retrieveSingleStakeData(param).getStakeCoins();
			long lastStakingTimestamp =retrieve.retrieveSingleStakeData(param).getTimestamp();
			long lastValTimestamp =retrieve.retrieveSingleStakeData(param).getLastValidationTimestamp();
			String lastValStatus = retrieve.retrieveSingleStakeData(param).getLastValidationStatus();
			
			System.out.println(" Stake Address- " + address);
			System.out.println(" Coins in stake object- " + stakeCoins);
			System.out.println(" Last stake timestamp- " + lastStakingTimestamp + " -vf " + WindowsSetSystemTime.convertTime(lastStakingTimestamp));
			System.out.println(" Last epoch validation status- " + lastValStatus);
			System.out.println(" Last epoch validation timestamp- " + lastValTimestamp + " -vf " + WindowsSetSystemTime.convertTime(lastValTimestamp));
		} 
	}
	
	@Override
	public void run() {
			getStakeObj();
	}
	
	
	
}
