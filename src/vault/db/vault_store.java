package vault.db;

import db.paths.db;
import temp.Static;
import vault.BackUpVault;
import vault.Vault_obj;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;

public class vault_store {

	
/////Store E-rebase backup vault account data 
	public static void storeBackUpVaultData(BackUpVault vault) {
	
		try {
			 EasyRam accDB = new EasyRam(db.BACKUP_VAULT);
			 accDB.createStore("BackUpVault", DataStore.Storage.PERSISTED,2);
			 accDB.putObject("BackUpVault",Static.BACKUP_VAULT, vault);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
///// Store Maintenance Vault Account Data	
	public static void storeMaintenanceVaultData(Vault_obj vault) {
		
		try {
			 EasyRam accDB = new EasyRam(db.MAINTENANCE_VAULT);
			 accDB.createStore("maintenanceVault", DataStore.Storage.PERSISTED,2);
			 accDB.putObject("maintenanceVault",Static.MAINTENANCE_VAULT, vault);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
}
