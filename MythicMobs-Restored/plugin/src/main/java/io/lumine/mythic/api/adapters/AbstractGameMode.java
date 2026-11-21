package io.lumine.mythic.api.adapters;

import java.util.HashMap;
import java.util.Map;

public enum AbstractGameMode {
   SURVIVAL("0", "survival", "s", "normal"),
   CREATIVE("1", "creative", "c", "build"),
   ADVENTURE("2", "adventure", "a"),
   SPECTATOR("3", "spectator", "sp", "spec", "watch");

   private final String id;
   private final String[] aliases;
   private static final Map<String, AbstractGameMode> LOOKUP = new HashMap<>();

   private AbstractGameMode(String id, String... aliases) {
      this.id = id;
      this.aliases = aliases;
   }

   public String getId() {
      return this.id;
   }

   public static AbstractGameMode fromString(String input) {
      return input == null ? null : LOOKUP.get(input.toLowerCase());
   }

   @Override
   public String toString() {
      return this.name().toLowerCase();
   }

   static {
      for (AbstractGameMode mode : values()) {
         LOOKUP.put(mode.id.toLowerCase(), mode);

         for (String alias : mode.aliases) {
            LOOKUP.put(alias.toLowerCase(), mode);
         }
      }
   }
}
