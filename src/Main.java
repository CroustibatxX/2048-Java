import Service.HistoryService;
import modele.Jeu;
import vue_controleur.Console2048;
import vue_controleur.Swing2048;

public class Main {

    public static void main(String[] args) {
        mainSwing();

    }

    public static void mainSwing() {
        HistoryService history = new HistoryService();
        Jeu jeu = new Jeu(4,history);
        Swing2048 vue = new Swing2048(jeu);
        jeu.addObserver(vue);

        vue.setVisible(true);
    }



}
