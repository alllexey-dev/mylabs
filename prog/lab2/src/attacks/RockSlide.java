package attacks;

import ru.ifmo.se.pokemon.*;

public final class RockSlide extends PhysicalMove {

    public RockSlide() {
        super(Type.ROCK, 75, 0.9);
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        if (Math.random() < 0.3) {
            Effect.flinch(p);
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
        return "uses Rock Slide";
    }
}
