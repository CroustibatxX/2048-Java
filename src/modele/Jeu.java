package modele;
import Service.HistoryData;
import Service.HistoryService;
import vue_controleur.Swing2048;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Jeu extends Observable {

    private Case[][] tabCases;
    private HashMap<Case,Point> map = new HashMap<>();

    private final HistoryService histoService;

    private int score = 0;

    //Gestion mouvement case
    private boolean moved = false;

    private boolean endgame = false;

    public int getScore() {
        return score;
    }

    public Jeu(int size,HistoryService histoService) {
        this.histoService = histoService;
        tabCases = new Case[size][size];
        init();
    }

    public Jeu getGame(){
        return this;
    }

    public void saveScore(){
        try {
            List<Integer> scores = getSavedScore();
            Collections.sort(scores,Collections.reverseOrder());
            if(scores.size() < 5){
                scores.add(score);
                Collections.sort(scores,Collections.reverseOrder());
            }
            else if(score > scores.get(scores.size() - 1)){
                scores.remove(scores.size() - 1);
                scores.add(score);
                Collections.sort(scores,Collections.reverseOrder());
            }

            FileWriter writer = new FileWriter(getScoreFile(), false);
            for (int i=0; i < scores.size(); i++) {
                writer.write(Integer.toString(scores.get(i))+ "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getScoreFile(){
        String scoreFile = "score.txt";
        switch(getSize()) {
            case 5:
                scoreFile = "score5.txt";
                break;
            case 8:
                scoreFile = "score8.txt";
                break;
        }
        return scoreFile;
    }

    public List<Integer> getSavedScore(){
        List<Integer> scores = new ArrayList<Integer>();
        try {
            FileReader reader = new FileReader(getScoreFile());
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                scores.add(Integer.parseInt(line));
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public void restartGame(){
        endgame = false;
        saveScore();
        score = 0;
        map.clear();
        init();
    }

    public int getSize() {
        return tabCases.length;
    }

    public void setTabCases(int size) {
        tabCases = new Case[size][size];
        restartGame();
    }

    public Case getCase(int i, int j) {
        return tabCases[i][j];
    }

    public void init() {
        histoService.clear();
        //On rempli le tableau à null
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                tabCases[i][j] = new Case(0, this);
            }
        }
        //On ajoute 2 cases
        for (int i = 0; i < 2; i++) {
            addCase();
        }
        histoService.savePlay(tabCases, score);
        updateScreen();
    }


    public boolean isEmpty(Point cell) {
        return getCase(cell.x,cell.y).getValeur() == 0;
    }

    //Permet d'obtenir la liste des cases vides
    public List<Point> emptyCases() {
        List<Point> result = new ArrayList<>();
        for (int x = 0; x < getSize(); ++x) {
            for (int y = 0; y < getSize(); ++y) {
                Point point = new Point(x, y);
                if (isEmpty(point)) {
                    result.add(point);
                }
            }
        }
        return result;
    }

    public void move(Direction direction){
        new Thread(){
            public void run(){
                if(!endgame){
                    moved = false;
                    switch(direction) {
                        case droite :
                            for (int i = getSize() - 1 ; i >= 0; i--) {
                                for (int j = getSize() - 1; j >= 0; j--) {
                                    if(tabCases[j][i].getValeur() != 0){
                                        tabCases[j][i].move(direction);
                                    }
                                }
                            }
                        case bas:
                            for (int i = getSize() - 1 ; i >= 0; i--) {
                                for (int j = getSize() - 1; j >= 0; j--) {
                                    if(tabCases[i][j].getValeur() != 0){
                                        tabCases[i][j].move(direction);
                                    }
                                }
                            }
                        case gauche:
                            for (int i = 0; i < getSize(); i++) {
                                for (int j = 0; j < getSize(); j++) {
                                    if(tabCases[j][i].getValeur() != 0){
                                        tabCases[j][i].move(direction);
                                    }
                                }
                            }
                        case haut:
                            for (int i = 0; i < getSize(); i++) {
                                for (int j = 0; j < getSize(); j++) {
                                    if(tabCases[i][j].getValeur() != 0){
                                        tabCases[i][j].move(direction);
                                    }
                                }
                            }


                    }
                    //Si un mouvement s'est effectuer on ajoute une case
                    if(moved){
                        addCase();
                        histoService.savePlay(tabCases, score);
                    }

                    if(checkLoose()){
                        Jeu game = getGame();
                        game.endgame = true;
                        Swing2048.displayLoosePopup(game);
                    }
                }
            }
        }.start();


    }

    public void lastMove(){
        HistoryData data = histoService.back();
        if(data != null){
            int[][] tab = data.getTab();
            score = data.getScore();
            for (int i = 0; i < getSize(); ++i) {
                for (int j = 0; j < getSize(); ++j) {
                    tabCases[j][i].setValeur(tab[j][i]);
                    map.put(tabCases[j][i], new Point(j,i));
                }
            }
            updateScreen();
        }
    }

    public void addCase() {
        List<Point> emptyCells = emptyCases();
        //S'il y a des cases vides
        if(!emptyCells.isEmpty()){
            Random rand = new Random();
            //80% de chance d'afficher un 2
            int number = rand.nextInt(10) >= 8 ? 4 : 2;
            int index = rand.nextInt(emptyCells.size());
            Point point = emptyCells.get(index);

            Case cell = new Case(number, this);
            tabCases[point.x][point.y] = cell;
            map.put(cell, point);

            moved = true;
            updateScreen();
        }
    }

    public Case getNeighbour(Direction direction, Case cell){
        Point cellPoint = map.get(cell);

        Case neighbour = null;
        Point neighPoint = new Point(cellPoint.x, cellPoint.y);
        switch (direction){
            case haut :
                neighPoint.x -= 1;
                if(neighPoint.x < 0){
                    neighbour = new Case(-1, this);
                }
                break;
            case bas :
                neighPoint.x += 1;
                if(neighPoint.x > getSize() - 1){
                    neighbour = new Case(-1, this);
                }
                break;
            case gauche :
                neighPoint.y -= 1;
                if(neighPoint.y < 0){
                    neighbour = new Case(-1, this);
                }
                break;
            case droite :
                neighPoint.y += 1;
                if(neighPoint.y > getSize() - 1){
                    neighbour = new Case(-1, this);
                }
                break;
        }

        //Il existe un voisin
        if(neighbour == null){
            neighbour = tabCases[neighPoint.x][neighPoint.y];
        }

        return neighbour;
    }

    private void checkWin(int value){
        if(!endgame && value == 2048){
            endgame = true;
            Swing2048.displayWinPopup();
        }
    }

    //Renvoie true si aucun mouvement n'est possible et donc que la partie est perdue
    private boolean checkLoose(){
        //S'il n'y a plus de case vide
        if(emptyCases().isEmpty()){
            //On vérifie chaque case
            for (int i = 0; i < getSize(); i++) {
                for (int j = 0; j < getSize(); j++) {
                    Case cell = tabCases[i][j];
                    for (Direction dir : Direction.values()) {
                        Case neighbour = this.getNeighbour(dir,cell);
                        if(cell.getValeur() == neighbour.getValeur()){
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void fusion(Case cell, Case neighbour){
        if(cell.getValeur() == neighbour.getValeur()){
            //On vide la case
            cell.setValeur(0);
            Point point = map.get(cell);
            tabCases[point.x][point.y].setValeur(0);
            //On fussione la nouvelle case
            int value = neighbour.getValeur()*2;
            neighbour.setValeur(value);
            score += value;
            checkWin(value);

            updateScreen();
        }
    }

    public void moveCase(Direction direction, Case cell){
        Point point = map.get(cell);

        tabCases[point.x][point.y] = new Case(0, this);
        switch (direction){
            case haut :
                point.x -= 1;
                if(point.x < 0){
                    point.x = 0;
                }
                break;
            case bas :
                point.x += 1;
                if(point.x > getSize() - 1){
                    point.x = getSize() - 1;
                }
                break;
            case gauche :
                point.y -= 1;
                if(point.y < 0){
                    point.y = 0;
                }
                break;
            case droite :
                point.y += 1;
                if(point.y > getSize() - 1){
                    point.y = getSize() - 1;
                }
                break;
        }

        tabCases[point.x][point.y] = cell;
        map.put(cell, point);
        moved = true;

        updateScreen();
    }



    private void updateScreen(){
        setChanged();
        notifyObservers();
    }

}
