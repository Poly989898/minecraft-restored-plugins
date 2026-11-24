package io.lumine.mythic.lib.script.targeter.location;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@Orientable
public class CircleLocationTargeter extends LocationTargeter {
   private final boolean source;
   private final DoubleFormula radius;
   private final DoubleFormula amount;

   public CircleLocationTargeter(ConfigObject config) {
      super(config);
      config.validateKeys("radius", "amount");
      this.source = config.getBoolean("source", false);
      this.radius = new DoubleFormula(config.getString("radius"));
      this.amount = new DoubleFormula(config.getString("amount"));
   }

   @Override
   public List<Location> findTargets(SkillMetadata meta) {
      Location source = meta.getSkillLocation(this.source);
      int amount = (int)this.amount.evaluate(meta);
      Validate.isTrue(amount >= 0, "Amount cannot be negative");
      double rad = this.radius.evaluate(meta);
      double step = (Math.PI * 2) / amount;
      List<Location> targets = new ArrayList<>();
      Vector axis = this.isOriented() ? meta.getTargetLocation().clone().subtract(meta.getSourceLocation()).toVector() : new Vector(0, 1, 0);
      double[] coords = UtilityMethods.getYawPitch(axis);

      for (int i = 0; i < amount; i++) {
         Vector vec = new Vector(rad * Math.cos(i * step), 0.0, rad * Math.sin(i * step));
         if (this.isOriented()) {
            vec = UtilityMethods.rotate(vec, coords[0], coords[1]);
         }

         targets.add(source.clone().add(vec));
      }

      return targets;
   }
}
