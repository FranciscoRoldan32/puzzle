package menu;

import java.awt.EventQueue;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Font;

import Run.Run;

public class Menu_dificultad extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<String> dificultadComboBox;
    private static String personaje;
    
    public Menu_dificultad(String personaje) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 450);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Cargar y establecer el ícono de la ventana
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/imgMenu.PNG"));
        setIconImage(icon.getImage());

        setContentPane(contentPane);
        contentPane.setLayout(null);

        this.personaje = personaje; // Inicializa el atributo personaje

        dificultadComboBox = new JComboBox<>(new String[] {"Facil", "Medio", "Dificil"});
        dificultadComboBox.setFont(new Font("Trebuchet MS", Font.BOLD, 15));
        dificultadComboBox.setBounds(175, 124, 83, 23);
        contentPane.add(dificultadComboBox);

        JButton btnStartGame = new JButton("Iniciar Juego");
        btnStartGame.setFont(new Font("Tahoma", Font.PLAIN, 20));
        btnStartGame.setBounds(135, 238, 169, 45);
        contentPane.add(btnStartGame);

        // Cargar la imagen y escalarla
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/img/back.png"));
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Crear el botón con la imagen escalada
        JButton btnBack = new JButton(scaledIcon);
        btnBack.setBounds(10, 355, 45, 45);
        contentPane.add(btnBack);

        btnBack.addActionListener(e -> {
            Menu mn = new Menu();
            mn.setVisible(true);
            //this.personaje="";
            dispose();
        });

        btnStartGame.addActionListener(e -> {
            String dificultadSeleccionada = (String) dificultadComboBox.getSelectedItem();
            Run run=new Run();
            run.setDificultad(dificultadSeleccionada);
            run.setPersonaje(personaje);
            run.main(null);
            //Interfaz juego = new Interfaz(dificultadSeleccionada, personaje);
            //juego.setVisible(true);
            dispose();
        });
    }
}
