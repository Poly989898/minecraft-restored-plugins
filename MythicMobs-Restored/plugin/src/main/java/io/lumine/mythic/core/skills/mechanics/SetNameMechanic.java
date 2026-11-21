package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setname",
   description = "Sets the casting mob's name"
)
public class SetNameMechanic extends SkillMechanic implements INoTargetSkill, ITargetedEntitySkill {
   @MythicField(
      name = "name",
      aliases = {"n"},
      description = "The display name template to assign",
      defValue = "<caster.type.name>"
   )
   private PlaceholderString name;

   public SetNameMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      String name = mlc.getString(new String[]{"name", "n"}, "<caster.type.name>");
      if (name.contains("<caster.name>")) {
         name = name.replace("<caster.name>", "<caster.type.name>");
      }

      this.name = PlaceholderString.of(name);
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (data.getCaster() instanceof ActiveMob am) {
         String newName = this.name.get(data);
         am.setDisplayName(newName);
         if (CompatibilityManager.LibsDisguises != null && am.getType() != null && am.getType().getDisguise() != null) {
            CompatibilityManager.LibsDisguises.setDisguiseName(am.getEntity(), newName);
         }
      }

      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (data.getCaster() instanceof ActiveMob am) {
         String newName = this.name.get(data, target);
         am.setDisplayName(newName);
         if (CompatibilityManager.LibsDisguises != null && am.getType() != null && am.getType().getDisguise() != null) {
            CompatibilityManager.LibsDisguises.setDisguiseName(am.getEntity(), newName);
         }
      }

      return SkillResult.SUCCESS;
   }
}
