/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smbhound;

import java.util.ArrayList;

/**
 *
 * @author akiel
 */
public class CartesianProduct {

    private static ArrayList<String> res1;
    private static ArrayList<String> res2;
    private static ArrayList<String> res3;
    private static ArrayList<String> res4;
    private static ArrayList<String> res5;

    private static int[][] genArr(int index, int len) {
        int[][] res = new int[index][len];
        for (int i = 0; i < index; i++) {
            int c = 0;
            for (int j = 0; j < len; j++) {
                res[i][j] = c++;
            }
        }
        return res;
    }

    private static ArrayList<String> cartesianProduct(int[][] sets) {
        ArrayList<String> res = new ArrayList<>();
        int solutions = 1;
        for (int i = 0; i < sets.length; solutions *= sets[i].length, i++);
        for (int i = 0; i < solutions; i++) {
            int j = 1;
            String aux = "";
            for (int[] set : sets) {
                aux += String.valueOf(set[(i / j) % set.length]);
//                System.out.print(set[(i / j) % set.length] + " ");
                j *= set.length;
            }
            res.add(aux);
//            System.out.println();
        }
        return res;
    }

    private static ArrayList<String> genNumbers(int length) {
        return cartesianProduct(genArr(length, 10));
    }

    public static void genNumbersCache() {
        res1 = cartesianProduct(genArr(1, 10));
        res2 = cartesianProduct(genArr(2, 10));
        res3 = cartesianProduct(genArr(3, 10));
        res4 = cartesianProduct(genArr(4, 10));
        res5 = cartesianProduct(genArr(5, 10));
    }

    public static ArrayList<String> getCachedNumbers(int length) throws Exception {
        switch (length) {
            case 1: {
                return res1;
            }
            case 2: {
                return res2;
            }
            case 3: {
                return res3;
            }
            case 4: {
                return res4;
            }
            case 5: {
                return res5;
            }
            default :{
                throw new Exception("cached numbers out of bounds");
            }
        }
    }

}
