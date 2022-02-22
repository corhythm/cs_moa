package com.mju.csmoa;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ListTest {

    @Test
    public void arrayListTest() {
        List<String> tempList = new ArrayList<>();
        tempList.add("Hello");
        tempList.add("World");
        tempList.add("Wow");
        tempList.add(2, "Good Job!");
        System.out.println(tempList);

        tempList.remove(1);
        System.out.println(tempList);
    }

    @Test
    public void doubleLinkedList() {
        
    }
}

