package org.example;

import java.util.NoSuchElementException;

public class MyLinkedList<I> {

    private Node first;

    private class Node
    {
        I item;
        Node next;
    }

    public MyLinkedList()
    {
        first = null;
    }

    public int size() {

        if (first == null) return 0;
        else
        {
            Node temp = first;
            int count = 1;
            while (temp.next != null)
            {
                temp = temp.next;
                count++;
            }
            return count;
        }
    }

    public void addFirst(I item) {
        Node newNode = new Node();
        newNode.item = item;
        newNode.next = first;
        first = newNode;
    }

    public void addLast(I item) {

        if (first == null)
        {
            addFirst(item);
            return;
        }

        Node newLast = new Node();
        newLast.item = item;
        newLast.next = null;

        Node temp = first;

        while (temp.next != null) {
            temp = temp.next;
        }

        temp.next = newLast;
    }

    public void add(int index, I item) {
        if (size() < index) throw new NoSuchElementException();
        else if (index < 0) throw new IndexOutOfBoundsException();
        else if (index == 0) addFirst(item);
        else
        {
            Node newNode = new Node();
            newNode.item = item;
            int count = 0;
            Node temp = first;

            while (count != index - 1) {
                temp = temp.next;
                count++;
            }

            newNode.next = temp.next;
            temp.next = newNode;
        }
    }

    public I getFirst() {
        if (first == null) throw new NoSuchElementException();
        return first.item;
    }

    public I getLast() {
        if (first == null) throw new NoSuchElementException();
        Node temp = first;
        while (temp.next != null) {
            temp = temp.next;
        }
        return temp.item;
    }

    public I get(int index) {

        if (size() < index) throw new NoSuchElementException();
        else if(index < 0) throw new IndexOutOfBoundsException();
        else
        {
            int count = 0;
            Node temp = first;

            while (count != index) {
                temp = temp.next;
                count++;
            }
            return temp.item;
        }
    }

    public I removeFirst() {
        if (first == null) throw new NoSuchElementException();
        I item = first.item;
        first = first.next;
        return item;
    }

    public I removeLast() {
        if (first == null) throw new NoSuchElementException();
        if (first.next == null)
        {
            I item = first.item;
            first = null;
            return item;
        }
        Node temp = first;
        while (temp.next.next != null) {
            temp = temp.next;
        }
        I item = temp.next.item;
        temp.next = null;
        return item;
    }

    public I remove(int index) {
        if (size() < index) throw new NoSuchElementException();
        else if (index < 0) throw new IndexOutOfBoundsException();
        else if (index == 0) return removeFirst();
        else
        {
            Node temp = first;
            int count = 0;
            while (count != index-1) {
                count++;
                temp = temp.next;
            }
            I item = temp.next.item;
            temp.next = temp.next.next;
            return item;
        }
    }
}
