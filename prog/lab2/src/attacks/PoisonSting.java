package attacks;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Type;

public final class PoisonSting extends PhysicalMove {

    public PoisonSting() {
        super(Type.POISON, 15, 1.0);
    }

    @Override
    protected void applyOppEffects(Pokemon pokemon) {
        if (Math.random() < 0.3) {
            Effect.poison(pokemon);
        }
    }

    @Override
    protected String describe() {
        return "uses Poison Sting";
    }
}
