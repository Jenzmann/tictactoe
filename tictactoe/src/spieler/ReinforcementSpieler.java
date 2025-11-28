package spieler;

import tictactoe.Farbe;
import tictactoe.IllegalerZugException;
import tictactoe.Zug;
import tictactoe.spieler.IAbbruchbedingung;
import tictactoe.spieler.ILernenderSpieler;

import java.io.IOException;

public class ReinforcementSpieler implements ILernenderSpieler {
    private String name;
    private Farbe farbe;

    public ReinforcementSpieler(String name) {
        this.name = name;
    }

    @Override
    public boolean trainieren(IAbbruchbedingung iAbbruchbedingung) {
        return false;
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
        return null;
    }
}
