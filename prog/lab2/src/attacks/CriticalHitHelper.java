package attacks;

import ru.ifmo.se.pokemon.Pokemon;
import ru.ifmo.se.pokemon.Stat;

public class CriticalHitHelper {

    public static void handleCriticalHit(Pokemon target) {
        double hpPercentage = target.getHP() / target.getStat(Stat.HP);

        if (hpPercentage > 0 && hpPercentage <= 0.1) {
            System.out.println(target + ": Oof, I don't feel good...");
        }
    }
}
