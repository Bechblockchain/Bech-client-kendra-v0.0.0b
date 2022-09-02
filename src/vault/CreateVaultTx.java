package vault;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.PrivateKey;
import java.security.PublicKey;

import crypto.BytesToFro;
import db.paths.db;
import temp.Static;
import transc.mod.Ctx;
import vault.db.vault_retrie;
import vault.db.vault_store;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;

public class CreateVaultTx {

	public static Ctx createVaultTx(String FromAddress, String ToAddress, BigDecimal value) throws IOException {
		String range = Static.VAULT_RANGE;
		Ctx transaction = null;
		long timestamp = System.currentTimeMillis();
		BigDecimal vaultCoins = null;
		long nonce = 0;
		PublicKey vaultPublicKey = null;
		BigDecimal VaultCoins = null;
		
		if(FromAddress.equals(Static.BACKUP_VAULT)) {
			BackUpVault backUpVault = vault_retrie.retrieveBackUpVaultData();
			VaultCoins = backUpVault.getVaultCoins();
			vaultPublicKey = backUpVault.getPublicKey();
			nonce = backUpVault.getNonce();
			
			
		}else if(FromAddress.equals(Static.MAINTENANCE_VAULT)) {
			Vault_obj maintenanceVault = vault_retrie.retrieveMaintenanceVaultData();
			vaultCoins = maintenanceVault.getVaultCoins();
			vaultPublicKey = maintenanceVault.getPublicKey();
			nonce = maintenanceVault.getNonce();
		}
     	byte[] ValueBytes = BytesToFro.convertBigDecimalToByteArray(value);
     	PrivateKey vaultPrivateKey = returnteKey(FromAddress);
     	
		if(value.compareTo(vaultCoins) >= 0) {
			
			long newNonce  = nonce + 1; 
			
			if(FromAddress.equals(Static.BACKUP_VAULT)) {
				vault_store.storeBackUpVaultData(new BackUpVault(Static.BACKUP_VAULT,vaultCoins,vaultPublicKey,newNonce));
			}else {
				vault_store.storeMaintenanceVaultData(new Vault_obj(Static.MAINTENANCE_VAULT,vaultCoins,vaultPublicKey,newNonce));
			}
			
			
			byte[] TxSig = generateVaultSignature(vaultPrivateKey, FromAddress, ToAddress, vaultPublicKey, value, newNonce, timestamp, range);
			
			byte[] senderAddress = BytesToFro.convertStringToByteArray(FromAddress);
			byte[] receiverAddress = BytesToFro.convertStringToByteArray(ToAddress);
			byte[] txtimestamp = BytesToFro.convertLongToBytes(timestamp);
			byte[] txNonce = BytesToFro.convertLongToBytes(newNonce);
			
			byte[] rangebytes = BytesToFro.convertStringToByteArray(FromAddress);
			
			
			transaction = new Ctx(senderAddress, receiverAddress, ValueBytes, txtimestamp, txNonce, TxSig, rangebytes);
			
		}else{
			System.out.println("Low Balance"); 
		}
		return transaction;
		
	}	
	

	public static PrivateKey returnteKey(String vault) {
		
       PrivateKey Key = null;
       EasyRam accDB = new EasyRam(db.PUB_PRIV_PATH);
		try {
   			accDB.createStore("vaultpubpriv", DataStore.Storage.PERSISTED,1);
   			Key = (PrivateKey) accDB.getObject("vaultpubpriv", vault);
   		} catch (Exception e) {
   			e.printStackTrace();
   		}
       return Key;
		     
	}
	
	
	public static void storeKey(String vault, PrivateKey privatekey) {
	
		try {
			 EasyRam accDB = new EasyRam(db.PUB_PRIV_PATH);
			 accDB.createStore("vaultpubpriv", DataStore.Storage.PERSISTED,1);
			 accDB.putObject("vaultpubpriv",vault, privatekey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//Generate Vault transaction signature
	public static byte[] generateVaultSignature(PrivateKey privateKey, String FromAddress, String ToAddress, PublicKey senderPublicKey, BigDecimal value, long Nonce, long timestamp, String range) {
		String data = FromAddress + ToAddress + transc.crypto.Hasher.getStringFromKey(senderPublicKey) + ((value).setScale(2, RoundingMode.HALF_EVEN).toString()) + Long.valueOf(Nonce) + Long.toString(timestamp) + range;
		return transc.crypto.Hasher.applyECDSASig(privateKey,data);		
	}

	
}
