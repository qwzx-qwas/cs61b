package gitlet;

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
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                /** check the length of args*/
                checkArgs(args,1);
                //调用Repository中的init的核心逻辑
                Repository.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
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
            default:


        }

    }
    //检查输入是否为空
    private static void checkMain(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command");
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
}
