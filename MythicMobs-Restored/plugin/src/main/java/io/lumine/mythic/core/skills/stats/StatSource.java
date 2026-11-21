package io.lumine.mythic.core.skills.stats;

public interface StatSource {
   default boolean removeOnReload() {
      return true;
   }
}
