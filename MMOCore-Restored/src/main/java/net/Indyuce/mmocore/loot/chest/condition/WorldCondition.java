package net.Indyuce.mmocore.loot.chest.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Arrays;
import java.util.List;

public class WorldCondition extends Condition {
   private final List<String> names;

   public WorldCondition(MMOLineConfig config) {
      super(config);
      config.validate(new String[]{"name"});
      this.names = Arrays.asList(config.getString("name").split(","));
   }

   @Override
   public boolean isMet(ConditionInstance entity) {
      return this.names.contains(entity.getLocation().getWorld().getName()) || this.names.contains("__global__");
   }
}
