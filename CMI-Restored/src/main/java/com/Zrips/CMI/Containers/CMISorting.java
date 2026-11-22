package com.Zrips.CMI.Containers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class CMISorting {
  public static Map<String, Integer> sortByValueDescending(Map<String, Integer> map) {
    return map.entrySet().stream()
        .sorted(Entry.<String, Integer>comparingByValue().reversed())
        .collect(
            Collectors.toMap(
                Entry::getKey, Entry::getValue, (value, other) -> value, LinkedHashMap::new));
  }
}
