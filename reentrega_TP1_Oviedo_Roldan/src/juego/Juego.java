package juego;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import interfaz.Interfaz;
//modelo
public class Juego {
	private Interfaz view;
	private Stack<JButton[][]> previousStates;
    private int movimientos = 0, emptyCell;
    private static final int DIM = 4;
    private static final int SIZE = DIM * DIM;
    final String[] WIN = new String[SIZE - 1];
    private JButton[][] board = new JButton[getDim()][getDim()];
    private BufferedImage[] images; 
    private String dificultad,personaje;

    public Juego(String dificultad, String personaje) {
        previousStates = new Stack<>();
        for (int i = 1; i < SIZE; i++) {
            WIN[i - 1] = Integer.toString(i);
        }
        this.setDificultad(dificultad);
        this.setPersonaje(personaje);
        view = new Interfaz(this.dificultad, this.personaje, DIM);
	    if(view.getisPause()) {
	    	finishedGame(false);//unicamnete para "bloquear" la pantalla 
	    }
	    
    }
    
    public void inicializar(JPanel boardPanel, Interfaz vista) {
		boardPanel.removeAll();
	    previousStates.clear();

	    ArrayList<Integer> initialList = new ArrayList<>(SIZE);
	    for (boolean isSolvable = false; !isSolvable;) {
	        initialList.clear();
	        for (int i = 0; i < SIZE; i++) {
	            initialList.add(i);
	        }
	        Collections.shuffle(initialList);
	        isSolvable = isSolvable(initialList);
	    }

	    if (personaje.equals("ryu")) {
	        cargarImagen("/img/ryu.png");
	    } else if (personaje.equals("chun")) {
	        cargarImagen("/img/chinli.png");
	    } else {
	        cargarImagen("/img/cammy.png");
	    }

	    for (int index = 0; index < SIZE; index++) {
	        final int ROW = index / getDim();
	        final int COL = index % getDim();
	        board[ROW][COL] = new JButton();

	        if (initialList.get(index) == 0) {
	            emptyCell = index;
	            board[ROW][COL].setVisible(false); // La celda vacía
	        } else {
	            int imageIndex = initialList.get(index) - 1;
	            board[ROW][COL].setIcon(new ImageIcon(images[imageIndex])); // Asigna la imagen correspondiente
	        }

	        board[ROW][COL].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	        board[ROW][COL].setBackground(Color.CYAN);
	        board[ROW][COL].setForeground(Color.BLACK);

	        board[ROW][COL].addActionListener(e -> {
	            if (makeMove(ROW, COL)) {
	                vista.setMovimientosText(String.valueOf(movimientos));
	                if (isFinished()) {
	                    gameWin();
	                }
	            }
	        });
	        boardPanel.add(board[ROW][COL]);
	    }

	    movimientos = 0;
	    boardPanel.revalidate();
	    boardPanel.repaint();
	}
	
    public void gameOver() {
        view.Special_msm("¡Se acabó el tiempo! Has perdido el juego", "Loser");
        playSound("/img/YOU-LOSE.wav"); // Añadir sonido de perder si existe
        finishedGame(false);
    }

    public void gameWin() {
        playSound("/img/YouWin.wav");
        view.Special_msm("¡Has ganado el juego!","Winner");
        finishedGame(false);  
    }
    
