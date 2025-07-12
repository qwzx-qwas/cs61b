package gitlet;

// TODO: any imports you need here

import java.io.*;
import java.util.Date;// TODO: You'll likely use this in this class
import java.util.HashMap;

import static gitlet.Repository.COMMITS_DIR;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *一个提交将仅仅由以下部分组成：
 *
 * 一条日志信息。
 *
 * 一个时间戳。
 *
 * 一个从文件名到 blob 引用的映射（这取代了独立的“tree”对象）。
 * （使用 HashMap<String, String> 这样的结构，来记录：每个文件名对应的 blob 的哈希值
 * （也就是内容的唯一标识，即blob的名字）。
 *
 * 一个父引用（指向其直接祖先）。
 *
 * 以及，对于合并提交，一个第二个父引用。
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     * String message
     * Date commitDate
     * Commit parent
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit.一条日志信息 */
    private String message;
    /** The timestamp when this Commit was created. 一个时间戳 */
    private String commitDate;
    /** 一个父引用 */
    private String parentId;//父提交的ID，即父文件名，用SHA-1哈希表示
    /** 一个从文件名到 blob 引用的映射（这取代了独立的“tree”对象）。
     （使用 HashMap<String, String> 这样的结构，来记录：
     每个文件名对应的 blob 的哈希值（也就是内容的唯一标识，即blob的名字）。*/
    private HashMap<String,String> fileSnapshot;

    /* TODO: fill in the rest of this class. */
    /** 创建一个Commit 实例*/
    public Commit(String message,String commitDate,String parent,HashMap<String,String> file) {
        this.message = message;
        this.parentId = parent;
        this.fileSnapshot = file;
        this.commitDate = commitDate;
    }
    //使用getter进行封装
    public String getMessage() {return message;}
    public String getCommitDate() {return commitDate;}
    public String getParent() {return parentId;}
    public HashMap<String,String> getHashmap() {return fileSnapshot;}

    //获取blobId
    public String getBlobId(String fileName) {
        return this.fileSnapshot.get(fileName);
    }


}
