package io.lumine.mythic.core.volatilecode.disabled;

import io.lumine.mythic.api.volatilecode.handlers.VolatileAIHandler;
import java.util.List;
import org.bukkit.entity.LivingEntity;

public class VolatileAIHandlerDisabled implements VolatileAIHandler {
   @Override
   public void addPathfinderGoals(LivingEntity entity, List<String> aiMods) {
   }

   @Override
   public void addTargetGoals(LivingEntity entity, List<String> aiMods) {
   }
}
