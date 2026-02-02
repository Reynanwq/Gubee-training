import java.util.ArrayList;
import java.util.List;

public class Dog extends Animal{

    @SuppressWarnings("rawtypes")
    private List toys = new ArrayList();

    @Override
    void makeSound() {
        System.out.println("Bark");
    }

    @SuppressWarnings("deprecation")
    void goForAWalk() {
        walk();
    }

    void play() {
        @SuppressWarnings("unchecked")
        List<String> names = toys;

        names.add("Ball");
    }


}
