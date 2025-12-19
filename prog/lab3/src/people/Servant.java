package people;

import other.Location;

public class Servant extends Person {

    public Servant(String name, Location location) {
        super(name, location);
    }

    @Override
    public void reactTo(Person other) {

    }

    public void announceArrival(Person guest) {
        System.out.printf(">>> %s доложил: На бал приехала %s!%n",  getName(), guest.getName());
    }
}
