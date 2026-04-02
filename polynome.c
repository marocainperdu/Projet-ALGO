#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <ctype.h>

// structure d'un maillon
typedef struct Maillon {
    double coef;
    int expo;
    struct Maillon *suiv;
    struct Maillon *general;  // pour Q7
    int utile;                // pour Q7
} Maillon;

typedef Maillon *POINTEUR;

// variables globales pour Q7
POINTEUR tousLesMaillons = NULL;
POINTEUR polyUtile[100];
int nbPolyUtiles = 0;

// variables globales pour Q1
char *src;
int pos;

// ============================================================
//                    QUESTION 7 - ALLOCATION
// ============================================================

POINTEUR creerMaillon(double c, int e) {
    POINTEUR m = (POINTEUR)malloc(sizeof(Maillon));
    m->coef = c;
    m->expo = e;
    m->suiv = NULL;
    m->utile = 0;
    m->general = tousLesMaillons;
    tousLesMaillons = m;
    return m;
}

// ============================================================
//                    QUESTION 4 - INSERTION TRIEE
// ============================================================

POINTEUR inserer(POINTEUR poly, double coef, int expo) {
    if (fabs(coef) < 1e-15)
        return poly;

    POINTEUR nouveau = creerMaillon(coef, expo);

    if (poly == NULL || expo > poly->expo) {
        nouveau->suiv = poly;
        return nouveau;
    }

    if (expo == poly->expo) {
        double s = poly->coef + coef;
        if (fabs(s) < 1e-15)
            return poly->suiv;
        nouveau->coef = s;
        nouveau->suiv = poly->suiv;
        return nouveau;
    }

    POINTEUR cur = poly;
    while (cur->suiv != NULL && cur->suiv->expo > expo)
        cur = cur->suiv;

    if (cur->suiv != NULL && cur->suiv->expo == expo) {
        double s = cur->suiv->coef + coef;
        if (fabs(s) < 1e-15) {
            cur->suiv = cur->suiv->suiv;
        } else {
            nouveau->coef = s;
            nouveau->suiv = cur->suiv->suiv;
            cur->suiv = nouveau;
        }
    } else {
        nouveau->suiv = cur->suiv;
        cur->suiv = nouveau;
    }
    return poly;
}

// ============================================================
//            QUESTION 1 & 2 - ANALYSEUR SYNTAXIQUE
// ============================================================

void erreur(char *msg) {
    printf("Erreur position %d : %s\n", pos, msg);
    printf("  %s\n", src);
    printf("  ");
    for (int i = 0; i < pos; i++) printf(" ");
    printf("^\n");
    exit(1);
}

void skipSpaces() {
    while (src[pos] == ' ') pos++;
}

char car() {
    return src[pos];
}

// naturel -> chiffre { chiffre }
int parseNaturel() {
    skipSpaces();
    if (!isdigit(car())) erreur("chiffre attendu");
    int val = 0;
    while (isdigit(car())) {
        val = val * 10 + (car() - '0');
        pos++;
    }
    return val;
}

// nombre -> naturel [ '.' { chiffre } ]
double parseNombre() {
    skipSpaces();
    int ent = parseNaturel();
    double val = ent;
    if (car() == '.') {
        pos++;
        double div = 10.0;
        while (isdigit(car())) {
            val += (car() - '0') / div;
            div *= 10.0;
            pos++;
        }
    }
    return val;
}

