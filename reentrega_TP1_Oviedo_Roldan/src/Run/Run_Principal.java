package Run;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import menu.Menu;

public class Run_Principal {
	

	public static void main(String[] args) {
		 EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                try {
	                    Menu frame = new Menu();
	                    frame.setVisible(true);
	                    frame.setResizable(false);
	                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	                    frame.setVisible(true);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        });
	}

}
