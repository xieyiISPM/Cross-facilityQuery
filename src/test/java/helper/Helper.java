package helper;

import java.math.BigInteger;

public class Helper {
    public static void printList(BigInteger[] list){
        for (BigInteger item: list){
            System.out.println(item + " ");
        }
        System.out.println();
    }
}
