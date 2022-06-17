package modele;
import Service.HistoryService;
import Service.ScoreService;
import vue_controleur.Swing2048;

import java.io.*;
import java.util.*;

public class Jeu extends Observable {

    private Case[][] tabCases;
    private HashMap<Case,Point> map = new HashMap<>();

    private final HistoryService histoService;
    private final ScoreService scoreService;

    //Gestion mouvement case
    private boolean moved = false;

    private boolean endgame = false;

    public Jeu(int size,HistoryService histoService, ScoreService scoreService) {
        this.histoService = histoService;
        this.scoreService = scoreService;
        tabCases = new Case[size][size];
        init();
    }

    public Jeu getGame(){
        return this;
    }

    public int getScore(){
        return scoreService.getScore();
    }

    public List<Integer> getSavedScore(){
        return scoreService.getSavedScore(getSize());
    }



    public void restartGame(){
        endgame = false;
        scoreService.saveScore(getSize());
        scoreService.scoreReset();
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
        histoService.savePlay(tabCases, scoreService.getScore());
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
                        histoService.savePlay(tabCases, scoreService.getScore());
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

    //Permet de revenir en arrière
    public void lastMove(){
        HistoryData data = histoService.back();
        if(data != null){
            int[][] tab = data.getTab();
            scoreService.setScore(data.getScore());
            for (int i = 0; i < getSize(); ++i) {
                for (int j = 0; j < getSize(); ++j) {
                    tabCases[j][i].setValeur(tab[j][i]);
                    map.put(tabCases[j][i], new Point(j,i));
                }
            }
            updateScreen();
        }
    }

    //Permet d'ajouter une case
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

    //Permet de fusionner 2 cases
    public void fusion(Case cell, Case neighbour){
        //On vide la case
        cell.setValeur(0);
        Point point = map.get(cell);
        tabCases[point.x][point.y].setValeur(0);
        //On fussione la nouvelle case
        int value = neighbour.getValeur()*2;
        neighbour.setValeur(value);
        scoreService.updateScore(value);
        checkWin(value);

        updateScreen();
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
