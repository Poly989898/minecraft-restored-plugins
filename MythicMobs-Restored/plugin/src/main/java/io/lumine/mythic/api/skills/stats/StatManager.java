package io.lumine.mythic.api.skills.stats;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.skills.stats.StatType;
import java.util.Collection;
import java.util.Optional;

public interface StatManager {
   Optional<StatType> getStat(String str);

   Optional<? extends StatSnapshot> getStatRegistry(AbstractEntity abstractEntity);

   Collection<StatType> getNonZeroBaseStats();

   static String translateLegacyStatAliases(String key) {
      if (key == null) {
         return null;
      } else {
         String toUpperCase = key.toUpperCase();

         return switch (toUpperCase) {
            case "ARMOUR" -> "ARMOR";
            case "ATTACKSPEED" -> "ATTACK_SPEED";
            case "DAMAGE" -> "ATTACK_DAMAGE";
            case "KNOCKBACKRESISTANCE" -> "KNOCKBACK_RESISTANCE";
            case "MOVEMENTSPEED" -> "MOVEMENT_SPEED";
            default -> key.toUpperCase();
         };
      }
   }
}
