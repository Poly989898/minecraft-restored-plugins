package net.Zrips.CMILib.Container;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CMISort {
  public static Map<String, Integer> sortIntDESC(Map<String, Integer> unsortMap) {
    List<Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());
    Collections.sort(
        list,
        new Comparator<Entry<String, Integer>>() {
          public int compare(Entry<String, Integer> first, Entry<String, Integer> second) {
            return second.getValue().compareTo(first.getValue());
          }
        });
    Map<String, Integer> sortedMap = new LinkedHashMap<>();

    for (Entry<String, Integer> entry : list) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }

    return sortedMap;
  }

  public static Map<String, Integer> sortIntASC(Map<String, Integer> unsortMap) {
    List<Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());
    Collections.sort(
        list,
        new Comparator<Entry<String, Integer>>() {
          public int compare(Entry<String, Integer> first, Entry<String, Integer> second) {
            return first.getValue().compareTo(second.getValue());
          }
        });
    Map<String, Integer> sortedMap = new LinkedHashMap<>();

    for (Entry<String, Integer> entry : list) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }

    return sortedMap;
  }

  public static Map<String, Double> sortDoubleDESC(Map<String, Double> unsortMap) {
    List<Entry<String, Double>> list = new LinkedList<>(unsortMap.entrySet());
    Collections.sort(
        list,
        new Comparator<Entry<String, Double>>() {
          public int compare(Entry<String, Double> first, Entry<String, Double> second) {
            return second.getValue().compareTo(first.getValue());
          }
        });
    Map<String, Double> sortedMap = new LinkedHashMap<>();

    for (Entry<String, Double> entry : list) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }

    return sortedMap;
  }

  public static Map<String, Double> sortDoubleASC(Map<String, Double> unsortMap) {
    List<Entry<String, Double>> list = new LinkedList<>(unsortMap.entrySet());
    Collections.sort(
        list,
        new Comparator<Entry<String, Double>>() {
          public int compare(Entry<String, Double> first, Entry<String, Double> second) {
            return first.getValue().compareTo(second.getValue());
          }
        });
    Map<String, Double> sortedMap = new LinkedHashMap<>();

    for (Entry<String, Double> entry : list) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }

    return sortedMap;
  }
}
