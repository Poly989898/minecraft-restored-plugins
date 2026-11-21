package io.lumine.mythic.api.volatilecode.handlers;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MobEars;
import io.lumine.mythic.core.mobs.ActiveMob;
import java.util.List;
import org.bukkit.entity.LivingEntity;

public interface VolatileAIHandler {
   default MobEars createEars(ActiveMob activeMob) {
      return null;
   }

   void addPathfinderGoals(LivingEntity livingEntity, List<String> strs);

   void addTargetGoals(LivingEntity livingEntity, List<String> strs);

   default void navigateToLocation(AbstractEntity entity, AbstractLocation destination, double speedModifier) {
   }

   default void clearPath(AbstractEntity entity) {
   }

   default void setPathfindingMalus(AbstractEntity entity, String malus, float value) {
   }

   default void setNavigationController(AbstractEntity entity, String type) {
   }

   default void resetAI(AbstractEntity entity) {
   }

   default void enableAsyncPathfinding(AbstractEntity entity) {
   }

   default void shutdownAsyncPathfinding() {
   }
}
