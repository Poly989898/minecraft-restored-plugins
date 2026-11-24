package io.lumine.mythic.lib.api.stat.provider;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface StatProvider {
   double getStat(String text);

   LivingEntity getEntity();

   @Deprecated
   static StatProvider get(LivingEntity living) {
      return get(living, EquipmentSlot.MAIN_HAND, true);
   }

   @Deprecated
   static StatProvider generate(LivingEntity living, EquipmentSlot actionHand) {
      return get(living, actionHand, true);
   }

   @NotNull
   static StatProvider get(LivingEntity living, EquipmentSlot actionHand, boolean cache) {
      if (!UtilityMethods.isRealPlayer(living)) {
         return new EntityStatProvider(living);
      }

      Player player = (Player)living;
      StatMap statMap = MMOPlayerData.get(player).getStatMap();
      return cache ? statMap.cache(actionHand) : statMap;
   }
}
