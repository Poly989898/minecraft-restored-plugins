package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ExecutionAudienceState;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicFieldKeys;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Collection;

@MythicMechanic(
   author = "evlad",
   name = "unsetAudience",
   aliases = {"clearaudience", "audienceclear"},
   description = "Clears the execution audience restriction"
)
public class UnsetAudienceMechanic extends SkillMechanic implements INoTargetSkill, ITargetedEntitySkill {
   @MythicField(
      name = "mobs",
      aliases = {"m"},
      description = "Targeter expression to resolve mobs to clear the audience on"
   )
   private final SkillTargeter mobTargeter;
   @MythicField(
      name = "ignoreSkillAudience",
      aliases = {"isa"},
      description = "If true, skip clearing the skill's execution audience and only clear on the specified mobs",
      defValue = "false"
   )
   private final boolean ignoreSkillAudience;

   public UnsetAudienceMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.target_creative = true;
      String mobsStr = mlc.getString(MythicFieldKeys.keys(this.getClass(), "mobTargeter"), null);
      this.mobTargeter = mobsStr != null && mobsStr.startsWith("@") ? this.parseSkillTargeter(mobsStr) : null;
      this.ignoreSkillAudience = mlc.getBoolean(MythicFieldKeys.keys(this.getClass(), "ignoreSkillAudience"), false);
      if (this.ignoreSkillAudience && this.mobTargeter == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "ignoreSkillAudience=true with no 'mobs' targeter leaves nothing for this mechanic to act on.");
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      return this.unsetAudience(data);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      return this.unsetAudience(data);
   }

   private SkillResult unsetAudience(SkillMetadata data) {
      if (!this.ignoreSkillAudience) {
         ExecutionAudienceState state = data.getExecutionAudienceState();
         state.clear();
         ActiveMob am = this.resolveActiveMob(data);
         if (am != null) {
            this.getPlugin().getMobManager().applyMobAudience(am, null);
         }
      }

      if (this.mobTargeter != null) {
         SkillMetadata targetData = data.deepClone();
         this.evaluateTargets(targetData, this.mobTargeter);
         Collection<AbstractEntity> entities = targetData.getEntityTargets();
         if (entities != null) {
            for (AbstractEntity entity : entities) {
               this.getPlugin().getMobManager().getActiveMob(entity.getUniqueId()).ifPresent(am -> this.getPlugin().getMobManager().applyMobAudience(am, null));
            }
         }
      }

      MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Cleared execution audience restriction");
      return SkillResult.SUCCESS;
   }
}
