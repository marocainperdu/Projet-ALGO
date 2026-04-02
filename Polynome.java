import java.util.ArrayList;

public class Polynome {

    static class SyntaxeException extends RuntimeException {
        SyntaxeException(String msg) { super(msg); }
    }

    Monome tete;

    // variables pour Q7
    static Monome tousLesMaillons = null;
    static ArrayList<Polynome> polyUtiles = new ArrayList<>();

    // variables pour Q1
    static String src;
    static int pos;

    Polynome() {
        this.tete = null;
    }

    // ============================================================
    //                  QUESTION 7 - ALLOCATION
    // ============================================================

    static Monome creerMaillon(double c, int e) {
        Monome m = new Monome(c, e);
        m.general = tousLesMaillons;
        tousLesMaillons = m;
        return m;
    }

    static void enregistrerPoly(Polynome p) {
        if (!polyUtiles.contains(p))
            polyUtiles.add(p);
    }

    // ============================================================
    //               QUESTION 4 - INSERTION TRIEE
    // ============================================================

    void inserer(double coef, int expo) {
        if (Math.abs(coef) < 1e-15) return;

        Monome nouveau = creerMaillon(coef, expo);

        if (tete == null || expo > tete.expo) {
            nouveau.suiv = tete;
            tete = nouveau;
            return;
        }

        if (expo == tete.expo) {
            double s = tete.coef + coef;
            if (Math.abs(s) < 1e-15) {
                tete = tete.suiv;
            } else {
                nouveau.coef = s;
                nouveau.suiv = tete.suiv;
                tete = nouveau;
            }
            return;
        }

        Monome cur = tete;
        while (cur.suiv != null && cur.suiv.expo > expo)
            cur = cur.suiv;

        if (cur.suiv != null && cur.suiv.expo == expo) {
            double s = cur.suiv.coef + coef;
            if (Math.abs(s) < 1e-15) {
                cur.suiv = cur.suiv.suiv;
            } else {
                nouveau.coef = s;
                nouveau.suiv = cur.suiv.suiv;
                cur.suiv = nouveau;
            }
        } else {
            nouveau.suiv = cur.suiv;
            cur.suiv = nouveau;
        }
    }

    // ============================================================
    //          QUESTION 1 & 2 - ANALYSEUR SYNTAXIQUE
    // ============================================================

    static void erreur(String msg) {
        System.out.println("Erreur position " + pos + " : " + msg);
        System.out.println("  " + src);
        String fleche = "  ";
        for (int i = 0; i < pos; i++) fleche += " ";
        System.out.println(fleche + "^");
        throw new SyntaxeException(msg);
    }

    static void skipSpaces() {
        while (pos < src.length() && src.charAt(pos) == ' ') pos++;
    }

    static char car() {
        if (pos < src.length()) return src.charAt(pos);
        return '\0';
    }

    static int parseNaturel() {
        skipSpaces();
        if (!Character.isDigit(car())) erreur("chiffre attendu");
        int val = 0;
        while (Character.isDigit(car())) {
            val = val * 10 + (car() - '0');
            pos++;
        }
        return val;
    }

    static double parseNombre() {
        skipSpaces();
        int ent = parseNaturel();
        double val = ent;
        if (car() == '.') {
            pos++;
            double div = 10.0;
            while (Character.isDigit(car())) {
                val += (car() - '0') / div;
                div *= 10.0;
                pos++;
            }
        }
        return val;
    }

    static int parseXpuissance() {
        skipSpaces();
        if (car() != 'X' && car() != 'x') erreur("X attendu");
        pos++;
        skipSpaces();
        if (car() == '^') {
            pos++;
            return parseNaturel();
        }
        return 1;
    }

    // retourne {coef, expo}
    static double[] parseMonome() {
        skipSpaces();
        double coef;
        int expo;

        if (car() == 'X' || car() == 'x') {
            coef = 1.0;
            expo = parseXpuissance();
            return new double[]{coef, expo};
        }

        if (!Character.isDigit(car())) erreur("nombre ou X attendu");

        double nb = parseNombre();
        skipSpaces();

        if (car() == '*') {
            pos++;
            coef = nb;
            expo = parseXpuissance();
        } else {
            coef = nb;
            expo = 0;
        }
        return new double[]{coef, expo};
    }

