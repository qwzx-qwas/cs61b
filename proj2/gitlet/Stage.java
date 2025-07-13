package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import static gitlet.Repository.STAGE_DIR;

public class Stage implements Serializable {
    //文件名 -> blobId的映射（暂存待添加的文件），文件名是 key，blobId 是 value
    private HashMap<String,String> addedFiles = new HashMap<>();
    //暂存待删除的文件的文件名的集合
    private HashSet<String> removedFiles = new HashSet<>();

    public HashMap<String,String> getAddedFiles() {
        return addedFiles;
    }
    public HashSet<String> getRemovedFiles() {
        return removedFiles;
    }
    //添加到缓存区
    public static void stageForAdd(String fileName , String blobId) {
       //先从硬盘里读取存储的Stage对象
        Stage stage = Utils.readObject(STAGE_DIR,Stage.class);
        //将文件名和blobId保存到addedFiles中
        stage.addedFiles.put(fileName,blobId);
        //如果之前是准备删除的，现在取消删除
        stage.removedFiles.remove(fileName);
        //将Stage写入硬盘
        Utils.writeObject(STAGE_DIR,stage);
    }
    //标记要删除的文件
    public static void stageForRemove(String fileName) {
        Stage stage = Utils.readObject(STAGE_DIR,Stage.class);
        //如果之前准备添加的，现在取消添加(即从addedFiles中移除）
        if (stage.addedFiles.containsKey(fileName)) {
            stage.addedFiles.remove(fileName);
        } else {
            stage.removedFiles.add(fileName);
        }
        //保存stage
        Utils.writeObject(STAGE_DIR,stage);
    }

    //移除某个文件的暂存记录
    public static void removeFromStagingAera(String filename) {
        Stage stage = Utils.readObject(STAGE_DIR,Stage.class);
        stage.removedFiles.add(filename);
        stage.addedFiles.remove(filename);
        Utils.writeObject(STAGE_DIR,stage);
    }
    //保存缓存区
    public static void clearStagingAera() {
        Utils.writeObject(STAGE_DIR,new Stage());
    }

}
