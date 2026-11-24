package io.lumine.mythic.lib.script.condition.location;

import io.lumine.mythic.lib.script.condition.type.LocationCondition;
import io.lumine.mythic.lib.script.variable.Variable;
import io.lumine.mythic.lib.script.variable.def.PositionVariable;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.Position;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class CuboidCondition extends LocationCondition {
   private final String varName1;
   private final String varName2;

   public CuboidCondition(ConfigObject config) {
      super(config, false);
      config.validateKeys("first", "second", "third");
      this.varName1 = config.getString("loc1");
      this.varName2 = config.getString("loc2");
   }

   @Override
   public boolean isMet(SkillMetadata meta, Location loc) {
      Variable var1x = meta.getVariable(this.varName1);
      Validate.isTrue(var1x instanceof PositionVariable, "Variable '" + this.varName1 + "' is not a vector");
      Vector cornerA = ((Position)var1x.getStored()).toVector();
      Variable var2x = meta.getVariable(this.varName1);
      Validate.isTrue(var2x instanceof PositionVariable, "Variable '" + this.varName2 + "' is not a vector");
      Vector cornerB = ((Position)var2x.getStored()).toVector();
      return BoundingBox.of(cornerA, cornerB).contains(loc.toVector());
   }
}
