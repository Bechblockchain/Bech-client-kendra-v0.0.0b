package cli;

import java.security.Security;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import wallets.crypto.Hasher;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

@Command( name = "getAccount", mixinStandardHelpOptions = true, requiredOptionMarker = '*' ,description = "\nget account variables")
public class GetAccount implements Runnable{

	@Option(names = { "-g", "--get" }, required = true, description = "create new account...add 'native' to set as staking account")
	String get;
	
	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		new CommandLine(new GetAccount()).execute(args);
	}

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		execute();
	}

	

	public void execute() {
		if(get.equals("native")) {
		
			Acc_obj acc;
			acc = Retrie.retrieveAccData(Retrie.retrieveNativeValidatorAddress());
			System.out.println("Native validator account:" );
			printData(acc);
				
		}else {
			
			Acc_obj acc = Retrie.retrieveAccData(get);
			printData(acc);
			
		}
		
	}
	
	
	public void printData(Acc_obj acc) {
		System.out.println(" Coin Address- " + acc.getCoinAddress());
		System.out.println(" Mint Address- " + acc.getMintAddress());
		System.out.println(" Coin Nonce- " + acc.getCtxNonce());
		System.out.println(" Pack Nonce- " + acc.getPtxNonce());
		System.out.println(" Account Balance- " + acc.getCoinBalance());
		System.out.println(" Coin Address-" + Hasher.returnPublicKeyString(acc.getPubkey()) + "\n");
		
	}
	
}
