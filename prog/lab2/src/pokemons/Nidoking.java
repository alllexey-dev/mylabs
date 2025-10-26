package pokemons;

import attacks.Rest;

public final class Nidoking extends Nidorino {

    public Nidoking(String s, int i) {
        super(s, i);
        setStats(81, 102, 77, 85, 75, 85);
        addMove(new Rest());
    }
}
