package io.lumine.mythic.core.skills.cinematic;

public enum CinematicMode {
   DISPLAY,
   TELEPORT;

   public static CinematicMode fromString(String name, CinematicMode fallback) {
      if (name == null) {
         return fallback;
      } else {
         try {
            return valueOf(name.trim().toUpperCase());
         } catch (IllegalArgumentException e) {
            return fallback;
         }
      }
   }
}
