package team.unnamed.creative.item.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface TimeItemNumericProperty extends ItemNumericProperty {
   @Internal
   boolean DEFAULT_WOBBLE = true;

   boolean wobble();

   @NotNull Source source();

   public static enum Source {
      DAYTIME,
      MOON_PHASE,
      RANDOM;
   }
}
