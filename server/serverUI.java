import java.util.*;
import javax.swing.*;
import java.io.*;

class serverUI
{
	public static void main(String[] args)
	{
		String n=JOptionPane.showInputDialog("Enter the Password");
		String file="passwd.txt";
		try 
    	 {
    	 	BufferedReader reader =	new BufferedReader(new FileReader(file));
    		String line = null;
    		if((line = reader.readLine()) != null) {
        	if(n.equals(line))
        	{
        		JOptionPane.showMessageDialog(null,"Welcome to REAC");
    		}
    		else
    		{
    			JOptionPane.showMessageDialog(null,"Incorrect Password");
    		}
			}
		}	
		 catch (IOException x) {
    	System.err.println(x);
		}
	}

}