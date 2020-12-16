/**
 * fshows.com
 * Copyright (C) 2013-2020 All Rights Reserved.
 */
package com.yangde.stack;

/**
 * @author yangdw
 * @version LeetCode_738.java, v 0.1 2020-12-15 09:19
 * <p>
 * 给定一个非负整数 N，找出小于或等于 N 的最大的整数，同时这个整数需要满足其各个位数上的数字是单调递增。
 * <p>
 * （当且仅当每个相邻位数上的数字 x 和 y 满足 x <= y 时，我们称这个整数是单调递增的。）
 * <p>
 * 示例 1:
 * <p>
 * 输入: N = 10
 * 输出: 9
 * 示例 2:
 * <p>
 * 输入: N = 1234
 * 输出: 1234
 * 示例 3:
 * <p>
 * 输入: N = 332
 * 输出: 299
 * 说明: N 是在 [0, 10^9] 范围内的一个整数。
 * <p>
 */

public class LeetCode_738 {

    /**
     * ☆☆
     * 贪心算法
     * 单调递增的数字
     * n   = 1234321
     * res = 1233999
     * <p>
     * n    = 2333332
     * res  = 2299999
     *
     * @param N
     * @return
     */
    public static int monotoneIncreasingDigits(int N) {
        char[] strN = Integer.valueOf(N).toString().toCharArray();
        // 先找到strN[i] > strN[i+1]
        int idx;
        for (int i = 0; i < strN.length - 1; i++) {
            if (strN[i] > strN[i + 1]) {
                strN[i] -= 1;
                idx = i;
                // strN[i]-1之后的值可能不能满足strN[i-1]<=strN[i],需要处理该情况
                if (idx > 0 && strN[idx] < strN[idx - 1]) {
                    for (int j = idx; j > 0; j--) {
                        if (strN[j] < strN[j - 1]) {
                            strN[j - 1] -= 1;
                            idx = j - 1;
                        }
                    }
                }
                //strN[idx+1]~strN[strN.length]赋值为9
                for (int j = idx + 1; j < strN.length; j++) {
                    strN[j] = '9';
                }
            }
        }
        return Integer.valueOf(new String(strN));
    }

    /**
     * ☆☆☆☆
     *
     * @param N
     * @return
     */
    public static int monotoneIncreasingDigits1(int N) {
        char[] arr = (N + "").toCharArray();
        int max = -1, idx = -1;
        for (int i = 0; i < arr.length - 1; i++) {
            if (max < arr[i]) {
                max = arr[i];
                idx = i;
            }
            if (arr[i] > arr[i + 1]) {
                arr[idx] -= 1;
                for (int j = idx + 1; j < arr.length; j++) {
                    arr[j] = '9';
                }
                break;
            }
        }
        return Integer.parseInt(new String(arr));
    }


    public static void main(String[] args) {
//        System.err.println(monotoneIncreasingDigits(1234321));
//        System.err.println(monotoneIncreasingDigits(92));
//        System.err.println(monotoneIncreasingDigits(1));
//        System.err.println(monotoneIncreasingDigits(232));
        System.err.println(monotoneIncreasingDigits1(2333332));


    }
}   