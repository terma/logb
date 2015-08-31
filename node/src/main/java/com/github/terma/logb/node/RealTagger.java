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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RealTagger {

    public HashSet<String> get(ArrayList<String> tagsPatterns, String message) {
        HashSet<String> result = new HashSet<>();

        if (tagsPatterns != null && message != null) {
            for (String tagPattern : tagsPatterns) {
                Pattern pattern = Pattern.compile(tagPattern);
                Matcher matcher = pattern.matcher(message);
                while (matcher.find()) {
                    result.add(matcher.group(matcher.groupCount()));
                }
            }
        }

        return result;
    }

}
