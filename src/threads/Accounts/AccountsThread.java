package threads.Accounts;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import connect.Network;
import wallets.db.Str;
import wallets.mod.Acc_obj;

public class AccountsThread implements Runnable{

	public static LinkedBlockingQueue<Acc_obj> newAccount = new LinkedBlockingQueue<Acc_obj>();	
	
	
	@Override
	public void run() {
		while(true) {
					
			try {
				Acc_obj account = newAccount.take();
				
				String coinAddress = account.getCoinAddress();
				String mintAddress = account.getMintAddress();
				long ctxNonce = 0;
				long ptxNonce = 0;
				BigDecimal coinBalance = new BigDecimal(0);
				PublicKey pubkey = account.getPubkey();
				HashMap<String,BigInteger> tradesNbalances = account.getTradesNbalances();
				tradesNbalances.clear();
				Acc_obj newAccount = new Acc_obj(coinAddress,mintAddress,ctxNonce,ptxNonce,coinBalance,pubkey,tradesNbalances);
				Str.storeSingleAccData(newAccount);
			
			    Network.broadcastNewAcount(newAccount);
					
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				
		}
		
	}

}
