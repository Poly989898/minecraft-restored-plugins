package io.lumine.mythic.lib.script.targeter.entity;

import io.lumine.mythic.lib.script.targeter.EntityTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class NearbyEntitiesTargeter implements EntityTargeter {
   private final DoubleFormula radius;
   private final DoubleFormula height;
   private final boolean source;

   public NearbyEntitiesTargeter(ConfigObject config) {
      config.validateKeys("radius");
      this.source = config.getBoolean("source", false);
      this.radius = new DoubleFormula(config.getString("radius"));
      this.height = config.contains("height") ? new DoubleFormula(config.getString("height")) : null;
   }

   @Override
   public List<Entity> findTargets(SkillMetadata meta) {
      Location loc = meta.getSkillLocation(this.source);
      double rad = this.radius.evaluate(meta);
      double height = this.height == null ? rad : this.height.evaluate(meta);
      return new ArrayList<>(loc.getWorld().getNearbyEntities(loc, rad, height, rad));
   }
}
