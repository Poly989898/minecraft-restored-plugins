package io.lumine.mythic.lib.script.targeter.entity;

import io.lumine.mythic.lib.script.targeter.EntityTargeter;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class ConeTargeter implements EntityTargeter {
   private final DoubleFormula radius;
   private final DoubleFormula angle;
   private final LocationTargeter sourceLocation;
   private final LocationTargeter direction;

   public ConeTargeter(ConfigObject config) {
      config.validateKeys("radius", "angle");
      this.sourceLocation = config.contains("source") ? config.getLocationTargeter("source") : null;
      this.direction = config.contains("direction") ? config.getLocationTargeter("direction") : null;
      this.angle = new DoubleFormula(config.getString("angle"));
      this.radius = new DoubleFormula(config.getString("radius"));
   }

   @Override
   public List<Entity> findTargets(SkillMetadata meta) {
      Location loc = this.sourceLocation == null ? meta.getCaster().getPlayer().getEyeLocation() : this.sourceLocation.findTargets(meta).get(0);
      Vector dir = this.direction == null ? loc.getDirection() : this.direction.findTargets(meta).get(0).toVector();
      double rad = this.radius.evaluate(meta);
      double angle = Math.toRadians(this.angle.evaluate(meta));
      List<Entity> list = new ArrayList<>();

      for (Entity nearby : loc.getWorld().getNearbyEntities(loc, rad, rad, rad)) {
         if (nearby.getLocation().subtract(loc).toVector().angle(dir) < angle && !nearby.equals(meta.getCaster().getPlayer())) {
            list.add(nearby);
         }
      }

      return list;
   }
}
