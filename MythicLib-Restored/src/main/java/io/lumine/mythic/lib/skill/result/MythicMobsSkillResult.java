package io.lumine.mythic.lib.skill.result;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.handler.MythicMobsSkillHandler;
import io.lumine.mythic.lib.util.RayTrace;
import java.util.HashSet;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MythicMobsSkillResult implements SkillResult {
   private final SkillMetadataImpl mmSkillMeta;
   private final boolean success;
   public static final String MMO_SKILLMETADATA_TAG = "MMOSkillMetadata";

   public MythicMobsSkillResult(@NotNull SkillMetadata skillMeta, @NotNull MythicMobsSkillHandler behaviour) {
      AbstractEntity trigger = BukkitAdapter.adapt(skillMeta.getCaster().getPlayer());
      SkillCaster caster = new GenericCaster(trigger);
      HashSet<AbstractEntity> targetEntities = new HashSet<>();
      HashSet<AbstractLocation> targetLocations = new HashSet<>();
      if (skillMeta.hasTargetEntity()) {
         targetEntities.add(BukkitAdapter.adapt(skillMeta.getTargetEntityOrNull()));
      } else {
         Player player = skillMeta.getCaster().getPlayer();
         RayTrace res = new RayTrace(player, 32.0, entity -> !entity.equals(player) && entity instanceof LivingEntity);
         if (res.hasHit()) {
            targetEntities.add(BukkitAdapter.adapt(res.getHit()));
         }
      }

      if (skillMeta.hasTargetLocation()) {
         targetLocations.add(BukkitAdapter.adapt(skillMeta.getTargetLocationOrNull()));
      }

      this.mmSkillMeta = new SkillMetadataImpl(
         SkillTriggers.API, caster, trigger, BukkitAdapter.adapt(skillMeta.getCaster().getPlayer().getEyeLocation()), targetEntities, targetLocations, 1.0F
      );
      this.mmSkillMeta.getVariables().putObject("MMOSkillMetadata", skillMeta);
      this.success = behaviour.getSkill().isUsable(this.mmSkillMeta);
   }

   @Override
   public boolean isSuccessful() {
      return this.success;
   }

   public SkillMetadataImpl getMythicMobsSkillMetadata() {
      return this.mmSkillMeta;
   }

   @Deprecated
   public SkillMetadataImpl getMythicMobskillMetadata() {
      return this.getMythicMobsSkillMetadata();
   }
}
