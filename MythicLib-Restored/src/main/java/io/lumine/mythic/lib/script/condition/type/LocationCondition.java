package io.lumine.mythic.lib.script.condition.type;

import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.Location;

public abstract class LocationCondition extends Condition {
   private final boolean source;

   public LocationCondition(ConfigObject config, boolean sourceIsDefault) {
      super(config);
      this.source = config.getBoolean("source", sourceIsDefault);
   }

   @Override
   public boolean isMet(SkillMetadata meta) {
      return this.isMet(meta, meta.getSkillLocation(this.source));
   }

   public abstract boolean isMet(SkillMetadata skillMetadata, Location location);
}
