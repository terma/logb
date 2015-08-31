/*
Copyright 2015 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.logb.node;

import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class RealTaggerTest {

    private RealTagger tagger = new RealTagger();

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
    public void getTagsWithGroup() {
        Assert.assertEquals(
                new HashSet<>(Collections.singletonList("1")),
                tagger.get(new ArrayList<>(Collections.singletonList("host-(\\d+)-log")), "host-1-log"));
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
