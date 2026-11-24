package io.lumine.mythic.lib.script.targeter.location;

import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;

@Orientable
public class CustomLocationTargeter extends LocationTargeter {
   private final DoubleFormula x;
   private final DoubleFormula y;
   private final DoubleFormula z;
   private final boolean relative;
   private final boolean source;

   public CustomLocationTargeter(ConfigObject config) {
      super(config);
      config.validateKeys("x", "y", "z");
      this.x = config.getDoubleFormula("x");
      this.y = config.getDoubleFormula("y");
      this.z = config.getDoubleFormula("z");
      this.relative = config.getBoolean("relative", true);
      this.source = config.getBoolean("source", false);
   }

   @Override
   public List<Location> findTargets(SkillMetadata meta) {
      Location loc = this.relative ? meta.getSkillLocation(this.source) : new Location(meta.getSourceLocation().getWorld(), 0.0, 0.0, 0.0);
      loc.add(this.x.evaluate(meta), this.y.evaluate(meta), this.z.evaluate(meta));
      return Arrays.asList(loc);
   }
}
