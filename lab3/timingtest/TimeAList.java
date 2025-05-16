package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
/* Ns is the first column, times is the second column, and opCounts is the third column.*/
public class TimeAList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        /*using AList instances to restore every for loop's data,like Ns times op and print them  */
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();

        for(int N = 1000;N <= 128000;N *= 2) {
            /*begin to record time*/
            Stopwatch timer = new Stopwatch();
            AList list = new AList();
            for (int i = 0; i < N; i++) {
                list.addLast(i);
            }
            /*return time*/
            double timeInSeconds = timer.elapsedTime();
            /*record data   put N int0 Ns ...*/
            Ns.addLast(N);
            times.addLast(timeInSeconds);
            opCounts.addLast(N);

        }
        printTimingTable(Ns,times,opCounts);
        // TODO: YOUR CODE HERE
    }
}
