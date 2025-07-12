package gitlet;

import java.io.File;

import static gitlet.Repository.*;

public class HEAD {

    public static Commit getHeadCommit(){
        String commitId = getHeadCommitId();
        //用commitId去找对应的commit
        File commitFile = Utils.join(COMMITS_DIR, commitId);
        //反序列化出commit对象
        Commit currentCommit = Utils.readObject(commitFile,Commit.class);
        return currentCommit;
    }
    //将commit序列化再转为SHA1，最后保存到HEAD文件
    public static void updateHeadCommit(Commit commit) {
        Utils.writeContents(HEAD_DIR, Utils.sha1(Utils.serialize(commit)));
    }
    public static String getHeadCommitId (){
        String refsPath = Utils.readContentsAsString(HEAD_DIR).trim();
        File branchFile = Utils.join(GITLET_DIR, refsPath);
        String commitId = Utils.readContentsAsString(branchFile).trim();
        return commitId;
    }
}
