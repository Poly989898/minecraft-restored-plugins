package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.ICasterCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.Bukkit;

@MythicCondition(
   author = "Joshinn",
   name = "plugin",
   aliases = {"pluginexists", "hasplugin"},
   description = "Whether or not a specific plugin exists."
)
public class PluginExistsCondition extends SkillCondition implements ICasterCondition {
   @MythicField(
      name = "plugin",
      aliases = {"p", "pl"},
      description = "The plugin to check for."
   )
   private String plugin;

   public PluginExistsCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.plugin = mlc.getString(new String[]{"plugin", "p", "pl"}, "MythicMobs");
   }

   @Override
   public boolean check(SkillCaster caster) {
      return Bukkit.getPluginManager().isPluginEnabled(this.plugin);
   }
}
