/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.FileWriter;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 *
 * @author scopeinfinity
 */

class SocketReceiver extends Thread
{
	ServerSocket sSocket;
	int port;
        Main main;
        private String content="";
	SocketReceiver(Main main,int port) {
		this.port = port;
                this.main = main;
	}
	public void run()
	{

            try {
      	
            	sSocket = new ServerSocket(port);
		Socket socket = sSocket.accept();
                socket.setSoTimeout(500);
		InputStream is = socket.getInputStream();
		int r;
                while(true){
                    StringBuffer sb = new StringBuffer();
                    try{
                        while((r=is.read())>=0) {
                               sb.append((char)r);
                        }


                    }catch(SocketTimeoutException exp) {
                       // exp.printStackTrace();
                    }
                    String out = sb.toString();
                    if(out.trim().isEmpty())
                        continue;


                        if(main.editFile)
                        {
                            
                            main.editFileContent(out);
                        }
                        else
                        {
                                          System.out.print(out);
                                          main.appendMsg(out);
                        }


                }
        }
      	catch (Exception g) {
            System.out.println("Exception: " + g);
        }	
        }

}

