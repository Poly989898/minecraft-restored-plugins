package io.lumine.mythic.bukkit.utils.collections.expiringmap;

public interface ExpirationListener<K, V> {
   void expired(K k, V v);
}
