package io.lumine.mythic.lib.script.targeter;

import io.lumine.mythic.lib.script.targeter.location.Orientable;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;

public abstract class LocationTargeter {
   private final boolean oriented;

   protected LocationTargeter(ConfigObject config) {
      this(config.getBoolean("oriented", false));
   }

   protected LocationTargeter(boolean oriented) {
      this.oriented = oriented;
      Validate.isTrue(
         !oriented || this.getClass().isAnnotationPresent(Orientable.class), "Tried creating an oriented location targeter with a non orientable type"
      );
   }

   protected boolean isOriented() {
      return this.oriented;
   }

   public abstract List<Location> findTargets(SkillMetadata skillMetadata);
}
