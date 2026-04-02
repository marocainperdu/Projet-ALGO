public class Monome {
    double coef;
    int expo;
    Monome suiv;
    Monome general;   // pour Q7
    boolean utile;    // pour Q7

    Monome(double coef, int expo) {
        this.coef = coef;
        this.expo = expo;
        this.suiv = null;
        this.general = null;
        this.utile = false;
    }
}
