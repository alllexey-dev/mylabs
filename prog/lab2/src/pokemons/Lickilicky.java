package pokemons;

import attacks.FocusBlast;

public final class Lickilicky extends Lickitung {

    public Lickilicky(String s, int i) {
        super(s, i);
        setStats(110, 85, 95, 80, 95, 50);
        this.addMove(new FocusBlast());
    }
}
