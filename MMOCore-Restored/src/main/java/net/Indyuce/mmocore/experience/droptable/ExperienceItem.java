package net.Indyuce.mmocore.experience.droptable;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;
import net.Indyuce.mmocore.api.quest.trigger.api.Removable;
import net.Indyuce.mmocore.api.quest.trigger.api.Temporary;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class ExperienceItem {
   private final String id;
   private final int period;
   private final int firstTrigger;
   private final int lastTrigger;
   private final int fixedLevel;
   private final double claimChance;
   private final double failReduction;
   private final List<Trigger> triggers;
   private static final Random random = new Random();

   @Deprecated
   public ExperienceItem(String id, int period, int firstTrigger, double claimChance, double failReduction, List<Trigger> triggers) {
      this(id, period, firstTrigger, Integer.MAX_VALUE, claimChance, failReduction, triggers);
   }

   public ExperienceItem(String id, int period, int firstTrigger, int lastTrigger, double claimChance, double failReduction, List<Trigger> triggers) {
      this.id = id;
      this.period = period;
      this.claimChance = claimChance;
      this.failReduction = failReduction;
      this.triggers = triggers;
      this.firstTrigger = firstTrigger;
      this.lastTrigger = lastTrigger;
      this.fixedLevel = 0;
   }

   public ExperienceItem(String id, int fixedLevel, double claimChance, List<Trigger> triggers) {
      this.id = id;
      this.period = 0;
      this.claimChance = claimChance;
      this.failReduction = 0.0;
      this.triggers = triggers;
      this.firstTrigger = 0;
      this.lastTrigger = 0;
      this.fixedLevel = fixedLevel;
   }

   public ExperienceItem(ConfigurationSection config) {
      Validate.notNull(config, "Config cannot be null");
      Validate.isTrue(config.contains("triggers"), "Could not find config section 'triggers'");
      this.id = config.getName();
      this.period = config.getInt("period", 1);
      this.firstTrigger = config.getInt("first-trigger", this.period);
      this.lastTrigger = config.getInt("last-trigger", Integer.MAX_VALUE);
      this.fixedLevel = config.getInt("level", -1);
      this.claimChance = config.getDouble("chance", 100.0) / 100.0;
      this.failReduction = config.getDouble("fail-reduction", 80.0) / 100.0;
      this.triggers = new ArrayList<>();

      for (String triggerFormat : config.getStringList("triggers")) {
         this.triggers.add(MMOCore.plugin.loadManager.loadTrigger(new MMOLineConfig(triggerFormat)));
      }
   }

   public String getId() {
      return this.id;
   }

   public boolean roll(int professionLevel, int timesCollected) {
      if (this.fixedLevel > -1) {
         return this.fixedLevel == professionLevel;
      }

      if (professionLevel > this.lastTrigger) {
         return false;
      }

      if (this.period == 0 && timesCollected > 0) {
         return false;
      }

      int claimsRequired = professionLevel + 1 - (this.firstTrigger + timesCollected * this.period);
      if (claimsRequired < 1) {
         return false;
      }

      double chance = 1.0 - (1.0 - this.claimChance) * Math.pow(this.failReduction, claimsRequired);
      return random.nextDouble() < chance;
   }

   public void applyTriggers(PlayerData levelingUp) {
      for (Trigger trigger : this.triggers) {
         trigger.apply(levelingUp);
      }
   }

   public void removeTriggers(PlayerData playerData) {
      for (Trigger trigger : this.triggers) {
         if (trigger instanceof Removable) {
            ((Removable)trigger).remove(playerData);
         }
      }
   }

   public void applyTemporaryTriggers(PlayerData playerData) {
      for (Trigger trigger : this.triggers) {
         if (trigger instanceof Temporary) {
            trigger.apply(playerData);
         }
      }
   }
}
