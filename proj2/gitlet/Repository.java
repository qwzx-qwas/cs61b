package gitlet;

import java.io.*;
import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *Repository 类是 Gitlet 系统的核心业务逻辑层。它不直接存储数据，
     * 而是充当一个服务层，协调和管理其他数据实体（如 Commit 对象、Blob 对象、分支引用）的创建、读取、更新和删除，
     * 从而实现整个版本控制系统的功能。
     *
     * 所以，当你在 Main 类中解析到用户输入的命令（例如 init、add、commit 等）时，
     * 你就会调用 Repository 类中对应的方法来执行实际的操作。
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */
    /**基本框架
     * .gitlet/
     *        ——objects/            数据仓库：实际保存所有快照和提交的地方
     *           ——commits          每一个提交对象（记录一次快照的元信息）
     *           ——blobs            每个文件的内容快照（文件的hashmap ID）
     *
     *        ——refs/               所有分支的“标签系统”（就是一堆分支指针）
     *           ——heads/           本地分支目录，每个文件表示一个分支
     *              ——master         master 分支当前指向的 commit ID
     *              ——...(branches)
     *
     *        ——HEAD                当前你正在工作的分支（通常指向 refs/heads/某个分支）
     *   。
     *        ——Stages/             暂存区：保存 add 和 remove 操作的记录
     *           ——addStage         暂存区：记录准备加入快照的新文件/修改文件
     *           ——removeStage      删除区：记录准备从快照中移除的文件
     *
     *
     * */
    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. 创建一个隐藏的 .gitlet/ 文件夹*/
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");
    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");

    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");

    public static final File HEAD_DIR = join(HEADS_DIR, "HEAD");

    public static final File STAGES_DIR = join(GITLET_DIR, "stages");
    public static final File ADDSTAGES_DIR = join(STAGES_DIR, "addstages");
    public static final File REMOVESTAGES_DIR = join(STAGES_DIR, "removestages");
    /* TODO: fill in the rest of this class. */

    public static void checkGitletDir() {
        if(GITLET_DIR.exists()) {
            //如果当前目录中已经存在一个 Gitlet 版本控制系统，程序应该终止并不覆盖现有系统。此时应打印错误消息
            System.out.println("Gitlet version-control system already exists in the current directory.");
            //并终止程序进行
            System.exit(0);
        }
    }


    public static void init() throws IOException {
        /** 创建一个隐藏的 .gitlet/ 文件夹（检查该文件夹是否存在) */
        checkGitletDir();
        /**创构建基本目录框架*/
        //优先创建子文件（mkdirs会自动补全父路径）
        if(!COMMITS_DIR.mkdirs()) {
            System.out.println("Error creating objects/commits directory.");
            System.exit(0);
        }
        if(!BLOBS_DIR.mkdirs()) {
            System.out.println("Error creating objects/blobs directory.");
            System.exit(0);
        }
        if(!HEADS_DIR.mkdirs()) {
            System.out.println("Error creating refs/heads directory.");
            System.exit(0);
        }
        if(!ADDSTAGES_DIR.mkdirs()) {
            System.out.println("Error creating stages/addstages directory.");
            System.exit(0);
        }
        if(!REMOVESTAGES_DIR.mkdirs()) {
            System.out.println("Error creating stages/removestages directory.");
            System.exit(0);
        }
        /**创建初始提交
         *
         *
         *
         *
         * */
        Date initDate = new Date(0);
        HashMap<String,String> initialSnapshot = new HashMap<>();
        Commit initial= new Commit("initial commit",initDate,null,initialSnapshot);
        //获取初始提交的哈希值,并保存初始提交为文件
        String initialCommitId = initial.getSHA1();
        File commitFile = new File(COMMITS_DIR, initialCommitId);
        FileOutputStream fos = new FileOutputStream(commitFile);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(initialSnapshot);
        oos.close();
        //创建HEAD文件,设置 HEAD 内容为 "refs/heads/master"
        Utils.writeContents(HEAD_DIR, "refs/heads/master");
        /** 创建master分支，指向initialCommit*/
        Utils.writeContents(new File(HEADS_DIR, "master"),initialCommitId);

    }
}
