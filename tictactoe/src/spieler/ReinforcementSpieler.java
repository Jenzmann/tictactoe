package spieler;

import tictactoe.Farbe;
import tictactoe.IllegalerZugException;
import tictactoe.Spielstand;
import tictactoe.Zug;
import tictactoe.spieler.IAbbruchbedingung;
import tictactoe.spieler.ILernenderSpieler;
import tictactoe.spieler.ISpieler;
import tictactoe.spieler.beispiel.Zufallsspieler;

import java.io.IOException;
import java.util.ArrayList;

public class ReinforcementSpieler implements ILernenderSpieler {
    private String name;
    private Farbe farbe;
    private QLearningAgent agent;
    private StringBuilder state = new StringBuilder();
    public boolean isTraining = false;

    public ReinforcementSpieler(String name) {
        this.name = name;
        agent = new QLearningAgent();
    }

    @Override
    public boolean trainieren(IAbbruchbedingung iAbbruchbedingung) {
        this.isTraining = true;
        ISpieler enemy = new Zufallsspieler("Zufall");

        GameResult gameResult;

        var statesAndActions = new ArrayList<StateAndAction>();

        while (!iAbbruchbedingung.abbruch()) {
            System.out.print(".");
            gameResult = this.neuesSpiel(enemy, 150);
            if (gameResult.result == 0) {
                for (var stateAndAction : statesAndActions) {
                    agent.train(stateAndAction.state, stateAndAction.action, 0);
                }

            } else if (gameResult.result == 2) {
                for (var stateAndAction : statesAndActions) {
                    agent.train(stateAndAction.state, stateAndAction.action, 1);
                }
            } else if (gameResult.result == 1) {
                for (var stateAndAction : statesAndActions) {
                    agent.train(stateAndAction.state, stateAndAction.action, 0.5);
                }
            }
        }
        this.isTraining = false;
        return true;
    }

    @Override
    public void speichereWissen(String s) throws IOException {

    }

    @Override
    public void ladeWissen(String s) throws IOException {

    }

    // DONE
    @Override
    public void neuesSpiel(Farbe farbe, int i) {
        this.farbe = farbe;
        state = new StringBuilder("000000000");
    }

    // DONE
    @Override
    public String getName() {
        return this.name;
    }

    // DONE
    @Override
    public void setName(String s) {
        this.name = s;
    }

    // DONE
    @Override
    public Farbe getFarbe() {
        return farbe;
    }

    // DONE
    @Override
    public void setFarbe(Farbe farbe) {
        this.farbe = farbe;
    }

    @Override
    public Zug berechneZug(Zug zug, long l, long l1) throws IllegalerZugException {
        if (zug != null) {
            var index = zug.getSpalte() + zug.getZeile() * 3;
            state.setCharAt(index, '2');
        }
        var validMoves = new boolean[9];
        for (int i = 0; i < 9; i++) {
            validMoves[i] = state.charAt(i) == '0';
        }
        var zugIndex = agent.getAction(state.toString(), validMoves, this.isTraining);
        state.setCharAt(zugIndex, '1');
        return new Zug(zugIndex / 3, zugIndex % 3);
    }

    // ------------------------COPIED FROM LIB--------------------------
    private SpielfeldCopy feld;

    // 0 lose
    // 1 draw
    // 2 win
    private GameResult neuesSpiel(ISpieler enemy, int bedenkzeitInSek) {
        var statesAndActions = new ArrayList<StateAndAction>();

        feld = new SpielfeldCopy();

        enemy.neuesSpiel(Farbe.Kreuz, bedenkzeitInSek);
        this.neuesSpiel(Farbe.Kreis, bedenkzeitInSek);

        try {
            Zug vorherigerZug = null;

            Spielstand stand;
            do {
                vorherigerZug = enemy.berechneZug(vorherigerZug, 0L, 0L);
                stand = this.machZug(enemy.getFarbe(), vorherigerZug);

                if (stand == Spielstand.GEWONNEN) {
                    return new GameResult(0, statesAndActions);
                }

                if (stand == Spielstand.UNENTSCHIEDEN) {
                    return new GameResult(1, statesAndActions);
                }

                vorherigerZug = this.berechneZug(vorherigerZug, 0L, 0L);
                var action = vorherigerZug.getSpalte() + vorherigerZug.getZeile() * 3;
                // var state = feld.ToStateString();
                statesAndActions.add(new StateAndAction(this.state.toString(), action));
                stand = this.machZug(this.getFarbe(), vorherigerZug);

                if (stand == Spielstand.GEWONNEN) {
                    return new GameResult(2, statesAndActions);
                }
            } while (stand != Spielstand.UNENTSCHIEDEN);

            return new GameResult(1, statesAndActions);
        } catch (Exception var7) {
            var7.printStackTrace();
            return new GameResult(1, statesAndActions);
        }
    }

    private Spielstand machZug(Farbe spieler, Zug zug) throws IllegalerZugException {
        if (this.feld.getFarbe(zug.getZeile(), zug.getSpalte()) == Farbe.Leer) {
            this.feld.setFarbe(zug.getZeile(), zug.getSpalte(), spieler);
            return this.feld.pruefeGewinn(spieler);
        } else {
            throw new IllegalerZugException();
        }
    }
}