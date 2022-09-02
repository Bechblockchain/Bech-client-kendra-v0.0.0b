package cli.Txs;

import java.io.IOException;
import java.security.Security;

import connect.Network;
import connect.Mod.Request;
import db.db_retrie;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import temp.Static;

@Command( name = "txFinality", mixinStandardHelpOptions = true, requiredOptionMarker = '*' ,description = "\nChecks finality state of transaction")
public class Txfinality implements Runnable {

	@Option(names = { "-h", "--hash" }, required = true,description = "Hex of transaction signature")
	String txSigHex;
	
	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		new CommandLine(new Txfinality()).execute(args);
	}
	
	public void checkFinality() {
		try {
			String blocknum = db_retrie.getCoinTxIndex(txSigHex);
			if(blocknum == null) {
				Request req = new Request("getTxFinality",txSigHex); 
				Network.sendNetworkRequest(req);
			}else {
				if(Long.parseLong(Static.PREV_BLOCK_NUM) > Long.parseLong(blocknum) + 5) {
					System.out.println("Transaction is in block " + blocknum + " and has reached finality");
				}else{
					System.out.println("Transaction is in block " + blocknum + " and waiting to reach finality");
				}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		checkFinality();
	}
	
	
	
}
