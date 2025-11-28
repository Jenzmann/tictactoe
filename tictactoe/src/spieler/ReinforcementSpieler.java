package spieler;

import tictactoe.Farbe;
import tictactoe.Spielfeld;
import tictactoe.Spielstand;
import tictactoe.Zug;
import tictactoe.spieler.IAbbruchbedingung;
import tictactoe.spieler.ILernenderSpieler;

import java.io.IOException;
import java.util.Random;

public class ReinforcementSpieler implements ILernenderSpieler {
    private String name;
    private Farbe farbe;
    private Spielfeld spielfeld;

    private QLearningAgent qAgent = new QLearningAgent();

    public ReinforcementSpieler(String name) {
        this.name = name;
    }

    @Override
    public boolean trainieren(IAbbruchbedingung abbruchbedingung) {
        qAgent.setExplorationRate(0.9);
        int iteration = 0;

        while (!abbruchbedingung.abbruch()) {
            simuliereEinSpielTraining(farbe, farbe.opposite());
            qAgent.decayEpsilon();
            iteration++;

            if (iteration % 1000 == 0) System.out.print(".");
        }

        System.out.println("\nTraining abgeschlossen. Epsilon ist jetzt: " + 0.1);

        return true;
    }

    private void simuliereEinSpielTraining(Farbe farbe1, Farbe farbe2) {
        spielfeld = new Spielfeld();

        boolean agentIstDran = new Random().nextBoolean();
        Farbe agentFarbe = agentIstDran ? farbe1 : farbe2;
        Farbe gegnerFarbe = agentFarbe.opposite();

        boolean spielLaeuft = true;

        String lastState = null;
        int lastAction = -1;

        while (spielLaeuft) {
            if (agentIstDran) {
                String currentState = getBoardStateString(spielfeld);
                boolean[] validMoves = getValidMoves();

                if (lastState != null) {
                    qAgent.train(lastState, lastAction, 0.0, currentState, validMoves);
                }

                int action = qAgent.getAction(currentState, validMoves, true);

                int row = action / 3;
                int column = action % 3;
                spielfeld.setFarbe(row, column, agentFarbe);

                lastState = currentState;
                lastAction = action;

                Spielstand stand = spielfeld.pruefeGewinn(agentFarbe);
                if (stand == Spielstand.GEWONNEN) {
                    qAgent.train(lastState, lastAction, 1.0, getBoardStateString(spielfeld), new boolean[9]);
                    spielLaeuft = false;
                } else if (stand == Spielstand.UNENTSCHIEDEN) {
                    qAgent.train(lastState, lastAction, 0.5, getBoardStateString(spielfeld), new boolean[9]);
                    spielLaeuft = false;
                }

                agentIstDran = false;
            } else {
                boolean[] validMoves = getValidMoves();
                int move = getRandomMoveIndex(validMoves);

                int row = move / 3;
                int column = move % 3;
                spielfeld.setFarbe(row, column, gegnerFarbe);

                Spielstand stand = spielfeld.pruefeGewinn(gegnerFarbe);
                if (stand == Spielstand.GEWONNEN) {
                    String finalState = getBoardStateString(spielfeld);
                    qAgent.train(lastState, lastAction, -1.0, finalState, new boolean[9]);
                    spielLaeuft = false;
                } else if (stand == Spielstand.UNENTSCHIEDEN) {
                    String finalState = getBoardStateString(spielfeld);
                    qAgent.train(lastState, lastAction, 0.5, finalState, new boolean[9]);
                    spielLaeuft = false;
                }
                agentIstDran = true;
            }
        }
    }

    @Override
    public void speichereWissen(String s) throws IOException {
        qAgent.save(s);
    }

    @Override
    public void ladeWissen(String s) throws IOException {
        try {
            this.qAgent = QLearningAgent.load(s);
        } catch (ClassNotFoundException e) {
            throw new IOException("Konnte Agent nicht laden", e);
        }
    }

    @Override
    public void neuesSpiel(Farbe farbe, int i) {
        this.farbe = farbe;
        spielfeld = new Spielfeld();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
    }

    @Override
    public Farbe getFarbe() {
        return farbe;
    }

    @Override
    public void setFarbe(Farbe farbe) {
        this.farbe = farbe;
    }

    @Override
    public Zug berechneZug(Zug vorherigerZug, long zeitKreis, long zeitKreuz) {
        if (vorherigerZug != null)
            spielfeld.setFarbe(vorherigerZug.getZeile(),
                    vorherigerZug.getSpalte(),
                    farbe.opposite());
        Zug neuerZug;
        do {
            var state = boardToString();
            var validMoves = getValidMoves();
            var actionIndex = qAgent.getAction(state, validMoves, false);

            int zeile = actionIndex / 3;
            int spalte = actionIndex % 3;

            neuerZug = new Zug(zeile, spalte);
        }
        while (spielfeld.getFarbe(neuerZug.getZeile(), neuerZug.getSpalte()) != Farbe.Leer);
        spielfeld.setFarbe(neuerZug.getZeile(), neuerZug.getSpalte(), farbe);
        return neuerZug;
    }

    private String boardToString() {
        return getBoardStateString(spielfeld);
    }

    private String getBoardStateString(Spielfeld spielfeld) {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                Farbe cell = spielfeld.getFarbe(row, column);
                if (cell == Farbe.Leer) {
                    sb.append(".");
                } else {
                    // Wir nutzen den Namen der Farbe (z.B. "X", "O" oder "ROT")
                    sb.append(cell.toString().charAt(0));
                }
            }
        }
        return sb.toString();
    }

    private boolean[] getValidMoves() {
        return getValidMovesSim(spielfeld);
    }

    private boolean[] getValidMovesSim(Spielfeld spielfeld) {
        boolean[] valid = new boolean[9];
        int index = 0;
        for (int zeile = 0; zeile < 3; zeile++) {
            for (int spalte = 0; spalte < 3; spalte++) {
                valid[index] = (spielfeld.getFarbe(zeile, spalte) == Farbe.Leer);
                index++;
            }
        }
        return valid;
    }

    private int getRandomMoveIndex(boolean[] valid) {
        Random random = new Random();
        int index;
        do {
            index = random.nextInt(9);
        } while (!valid[index]);
        return index;
    }
}