    public void finishedGame(boolean estado) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                JButton boton = board[i][j];
                if (boton != null) {
                    boton.setEnabled(estado);  // Habilitar o deshabilitar el botón según el estado
                    if (!estado) {
                        boton.setBackground(Color.BLACK);  // Cambiar el color de fondo si está pausado
                    } else {
                        boton.setBackground(null);  // Restaurar el color original si está activo
                    }
                }
            }
        }
    }


	public void moverBlanco(int keyCode) {
        boolean moveSuccessful = false;
        int emptyRow = getEmptyCellRow();
        int emptyColumn = getEmptyCellColumn();
        
        switch (keyCode) {
            case KeyEvent.VK_DOWN: // Flecha abajo
                if (emptyRow < DIM - 1) {
                    moveSuccessful = makeMove(emptyRow + 1, emptyColumn);
                }
                break;
            case KeyEvent.VK_UP: // Flecha arriba
                if (emptyRow > 0) {
                    moveSuccessful = makeMove(emptyRow - 1, emptyColumn);
                }
                break;
            case KeyEvent.VK_LEFT: // Flecha izquierda
                if (emptyColumn > 0) {
                    moveSuccessful = makeMove(emptyRow, emptyColumn - 1);
                }
                break;
            case KeyEvent.VK_RIGHT: // Flecha derecha
                if (emptyColumn < DIM - 1) {
                    moveSuccessful = makeMove(emptyRow, emptyColumn + 1);
                }
                break;
        }
	    // Si el movimiento fue exitoso, actualizar el contador de movimientos y verificar si se ganó el juego
	    if (moveSuccessful) {
	        view.setMovimientosText(String.valueOf(movimientos));

	        // Verificar si el juego, si lo hizo gano
	        if (isFinished()) {
	            gameWin();
	        }
	    }
	}
    
    private void cargarImagen(String rutaImagen) {
        try {
            // Cargar la imagen desde la ruta proporcionada
            BufferedImage img = ImageIO.read(getClass().getResource(rutaImagen));

            // Obtener las dimensiones de cada subimagen
            int imgWidth = img.getWidth() / DIM;
            int imgHeight = img.getHeight() / DIM;

            // Crear un array de BufferedImage para almacenar las subimágenes
            images = new BufferedImage[SIZE - 1];

            int index = 0;
            for (int row = 0; row < DIM; row++) {
                for (int col = 0; col < DIM; col++) {
                    if (index < SIZE - 1) {
                        // Extraer y almacenar cada subimagen
                        images[index] = img.getSubimage(col * imgWidth, row * imgHeight, imgWidth, imgHeight);
                        index++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyBoard() {
        JButton[][] copy = new JButton[DIM][DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                JButton buttonCopy = new JButton();
                buttonCopy.setIcon(board[i][j].getIcon());
                buttonCopy.setVisible(board[i][j].isVisible());
                buttonCopy.setEnabled(board[i][j].isEnabled());
                buttonCopy.setBackground(board[i][j].getBackground());

                // Copiar el ActionListener para mantener la funcionalidad del botón
                for (ActionListener listener : board[i][j].getActionListeners()) {
                    buttonCopy.addActionListener(listener);
                }
                copy[i][j] = buttonCopy;
            }
        }
        previousStates.push(copy);
    }

    public void updateBoardPanel() {
        view.cleanBoardPanel(); // Limpiar el panel
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                view.getBoardPanel().add(getButtonAt(i, j)); // Agregar cada botón al panel
            }
        }
        view.updateViewBoardPanel();
    }

    public void undoMove() {
        if (!previousStates.isEmpty()) {
            JButton[][] previousBoard = previousStates.pop();
            for (int i = 0; i < DIM; i++) {
                for (int j = 0; j < DIM; j++) {
                    board[i][j].setIcon(previousBoard[i][j].getIcon());
                    board[i][j].setVisible(previousBoard[i][j].isVisible());
                    board[i][j].setEnabled(previousBoard[i][j].isEnabled());
                    board[i][j].setBackground(previousBoard[i][j].getBackground());

                    // Restaurar el ActionListener para cada botón
                    for (ActionListener listener : board[i][j].getActionListeners()) {
                        board[i][j].removeActionListener(listener);
                    }
                    for (ActionListener listener : previousBoard[i][j].getActionListeners()) {
                        board[i][j].addActionListener(listener);
                    }
                }
            }
            movimientos--;
            // Restaurar la celda vacía
            emptyCell = getEmptyCellFromPreviousState(previousBoard);
        }
    }

    private int getEmptyCellFromPreviousState(JButton[][] previousBoard) {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if (!previousBoard[i][j].isVisible()) {
                    return i * DIM + j;
                }
            }
        }
        return -1; // No debería suceder, ya que siempre debe haber una celda vacía
    }




    public boolean makeMove(int row, int col) {
        final int emptyRow = emptyCell / DIM;
        final int emptyCol = emptyCell % DIM;

        if ((row == emptyRow && Math.abs(col - emptyCol) == 1) || (col == emptyCol && Math.abs(row - emptyRow) == 1)) {
            copyBoard();

            board[emptyRow][emptyCol].setIcon(board[row][col].getIcon());
            board[row][col].setIcon(null);
            board[emptyRow][emptyCol].setVisible(true);
            board[row][col].setVisible(false);

            emptyCell = row * DIM + col;
            movimientos++;
            return true;
        }
        return false;
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

    public boolean isFinished() {
        for (int index = WIN.length - 1; index >= 0; index--) {
            String number = board[index / getDim()][index % getDim()].getText();
            if (!number.equals(WIN[index])) {
                return false;
            }
        }
        return true;
    }
   

    public void playSound(String soundFilePath) {
        try {
            // Cargar el archivo de sonido como un InputStream
            InputStream audioSrc = getClass().getResourceAsStream(soundFilePath);
            if (audioSrc == null) {
                System.out.println("No se encontró el archivo de sonido: " + soundFilePath);
                return;
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioSrc);

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

            // Cerrar el clip al terminar
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
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

    public int getDim() {
        return DIM;
    }

    public int getEmptyCellRow() {
        return emptyCell / DIM;
    }

    public int getEmptyCellColumn() {
        return emptyCell % DIM;
    }
    

    public String getDificultad() {
		return dificultad;
	}

	public String getPersonaje() {
		return personaje;
	}

	public void setDificultad(String dificultad) {
	    if (dificultad!=null &&( dificultad.equals("Facil") || dificultad.equals("Medio") || dificultad.equals("Dificil"))) {
	        this.dificultad = dificultad;
	    } else {
	        throw new IllegalArgumentException("Dificultad no válida");
	    }
	}

	public void setPersonaje(String personaje) {
	    if (personaje!=null &&( personaje.equals("ryu") || personaje.equals("chun") || personaje.equals("camy"))) {
	        this.personaje = personaje;
	    } else {
	        throw new IllegalArgumentException("Personaje no válido");
	    }
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