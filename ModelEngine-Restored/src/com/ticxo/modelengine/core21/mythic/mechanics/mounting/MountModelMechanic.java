package com.ticxo.modelengine.core21.mythic.mechanics.mounting;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.model.bone.behavior.GlobalBehaviorData;
import com.ticxo.modelengine.api.model.bone.manager.BehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.MountData;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.mount.controller.MountControllerSupplier;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.utils.MythicMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.packs.Pack;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillHolder;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.skills.SkillExecutor;
import java.io.File;
import java.util.List;
import java.util.Optional;

@MythicMechanic(name = "mountmodel", aliases = {})
public class MountModelMechanic implements ITargetedEntitySkill, SkillHolder {
   private final boolean isDriver;
   private final boolean force;
   private final boolean autoDismount;
   private final boolean canDamageMount;
   private final boolean canInteractMount;
   private final PlaceholderString mode;
   private final PlaceholderString modelId;
   private Skill controllerSkill;
   private PlaceholderString pbone;

   public MountModelMechanic(MythicMechanicLoadEvent event) {
      File file = event.getContainer().getFile();
      SkillExecutor manager = event.getContainer().getManager();
      MythicLineConfig mlc = event.getConfig();
      this.isDriver = mlc.getBoolean(new String[]{"d", "drive", "driver"}, true);
      this.force = mlc.getBoolean(new String[]{"f", "force"}, false);
      this.autoDismount = mlc.getBoolean(new String[]{"ad", "autodismount"}, false);
      this.canDamageMount = mlc.getBoolean(new String[]{"dmg", "damagemount"}, false);
      this.canInteractMount = mlc.getBoolean(new String[]{"int", "interactmount"}, false);
      this.mode = mlc.getPlaceholderString(new String[]{"m", "mode"}, "walking", new String[0]);
      this.modelId = mlc.getPlaceholderString(new String[]{"mid", "model", "modelid"}, null, new String[0]);
      if (!this.isDriver) {
         this.pbone = mlc.getPlaceholderString(new String[]{"p", "pbone", "seat"}, null, new String[0]);
      }

      manager.queueSecondPass(() -> manager.getSkill(file, this, this.mode.serialize()).ifPresent(skill -> {
         this.controllerSkill = skill;
         if (this.controllerSkill.isInlineSkill()) {
            this.controllerSkill.addParent(this);
         }
      }));
   }

   public ThreadSafetyLevel getThreadSafetyLevel() {
      return ThreadSafetyLevel.SYNC_ONLY;
   }

   public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity target) {
      if (ServerInfo.scheduleOnRegion(target.getBukkitEntity(), () -> this.castAtEntity(meta, target))) {
         return SkillResult.SUCCESS;
      }

      AbstractEntity caster = meta.getCaster().getEntity();
      ModeledEntity model = ModelEngineAPI.getModeledEntity(caster.getUniqueId());
      if (model == null) {
         return SkillResult.CONDITION_FAILED;
      }

      String controllerId = MythicUtils.getOrNullLowercase(this.mode, meta, target);
      MountControllerSupplier controllerType = this.controllerSkill != null
         ? MythicUtils.createControllerSupplier(this.controllerSkill, meta)
         : ModelEngineAPI.getMountControllerTypeRegistry().get(controllerId);
      if (this.isDriver) {
         GlobalBehaviorData mountData = model.getMountData();
         BehaviorManager mountManager = ((MountData)mountData).getMainMountManager();
         if (mountManager == null) {
            String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
            Optional<ActiveModel> maybeModel = model.getModel(modelId);
            if (maybeModel.isPresent()) {
               Optional maybeManager = maybeModel.get().getMountManager();
               if (maybeManager.isPresent()) {
                  mountManager = (BehaviorManager)maybeManager.get();
               }
            }
         }

         if (mountManager == null) {
            return SkillResult.CONDITION_FAILED;
         }

         if (!((MountManager)mountManager).canDrive()) {
            return SkillResult.CONDITION_FAILED;
         }

         if (!this.force && ((MountManager)mountManager).isControlled()) {
            return SkillResult.CONDITION_FAILED;
         }

         if (this.tryDismountOld(target)) {
            ((MountManager)mountManager).dismountDriver();
            ((MountManager)mountManager).mountDriver(target.getBukkitEntity(), controllerType, mountController -> {
               mountController.setCanDamageMount(this.canDamageMount);
               mountController.setCanInteractMount(this.canInteractMount);
            });
         }
      } else {
         String modelId = MythicUtils.getOrNullLowercase(this.modelId, meta, target);
         String pbone = MythicUtils.getOrNullLowercase(this.pbone, meta, target);
         if (modelId == null || pbone == null) {
            return SkillResult.INVALID_CONFIG;
         }

         List<String> seats = List.of(pbone.split(","));
         model.getModel(modelId).ifPresent(activeModel -> activeModel.getMountManager().ifPresent(mountManager -> {
            if (((MountManager)mountManager).canRide()) {
               if (this.tryDismountOld(target)) {
                  if (this.force) {
                     ((MountManager)mountManager).mountLeastOccupied(target.getBukkitEntity(), seats, controllerType, mountController -> {
                        mountController.setCanDamageMount(this.canDamageMount);
                        mountController.setCanInteractMount(this.canInteractMount);
                     });
                  } else {
                     ((MountManager)mountManager).mountAvailable(target.getBukkitEntity(), seats, controllerType, mountController -> {
                        mountController.setCanDamageMount(this.canDamageMount);
                        mountController.setCanInteractMount(this.canInteractMount);
                     });
                  }
               }
            }
         }));
      }

      return SkillResult.SUCCESS;
   }

   private boolean tryDismountOld(AbstractEntity target) {
      ActiveModel model = ModelEngineAPI.getMountPairManager().getMountedPair(target.getUniqueId());
      if (model == null) {
         return true;
      }

      if (this.autoDismount) {
         Optional maybeManager = model.getMountManager();
         if (maybeManager.isEmpty()) {
            return false;
         }

         BehaviorManager mountManager = (BehaviorManager)maybeManager.get();
         ((MountManager)mountManager).dismountRider(target.getBukkitEntity());
         return true;
      } else {
         return false;
      }
   }

   public boolean getTargetsCreatives() {
      return true;
   }

   public String getInternalName() {
      return "Unknown/mountModel";
   }

   public Pack getPack() {
      return null;
   }
}
