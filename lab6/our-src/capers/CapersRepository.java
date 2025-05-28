package capers;

import java.io.File;

import static capers.Dog.DOG_FOLDER;
import static capers.Utils.*;

/** A repository for Capers 
 * @author
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 *
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = join(CWD,".caper");
     //cwd（当前目录）下隐藏文件caper的路径,加"."代表隐藏文件                      //      function in Utils
    //将这俩文件放到一个路径上
    /**
     * Does required filesystem operations to allow for persistence.
     * 做一些文件的操作，让我们能持久保持数据
     * (creates any necessary folders or files)
     * 创建需要的文件夹或文件
     * Remember: recommended structure (you do not have to follow):
     *推荐的文件结构如下
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *             最上层的文件夹，存你所有的故事和狗狗
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *              存所有狗狗的文件
     *    - story -- file containing the current story
     *              保存当前故事的文件
     */
    public static void setupPersistence() {
        //确保 .capers 文件夹存在，
        if (!CAPERS_FOLDER.exists()) {
            CAPERS_FOLDER.mkdirs();
        }
        // 确保 .capers/dogs 文件夹也存在
        if(!DOG_FOLDER.exists()) {
            DOG_FOLDER.mkdirs();
        }
    }
    //mkdir() 只能创建 当前这一级目录，如果父目录不存在，它就失败。
    //
    //mkdirs() 会自动创建所有需要的父目录（就像 mkdir -p 命令那样）。


    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        //将args中第一个不是命令的参数（用户输入的文本）追加（不是覆盖，因此不能用writeContent方法）到
        // .capers/story文件中
        //传进来的参数text就是要追加的内容

        //先创建一个文件，他的路径是 .capers/story
        File story = new File(CAPERS_FOLDER,"story.txt");
        //创建一个空oldStory，用你来储存读取的文件
        String oldStory = "";
        //如果这个文件存在，就读取他到oldStory
        if (story.exists()) {
            oldStory= readContentsAsString(story);
        }
        //追加text到新的文件中,最后加上换行符
        String newStory = oldStory+ text + "\n";
        //打印新的故事
        System.out.println(newStory);
        //最后需要保存文件，
        //注意这里和saveDog中调用writeObject不一样，这里只是写入string，不需要序列化
        writeContents(story,newStory);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) {
        //创建dog
        Dog d = new Dog(name, breed, age);
        //保存dog
        d.saveDog();
        System.out.println(d.toString());
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        //使用静态方法来获取对象
        Dog d = Dog.fromFile(name);
        d.haveBirthday();
        d.saveDog();
    }
}
