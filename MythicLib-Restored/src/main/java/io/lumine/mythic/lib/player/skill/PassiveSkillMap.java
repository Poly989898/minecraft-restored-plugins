package io.lumine.mythic.lib.player.skill;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.player.modifier.ModifierMap;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerMetadata;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PassiveSkillMap extends ModifierMap<PassiveSkill> {
   private final Map<String, Long> lastCast = new HashMap<>();

   public PassiveSkillMap(MMOPlayerData playerData) {
      super(playerData);
   }

   @Nullable
   public PassiveSkill getSkill(@NotNull SkillHandler handler) {
      for (PassiveSkill passive : this.getModifiers()) {
         if (handler.equals(passive.getTriggeredSkill().getHandler())) {
            return passive;
         }
      }

      return null;
   }

   public void tickTimerSkills() {
      TriggerMetadata triggerMeta = new TriggerMetadata(this.getPlayerData(), TriggerType.TIMER, EquipmentSlot.MAIN_HAND, null, null, null, null, null);

      for (PassiveSkill passive : this.getModifiers()) {
         if (passive.getType().equals(TriggerType.TIMER) && this.getPlayerData().getPlayer().getGameMode() != GameMode.SPECTATOR) {
            String key = passive.getTriggeredSkill().getHandler().getId();
            Long mapValue = this.lastCast.get(key);
            long lastCast = mapValue == null ? 0L : mapValue;
            if (lastCast + passive.getTimerPeriod() <= System.currentTimeMillis()) {
               this.lastCast.put(key, System.currentTimeMillis());
               passive.getTriggeredSkill().cast(triggerMeta);
            }
         }
      }
   }
}
