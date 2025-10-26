package attacks;

import ru.ifmo.se.pokemon.*;

public final class ThunderPunch extends PhysicalMove {

    public ThunderPunch() {
        super(Type.ELECTRIC, 75, 1.0);
    }

    @Override
    protected void applyOppEffects(Pokemon pokemon) {
        if (!pokemon.hasType(Type.ELECTRIC) && Math.random() < 0.1) {
            Effect.paralyze(pokemon);
        }
    }

    @Override
    protected String describe() {
        return "uses Thunder Punch";
    }
}
