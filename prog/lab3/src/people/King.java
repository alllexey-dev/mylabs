package people;

import other.Emotion;
import other.Location;

public class King extends Person {

    public King(String name, Location location) {
        super(name, location);
    }

    public void whisperToQueen(String message) {
        System.out.printf(">>> %s шепчет королеве: \"%s\"%n", getName(), message);
    }

    @Override
    public void reactTo(Person other) {
        if (other instanceof MysteriousStranger) {
            setEmotion(Emotion.AMAZEMENT);
            whisperToQueen("Давно я не видел такого чуда!");
        }
    }
}
