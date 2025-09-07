package gitlet;

import java.io.File;

import static gitlet.Repository.*;

public class HEAD {

    public static Commit getHeadCommit() {
        String commitId = getHeadCommitId();
        //用commitId去找对应的commit
        File commitFile = Utils.join(COMMITS_DIR, commitId);
        //反序列化出commit对象
        Commit currentCommit = Utils.readObject(commitFile, Commit.class);
        return currentCommit;
    }

    //将commit序列化再转为SHA1，最后保存到HEAD文件
    public static void updateHeadCommit(String commitId) {
        //获取当前HEAD指向的分支的路径
        String refsPath = Utils.readContentsAsString(HEAD_DIR).trim();
        File branchFile = Utils.join(GITLET_DIR, refsPath);
        //把新的commit的Id写入该路径
        Utils.writeContents(branchFile, commitId);
    }

    public static String getHeadCommitId() {
        String refsPath = Utils.readContentsAsString(HEAD_DIR).trim();
        File branchFile = Utils.join(GITLET_DIR, refsPath);
        String commitId = Utils.readContentsAsString(branchFile).trim();
        return commitId;
    }

    public static String getCurrentBranchName() {
        String refsPath = Utils.readContentsAsString(HEAD_DIR).trim();
        if (refsPath.startsWith("refs/heads/")) {
            return refsPath.substring("refs/heads/".length());
        } else {
            // detached HEAD 状态，返回 null 或特殊标记
            return null;
        }
    }
}
