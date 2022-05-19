package modele;

public class Case {


    private int valeur;

    private Jeu game;

    public Case(int _valeur, Jeu _game) {
        valeur = _valeur;
        game = _game;
    }

    public int getValeur() {
        return valeur;
    }
    public void setValeur(int valeur) {
        this.valeur = valeur;
    }

    public void move(Direction direction){
        Case neighbour = null;
        //La valeur -1 est renvoyé par getNeighbour lorsque la case est au bord
        while(neighbour == null || neighbour.getValeur() == 0){
            neighbour = game.getNeighbour(direction, this);
            if(neighbour != null){
                if (neighbour.getValeur() == 0){
                    game.moveCase(direction, this);
                }
                else{
                    //Case non vide
                    game.fusion(this, neighbour);
                }
            }
        }
    }
}
