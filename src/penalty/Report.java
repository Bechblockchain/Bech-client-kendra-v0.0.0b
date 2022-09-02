package penalty;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import Blocks.db.Block_db;
import Blocks.mod.Block_obj;
import Blocks.mod.StakeBlock;
import SEWS_Protocol.weightResults;
import crypto.BytesToFro;
import temp.Static;
import transc.crypto.Hasher;
import transc.mod.Ctx;
import transc.mod.Ptx;
import wallets.db.Retrie;
import wallets.mod.Acc_obj;

public class Report implements Serializable{

	String reportID; String request; String crime; String reporterAddress; String suspectAddress; BigDecimal coinsClaimed; Block_obj block;StakeBlock stakeblock; Ctx ctx; Ptx ptx; String epochCoinBlockHash;List<weightResults>reporterResults;
	String type;
	String epochHash;
	BigInteger stakerPos;
	String prevValidator;
	List<String> stakeTxPoolSnap; List<String> processedStakeTx;
	public Report(String reportID,String request, String crime, String reporterAddress, String suspectAddress,Ctx ctx,String type,String stakeHash,BigInteger stakerPos,String prevValidator) {
		this.reportID = reportID;
		this.request = request;
		this.crime = crime;
		this.reporterAddress = reporterAddress;
		this.suspectAddress = suspectAddress;
		this.ctx = ctx;
		this.type = type;
		this.epochHash =stakeHash;
		this.stakerPos = stakerPos;
		this.prevValidator = prevValidator;
	}

	public Report(String reportID, String request, String crime, String reporterAddress, String suspectAddress,Ptx ptx,String type,String stakeHash,BigInteger stakerPos,String prevValidator ) {
		this.reportID = reportID;
		this.request = request;
		this.crime = crime;
		this.reporterAddress = reporterAddress;
		this.suspectAddress = suspectAddress;
		this.ptx = ptx;
		this.type = type;
		this.epochHash = stakeHash;
		this.stakerPos = stakerPos;
		this.prevValidator = prevValidator;
	}
	
	public Report(String reportID, String request, String crime, String reporterAddress, String suspectAddress,Block_obj block,String type,String stakeHash,BigInteger stakerPos, String prevValidator) {
		this.reportID= reportID;
		this.request = request;
		this.crime = crime;
		this.reporterAddress = reporterAddress;
		this.suspectAddress = suspectAddress;
		this.block = block;
		this.type = type;
		this.epochHash = stakeHash;
		this.stakerPos = stakerPos;
		this.prevValidator = prevValidator;
	}
	
	public Report(String reportID, String request, String crime, String reporterAddress, String suspectAddress, List<String> stakeTxPoolSnap, List<String> processedStakeTx,String type,String stakeHash,BigInteger stakerPos, String prevValidator, String epochCoinBlockHash) {
		this.reportID= reportID;
		this.request = request;
		this.crime = crime;
		this.reporterAddress = reporterAddress;
		this.suspectAddress = suspectAddress;
		this.processedStakeTx = processedStakeTx;
		this.stakeTxPoolSnap = stakeTxPoolSnap;
		this.type = type;
		this.epochHash = stakeHash;
		this.stakerPos = stakerPos;
		this.prevValidator = prevValidator;
		this.epochCoinBlockHash = epochCoinBlockHash;
	}

	public Report(String reportID, String request, String crime, String reporterAddress, String suspectAddress, StakeBlock stakeblock,String type,String stakeHash,BigInteger stakerPos, String prevValidator,String epochCoinBlockHash,List<weightResults>reporterResults) {
		this.reportID = reportID;
		this.request = request;
		this.crime = crime;
		this.reporterAddress = reporterAddress;
		this.suspectAddress = suspectAddress;
		this.stakeblock = stakeblock;
		this.type = type;
		this.epochHash = stakeHash;
		this.stakerPos = stakerPos;
		this.prevValidator = prevValidator;
		this.epochCoinBlockHash = epochCoinBlockHash;
		this.reporterResults = reporterResults;
	}
	
