package io.lumine.mythic.api.adapters;

import lombok.Getter;


@Getter
public abstract class AbstractBiome {
   private final String key;

   public AbstractBiome(String key) {
      this.key = key.toLowerCase();
   }

   public abstract String getType();

   @Override
   public String toString() {
      return this.key;
   }

   public boolean matches(String biome) {
      return !biome.contains("*") && !biome.contains("?") ? this.key.equalsIgnoreCase(biome) : this.key.matches(biome.replace("?", ".?").replace("*", ".*?"));
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof AbstractBiome biome) {
         return this.getKey().equals(biome.getKey());
      } else {
         return o instanceof String string ? this.getKey().equalsIgnoreCase(string) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.key.hashCode();
   }
}
