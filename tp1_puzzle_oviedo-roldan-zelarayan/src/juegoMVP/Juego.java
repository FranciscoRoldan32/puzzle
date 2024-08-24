package juegoMVP;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

//modelo
public class Juego {

    private int movimientos = 0;
    private int emptyCell;
    private static final int DIM = 4;
    private static final int SIZE = getDim() * getDim(); //4 columnas * 4 filas
    final String[] WIN = new String[SIZE - 1]; // 15 casilleros
    private JButton[][] board = new JButton[getDim()][getDim()]; //matriz de casillas

    public Juego() {
        for (int i = 1; i < SIZE; i++) {
            WIN[i - 1] = Integer.toString(i);
        }
    }

    public void inicializar(JPanel boardPanel, Interfaz vista) {
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
            final int ROW = index / getDim();
            final int COL = index % getDim();
            board[ROW][COL] = new JButton(String.valueOf(initialList.get(index)));

            if (initialList.get(index) == 0) {
                emptyCell = index;
                board[ROW][COL].setVisible(false);
            }

            board[ROW][COL].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            board[ROW][COL].setBackground(Color.CYAN);
            board[ROW][COL].setForeground(Color.BLACK);
            
            // Usar la instancia de vista actual en lugar de crear una nueva
            board[ROW][COL].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (makeMove(ROW, COL)) {
                        vista.setMovimientosText(String.valueOf(movimientos)); // Actualizar el conteo de movimientos
                        if (isFinished()) {
                            playWinSound();
                            JOptionPane.showMessageDialog(null, "¡Has ganado el juego!");
                        }
                    }
                }
            });
            boardPanel.add(board[ROW][COL]);
        }

        movimientos = 0;
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private boolean isSolvable(ArrayList<Integer> list) {
        int inversionSum = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == 0) {
                inversionSum += ((i / getDim()) + 1);
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
        return ((i * getDim()) + j);
    }

    public int indexOf(String cellNum) {
        for (int ROW = 0; ROW < board.length; ROW++) {
            for (int COL = 0; COL < board[ROW].length; COL++) {
                if (board[ROW][COL].getText().equals(cellNum)) {
                    return getIndex(ROW, COL);
                }
            }
        }
        return -1;
    }

    public boolean makeMove(int row, int col) {
        final int emptyRow = this.getEmptyCellRow();
        final int emptyCol = this.getEmptyCellColumn();

        if (row < 0 || row >= getDim() || col < 0 || col >= getDim()) {
            return false;
        }

        int rowDiff = emptyRow - row;
        int colDiff = emptyCol - col;
        boolean isInRow = (row == emptyRow);
        boolean isInCol = (col == emptyCol);
        boolean isNotDiagonal = (isInRow || isInCol);

        if (isNotDiagonal) {
            int diff = Math.abs(colDiff);

            if (colDiff < 0 && isInRow) {
                for (int i = 0; i < diff; i++) {
                    if (emptyCol + i + 1 >= getDim()) {
                        return false;
                    }
                    board[emptyRow][emptyCol + i].setText(board[emptyRow][emptyCol + (i + 1)].getText());
                }
            } else if (colDiff > 0 && isInRow) {
                for (int i = 0; i < diff; i++) {
                    if (emptyCol - i - 1 < 0) {
                        return false;
                    }
                    board[emptyRow][emptyCol - i].setText(board[emptyRow][emptyCol - (i + 1)].getText());
                }
            }

            diff = Math.abs(rowDiff);

            if (rowDiff < 0 && isInCol) {
                for (int i = 0; i < diff; i++) {
                    if (emptyRow + i + 1 >= getDim()) {
                        return false;
                    }
                    board[emptyRow + i][emptyCol].setText(board[emptyRow + (i + 1)][emptyCol].getText());
                }
            } else if (rowDiff > 0 && isInCol) {
                for (int i = 0; i < diff; i++) {
                    if (emptyRow - i - 1 < 0) {
                        return false;
                    }
                    board[emptyRow - i][emptyCol].setText(board[emptyRow - (i + 1)][emptyCol].getText());
                }
            }

            board[emptyRow][emptyCol].setVisible(true);
            board[row][col].setText("0");
            board[row][col].setVisible(false);
            emptyCell = getIndex(row, col);

            incrementoMovimientos(); 
            return true;
        }

        return false;
    }


    public boolean isFinished() {
        for (int index = WIN.length - 1; index >= 0; index--) {
            String number = board[index / getDim()][index % getDim()].getText();
            if (!number.equals(WIN[index])) {
                return false;
            }
        }
        return true;
    }

    public void playWinSound() {
        try {
            InputStream audioStream = getClass().getResourceAsStream("/img/YouWin.wav");
            if (audioStream == null) {
                throw new IOException("No se encontró el archivo de audio");
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public int getMovimientos() {
        return movimientos;
    }

    public int getEmptyCell() {
        return emptyCell;
    }

    public static int getDim() {
        return DIM;
    }

    public int getEmptyCellRow() {
        return emptyCell / DIM;
    }

    public int getEmptyCellColumn() {
        return emptyCell % DIM;
    }

    public JButton getButtonAt(int row, int col) {
        if (row >= 0 && row < getDim() && col >= 0 && col < getDim()) {
            return board[row][col];
        } else {
            throw new IndexOutOfBoundsException("Índice fuera de rango.");
        }
    }

	public void incrementoMovimientos() {movimientos++;}

}