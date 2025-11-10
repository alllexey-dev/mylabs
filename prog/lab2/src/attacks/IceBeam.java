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

    private boolean criticalHit = false;

    @Override
    protected double calcCriticalHit(Pokemon p1, Pokemon p2) {
        double crit = super.calcCriticalHit(p1, p2);
        criticalHit = crit > 1;
        return crit;
    }

    @Override
    protected void applyOppDamage(Pokemon pokemon, double v) {
        super.applyOppDamage(pokemon, v);
        if (!criticalHit) return;
        CriticalHitHelper.handleCriticalHit(pokemon);
    }

    @Override
    protected String describe() {
        return "uses Ice Beam";
    }
}
