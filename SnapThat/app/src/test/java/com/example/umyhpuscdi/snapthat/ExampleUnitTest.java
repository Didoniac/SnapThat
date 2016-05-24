package com.example.umyhpuscdi.snapthat;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Test
    public void containsSuccess(){
        ArrayList<String> a = new ArrayList<>();
        a.add("hej");
        a.add("hoj");
        ArrayList<String> b = new ArrayList<>();
        b.add("hej");
        b.add("hoj");
        assertTrue(MainActivity.doesStringArrayListContainOtherList(a,b));
    }

    @Test
    public void containTrueSubset(){
        ArrayList<String> a = new ArrayList<>();
        a.add("hej");
        a.add("hoj");
        ArrayList<String> b = new ArrayList<>();
        b.add("hej");
        assertTrue(MainActivity.doesStringArrayListContainOtherList(a,b));
    }

    @Test
    public void doesNotContain(){
        ArrayList<String> a = new ArrayList<>();
        a.add("hej");
        a.add("hoj");
        ArrayList<String> b = new ArrayList<>();
        b.add("hej");
        assertFalse(MainActivity.doesStringArrayListContainOtherList(b,a));
    }

    @Test
    public void doesContainEmpty(){
        ArrayList<String> a = new ArrayList<>();
        a.add("hej");
        a.add("hoj");
        ArrayList<String> b = new ArrayList<>();
        assertTrue(MainActivity.doesStringArrayListContainOtherList(a,b));
    }


    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


}