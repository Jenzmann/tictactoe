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

    @Override
    public void neuesSpiel(Farbe farbe, int i) {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void setName(String s) {

    }

    @Override
    public Farbe getFarbe() {
        return null;
    }

    @Override
    public void setFarbe(Farbe farbe) {

    }

    @Override
    public Zug berechneZug(Zug zug, long l, long l1) throws IllegalerZugException {
        return null;
    }
}
