package wtf.thepalbi;

public class ClassUnderTest2 {

    public Animal favouriteAnimal;

    public ClassUnderTest2(Animal favouriteAnimal) {
        this.favouriteAnimal = favouriteAnimal;
    }

    public static void main(String args) {
        Animal someAnimal = AnimalFactory.build();
        String animalDialogue = someAnimal.talk();
        System.out.println(animalDialogue);
    }

    public static void main2(String[] args) {
        ClassUnderTest2 test = new ClassUnderTest2(AnimalFactory.build());
        System.out.println(test);
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
