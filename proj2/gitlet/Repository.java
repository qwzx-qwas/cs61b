package gitlet;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.HEAD.getHeadCommitId;
import static gitlet.Stage.STAGE_FILE;
import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 * <p>
 * does at a high level.
 *
 * @author
 */
public class Repository {
    /**
     *
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
    /**
     * The current working directory. 工作目录
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory. 创建一个隐藏的 .gitlet/ 文件夹
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");
    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");

    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    public static final File HEADS_DIR = join(REFS_DIR, "heads");

    public static final File HEAD_DIR = join(HEADS_DIR, "HEAD");

    public static final File STAGE_DIR = join(GITLET_DIR, "stage");

    public static void checkGitletDir() {
        if (GITLET_DIR.exists()) {
            //如果当前目录中已经存在一个 Gitlet 版本控制系统，程序应该终止并不覆盖现有系统。此时应打印错误消息
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            //并终止程序进行
            System.exit(0);
        }
    }

    public static void init() {
        /** 创建一个隐藏的 .gitlet/ 文件夹（检查该文件夹是否存在) */
        checkGitletDir();
        GITLET_DIR.mkdirs();
        /**创构建基本目录框架*/
        //优先创建子文件（mkdirs会自动补全父路径）
        if (!COMMITS_DIR.mkdirs()) {
            System.out.println("Error creating objects/commits directory.");
            System.exit(0);
        }
        if (!BLOBS_DIR.mkdirs()) {
            System.out.println("Error creating objects/blobs directory.");
            System.exit(0);
        }
        if (!HEADS_DIR.mkdirs()) {
            System.out.println("Error creating refs/heads directory.");
            System.exit(0);
        }
        if (!STAGE_DIR.mkdirs()) {
            System.out.println("Error creating stage directory.");
            System.exit(0);
        }

        Date epoch = new Date(0); // Unix Epoch
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        String initDate = dateFormat.format(epoch);
        //String initDate = " 00:00:00 UTC, Thursday, 1 January 1970";
        HashMap<String, String> initialSnapshot = new HashMap<>();
        Commit initial = new Commit("initial commit", initDate, initialSnapshot, null);

        //获取初始提交的哈希值,并保存初始提交为文件
        String initialCommitId = Utils.sha1(Utils.serialize(initial));
        File commitFile = new File(COMMITS_DIR, initialCommitId);

        Utils.writeObject(commitFile, initial);
        //创建HEAD文件,设置 HEAD 内容为 "refs/heads/master"
        Utils.writeContents(HEAD_DIR, "refs/heads/master");
        /** 创建master分支，指向initialCommit*/
        Utils.writeContents(new File(HEADS_DIR, "master"), initialCommitId);

