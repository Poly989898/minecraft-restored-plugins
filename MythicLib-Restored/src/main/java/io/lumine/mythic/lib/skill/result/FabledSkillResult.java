package io.lumine.mythic.lib.skill.result;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.SkillMetadata;
import java.util.Objects;
import javax.annotation.Nullable;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.player.PlayerData;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.api.skills.SkillShot;
import studio.magemonkey.fabled.api.skills.TargetSkill;
import studio.magemonkey.fabled.api.target.TargetHelper;

public class FabledSkillResult implements SkillResult {
   private final PlayerData skillPlayerData;
   private final Skill skill;
   private final int level;
   private final boolean success;
   @Nullable
   private LivingEntity target;

   public FabledSkillResult(@NotNull SkillMetadata skillMeta, @NotNull Skill skill) {
      this.skillPlayerData = Fabled.getData(skillMeta.getCaster().getPlayer());
      this.skill = skill;
      this.level = (int)skillMeta.getParameter("level");
      this.success = this.checkUsage(skillMeta);
   }

   public Skill getSkill() {
      return this.skill;
   }

   public int getLevel() {
      return this.level;
   }

   @NotNull
   public LivingEntity getTarget() {
      return Objects.requireNonNull(this.target, "Skill has no target");
   }

   @Override
   public boolean isSuccessful() {
      return this.success;
   }

   private boolean checkUsage(SkillMetadata skillMeta) {
      if (UtilityMethods.isInvalidated(skillMeta.getCaster())) {
         return false;
      }

      if (this.skill instanceof SkillShot) {
         return true;
      }

      if (this.skill instanceof TargetSkill) {
         this.target = skillMeta.hasTargetEntity() && skillMeta.getTargetEntityOrNull() instanceof LivingEntity
            ? (LivingEntity)skillMeta.getTargetEntity()
            : TargetHelper.getLivingTarget(this.skillPlayerData.getPlayer(), this.skill.getRange(this.level));
         if (this.target == null) {
            return false;
         }
      }

      return false;
   }
}
