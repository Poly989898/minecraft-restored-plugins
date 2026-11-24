package io.lumine.mythic.lib.skill.result.def;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.damage.AttackMetadata;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.result.SkillResult;
import java.util.Objects;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttackSkillResult implements SkillResult {
   @Nullable
   private final LivingEntity target;
   @Nullable
   private final AttackMetadata attackMeta;

   public AttackSkillResult(SkillMetadata skillMeta) {
      this.target = skillMeta.hasTargetEntity()
            && MythicLib.plugin.getEntities().canInteract(skillMeta.getCaster().getPlayer(), skillMeta.getTargetEntity(), InteractionType.OFFENSE_SKILL)
         ? (LivingEntity)skillMeta.getTargetEntity()
         : null;
      this.attackMeta = this.target == null ? null : MythicLib.plugin.getDamage().getRegisteredAttackMetadata(this.target);
   }

   @NotNull
   public AttackMetadata getAttack() {
      return Objects.requireNonNull(this.attackMeta, "Skill is unsuccessful");
   }

   @NotNull
   public LivingEntity getTarget() {
      return Objects.requireNonNull(this.target, "Skill is unsuccessful");
   }

   @Override
   public boolean isSuccessful() {
      return this.target != null;
   }
}
