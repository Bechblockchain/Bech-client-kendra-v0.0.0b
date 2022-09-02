package transc.createTx;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import crypto.BytesToFro;
import penalty.Report;
import penalty.thePunisher;
import penalty.thePunisherAcc;
import temp.Static;
import transc.crypto.Hasher;
import transc.mod.Ctx;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

public class PenaltyTx {

	public static Ctx createPenaltyTx(Report report, BigDecimal confiscatedRewards) throws IOException {
		Ctx transaction = null;
		long timestamp = System.currentTimeMillis();
		
		String validatorCoinAddress = report.getReporterAddress();
		String felonCoinAddress = report.getSuspectAddress();
		
		//felon punishment data
		thePunisher punishmentData = thePunisherAcc.retrieveThePunisherAcc(felonCoinAddress);
		BigDecimal fines = punishmentData.getFines();
		List<Report> judgedReports = punishmentData.getReports();
		long nonce = punishmentData.getPenaltyNonce();
		
		//Validator(reporter) account data
		Acc_obj accData = Retrie.retrieveAccData(validatorCoinAddress);
		
		PublicKey senderPublicKey = accData.getPubkey();
	   
     	byte[] ValueBytes = BytesToFro.convertBigDecimalToByteArray(confiscatedRewards);
     	byte[] reportBytes = BytesToFro.convertObjToByteArray(report);
     	long penaltyTxNonce = nonce + 1L;
     	byte[] penaltyTxNonceBytes = BytesToFro.convertLongToBytes(penaltyTxNonce);
		
     	PrivateKey senderPrivateKey = Retrie.retrievePrivateKeyWithPublicKey(senderPublicKey);
     
     	thePunisher newPunishement = new thePunisher("the_punisher",felonCoinAddress,fines,judgedReports,penaltyTxNonce);
		thePunisherAcc.storePunishmentNonce(newPunishement);
	
			byte[] TxSig = Hasher.generateSenderCoinSignature(senderPrivateKey, validatorCoinAddress, felonCoinAddress, senderPublicKey, confiscatedRewards, penaltyTxNonce, timestamp, report, Static.PENALTY_RANGE);
			
			byte[] validatorAddress = BytesToFro.convertStringToByteArray(validatorCoinAddress);
			byte[] felonAddress = BytesToFro.convertStringToByteArray(felonCoinAddress);
			byte[] txtimestamp = BytesToFro.convertLongToBytes(timestamp);
			byte[] range = BytesToFro.convertStringToByteArray(Static.PENALTY_RANGE);
			
			transaction = new Ctx(validatorAddress, felonAddress, ValueBytes, txtimestamp, penaltyTxNonceBytes, TxSig, reportBytes, range);
			
		return transaction;
		
	}	
	
}
