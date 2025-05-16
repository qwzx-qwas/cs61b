package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing list1 = new AListNoResizing();
        BuggyAList list2 = new BuggyAList();
        for(int i = 4; i < 7; i++){
            list1.addLast(i);
            list2.addLast(i);
        }
        for (int i = 0; i < list1.size() && i < list2.size(); i++){
            assertEquals(list1.removeLast(), list2.removeLast());
        }
    }


    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> list = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0,3 );
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                list.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int BuggySize = list.size();
                assertEquals(size, BuggySize);
            }else if (operationNumber == 2) {
                if (L.size() == 0) {
                    continue;
                }else if(list.size() == 0){
                    continue;
                }
                else{
                    L.getLast();
                    list.getLast();
                    assertEquals(L.removeLast(), list.removeLast());
                }
            }
        }
    }
    }
