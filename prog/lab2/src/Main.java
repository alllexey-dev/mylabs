import pokemons.*;
import ru.ifmo.se.pokemon.Battle;
import ru.ifmo.se.pokemon.Pokemon;

// 30306
public final class Main {
    public static void main(String[] args) {
        Battle b = new Battle();
        Pokemon p1 = new Lickilicky("bob", 40);
        Pokemon p2 = new Nidoking("alex", 40);
        Pokemon p3 = new Regigigas("rick", 40);
        Pokemon p4 = new Nidorino("mickey", 40);
        b.addAlly(p1);
        b.addAlly(p2);
        b.addFoe(p3);
        b.addFoe(p4);
        b.go();
    }
}