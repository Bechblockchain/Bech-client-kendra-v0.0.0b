package transc.createTx;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import SEWS_Protocol.StakeObj;
import crypto.BytesToFro;
import temp.Static;
import transc.crypto.Hasher;
import transc.mod.Ctx;
import wallets.db.Retrie;
import wallets.db.Str;
import wallets.mod.Acc_obj;

public class ClearObsoleteStakeAcc {

	public static Ctx createObsoleteStakeAccClearanceTx(StakeObj stakeobj,String validatorCoinAddress) throws IOException {
		Ctx transaction = null;
		long timestamp = System.currentTimeMillis();
		BigDecimal stakedCoins = stakeobj.getStakeCoins();
		String stakerAddress = stakeobj.getAddress();
		BigDecimal coinsToBeReversed = stakedCoins;
	
		//Validator(reporter) account data
		Acc_obj validatorAccData = Retrie.retrieveAccData(validatorCoinAddress);
		
		PublicKey validatorPublicKey = validatorAccData.getPubkey();
	    long validatorCtxNonce = validatorAccData.getCtxNonce();
     	long validatorPtxNonce = validatorAccData.getPtxNonce();
     	BigDecimal validatorCoinBalance = validatorAccData.getCoinBalance();
     	String validatorMintAddress = validatorAccData.getMintAddress();
     	HashMap<String,BigInteger> validatorTradesNbalances = validatorAccData.getTradesNbalances();
		
     	byte[] coinsToBeReversedBytes = BytesToFro.convertBigDecimalToByteArray(coinsToBeReversed);
     	long stakeClearanceTxNonce = validatorCtxNonce + 1L;
     	byte[] stakeClearanceTxNonceNonceBytes = BytesToFro.convertLongToBytes(stakeClearanceTxNonce);
		
     	PrivateKey validatorPrivateKey = Retrie.retrievePrivateKeyWithPublicKey(validatorPublicKey);
     
			byte[] TxSig = Hasher.generateSenderCoinSignature(validatorPrivateKey, validatorCoinAddress, stakerAddress, validatorPublicKey, coinsToBeReversed, stakeClearanceTxNonce, timestamp, Static.OBSOLETE_STAKE_CLEARANCE);
			
			byte[] validatorAddress = BytesToFro.convertStringToByteArray(validatorCoinAddress);
			byte[] stakerAddressBytes = BytesToFro.convertStringToByteArray(stakerAddress);
			byte[] txtimestamp = BytesToFro.convertLongToBytes(timestamp);
			byte[] range = BytesToFro.convertStringToByteArray(Static.OBSOLETE_STAKE_CLEARANCE);
			
			Acc_obj Account = new Acc_obj(validatorCoinAddress,validatorMintAddress,stakeClearanceTxNonce,validatorPtxNonce,validatorCoinBalance,validatorPublicKey,validatorTradesNbalances);
			Str.storeSingleAccData(Account);
			
			transaction = new Ctx(validatorAddress, stakerAddressBytes, coinsToBeReversedBytes, txtimestamp, stakeClearanceTxNonceNonceBytes, TxSig, range);
			
		return transaction;
		
	}	
	
	
}
