package interfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import java.util.TimerTask;

public class Interfaz extends JFrame{
	
	private static int DIM;
	private JLabel lblMovimientos,lblTiempo;
	private JPanel boardPanel;
	private JMenuItem btnReset,btnDeshacer,btnVolver;
	public JButton btnArriba;
	public JButton btnAbajo;
	public JButton btnIzquierda;
	public JButton btnDerecha;
	public JButton btnPlay;
	public JButton btnPause;
	private Timer timer;
	private int tiempoRestante;
	private boolean isPaused = false;
	private Runnable onTimeOver;
	private int facil=40,medio=30,dificil=15;

    public Interfaz(String dificultad, String personaje, int dim) { 
    	DIM=dim;
    	
    	window();
    
	    // Crear el panel principal con BorderLayout
	    JPanel mainPanel = new JPanel(new BorderLayout());
	
	    // Panel superior que contiene las etiquetas
	    JPanel topPanel = topPanel();
	    mainPanel.add(topPanel, BorderLayout.NORTH);
	    
	    // Panel menu con botones
	    menuBar();
	    
	    // Marco para botones del juego
	    JPanel board=getBoardPanel();
	
	    // Panel de tablero y controles
	    JPanel ControlsPanel = boardAndControlsPanel(); 
	    
	    //tiempo
	    setTime(dificultad);
	    
	    // Panel de referencia
	    JPanel referencePanel = referencePanel(personaje, dificultad);
	
	    // Dividir la pantalla en dos: tablero+controles a la izquierda y referencia a la derecha
	    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, ControlsPanel, referencePanel);
	    splitPane.setResizeWeight(1); // 70% para el tablero y controles, 30% para la imagen de referencia
	
