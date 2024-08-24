package juegoMVP;

import javax.swing.SwingUtilities;

public class Run {
    public static void main(String[] args) {
    	SwingUtilities.invokeLater(new Runnable() {
    	            @Override
    	            public void run() {
    	                Juego juego = new Juego(); //m
    	                Interfaz vista = new Interfaz();//v
    	                Presenter presenter = new Presenter(juego, vista);//p

    	                vista.setVisible(true);
    	                presenter.iniciarJuego();
    	            }
    	        });
    	    }
}
