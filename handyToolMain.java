/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handyXMLTool;

/**
 *
 * @author kkell
 */

public class handyToolMain {

    /**
     * @param args the command line arguments
     */
    /**
     * @param args the command line arguments
     */
    public static void showMainPage() {
        javax.swing.JFrame mainDisplay = new javax.swing.JFrame();

        javax.swing.JPanel xMain = new pnlXRev();

        mainDisplay.getContentPane().add(xMain);
        
        mainDisplay.pack();
    
        
        mainDisplay.setVisible(true);
    }
    
    
    public static void main(String[] args) {
         javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                showMainPage();
            }
        });    

    }
    
}