	    mainPanel.add(splitPane, BorderLayout.CENTER);
	    setContentPane(mainPanel);
	    
    }
    
    public void stopTimer() {
        if (timer != null) {
            timer.cancel(); // Detener el temporizador si está corriendo
        }
    }
    
  
 // Método para iniciar el temporizador
    public void startTime(Runnable onTimeOver) {  // Recibe un Runnable como callback
    	this.onTimeOver = onTimeOver;
        timer = new Timer();
        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                if (tiempoRestante > 0) {
                    tiempoRestante--;
                    String tiempoFormateado = formatearTiempo(tiempoRestante);
                    setTimerText(tiempoFormateado);
                } else {
                    stopTimer(); // Detener el temporizador
                    onTimeOver.run();  // Llamar al callback cuando el tiempo acaba
                }
            }
        };
        timer.scheduleAtFixedRate(tarea, 0, 1000); // Programar la tarea cada segundo
    }
    
    public void setTimerText(String text) {
    	lblTiempo.setText(text);
    	lblTiempo.repaint();
    }

		// Método para formatear el tiempo en minutos y segundos (MM:SS)
    public String formatearTiempo(int segundosTotales) {
        long minutos = TimeUnit.SECONDS.toMinutes(segundosTotales);
        long segundos = segundosTotales - (minutos * 60);
        return String.format("%02d:%02d", minutos, segundos);
    }
    

	public void setTime(String dificultad) {
		tiempoRestante=60;
		switch (dificultad) {
         case "Facil":
             tiempoRestante = tiempoRestante * facil; // 40 minutos en segundos
             break;
         case "Medio":
             tiempoRestante = tiempoRestante * medio; // 30 minutos en segundos
             break;
         case "Dificil":
             tiempoRestante = tiempoRestante * dificil; // 15 minutos en segundos
             break;
         default:
             throw new IllegalArgumentException("Dificultad no válida");
     }
	}

	private void window() {
        setTitle("Puzzle 15 - MVP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        // Cargar y establecer el ícono de la ventana
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/logo2.png"));
        setIconImage(icon.getImage());
    }
	
	
	
    private JPanel topPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel lblNewLabel = new JLabel("Cantidad movimientos:");
        topPanel.add(lblNewLabel);

        lblMovimientos = new JLabel("0");
        topPanel.add(lblMovimientos);

        JLabel lblNewLabel_1 = new JLabel("Tiempo:");
        topPanel.add(lblNewLabel_1);
        
        setLabelInitTimer();
        topPanel.add(lblTiempo); // Agregar lblTiempo al panel
        
        btnPlay = new JButton("Play");
        btnPlay.setBackground(Color.GREEN);
        btnPlay.setVisible(false);
        btnPlay.addActionListener(e -> {
            btnPlay.setVisible(false);
            btnPause.setVisible(true);
            startTime(onTimeOver);  // Reiniciar el temporizador
            isPaused = false;       // Actualizar el estado a no pausado
            setTeclas(true);
        });
        topPanel.add(btnPlay);

        // Botón Pause
        btnPause = new JButton("Pause");
        btnPause.setBackground(Color.RED);
        btnPause.setVisible(true);
        btnPause.addActionListener(e -> {
            btnPlay.setVisible(true);
            btnPause.setVisible(false);
            stopTimer();  // Detener el temporizador
            isPaused = true;  // Actualizar el estado a pausado
            setTeclas(false);
        });
        topPanel.add(btnPause);


        return topPanel;
    }
    
    public JPanel getBoardPanel() {
        if (boardPanel == null) {
            boardPanel = new JPanel(new GridLayout(DIM, DIM));  // Inicializar boardPanel
        }
        return boardPanel;
    }

    public void cleanBoardPanel() {
        boardPanel.removeAll();
    }

    public void updateViewBoardPanel() {
        boardPanel.revalidate();
        boardPanel.repaint();
    }
    
    public void setLabelInitTimer() {
		lblTiempo = new JLabel(formatearTiempo(tiempoRestante)); 
	}
	
	public boolean getisPause()	{
		return this.isPaused;
	}   
	
	public int getTimepoRestante() {
		return this.tiempoRestante;
	}
	
	public void setisPause(boolean est) {
		this.isPaused=est;
	}
	
	// Agrega un campo para el ActionListener
    private ActionListener arrowButtonListener;

    // Método para registrar el listener
    public void setArrowButtonListener(ActionListener listener) {
        this.arrowButtonListener = listener;
    }

    // Dentro de boardAndControlsPanel, agrega los listeners a los botones de flecha
    public JPanel boardAndControlsPanel() {
        JPanel boardAndControlsPanel = new JPanel(new BorderLayout());

        // Crear el panel del tablero
        boardAndControlsPanel.add(boardPanel, BorderLayout.CENTER);

        // Crear los botones de control
        JPanel controlPanel = new JPanel(new GridLayout(3, 3, 5, 5)); // Más espaciamiento
        btnArriba = new JButton("↑");
        btnAbajo = new JButton("↓");
        btnIzquierda = new JButton("←");
        btnDerecha = new JButton("→");

        // Asignar listeners a los botones de flecha
        btnArriba.addActionListener(e -> {
            if (arrowButtonListener != null) {
                arrowButtonListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "up"));
            }
        });

        btnAbajo.addActionListener(e -> {
            if (arrowButtonListener != null) {
                arrowButtonListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "down"));
            }
        });

        btnIzquierda.addActionListener(e -> {
            if (arrowButtonListener != null) {
                arrowButtonListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "left"));
            }
        });

        btnDerecha.addActionListener(e -> {
            if (arrowButtonListener != null) {
                arrowButtonListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "right"));
            }
        });

        controlPanel.add(new JLabel()); // Espacio vacío (arriba-izquierda)
        controlPanel.add(btnArriba);    // Botón arriba
        controlPanel.add(new JLabel()); // Espacio vacío (arriba-derecha)
        controlPanel.add(btnIzquierda); // Botón izquierda
        controlPanel.add(new JLabel()); // Espacio vacío (centro)
        controlPanel.add(btnDerecha);   // Botón derecha
        controlPanel.add(new JLabel()); // Espacio vacío (abajo-izquierda)
        controlPanel.add(btnAbajo);     // Botón abajo
        controlPanel.add(new JLabel()); // Espacio vacío (abajo-derecha)
        
        boardAndControlsPanel.add(controlPanel, BorderLayout.SOUTH);
        return boardAndControlsPanel;
    }

	//para bloquear (o no) para cuando se pausa el jeugo
	public void setTeclas(boolean est) {
		btnArriba.setVisible(est);
		btnAbajo.setVisible(est);
        btnIzquierda.setVisible(est);
        btnDerecha.setVisible(est);
	}
	
	
    private JPanel referencePanel(String personaje, String dificultad) {
        JPanel referencePanel = new JPanel(new GridLayout(4, 1));  // Cambiar el layout a GridLayout con 4 filas

        JLabel dificultadLabel = new JLabel(personaje, SwingConstants.CENTER);
        referencePanel.add(dificultadLabel);

        JLabel lblPersonaje = new JLabel("Avatar:", SwingConstants.CENTER);
        referencePanel.add(lblPersonaje);

        // Imagen del personaje
        ImageIcon referenceImage = new ImageIcon(loadAvatar(personaje));
        Image image = referenceImage.getImage();
        Image scaledImage = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel imageLabel = new JLabel(scaledIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        referencePanel.add(imageLabel);

        JLabel lblDificultad = new JLabel("Challenge: "+dificultad, SwingConstants.CENTER);
        referencePanel.add(lblDificultad);
        
        return referencePanel;
    }
    
    private void menuBar() {
    	// Crear el menú
        JMenuBar menuBar = new JMenuBar();
        JMenu mnNewMenu = new JMenu("Menu ⬇️");
        
        // Crear los botones y agregarlos al menú
        btnReset = new JMenuItem("Resetear");
        mnNewMenu.add(btnReset);
        btnReset.setForeground(new Color(255, 255, 0));
        btnReset.setHorizontalAlignment(SwingConstants.RIGHT);
        btnReset.setBackground(new Color(0, 0, 255));

        btnDeshacer = new JMenuItem("Deshacer");
        mnNewMenu.add(btnDeshacer);
        btnDeshacer.setHorizontalAlignment(SwingConstants.RIGHT);
        btnDeshacer.setForeground(new Color(248, 248, 255));
        btnDeshacer.setBackground(Color.RED);

        btnVolver = new JMenuItem("Volver");
        btnVolver.setHorizontalAlignment(SwingConstants.RIGHT);
        btnVolver.setForeground(new Color(248, 248, 255));
        btnVolver.setBackground(Color.BLUE);
        mnNewMenu.add(btnVolver);
        

        // Agregar el menú al menúBar
        menuBar.add(mnNewMenu);
        setJMenuBar(menuBar); // Establecer el JMenuBar en el JFrame
    }
    
    
    public void addMenuItemClickListener(ActionListener listener) {
        btnReset.addActionListener(listener);
        btnDeshacer.addActionListener(listener);
        btnVolver.addActionListener(listener);
    }

    private String loadAvatar(String personaje) {
 	    if (personaje.equalsIgnoreCase("ryu"))
            personaje = "src/img/ryu.png";
        else if (personaje.equalsIgnoreCase("camy"))
            personaje = "src/img/cammy.png"; 
        else if (personaje.equalsIgnoreCase("chun"))
            personaje = "src/img/chinli.PNG";
        
        return personaje;
    }
    
    public void setMovimientosText(String text) {
        lblMovimientos.setText(text);
    }
    
    public void Special_msm(String tit,String msm) {
		JOptionPane optionPane = new JOptionPane(
				    tit,
				    JOptionPane.INFORMATION_MESSAGE,
				    JOptionPane.DEFAULT_OPTION,
				    null,
				    new Object[]{},  // Arreglo vacío para no mostrar botones
				    null
				);

				// Crear el cuadro de diálogo sin botones
				JDialog dialog = optionPane.createDialog(msm);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

				dialog.setVisible(true);
	}

    
	public void closeWindow() {
		dispose();
	}


}
