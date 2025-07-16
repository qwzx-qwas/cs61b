package gitlet;

import java.io.File;
import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        // TODO: what if args is empty?
        //if args is empty
        checkMain(args);
        String firstArg = args[0];
        if(firstArg !="init") {
            checkInitialized();
        }
        switch(firstArg) {
            case "init":
                /** check the length of args*/
                checkArgs(args,1);
                //调用Repository中的init的核心逻辑
                Repository.init();
                break;
            case "add":
                checkArgs(args,2);
                String fileName = args[1];
                Repository.add(fileName);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                checkArgs(args,2);
                Repository.commit(args[1]);
                break;
            case "rm":
                checkArgs(args,2);
                Repository.rm(args[1]);
                break;
            case "log":
                checkArgs(args,1);
                Repository.log();
                break;
            case "global-log" :
                checkArgs(args,1);
                Repository.globalLog();
                break;
            case "find":
                checkArgs(args,2);
                Repository.find(args[1]);
                break;
            case "status":
                checkArgs(args,1);
                Repository.status();
                break;
            case "checkout":
                //切换到另一个分支,git checkout [分支名]
                if(args.length==2){
                    Repository.checkoutBranch(args[1]);
                } else if (args.length==3 && args[1].equals("--")) {
                    //回到HEAD指向的状态,git checkout [文件名]
                    Repository.checkoutFileFromHEAD(args[2]);
                } else if (args.length == 4 && args[2].equals("--")) {
                    //回到文件某个状态，git checkout [commit id] -- [文件名]
                    Repository.checkoutFromCommit(args[1],args[3]);
                } else {
                    System.out.println("Incorrect operand.");
                    System.exit(0);
                }
                break;
            case "branch":
                checkArgs(args,2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                checkArgs(args,2);
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                checkArgs(args,2);
                Repository.reset(args[1]);
                break;
            case "merge":
                checkArgs(args,2);
                Repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }

    }
    //检查输入是否为空
    private static void checkMain(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
    }

    //检查输入的字数是否有问题
    private static void checkArgs(String[] args,int num) {
        if (args.length != num) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
    private static final File GITLET_DIR = new File(".gitlet");

    public static void checkInitialized() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}
