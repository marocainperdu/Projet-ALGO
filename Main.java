public class Main {
    public static void main(String[] args) {

        // --- Q1 a Q4 : analyse + affichage ---
        System.out.println("=== Q1-Q4 : Analyse et Affichage ===\n");

        Polynome p1 = Polynome.analyser("- 4.5 * X^5 + 2 * X^4 + X^3 - X + 123.0");
        p1.afficher();
        Polynome p2 = Polynome.analyser("3 * X^2 + 2 * X^5 - 3");
        p2.afficher();
        Polynome p3 = Polynome.analyser("X^3 - 1");
        p3.afficher();
        Polynome p4 = Polynome.analyser("5");
        p4.afficher();
        Polynome p5 = Polynome.analyser("- X^2 + 3 * X - 7");
        p5.afficher();
        Polynome p6 = Polynome.analyser("- A^2 + 3 * A - 7");
        if (p6 != null) p6.afficher();
        else System.out.println("P6 = (invalide)");

        Polynome.enregistrerPoly(p1);
        Polynome.enregistrerPoly(p2);
        Polynome.enregistrerPoly(p3);
        Polynome.enregistrerPoly(p4);
        Polynome.enregistrerPoly(p5);
        if (p6 != null) Polynome.enregistrerPoly(p6);
        // --- Q5 : evaluation ---
        System.out.println("\n=== Q5 : Evaluation ===\n");
        System.out.println("P2 = " + p2);
        System.out.println("P2(0) = " + p2.eval(0));
        System.out.println("P2(1) = " + p2.eval(1));
        System.out.println("P2(2) = " + p2.eval(2));
        System.out.println("P2(-1) = " + p2.eval(-1));

        // --- Q6 : operations ---
        System.out.println("\n=== Q6 : Operations ===\n");

        Polynome a = Polynome.analyser("3 * X^3 + 2 * X^2 + X + 5");
        Polynome b = Polynome.analyser("X^2 - 3 * X + 2");
        Polynome.enregistrerPoly(a);
        Polynome.enregistrerPoly(b);

        System.out.println("A = " + a);
        System.out.println("B = " + b);

        Polynome somme = Polynome.plus(a, b);
        Polynome.enregistrerPoly(somme);
        System.out.println("A + B = " + somme);

        Polynome diff = Polynome.moins(a, b);
        Polynome.enregistrerPoly(diff);
        System.out.println("A - B = " + diff);

        Polynome prod = Polynome.fois(a, b);
        Polynome.enregistrerPoly(prod);
        System.out.println("A * B = " + prod);

        Polynome[] div = Polynome.quotient(a, b);
        Polynome.enregistrerPoly(div[0]);
        Polynome.enregistrerPoly(div[1]);
        System.out.println("A / B = " + div[0]);
        System.out.println("Reste = " + div[1]);

        // verification
        Polynome verif = Polynome.plus(Polynome.fois(b, div[0]), div[1]);
        Polynome.enregistrerPoly(verif);
        System.out.println("Verif B*Q+R = " + verif);

        // X^4-1 / X^2-1
        Polynome c = Polynome.analyser("X^4 - 1");
        Polynome d = Polynome.analyser("X^2 - 1");
        Polynome.enregistrerPoly(c);
        Polynome.enregistrerPoly(d);
        Polynome[] div2 = Polynome.quotient(c, d);
        Polynome.enregistrerPoly(div2[0]);
        Polynome.enregistrerPoly(div2[1]);
        System.out.println("\n(X^4-1) / (X^2-1) = " + div2[0]);
        System.out.println("Reste = " + div2[1]);

        // --- Q7 : garbage collector ---
        System.out.println("\n=== Q7 : Garbage Collector ===\n");
        System.out.println("Maillons avant : " + Polynome.compterMaillons());
        Polynome.garbageCollector();
        System.out.println("Maillons apres : " + Polynome.compterMaillons());

        // --- Q8 : recursif ---
        System.out.println("\n=== Q8 : Versions Recursives ===\n");

        Polynome e = Polynome.analyser("5 * X^3 + 3 * X + 1");
        Polynome f = Polynome.analyser("2 * X^3 - X^2 + 4");
        Polynome.enregistrerPoly(e);
        Polynome.enregistrerPoly(f);
        System.out.println("E = " + e);
        System.out.println("F = " + f);

        System.out.println("plusRec(E,F)  = " + Polynome.plusRec(e, f));
        System.out.println("moinsRec(E,F) = " + Polynome.moinsRec(e, f));
        System.out.println("plus(E,F)     = " + Polynome.plus(e, f));
        System.out.println("moins(E,F)    = " + Polynome.moins(e, f));

        System.out.println("\nFin du programme.");
    }
}
