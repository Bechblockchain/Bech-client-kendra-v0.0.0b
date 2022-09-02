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
import transc.createTx.CoinTx;
import transc.mod.Ctx;

@Command(name = "-createCoinTx", mixinStandardHelpOptions = true , requiredOptionMarker = '*' ,description = "\nCreates and executes a coin transaction")
public class Transactions implements Runnable{

	@Option(names = { "-f", "--fromAddress" }, required = true ,description = "Account address to sent coins from...enter 'native'(optional) if staking account")
	static
	String FromCoinAddress; 
	
	@Option(names = { "-t", "--toAddress" }, required = true,description = "Account address of recipient...enter 'native'(optional) if staking account")
	static
	String ToCoinAddress;
	
	@Option(names = { "-a", "--amount" }, required = true,description = "Amount of coins to be sent")
	static
	BigDecimal value;
	
	
	public static void createCoinTx() {
		try {
			Ctx ctx = CoinTx.createCoinTx(FromCoinAddress,ToCoinAddress,value);
			
			if(ctx != null) {
				if(Verify_Tx.verifyCtx(ctx, 0)) {

					System.out.println("\nFrom Address- " + BytesToFro.convertByteArrayToString(ctx.getFromAddress()));
					System.out.println("Amount- " + BytesToFro.convertBytesToBigDecimal(ctx.getValue()));
					System.out.println("To Address- " + BytesToFro.convertByteArrayToString(ctx.getToAddress()));
					System.out.println("Fee- " + Static.FEE);
					System.out.println("Tx Signature- " + BytesToFro.bytesToHex(ctx.getTxSig()));
					System.out.println("S Tx Nonce- " + BytesToFro.convertBytesToLong(ctx.getNonce()));
					System.out.println("Range- " + BytesToFro.convertByteArrayToString(ctx.getRange()));
					System.out.println("Timestamp- " + BytesToFro.convertBytesToLong(ctx.getTimestamp()) + " -vf " + WindowsSetSystemTime.convertTime(BytesToFro.convertBytesToLong(ctx.getTimestamp())) + "\n");
				
					Holder.addCoinTranc2Holder(ctx);
					
					Network.broadcastCoinTx(ctx);
					System.out.println("Coin transaction signed and sent to network!");
				}else {
					System.out.println("\nTransaction failed!");
					
				}
			
			}
				
			
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		new CommandLine(new Transactions()).execute(args);
	}

	@Override
	public void run() {
		createCoinTx();
	}
	
}
