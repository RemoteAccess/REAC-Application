import java.util.*;
import javax.swing.*;
import java.io.*;

class serverUI
{
	public static void main(String[] args)
	{
		String n=JOptionPane.showInputDialog("Enter the current Password");
		String file="passwd.txt";
		try 
    	 {
    	 	BufferedReader reader =	new BufferedReader(new FileReader(file));
    		String line = null;
    		if((line = reader.readLine()) != null) {
        	if(n.equals(line))
        	{
        		String o=JOptionPane.showInputDialog("Enter the new Password");

        		try
        		{	
        			FileWriter f = new FileWriter(file);
           			f.write(o);
           			f.close();
        			JOptionPane.showMessageDialog(null,"Password changed successfully");
        		}
        		catch(IOException e)
        		{
        			e.printStackTrace();
        		}		


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