package vue_controleur;

import com.sun.tools.javac.Main;
import modele.Case;
import modele.Direction;
import modele.Jeu;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class Swing2048 extends JFrame implements Observer {
    private static final int PIXEL_PER_SQUARE = 100;
    // tableau de cases : i, j -> case graphique
    private JLabel[][] tabC;
    private Jeu jeu;
    private JLabel statusLabel;
    private JPanel contentPane;


    public Swing2048(Jeu _jeu) {
        jeu = _jeu;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("2048 - BERRY DEREMBLE");

        // création du menu
        JMenuBar menuBar = new JMenuBar();
        JMenu actionMenu = new JMenu("Action");
        JMenu gameModeMenu = new JMenu("Game Mode");

        // création des sous-menus*
        JMenuItem restartMenuItem = new JMenuItem("Restart");
        restartMenuItem.setActionCommand("Restart");

        JMenuItem x4MenuItem = new JMenuItem("4x4");
        x4MenuItem.setActionCommand("4x4");

        JMenuItem x5MenuItem = new JMenuItem("5x5");
        x5MenuItem.setActionCommand("5x5");

        JMenuItem x8MenuItem = new JMenuItem("8x8");
        x8MenuItem.setActionCommand("8x8");

        // ajout des listeners aux sous-menus
        restartMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jeu.restartGame();
            }
        });

        x4MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jeu.setTabCases(4);
                getContentPane().remove(contentPane);
                createPane();

            }
        });

        x5MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jeu.setTabCases(5);
                getContentPane().remove(contentPane);
                createPane();
            }
        });

        x8MenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jeu.setTabCases(8);
                getContentPane().remove(contentPane);
                createPane();
            }
        });


        // ajout des sous-menu aux menus
        actionMenu.add(restartMenuItem);
        gameModeMenu.add(x4MenuItem);
        gameModeMenu.add(x5MenuItem);
        gameModeMenu.add(x8MenuItem);

        // ajout des menus à la menubar
        menuBar.add(actionMenu);
        menuBar.add(gameModeMenu);

        // ajout de la menubar au JFrame
        setJMenuBar(menuBar);

        // ajout du JPanel au JFrame
        setVisible(true);


        createPane();
        ajouterEcouteurClavier();
        rafraichir();

    }

    private void createPane(){
        setSize(jeu.getSize() * PIXEL_PER_SQUARE, jeu.getSize() * PIXEL_PER_SQUARE);
        tabC = new JLabel[jeu.getSize()][jeu.getSize()];

        contentPane = new JPanel(new GridLayout(jeu.getSize(), jeu.getSize()));
        for (int i = 0; i < jeu.getSize(); i++) {
            for (int j = 0; j < jeu.getSize(); j++) {
                Border border = BorderFactory.createLineBorder(Color.decode("#bbada0"), 5);
                tabC[i][j] = new JLabel();
                tabC[i][j].setFont(new Font("", Font.BOLD, 18));
                tabC[i][j].setBorder(border);
                tabC[i][j].setHorizontalAlignment(SwingConstants.CENTER);

                tabC[i][j].setOpaque(true);

                contentPane.add(tabC[i][j]);

            }
        }
        add(contentPane);

        setContentPane(contentPane);
    }


    /**
     * Correspond à la fonctionnalité de Vue : affiche les données du modèle
     */
    private void rafraichir()  {

        SwingUtilities.invokeLater(new Runnable() { // demande au processus graphique de réaliser le traitement
            @Override
            public void run() {
                for (int i = 0; i < jeu.getSize(); i++) {
                    for (int j = 0; j < jeu.getSize(); j++) {
                        Case c = jeu.getCase(i, j);

                        if (c.getValeur() == 0) {
                            tabC[i][j].setText("");
                        } else {
                            tabC[i][j].setText(c.getValeur() + "");
                        }

                        String textColor = "#f9f6f2";
                        if(c.getValeur() == 2 || c.getValeur() == 4){
                            textColor = "#776e65";
                        }

                        String color = updateColor(c.getValeur());
                        tabC[i][j].setBackground(Color.decode(color));
                        tabC[i][j].setForeground(Color.decode(textColor));
                    }
                }
            }
        });
    }

    private String updateColor(int value){

        String color = "#ccc0b3";
        switch (value){
            case 2:
                color = "#eee4da";
                break;
            case 4:
                color = "#ede0c8";
                break;
            case 8:
                color = "#f2b179";
                break;
            case 16:
                color = "#f59563";
                break;
            case 32:
                color = "#f67c5f";
                break;
            case 64:
                color = "#f65e3b";
                break;
            case 128:
                color = "#edcf72";
                break;
            case 256:
                color = "#edcc61";
                break;
            case 512:
                color = "#edc850";
                break;
            case 1024:
                color = "#edc53f";
                break;
            case 2048:
                color = "#edc53f";
                break;
        }

        return color;

    }

    /**
     * Correspond à la fonctionnalité de Contrôleur : écoute les évènements, et déclenche des traitements sur le modèle
     */
    private void ajouterEcouteurClavier() {
        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC

            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {  // on regarde quelle touche a été pressée
                    case KeyEvent.VK_LEFT : jeu.move(Direction.gauche); break;
                    case KeyEvent.VK_RIGHT : jeu.move(Direction.droite); break;
                    case KeyEvent.VK_DOWN : jeu.move(Direction.bas); break;
                    case KeyEvent.VK_UP : jeu.move(Direction.haut); break;
                }
            }
        });
    }

    public static void displayWinPopup(){
        JFrame jFrame = new JFrame();
        UIManager.put("OptionPane.okButtonText", "Continuer");
        JOptionPane.showMessageDialog(jFrame, "C'est gagné, bravo !");
    }

    public static void displayLoosePopup(Jeu game){
        JFrame frame = new JFrame();
        JDialog dialog = new JDialog(frame , "Dialog Example", true);
        dialog.setLayout( new FlowLayout() );
        JButton b = new JButton ("Relancer");
        b.addActionListener ( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                game.restartGame();
                dialog.setVisible(false);
            }
        });
        dialog.add( new JLabel ("C'est perdu, dommage !"));
        dialog.add(b);
        dialog.setSize(200,150);
        dialog.setVisible(true);

    }



    @Override
    public void update(Observable o, Object arg) {
        rafraichir();
    }
}

