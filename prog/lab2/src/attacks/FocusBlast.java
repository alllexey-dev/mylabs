package attacks;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.SpecialMove;
import ru.ifmo.se.pokemon.Stat;
import ru.ifmo.se.pokemon.Type;

public final class FocusBlast extends SpecialMove {

    public FocusBlast() {
        super(Type.FIGHTING, 120, 0.7);
    }

    @Override
    protected void applyOppEffects(Pokemon pokemon) {
        if (Math.random() < 0.1) {
            pokemon.setMod(Stat.DEFENSE, -1);
        }
    }

    @Override
    protected String describe() {
        return "uses Focus Blast";
    }
}
