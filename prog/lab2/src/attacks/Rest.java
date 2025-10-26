package attacks;

import ru.ifmo.se.pokemon.*;

public final class Rest extends StatusMove {

    public Rest() {
        super(Type.PSYCHIC, 0, 1.0);
    }

    @Override
    protected void applySelfEffects(Pokemon pokemon) {
        double hpToRestore = pokemon.getStat(Stat.HP) - pokemon.getHP();
        pokemon.setMod(Stat.HP, (int) -hpToRestore);

        Effect e = new Effect().condition(Status.SLEEP).attack(0).turns(3);
        pokemon.setCondition(e);
    }

    @Override
    protected String describe() {
        return "uses Rest and goes to sleep";
    }
}
