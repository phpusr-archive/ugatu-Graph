package org.dyndns.phpusr.graph;

import java.util.LinkedList;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: phpusr
 * Date: 31.03.13
 * Time: 18:28
 * To change this template use File | Settings | File Templates.
 */

/**
 * Тест для Стека и Очереди
 */
public class TestStack {
    public static void main(String[] args) {
        System.out.println(">> Stack");
        Stack<String> stack = new Stack<String>();
        stack.push("Sring 1");
        stack.push("Sring 2");
        stack.push("Sring 3");

        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        //System.out.println(stack.pop());

        System.out.println("\n>> Queue & Dequeue");
        LinkedList<String> list = new LinkedList<String>();
        list.push("Sring 1");
        list.push("Sring 2");
        list.push("Sring 3");
        list.push("Sring 4");
        list.push("Sring push");
        list.add("Sring add");
        list.addFirst("Sring addFirst");
        list.addLast("Sring addLast");

        System.out.println(list.poll());
        System.out.println(list.pollFirst());
        System.out.println(list.pollLast());
    }
}
