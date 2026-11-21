package com.ticxo.modelengine.core21.mythic.conditions;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.core21.mythic.utils.MythicCondition;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;

@MythicCondition(name = "hitbox")
public class HitboxCondition implements ISkillMetaCondition {
   private PlaceholderString hitbox;

   public HitboxCondition(ModelEngineAPI plugin, MythicConditionLoadEvent loader) {
      MythicLineConfig config = loader.getConfig();
      this.hitbox = config.getPlaceholderString(new String[]{"hitbox", "hb", "h", "ob"}, loader.getArgument(), new String[0]);
   }

   public boolean check(SkillMetadata skillMetadata) {
      if (!skillMetadata.getVariables().has("hitbox")) {
         return false;
      }

      String hitbox = skillMetadata.getVariables().getString("hitbox");
      return hitbox.equalsIgnoreCase(this.hitbox.get(skillMetadata));
   }
}
