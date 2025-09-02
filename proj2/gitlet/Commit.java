package gitlet;


import java.io.*;
import java.util.HashMap;
import java.util.List;

import static gitlet.Repository.COMMITS_DIR;

/**
 * Represents a gitlet commit object.
 * <p>
 * does at a high level.
 * 一个提交将仅仅由以下部分组成：
 * <p>
 * 一条日志信息。
 * <p>
 * 一个时间戳。
 * <p>
 * 一个从文件名到 blob 引用的映射（这取代了独立的“tree”对象）。
 * （使用 HashMap<String, String> 这样的结构，来记录：每个文件名对应的 blob 的哈希值
 * （也就是内容的唯一标识，即blob的名字）。
 * <p>
 * 一个父引用（指向其直接祖先）。
 * <p>
 * 以及，对于合并提交，一个第二个父引用。
 *
 * @author
 */
public class Commit implements Serializable {
    /**
     * String message
     * Date commitDate
     * Commit parent
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.一条日志信息
     */
    private String message;
    /**
     * The timestamp when this Commit was created. 一个时间戳
     */
    private String commitDate;
    /**
     * 一个从文件名到 blob 引用的映射（这取代了独立的“tree”对象）。
     * （使用 HashMap<String, String> 这样的结构，来记录：
     * 每个文件名对应的 blob 的哈希值（也就是内容的唯一标识，即blob的名字）。
     */
    private HashMap<String, String> fileSnapshot;
    //存放直接父级的ID
    private List<String> parents;

    /**
     * 创建一个Commit 实例
     */

    public Commit(String message, String commitDate,
                  HashMap<String, String> file, List<String> parents) {
        this.message = message;
        this.fileSnapshot = file;
        this.commitDate = commitDate;
        this.parents = parents;
    }

    //使用getter进行封装
    public String getMessage() {
        return message;
    }

    public String getCommitDate() {
        return commitDate;
    }

    public String getParent() {
        return parents.isEmpty() ? null : parents.get(0);
    }

    public HashMap<String, String> getFileSnapshot() {
        return fileSnapshot;
    }

    public List<String> getParents() {
        return parents;
    }

    //获取blobId
    public String getBlobId(String fileName) {
        return this.fileSnapshot.get(fileName);
    }

    //通过commitId返回commit
    public static Commit readCommit(String commitId) {
        File file = Utils.join(COMMITS_DIR, commitId);
        return Utils.readObject(file, Commit.class);
    }

    public static void printCommit(Commit commit, String commitId) {
        System.out.println();
        System.out.println("===");
        System.out.println("commitId:" + commitId);
        System.out.println("Date:" + commit.getCommitDate());
        System.out.println(commit.getMessage());
        System.out.println();
        System.out.println();
    }

    //通过一截commitId来找到commit
    public static String resolveFullCommitId(String shortId) {
        for (String fileName : Utils.plainFilenamesIn(COMMITS_DIR)) {
            if (fileName.startsWith(shortId)) {
                return fileName;
            }
        }
        return null;
    }
}
