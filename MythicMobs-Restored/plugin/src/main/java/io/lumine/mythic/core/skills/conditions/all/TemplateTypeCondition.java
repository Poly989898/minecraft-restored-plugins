package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@MythicCondition(
   author = "Seyarada",
   name = "templateType",
   aliases = {"template", "instanceOf"},
   description = "Checks if the target mob extends the specified Template"
)
public class TemplateTypeCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "templates",
      aliases = {"template", "t"},
      description = "A list of MythicMob templates"
   )
   private Set<String> types = new HashSet<>();

   public TemplateTypeCondition(String line, MythicLineConfig mlc) {
      super(line);
      String types = mlc.getString(new String[]{"templates", "template", "t"}, this.conditionVar);
      String[] split = types.split(",");
      this.types.addAll(Arrays.asList(split));
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (MythicBukkit.inst().getMobManager().isActiveMob(entity)) {
         ActiveMob am = getPlugin().getMobManager().getMythicMobInstance(entity);
         if (this.types.contains(am.getType().getInternalName())) {
            return true;
         }

         if (am.getType().getConfig().isSet("TemplateParents")) {
            List<String> mobTemplates = am.getType().getConfig().getStringList("TemplateParents");
            return mobTemplates.stream().anyMatch(this.types::contains);
         }
      }

      return false;
   }
}
