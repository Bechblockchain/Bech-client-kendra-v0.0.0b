package wallets.mod;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.HashMap;

public class Acc_obj implements Serializable{

	String coinAddress;
	String mintAddress;
	long ctxNonce;
	long ptxNonce;
	BigDecimal coinBalance;
	PublicKey pubkey;
	HashMap<String,BigInteger> tradesNbalances;

	
	public Acc_obj(String coinAddress, String mintAddress, long ctxNonce,long ptxNonce, BigDecimal coinBalance, PublicKey pubkey, HashMap<String,BigInteger> tradesNbalances) {
		this.coinAddress = coinAddress;
		this.mintAddress = mintAddress;
		this.ctxNonce = ctxNonce;
		this.ptxNonce = ptxNonce;
		this.coinBalance = coinBalance;
		this.pubkey = pubkey;
		this.tradesNbalances = tradesNbalances;
	}
	
	public String getCoinAddress() {
		return coinAddress;
	}
	public String getMintAddress() {
		return mintAddress;
	}
	
	public long getCtxNonce() {
		return ctxNonce;
	}
	
	public long getPtxNonce() {
		return ptxNonce;
	}
	
	public BigDecimal getCoinBalance() {
		return coinBalance;
	}
	
	public PublicKey getPubkey() {
		return pubkey;
	}
	public HashMap<String,BigInteger> getTradesNbalances() {
		return tradesNbalances;
	}
	
}
