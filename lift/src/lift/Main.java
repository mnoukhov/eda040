package lift;


public class Main {
    public static void main(String[] args) {
        int NO_OF_PEOPLE = 10;
        int NO_OF_FLOORS = 7;
        int MAX_LOAD = 4;
        Person[] people = new Person[NO_OF_PEOPLE];

        LiftView lv = new LiftView();
        Shared s = new Shared(NO_OF_FLOORS, MAX_LOAD, lv);
        Lift l = new Lift(s, lv);
        for (int i = 0; i < NO_OF_PEOPLE; i++) {
            people[i] = new Person(s, NO_OF_FLOORS);
            people[i].start();
        }
        l.start();
    }
}
