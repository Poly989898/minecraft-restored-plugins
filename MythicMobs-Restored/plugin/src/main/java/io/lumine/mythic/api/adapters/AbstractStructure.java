package io.lumine.mythic.api.adapters;

import lombok.Getter;


@Getter
public abstract class AbstractStructure {
   private final String key;

   public AbstractStructure(String key) {
      this.key = key.toLowerCase();
   }

   @Override
   public String toString() {
      return this.key;
   }

   public boolean matches(String structure) {
      return !structure.contains("*") && !structure.contains("?")
         ? this.key.equalsIgnoreCase(structure)
         : this.key.matches(structure.replace("?", ".?").replace("*", ".*?"));
   }

   @Override
   public boolean equals(Object o) {
      if (o instanceof AbstractStructure structure) {
         return this.getKey().equals(structure.getKey());
      } else {
         return o instanceof String string ? this.getKey().equalsIgnoreCase(string) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.key.hashCode();
   }
}