    static Polynome analyser(String texte) {
        src = texte;
        pos = 0;
        Polynome poly = new Polynome();

        System.out.println("Analyse de : \"" + texte + "\"");

        try {
            skipSpaces();

            boolean negatif = false;
            if (car() == '-') { negatif = true; pos++; }
            else if (car() == '+') { pos++; }

            double[] m = parseMonome();
            double coef = negatif ? -m[0] : m[0];
            poly.inserer(coef, (int) m[1]);

            skipSpaces();
            while (car() == '+' || car() == '-') {
                int signe = (car() == '-') ? -1 : 1;
                pos++;
                m = parseMonome();
                poly.inserer(signe * m[0], (int) m[1]);
                skipSpaces();
            }

            skipSpaces();
            if (car() != '\0') erreur("caractere inattendu");

            System.out.println("Q1: Syntaxe correcte");
            System.out.println("Q2: Enregistre dans la liste chainee");
            System.out.println("Q4: Liste reorganisee dans l'ordre decroissant");
        } catch (SyntaxeException e) {
            System.out.println();
            return null;
        }

        return poly;
    }

    // ============================================================
    //                 QUESTION 3 - AFFICHAGE
    // ============================================================

    void afficher() {
        System.out.println("Q3: " + this);
        System.out.println();
    }

    private static String formatCoef(double val) {
        if (val == (int) val) return String.valueOf((int) val);
        return String.valueOf(val);
    }

    public String toString() {
        if (tete == null) return "0";

        String res = "";
        boolean premier = true;
        Monome p = tete;

        while (p != null) {
            double c = p.coef;
            int e = p.expo;
            double ac = Math.abs(c);

            if (!premier) res += " ";
            res += "(";

            if (premier) {
                if (c < 0) res += "- ";
            } else {
                if (c < 0) res += "- ";
                else res += "+ ";
            }

            if (e == 0) {
                res += formatCoef(ac);
            } else {
                if (ac != 1.0) {
                    res += formatCoef(ac) + " * ";
                }
                if (e == 1) res += "X";
                else res += "X^" + e;
            }

            res += ")";
            premier = false;
            p = p.suiv;
        }
        return res;
    }

    // ============================================================
    //                 QUESTION 5 - EVALUATION
    // ============================================================

    double eval(double x) {
        double res = 0.0;
        Monome p = tete;
        while (p != null) {
            res += p.coef * Math.pow(x, p.expo);
            p = p.suiv;
        }
        return res;
    }

    // ============================================================
    //                 QUESTION 6 - OPERATIONS
    // ============================================================

    Polynome copier() {
        Polynome res = new Polynome();
        Monome p = tete;
        while (p != null) {
            res.inserer(p.coef, p.expo);
            p = p.suiv;
        }
        return res;
    }

    static Polynome plus(Polynome a, Polynome b) {
        Polynome res = new Polynome();
        Monome pa = a.tete, pb = b.tete;

        while (pa != null && pb != null) {
            if (pa.expo > pb.expo) {
                res.inserer(pa.coef, pa.expo);
                pa = pa.suiv;
            } else if (pa.expo < pb.expo) {
                res.inserer(pb.coef, pb.expo);
                pb = pb.suiv;
            } else {
                double s = pa.coef + pb.coef;
                if (Math.abs(s) > 1e-15)
                    res.inserer(s, pa.expo);
                pa = pa.suiv;
                pb = pb.suiv;
            }
        }
        while (pa != null) { res.inserer(pa.coef, pa.expo); pa = pa.suiv; }
        while (pb != null) { res.inserer(pb.coef, pb.expo); pb = pb.suiv; }
        return res;
    }

    static Polynome moins(Polynome a, Polynome b) {
        Polynome res = new Polynome();
        Monome pa = a.tete, pb = b.tete;

        while (pa != null && pb != null) {
            if (pa.expo > pb.expo) {
                res.inserer(pa.coef, pa.expo);
                pa = pa.suiv;
            } else if (pa.expo < pb.expo) {
                res.inserer(-pb.coef, pb.expo);
                pb = pb.suiv;
            } else {
                double d = pa.coef - pb.coef;
                if (Math.abs(d) > 1e-15)
                    res.inserer(d, pa.expo);
                pa = pa.suiv;
                pb = pb.suiv;
            }
        }
        while (pa != null) { res.inserer(pa.coef, pa.expo); pa = pa.suiv; }
        while (pb != null) { res.inserer(-pb.coef, pb.expo); pb = pb.suiv; }
        return res;
    }

    static Polynome foisMonome(Polynome p, double coef, int expo) {
        Polynome res = new Polynome();
        Monome cur = p.tete;
        while (cur != null) {
            res.inserer(cur.coef * coef, cur.expo + expo);
            cur = cur.suiv;
        }
        return res;
    }

