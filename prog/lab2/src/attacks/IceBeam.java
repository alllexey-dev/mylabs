package attacks;

import ru.ifmo.se.pokemon.Effect;
import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.SpecialMove;
import ru.ifmo.se.pokemon.Type;

public final class IceBeam extends SpecialMove {

    public IceBeam() {
        super(Type.ICE, 90, 1.0);
    }

    @Override
    protected void applyOppEffects(Pokemon pokemon) {
        if (Math.random() < 0.1) {
            Effect.freeze(pokemon);
        }
    }

    @Override
    protected String describe() {
        return "uses Ice Beam";
    }
}
