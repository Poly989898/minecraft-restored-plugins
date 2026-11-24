package net.Indyuce.mmocore.api.player.stats;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.api.stat.modifier.StatModifier;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.player.skill.PassiveSkill;
import io.lumine.mythic.lib.player.skill.PassiveSkillMap;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmocore.player.stats.StatInfo;
import net.Indyuce.mmocore.skill.ClassSkill;

public class PlayerStats {
   private final PlayerData data;
   private static final String MODIFIER_KEY = "MMOCoreClass";

   public PlayerStats(PlayerData data) {
      this.data = data;
   }

   public PlayerData getData() {
      return this.data;
   }

   public StatMap getMap() {
      return this.data.getMMOPlayerData().getStatMap();
   }

   @Deprecated
   public StatInstance getInstance(StatType stat) {
      return this.getMap().getInstance(stat.name());
   }

   @Deprecated
   public StatInstance getInstance(String stat) {
      return this.getMap().getInstance(stat);
   }

   public double getStat(String stat) {
      return this.getMap().getStat(stat);
   }

   public double getBase(String stat) {
      Profession profession = StatInfo.valueOf(stat).profession;
      return this.data.getProfess().calculateStat(stat, profession == null ? this.data.getLevel() : this.data.getCollectionSkills().getLevel(profession));
   }

   public void updateStats() {
      this.updateStats(false);
   }

   public synchronized void updateStats(boolean castLoginScripts) {
      this.getMap()
         .bufferUpdates(
            () -> {
               for (String stat : MMOCore.plugin.statManager.getRegistered()) {
                  StatInstance instance = this.getMap().getInstance(stat);
                  instance.removeIf("MMOCoreClass"::equals);
                  double total = this.getBase(instance.getStat()) - instance.getBase();
                  if (total != 0.0) {
                     instance.registerModifier(
                        new StatModifier("MMOCoreClass", instance.getStat(), total, ModifierType.FLAT, EquipmentSlot.OTHER, ModifierSource.OTHER)
                     );
                  }
               }
            }
         );
      PassiveSkillMap skillMap = this.data.getMMOPlayerData().getPassiveSkillMap();
      skillMap.removeModifiers("MMOCorePermanentSkill");

      for (ClassSkill skill : this.data.getProfess().getSkills()) {
         if (skill.isPermanent() && skill.getSkill().getTrigger() != TriggerType.LOGIN && this.data.hasUnlocked(skill) && this.data.hasUnlockedLevel(skill)) {
            skillMap.addModifier(skill.toPassive(this.data));
         }
      }

      skillMap.removeModifiers("MMOCoreClassScript");

      for (PassiveSkill script : this.data.getProfess().getScripts()) {
         if (script.getType() != TriggerType.LOGIN) {
            skillMap.addModifier(script);
         }
      }

      if (castLoginScripts) {
         for (ClassSkill skill : this.data.getProfess().getSkills()) {
            if (skill.getSkill().getTrigger() == TriggerType.LOGIN) {
               skill.toCastable(this.data).cast(this.data.getMMOPlayerData());
            }
         }

         for (PassiveSkill skill : this.data.getProfess().getScripts()) {
            if (skill.getType() == TriggerType.LOGIN) {
               skill.getTriggeredSkill().cast(this.data.getMMOPlayerData());
            }
         }
      }
   }
}
