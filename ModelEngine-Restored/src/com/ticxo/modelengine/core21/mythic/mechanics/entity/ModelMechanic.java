package com.ticxo.modelengine.core21.mythic.mechanics.entity;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.data.BukkitEntityData;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.operation.ModelOperation;
import com.ticxo.modelengine.api.model.operation.OperationResult;
import com.ticxo.modelengine.api.model.operation.OperationType;
import com.ticxo.modelengine.api.nms.entity.wrapper.TrackedEntity;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.Player;

@MythicMechanic(name = "model", aliases = {})
public class ModelMechanic implements ITargetedEntitySkill {
   private final PlaceholderString modelId;
   private final PlaceholderBoolean hitbox;
   private final PlaceholderBoolean remove;
   private final PlaceholderBoolean killOwner;
   private final PlaceholderString invisible;
   private final PlaceholderBoolean doDamageTint;
   private final PlaceholderString nametag;
   private final PlaceholderBoolean canDrive;
   private final PlaceholderBoolean canRide;
   private final PlaceholderBoolean lockPitch;
   private final PlaceholderBoolean lockYaw;
   private final PlaceholderDouble stepHeight;
   private final PlaceholderInt viewRadius;
   private final PlaceholderDouble viewRange;
   private final PlaceholderDouble scale;
   private final PlaceholderDouble hitboxScale;
   private final PlaceholderBoolean useStateMachine;
   private final PlaceholderBoolean invisUpdate;
   private final PlaceholderBoolean initRenderer;
   private final PlaceholderBoolean showHitbox;
   private final PlaceholderBoolean showShadow;
   private final PlaceholderBoolean syncBodyYaw;
   private final PlaceholderBoolean shouldSave;
   private final PlaceholderBoolean viewSelf;
   private final PlaceholderBoolean shouldTrack;
   private final PlaceholderBoolean pivotOverride;
   private final PlaceholderBoolean cameraPivot;
   private final PlaceholderBoolean renderFire;
   private final PlaceholderBoolean forceFire;
   private final PlaceholderString rootBone;

   public ModelMechanic(MythicLineConfig mlc) {
      this.modelId = mlc.getPlaceholderString(new String[]{"m", "mid", "model", "modelid"}, null, new String[0]);
      this.hitbox = mlc.getPlaceholderBoolean(new String[]{"h", "hitbox"}, true);
      this.remove = mlc.getPlaceholderBoolean(new String[]{"r", "remove"}, false);
      this.killOwner = mlc.getPlaceholderBoolean(new String[]{"ko", "killowner"}, false);
      this.invisible = mlc.getPlaceholderString(new String[]{"i", "invis", "invisible"}, null, new String[0]);
      this.doDamageTint = mlc.getPlaceholderBoolean(new String[]{"d", "tint", "damagetint"}, true);
      this.nametag = mlc.getPlaceholderString(new String[]{"n", "name", "nametag"}, null, new String[0]);
      this.canDrive = mlc.getPlaceholderBoolean(new String[]{"drive"}, false);
      this.canRide = mlc.getPlaceholderBoolean(new String[]{"ride"}, false);
      this.lockPitch = mlc.getPlaceholderBoolean(new String[]{"lp", "lpitch", "lockpitch"}, false);
      this.lockYaw = mlc.getPlaceholderBoolean(new String[]{"ly", "lyaw", "lockyaw"}, false);
      this.stepHeight = mlc.getPlaceholderDouble(new String[]{"s", "step"}, (String)null, new String[0]);
      this.viewRadius = mlc.getPlaceholderInteger(new String[]{"rad", "radius"}, 0, new String[0]);
      this.viewRange = mlc.getPlaceholderDouble(new String[]{"vr", "viewrange"}, "4096", new String[0]);
      this.scale = mlc.getPlaceholderDouble(new String[]{"scale"}, "1", new String[0]);
      this.hitboxScale = mlc.getPlaceholderDouble(new String[]{"hitboxscale"}, this.scale, new String[0]);
      this.useStateMachine = mlc.getPlaceholderBoolean(new String[]{"usm", "state", "statemachine", "usestatemachine"}, false);
      this.invisUpdate = mlc.getPlaceholderBoolean(new String[]{"iu", "invisupdate"}, false);
      this.initRenderer = mlc.getPlaceholderBoolean(new String[]{"init", "initrender"}, true);
      this.showHitbox = mlc.getPlaceholderBoolean(new String[]{"showhitbox"}, true);
      this.showShadow = mlc.getPlaceholderBoolean(new String[]{"showshadow"}, true);
      this.syncBodyYaw = mlc.getPlaceholderBoolean(new String[]{"syncbody"}, true);
      this.shouldSave = mlc.getPlaceholderBoolean(new String[]{"save"}, false);
      this.viewSelf = mlc.getPlaceholderBoolean(new String[]{"vs", "see", "seeself"}, true);
      this.shouldTrack = mlc.getPlaceholderBoolean(new String[]{"track"}, null);
      this.pivotOverride = mlc.getPlaceholderBoolean(new String[]{"pv", "pivot"}, false);
      this.cameraPivot = mlc.getPlaceholderBoolean(new String[]{"campivot", "camerapivot"}, false);
      this.renderFire = mlc.getPlaceholderBoolean(new String[]{"rf", "renderfire"}, null);
      this.forceFire = mlc.getPlaceholderBoolean(new String[]{"ff", "forcefire"}, null);
      this.rootBone = mlc.getPlaceholderString(new String[]{"rt", "root", "rootbone"}, null, new String[0]);
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      return ServerInfo.scheduleOnRegion(BukkitAdapter.adapt(target), () -> this.castAtEntitySync(meta, target))
         ? SkillResult.SUCCESS
         : this.castAtEntitySync(meta, target);
   }

