package io.lumine.mythic.core.worlds;

import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import java.util.UUID;

public class WorldData {
   private UUID uniqueId;
   private String name;
   private VariableRegistry variables = new VariableRegistry();

   public WorldData(AbstractWorld world) {
      this.uniqueId = world.getUniqueId();
      this.name = world.getName();
   }

   public VariableRegistry getVariables() {
      return this.variables;
   }
}
