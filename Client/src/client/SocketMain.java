/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.net.ServerSocket;
import java.net.*;
import java.io.*;
import java.util.*;

class SocketMain
{
	/** Define a host server */
    String host;
    /** Define a port */
    int port;
	Socket connection ;
	OutputStreamWriter osw;
        InputStreamReader isr;
 
	
	SocketMain(int port, String host) {
		this.port = port;
		this.host = host;
	}
	public boolean connect()
	{

    try {
      /** Obtain an address object of the server */
      InetAddress address = InetAddress.getByName(host);
      if(!address.isReachable(1000))
          return false;
      /** Establish a socket connection */
      connection = new Socket(address, port);
      /** Instantiate a BufferedOutputStream object */
      BufferedOutputStream bos = new BufferedOutputStream(connection.
          getOutputStream());
      isr = new InputStreamReader(connection.getInputStream());
      connection.setSoTimeout(500);

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
   
  public String getNextTag() {
        
        int c;
        
        try{
            StringBuffer sb = new StringBuffer();
            while((c=isr.read())>=0) {
                
                if((char)c=='\r' || (char)c=='\n')
                {
                     String s = sb.toString().trim();
                    if(s.startsWith("[[") && s.endsWith("]]"))
                         return s.substring(2,s.length()-2);
                     sb = new StringBuffer();
                
                }
                else
                sb.append((char)c);
                
            }
                    
        
        }catch(SocketTimeoutException ste) {
            return null;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        
        return null;
        
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
