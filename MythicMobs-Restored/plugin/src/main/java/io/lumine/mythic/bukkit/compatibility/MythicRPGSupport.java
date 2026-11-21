package io.lumine.mythic.bukkit.compatibility;

public interface MythicRPGSupport {
   default boolean isUsingSQL() {
      return false;
   }
}
