package com.ticxo.modelengine.core21.mythic.mechanics.entity;

import com.ticxo.modelengine.api.entity.CullType;
import com.ticxo.modelengine.api.entity.data.IEntityData;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import java.util.Optional;
import java.util.function.Function;

@MythicMechanic(name = "cullconfig", aliases = "cull")
public class ConfigureCullMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final MythicLineConfig config;

   public ConfigureCullMechanic(MythicLineConfig mlc) {
      this.config = mlc;
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, target.getUniqueId());
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      IEntityData data = model.getBase().getData();
      Integer cullInterval = this.getOrDefault(meta, target, Integer::parseInt, data.getCullInterval(), "ci", "cullinterval");
      Boolean verticalCull = this.getOrDefault(meta, target, Boolean::parseBoolean, data.getVerticalCull(), "vc", "verticalcull");
      Double verticalCullDistance = this.getOrDefault(meta, target, Double::parseDouble, data.getVerticalCullDistance(), "vcd", "verticalculldistance");
      CullType verticalCullType = this.getOrDefault(meta, target, CullType::get, data.getVerticalCullType(), "vct", "verticalculltype");
      Boolean backCull = this.getOrDefault(meta, target, Boolean::parseBoolean, data.getBackCull(), "bkc", "backcull");
      Double backCullAngle = this.getOrDefault(meta, target, val -> {
         double degree = Double.parseDouble(val);
         return Math.cos(TMath.clamp(degree, 0.0, 360.0) * 0.5 * (float) (Math.PI / 180.0));
      }, data.getBackCullAngle(), "bkca", "backcullangle");
      Double backCullIgnoreRadius = this.getOrDefault(meta, target, Double::parseDouble, data.getBackCullIgnoreRadius(), "bkcr", "backcullignoreradius");
      CullType backCullType = this.getOrDefault(meta, target, CullType::get, data.getBackCullType(), "bkct", "backculltype");
      Boolean blockedCull = this.getOrDefault(meta, target, Boolean::parseBoolean, data.getBlockedCull(), "blc", "blockedcull");
      Double blockedCullIgnoreRadius = this.getOrDefault(
         meta, target, Double::parseDouble, data.getBlockedCullIgnoreRadius(), "blcr", "blockedcullignoreradius"
      );
      CullType blockedCullType = this.getOrDefault(meta, target, CullType::get, data.getBlockedCullType(), "blct", "blockedculltype");
      data.setCullInterval(cullInterval);
      data.setVerticalCull(verticalCull);
      data.setVerticalCullDistance(verticalCullDistance);
      data.setVerticalCullType(verticalCullType);
      data.setBackCull(backCull);
      data.setBackCullAngle(backCullAngle);
      data.setBackCullIgnoreRadius(backCullIgnoreRadius);
      data.setBackCullType(backCullType);
      data.setBlockedCull(blockedCull);
      data.setBlockedCullIgnoreRadius(blockedCullIgnoreRadius);
      data.setBlockedCullType(blockedCullType);
      Boolean animationLod = this.getOrDefault(meta, target, Boolean::parseBoolean, model.getAnimationLodHandler().getEnabled(), "alod", "animationlod");
      model.getAnimationLodHandler().setEnabled(animationLod);
      return SkillResult.SUCCESS;
   }

   public SkillResult cast(SkillMetadata meta) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, null);
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      IEntityData data = model.getBase().getData();
      Integer cullInterval = this.getOrDefault(meta, null, Integer::parseInt, data.getCullInterval(), "ci", "cullinterval");
      Boolean verticalCull = this.getOrDefault(meta, null, Boolean::parseBoolean, data.getVerticalCull(), "vc", "verticalcull");
      Double verticalCullDistance = this.getOrDefault(meta, null, Double::parseDouble, data.getVerticalCullDistance(), "vcd", "verticalculldistance");
      CullType verticalCullType = this.getOrDefault(meta, null, CullType::get, data.getVerticalCullType(), "vct", "verticalculltype");
      Boolean backCull = this.getOrDefault(meta, null, Boolean::parseBoolean, data.getBackCull(), "bkc", "backcull");
      Double backCullAngle = this.getOrDefault(meta, null, val -> {
         double degree = Double.parseDouble(val);
         return Math.cos(TMath.clamp(degree, 0.0, 360.0) * 0.5 * (float) (Math.PI / 180.0));
      }, data.getBackCullAngle(), "bkca", "backcullangle");
      Double backCullIgnoreRadius = this.getOrDefault(meta, null, Double::parseDouble, data.getBackCullIgnoreRadius(), "bkcr", "backcullignoreradius");
      CullType backCullType = this.getOrDefault(meta, null, CullType::get, data.getBackCullType(), "bkct", "backculltype");
      Boolean blockedCull = this.getOrDefault(meta, null, Boolean::parseBoolean, data.getBlockedCull(), "blc", "blockedcull");
      Double blockedCullIgnoreRadius = this.getOrDefault(meta, null, Double::parseDouble, data.getBlockedCullIgnoreRadius(), "blcr", "blockedcullignoreradius");
      CullType blockedCullType = this.getOrDefault(meta, null, CullType::get, data.getBlockedCullType(), "blct", "blockedculltype");
      data.setCullInterval(cullInterval);
      data.setVerticalCull(verticalCull);
      data.setVerticalCullDistance(verticalCullDistance);
      data.setVerticalCullType(verticalCullType);
      data.setBackCull(backCull);
      data.setBackCullAngle(backCullAngle);
      data.setBackCullIgnoreRadius(backCullIgnoreRadius);
      data.setBackCullType(backCullType);
      data.setBlockedCull(blockedCull);
      data.setBlockedCullIgnoreRadius(blockedCullIgnoreRadius);
      data.setBlockedCullType(blockedCullType);
      Boolean animationLod = this.getOrDefault(meta, null, Boolean::parseBoolean, model.getAnimationLodHandler().getEnabled(), "alod", "animationlod");
      model.getAnimationLodHandler().setEnabled(animationLod);
      return SkillResult.SUCCESS;
   }

   private <T> T getOrDefault(SkillMetadata meta, AbstractEntity target, Function<String, T> function, T def, String... alias) {
      PlaceholderString placeholder = this.config.getPlaceholderString(alias, null, new String[0]);
      if (placeholder != null && placeholder.isPresent()) {
         try {
            return function.apply(placeholder.get(meta, target));
         } catch (Exception ignored) {
            return def;
         }
      } else {
         return def;
      }
   }
}
