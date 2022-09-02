package penalty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class thePunisher implements Serializable {

	String punisherAddress = "the_punisher";
	BigDecimal negBalance = new BigDecimal(0);
	String felonAddress;
	String punishment;
	List<Report> reports;
	Report report;
	long penaltyNonce;
	
	public thePunisher(String punisherAddress,String felonAddress,BigDecimal negBalance,List<Report> reports,long penaltyNonce) {
		this.punisherAddress = "the_punisher";
		this.negBalance = negBalance;
		this.felonAddress = felonAddress;
		this.reports = reports;
		this.penaltyNonce = penaltyNonce;
	}
	
	public thePunisher(String punisherAddress,String felonAddress,BigDecimal negBalance, Report report,long penaltyNonce) {
		this.punisherAddress = "the_punisher";
		this.negBalance = negBalance;
		this.felonAddress = felonAddress;
		this.report = report;
		this.penaltyNonce = penaltyNonce;
	}

	public String getFelonAddress() {
		// TODO Auto-generated method stub
		return felonAddress;
	}
	
	public List<Report> getReports() {
		// TODO Auto-generated method stub
		return reports;
	}
	
	public Report getReport() {
		// TODO Auto-generated method stub
		return report;
	}

	public BigDecimal getFines() {
		// TODO Auto-generated method stub
		return negBalance;
	}

	public long getPenaltyNonce() {
		// TODO Auto-generated method stub
		return penaltyNonce;
	}
	
}
