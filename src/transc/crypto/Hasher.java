package transc.crypto;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.List;

import Blocks.mod.Block_obj;
import Blocks.mod.StakeBlock;
import SEWS_Protocol.weightResults;
import Transc_Util.Merkle_tree;
import crypto.BytesToFro;
import penalty.Report;
import transc.mod.Ctx;
import transc.mod.Ptx;

public class Hasher {
	//Applies Sha256 to a string and returns the result. 
	public static String applySha256(String input){
		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        
			//Applies sha256 to our input, 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
	        
			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}	
	
	//Applies ECDSA coin signature
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}
	
	
	public static byte[] applyECDSASig(PrivateKey privateKey, byte[] strByte) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
		//	byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}
	
	
	//Verifies a ECDSA coin signature 
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
		

	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	
	public static byte[] generateValidatorSignature(PrivateKey privateKey, byte[] data) {
		return Hasher.applyECDSASig(privateKey,data);	
	}
	
	public static boolean verifiyValidatorSignature(PublicKey validatorPublicKey, String data, byte[] signature ) {
		return Hasher.verifyECDSASig(validatorPublicKey, data, signature);
	}
	
	//Generate buy order pack transaction signature
	public static byte[] generateOrderBuyerPackSignature(PrivateKey privateKey, String buyerCoinAddress, BigInteger packs, PublicKey senderPublicKey, BigDecimal value, long pTxNonce, long timestamp, String stat,String Data,String type) {
		String data = buyerCoinAddress + String.valueOf(packs) + Hasher.getStringFromKey(senderPublicKey) + ((value).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(pTxNonce) + Long.toString(timestamp) + stat + Data + type;
		return Hasher.applyECDSASig(privateKey,data);		
	}
	
	//Verify buy order pack signature
	public static boolean verifiyOrderBuyerPackSignature(String buyerCoinAddress, BigInteger packs, PublicKey senderPublicKey, BigDecimal value, long pTxNonce, long timestamp, byte[]signature,String stat,String Data, String type) {
		String data =  buyerCoinAddress + String.valueOf(packs) + Hasher.getStringFromKey(senderPublicKey) + ((value).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(pTxNonce) + Long.toString(timestamp) + stat + Data + type  ;
		return Hasher.verifyECDSASig(senderPublicKey, data, signature);
	}
	
	//Generate sell order pack transaction signature
	public static byte[] generateSellOrderPackSignature(PrivateKey privateKey, String buyerCoinAddress, BigInteger packs, PublicKey senderPublicKey, BigDecimal value, long pTxNonce, long timestamp,String type) {
		String data = buyerCoinAddress + String.valueOf(packs) + Hasher.getStringFromKey(senderPublicKey) + ((value).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(pTxNonce) + Long.toString(timestamp) + type;
		return Hasher.applyECDSASig(privateKey,data);		
	}
	
	//Verify buy order pack signature
	public static boolean verifiySellOrderPackSignature(String sellerCoinAddress, BigInteger packs, PublicKey senderPublicKey, BigDecimal value, long pTxNonce, long timestamp, byte[]signature, String type) {
		String data =  sellerCoinAddress + String.valueOf(packs) + Hasher.getStringFromKey(senderPublicKey) + ((value).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(pTxNonce) + Long.toString(timestamp) + type ;
		return Hasher.verifyECDSASig(senderPublicKey, data, signature);
	}
		
	//Generate pack execution transaction signature
	public static byte[] generatePackExecTxSignature(PrivateKey privateKey, String sellerCoinAddress, String buyerCoinAddress, BigInteger packs, PublicKey senderPublicKey, BigDecimal value, long pTxNonce, long timestamp,String type) {
		String data = sellerCoinAddress + buyerCoinAddress + String.valueOf(packs) + Hasher.getStringFromKey(senderPublicKey) + ((value).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(pTxNonce) + Long.toString(timestamp) + type;
		return Hasher.applyECDSASig(privateKey,data);		
	}
	
	//Verify pack execution signature
	public static boolean verifiyPackExecTxSignature(String sellerCoinAddress, String buyerCoinAddress, BigInteger packs, PublicKey senderPublicKey, BigDecimal value, long pTxNonce, long timestamp, byte[]signature,String type) {
		String data =  sellerCoinAddress + buyerCoinAddress + String.valueOf(packs) + Hasher.getStringFromKey(senderPublicKey) + ((value).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(pTxNonce) + Long.toString(timestamp) + type;
		return Hasher.verifyECDSASig(senderPublicKey, data, signature);
	}
		
	
	
	//Generate coin transaction signature
	public static byte[] generateSenderCoinSignature(PrivateKey privateKey, String FromAddress, String ToAddress, PublicKey senderPublicKey, BigDecimal value, long Nonce, long timestamp, String range) {
		String data = FromAddress + ToAddress + Hasher.getStringFromKey(senderPublicKey) + ((value).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(Nonce) + Long.toString(timestamp) + range;
		return Hasher.applyECDSASig(privateKey,data);		
	}

	//Verify coin signature
	public static boolean verifiyCoinSignature(String FromAddress, String ToAddress, PublicKey senderPublicKey, BigDecimal value, long Nonce, long timestamp, byte[] signature,String range) {
		String data = FromAddress + ToAddress + Hasher.getStringFromKey(senderPublicKey) + ((value).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(Nonce) + Long.toString(timestamp) + range;
		return Hasher.verifyECDSASig(senderPublicKey, data, signature);
	}
	
	//Generate report transaction signature
	public static byte[] generateSenderCoinSignature(PrivateKey privateKey, String FromAddress, String ToAddress, PublicKey senderPublicKey, BigDecimal value, long Nonce, long timestamp, Report report, String range) {
		String data = FromAddress + ToAddress + Hasher.getStringFromKey(senderPublicKey) + ((value).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(Nonce) + Long.toString(timestamp) + String.valueOf(report) + range;
		return Hasher.applyECDSASig(privateKey,data);		
	}
	
	//Verify report signature
	public static boolean verifiyCoinSignature(String FromAddress, String ToAddress, PublicKey senderPublicKey, BigDecimal value, long Nonce, long timestamp, byte[] signature,Report report,String range) {
		String data = FromAddress + ToAddress + Hasher.getStringFromKey(senderPublicKey) + ((value).setScale(2, RoundingMode.HALF_EVEN).toString()) +   Long.valueOf(Nonce) + Long.toString(timestamp) + String.valueOf(report) +  range;
		return Hasher.verifyECDSASig(senderPublicKey, data, signature);
	}
	
	//Generate reward transaction signature
	public static byte[] generateSenderCoinSignature(PrivateKey privateKey, String FromAddress, String ToAddress, PublicKey senderPublicKey, BigDecimal totalEpochReward,BigDecimal coinRewardAfterPartition,BigDecimal stakeRewardAfterPartition,BigDecimal vaultRatio, BigDecimal eachStaker50Partition, long Nonce, long timestamp, long epochNum, String epochHash, String range) {
		String data = FromAddress + ToAddress + Hasher.getStringFromKey(senderPublicKey) + ((totalEpochReward).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((coinRewardAfterPartition).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((stakeRewardAfterPartition).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((vaultRatio).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((eachStaker50Partition).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(Nonce) + Long.toString(timestamp) + String.valueOf(epochNum) + epochHash + range;
		return Hasher.applyECDSASig(privateKey,data);		
	}
		
	//Verify reward signature
	public static boolean verifiyCoinSignature(String FromAddress, String ToAddress, PublicKey senderPublicKey, BigDecimal totalEpochReward,BigDecimal coinRewardAfterPartition,BigDecimal stakeRewardAfterPartition, BigDecimal vaultRatio, BigDecimal eachStaker50Partition, long Nonce, long timestamp, byte[] signature,long epochNum, String epochHash,String range) {
		String data = FromAddress + ToAddress + Hasher.getStringFromKey(senderPublicKey) + ((totalEpochReward).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((coinRewardAfterPartition).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((stakeRewardAfterPartition).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((vaultRatio).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((eachStaker50Partition).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(Nonce) + Long.toString(timestamp) +  String.valueOf(epochNum) + epochHash + range;
		return Hasher.verifyECDSASig(senderPublicKey, data, signature);
	}
	
	
	//Generate reward transaction signature
	public static byte[] generateSenderCoinSignature(PrivateKey privateKey, String FromAddress, String ToAddress, PublicKey senderPublicKey, BigDecimal totalEpochReward,BigDecimal coinRewardAfterPartition,BigDecimal stakeRewardAfterPartition,BigDecimal vaultRatio, long Nonce, long timestamp, long epochNum, String epochHash, String range) {
		String data = FromAddress + ToAddress + Hasher.getStringFromKey(senderPublicKey) + ((totalEpochReward).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((coinRewardAfterPartition).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((stakeRewardAfterPartition).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((vaultRatio).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(Nonce) + Long.toString(timestamp) + String.valueOf(epochNum) + epochHash + range;
		return Hasher.applyECDSASig(privateKey,data);		
	}
		
	//Verify reward signature
	public static boolean verifiyCoinSignature(String FromAddress, String ToAddress, PublicKey senderPublicKey, BigDecimal totalEpochReward,BigDecimal coinRewardAfterPartition,BigDecimal stakeRewardAfterPartition, BigDecimal vaultRatio, long Nonce, long timestamp, byte[] signature,long epochNum, String epochHash,String range) {
		String data = FromAddress + ToAddress + Hasher.getStringFromKey(senderPublicKey) + ((totalEpochReward).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((coinRewardAfterPartition).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((stakeRewardAfterPartition).setScale(2, RoundingMode.HALF_EVEN).toString()) + ((vaultRatio).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(Nonce) + Long.toString(timestamp) +  String.valueOf(epochNum) + epochHash + range;
		return Hasher.verifyECDSASig(senderPublicKey, data, signature);
	}
		
	
	
	//////////////////////////////////////////////// Report /////////////////////////////////////////////////////////
	
	//Generate stake block report signature
	public static byte[] generateReportSignature(PrivateKey reporterPrivateKey,String request, String crime, String suspectAddress,String Hash,BigInteger position,String previousValidatorAddress,List<weightResults> reporterResults, String epochCoinBlockHash, StakeBlock stakeblock) {
		String data = request + crime + suspectAddress + Hash + position + previousValidatorAddress + reporterResults.toString() + epochCoinBlockHash + stakeblock.getHash();
		return Hasher.applyECDSASig(reporterPrivateKey,data);	
	}
	
	//Verify stake block report signature
	public static boolean verifiyReportSignature(PublicKey reporterPublicKey,byte[]signature,String request, String crime, String suspectAddress,String Hash,BigInteger position,String previousValidatorAddress,List<weightResults> reporterResults, String epochCoinBlockHash, StakeBlock stakeblock) {
		String data = request + crime + suspectAddress + Hash + position + previousValidatorAddress + reporterResults.toString() + epochCoinBlockHash + stakeblock.getHash();
		return Hasher.verifyECDSASig(reporterPublicKey, data, signature);
	}
	
	//Generate stake txs ratio report signature
	public static byte[] generateReportSignature(PrivateKey reporterPrivateKey,String request, String crime, String suspectAddress,String Hash,BigInteger position,String previousValidatorAddress,List<weightResults> reporterResults, String epochCoinBlockHash, List<String> stakeTxPoolSnap, List<String> processedStakeTx) throws IOException {
		String data = request + crime + suspectAddress + Hash + position + previousValidatorAddress + reporterResults.toString() + epochCoinBlockHash + Merkle_tree.getStringMerkleRoot(stakeTxPoolSnap)+ Merkle_tree.getStringMerkleRoot(processedStakeTx);
		return Hasher.applyECDSASig(reporterPrivateKey,data);	
	}
		
	//Verifystake txs ratio report signature
	public static boolean verifiyReportSignature(PublicKey reporterPublicKey,byte[]signature,String request, String crime, String suspectAddress,String Hash,BigInteger position,String previousValidatorAddress,List<weightResults> reporterResults, String epochCoinBlockHash, List<String> stakeTxPoolSnap, List<String> processedStakeTx) throws IOException{
		String data = request + crime + suspectAddress + Hash + position + previousValidatorAddress + reporterResults.toString() + epochCoinBlockHash +  Merkle_tree.getStringMerkleRoot(stakeTxPoolSnap)+ Merkle_tree.getStringMerkleRoot(processedStakeTx);
		return Hasher.verifyECDSASig(reporterPublicKey, data, signature);
	}
	
	//Generate coin block report signature
	public static byte[] generateReportSignature(PrivateKey reporterPrivateKey,String request, String crime, String suspectAddress,String Hash,BigInteger position,String previousValidatorAddress, Block_obj block) {
		String data = request + crime + suspectAddress + Hash + position + previousValidatorAddress + block.getHash();
		return Hasher.applyECDSASig(reporterPrivateKey,data);	
	}
		
	//Verify coin block report signature
	public static boolean verifiyReportSignature(PublicKey reporterPublicKey,byte[]signature,String request, String crime, String suspectAddress,String Hash,BigInteger position,String previousValidatorAddress, Block_obj block) {
		String data = request + crime + suspectAddress + Hash + position + previousValidatorAddress + block.getHash();
		return Hasher.verifyECDSASig(reporterPublicKey, data, signature);
	}
		
	//Generate ctx report signature
	public static byte[] generateReportSignature(PrivateKey reporterPrivateKey,String request, String crime, String suspectAddress,String Hash,BigInteger position,String previousValidatorAddress, Ctx ctx) {
		String data = request + crime + suspectAddress + Hash + position + previousValidatorAddress + BytesToFro.convertByteArrayToString(ctx.getTxSig());
		return Hasher.applyECDSASig(reporterPrivateKey,data);	
	}
		
	//Verify ctx report signature
	public static boolean verifiyReportSignature(PublicKey reporterPublicKey,byte[]signature,String request, String crime, String suspectAddress,String Hash,BigInteger position,String previousValidatorAddress, Ctx ctx) {
		String data = request + crime + suspectAddress + Hash + position + previousValidatorAddress + BytesToFro.convertByteArrayToString(ctx.getTxSig());
		return Hasher.verifyECDSASig(reporterPublicKey, data, signature);
	}
		
	//Generate ptx report signature
	public static byte[] generateReportSignature(PrivateKey reporterPrivateKey,String request, String crime, String suspectAddress,String Hash,BigInteger position,String previousValidatorAddress, Ptx ptx) {
		String data = request + crime + suspectAddress + Hash + position + previousValidatorAddress + BytesToFro.convertByteArrayToString(ptx.getSignature());
		return Hasher.applyECDSASig(reporterPrivateKey,data);	
	}
		
	//Verify ptx report signature
	public static boolean verifiyReportSignature(PublicKey reporterPublicKey,byte[]signature,String request, String crime, String suspectAddress,String Hash,BigInteger position,String previousValidatorAddress, Ptx ptx) {
		String data = request + crime + suspectAddress + Hash + position + previousValidatorAddress + BytesToFro.convertByteArrayToString(ptx.getSignature());
		return Hasher.verifyECDSASig(reporterPublicKey, data, signature);
	}
}
