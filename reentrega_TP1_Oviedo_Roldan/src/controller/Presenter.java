package controller;

import interfaz.Interfaz;
import juego.Juego;
import menu.Menu_dificultad;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JMenuItem;

public class Presenter implements ActionListener {
    private Juego juego;
    private Interfaz view;
    private String dificultad, personaje;

    public Presenter(Juego juego, Interfaz view, String dif, String pers) {
        this.juego = juego;
        this.view = view; // Asignar la view correctamente
        this.dificultad = dif;
        this.personaje = pers;

        iniciarJuego();
        functionMenu();
        
        functionTimer();
        
        blockGame();
        
        // Configuración del KeyListener para el panel de juego
        view.getBoardPanel().setFocusable(true);
        view.getBoardPanel().requestFocusInWindow();
        view.getBoardPanel().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                moverBlanco(e.getKeyCode());
            }
        });

        // Configuración de los listeners para los botones de la interfaz
        view.setArrowButtonListener(this); // Registrar el listener de botones
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        switch (actionCommand) {
            case "up":
                moverBlanco(KeyEvent.VK_UP);
                break;
            case "down":
                moverBlanco(KeyEvent.VK_DOWN);
                break;
            case "left":
                moverBlanco(KeyEvent.VK_LEFT);
                break;
            case "right":
                moverBlanco(KeyEvent.VK_RIGHT);
                break;
            default:
                break;
        }
        view.getBoardPanel().requestFocusInWindow(); // Volver a enfocar el panel
    }
   
    private void moverBlanco(int keyCode) {
        // Comprobar si la tecla es una de las flechas o WASD
        switch (keyCode) {
            case KeyEvent.VK_UP: // Flecha arriba
            case KeyEvent.VK_W: // Tecla W
                juego.moverBlanco(KeyEvent.VK_UP);
                break;
            case KeyEvent.VK_DOWN: // Flecha abajo
            case KeyEvent.VK_S: // Tecla S
                juego.moverBlanco(KeyEvent.VK_DOWN);
                break;
            case KeyEvent.VK_LEFT: // Flecha izquierda
            case KeyEvent.VK_A: // Tecla A
                juego.moverBlanco(KeyEvent.VK_LEFT);
                break;
            case KeyEvent.VK_RIGHT: // Flecha derecha
            case KeyEvent.VK_D: // Tecla D
                juego.moverBlanco(KeyEvent.VK_RIGHT);
                break;
            default:
                break;
        }
        updateMovimientos(); // Actualizar el conteo de movimientos si es necesario
    }
  
    private void blockGame() {
        view.btnPause.addActionListener(e -> {
            juego.finishedGame(false); // Bloquear el tablero
            view.setisPause(true); // Actualizar el estado de pausa en la vista
        });

        view.btnPlay.addActionListener(e -> {
            juego.finishedGame(true); // Desbloquear el tablero
            view.setisPause(false); // Actualizar el estado de pausa en la vista
        });
    }

    private void functionMenu() {
        view.addMenuItemClickListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                JMenuItem item = (JMenuItem) event.getSource();
                handleMenuItemClick(item);
            }
        });
    }
    
    private void functionTimer() {
        view.startTime(() -> {
            juego.gameOver(); // Lógica cuando el tiempo acaba
        });
    }
	
    public void resetTimer(String dificultad) {
        view.stopTimer(); // Detener el temporizador actual
        view.setTime(dificultad); // Reiniciar el tiempo según la dificultad
        view.setTimerText(view.formatearTiempo(view.getTimepoRestante())); // Actualizar la etiqueta del temporizador
        functionTimer(); // Iniciar el temporizador de nuevo
        juego.setPersonaje(personaje);
    }

    // Lógica para manejar las opciones del menú
    private void handleMenuItemClick(JMenuItem item) {
        if (item.getText().equals("Resetear")) {
            this.iniciarJuego();
            view.updateViewBoardPanel(); // Actualizar el panel del juego
            resetTimer(dificultad); // Resetear el temporizador con la dificultad actual
            view.setMovimientosText("0"); // Reiniciar el contador de movimientos
        } else if (item.getText().equals("Deshacer")) {
            juego.undoMove(); // Deshacer el último movimiento
            view.updateViewBoardPanel(); // Actualizar el panel del juego
            view.setMovimientosText(String.valueOf(juego.getMovimientos())); // Actualizar los movimientos
        } else if (item.getText().equals("Volver")) {
            view.closeWindow();
            volverMenu(); // Lógica para volver al menú
        } else {
            // no es necesario
        }
    }

    private void volverMenu() {
        Menu_dificultad md = new Menu_dificultad(personaje);
        md.setVisible(true);
    }
    
    public void iniciarJuego() {
        juego.setDificultad(this.dificultad);
        juego.setPersonaje(this.personaje);
        if (view.getBoardPanel() != null) {
            juego.inicializar(view.getBoardPanel(), view); // Pasar la instancia actual de Interfaz
            view.updateViewBoardPanel(); // Asegúrate de que el panel se actualice
            updateMovimientos();
            view.setMovimientosText(String.valueOf(juego.getMovimientos())); // Actualizar el conteo de movimientos
        }
    }
    
    private void updateMovimientos() {
        view.setMovimientosText(Integer.toString(juego.getMovimientos()));
    }
}
