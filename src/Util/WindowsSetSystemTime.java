package Util;

import java.security.Security;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinBase.SYSTEMTIME;
import com.sun.jna.win32.StdCallLibrary;

import connect.Util.NetworkTime;

public class WindowsSetSystemTime {

    /**
     * Kernel32 DLL Interface. kernel32.dll uses the __stdcall calling
     * convention (check the function declaration for "WINAPI" or "PASCAL"), so
     * extend StdCallLibrary Most C libraries will just extend
     * com.sun.jna.Library,
     */
    public interface Kernel32 extends StdCallLibrary {

        boolean SetLocalTime(SYSTEMTIME st);
        void GetLocalTime(SYSTEMTIME st);
        Kernel32 instance = (Kernel32) Native.load("kernel32.dll", Kernel32.class);

    }

    public static boolean SetLocalTime(SYSTEMTIME st) {
        return Kernel32.instance.SetLocalTime(st);
    }
    
    public static void GetLocalTime(SYSTEMTIME st) {
         Kernel32.instance.GetLocalTime(st);
    }

    public static boolean SetLocalTime(short wYear, short wMonth, short wDay, short wHour, short wMinute, short wSecond, short wMilliseconds) {
        SYSTEMTIME st = new SYSTEMTIME();
        st.wYear = wYear;
        st.wMonth = wMonth;
        st.wDay = wDay;
        st.wHour = wHour;
        st.wMinute = wMinute;
        st.wSecond = wSecond;
        st.wMilliseconds = wMilliseconds;
        return SetLocalTime(st);
    }
    
    public static void GetLocalTime() {
    	 SYSTEMTIME st = new SYSTEMTIME();
        GetLocalTime(st);
        System.out.println("Day: " + st.wDay + "\n" + "Hour: " + st.wHour + "\n" + "Minute: " + st.wMinute);
    }
    
    public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		long otime =System.currentTimeMillis();
		long newTime = otime + NetworkTime.getTimeOffset();
		
		String[] array = String.valueOf(NetworkTime.convertTime(newTime)).split("[.]");
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
		
		System.out.println("System Time: " +NetworkTime.convertTime(otime) + "\n");
		System.out.println("Time after: " +NetworkTime.convertTime(newTime) + "\n");
		System.out.println("New constructed Time: " + "\n"+ "Year: " + year+ "\n" + " Month: " + month+ "\n" + " Day: " + day+ "\n" + " Hour: " + hour+ "\n"
				+ " Minute: " + minute+ "\n" + " Second: " + second+ "\n" + " MilliSecond: " + millisecond+ "\n" + "\n");
		System.out.println("New time: " + SetLocalTime(Short.valueOf(year),Short.valueOf(month), Short.valueOf(day),Short.valueOf(hour),Short.valueOf(minute),Short.valueOf(second), Short.valueOf(millisecond)));
		
	
		
	}
    public static String convertTime(long time){
	    Date date = new Date(time);
	    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss.SSS");
	    return format.format(date);
	}
    
}
