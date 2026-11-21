package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;

public interface CompassItemNumericProperty extends ItemNumericProperty {
   boolean DEFAULT_WOBBLE = true;

   @NotNull Target target();

   boolean wobble();

   public static enum Target {
      NONE,
      SPAWN,
      LODESTONE,
      RECOVERY;
   }
}
