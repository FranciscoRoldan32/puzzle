package juego;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Interfaz extends JFrame implements ActionListener {

    private Juego juego;
    private JLabel lblMovimientos;
    private JLabel lblTiempo;
    private Timer timer;
    private int segundos = 0;

    public Interfaz() {
        juego = new Juego();
        setTitle("Puzzle 15");
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

        JPanel boardPanel = new JPanel(new GridLayout(Juego.getDim(), Juego.getDim()));
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        juego.inicializar(boardPanel, lblMovimientos, lblTiempo);
        startTimer();
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
            juego.inicializar((JPanel) getContentPane().getComponent(1), lblMovimientos, lblTiempo);
            resetTimer();
        } else {
            int index = juego.indexOf(buttonPressed.getText());
            if (index == -1) {
                throw new IllegalArgumentException("Index should be between 0-15");
            }
            int row = index / Juego.getDim();
            int column = index % Juego.getDim();
            if (juego.makeMove(row, column)) {
                lblMovimientos.setText(String.valueOf(juego.getMovimientos()));

                if (juego.isFinished()) {
                    timer.stop();
                    juego.playWinSound();
                    JOptionPane.showMessageDialog(null, "¡Has ganado el juego!");
                }
            }
        }
    }

}