        Stage.initStage();
    }

    public static void add(String fileName) {
        File addedFile = new File(fileName);
        //1. 验证文件是否存在
        if (!addedFile.exists()) {
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
        if (!blobFile.exists()) {
            Utils.writeContents(blobFile, fileContent);
        }
        //更新缓存区   如果之前是 rm 过的文件，现在就取消标记；
        Stage.stageForAdd(fileName, blobId);
    }

    public static void commit(String message) {
        //检查message是否为空
        if (message == null || message.equals("")) {
            System.out.println("Commit message is null.");
            System.exit(0);
        }
        //检查缓存区是否为空
        Stage stage = Utils.readObject(STAGE_FILE, Stage.class);
        if (stage.getAddedFiles().isEmpty() && stage.getRemovedFiles().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        //获取当前HEAD指向的commit作为父commit
        Commit parentCommit = HEAD.getHeadCommit();
        List<String> parents = List.of(HEAD.getHeadCommitId());
        //获取旧的快照
        HashMap<String, String> newSnapshot = new HashMap<>(parentCommit.getFileSnapshot());
        //更新快照
        for (String fileName : stage.getAddedFiles().keySet()) {
            String blobId = stage.getAddedFiles().get(fileName);
            newSnapshot.put(fileName, blobId);
        }
        for (String fileName : stage.getRemovedFiles()) {
            newSnapshot.remove(fileName);
        }
        //获取当前时间
        Date epoch = new Date(); // Unix Epoch
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        String currentDate = dateFormat.format(epoch);
        //构造新的commit并保存到文件中
        Commit currentCommit = new Commit(message, currentDate, newSnapshot, parents);
        String currentCommitId = Utils.sha1(Utils.serialize(currentCommit));
        File commitFile = new File(COMMITS_DIR, currentCommitId);
        Utils.writeObject(commitFile, currentCommit);
        //更新head以及清空stage
        HEAD.updateHeadCommit(currentCommitId);
        Stage.clearStagingAera();
    }

    public static void rm(String fileName) {
        Commit currentCommit = HEAD.getHeadCommit();
        Stage stage = Utils.readObject(STAGE_FILE, Stage.class);
        Stage.stageForRemove(fileName);
        if (currentCommit.getFileSnapshot().containsKey(fileName)) {
            stage.getRemovedFiles().add(fileName);
            //看工作路径上是否存在该文件，有就删去
            File fileINCWD = new File(fileName);
            if (fileINCWD.exists()) {
                fileINCWD.delete();
            }
        }
    }

    public static void log() {
        //先获取HEAD指向的commit,再顺着parentId往上走，直到null
        String commitId = getHeadCommitId();
        while (commitId != null) {
            Commit commit = Commit.readCommit(commitId);
            Commit.printCommit(commit, commitId);
            commitId = commit.getParent();
        }
    }

    public static void globalLog() {
        List<String> fileName = Utils.plainFilenamesIn(COMMITS_DIR);
        for (String commitId : fileName) {
            Commit commit = Commit.readCommit(commitId);
            Commit.printCommit(commit, commitId);
        }
    }

    public static void find(String message) {
        List<String> fileName = Utils.plainFilenamesIn(COMMITS_DIR);
        boolean found = false;
        for (String commitId : fileName) {
            Commit commit = Commit.readCommit(commitId);
            if (commit.getMessage().equals(message)) {
                System.out.println(commitId);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        //列出所有分支
        List<String> branchName = Utils.plainFilenamesIn(HEADS_DIR);
        Collections.sort(branchName);
        System.out.println("=== Branches ===");
        for (String branch : branchName) {
            //当前活跃的分支前加*
            String currentBranch = HEAD.getCurrentBranchName();
            if (branch.equals(currentBranch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }

        //列出所有已暂存以备添加的文件
        Stage stage = Utils.readObject(STAGE_FILE, Stage.class);
        List<String> addedFiles = new ArrayList<>(stage.getAddedFiles().keySet());
        System.out.println();

        System.out.println("=== Staged Files ===");
        Collections.sort(addedFiles);
        for (String addedFile : addedFiles) {
            System.out.println(addedFile);
        }
        //列出所有已暂存以备移除的文件
        System.out.println();

        System.out.println("=== Removed Files ===");
        List<String> removedFilesList = new ArrayList<>(stage.getRemovedFiles());
        for (String removedFile : removedFilesList) {
            System.out.println(removedFile);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        //1.已 add 到缓存区，然后又修改，但没重新 add
        //找到当前工作目录下与Stage中存在的文件名的文件名
        for (String file : stage.getAddedFiles().keySet()) {
            File fileINCWD = new File(CWD, file);
            //如果工作目录下的文件已经被删除，而缓存区中仍有 or 如果工作目录下的文件已经被删除,而原先commit仍有
            if (!fileINCWD.exists()) {
                if (stage.getAddedFiles().containsKey(file) || HEAD.getHeadCommit().getBlobId(file) != null) {
                    System.out.println(file + "(deleted)");
                }
            } else {
                String currentId = Utils.sha1(readContents(fileINCWD));
                String IdInStage = stage.getAddedFiles().get(file);
                if (!currentId.equals(IdInStage)) {
                    System.out.println(file + "(modified)");
                }
            }
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        List<String> result = new ArrayList<>();
        //获取CWD下所有文件名字
        List<String> CWDFileNameList = Utils.plainFilenamesIn(CWD);
        Collections.sort(CWDFileNameList);
        Commit head = HEAD.getHeadCommit();
        Map<String, String> tracked = head.getFileSnapshot();
        for (String CWDFileName : CWDFileNameList) {
            File file = new File(CWD, CWDFileName);
            if (!file.exists()) {
                continue;
            }
            //既未暂存以备添加也未被跟踪的文件
            boolean untracked = !tracked.containsKey(CWDFileName) && !stage.getAddedFiles().containsKey(CWDFileName);
            //还有已被暂存以备移除，但又被创建
            if (untracked || stage.getRemovedFiles().contains(CWDFileName)) {
                result.add(CWDFileName);
            }
        }
        for (String file : result) {
            System.out.println(file);
        }
        System.out.println();
    }

    public static void checkoutBranch(String branchName) {
        //将指定文件从指定分支中覆盖到CWD，把HEAD指向当前分支，清空缓存区
        File distBranch = Utils.join(HEADS_DIR, branchName);
        //是否存在该branch
        if (!distBranch.exists()) {
            System.out.println(" No such branch exists.");
            System.exit(0);
        }
        //检查是否为当前分支
        String currentBranch = HEAD.getCurrentBranchName();
        if (currentBranch.equals(branchName)) {
            System.out.println("No need to checkout the current branch");
            System.exit(0);
        }
        String distCommitId = Utils.readContentsAsString(distBranch).trim();
        Commit distCommit = Commit.readCommit(distCommitId);
        //检查加覆盖CWD中的文件加清除缓存
        Repository.checkoutCommit(distCommit);
        //更新HEAD
        HEAD.updateHeadCommit(distCommitId);
    }

    public static void checkoutCommit(Commit distCommit) {
        //检查是否工作目录中的文件在当前分支中是未跟踪的，并且会被检出操作覆盖
        //目标commit
        HashMap<String, String> distSnapshot = distCommit.getFileSnapshot();
        //当前commit
        String currentCommitId = getHeadCommitId();
        Commit currentCommit = Commit.readCommit(currentCommitId);
        HashMap<String, String> currentSnapshot = currentCommit.getFileSnapshot();
        //检查是否有untracked文件被覆盖
        //列出当前CWD下的所有文件名
        List<String> cwdFileNameList = Utils.plainFilenamesIn(CWD);
        for (String cwdFileName : cwdFileNameList) {
            //当前没有，而目标分支有
            if (distSnapshot.containsKey(cwdFileName) && !currentSnapshot.containsKey(cwdFileName)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        //删除当前commit中有但对应分支没有的
        for (String fileName : currentSnapshot.keySet()) {
            if (!distSnapshot.containsKey(fileName)) {
                File file = Utils.join(CWD, fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        //遍历目标branch的所有文件，把它放到CWD中
        for (Map.Entry<String, String> entry : distSnapshot.entrySet()) {
            String distFileName = entry.getKey();
            String blobId = entry.getValue();
            File blobFile = Utils.join(BLOBS_DIR, blobId);
            byte[] blobContents = Utils.readContents(blobFile);
            File newFile = Utils.join(CWD, distFileName);
            Utils.writeContents(newFile, blobContents);
        }
        //清空缓存区
        Stage.clearStagingAera();
    }

    public static void checkoutFileFromHEAD(String fileName) {
        //将指定文件从HEAD指向的版本的commit文件中取出，加入到工作目录中
        String commitId = getHeadCommitId();
        checkoutFromCommit(commitId, fileName);
    }

    public static void checkoutFromCommit(String commitId, String fileName) {
        String shortCommitId = Commit.resolveFullCommitId(commitId);
        Commit distCommit = Commit.readCommit(shortCommitId);
        if (distCommit == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        HashMap<String, String> distMap = distCommit.getFileSnapshot();
        //如果当前文件不存在于当前的commit中
        if (!distMap.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        //从commit中取出blobId（用对应的文件名）,用blobId去找blob文件，再写入
        //如果工作目录已存在该文件，就覆盖他（writeContents方法已经包含）
        String blobId = distMap.get(fileName);
        File blobFile = Utils.join(BLOBS_DIR, blobId);
        byte[] blobContents = Utils.readContents(blobFile);
        File newFile = Utils.join(CWD, fileName);
        Utils.writeContents(newFile, blobContents);
    }

    public static void branch(String branchName) {
        File newBranch = Utils.join(HEADS_DIR, branchName);
        if (newBranch.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        String headCommitId = getHeadCommitId();
        Utils.writeContents(newBranch, headCommitId);
    }

    public static void rmBranch(String branchName) {
        File branchFile = Utils.join(HEADS_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        String currentBranchName = HEAD.getCurrentBranchName();
        if (branchName.equals(currentBranchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branchFile.delete();
    }

    //跟checkout差不多，不过输入是commitId
    public static void reset(String commitId) {
        Commit commit = Commit.readCommit(commitId);
        checkoutCommit(commit);
        HEAD.updateHeadCommit(commitId);
    }

    public static void merge(String branchName) {
        boolean hasConflicts = false;
        HashMap<String, String> mergedSnapshot = new HashMap<>();
        Stage stage = Utils.readObject(STAGE_FILE, Stage.class);
        // 如果存在任何暂存的添加或删除
        if (!stage.getAddedFiles().isEmpty() || !stage.getRemovedFiles().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        //如果指定的分支名称不存在，
        File branchFile = Utils.join(HEADS_DIR, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        //尝试将分支与其自身合并会显示错误消息“Cannot merge a branch with itself.”（无法将分支与自身合并）。
        if (branchName.equals(HEAD.getCurrentBranchName())) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        //检查是否工作目录中的文件在当前分支中是未跟踪的，并且会被检出操作覆盖
        //目标commit
        String distCommitId = Utils.readContentsAsString(branchFile).trim();
        Commit distCommit = Commit.readCommit(distCommitId);
        HashMap<String, String> distSnapshot = distCommit.getFileSnapshot();
        //当前commit
        String currentCommitId = getHeadCommitId();
        Commit currentCommit = Commit.readCommit(currentCommitId);
        HashMap<String, String> currentSnapshot = currentCommit.getFileSnapshot();
        //检查是否有untracked文件被覆盖
        //列出当前CWD下的所有文件名
        List<String> cwdFileNameList = Utils.plainFilenamesIn(CWD);
        for (String cwdFileName : cwdFileNameList) {
            //当前没有，而目标分支有
            if (distSnapshot.containsKey(cwdFileName) && !currentSnapshot.containsKey(cwdFileName)) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        String split = getSplitPoint(currentCommitId, distCommitId);
        //如果splitPoint（最新共同祖先）与给定分支的头部是同一个提交
        if (split.equals(distCommitId)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        //如果splitPoint与当前分支的头部是同一个提交，Gitlet 会执行“快进”合并
        if (split.equals(currentCommitId)) {
            System.out.println("Current branch fast-forwarded.");
            HEAD.updateHeadCommit(distCommitId);
        }
        Commit splitedCommit = Commit.readCommit(split);
        Map<String, String> splitSnapshot = splitedCommit.getFileSnapshot();
        //把三代文件放到一起
        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(currentSnapshot.keySet());
        allFiles.addAll(distSnapshot.keySet());
        allFiles.addAll(splitSnapshot.keySet());
        //遍历所有文件，看看同样的文件，三代是否相同
        for (String file : allFiles) {
            String currentHash = currentSnapshot.get(file);
            String distHash = distSnapshot.get(file);
            String splitHash = splitSnapshot.get(file);
            //判断是否存在该文件
            boolean inCurr = (currentHash != null);
            boolean inDist = (distHash != null);
            boolean inSplit = (splitHash != null);
            if (splitHash.equals(distHash) && splitHash.equals(currentHash)) {
                mergedSnapshot.put(file, currentHash);
                continue;
            }
            //只在“给定分支”里改动了文件,split和current一样，和dist不一样
            if (splitHash.equals(currentHash) && !splitHash.equals(distHash)) {
                checkoutFromCommit(distCommitId, file);
                Stage.stageForAdd(file, distHash);
                mergedSnapshot.put(file, distHash);
                continue;
            }
            //仅在curr分支中修改
            if (splitHash.equals(distHash) && !splitHash.equals(currentHash)) {
                mergedSnapshot.put(file, currentHash);
                continue;
            }
            //curr和dist都修改或删除
            if (currentHash.equals(distHash)) {
                continue;
            }
            //只在当前分支中新增
            if (inCurr && !inSplit && !inDist) {
                mergedSnapshot.put(file, currentHash);
                continue;
            }
            // 仅给定分支新增
            if (inDist && !inSplit && !inCurr) {
                checkoutFromCommit(distCommitId, file);
                Stage.stageForAdd(file, splitHash);
                mergedSnapshot.put(file, distHash);
                continue;
            }
            //当前未修改，给定删除（要删除）
            if (inSplit && splitHash.equals(currentHash) && !inDist) {
                //删除 + 取消跟踪（即移除暂存记录）
                File newFile = Utils.join(CWD, file);
                newFile.delete();
                continue;
            }
            //给定未修改，当前删除
            if (distHash.equals(splitHash) && !inCurr) {
                continue;
            }
            //其余为冲突
            hasConflicts = true;
            String currentContent;
            String distContent;
            if (inCurr) {
                File currentFile = Utils.join(BLOBS_DIR, currentHash);
                currentContent = Utils.readContentsAsString(currentFile);
            } else {
                currentContent = "";
            }
            if (inDist) {
                File distFile = Utils.join(BLOBS_DIR, distHash);
                distContent = Utils.readContentsAsString(distFile);
            } else {
                distContent = "";
            }
            String conflictContent = "<<<<<<< HEAD\n" + currentContent +
                    "=======\n" + distContent +
                    ">>>>>>>\n";
            //把合并后的冲突内容写进工作目录的对应文件。
            //相当于创建了一个“带冲突提示”的版本，让用户知道该手动解决
            File conflictFile = Utils.join(CWD, file);
            Utils.writeContents(conflictFile, conflictContent);
            //加入到暂存区
            String blobId = Utils.sha1(conflictContent);
            Stage.stageForAdd(file, blobId);
            mergedSnapshot.put(file, blobId);
        }
        //合并提交
        String mergeMessage = "Merged " + branchName + " into " + HEAD.getCurrentBranchName() + ".";
        List<String> parents = List.of(currentCommitId, distCommitId);
        Date epoch = new Date(); // Unix Epoch
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        String currentDate = dateFormat.format(epoch);
        Commit mergeCommit = new Commit(mergeMessage, currentDate, mergedSnapshot, parents);
        String mergeCommitId = Utils.sha1(Utils.serialize(mergeCommit));
        File commitFile = Utils.join(COMMITS_DIR, mergeCommitId);
        Utils.writeContents(commitFile, mergeCommitId);
        HEAD.updateHeadCommit(mergeCommitId);
        if (hasConflicts) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    public static String getSplitPoint(String CommitId1, String CommitId2) {
        //用集合记录commitId1家谱中所有祖先的ID
        Set<String> ancestor1 = new HashSet<>();
        //BFS实现遍历，记录全部祖先
        Queue<String> queue1 = new LinkedList<>();
        queue1.add(CommitId1);
        while (!queue1.isEmpty()) {
            //获取第一个，并删除
            String id = queue1.poll();
            Commit c = Commit.readCommit(id);
            List<String> parents = c.getParents();
            if (parents != null) {
                queue1.addAll(parents);
            }
        }
        //找相同祖先
        Queue<String> queue2 = new LinkedList<>();
        queue2.add(CommitId2);
        while (!queue2.isEmpty()) {
            String id = queue2.poll();
            if (ancestor1.contains(id)) {
                return id;
            }
            Commit c = Commit.readCommit(id);
            List<String> parents = c.getParents();
            if (parents != null) {
                queue2.addAll(parents);
            }
        }
        return null;
    }
}

