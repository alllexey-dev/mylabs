package attacks;

import ru.ifmo.se.pokemon.PhysicalMove;
import ru.ifmo.se.pokemon.Type;

public final class Facade extends PhysicalMove {

    public Facade() {
        super(Type.NORMAL, 70, 1.0);
    }

    @Override
    protected String describe() {
        return "uses Facade";
    }
}
