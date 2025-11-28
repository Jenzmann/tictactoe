package spieler;

import tictactoe.Farbe;
import tictactoe.Spielstand;

public class SpielfeldCopy {
    private Farbe[][] feld = new Farbe[3][3];

    public SpielfeldCopy() {
        for (int zeile = 0; zeile < 3; ++zeile) {
            for (int spalte = 0; spalte < 3; ++spalte) {
                this.feld[zeile][spalte] = Farbe.Leer;
            }
        }

    }

    public String ToStateString() {
        StringBuilder sb = new StringBuilder(9); // exact size
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (feld[i][j] == Farbe.Leer)
                    sb.append('0');
                if (feld[i][j] == Farbe.Kreis)
                    sb.append('1');
                if (feld[i][j] == Farbe.Kreuz)
                    sb.append('2');
            }
        }
        return sb.toString();
    }

    public void setFarbe(int zeile, int spalte, Farbe farbe) {
        assert zeile >= 0 && zeile < 3 && spalte >= 0 && spalte < 3;

        this.feld[zeile][spalte] = farbe;
    }

    public Farbe getFarbe(int zeile, int spalte) {
        assert zeile >= 0 && zeile < 3 && spalte >= 0 && spalte < 3;

        return this.feld[zeile][spalte];
    }

    public Spielstand pruefeGewinn(Farbe spieler) {
        Farbe s = this.getFarbe(0, 0);
        if (s != Farbe.Leer) {
            if (this.getFarbe(0, 1) == s && this.getFarbe(0, 2) == s) {
                return spielende(spieler, s);
            }

            if (this.getFarbe(1, 1) == s && this.getFarbe(2, 2) == s) {
                return spielende(spieler, s);
            }

            if (this.getFarbe(1, 0) == s && this.getFarbe(2, 0) == s) {
                return spielende(spieler, s);
            }
        }

        s = this.getFarbe(1, 0);
        if (s != Farbe.Leer && this.getFarbe(1, 1) == s && this.getFarbe(1, 2) == s) {
            return spielende(spieler, s);
        } else {
            s = this.getFarbe(2, 0);
            if (s != Farbe.Leer) {
                if (this.getFarbe(2, 1) == s && this.getFarbe(2, 2) == s) {
                    return spielende(spieler, s);
                }

                if (this.getFarbe(1, 1) == s && this.getFarbe(0, 2) == s) {
                    return spielende(spieler, s);
                }
            }

            s = this.getFarbe(0, 1);
            if (s != Farbe.Leer && this.getFarbe(1, 1) == s && this.getFarbe(2, 1) == s) {
                return spielende(spieler, s);
            } else {
                s = this.getFarbe(0, 2);
                if (s != Farbe.Leer && this.getFarbe(1, 2) == s && this.getFarbe(2, 2) == s) {
                    return spielende(spieler, s);
                } else {
                    for (int x = 0; x < 3; ++x) {
                        for (int y = 0; y < 3; ++y) {
                            if (this.getFarbe(x, y) == Farbe.Leer) {
                                return Spielstand.OFFEN;
                            }
                        }
                    }

                    return Spielstand.UNENTSCHIEDEN;
                }
            }
        }
    }

    private static Spielstand spielende(Farbe f1, Farbe f2) {
        return f1 == f2 ? Spielstand.GEWONNEN : Spielstand.VERLOREN;
    }
}