// xpuissance -> 'X' | 'X' '^' naturel
int parseXpuissance() {
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

// monome -> nombre '*' xpuissance | xpuissance | nombre
void parseMonome(double *coef, int *expo) {
    skipSpaces();

    if (car() == 'X' || car() == 'x') {
        *coef = 1.0;
        *expo = parseXpuissance();
        return;
    }

    if (!isdigit(car())) erreur("nombre ou X attendu");

    double nb = parseNombre();
    skipSpaces();

    if (car() == '*') {
        pos++;
        *coef = nb;
        *expo = parseXpuissance();
    } else {
        *coef = nb;
        *expo = 0;
    }
}

// polynome -> [ '-' ] monome { ( '+' | '-' ) monome }
POINTEUR analyser(char *texte) {
    src = texte;
    pos = 0;
    POINTEUR poly = NULL;
    double coef;
    int expo;

    skipSpaces();

    int negatif = 0;
    if (car() == '-') { negatif = 1; pos++; }
    else if (car() == '+') { pos++; }

    parseMonome(&coef, &expo);
    if (negatif) coef = -coef;
    poly = inserer(poly, coef, expo);

    skipSpaces();
    while (car() == '+' || car() == '-') {
        int signe = (car() == '-') ? -1 : 1;
        pos++;
        parseMonome(&coef, &expo);
        poly = inserer(poly, signe * coef, expo);
        skipSpaces();
    }

    skipSpaces();
    if (car() != '\0') erreur("caractere inattendu");

    return poly;
}

// ============================================================
//                 QUESTION 3 - AFFICHAGE
// ============================================================

void afficher(POINTEUR p) {
    if (p == NULL) {
        printf("0");
        return;
    }
    int premier = 1;
    while (p != NULL) {
        double c = p->coef;
        int e = p->expo;
        double ac = fabs(c);

        if (premier) {
            if (c < 0) printf("- ");
            premier = 0;
        } else {
            if (c < 0) printf(" - ");
            else printf(" + ");
        }

        if (e == 0) {
            if (ac == (int)ac) printf("%d", (int)ac);
            else printf("%g", ac);
        } else {
            if (ac != 1.0) {
                if (ac == (int)ac) printf("%d", (int)ac);
                else printf("%g", ac);
                printf("*");
            }
            if (e == 1) printf("X");
            else printf("X^%d", e);
        }
        p = p->suiv;
    }
}

void afficherLn(POINTEUR p) {
    afficher(p);
    printf("\n");
}

// ============================================================
//                 QUESTION 5 - EVALUATION
// ============================================================

double eval(POINTEUR p, double x) {
    double res = 0.0;
    while (p != NULL) {
        res += p->coef * pow(x, p->expo);
        p = p->suiv;
    }
    return res;
}

// ============================================================
//                 QUESTION 6 - OPERATIONS
// ============================================================

POINTEUR copier(POINTEUR p) {
    POINTEUR res = NULL;
    POINTEUR last = NULL;
    while (p != NULL) {
        POINTEUR m = creerMaillon(p->coef, p->expo);
        if (res == NULL) res = m;
        else last->suiv = m;
        last = m;
        p = p->suiv;
    }
    return res;
}

POINTEUR plus(POINTEUR a, POINTEUR b) {
    POINTEUR res = NULL;
    while (a != NULL && b != NULL) {
        if (a->expo > b->expo) {
            res = inserer(res, a->coef, a->expo);
            a = a->suiv;
        } else if (a->expo < b->expo) {
            res = inserer(res, b->coef, b->expo);
            b = b->suiv;
        } else {
            double s = a->coef + b->coef;
            if (fabs(s) > 1e-15)
                res = inserer(res, s, a->expo);
            a = a->suiv;
            b = b->suiv;
        }
    }
    while (a != NULL) { res = inserer(res, a->coef, a->expo); a = a->suiv; }
    while (b != NULL) { res = inserer(res, b->coef, b->expo); b = b->suiv; }
    return res;
}

POINTEUR moins(POINTEUR a, POINTEUR b) {
    POINTEUR res = NULL;
    while (a != NULL && b != NULL) {
        if (a->expo > b->expo) {
            res = inserer(res, a->coef, a->expo);
            a = a->suiv;
        } else if (a->expo < b->expo) {
            res = inserer(res, -(b->coef), b->expo);
            b = b->suiv;
        } else {
            double d = a->coef - b->coef;
            if (fabs(d) > 1e-15)
                res = inserer(res, d, a->expo);
            a = a->suiv;
            b = b->suiv;
        }
    }
    while (a != NULL) { res = inserer(res, a->coef, a->expo); a = a->suiv; }
    while (b != NULL) { res = inserer(res, -(b->coef), b->expo); b = b->suiv; }
    return res;
}

POINTEUR foisMonome(POINTEUR p, double coef, int expo) {
    POINTEUR res = NULL;
    while (p != NULL) {
        res = inserer(res, p->coef * coef, p->expo + expo);
        p = p->suiv;
    }
    return res;
}

POINTEUR fois(POINTEUR a, POINTEUR b) {
    POINTEUR res = NULL;
    while (a != NULL) {
        POINTEUR tmp = foisMonome(b, a->coef, a->expo);
        res = plus(res, tmp);
        a = a->suiv;
    }
    return res;
}

POINTEUR quotient(POINTEUR a, POINTEUR b, POINTEUR *reste) {
    if (b == NULL) {
        printf("Erreur : division par zero\n");
        exit(1);
    }
    POINTEUR q = NULL;
    POINTEUR r = copier(a);

    while (r != NULL && r->expo >= b->expo) {
        double cq = r->coef / b->coef;
        int eq = r->expo - b->expo;
        q = inserer(q, cq, eq);
        POINTEUR prod = foisMonome(b, cq, eq);
        r = moins(r, prod);
    }
    *reste = r;
    return q;
}

// ============================================================
//             QUESTION 7 - GARBAGE COLLECTOR
// ============================================================

void enregistrerPoly(POINTEUR p) {
    polyUtile[nbPolyUtiles++] = p;
}

void garbageCollector() {
    // effacer les marques
    POINTEUR m = tousLesMaillons;
    while (m != NULL) {
        m->utile = 0;
        m = m->general;
    }

    // marquer les utiles
    for (int i = 0; i < nbPolyUtiles; i++) {
        POINTEUR p = polyUtile[i];
        while (p != NULL) {
            p->utile = 1;
            p = p->suiv;
        }
    }

    // liberer les non marques
    POINTEUR prec = NULL;
    m = tousLesMaillons;
    int liberes = 0;
    while (m != NULL) {
        POINTEUR suiv = m->general;
        if (!m->utile) {
            if (prec == NULL)
                tousLesMaillons = suiv;
            else
                prec->general = suiv;
            free(m);
            liberes++;
        } else {
            m->utile = 0;
            prec = m;
        }
        m = suiv;
    }
    printf("Maillons liberes : %d\n", liberes);
}

// ============================================================
//          QUESTION 8 - PLUS ET MOINS RECURSIFS
// ============================================================

POINTEUR plusRec(POINTEUR a, POINTEUR b) {
    if (a == NULL && b == NULL) return NULL;

    if (a == NULL) {
        POINTEUR m = creerMaillon(b->coef, b->expo);
        m->suiv = plusRec(NULL, b->suiv);
        return m;
    }
    if (b == NULL) {
        POINTEUR m = creerMaillon(a->coef, a->expo);
        m->suiv = plusRec(a->suiv, NULL);
        return m;
    }

    if (a->expo > b->expo) {
        POINTEUR m = creerMaillon(a->coef, a->expo);
        m->suiv = plusRec(a->suiv, b);
        return m;
    }
    if (a->expo < b->expo) {
        POINTEUR m = creerMaillon(b->coef, b->expo);
        m->suiv = plusRec(a, b->suiv);
        return m;
    }

    // meme exposant
    double s = a->coef + b->coef;
    if (fabs(s) < 1e-15)
        return plusRec(a->suiv, b->suiv);
    POINTEUR m = creerMaillon(s, a->expo);
    m->suiv = plusRec(a->suiv, b->suiv);
    return m;
}

POINTEUR moinsRec(POINTEUR a, POINTEUR b) {
    if (a == NULL && b == NULL) return NULL;

    if (a == NULL) {
        POINTEUR m = creerMaillon(-(b->coef), b->expo);
        m->suiv = moinsRec(NULL, b->suiv);
        return m;
    }
    if (b == NULL) {
        POINTEUR m = creerMaillon(a->coef, a->expo);
        m->suiv = moinsRec(a->suiv, NULL);
        return m;
    }

    if (a->expo > b->expo) {
        POINTEUR m = creerMaillon(a->coef, a->expo);
        m->suiv = moinsRec(a->suiv, b);
        return m;
    }
    if (a->expo < b->expo) {
        POINTEUR m = creerMaillon(-(b->coef), b->expo);
        m->suiv = moinsRec(a, b->suiv);
        return m;
    }

    double d = a->coef - b->coef;
    if (fabs(d) < 1e-15)
        return moinsRec(a->suiv, b->suiv);
    POINTEUR m = creerMaillon(d, a->expo);
    m->suiv = moinsRec(a->suiv, b->suiv);
    return m;
}

// ============================================================
//                         MAIN
// ============================================================

int main() {

    // --- Q1 a Q4 : analyse + affichage ---
    printf("=== Q1-Q4 : Analyse et Affichage ===\n\n");

    POINTEUR p1 = analyser("- 4.5 * X^5 + 2 * X^4 + X^3 - X + 123.0");
    printf("P1 = "); afficherLn(p1);

    POINTEUR p2 = analyser("3 * X^2 + 2 * X + 1");
    printf("P2 = "); afficherLn(p2);

    POINTEUR p3 = analyser("X^3 - 1");
    printf("P3 = "); afficherLn(p3);

    POINTEUR p4 = analyser("5");
    printf("P4 = "); afficherLn(p4);

    POINTEUR p5 = analyser("- X^2 + 3 * X - 7");
    printf("P5 = "); afficherLn(p5);

    enregistrerPoly(p1);
    enregistrerPoly(p2);
    enregistrerPoly(p3);
    enregistrerPoly(p4);
    enregistrerPoly(p5);

    // --- Q5 : evaluation ---
    printf("\n=== Q5 : Evaluation ===\n\n");
    printf("P2 = "); afficherLn(p2);
    printf("P2(0) = %g\n", eval(p2, 0));
    printf("P2(1) = %g\n", eval(p2, 1));
    printf("P2(2) = %g\n", eval(p2, 2));
    printf("P2(-1) = %g\n", eval(p2, -1));

    // --- Q6 : operations ---
    printf("\n=== Q6 : Operations ===\n\n");

    POINTEUR a = analyser("3 * X^3 + 2 * X^2 + X + 5");
    POINTEUR b = analyser("X^2 - 3 * X + 2");
    enregistrerPoly(a);
    enregistrerPoly(b);

    printf("A = "); afficherLn(a);
    printf("B = "); afficherLn(b);

    POINTEUR somme = plus(a, b);
    enregistrerPoly(somme);
    printf("A + B = "); afficherLn(somme);

    POINTEUR diff = moins(a, b);
    enregistrerPoly(diff);
    printf("A - B = "); afficherLn(diff);

    POINTEUR prod = fois(a, b);
    enregistrerPoly(prod);
    printf("A * B = "); afficherLn(prod);

    POINTEUR reste;
    POINTEUR q = quotient(a, b, &reste);
    enregistrerPoly(q);
    enregistrerPoly(reste);
    printf("A / B = "); afficherLn(q);
    printf("Reste = "); afficherLn(reste);

    // verification : B*Q + R = A
    POINTEUR verif = plus(fois(b, q), reste);
    enregistrerPoly(verif);
    printf("Verif B*Q+R = "); afficherLn(verif);

    // X^4 - 1 divise par X^2 - 1
    POINTEUR c = analyser("X^4 - 1");
    POINTEUR d = analyser("X^2 - 1");
    enregistrerPoly(c);
    enregistrerPoly(d);
    POINTEUR reste2;
    POINTEUR q2 = quotient(c, d, &reste2);
    enregistrerPoly(q2);
    enregistrerPoly(reste2);
    printf("\n(X^4-1) / (X^2-1) = "); afficherLn(q2);
    printf("Reste = "); afficherLn(reste2);

    // --- Q7 : garbage collector ---
    printf("\n=== Q7 : Garbage Collector ===\n\n");

    int avant = 0;
    POINTEUR m = tousLesMaillons;
    while (m != NULL) { avant++; m = m->general; }
    printf("Maillons avant : %d\n", avant);

    garbageCollector();

    int apres = 0;
    m = tousLesMaillons;
    while (m != NULL) { apres++; m = m->general; }
    printf("Maillons apres : %d\n", apres);

    // --- Q8 : recursif ---
    printf("\n=== Q8 : Versions Recursives ===\n\n");

    POINTEUR e = analyser("5 * X^3 + 3 * X + 1");
    POINTEUR f = analyser("2 * X^3 - X^2 + 4");
    enregistrerPoly(e);
    enregistrerPoly(f);
    printf("E = "); afficherLn(e);
    printf("F = "); afficherLn(f);

    printf("plusRec(E,F)  = "); afficherLn(plusRec(e, f));
    printf("moinsRec(E,F) = "); afficherLn(moinsRec(e, f));
    printf("plus(E,F)     = "); afficherLn(plus(e, f));
    printf("moins(E,F)    = "); afficherLn(moins(e, f));

    printf("\nFin du programme.\n");
    return 0;
}
