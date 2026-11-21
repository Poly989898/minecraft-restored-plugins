package com.ticxo.modelengine.api.mount;

import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.ServerInfo;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.manager.MountManager;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.MountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import it.unimi.dsi.fastutil.Pair;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class MountPairManager {
   private final Map<UUID, Pair<ActiveModel, MountController>> mountedPair = Maps.newConcurrentMap();

   public void updatePassengerPosition() {
      for (Pair<ActiveModel, MountController> pair : this.mountedPair.values()) {
         ActiveModel model = pair.left();
         MountController controller = pair.right();
         if (model.getModeledEntity() != null) {
            BaseEntity<?> base = model.getModeledEntity().getBase();
            MoveController moveController = base.getMoveController();
            Runnable updateTask = () -> {
               boolean engineTick;
               if (moveController.ticksMountController()) {
                  engineTick = base.getOriginal() instanceof LivingEntity living && !living.hasAI();
               } else {
                  engineTick = controller.shouldEngineTickMovement();
               }

               if (engineTick) {
                  if (controller.getInput() == null) {
                     controller.setInput(new MountController.MountInput());
                  }

                  Mount mount = controller.getMount();
                  if (mount != null && mount.isDriver()) {
                     controller.updateDriverMovement(moveController, model);
                  } else {
                     controller.updatePassengerMovement(moveController, model);
                  }
               }

               controller.updateRiderPosition(moveController);
            };
            if (ServerInfo.IS_FOLIA) {
               Entity entity = (Entity)base.getOriginal();
               entity.getScheduler().execute(ModelEngineAPI.getAPI(), updateTask, null, 1L);
            } else {
               try {
                  updateTask.run();
               } catch (Throwable t) {
                  t.printStackTrace();
               }
            }
         }
      }
   }

   public void registerMountedPair(Entity entity, ActiveModel model, MountController controller) {
      this.mountedPair.put(entity.getUniqueId(), Pair.of(model, controller));
   }

   public void unregisterMountedPair(UUID uuid) {
      this.mountedPair.remove(uuid);
   }

   @Nullable
   public Pair<ActiveModel, MountController> get(UUID uuid) {
      return this.mountedPair.get(ModelEngineAPI.getAPI().getDisguiseRelayOrDefault(uuid));
   }

   public ActiveModel getMountedPair(UUID uuid) {
      Pair<ActiveModel, MountController> pair = this.get(uuid);
      return pair == null ? null : pair.left();
   }

   public MountController getController(UUID uuid) {
      Pair<ActiveModel, MountController> pair = this.get(uuid);
      return pair == null ? null : pair.right();
   }

   public void tryDismount(Entity entity) {
      ActiveModel model = this.getMountedPair(entity.getUniqueId());
      if (model != null) {
         model.getMountManager().ifPresent(mountManager -> ((MountManager)mountManager).dismountRider(entity));
      }
   }
}
