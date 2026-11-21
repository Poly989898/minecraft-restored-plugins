package io.lumine.mythic.core.skills.targeters.shapes;

import io.lumine.mythic.api.config.MythicLineConfig;
import java.util.Locale;
import java.util.function.Function;

public enum TargeterShapes {
   SPHERE("sphere", SphereShape::new),
   CYLINDER("cylinder", CylinderShape::new),
   SQUARE("square", SquareShape::new);

   private final String key;
   private final Function<MythicLineConfig, ITargeterShape> constructor;

   private TargeterShapes(String key, Function<MythicLineConfig, ITargeterShape> constructor) {
      this.key = key;
      this.constructor = constructor;
   }

   public ITargeterShape create(MythicLineConfig config) {
      return this.constructor.apply(config);
   }

   public static TargeterShapes from(String name) {
      String normalized = name == null ? "" : name.trim().toLowerCase(Locale.ROOT);

      for (TargeterShapes ts : values()) {
         if (ts.key.equals(normalized)) {
            return ts;
         }
      }

      return SPHERE;
   }
}
