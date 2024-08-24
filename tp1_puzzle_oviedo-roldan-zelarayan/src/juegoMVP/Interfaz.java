package juegoMVP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Interfaz extends JFrame implements ActionListener, KeyListener {

    private Juego juego;
    private JLabel lblMovimientos;
    private JLabel lblTiempo;
    private Timer timer;
    private int segundos = 0;
    private JPanel boardPanel; 
    

    public Interfaz() {
        juego = new Juego();
        setTitle("Puzzle 15 - MVP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel lblNewLabel = new JLabel("Cantidad movimientos:");
        topPanel.add(lblNewLabel);

        lblMovimientos = new JLabel("0");
        topPanel.add(lblMovimientos);

        JLabel lblNewLabel_1 = new JLabel("Tiempo:");
        topPanel.add(lblNewLabel_1);

        lblTiempo = new JLabel("0:00");
        topPanel.add(lblTiempo);

        JButton btnReset = new JButton("Resetear");
        btnReset.setForeground(new Color(255, 255, 0));
        btnReset.setHorizontalAlignment(SwingConstants.RIGHT);
        btnReset.setBackground(new Color(0, 0, 255));
        topPanel.add(btnReset);
        btnReset.addActionListener(this);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        boardPanel = new JPanel(new GridLayout(Juego.getDim(), Juego.getDim()));
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        juego.inicializar(boardPanel, this);
        startTimer();
        
        //teclas
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    public JPanel getBoardPanel() {
        return boardPanel;
    }

    public void updateBoardPanel() {
        boardPanel.removeAll(); // Limpiar el panel
        for (int i = 0; i < Juego.getDim(); i++) {
            for (int j = 0; j < Juego.getDim(); j++) {
                boardPanel.add(juego.getButtonAt(i, j)); // Agregar cada botón al panel
            }
        }
        boardPanel.revalidate(); // Asegúrate de que el panel se actualice
        boardPanel.repaint();
    }


    public void setMovimientosText(String text) {
        lblMovimientos.setText(text);
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            segundos++;
            int minutos = segundos / 60;
            int segs = segundos % 60;
            lblTiempo.setText(String.format("%d:%02d", minutos, segs));
        });
        timer.start();
    }

    private void resetTimer() {
        if (timer != null) {
            timer.stop();
        }
        segundos = 0;
        lblTiempo.setText("0:00");
        startTimer();
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        JButton buttonPressed = (JButton) event.getSource();

        if (buttonPressed.getText().equals("Resetear")) {
            juego.inicializar(boardPanel, this); // Pasar la instancia actual de Interfaz
            updateBoardPanel();
            resetTimer();
            setMovimientosText("0"); // Reiniciar el conteo de movimientos
        } else {
            int index = juego.indexOf(buttonPressed.getText());
            if (index == -1) {
                throw new IllegalArgumentException("Index should be between 0-15");
            }
            int row = index / Juego.getDim();
            int column = index % Juego.getDim();

            if (juego.makeMove(row, column)) {
            	juego.incrementoMovimientos();
            	setMovimientosText(String.valueOf(juego.getMovimientos())); // Actualizar el conteo de movimientos
                //updateBoardPanel(); // Actualizar el panel de juego
            	if (juego.isFinished()) {
                    timer.stop();
                    juego.playWinSound();
                    JOptionPane.showMessageDialog(null, "¡Has ganado el juego!");
                }
            }
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        boolean moveSuccessful = false;

        switch (keyCode) {
            case KeyEvent.VK_DOWN: // Flecha abajo o 'S'
            case KeyEvent.VK_S:
                moveSuccessful = juego.makeMove(juego.getEmptyCellRow() + 1, juego.getEmptyCellColumn());
                break;
            case KeyEvent.VK_UP: // Flecha arriba o 'W'
            case KeyEvent.VK_W:
                moveSuccessful = juego.makeMove(juego.getEmptyCellRow() - 1, juego.getEmptyCellColumn());
                break;
            case KeyEvent.VK_LEFT: // Flecha izquierda o 'A'
            case KeyEvent.VK_A:
                moveSuccessful = juego.makeMove(juego.getEmptyCellRow(), juego.getEmptyCellColumn() - 1);
                break;
            case KeyEvent.VK_RIGHT: // Flecha derecha o 'D'
            case KeyEvent.VK_D:
                moveSuccessful = juego.makeMove(juego.getEmptyCellRow(), juego.getEmptyCellColumn() + 1);
                break;
        }

        if (moveSuccessful) {
            //lblMovimientos.setText(String.valueOf(juego.getMovimientos()));
        	//juego.incrementoMovimientos();
        	setMovimientosText(String.valueOf(juego.getMovimientos()));
            if (juego.isFinished()) {
                timer.stop();
                juego.playWinSound();
                JOptionPane.showMessageDialog(this, "¡Has ganado el juego!");
            }
        }
    }

    
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
    

}