package net.Zrips.CMILib.Util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Sorting {
  public HashMap<String, Double> sortByValue(Map<String, Double> unsortMap) {
    List<Entry<String, Double>> list = new LinkedList<>(unsortMap.entrySet());
    Collections.sort(
        list,
        new Comparator<Entry<String, Double>>() {
          public int compare(Entry<String, Double> first, Entry<String, Double> second) {
            return second.getValue().compareTo(first.getValue());
          }
        });
    HashMap<String, Double> sortedMap = new LinkedHashMap<>();

    for (Entry<String, Double> entry : list) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }

    return sortedMap;
  }

  public HashMap<String, Long> sortByValueLong(Map<String, Long> unsortMap) {
    List<Entry<String, Long>> list = new LinkedList<>(unsortMap.entrySet());
    Collections.sort(
        list,
        new Comparator<Entry<String, Long>>() {
          public int compare(Entry<String, Long> first, Entry<String, Long> second) {
            return second.getValue().compareTo(first.getValue());
          }
        });
    HashMap<String, Long> sortedMap = new LinkedHashMap<>();

    for (Entry<String, Long> entry : list) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }

    return sortedMap;
  }

  public HashMap<Object, Integer> sortByValueObject(Map<Object, Integer> unsortMap) {
    List<Entry<Object, Integer>> list = new LinkedList<>(unsortMap.entrySet());
    Collections.sort(
        list,
        new Comparator<Entry<Object, Integer>>() {
          public int compare(Entry<Object, Integer> first, Entry<Object, Integer> second) {
            return first.getValue().compareTo(second.getValue());
          }
        });
    HashMap<Object, Integer> sortedMap = new LinkedHashMap<>();

    for (Entry<Object, Integer> entry : list) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }

    return sortedMap;
  }
}
