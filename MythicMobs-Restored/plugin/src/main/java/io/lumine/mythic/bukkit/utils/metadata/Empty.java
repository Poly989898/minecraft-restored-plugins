package io.lumine.mythic.bukkit.utils.metadata;

public final class Empty {
   private static final Empty INSTANCE = new Empty();

   public static Empty instance() {
      return INSTANCE;
   }

   private Empty() {
   }

   @Override
   public boolean equals(Object obj) {
      return obj == this;
   }

   @Override
   public String toString() {
      return "Empty";
   }
}
