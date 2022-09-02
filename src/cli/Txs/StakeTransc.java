package cli.Txs;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Security;

import Transc_Util.Verify_Tx;
import Util.WindowsSetSystemTime;
import connect.Network;
import crypto.BytesToFro;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import temp.Holder;
import temp.Static;
import transc.createTx.StakeTx;
import transc.mod.Ctx;

@Command(name = "-createStakeTx", mixinStandardHelpOptions = true, requiredOptionMarker = '*' ,description = "\nExecutes a stake transaction if stake obj exists or automatically creates a stake object if it doesn't exist in SEWS object pool")
public class StakeTransc implements Runnable{

	@Option(names = { "-f", "--fromAddress" }, required = true ,description = "Account address to sent coins from...enter 'native'(optional) if staking account")
	static
	String FromCoinAddress; 
	
	@Option(names = { "-a", "--amount" }, required = true,description = "Amount of coins to be sent")
	static
	BigDecimal value;
	

	@Option(names = { "-r", "--range" }, required = true,description = "Type of stake transaction to be executed 'stake/unstake'")
	static
	String range;
	
	
	public static void createCoinTx() throws IOException {
	
			Ctx ctx = StakeTx.createStakeTx(FromCoinAddress,value,range);
			
			if(ctx != null) {
				try {
					if(Verify_Tx.verifyCtx(ctx, 0)) {

						System.out.println("\nFrom Address- " + BytesToFro.convertByteArrayToString(ctx.getFromAddress()));
						System.out.println("Amount- " + BytesToFro.convertBytesToBigDecimal(ctx.getValue()));
						System.out.println("To Address- " + BytesToFro.convertByteArrayToString(ctx.getToAddress()));
						System.out.println("Fee- " + Static.FEE);
						System.out.println("Tx Signature- " + BytesToFro.bytesToHex(ctx.getTxSig()));
						System.out.println("S Tx Nonce- " + BytesToFro.convertBytesToLong(ctx.getNonce()));
						System.out.println("Range- " + BytesToFro.convertByteArrayToString(ctx.getRange()));
						System.out.println("Timestamp- " + BytesToFro.convertBytesToLong(ctx.getTimestamp()) + " -vf " + WindowsSetSystemTime.convertTime(BytesToFro.convertBytesToLong(ctx.getTimestamp()))+ "\n");
						
						Holder.addStakeTranc2Holder(ctx);
						
						Network.broadcastCoinTx(ctx);
						System.out.println("Stake transaction signed and sent to network!");
						
					}else {
						System.out.println("\nTransaction failed!");
						
					}
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		new CommandLine(new StakeTransc()).execute(args);
	}

	@Override
	public void run() {
		try {
			createCoinTx();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
