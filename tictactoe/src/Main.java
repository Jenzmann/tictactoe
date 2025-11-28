import spieler.Spieler;
import tictactoe.TicTacToe;
import tictactoe.spieler.AbbruchNachIterationen;
import tictactoe.spieler.ILernenderSpieler;
import tictactoe.spieler.ISpieler;
import tictactoe.spieler.beispiel.Zufallsspieler;

public static void main(String[] args) {
    ISpieler spieler1 = new Zufallsspieler("Zufall");

    ILernenderSpieler spieler2 = new Spieler("Lernender Spieler");

    TicTacToe spiel = new TicTacToe();
    ISpieler gewinner;
    int gewinne1 = 0;
    int gewinne2 = 0;
    int unentschieden = 0;

    System.out.println("Vor dem Training");
    System.out.println(spieler1.getName() + " gegen " + spieler2.getName());
    System.out.println("=======================================================");
    for (long i = 0; i < 1000; i++) {
        gewinner = spiel.neuesSpiel(spieler1, spieler2, 150, false);
        if (gewinner == spieler1) {
            gewinne1++;
        } else if (gewinner == spieler2) {
            gewinne2++;
        } else {
            unentschieden++;
        }
        gewinner = spiel.neuesSpiel(spieler2, spieler1, 150, false);
        if (gewinner == spieler1) {
            gewinne1++;
        } else if (gewinner == spieler2) {
            gewinne2++;
        } else {
            unentschieden++;
        }
    }

    System.out.println(
            "Gewinne " + spieler1.getName() + ": " + gewinne1 + ". Gewinne " + spieler2.getName() + ": " + gewinne2);
    System.out.println();

    gewinne1 = 0;
    unentschieden = 0;
    gewinne2 = 0;

    System.out.println("Starte Training mit 200000 Iterationen. Bitte haben Sie etwas Geduld!");

    long starttime = System.currentTimeMillis();
    spieler2.trainieren(new AbbruchNachIterationen(200000));
    long endtime = System.currentTimeMillis();

    System.out.println("Training beendet. Gesamtdauer in Sekunden: " + ((endtime - starttime) / 1000));
    System.out.println(spieler1.getName() + " gegen " + spieler2.getName());
    System.out.println("=======================================================");

    for (long i = 0; i < 1000; i++) {
        gewinner = spiel.neuesSpiel(spieler1, spieler2, 150, false);
        if (gewinner == spieler1) {
            gewinne1++;
        } else if (gewinner == spieler2) {
            gewinne2++;
        } else {
            unentschieden++;
        }

        gewinner = spiel.neuesSpiel(spieler2, spieler1, 150, false);
        if (gewinner == spieler1) {
            gewinne1++;
        } else if (gewinner == spieler2) {
            gewinne2++;
        } else {
            unentschieden++;
        }
    }

    System.out.println(
            "Gewinne " + spieler1.getName() + ": " + gewinne1 + ". Gewinne " + spieler2.getName() + ": " + gewinne2 + ". Unentschieden " + unentschieden);
    System.out.println();
    System.out.println("Ein Einzelspiel im DEBUG-Modus, lernender Spieler startet mit X");
    System.out.println("===============================================================");
    System.out.println("Gewonnen hat: " + spiel.neuesSpiel(spieler2, spieler1, 150, true).getName());
    System.out.println();
    System.out.println("Ein Einzelspiel im DEBUG-Modus, lernender Spieler zweiter mit O");
    System.out.println("===============================================================");
    System.out.println("Gewonnen hat: " + spiel.neuesSpiel(spieler1, spieler2, 150, true).getName());
}