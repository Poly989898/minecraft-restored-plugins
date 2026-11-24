package net.Indyuce.inventory.compat;

import javax.inject.Provider;
import net.Indyuce.inventory.compat.list.AureliumSkillsHook;
import net.Indyuce.inventory.compat.list.BattleLevelsHook;
import net.Indyuce.inventory.compat.list.DefaultHook;
import net.Indyuce.inventory.compat.list.FabledHook;
import net.Indyuce.inventory.compat.list.HeroesHook;
import net.Indyuce.inventory.compat.list.MMOCoreHook;
import net.Indyuce.inventory.compat.list.McMMOHook;
import net.Indyuce.inventory.compat.list.McRPGHook;
import net.Indyuce.inventory.compat.list.ProSkillAPIHook;
import net.Indyuce.inventory.compat.list.RPGPlayerLevelingHook;
import net.Indyuce.inventory.compat.list.RacesAndClassesHook;
import net.Indyuce.inventory.compat.list.SkillsHook;
import net.Indyuce.inventory.compat.list.SkillsProHook;
import org.bukkit.Bukkit;

public enum ModuleType {
   AURELIUMSKILLS("AureliumSkills", AureliumSkillsHook::new),
   BATTLELEVELS("BattleLevels", BattleLevelsHook::new),
   DEFAULT(null, DefaultHook::new),
   FABLED("Fabled", FabledHook::new),
   HEROES("Heroes", HeroesHook::new),
   MCMMO("mcMMO", McMMOHook::new),
   MCRPG("McRPG", McRPGHook::new),
   MMOCORE("MMOCore", MMOCoreHook::new),
   PROSKILLAPI("ProSkillAPI", ProSkillAPIHook::new),
   RACESANDCLASSES("RacesAndClasses", RacesAndClassesHook::new),
   RPGPLAYERLEVELING("RPGPlayerLeveling", RPGPlayerLevelingHook::new),
   SKILLAPI("SkillAPI", ProSkillAPIHook::new),
   SKILLS("Skills", SkillsHook::new),
   SKILLSPRO("SkillsPro", SkillsProHook::new);

   private final Provider<Object> moduleProvider;
   private final String pluginName;

   ModuleType(String pluginName, Provider<Object> moduleProvider) {
      this.pluginName = pluginName;
      this.moduleProvider = moduleProvider;
   }

   public Object getModule() {
      return this.moduleProvider.get();
   }

   public boolean findPlugin() {
      return this.pluginName == null || Bukkit.getPluginManager().getPlugin(this.pluginName) != null;
   }
}
