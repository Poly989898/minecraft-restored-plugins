package io.lumine.mythic.bukkit.utils.collections.expiringmap;

public interface ExpiringEntryLoader<K, V> {
   ExpiringValue<V> load(K k);
}
