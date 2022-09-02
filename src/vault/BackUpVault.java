package vault;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.PublicKey;

import temp.Static;

public class BackUpVault implements Serializable{

	String vaultAddress;
	BigDecimal vaultCoins;
	PublicKey publicKey;
	long nonce;
	
	public BackUpVault(String vaultAddress,BigDecimal vaultCoins, PublicKey publicKey,long nonce) {
		this.vaultAddress = Static.BACKUP_VAULT;
		this.vaultCoins = vaultCoins;
		this.publicKey = publicKey;
		this.nonce = nonce;
	}

	public String getVaultAddress() {
		return vaultAddress;
	}
	
	public BigDecimal getVaultCoins() {
		return vaultCoins;
	}
	
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	public long getNonce() {
		return nonce;
	}
	
}
