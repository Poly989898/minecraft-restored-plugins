package io.lumine.mythic.lib.script.targeter.entity;

import io.lumine.mythic.lib.script.targeter.EntityTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class NearestEntityTargeter implements EntityTargeter {
   private final DoubleFormula radius;
   private final boolean source;

   public NearestEntityTargeter(ConfigObject config) {
      config.validateKeys("radius");
      this.source = config.getBoolean("source", false);
      this.radius = new DoubleFormula(config.getString("radius"));
   }

   @Override
   public List<Entity> findTargets(SkillMetadata meta) {
      Location loc = meta.getSkillLocation(this.source);
      double rad = this.radius.evaluate(meta);
      Entity nearest = null;
      double dist = Double.MAX_VALUE;

      for (Entity entity : loc.getWorld().getNearbyEntities(loc, rad, rad, rad)) {
         double checked = entity.getLocation().distanceSquared(loc);
         if (checked < dist) {
            nearest = entity;
            dist = checked;
         }
      }

      List<Entity> list = new ArrayList<>();
      if (nearest != null) {
         list.add(nearest);
      }

      return list;
   }
}
