import java.net.ServerSocket;
import java.net.*;
import java.io.*;
import java.util.*;

class ip_web
{

	public void update_ip(String s)
	{
		String content = null;
		URLConnection connection = null;
		try {
  				connection =  new URL("http://www.google.com/sendip?"+s).openConnection();
  				Scanner scanner = new Scanner(connection.getInputStream());
 				scanner.useDelimiter("\\Z");
  				content = scanner.next();
			}
		catch ( Exception ex ) {
    			ex.printStackTrace();
			}
	}		
}	