
import java.net.ServerSocket;
import java.net.*;
import java.io.*;
class SocketMain
{
	/** Define a host server */
    String host;
    /** Define a port */
    int port;
	Socket connection ;
	OutputStreamWriter osw;
	
	SocketMain(int port, String host) {
		this.port = port;
		this.host = host;
	}
	public boolean connect()
	{

    try {
      /** Obtain an address object of the server */
      InetAddress address = InetAddress.getByName(host);
      /** Establish a socket connetion */
      connection = new Socket(address, port);
      /** Instantiate a BufferedOutputStream object */
      BufferedOutputStream bos = new BufferedOutputStream(connection.
          getOutputStream());

      /** Instantiate an OutputStreamWriter object with the optional character
       * encoding.
       */
      osw = new OutputStreamWriter(bos, "US-ASCII");

      
      return true;
      
     }
    
    catch (Exception g) {
      System.out.println("Exception: " + g);
    }

    return false;
  }

  public void startShell() {
  	try
  	{
	  	osw.write("START_SHELL\r\n");
	  	osw.flush();
  	} catch(IOException e) {
  		System.out.println(e.toString());
  	}
  }

  public void sendMsg(String msg) {
 	try
  	{
	  	osw.write(msg);
	  	osw.flush();
 	} catch(IOException e) {
  		System.out.println(e.toString());
  	}
   }

  public void close() {
  	/** Close the socket connection. */
     try
  	{
		connection.close();
	} catch(IOException e) {
  		System.out.println(e.toString());
  	}
  
  }
}

class SocketReceiver extends Thread
{
	ServerSocket sSocket;
	int port;
	REAC ref;
	SocketReceiver(REAC ref, int port) {
		this.port = port;
		this.ref = ref;
	}
	public void run()
	{

		try {
      	
      	sSocket = new ServerSocket(port);
		Socket socket = sSocket.accept();

		InputStream is = socket.getInputStream();
		int r;
		while((r=is.read())>=0) {
			System.out.print((char)r);
			ref.appendMsg(""+(char)r);
		}


        }
      	catch (Exception g) {
      System.out.println("Exception: " + g);
    }	
    }

}