    public static Report createCoinBlockReport(String request, String crime, String reporterAddress, String suspectAddress,Block_obj block)  {
    	Acc_obj accData = Retrie.retrieveAccData(reporterAddress);
  		PublicKey reporterPublicKey = accData.getPubkey();
  		
  		List<weightResults> results = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash()).getProcessedStakes();
  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
  		Report report = null;
			for(weightResults weightresults: results) {
				if(weightresults.getStakeobj().getAddress().equals(suspectAddress)) {
					BigInteger position = weightresults.getFallPosition();
					PrivateKey reporterPrivateKey = Retrie.retrievePrivateKeyWithPublicKey(reporterPublicKey);
			  		String reportID = BytesToFro.convertByteArrayToString(Hasher.generateReportSignature(reporterPrivateKey,request, crime, suspectAddress ,Block_db.getLatestStakeBlockHash(),position,Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS, block));
			  		report = new Report(reportID, request, crime, reporterAddress, suspectAddress, block,"coin_block",Block_db.getLatestStakeBlockHash(),position,Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS);
			  		if(!crime.equals(Static.EPOCH_DISHONESTY)) {
						Static.EPOCH_VALIDATOR_ADDRESS = results.get((position).intValue() + 1).getStakeobj().getAddress();
				  		Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS = suspectAddress;
					}
			  		break;
				}
			}
  		return report;
	}
    	
	public static Report createCtxTxReport(String request, String crime, String reporterAddress, String suspectAddress,Ctx ctx ) {
		    Acc_obj accData = Retrie.retrieveAccData(reporterAddress);
			PublicKey reporterPublicKey = accData.getPubkey();
		   
			List<weightResults> results = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash()).getProcessedStakes();
	  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
	  		Report report = null;
	  			for(weightResults weightresults: results) {
					if(weightresults.getStakeobj().getAddress().equals(suspectAddress)) {
						BigInteger position = weightresults.getFallPosition();
						PrivateKey reporterPrivateKey = Retrie.retrievePrivateKeyWithPublicKey(reporterPublicKey);
						String reportID = BytesToFro.convertByteArrayToString(Hasher.generateReportSignature(reporterPrivateKey,request, crime, suspectAddress ,Block_db.getLatestStakeBlockHash(),position,Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS, ctx));
						report = new Report(reportID, request, crime, reporterAddress, suspectAddress, ctx,"ctx",Block_db.getLatestStakeBlockHash(),position,Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS);
						if(!crime.equals(Static.EPOCH_DISHONESTY)) {
							Static.EPOCH_VALIDATOR_ADDRESS = results.get((position).intValue() + 1).getStakeobj().getAddress();
					  		Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS = suspectAddress;
						}
				  		
				  		break;
					}
	  			}
			return report;
	}
	
	public static Report createPtxTxReport(String request, String crime, String reporterAddress, String suspectAddress,Ptx ptx) {
		  Acc_obj accData = Retrie.retrieveAccData(reporterAddress);
			PublicKey reporterPublicKey = accData.getPubkey();
		   
			List<weightResults> results = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash()).getProcessedStakes();
	  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
	  		Report report = null;
	  			for(weightResults weightresults: results) {
					if(weightresults.getStakeobj().getAddress().equals(suspectAddress)) {
						BigInteger position = weightresults.getFallPosition();
						PrivateKey reporterPrivateKey = Retrie.retrievePrivateKeyWithPublicKey(reporterPublicKey);
						String reportID = BytesToFro.convertByteArrayToString(Hasher.generateReportSignature(reporterPrivateKey,request, crime, suspectAddress ,Block_db.getLatestStakeBlockHash(),position,Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS, ptx));
						report = new Report(reportID, request, crime, reporterAddress, suspectAddress, ptx,"ptx",Block_db.getLatestStakeBlockHash(),position,Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS);
						if(!crime.equals(Static.EPOCH_DISHONESTY)) {
							Static.EPOCH_VALIDATOR_ADDRESS = results.get((position).intValue() + 1).getStakeobj().getAddress();
					  		Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS = suspectAddress;
						}
				  		break;
					}
	  			}
			return report;
	}
	
	public static Report createStakeBlockReport(String request, String crime, String reporterAddress, String suspectAddress,List<weightResults> reporterResults, String epochCoinBlockHash, StakeBlock block) {
        Acc_obj accData = Retrie.retrieveAccData(reporterAddress);
		PublicKey reporterPublicKey = accData.getPubkey();
	   
		List<weightResults> results = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash()).getProcessedStakes();
  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
  		Report report = null;
  			for(weightResults weightresults: results) {
				if(weightresults.getStakeobj().getAddress().equals(suspectAddress)) {
					BigInteger position = weightresults.getFallPosition();
					PrivateKey reporterPrivateKey = Retrie.retrievePrivateKeyWithPublicKey(reporterPublicKey);
					String reportID = BytesToFro.convertByteArrayToString(Hasher.generateReportSignature(reporterPrivateKey,request, crime, suspectAddress ,Block_db.getLatestStakeBlockHash(),position,Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS,reporterResults,epochCoinBlockHash, block));
					report = new Report(reportID, request, crime, reporterAddress, suspectAddress, block,"stake_block",Block_db.getLatestStakeBlockHash(),position,Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS,epochCoinBlockHash,reporterResults);
					if(!crime.equals(Static.EPOCH_DISHONESTY)) {
						Static.EPOCH_VALIDATOR_ADDRESS = results.get((position).intValue() + 1).getStakeobj().getAddress();
				  		Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS = suspectAddress;
					}
					break;
				}
  			}
		
		return report;
	}
	
	public static Report createStakeTxReport(String request, String crime, String reporterAddress, String suspectAddress,List<weightResults> reporterResults, String epochCoinBlockHash, List<String> stakeTxPoolSnap, List<String> processedStakeTx) throws IOException {
        Acc_obj accData = Retrie.retrieveAccData(reporterAddress);
		PublicKey reporterPublicKey = accData.getPubkey();
	   
		List<weightResults> results = Block_db.getSingleStakeBlockData(Block_db.getLatestStakeBlockHash()).getProcessedStakes();
  		results.sort((o1, o2) -> o1.getFallPosition().compareTo(o2.getFallPosition()));
  		Report report = null;
  			for(weightResults weightresults: results) {
				if(weightresults.getStakeobj().getAddress().equals(suspectAddress)) {
					BigInteger position = weightresults.getFallPosition();
					PrivateKey reporterPrivateKey = Retrie.retrievePrivateKeyWithPublicKey(reporterPublicKey);
					String reportID = BytesToFro.convertByteArrayToString(Hasher.generateReportSignature(reporterPrivateKey,request, crime, suspectAddress ,Block_db.getLatestStakeBlockHash(),position,Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS,reporterResults,epochCoinBlockHash, stakeTxPoolSnap,processedStakeTx));
					report = new Report(reportID, request, crime, reporterAddress, suspectAddress, stakeTxPoolSnap,processedStakeTx,"stake_tx_pool",Block_db.getLatestStakeBlockHash(),position,Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS,epochCoinBlockHash);
					if(!crime.equals(Static.EPOCH_DISHONESTY)) {
						Static.EPOCH_VALIDATOR_ADDRESS = results.get((position).intValue() + 1).getStakeobj().getAddress();
				  		Static.PREVIOUS_EPOCH_VALIDATOR_ADDRESS = suspectAddress;
					}
					break;
				}
  			}
		
		return report;
	}

	public String getReportID() {
		return reportID;
	}
	
	public String getRequest() {
		return request;
	}
	
	public String getCrime() {
		return crime;
	}
	
	public String getReporterAddress() {
		return reporterAddress;
	}
	
	public String getSuspectAddress() {
		return suspectAddress;
	}
	
	public BigDecimal getCoins() {
		return coinsClaimed;
	}
	
	public Ptx getPtxTx() {
		return ptx;
	}
	
	public Ctx getCtxTx() {
		return ctx;
	}
	
	public Block_obj getCoinBlock() {
		return block;
	}
	
	public String getType() {
		return type;
	}
	
	public StakeBlock getStakeBlock() {
		return stakeblock;
	}
	
	public String getEpochHash() {
		return epochHash;
	}
	
	public BigInteger getStakerPos() {
		return stakerPos;
	}

	public String getPrevValidatorAddress() {
		return prevValidator;
	}

	public List<weightResults> getReporterResults() {
		// TODO Auto-generated method stub
		return reporterResults;
	}
	
	public String getEpochCoinBlockHash() {
		return epochCoinBlockHash;
	}

	public List<String> getStakeTxPoolSnap() {
		// TODO Auto-generated method stub
		return stakeTxPoolSnap;
	}

	public List<String> getProcessedStakeTxSnap() {
		// TODO Auto-generated method stub
		return processedStakeTx;
	}
	
}
