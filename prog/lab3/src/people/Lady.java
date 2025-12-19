package people;

import exceptions.IdentityRevealedException;
import interfaces.OutfitHolder;
import other.Emotion;
import other.Fruit;
import other.Location;
import other.Outfit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Lady extends Person implements OutfitHolder {

    private final Outfit currentOutfit;
    private final Set<Outfit> wishlist = new HashSet<>();
    private final List<Fruit> fruits = new ArrayList<>();
    private final boolean isStepsister;

    public Lady(String name, Location location, Outfit outfit, boolean isStepsister) {
        super(name, location);
        this.currentOutfit = outfit;
        this.isStepsister = isStepsister;
    }

    @Override
    public void reactTo(Person other) {
        if (other instanceof MysteriousStranger zolushka) {
            examineOutfit(zolushka.getOutfit());
        } else {
            setEmotion(Emotion.AMAZEMENT);
        }
    }

    public void examineOutfit(Outfit outfit) {
        System.out.printf("%s внимательно рассматривает наряд (%s)%n", getName(), outfit.style());
        if (outfit.luxury() > currentOutfit.luxury() + 1) {
            System.out.printf(">>> %s поражена роскошностью наряда, желает заказать себе такой же%n", getName());
            setEmotion(Emotion.ENVY);
            wishlist.add(outfit);
        }
    }

    public void receiveFruit(Fruit fruit, Person giver) {
        this.fruits.add(fruit);
        System.out.printf("%s получила %s от %s%n", getName(), fruit.name(), giver.getName());

        double chance = 0.05;
        if (giver instanceof MysteriousStranger && isStepsister && Math.random() < chance) {
            throw new IdentityRevealedException(getName() + " узнала в прекрасной незнакомке Золушку!");
        }

        setEmotionQuiet(Emotion.PLEASURE);
        System.out.printf("%s зарделась от удовольствия, удостоившись такой чести.%n", getName());
    }

    @Override
    public Outfit getOutfit() {
        return currentOutfit;
    }
}
