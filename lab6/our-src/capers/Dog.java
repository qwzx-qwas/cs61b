package capers;

import java.io.File;
import java.io.Serializable;

import static capers.CapersRepository.CAPERS_FOLDER;
import static capers.Utils.*;

/** Represents a dog that can be serialized.
 * @author
*/// 使Dog类能被序列化
public class Dog implements Serializable{

    /** Folder that dogs live in. */
    static final File DOG_FOLDER = join(CAPERS_FOLDER,"dogs");
                                         //      function in Utils)

    /** Age of dog. */
    private int age;
    /** Breed of dog. */
    private String breed;
    /** Name of dog. */
    private String name;

    /**
     * Creates a dog object with the specified parameters.
     * @param name Name of dog
     * @param breed Breed of dog
     * @param age Age of dog
     */
    public Dog(String name, String breed, int age) {
        this.age = age;
        this.breed = breed;
        this.name = name;
    }

    /**
     * Reads in and deserializes a dog from a file with name NAME in DOG_FOLDER.
     *
     * @param name Name of dog to load
     * @return Dog read from file
     */
    public static Dog fromFile(String name) {

        //查找路径下符合名字的文件
        File dogfile = join(DOG_FOLDER,name);
        //读出这个文件
        return readObject(dogfile, Dog.class);
    }

    /**
     * Increases a dog's age and celebrates!
     */
    public void haveBirthday() {
        age += 1;
        System.out.println(toString());
        System.out.println("Happy birthday! Woof! Woof!");
    }

    /**
     * Saves a dog to a file for future use.
     */
    public void saveDog() {
        //创建每一只狗的独属文件，以狗的名字命名，并添加到狗的路径上
         File dogFile = new File(join(CAPERS_FOLDER,"dogs"),name);
         //把这个dog对象存储到这个文件中，需要序列化
         writeObject(dogFile,this);
    }

    @Override
    public String toString() {
        return String.format(
            "Woof! My name is %s and I am a %s! I am %d years old! Woof!",
            name, breed, age);
    }

}
