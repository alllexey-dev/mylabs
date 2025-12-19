import exceptions.IdentityRevealedException;
import exceptions.LoveStruckException;
import other.Location;
import other.Outfit;
import people.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Prince prince = new Prince("Принц", Location.ROYAL_ROOM);
        King king = new King("Старый Король", Location.ROYAL_ROOM);

        Servant servant = new Servant("Слуга", Location.ENTRANCE);

        Outfit zolushkaOutfit = new Outfit(
                "Серебряное платье",
                "Серебристый",
                9
        );

        MysteriousStranger zolushka = new MysteriousStranger("Прекрасная Незнакомка", Location.OTHER, zolushkaOutfit);

        Lady sister1 = new Lady("Анастасия", Location.DINING_TABLE, randomOutfit(), true);
        Lady sister2 = new Lady("Анна", Location.DINING_TABLE, randomOutfit(), true);
        Lady otherLady = new Lady("Мария", Location.DINING_TABLE, randomOutfit(), false);

        List<Person> guests = new ArrayList<>(Arrays.asList(king, sister1, sister2, otherLady));

        try {
            servant.announceArrival(zolushka);

            zolushka.moveTo(Location.ENTRANCE);
            prince.reactTo(zolushka);

            System.out.println("\n--- Реакция зала на Золушку ---");
            System.out.println(">>> Легкий шепот изумления и восторга пробежал по залу.");
            for (Person guest : guests) {
                guest.reactTo(zolushka);
            }

            prince.giveFruits(zolushka);

            System.out.println("\n--- Танец ---");
            prince.danceWith(zolushka);

            System.out.println("\n--- Ужин ---");
            System.out.println(">>> Подали ужин.");
            List<Person> diners = new ArrayList<>(guests);
            diners.add(prince);
            diners.add(zolushka);

            for (Person p : diners) {
                try {
                    p.moveTo(Location.DINING_TABLE);
                    p.eat();
                } catch (LoveStruckException e) {
                    System.out.println(">>> " + e.getMessage());
                }
            }

            System.out.println("\n--- Угощение ---");
            List<Lady> ladies = Arrays.asList(sister1, sister2, otherLady);
            zolushka.treatLadies(ladies);
        } catch (IdentityRevealedException e) {
            System.out.printf("[!] О НЕТ: %s%n", e.getMessage());
            System.out.printf(">>> %s убегает из дворца.%n", zolushka.getName());
        }
    }

    private static Outfit randomOutfit() {
        String[] styles = {"Шелк", "Бархат"};
        String[] colors = {"Красный", "Зелёный", "Розовый", "Оранжевый"};
        Random r = new Random();
        return new Outfit(styles[r.nextInt(styles.length)], colors[r.nextInt(colors.length)], r.nextInt(10));
    }
}
