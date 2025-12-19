package people;

import exceptions.CannotDanceException;
import exceptions.LoveStruckException;
import interfaces.FruitReceiver;
import other.Emotion;
import other.Fruit;
import other.Location;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Prince extends Person {

    private boolean isEnchanted = false;

    public Prince(String name, Location location) {
        super(name, location);
    }

    @Override
    public void reactTo(Person other) {
        if (other instanceof MysteriousStranger) {
            isEnchanted = true;
            System.out.printf(">>> %s спешит встретить гостью и проводить её во дворец.%n", getName());
            moveTo(Location.ENTRANCE);
            setEmotion(Emotion.LOVE);
        }
    }

    @Override
    public void eat() {
        if (isEnchanted && getEmotion() == Emotion.LOVE) {
            throw new LoveStruckException("Принц забыл про еду, его глаза не отрывались от незнакомки.");
        }
        super.eat();
    }

    public void danceWith(Person partner) {
        System.out.printf(">>> %s пригласил %s на танец.%n", getName(), partner.getName());
        if (partner instanceof MysteriousStranger zolushka) {
            try {
                zolushka.tryToDance();
            } catch (CannotDanceException e) {
                System.out.printf("[!] %s%n", e.getMessage());
                return;
            }
        }
        this.moveTo(Location.HALL_CENTER);
        partner.moveTo(Location.HALL_CENTER);
        if (partner instanceof MysteriousStranger) {
            System.out.println("Одно удовольствие было смотреть, как они танцуют");
        }
    }

    public void giveFruits(FruitReceiver target) {
        int count = 3 + (int) (Math.random() * 2);
        List<Fruit> fruits = IntStream.range(0, count).mapToObj(i -> randomFruit()).toList();
        target.receiveFruits(fruits, this);
    }

    private Fruit randomFruit() {
        List<Fruit> fruits = Arrays.asList(
                new Fruit("Апельсин", false),
                new Fruit("Яблоко", false),
                new Fruit("Банан", false),
                new Fruit("Манго", true),
                new Fruit("Киви", true),
                new Fruit("Маракуйя", true)
        );

        return fruits.get((int) (Math.random() * fruits.size()));
    }
}
