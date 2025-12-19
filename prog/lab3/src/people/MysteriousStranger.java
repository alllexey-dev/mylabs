package people;

import exceptions.CannotDanceException;
import interfaces.FruitReceiver;
import interfaces.OutfitHolder;
import other.Fruit;
import other.Location;
import other.Outfit;

import java.util.ArrayList;
import java.util.List;

public class MysteriousStranger extends Person implements FruitReceiver, OutfitHolder {

    private final Outfit outfit;
    private final List<Fruit> basket = new ArrayList<>();

    public MysteriousStranger(String name, Location location, Outfit outfit) {
        super(name, location);
        this.outfit = outfit;
    }

    @Override
    public void receiveFruits(List<Fruit> givenFruits, Person giver) {
        this.basket.addAll(givenFruits);
        System.out.printf(">>> %s получает корзинку фруктов (%s шт.) от %s%n", getName(), givenFruits.size(), giver.getName());
    }

    @Override
    public void reactTo(Person other) { }

    public void treatLadies(List<Lady> ladies) {
        System.out.printf(">>> %s садится за стол и достает фрукты.%n", getName());
        this.moveTo(Location.SECLUDED_CORNER);

        for (Lady lady : ladies) {
            if (basket.isEmpty()) break;
            Fruit f = basket.remove(0);
            lady.receiveFruit(f, this);
        }
    }

    public void tryToDance() throws CannotDanceException {
        if (basket.size() > 3) throw new CannotDanceException(getName() + " не может танцевать: у неё много фруктов!");
    }

    @Override
    public Outfit getOutfit() {
        return outfit;
    }
}
