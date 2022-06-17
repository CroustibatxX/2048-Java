import Service.HistoryService;
import Service.ScoreService;
import modele.Jeu;
import vue_controleur.Swing2048;

public class Main {

    public static void main(String[] args) {
        mainSwing();

    }

    public static void mainSwing() {
        HistoryService history = new HistoryService();
        ScoreService scoreService = new ScoreService();
        Jeu jeu = new Jeu(4,history, scoreService);
        Swing2048 vue = new Swing2048(jeu);
        jeu.addObserver(vue);

        vue.setVisible(true);
    }



}
