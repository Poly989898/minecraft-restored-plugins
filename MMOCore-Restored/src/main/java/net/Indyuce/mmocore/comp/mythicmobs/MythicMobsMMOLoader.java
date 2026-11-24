package net.Indyuce.mmocore.comp.mythicmobs;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.load.MMOLoader;
import net.Indyuce.mmocore.api.quest.objective.Objective;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;
import net.Indyuce.mmocore.comp.mythicmobs.load.KillMythicFactionExperienceSource;
import net.Indyuce.mmocore.comp.mythicmobs.load.KillMythicFactionObjective;
import net.Indyuce.mmocore.comp.mythicmobs.load.KillMythicMobExperienceSource;
import net.Indyuce.mmocore.comp.mythicmobs.load.KillMythicMobObjective;
import net.Indyuce.mmocore.comp.mythicmobs.load.MMDropTableDropItem;
import net.Indyuce.mmocore.comp.mythicmobs.load.MythicSkillTrigger;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.ExperienceSource;
import net.Indyuce.mmocore.loot.droptable.dropitem.DropItem;
import org.bukkit.configuration.ConfigurationSection;

public class MythicMobsMMOLoader extends MMOLoader {
   @Override
   public Trigger loadTrigger(MMOLineConfig config) {
      return !config.getKey().equalsIgnoreCase("mmskill") && !config.getKey().equalsIgnoreCase("mythicmobskill") ? null : new MythicSkillTrigger(config);
   }

   @Override
   public DropItem loadDropItem(MMOLineConfig config) {
      return config.getKey().equals("mmdroptable") ? new MMDropTableDropItem(config) : null;
   }

   @Override
   public Objective loadObjective(MMOLineConfig config, ConfigurationSection section) {
      if (config.getKey().equalsIgnoreCase("killmythicmob")) {
         return new KillMythicMobObjective(section, config);
      } else {
         return config.getKey().equalsIgnoreCase("killmythicfaction") ? new KillMythicFactionObjective(section, config) : null;
      }
   }

   @Override
   public ExperienceSource<?> loadExperienceSource(MMOLineConfig config, ExperienceDispenser dispenser) {
      if (config.getKey().equalsIgnoreCase("killmythicmob")) {
         return new KillMythicMobExperienceSource(dispenser, config);
      } else {
         return config.getKey().equalsIgnoreCase("killmythicfaction") ? new KillMythicFactionExperienceSource(dispenser, config) : null;
      }
   }
}