    static Polynome fois(Polynome a, Polynome b) {
        Polynome res = new Polynome();
        Monome pa = a.tete;
        while (pa != null) {
            Polynome tmp = foisMonome(b, pa.coef, pa.expo);
            res = plus(res, tmp);
            pa = pa.suiv;
        }
        return res;
    }

    // retourne {quotient, reste}
    static Polynome[] quotient(Polynome a, Polynome b) {
        if (b.tete == null) {
            System.out.println("Erreur : division par zero");
            System.exit(1);
        }
        Polynome q = new Polynome();
        Polynome r = a.copier();

        while (r.tete != null && r.tete.expo >= b.tete.expo) {
            double cq = r.tete.coef / b.tete.coef;
            int eq = r.tete.expo - b.tete.expo;
            q.inserer(cq, eq);
            Polynome prod = foisMonome(b, cq, eq);
            r = moins(r, prod);
        }
        return new Polynome[]{q, r};
    }

    // ============================================================
    //             QUESTION 7 - GARBAGE COLLECTOR
    // ============================================================

    static void garbageCollector() {
        // effacer les marques
        Monome m = tousLesMaillons;
        while (m != null) {
            m.utile = false;
            m = m.general;
        }

        // marquer les utiles
        for (Polynome p : polyUtiles) {
            Monome cur = p.tete;
            while (cur != null) {
                cur.utile = true;
                cur = cur.suiv;
            }
        }

        // retirer les non marques
        Monome prec = null;
        m = tousLesMaillons;
        int liberes = 0;
        while (m != null) {
            Monome suiv = m.general;
            if (!m.utile) {
                if (prec == null)
                    tousLesMaillons = suiv;
                else
                    prec.general = suiv;
                m.general = null;
                liberes++;
            } else {
                m.utile = false;
                prec = m;
            }
            m = suiv;
        }
        System.out.println("Maillons liberes : " + liberes);
    }

    static int compterMaillons() {
        int n = 0;
        Monome m = tousLesMaillons;
        while (m != null) { n++; m = m.general; }
        return n;
    }

    // ============================================================
    //        QUESTION 8 - PLUS ET MOINS RECURSIFS
    // ============================================================

    static Monome plusRecAux(Monome a, Monome b) {
        if (a == null && b == null) return null;

        if (a == null) {
            Monome m = creerMaillon(b.coef, b.expo);
            m.suiv = plusRecAux(null, b.suiv);
            return m;
        }
        if (b == null) {
            Monome m = creerMaillon(a.coef, a.expo);
            m.suiv = plusRecAux(a.suiv, null);
            return m;
        }

        if (a.expo > b.expo) {
            Monome m = creerMaillon(a.coef, a.expo);
            m.suiv = plusRecAux(a.suiv, b);
            return m;
        }
        if (a.expo < b.expo) {
            Monome m = creerMaillon(b.coef, b.expo);
            m.suiv = plusRecAux(a, b.suiv);
            return m;
        }

        double s = a.coef + b.coef;
        if (Math.abs(s) < 1e-15)
            return plusRecAux(a.suiv, b.suiv);
        Monome m = creerMaillon(s, a.expo);
        m.suiv = plusRecAux(a.suiv, b.suiv);
        return m;
    }

    static Polynome plusRec(Polynome a, Polynome b) {
        Polynome res = new Polynome();
        res.tete = plusRecAux(a.tete, b.tete);
        return res;
    }

    static Monome moinsRecAux(Monome a, Monome b) {
        if (a == null && b == null) return null;

        if (a == null) {
            Monome m = creerMaillon(-b.coef, b.expo);
            m.suiv = moinsRecAux(null, b.suiv);
            return m;
        }
        if (b == null) {
            Monome m = creerMaillon(a.coef, a.expo);
            m.suiv = moinsRecAux(a.suiv, null);
            return m;
        }

        if (a.expo > b.expo) {
            Monome m = creerMaillon(a.coef, a.expo);
            m.suiv = moinsRecAux(a.suiv, b);
            return m;
        }
        if (a.expo < b.expo) {
            Monome m = creerMaillon(-b.coef, b.expo);
            m.suiv = moinsRecAux(a, b.suiv);
            return m;
        }

        double d = a.coef - b.coef;
        if (Math.abs(d) < 1e-15)
            return moinsRecAux(a.suiv, b.suiv);
        Monome m = creerMaillon(d, a.expo);
        m.suiv = moinsRecAux(a.suiv, b.suiv);
        return m;
    }

    static Polynome moinsRec(Polynome a, Polynome b) {
        Polynome res = new Polynome();
        res.tete = moinsRecAux(a.tete, b.tete);
        return res;
    }
}
