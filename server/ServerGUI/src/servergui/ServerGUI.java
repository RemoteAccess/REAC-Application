/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servergui;


/**
 *
 * @author scopeinfinity
 */
public class ServerGUI {

    public static final String rootPath = "./";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Home home = new Home();
        home.setVisible(true);
        home.setTitle("Remote Access [Server Side]");
        
    }
    
}
