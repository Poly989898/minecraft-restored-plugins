package io.lumine.mythic.lib.skill.handler;

import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.result.FabledSkillResult;
import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import studio.magemonkey.fabled.Fabled;
import studio.magemonkey.fabled.api.skills.Skill;
import studio.magemonkey.fabled.api.skills.SkillShot;
import studio.magemonkey.fabled.api.skills.TargetSkill;
import studio.magemonkey.fabled.log.Logger;

public class FabledSkillHandler extends SkillHandler<FabledSkillResult> {
   private final Skill skill;

   public FabledSkillHandler(ConfigurationSection config) {
      super(config.getString("fabled-skill-id", config.getString("skillapi-skill-id")));
      String skillName = config.getString("fabled-skill-id", config.getString("skillapi-skill-id"));
      this.skill = Objects.requireNonNull(Fabled.getSkill(skillName), "Could not find Fabled skill with name '" + skillName + "'");
      this.registerModifiers("level");
   }

   public Skill getSkill() {
      return this.skill;
   }

   public FabledSkillResult getResult(SkillMetadata meta) {
      return new FabledSkillResult(meta, this.skill);
   }

   public void whenCast(FabledSkillResult result, SkillMetadata skillMeta) {
      if (this.skill instanceof SkillShot) {
         try {
            ((SkillShot)this.skill).cast(skillMeta.getCaster().getPlayer(), result.getLevel());
         } catch (Exception exception) {
            Logger.bug("Failed to cast skill - " + this.skill.getName() + ": Internal skill error");
            exception.printStackTrace();
         }
      } else if (this.skill instanceof TargetSkill) {
         try {
            boolean isAlly = !Fabled.getSettings().canAttack(skillMeta.getCaster().getPlayer(), result.getTarget());
            ((TargetSkill)this.skill).cast(skillMeta.getCaster().getPlayer(), result.getTarget(), result.getLevel(), isAlly);
         } catch (Exception exception) {
            Logger.bug("Failed to cast skill - " + this.skill.getName() + ": Internal skill error");
            exception.printStackTrace();
         }
      }
   }
}
