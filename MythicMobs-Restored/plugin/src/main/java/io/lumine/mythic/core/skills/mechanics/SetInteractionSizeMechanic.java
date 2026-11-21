package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.Interaction;

@MythicMechanic(
   name = "setInteractionSize",
   aliases = {"interactionSize"},
   description = "Sets the interaction hitbox size"
)
public class SetInteractionSizeMechanic extends SkillMechanic implements INoTargetSkill, ITargetedEntitySkill {
   @MythicField(
      name = "height",
      aliases = {"h"},
      description = "Sets the interaction height"
   )
   private final PlaceholderFloat height;
   @MythicField(
      name = "width",
      aliases = {"w"},
      description = "Sets the interaction width"
   )
   private final PlaceholderFloat width;

   public SetInteractionSizeMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.height = mlc.getPlaceholderFloat(new String[]{"height", "h"}, 1.0F);
      this.width = mlc.getPlaceholderFloat(new String[]{"width", "w"}, 1.0F);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.getBukkitEntity() instanceof Interaction interaction) {
         this.setSize(interaction, data);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (data.getCaster().getEntity().getBukkitEntity() instanceof Interaction interaction) {
         this.setSize(interaction, data);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }

   protected void setSize(Interaction interaction, SkillMetadata data) {
      float h = this.height.get(data, BukkitAdapter.adapt(interaction));
      float w = this.width.get(data, BukkitAdapter.adapt(interaction));
      Schedulers.runOnRegion(interaction, () -> {
         interaction.setInteractionHeight(h);
         interaction.setInteractionWidth(w);
      });
   }
}
