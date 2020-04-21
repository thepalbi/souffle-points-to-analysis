package wtf.thepalbi;

public class ClassUnderTest2 {
    public static void main(String args) {
        Animal someAnimal = AnimalFactory.build();
        String animalDialogue = someAnimal.talk();
        System.out.println(animalDialogue);
    }
}

class AnimalFactory {
    public static Animal build() {
        return new Dog();
    }
}

interface Animal {
    String talk();
}

class Dog implements Animal {

    @Override
    public String talk() {
        return "guau";
    }
}

class Cat implements Animal {
    @Override
    public String talk() {
        return "miau";
    }
}
