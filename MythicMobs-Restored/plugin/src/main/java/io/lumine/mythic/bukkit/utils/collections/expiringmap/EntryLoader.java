package io.lumine.mythic.bukkit.utils.collections.expiringmap;

public interface EntryLoader<K, V> {
   V load(K k);
}
