package Blocks.Validate.Util.ReportVerification;

import java.io.IOException;
import java.security.PublicKey;

import crypto.BytesToFro;
import penalty.Report;
import transc.crypto.Hasher;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

public class VerifyPenalty {

	public static boolean verifyPenaltyDecision(Report report) throws IOException, ClassNotFoundException   {
		String type = report.getType();
		Acc_obj accData1 = Retrie.retrieveAccData(report.getReporterAddress());
      	PublicKey pubkey = accData1.getPubkey();
      	
      	
      	if(type.equals("ctx")) {
      		if(Hasher.verifiyReportSignature(pubkey,BytesToFro.convertStringToByteArray(report.getReportID()),report.getRequest(),report.getCrime(),report.getSuspectAddress(),report.getEpochHash(),report.getStakerPos(),report.getPrevValidatorAddress(),report.getCtxTx()) && !VerifyCtxPenalty.verifyCtxTxPenalty(report.getCtxTx(),report)) {
      	      	return true;
      	    }else {
      	    	return false;
      	    }
      	}//else if(type.equals("ptx")) {
      	//	if(Hasher.verifiyReportSignature(pubkey,BytesToFro.convertStringToByteArray(report.getReportID()),report.getRequest(),report.getCrime(),report.getSuspectAddress(),report.getEpochHash(),report.getStakerPos(),report.getPrevValidatorAddress(),report.getPtxTx()) && !verifyPtxTxPenalty(report.getPtxTx())) {
      	//		return true;
      	//    }else {
      	//    	return false;
      	//    }
     // 	}
      	else if(type.equals("coin_block")) {
      		if(Hasher.verifiyReportSignature(pubkey,BytesToFro.convertStringToByteArray(report.getReportID()),report.getRequest(),report.getCrime(),report.getSuspectAddress(),report.getEpochHash(),report.getStakerPos(),report.getPrevValidatorAddress(),report.getCoinBlock()) && !VerifyCoinBlockPenalty.verifyCoinBlockPenalty(report,report.getCoinBlock()) ) {
      	      	return true;
      	    }else {
      	    	return false;
      	    }
      	}else if(type.equals("stake_block")) {
      		if(Hasher.verifiyReportSignature(pubkey,BytesToFro.convertStringToByteArray(report.getReportID()),report.getRequest(),report.getCrime(),report.getSuspectAddress(),report.getEpochHash(),report.getStakerPos(),report.getPrevValidatorAddress(),report.getReporterResults(),report.getEpochCoinBlockHash(),report.getStakeBlock()) && !VerifyStakeBlockPenalty.verifyStakeBlockPenalty(report,report.getStakeBlock())) {
      	      	return true;
      	    }else {
      	    	return false;
      	    }
      	}
      	
		return false;
	}

	
}