   private SkillResult castAtEntitySync(SkillMetadata meta, AbstractEntity target) {
      return this.remove.get(meta, target) ? this.removeModel(meta, target) : this.addModel(meta, target);
   }

   private SkillResult removeModel(SkillMetadata meta, AbstractEntity target) {
      ModeledEntity model = ModelEngineAPI.getModeledEntity(target.getUniqueId());
      if (model == null) {
         return SkillResult.CONDITION_FAILED;
      }

      boolean disguise = target.isPlayer();
      String id = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
      if (id == null) {
         for (ActiveModel activeModel : model.getModels().values()) {
            model.removeModel(activeModel.getBlueprint().getName()).ifPresent(ActiveModel::destroy);
         }
      } else {
         model.removeModel(id).ifPresent(ActiveModel::destroy);
      }

      if (this.killOwner.get(meta, target)) {
         target.remove();
         return SkillResult.SUCCESS;
      }

      if (model.getModels().isEmpty()) {
         model.setBaseEntityVisible(true);
         ModelEngineAPI.removeModeledEntity(target.getUniqueId());
         ModelEngineAPI.getEntityHandler().setForcedInvisible(BukkitAdapter.adapt(target), false);
         if (disguise) {
            Player player = BukkitAdapter.adapt(target.asPlayer());
            if (model.getBase().getData() instanceof BukkitEntityData data) {
               data.getTracked().setBaseRange(data.getTracked().getInitialBaseRange());
               data.getTracked().setPlayerPredicate(TrackedEntity.DEFAULT_PREDICATE);
            }
         }
      } else {
         String strInvis = MythicUtils.getOrNullLowercase(this.invisible, meta, target);
         if (strInvis != null) {
            model.setBaseEntityVisible(Boolean.parseBoolean(strInvis));
         }
      }

      return SkillResult.SUCCESS;
   }

   private SkillResult addModel(SkillMetadata meta, AbstractEntity target) {
      String strInvis = MythicUtils.getOrNullLowercase(this.invisible, meta, target);
      OperationResult result = ModelOperation.builder()
         .bukkitEntity(BukkitAdapter.adapt(target))
         .modelId(MythicUtils.getOrNullLowercase(this.modelId, meta, target))
         .overrideHitbox(this.hitbox.get(meta, target))
         .stepHeight(this.stepHeight == null ? null : this.stepHeight.get(meta, target))
         .viewRadius(this.viewRadius.get(meta, target))
         .viewRange((float)this.viewRange.get(meta, target))
         .scale(this.scale.get(meta, target))
         .hitboxScale(this.hitboxScale.get(meta, target))
         .shouldSave(this.shouldSave.get(meta, target))
         .baseInvisible(strInvis == null || Boolean.parseBoolean(strInvis))
         .doDamageTint(this.doDamageTint.get(meta, target))
         .lockPitch(this.lockPitch.get(meta, target))
         .lockYaw(this.lockYaw.get(meta, target))
         .invisibleUpdate(this.invisUpdate.get(meta, target))
         .initializeRenderer(this.initRenderer.get(meta, target))
         .syncBodyYaw(this.syncBodyYaw.get(meta, target))
         .useStateMachine(this.useStateMachine.get(meta, target))
         .useBaseAsPivot(this.pivotOverride.get(meta, target))
         .cameraPivot(this.cameraPivot.get(meta, target))
         .showHitbox(this.showHitbox.get(meta, target))
         .showShadow(this.showShadow.get(meta, target))
         .drivable(this.canDrive.get(meta, target))
         .rideable(this.canRide.get(meta, target))
         .nametagBone(MythicUtils.getOrNullLowercase(this.nametag, meta, target))
         .rootBone(MythicUtils.getOrNullLowercase(this.rootBone, meta, target))
         .renderFire(this.renderFire == null ? null : this.renderFire.get(meta, target))
         .forceFire(this.forceFire == null ? null : this.forceFire.get(meta, target))
         .viewSelf(this.viewSelf.get(meta, target))
         .playerPredicate(this.shouldTrack == null ? null : (this.shouldTrack.get(meta, target) ? TrackedEntity.DEFAULT_PREDICATE : player -> false))
         .operate(OperationType.ADD_MODEL);
      return result.isSuccessful() ? SkillResult.SUCCESS : SkillResult.ERROR;
   }
}
