package gitlet;

import java.io.File;

import static gitlet.Repository.*;

public class HEAD {

    public static Commit getHeadCommit(){
        //去HEAD文件中找到其储存的branch的名字
        String branchPath = Utils.readContentsAsString(HEAD_DIR);
        //用branch的名字去找commitId
        File branchFile = Utils.join(GITLET_DIR, branchPath);
        String commitId = Utils.readContentsAsString(branchFile);
        //用commitId去找对应的commit
        File commitFile = Utils.join(COMMITS_DIR, commitId);
        //反序列化出commit对象
        Commit currentCommit = Utils.readObject(commitFile,Commit.class);
        return currentCommit;
    }
}
