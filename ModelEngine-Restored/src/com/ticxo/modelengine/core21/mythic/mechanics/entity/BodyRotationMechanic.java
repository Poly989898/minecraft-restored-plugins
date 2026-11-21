package com.ticxo.modelengine.core21.mythic.mechanics.entity;

import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import java.util.Optional;

@MythicMechanic(name = "bodyrotation", aliases = "bodyclamp")
public class BodyRotationMechanic implements ITargetedEntitySkill, INoTargetSkill {
   private final MythicLineConfig config;

   public BodyRotationMechanic(MythicLineConfig mlc) {
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
      BodyRotationController controller = model.getBase().getBodyRotationController();
      boolean headUneven = this.config.getBoolean(new String[]{"hu", "head", "headuneven"}, controller.isHeadClampUneven());
      PlaceholderFloat maxHeadAngle = this.config.getPlaceholderFloat(new String[]{"mh", "mxh", "maxhead"}, controller.getMaxHeadAngle(), new String[0]);
      PlaceholderFloat minHeadAngle = this.config.getPlaceholderFloat(new String[]{"mnh", "minhead"}, controller.getMinHeadAngle(), new String[0]);
      boolean bodyUneven = this.config.getBoolean(new String[]{"bu", "body", "bodyuneven"}, controller.isBodyClampUneven());
      PlaceholderFloat maxBodyAngle = this.config.getPlaceholderFloat(new String[]{"mb", "mxb", "maxbody"}, controller.getMaxBodyAngle(), new String[0]);
      PlaceholderFloat minBodyAngle = this.config.getPlaceholderFloat(new String[]{"mnb", "minbody"}, controller.getMinBodyAngle(), new String[0]);
      boolean mode = this.config.getBoolean(new String[]{"m", "mode", "player", "playermode"}, controller.isPlayerMode());
      PlaceholderFloat stableAngle = this.config.getPlaceholderFloat(new String[]{"s", "stable"}, controller.getStableAngle(), new String[0]);
      PlaceholderInt rotationDelay = this.config.getPlaceholderInteger(new String[]{"rde", "rdelay"}, controller.getRotationDelay(), new String[0]);
      PlaceholderInt rotationDuration = this.config.getPlaceholderInteger(new String[]{"rdu", "rduration"}, controller.getRotationDuration(), new String[0]);
      controller.setHeadClampUneven(headUneven);
      controller.setMaxHeadAngle(maxHeadAngle.get(meta, target));
      controller.setMinHeadAngle(minHeadAngle.get(meta, target));
      controller.setBodyClampUneven(bodyUneven);
      controller.setMaxBodyAngle(maxBodyAngle.get(meta, target));
      controller.setMinBodyAngle(minBodyAngle.get(meta, target));
      controller.setPlayerMode(mode);
      controller.setStableAngle(stableAngle.get(meta, target));
      controller.setRotationDelay(rotationDelay.get(meta, target));
      controller.setRotationDuration(rotationDuration.get(meta, target));
      return SkillResult.SUCCESS;
   }

   public SkillResult cast(SkillMetadata meta) {
      Optional<ModeledEntity> maybeModel = MythicUtils.findSkillableModel(meta, null);
      if (maybeModel.isEmpty()) {
         return SkillResult.CONDITION_FAILED;
      }

      ModeledEntity model = maybeModel.get();
      BodyRotationController controller = model.getBase().getBodyRotationController();
      boolean headUneven = this.config.getBoolean(new String[]{"hu", "head", "headuneven"}, controller.isHeadClampUneven());
      PlaceholderFloat maxHeadAngle = this.config.getPlaceholderFloat(new String[]{"mh", "mxh", "maxhead"}, controller.getMaxHeadAngle(), new String[0]);
      PlaceholderFloat minHeadAngle = this.config.getPlaceholderFloat(new String[]{"mnh", "minhead"}, controller.getMinHeadAngle(), new String[0]);
      boolean bodyUneven = this.config.getBoolean(new String[]{"bu", "body", "bodyuneven"}, controller.isBodyClampUneven());
      PlaceholderFloat maxBodyAngle = this.config.getPlaceholderFloat(new String[]{"mb", "mxb", "maxbody"}, controller.getMaxBodyAngle(), new String[0]);
      PlaceholderFloat minBodyAngle = this.config.getPlaceholderFloat(new String[]{"mnb", "minbody"}, controller.getMinBodyAngle(), new String[0]);
      boolean mode = this.config.getBoolean(new String[]{"m", "mode", "player", "playermode"}, controller.isPlayerMode());
      PlaceholderFloat stableAngle = this.config.getPlaceholderFloat(new String[]{"s", "stable"}, controller.getStableAngle(), new String[0]);
      PlaceholderInt rotationDelay = this.config.getPlaceholderInteger(new String[]{"rde", "rdelay"}, controller.getRotationDelay(), new String[0]);
      PlaceholderInt rotationDuration = this.config.getPlaceholderInteger(new String[]{"rdu", "rduration"}, controller.getRotationDuration(), new String[0]);
      controller.setHeadClampUneven(headUneven);
      controller.setMaxHeadAngle(maxHeadAngle.get(meta));
      controller.setMinHeadAngle(minHeadAngle.get(meta));
      controller.setBodyClampUneven(bodyUneven);
      controller.setMaxBodyAngle(maxBodyAngle.get(meta));
      controller.setMinBodyAngle(minBodyAngle.get(meta));
      controller.setPlayerMode(mode);
      controller.setStableAngle(stableAngle.get(meta));
      controller.setRotationDelay(rotationDelay.get(meta));
      controller.setRotationDuration(rotationDuration.get(meta));
      return SkillResult.SUCCESS;
   }
}
