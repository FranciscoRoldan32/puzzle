package juego;
import javax.sound.sampled.*;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Cursor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Juego {

    private int movimientos = 0;
    private int emptyCell;
    private static final int DIM = 4;
    private static final int SIZE = getDim() * getDim(); //4 columnas * 4 filas
    final String[] WIN = new String[SIZE - 1]; // 15 casilleros
    private JButton[][] board = new JButton[getDim()][getDim()]; //matriz de casillas
    private int segundos = 0;

    public Juego() {
        for (int i = 1; i < SIZE; i++) {
            WIN[i - 1] = Integer.toString(i);
        }
    }

    public void inicializar(JPanel boardPanel, JLabel lblMovimientos, JLabel lblTiempo) {
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
            board[ROW][COL].setBackground(Color.RED);
            board[ROW][COL].setForeground(Color.WHITE);
            
            // Añadir ActionListener a cada botón
            board[ROW][COL].addActionListener(e -> {
                // Aquí puedes usar el índice del botón para hacer el movimiento
                if (makeMove(ROW, COL)) {
                    lblMovimientos.setText(String.valueOf(movimientos));
                    if (isFinished()) {
                        playWinSound();
                        JOptionPane.showMessageDialog(null, "¡Has ganado el juego!");
                    }
                }
            });
            
            boardPanel.add(board[ROW][COL]);
        }

        movimientos = 0;
        lblMovimientos.setText(String.valueOf(movimientos));
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

    // Verifica que los índices estén dentro del rango válido
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
                // Verifica que los índices estén dentro del rango
                if (emptyCol + i + 1 >= getDim()) {
                    return false;
                }
                board[emptyRow][emptyCol + i].setText(board[emptyRow][emptyCol + (i + 1)].getText());
            }
        } else if (colDiff > 0 && isInRow) {
            for (int i = 0; i < diff; i++) {
                // Verifica que los índices estén dentro del rango
                if (emptyCol - i - 1 < 0) {
                    return false;
                }
                board[emptyRow][emptyCol - i].setText(board[emptyRow][emptyCol - (i + 1)].getText());
            }
        }

        diff = Math.abs(rowDiff);

        if (rowDiff < 0 && isInCol) {
            for (int i = 0; i < diff; i++) {
                // Verifica que los índices estén dentro del rango
                if (emptyRow + i + 1 >= getDim()) {
                    return false;
                }
                board[emptyRow + i][emptyCol].setText(board[emptyRow + (i + 1)][emptyCol].getText());
            }
        } else if (rowDiff > 0 && isInCol) {
            for (int i = 0; i < diff; i++) {
                // Verifica que los índices estén dentro del rango
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

        movimientos++;
        return true; // Asegúrate de que el movimiento se haya realizado correctamente
    }

    return false; // El movimiento no es válido
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

    public void setMovimientos(int movimientos) {
        this.movimientos = movimientos;
    }

    public int getEmptyCell() {
        return emptyCell;
    }

    public void setEmptyCell(int emptyCell) {
        this.emptyCell = emptyCell;
    }

    public JButton[][] getBoard() {
        return board;
    }

    public void setBoard(JButton[][] board) {
        this.board = board;
    }

    public void setSegundos(int segundos) {
        this.segundos = segundos;
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

	public void jugar() {
		 SwingUtilities.invokeLater(() -> new Interfaz().setVisible(true));
	}
}
