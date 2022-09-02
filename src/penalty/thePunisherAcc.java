package penalty;

import java.util.List;
import db.paths.db;
import wallets.db.easyram.DataStore;
import wallets.db.easyram.EasyRam;

public class thePunisherAcc {

	public static thePunisher retrieveThePunisherAcc(String felonAddress) {
	      	thePunisher punisherData = null;
	      	
	      	EasyRam accDB = new EasyRam(db.THE_PUNISHER_DIR);
			try {
	   			accDB.createStore("ThePunisher", DataStore.Storage.PERSISTED,1);
	   			punisherData = (thePunisher) accDB.getObject("ThePunisher", felonAddress);
	   		} catch (Exception e) {
	   			// TODO Auto-generated catch block
	   			e.printStackTrace();
	   		}
	      	
	          return punisherData;
	   
	}
	
	public static void storePunishmentData(thePunisher Account) {
		 
		 try {
			 thePunisher felonAcc =  retrieveThePunisherAcc(Account.getFelonAddress());
	    	  
	    	   List<Report> reports = felonAcc.getReports();
	    	   reports.add(Account.getReport());
	    	   
	    	   thePunisher newPunishment = new thePunisher("the_punisher",felonAcc.getFelonAddress(),felonAcc.getFines(),reports,felonAcc.getPenaltyNonce());
	    	   
			 EasyRam accDB = new EasyRam(db.THE_PUNISHER_DIR);
			 accDB.createStore("ThePunisher", DataStore.Storage.PERSISTED,2);
			 accDB.putObject("ThePunisher",felonAcc.getFelonAddress(), newPunishment);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
	}
	
	
	public static void storeGehennaData(Report report) {
		 
		try {
			 thePunisher felonAcc =  retrieveThePunisherAcc(report.getSuspectAddress());
	    	 
			 EasyRam accDB = new EasyRam(db.THE_PUNISHER_DIR);
			 accDB.createStore("GehennaList", DataStore.Storage.PERSISTED,2);
			 accDB.putObject("GehennaList",felonAcc.getFelonAddress(), report);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	public static void storePunishmentNonce(thePunisher Account) {
		
		try {
			 thePunisher felonAcc =  retrieveThePunisherAcc(Account.getFelonAddress());
	    	 
			 EasyRam accDB = new EasyRam(db.THE_PUNISHER_DIR);
			 accDB.createStore("ThePunisher", DataStore.Storage.PERSISTED,2);
			 accDB.putObject("ThePunisher",felonAcc.getFelonAddress(), Account);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			 
	}
		
	public static void createPunishmentData(String felonAddress, thePunisher Account) {
		
		try {
			 EasyRam accDB = new EasyRam(db.THE_PUNISHER_DIR);
			 accDB.createStore("ThePunisher", DataStore.Storage.PERSISTED,2);
			 accDB.putObject("ThePunisher",felonAddress, Account);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			 
	}
	
}
