package people;

import other.Emotion;
import other.Location;

public abstract class Person {

    private final String name;
    private Location location;
    private Emotion emotion;

    public Person(String name, Location location) {
        this.name = name;
        this.location = location;
        this.emotion = Emotion.CALM;
    }

    // interaction

    public abstract void reactTo(Person other);

    public void eat() {
        System.out.printf("%s приступает к ужину%n", getName());
    }

    // setters

    public void moveTo(Location location) {
        this.location = location;
        System.out.printf("%s переместился в %s%n", getName(), getLocation().getTitle());
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
        System.out.printf("%s теперь чувствует %s%n", getName(), getEmotion().getTitle());
    }

    public void setEmotionQuiet(Emotion emotion) {
        this.emotion = emotion;
    }

    // getters

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Emotion getEmotion() {
        return emotion;
    }
}
