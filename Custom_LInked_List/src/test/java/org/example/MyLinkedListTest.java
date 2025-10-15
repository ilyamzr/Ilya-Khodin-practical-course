package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class MyLinkedListTest {

    private MyLinkedList<String> list;

    @BeforeEach
    void setUp() {
        list = new MyLinkedList<>();
    }

    @Test
    void EmptyListSize()
    {
        assertEquals(0, list.size());
    }

    @Test
    void OneElementSize()
    {
        list.addFirst("A");
    }

    @Test
    void MultipleElementsSize()
    {
        list.addFirst("A");
        list.addFirst("B");
        list.addFirst("C");
        assertEquals(3, list.size());
    }

    @Test
    void addFirstEmpty()
    {
        list.addFirst("A");
        assertEquals("A", list.getFirst());
    }

    @Test
    void addFirstNotEmpty()
    {
        list.addFirst("A");
        list.addFirst("B");
        assertEquals("B", list.getFirst());
    }

    @Test
    void addFirstMultiple()
    {
        list.addFirst("A");
        list.addFirst("B");
        list.addFirst("C");
        assertEquals("A", list.get(2));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(0));
    }

    @Test
    void addLastEmpty()
    {
        list.addLast("A");
        assertEquals("A", list.getLast());
    }

    @Test
    void addLastNotEmpty()
    {
        list.addLast("A");
        list.addLast("B");
        assertEquals("B", list.getLast());
    }

    @Test
    void addLastMultiple()
    {
        list.addLast("A");
        list.addLast("B");
        list.addLast("C");
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    @Test
    void addFirstAndLast()
    {
        list.addFirst("A");
        list.addFirst("B");
        list.addLast("C");
        assertEquals("B", list.getFirst());
        assertEquals("C", list.getLast());
    }

    @Test
    void addZeroIndex()
    {
        list.add(0,"A");
        assertEquals("A", list.get(0));
    }

    @Test
    void addMiddleIndex()
    {
        list.addFirst("A");
        list.addLast("C");
        list.add(1,"B");
        assertEquals("B", list.get(1));
    }

    @Test
    void addEndIndex() {
        list.addLast("A");
        list.add(1, "B");
        assertEquals("B", list.get(1));
    }

    @Test
    void addNegativeIndex()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, "A"));
    }

    @Test
    void addTooBigIndex() {
        list.addFirst("A");
        assertThrows(NoSuchElementException.class, () -> list.add(2, "B"));
    }

    @Test
    void getFirstEmpty() {
        assertThrows(NoSuchElementException.class, list::getFirst);
    }

    @Test
    void getFirstNotEmpty() {
        list.addFirst("A");
        assertEquals("A", list.getFirst());
    }


    @Test
    void getLastNotEmpty() {
        list.addLast("A");
        list.addLast("B");
        assertEquals("B", list.getLast());
    }

    @Test
    void getLastElement() {
        list.addLast("A");
        assertEquals("A", list.getLast());
    }

    @Test
    void getLastEmpty() {
        assertThrows(NoSuchElementException.class, list::getLast);
    }

    @Test
    void getZeroIndex() {
        list.addFirst("A");
        assertEquals("A", list.get(0));
    }

    @Test
    void getMiddleIndex() {
        list.addFirst("A");
        list.addFirst("B");
        list.addFirst("C");
        assertEquals("B", list.get(1));
    }

    @Test
    void getLastIndex() {
        list.addFirst("A");
        list.addFirst("B");
        assertEquals("A", list.get(1));
    }

    @Test
    void getNegativeIndex() {
        list.addFirst("A");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    }

    @Test
    void getTooBigIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    void getEmptyList() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    void removeFirstNotEmpty() {
        list.addFirst("A");
        list.addFirst("B");
        String check = list.removeFirst();
        assertEquals("A", check);
        assertEquals("A", list.getFirst());
    }

    @Test
    void removeFirstElement() {
        list.addFirst("A");
        String check = list.removeFirst();
        assertEquals("A", check);
        assertEquals(0, list.size());
    }

    @Test
    void removeFirstEmpty() {
        assertThrows(NoSuchElementException.class, list::removeFirst);
    }

    @Test
    void removeLastMultiple() {
        list.addLast("A");
        list.addLast("B");
        list.addLast("C");
        String check = list.removeLast();
        assertEquals("C", check);
        assertEquals("B", list.getLast());
    }

    @Test
    void removeLastElement() {
        list.addLast("A");
        String check = list.removeLast();
        assertEquals("A", check);
        assertEquals(0, list.size());
    }

    @Test
    void removeLastEmpty() {
        assertThrows(NoSuchElementException.class, list::removeLast);
    }

    @Test
    void removeZeroIndex() {
        list.addLast("A");
        list.addLast("B");
        String check = list.remove(0);
        assertEquals("A", check);
        assertEquals(1, list.size());
        assertEquals("B", list.get(0));
    }

    @Test
    void removeMiddleIndex() {
        list.addLast("A");
        list.addLast("B");
        list.addLast("C");
        String check = list.remove(1);
        assertEquals("B", check);
        assertEquals(2, list.size());
        assertEquals("A", list.get(0));
        assertEquals("C", list.get(1));
    }

    @Test
    void removeLastIndex() {
        list.addLast("A");
        list.addLast("B");
        String check =  list.remove(1);
        assertEquals("B", check);
        assertEquals(1, list.size());
        assertEquals("A", list.get(0));
    }

    @Test
    void removeNegativeIndex() {
        list.addLast("A");
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
    }

    @Test
    void removeTooBigIndex() {
        list.addLast("A");
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(2));
    }

    @Test
    void removeEmptyList() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }
}
