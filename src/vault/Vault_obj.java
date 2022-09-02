package vault;

import java.io.Serializable;
import java.math.BigDecimal;
import java.security.PublicKey;

import temp.Static;

public class Vault_obj implements Serializable{
	String vaultAddress;
	BigDecimal vaultCoins;
	PublicKey publicKey;
	long nonce;
	
	public Vault_obj(String vaultAddress,BigDecimal vaultCoins, PublicKey publicKey,long nonce) {
		this.vaultAddress = Static.MAINTENANCE_VAULT;
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
		// TODO Auto-generated method stub
		return nonce;
	}
	
}
