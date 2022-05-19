package modele;
import vue_controleur.Swing2048;

import java.util.*;

public class Jeu extends Observable {

    public Case[][] tabCases;
    public HashMap<Case,Point> map = new HashMap<>();

    public boolean endgame = false;

    public Jeu(int size) {
        tabCases = new Case[size][size];
        init();
    }


    public void restartGame(){
        endgame = false;
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
        //On rempli le tableau à null

        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                tabCases[i][j] = new Case(0, this);
            }
        }
        System.out.println(tabCases);
        //On ajoute 2 cases
        for (int i = 0; i < 2; i++) {
            addCase();
        }
        setChanged();
        notifyObservers();
        System.out.println(tabCases);
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
        System.out.println("ça bouge");
        checkLoose();
        if(!endgame){
            if(direction == Direction.droite) {
                for (int i = getSize() - 1 ; i >= 0; i--) {
                    for (int j = getSize() - 1; j >= 0; j--) {
                        if(tabCases[j][i].getValeur() != 0){
                            tabCases[j][i].move(direction);
                        }
                    }
                }
            }else if (direction == Direction.bas){
                for (int i = getSize() - 1 ; i >= 0; i--) {
                    for (int j = getSize() - 1; j >= 0; j--) {
                        if(tabCases[i][j].getValeur() != 0){
                            tabCases[i][j].move(direction);
                        }
                    }
                }
            }
            else if (direction == Direction.gauche){
                for (int i = 0; i < getSize(); i++) {
                    for (int j = 0; j < getSize(); j++) {
                        if(tabCases[j][i].getValeur() != 0){
                            tabCases[j][i].move(direction);
                        }
                    }
                }
            }
            else{
                for (int i = 0; i < getSize(); i++) {
                    for (int j = 0; j < getSize(); j++) {
                        if(tabCases[i][j].getValeur() != 0){
                            tabCases[i][j].move(direction);
                        }
                    }
                }
            }
            addCase();
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

    private boolean isMovable(){
        if(emptyCases().isEmpty()){
            //On vérifie chaque case
            for (int i = 0; i < getSize(); i++) {
                for (int j = 0; j < getSize(); j++) {
                    Case cell = tabCases[i][j];
                    for (Direction dir : Direction.values()) {
                        Case neighbour = this.getNeighbour(dir,cell);
                        if(cell.getValeur() == neighbour.getValeur()){
                            endgame = true;
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        return true;
    }

    private void checkLoose(){
        //On vérifie si un mouvement est possible
        if(!isMovable()){
            Swing2048.displayLoosePopup(this);
        }
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
            setChanged();
            notifyObservers();

            checkWin(value);
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
        map.put(cell, new Point(point.x, point.y));
        setChanged();
        notifyObservers();
    }

}
