package attacks;

import ru.ifmo.se.pokemon.*;

public final class Flatter extends StatusMove {

    public Flatter() {
        super(Type.DARK, 0, 1.0);
    }

    @Override
    protected void applyOppEffects(Pokemon pokemon) {
        pokemon.setMod(Stat.SPECIAL_ATTACK, +1);
        Effect.confuse(pokemon);
    }

    @Override
    protected String describe() {
        return "uses Flatter";
    }
}
