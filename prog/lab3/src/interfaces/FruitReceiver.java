package interfaces;

import other.Fruit;
import people.Person;

import java.util.List;

public interface FruitReceiver {

    void receiveFruits(List<Fruit> fruits, Person giver);
}
