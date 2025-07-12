package gitlet;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
     *        ——Stage              这里是暂存区！
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

    public static final File STAGE_DIR = join(GITLET_DIR, "stage");
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
        if(!STAGE_DIR.mkdirs()) {
            System.out.println("Error creating stage directory.");
            System.exit(0);
        }
        String initDate = " 00:00:00 UTC, Thursday, 1 January 1970";
        HashMap<String,String> initialSnapshot = new HashMap<>();
        Commit initial= new Commit("initial commit",initDate,null,initialSnapshot);

        //获取初始提交的哈希值,并保存初始提交为文件
        String initialCommitId = Utils.sha1(initial);
        File commitFile = new File(COMMITS_DIR, initialCommitId);

        FileOutputStream fos = new FileOutputStream(commitFile);
         //缓冲输出流
         BufferedOutputStream bos = new BufferedOutputStream(fos);
         //把Java对象序列化
         ObjectOutputStream oos = new ObjectOutputStream(bos);
         //oos写入至文档initialSnapshot
         oos.writeObject(initialSnapshot);
         oos.close();//打开文件准备写字

        //创建HEAD文件,设置 HEAD 内容为 "refs/heads/master"
        Utils.writeContents(HEAD_DIR, "refs/heads/master");
        /** 创建master分支，指向initialCommit*/
        Utils.writeContents(new File(HEADS_DIR, "master"),initialCommitId);
    }

    public static void add(String fileName) {
        File addedFile = new File(fileName);
        //1. 验证文件是否存在
        if(!addedFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        // 2. 获取当前提交（HEAD所指向的commit）；
        Commit currentCommit = HEAD.getHeadCommit();
        //    3. 比较这个文件当前内容与当前提交中的版本：即比较二者的sha1
        byte[] fileContent = Utils.readContents(addedFile);
        String blobId = Utils.sha1(fileContent);

        String blobIdInCommit = currentCommit.getBlobId(fileName);
        //        - 如果一样：从暂存区中移除；
        if (blobId.equals(blobIdInCommit)) {
            Stage.removeFromStagingAera(fileName);
            return;
        }
        //  - 如果不一样：复制一份放入暂存区；
        File blobFile = new File(BLOBS_DIR, blobId);
        if(!blobFile.exists()) {
            Utils.writeContents(blobFile, fileContent);
        }
        //更新缓存区   如果之前是 rm 过的文件，现在就取消标记；
        Stage.stageForAdd(fileName,blobId);
    }

    public static void commit(String message) {
        //检查message是否为空
        if(message == null || message.equals( "")) {
            System.out.println("Commit message is null.");
            System.exit(0);
        }
        //检查缓存区是否为空
        Stage stage = Utils.readObject(STAGE_DIR,Stage.class);
        if(stage.getAddedFiles().isEmpty() && stage.getRemovedFiles().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        //获取当前HEAD指向的commit作为父commit
        Commit parentCommit= HEAD.getHeadCommit();
        String parentId = HEAD.getHeadCommitId();
        //获取旧的快照
        HashMap<String,String> newSnapshot = new HashMap<>(parentCommit.getFileSnapshot());
        //更新快照
        for(String fileName:stage.getAddedFiles().keySet()) {
            String blobId = stage.getAddedFiles().get(fileName);
            newSnapshot.put(fileName,blobId);
        }
        for(String fileName:stage.getRemovedFiles()) {
            newSnapshot.remove(fileName);
        }
        //获取当前时间
        String currentDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z").format(new Date());
        //构造新的commit并保存到文件中
        Commit currentCommit = new Commit(message,currentDate,parentId,newSnapshot);
        Utils.writeContents(COMMITS_DIR,currentCommit);
        //更新head以及清空stage
        HEAD.updateHeadCommit(currentCommit);
        Stage.clearStagingAera();
    }

    public static void rm(String fileName) {
        Commit currentCommit = HEAD.getHeadCommit();
        Stage stage = Utils.readObject(STAGE_DIR,Stage.class);
        Stage.stageForRemove(fileName);
        if(currentCommit.getFileSnapshot().containsKey(fileName)) {
            stage.getRemovedFiles().add(fileName);
            //看工作路径上是否存在该文件，有就删去
            File fileINCWD = new File(fileName);
            if(fileINCWD.exists()) {
                fileINCWD.delete();
            }
        }
    }

    public static void log (){
        //先获取HEAD指向的commit,再顺着parentId往上走，直到null
        String commitId = HEAD.getHeadCommitId();
        while(commitId != null) {
            Commit commit = Commit.readCommit(commitId);
            Commit.printCommit(commit,commitId);
            commitId = commit.getParent();
        }
    }

    public static void globalLog() {
        List<String> fileName = Utils.plainFilenamesIn(COMMITS_DIR);
        for(String commitId:fileName) {
            Commit commit = Commit.readCommit(commitId);
            Commit.printCommit(commit,commitId);
        }
    }

    public static void find(String message) {
        List<String> fileName = Utils.plainFilenamesIn(COMMITS_DIR);
        boolean found = false;
        for(String commitId:fileName) {
            Commit commit = Commit.readCommit(commitId);
            if(commit.getMessage().equals(message)) {
                System.out.println(commitId);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    
}
