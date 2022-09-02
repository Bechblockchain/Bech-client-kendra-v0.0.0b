package connect;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class IpsForNewClients {

	public static String returnIP() throws IOException {
		URL ur = new URL("https://serveip.testchain.repl.co/");
		URLConnection conn = ur.openConnection();
		InputStream is = conn.getInputStream();
		@SuppressWarnings("resource")
		String ip = new Scanner(is).useDelimiter("\\A").next();
		System.out.println("IP from serveip::  " + ip + "\n");
		return ip;
		
	}
	
	
}
