package com.example.umyhpuscdi.snapthat.Comparators;

import com.example.umyhpuscdi.snapthat.ThingToPhotograph;

import java.util.Comparator;

/**
 * Created by umyhpuscdi on 2016-05-18.
 */
public class ThingToPhotographIndexComparator implements Comparator<ThingToPhotograph> {
    @Override
    public int compare(ThingToPhotograph firstThing, ThingToPhotograph secondThing) {
        if (firstThing.getIndex() > secondThing.getIndex()) {
            return 1;
        } else if (firstThing.getIndex() < secondThing.getIndex()) {
            return -1;
        } else {
            return 0;
        }
    }
}
