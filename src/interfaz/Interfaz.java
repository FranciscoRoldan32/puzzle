package interfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import juego.Juego;
import menu.Menu;
import menu.Menu_dificultad;

public class Interfaz extends JFrame implements ActionListener, KeyListener {

    private Juego juego;
    private JLabel lblMovimientos;
    private JLabel lblTiempo;
    private Timer timer;
    private int tiempoRestante;  // Nuevo atributo para manejar el tiempo restante en segundos
    private JPanel boardPanel;
    private String dificultad;
    private String personaje;

    public Interfaz(String dificultad, String personaje) {
        this.setDificultad(dificultad);
        this.setPersonaje(personaje);
        juego = new Juego(this.getDificultad(), this.getPersonaje());
        setTitle("Puzzle 15 - MVP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        //setLocationRelativeTo(null);
        // Cargar y establecer el ícono de la ventana
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/logo2.png"));
        setIconImage(icon.getImage());


        // Configurar tiempo restante según dificultad
        configurarTiempoRestante();

        // Crear el panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel superior que contiene las etiquetas
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

        // Agregar el topPanel al mainPanel
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Crear el menú
        JMenuBar menuBar = new JMenuBar();
        JMenu mnNewMenu = new JMenu("Menu ⬇️");

        // Crear los botones y agregarlos al menú
        JMenuItem btnReset = new JMenuItem("Resetear");
        mnNewMenu.add(btnReset);
        btnReset.setForeground(new Color(255, 255, 0));
        btnReset.setHorizontalAlignment(SwingConstants.RIGHT);
        btnReset.setBackground(new Color(0, 0, 255));

        JMenuItem btnDeshacer = new JMenuItem("Deshacer");
        mnNewMenu.add(btnDeshacer);
        btnDeshacer.setHorizontalAlignment(SwingConstants.RIGHT);
        btnDeshacer.setForeground(new Color(248, 248, 255));
        btnDeshacer.setBackground(Color.RED);

        JMenuItem btnVolver = new JMenuItem("Volver");
        btnVolver.setHorizontalAlignment(SwingConstants.RIGHT);
        btnVolver.setForeground(new Color(248, 248, 255));
        btnVolver.setBackground(Color.BLUE);
        mnNewMenu.add(btnVolver);
        
        btnVolver.addActionListener(e -> {
            Menu mn = new Menu();
            mn.setVisible(true);
            //this.personaje="";
            dispose();
        });

        // Agregar el menú al menúBar
        menuBar.add(mnNewMenu);
        setJMenuBar(menuBar); // Establecer el JMenuBar en el JFrame

        // Asignar ActionListener a los botones del menú
        btnReset.addActionListener(this);
        btnDeshacer.addActionListener(this);
        btnVolver.addActionListener(this);

        // Crear el panel del tablero
        boardPanel = new JPanel(new GridLayout(Juego.getDim(), Juego.getDim()));
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Establecer el mainPanel como el contenido de la ventana
        setContentPane(mainPanel);

        juego.inicializar(boardPanel, this);
        startTimer();

        // Configurar las teclas
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    public String getDificultad() {
        return dificultad;
    }

    public String getPersonaje() {
        return personaje;
    }

    public void setDificultad(String dificultad) {
        if (dificultad != null && (dificultad.equals("Facil") || dificultad.equals("Medio") || dificultad.equals("Dificil"))) {
            this.dificultad = dificultad;
        } else {
            throw new IllegalArgumentException("Dificultad no válida");
        }
    }

    public void setPersonaje(String personaje) {
        if (personaje != null && (personaje.equals("ryu") || personaje.equals("chun") || personaje.equals("camy"))) {
            this.personaje = personaje;
        } else {
            throw new IllegalArgumentException("Personaje no válido");
        }
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

    private void configurarTiempoRestante() {
        // Configurar el tiempo restante según la dificultad seleccionada
        switch (this.dificultad) {
            case "Facil":
                tiempoRestante = 40 * 60; // 40 minutos en segundos
                break;
            case "Medio":
                tiempoRestante = 30 * 60; // 30 minutos en segundos
                break;
            case "Dificil":
                tiempoRestante = 15 * 60; // 15 minutos en segundos
                break;
            default:
                throw new IllegalArgumentException("Dificultad no válida");
        }
    }

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tiempoRestante > 0) {
                    tiempoRestante--;
                    int minutos = tiempoRestante / 60;
                    int segs = tiempoRestante % 60;
                    lblTiempo.setText(String.format("%d:%02d", minutos, segs));
                } else {
                    timer.stop();
                    JOptionPane.showMessageDialog(Interfaz.this, "¡Se acabó el tiempo! Has perdido el juego.");
                    juego.playSound("/img/YOU-LOSE.wav"); // Añadir sonido de perder si existe
                    juego.finishedGame();               
                    }
            }
        });
        timer.start();
    }


    private void resetTimer() {
        if (timer != null) {
            timer.stop();
        }
        configurarTiempoRestante(); // Restablecer el tiempo restante basado en la dificultad
        lblTiempo.setText(String.format("%d:%02d", tiempoRestante / 60, tiempoRestante % 60));
        startTimer();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem item = (JMenuItem) event.getSource();

        if (item.getText().equals("Resetear")) {
            juego.inicializar(boardPanel, this); // Pasar la instancia actual de Interfaz
            updateBoardPanel();
            resetTimer();
            setMovimientosText("0"); // Reiniciar el conteo de movimientos
        } else if (item.getText().equals("Deshacer")) {
            juego.undoMove();
            updateBoardPanel();
            setMovimientosText(String.valueOf(juego.getMovimientos()));
        } else if (item.getText().equals("Volver")) {
            Menu_dificultad md = new Menu_dificultad(this.getPersonaje());
            //this.setDificultad("");
            dispose();
        } else {
            // Lógica adicional si es necesario
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
            setMovimientosText(String.valueOf(juego.getMovimientos()));

            // Verificar si el juego ha terminado con una victoria
            if (juego.isFinished()) {
                timer.stop();
                juego.playSound("/img/YouWin.wav");
                JOptionPane.showMessageDialog(this, "¡Has ganado el juego!");
                juego.finishedGame();  
            }
        } 
    }
    

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
