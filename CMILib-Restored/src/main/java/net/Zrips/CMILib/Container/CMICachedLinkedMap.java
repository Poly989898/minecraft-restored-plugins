package net.Zrips.CMILib.Container;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class CMICachedLinkedMap<K, V> {
  private final Map<K, V> map;

  public CMICachedLinkedMap(final int maxSize) {
    this.map =
        new LinkedHashMap<K, V>(maxSize, 0.75F, true) {
          @Override
          protected boolean removeEldestEntry(Entry<K, V> eldest) {
            return this.size() > maxSize;
          }
        };
  }

  public synchronized V get(K key) {
    return this.map.get(key);
  }

  public synchronized void put(K key, V value) {
    this.map.put(key, value);
  }

  public synchronized int size() {
    return this.map.size();
  }

  public synchronized void clear() {
    this.map.clear();
  }

  public synchronized boolean containsKey(K key) {
    return this.map.containsKey(key);
  }

  public synchronized boolean containsValue(V value) {
    return this.map.containsValue(value);
  }

  public synchronized Set<Entry<K, V>> entrySet() {
    return this.map.entrySet();
  }

  public synchronized Collection<V> values() {
    return this.map.values();
  }
}
