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
        return "uses Poison Sting";
    }
}
