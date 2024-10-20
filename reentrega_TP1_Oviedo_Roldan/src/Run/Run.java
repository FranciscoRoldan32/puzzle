package Run;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import juego.Juego;
import controller.Presenter;
import interfaz.Interfaz;

public class Run {
    private static String dificultad,personaje;

    public static void setDificultad(String dificultad) {
        Run.dificultad = dificultad;
    }

    public static void setPersonaje(String personaje) {
        Run.personaje = personaje;
    }

    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Juego juego = new Juego(dificultad,personaje);

                Interfaz vista = new Interfaz(dificultad,personaje,juego.getDim());
                vista.setResizable(false);
                vista.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                vista.setVisible(true);

                Presenter presenter = new Presenter(juego, vista,dificultad,personaje);
                vista.setVisible(true);
                presenter.iniciarJuego();
            }
        });
    }
}
