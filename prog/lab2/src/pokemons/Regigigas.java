package pokemons;

import attacks.Confide;
import attacks.Facade;
import attacks.RockSlide;
import attacks.ThunderPunch;
import ru.ifmo.se.pokemon.Pokemon;

public final class Regigigas extends Pokemon {

    public Regigigas(String s, int i) {
        super(s, Math.min(i, 12));
        setStats(110, 160, 110, 80, 110, 100);
        this.addMove(new ThunderPunch());
        this.addMove(new RockSlide());
        this.addMove(new Facade());
        this.addMove(new Confide());
    }

}
