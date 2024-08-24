package JuegoStreetFighter;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Menu extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Menu frame = new Menu();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Menu() {
        setTitle("Puzzle 15 - Street Fighter edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 450);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 240));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Cargar y establecer el ícono de la ventana
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/logo2.png"));
        setIconImage(icon.getImage());

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Participantes: Oviedo, Roldan, Zelarayan");
        lblNewLabel.setFont(new Font("Baskerville Old Face", Font.PLAIN, 15));
        lblNewLabel.setBounds(10, 209, 326, 30);
        contentPane.add(lblNewLabel);

     // Cargar y redimensionar la imagen
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/img/logo.png"));
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel img = new JLabel("");
        img.setIcon(scaledIcon);
        img.setBounds(110, 0, 226, 226);
        contentPane.add(img);

        // Crear botones con texto y cambio a imagen sin fondo al pasar el cursor
        JButton btnRyu = createButton("RYU", "/img/ryu-removebg.PNG", Color.RED, 38, 276, 95, 65);
        JButton btnChun = createButton("CHUN-LI", "/img/chinli-removebg.PNG", Color.BLUE, 169, 276, 95, 65);
        JButton btnCamy = createButton("CAMMY", "/img/cammy-removebg.PNG", Color.GREEN, 296, 276, 95, 65);

        contentPane.add(btnRyu);
        contentPane.add(btnChun);
        contentPane.add(btnCamy);
    }
    
    private void playSound(String soundFilePath) {
        try {
            // Cargar el archivo de sonido desde el paquete img
            InputStream audioSrc = getClass().getResourceAsStream(soundFilePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioSrc);

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }
    


    private JButton createButton(String text, String imagePath, Color backgroundColor, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setForeground(new Color(255, 255, 255));
        button.setBackground(backgroundColor);
        button.setBounds(x, y, width, height);

        // Cargar la imagen sin fondo
        ImageIcon imageIcon = new ImageIcon(getClass().getResource(imagePath));
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Establecer el ícono de fondo transparente cuando el cursor está sobre el botón
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setText(""); 
                button.setIcon(scaledIcon); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setText(text); 
                button.setIcon(null); 
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                switch (text) {
                    case "RYU":
                        playSound("/img/JAPAN.wav");
                        System.out.println("Se ha seleccionado Ryu");
                        break;
                    case "CHUN-LI":
                        playSound("/img/CHINA.wav");
                        System.out.println("Se ha seleccionado Chun-li");
                        break;
                    case "CAMMY":
                        playSound("/img/USA.wav");
                        System.out.println("Se ha seleccionado Cammy");
                        break;
                }
            }
        });

        return button;
    }

}
