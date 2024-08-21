package JuegoStreetFighter;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Interfaz extends JFrame implements ActionListener {

    private int movimientos = 0;
    private JLabel lblMovimientos;
    private JLabel lblTiempo;
    private static final int DIM = 4;
    private static final int SIZE = DIM * DIM;
    final String[] WIN = new String[SIZE - 1];
    private int emptyCell = DIM * DIM;
    private JButton[][] board = new JButton[DIM][DIM];
    private Timer timer; // Timer para manejar el tiempo.
    private int segundos = 0;

    public Interfaz() {
        setTitle("Puzzle 15 - Street Fighter edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        // Cargar y establecer el ícono de la ventana
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/logo2.png"));
        setIconImage(icon.getImage());

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

        JPanel boardPanel = new JPanel(new GridLayout(DIM, DIM));
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        for (int i = 1; i < SIZE; i++) {
            WIN[i - 1] = Integer.toString(i);
        }

        setContentPane(mainPanel);

        inicializar();
        startTimer(); // Comenzar el temporizador.
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
        startTimer(); // Reiniciar el temporizador.
    }

    public void inicializar() {
        JPanel boardPanel = (JPanel) getContentPane().getComponent(1);
        boardPanel.removeAll(); // Limpiar el tablero.

        ArrayList<Integer> initialList = new ArrayList<>(SIZE);
        for (boolean isSolvable = false; !isSolvable;) {
            initialList.clear();
            for (int i = 0; i < SIZE; i++) {
                initialList.add(i);
            }
            Collections.shuffle(initialList);
            isSolvable = isSolvable(initialList);
        }

        for (int index = 0; index < SIZE; index++) {
            final int ROW = index / DIM;
            final int COL = index % DIM;
            board[ROW][COL] = new JButton(String.valueOf(initialList.get(index)));

            if (initialList.get(index) == 0) {
                emptyCell = index;
                board[ROW][COL].setVisible(false);
            }

            board[ROW][COL].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            board[ROW][COL].setBackground(Color.BLACK);
            board[ROW][COL].setForeground(Color.GREEN);
            board[ROW][COL].addActionListener(this);
            boardPanel.add(board[ROW][COL]);
        }

        movimientos = 0;
        lblMovimientos.setText(String.valueOf(movimientos));
        resetTimer(); // Reiniciar el tiempo.
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private boolean isSolvable(ArrayList<Integer> list) {
        int inversionSum = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == 0) {
                inversionSum += ((i / DIM) + 1);
                continue;
            }
            int count = 0;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j) == 0) {
                    continue;
                } else if (list.get(i) > list.get(j)) {
                    count++;
                }
            }
            inversionSum += count;
        }
        return (inversionSum & 1) == 0;
    }

    private int getIndex(int i, int j) {
        return ((i * DIM) + j);
    }

    private int indexOf(String cellNum) {
        for (int ROW = 0; ROW < board.length; ROW++) {
            for (int COL = 0; COL < board[ROW].length; COL++) {
                if (board[ROW][COL].getText().equals(cellNum)) {
                    return getIndex(ROW, COL);
                }
            }
        }
        return -1;
    }

    private boolean makeMove(int row, int col) {
        final int emptyRow = emptyCell / DIM;
        final int emptyCol = emptyCell % DIM;
        int rowDiff = emptyRow - row;
        int colDiff = emptyCol - col;
        boolean isInRow = (row == emptyRow);
        boolean isInCol = (col == emptyCol);
        boolean isNotDiagonal = (isInRow || isInCol);

        if (isNotDiagonal) {
            int diff = Math.abs(colDiff);

            if (colDiff < 0 && isInRow) {
                for (int i = 0; i < diff; i++) {
                    board[emptyRow][emptyCol + i].setText(board[emptyRow][emptyCol + (i + 1)].getText());
                }
            } else if (colDiff > 0 && isInRow) {
                for (int i = 0; i < diff; i++) {
                    board[emptyRow][emptyCol - i].setText(board[emptyRow][emptyCol - (i + 1)].getText());
                }
            }

            diff = Math.abs(rowDiff);

            if (rowDiff < 0 && isInCol) {
                for (int i = 0; i < diff; i++) {
                    board[emptyRow + i][emptyCol].setText(board[emptyRow + (i + 1)][emptyCol].getText());
                }
            } else if (rowDiff > 0 && isInCol) {
                for (int i = 0; i < diff; i++) {
                    board[emptyRow - i][emptyCol].setText(board[emptyRow - (i + 1)][emptyCol].getText());
                }
            }

            board[emptyRow][emptyCol].setVisible(true);
            board[row][col].setText("0");
            board[row][col].setVisible(false);
            emptyCell = getIndex(row, col);

            movimientos++;
            lblMovimientos.setText(String.valueOf(movimientos));
        }

        return true;
    }

    private boolean isFinished() {
        for (int index = WIN.length - 1; index >= 0; index--) {
            String number = board[index / DIM][index % DIM].getText();
            if (!number.equals(WIN[index])) {
                return false;
            }
        }
        return true;
    }

    private void playWinSound() {
        try {
            // Cargar el archivo de audio desde el classpath
            InputStream audioStream = getClass().getResourceAsStream("/img/YouWin.wav");
            if (audioStream == null) {
                throw new IOException("No se encontró el archivo de audio");
            }

            // Crear el flujo de entrada de audio
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    public void actionPerformed(ActionEvent event) {
        JButton buttonPressed = (JButton) event.getSource();

        if (buttonPressed.getText().equals("Resetear")) {
            inicializar(); // Resetea el juego.
        } else {
            int index = indexOf(buttonPressed.getText());
            if (index == -1) {
                throw new IllegalArgumentException("Index should be between 0-15");
            }
            int row = index / DIM;
            int column = index % DIM;
            makeMove(row, column);

            if (isFinished()) {
                timer.stop();
                playWinSound(); // Reproducir el sonido de victoria.
                JOptionPane.showMessageDialog(null, "¡Has ganado el juego!");
            }
        }
    }

    public void jugar() {
        inicializar();
        SwingUtilities.invokeLater(() -> new Interfaz().setVisible(true));
    }
}
