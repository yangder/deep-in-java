/**
 * fshows.com
 * Copyright (C) 2013-2020 All Rights Reserved.
 */
package com.yangde.stack;

import java.util.Stack;

/**
 * @author yangdw
 * @version LeetCode_20.java, v 0.1 2020-11-24 21:57
 * <p>
 * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。
 * <p>
 * 有效字符串需满足：
 * <p>
 * 左括号必须用相同类型的右括号闭合。
 * 左括号必须以正确的顺序闭合。
 * 注意空字符串可被认为是有效字符串。
 * 示例 1:
 * <p>
 * 输入: "()"
 * 输出: true
 * 示例 2:
 * <p>
 * 输入: "()[]{}"
 * 输出: true
 * 示例 3:
 * <p>
 * 输入: "(]"
 * 输出: false
 * 示例 4:
 * <p>
 * 输入: "([)]"
 * 输出: false
 * 示例 5:
 * <p>
 * 输入: "{[]}"
 * 输出: true
 */
public class LeetCode_20 {

    /**
     * stack.push()压栈处理,stack.pop()栈顶数据出栈,并删除,stack.peek()栈顶数据出栈不删除
     *
     * @param s
     * @return
     */
    public static boolean isValid(String s) {
        Stack<Character> stack = new Stack<Character>();
        for (Character c : s.toCharArray()) {
            if (c == '(') stack.push(')');
            else if (c == '{') stack.push('}');
            else if (c == '[') stack.push(']');
            else if (stack.isEmpty() || c != stack.pop()) return false;
        }
        return false;
    }

    /**
     * 因为符号是遵循顺序闭合,即先有左括号后又右括号,闭合的左右括号中的元素也会满足顺序闭合,循环字符过程中,将右括号先压栈
     * 循环过程到右括号的时候,再通过stack.pop()出栈,并判断c != stack.pop()是否相等
     *
     * @param s
     * @return
     */
    public static boolean isValid1(String s) {
        Stack<Character> stack = new Stack<Character>();
        for (char c : s.toCharArray()) {
            if (c == '(') stack.push(')');
            else if (c == '[') stack.push(']');
            else if (c == '{') stack.push('}');
            else if (stack.isEmpty() || c != stack.pop()) return false;
        }
        return stack.isEmpty();
    }

    /**
     * 如果符合条件的字符串每次会替换掉两个元素,循环s.length()/2
     *
     * @param s
     * @return
     */
    public static boolean isValid2(String s) {
        int length = s.length() / 2;
        for (int i = 0; i < length; i++) {
            s = s.replace("()", "").replace("{}", "").replace("[]", "");
        }

        return s.length() == 0;
    }


    public static void main(String[] args) {
        System.err.println(isValid2("{[]}"));
        System.err.println(isValid1("([)]"));
    }
}   