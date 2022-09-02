package cli;

import java.security.Security;
import java.util.Set;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command( name = "stop", mixinStandardHelpOptions = true, requiredOptionMarker = '*' ,description = "stops native node machine")
public class Stop implements Runnable {

	@Option(names = { "-st", "--stop" }, required = true,description = "stop native node machine, kill all processes and exit")
	String stop;
	
	public void killAllThreads() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		for (Thread t : threadSet) {
			t.interrupt();
	    }
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		killAllThreads();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		System.exit(new CommandLine(new Stop()).execute(args));
	}
	
}
