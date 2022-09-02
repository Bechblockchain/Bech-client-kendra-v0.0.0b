package connect.Util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpUtils;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;

import Util.WindowsSetSystemTime;
import temp.Holder;
import temp.Static;

public class NetworkTime {

	public static List<String> ntpList = new CopyOnWriteArrayList<>();
	static Timer timer = new Timer();
	
	public static boolean checkTimeOffset (long offset) {
		long clientTimestamp = System.currentTimeMillis();
		List<Long> offsetList = Holder.allTimestampOffset;
		Collections.sort(offsetList);
		long medianTimeOffset = 0;
		    if (Holder.allTimestampOffset.size() % 2 == 1) { 
		    	medianTimeOffset = offsetList.get((offsetList.size() + 1) / 2 - 1);
		    	
		    } else { // even
	    	    long lower = offsetList.get(offsetList.size() / 2 - 1);
	            long upper = offsetList.get(offsetList.size() / 2);

	            medianTimeOffset =  (lower + upper) / 2;
		    }
		    if(medianTimeOffset != 0) {
		    	 if(offset > medianTimeOffset + Static.NETWORK_TIME_OFFSET || offset < medianTimeOffset + Static.NETWORK_TIME_OFFSET){
				    	System.out.println("Peer clock not synced....terminate connection..."+ "\n");
				    	return false;
				    }else {
				    	long networkTime = clientTimestamp + medianTimeOffset;
					    Static.NETWORK_TIME = networkTime;
					    System.out.println("Peer clock synced....." + networkTime + "\n");
					    return true;
				    }
		    }else {
		    	 if(offset > Static.NETWORK_TIME_OFFSET || offset < Static.NETWORK_TIME_OFFSET){
				    	System.out.println("Peer clock not synced....terminate connection..."+ "\n");
				    	return false;
				    }else {
				    	long networkTime = clientTimestamp + medianTimeOffset;
					    Static.NETWORK_TIME = networkTime;
					    System.out.println("Peer clock synced....." + networkTime + "\n");
					    return true;
				    }
		    }
		    
		   
	}
	
	public static long processResponse(final TimeInfo info) {
        final NtpV3Packet message = info.getMessage();
        final int stratum = message.getStratum();
        final int version = message.getVersion();
        final int refId = message.getReferenceId();
        String refAddr = NtpUtils.getHostAddress(refId);
        String refName = null;
        
        if (refId != 0) {
            if (refAddr.equals("127.127.1.0")) {
                refName = "LOCAL"; 
            } else if (stratum >= 2) {
                if (!refAddr.startsWith("127.127")) {
                    try {
                        final InetAddress addr = InetAddress.getByName(refAddr);
                        final String name = addr.getHostName();
                        if (name != null && !name.equals(refAddr)) {
                            refName = name;
                        }
                    } catch (final UnknownHostException e) {
                        refName = NtpUtils.getReferenceClock(message);
                    }
                }
            } else if (version >= 3 && (stratum == 0 || stratum == 1)) {
                refName = NtpUtils.getReferenceClock(message);
            }
        }
        if (refName != null && refName.length() > 1) {
            refAddr += " (" + refName + ")";
        }

        info.computeDetails(); 
        final Long offsetMillis = info.getOffset();
        final Long delayMillis = info.getDelay();
        final String delay = delayMillis == null ? "N/A" : delayMillis.toString();
        final String offset = offsetMillis == null ? "N/A" : offsetMillis.toString();

     //   System.out.println(" Roundtrip delay(ms)=" + delay + ", clock offset(ms)=" + offset+ "\n"); 
     
        return Long.valueOf(offset);
	}
	    
	public static long getTimeOffset(){
    	final NTPUDPClient client = new NTPUDPClient();
        // We want to timeout if a response takes longer than 10 seconds
        Random rand = new Random();
        if(ntpList.size() == 0) {
        	addNtp();
        }
        long offset = 0;
        String host = ntpList.get(rand.nextInt(ntpList.size()));
        client.setDefaultTimeout(20000);
        try {
            client.open();
                System.out.println();
                try {
                    final InetAddress hostAddr = InetAddress.getByName(host);
                 //   System.out.println("> " + hostAddr.getHostName() + "/" + hostAddr.getHostAddress());
                    final TimeInfo info = client.getTime(hostAddr);
                    offset = processResponse(info);
                } catch (final IOException ioe) {
                    ioe.printStackTrace();
                }
        } catch (final SocketException e) {
            e.printStackTrace();
        }
        client.close();
        return offset;
	 }
	
	
	public static void checkSetTime(){
		long otime = 0;long offset = 0;long newTime = 0;

		  otime = System.currentTimeMillis();
	      offset =  NetworkTime.getTimeOffset();
			
		newTime = otime + offset;
		String[] array = String.valueOf(convertTime(newTime)).split("[.]");
		String time = array[0];
		String millisecond = array[1];
		
		
		String[] array1 = time.split("[:]");
		String time1 = array1[0];
		String minute = array1[1];
		String second = array1[2];
		
		String[] array2 = time1.split(" ");
		String year = array2[0];
		String month = array2[1];
		String day = array2[2];
		String hour = array2[3];
		
        if(Long.valueOf(offset) > Static.NTP_TIME_OFFSET || Long.valueOf(offset) < Static.NTP_TIME_OFFSET) {
        	WindowsSetSystemTime.SetLocalTime(Short.valueOf(year),Short.valueOf(month), Short.valueOf(day),Short.valueOf(hour),Short.valueOf(minute),Short.valueOf(second), Short.valueOf(millisecond));
      
        }
    }
	
	
	public static void addNtp() {
		ntpList.add("time-a-g.nist.gov");
		ntpList.add("time-b-g.nist.gov");
		ntpList.add("time-c-g.nist.gov");
		ntpList.add("time-d-g.nist.gov");
		ntpList.add("time-e-g.nist.gov");
		ntpList.add("time-a-wwv.nist.gov");
		ntpList.add("time-b-wwv.nist.gov");
		ntpList.add("time-c-wwv.nist.gov");
		ntpList.add("time-d-wwv.nist.gov");
		ntpList.add("time-e-wwv.nist.gov");
		ntpList.add("time.nist.gov");
		ntpList.add("time-d-b.nist.gov");
		ntpList.add("time-e-b.nist.gov");
		
		
		
	}
	
	public static String convertTime(long time){
	    Date date = new Date(time);
	    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss.SSS");
	    return format.format(date);
	}
	
}
