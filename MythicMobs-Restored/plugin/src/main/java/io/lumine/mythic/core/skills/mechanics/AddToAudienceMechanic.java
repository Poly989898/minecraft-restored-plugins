package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ExecutionAudienceState;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicFieldKeys;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@MythicMechanic(
   author = "evlad",
   name = "addToAudience",
   aliases = {"addaudience", "audienceadd"},
   description = "Adds players to the execution audience"
)
public class AddToAudienceMechanic extends SkillMechanic implements INoTargetSkill, ITargetedEntitySkill {
   @MythicField(
      name = "audience",
      aliases = {"a"},
      description = "The audience resolver to use for selecting players to add"
   )
   private final SkillAudience audienceResolver;
   @MythicField(
      name = "mobs",
      aliases = {"m"},
      description = "Targeter expression to resolve mobs to apply the audience change to"
   )
   private final SkillTargeter mobTargeter;
   @MythicField(
      name = "ignoreSkillAudience",
      aliases = {"isa"},
      description = "If true, skip modifying the skill's execution audience and only apply to the specified mobs",
      defValue = "false"
   )
   private final boolean ignoreSkillAudience;

   public AddToAudienceMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.target_creative = true;
      this.audienceResolver = mlc.getAudience(MythicFieldKeys.keys(this.getClass(), "audienceResolver"), null);
      String mobsStr = mlc.getString(MythicFieldKeys.keys(this.getClass(), "mobTargeter"), null);
      this.mobTargeter = mobsStr != null && mobsStr.startsWith("@") ? this.parseSkillTargeter(mobsStr) : null;
      this.ignoreSkillAudience = mlc.getBoolean(MythicFieldKeys.keys(this.getClass(), "ignoreSkillAudience"), false);
      if (this.ignoreSkillAudience && this.mobTargeter == null) {
         MythicLogger.errorMechanicConfig(this, mlc, "ignoreSkillAudience=true with no 'mobs' targeter leaves nothing for this mechanic to act on.");
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (this.audienceResolver != null) {
         Collection<AbstractPlayer> players = this.audienceResolver.get(data, data.getCaster().getEntity());
         this.addToAudience(data, players);
      } else {
         AbstractEntity caster = data.getCaster().getEntity();
         if (caster.isPlayer()) {
            this.addToAudience(data, Set.of(caster.asPlayer()));
         }
      }

      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (this.audienceResolver != null) {
         Collection<AbstractPlayer> players = this.audienceResolver.get(data, target);
         this.addToAudience(data, players);
      } else if (target.isPlayer()) {
         this.addToAudience(data, Set.of(target.asPlayer()));
      }

      return SkillResult.SUCCESS;
   }

   private void addToAudience(SkillMetadata data, Collection<AbstractPlayer> players) {
      if (players != null && !players.isEmpty()) {
         ArrayList<UUID> uuids = new ArrayList<>(players.size());

         for (AbstractPlayer player : players) {
            uuids.add(player.getUniqueId());
         }

         if (!this.ignoreSkillAudience) {
            ExecutionAudienceState state = data.getExecutionAudienceState();
            state.addAll(uuids);
            ActiveMob am = this.resolveActiveMob(data);
            if (am != null) {
               this.getPlugin().getMobManager().applyMobAudience(am, state.get().orElse(null));
            }
         }

         if (this.mobTargeter != null) {
            SkillMetadata targetData = data.deepClone();
            this.evaluateTargets(targetData, this.mobTargeter);
            Collection<AbstractEntity> entities = targetData.getEntityTargets();
            if (entities != null) {
               for (AbstractEntity entity : entities) {
                  this.getPlugin().getMobManager().getActiveMob(entity.getUniqueId()).ifPresent(am -> {
                     Set<UUID> mobAudience = am.getExecutionAudience();
                     HashSet hashSet;
                     if (mobAudience == null) {
                        hashSet = new HashSet();
                     } else {
                        hashSet = new HashSet<>(mobAudience);
                     }

                     hashSet.addAll(uuids);
                     this.getPlugin().getMobManager().applyMobAudience(am, hashSet);
                  });
               }
            }
         }

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Added {0} players to execution audience", uuids.size());
      }
   }
}
