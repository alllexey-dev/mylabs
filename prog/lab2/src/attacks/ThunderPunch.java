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
        return "uses Thunder Punch";
    }
}
