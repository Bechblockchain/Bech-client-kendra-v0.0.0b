package threads.Tx;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import Transc_Util.Verify_Tx;
import connect.Network;
import crypto.BytesToFro;
import temp.Holder;
import temp.Static;
import transc.mod.Ctx;

public class CoinTxThread implements Runnable{

	public static LinkedBlockingQueue<Ctx> newCtx = new LinkedBlockingQueue<Ctx>();
	
	@Override
	public void run() {
		while(true) {
			long epochConfirmationTime = Static.EPOCH_TIMESTAMP + Static.PREVIOUS_VALIDATOR_CONFIRMATION_TIME.longValue();
			try {
				Ctx ctx = newCtx.take();
				if(Verify_Tx.verifyCtx(ctx,epochConfirmationTime)){
					Network.broadcastCoinTx(ctx);
					if(BytesToFro.convertByteArrayToString(ctx.getToAddress()).equals(Static.STAKE_OBJ)) {
						
						Holder.addStakeTranc2Holder(ctx);
					}else {
						Holder.addCoinTranc2Holder(ctx);
					}
					}
			} catch (InterruptedException | ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				
				
		}
		
	}

	
}
