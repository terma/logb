package com.github.terma.logb.node;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class TaggerTest {

    private Tagger tagger = new Tagger();

    @Test
    public void getEmptyTagsIfNullMessage() {
        Assert.assertEquals(new HashSet<String>(), tagger.get(new ArrayList<>(Arrays.asList("[ab]")), null));
    }

    @Test
    public void getEmptyTagsIfNullTags() {
        Assert.assertEquals(new HashSet<String>(), tagger.get(null, "mess"));
    }

    @Test
    public void getTags() {
        Assert.assertEquals(
                new HashSet<>(Arrays.asList("a")),
                tagger.get(new ArrayList<>(Arrays.asList("a")), "a b test"));
    }

    @Test
    public void getMultiOccurienceTags() {
        Assert.assertEquals(
                new HashSet<>(Arrays.asList("a", "b")),
                tagger.get(new ArrayList<>(Arrays.asList("[ab]")), "a b test"));
    }

    @Test
    public void getMultiTags() {
        Assert.assertEquals(
                new HashSet<>(Arrays.asList("a", "b")),
                tagger.get(new ArrayList<>(Arrays.asList("a", "b")), "a b test"));
    }

}
