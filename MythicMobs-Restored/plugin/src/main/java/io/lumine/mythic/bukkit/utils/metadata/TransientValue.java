package io.lumine.mythic.bukkit.utils.metadata;

public interface TransientValue<T> {
   T getOrNull();

   boolean shouldExpire();
}
