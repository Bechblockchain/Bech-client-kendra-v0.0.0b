package vault.db;

import db.paths.db;
import temp.Static;
import vault.BackUpVault;
import vault.Vault_obj;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;

public class vault_retrie {

	///// Retrieve maintenance vault Data
	public static Vault_obj retrieveMaintenanceVaultData() {
		Vault_obj vaultData = null;
    	EasyRam accDB = new EasyRam(db.MAINTENANCE_VAULT);
		try {
   			accDB.createStore("maintenanceVault", DataStore.Storage.PERSISTED,1);
   			vaultData = (Vault_obj) accDB.getObject("maintenanceVault", Static.MAINTENANCE_VAULT);
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
        return vaultData;
	   
	}
	
	///// Retrieve e-rebase backup vault Data
	public static BackUpVault retrieveBackUpVaultData() {
		
      	BackUpVault vaultData = null;
    	EasyRam accDB = new EasyRam(db.BACKUP_VAULT);
		try {
   			accDB.createStore("BackUpVault", DataStore.Storage.PERSISTED,1);
   			vaultData = (BackUpVault) accDB.getObject("BackUpVault", Static.BACKUP_VAULT);
   		} catch (Exception e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
      	return vaultData;
      	
	}
	
}
