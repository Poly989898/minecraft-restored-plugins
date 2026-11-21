package com.ticxo.modelengine.api.vfx;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.vfx.render.VFXRenderer;
import java.util.Collection;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Vector3f;

public interface VFX {
   BaseEntity<?> getBase();

   VFXRenderer getRenderer();

   boolean tick();

   void destroy();

   boolean isInitialized();

   boolean isDestroyed();

   void markRemoved();

   void queuePostInitTask(Runnable runnable);

   boolean isBaseEntityVisible();

   void setBaseEntityVisible(boolean baseEntityVisible);

   void setModelScale(int scale);

   float getYaw();

   void setYaw(float yaw);

   float getPitch();

   void setPitch(float pitch);

   Vector getOrigin();

   void setOrigin(Vector origin);

   Vector3f getPosition();

   void setPosition(Vector3f position);

   Vector3f getRotation();

   void setRotation(Vector3f rotation);

   Vector3f getScale();

   void setScale(Vector3f scale);

   ItemStack getModel();

   void setModel(ItemStack stack);

   DataTracker<ItemStack> getModelTracker();

   Color getColor();

   void setColor(Color color);

   boolean isEnchanted();

   void setEnchanted(boolean enchanted);

   int getBlockLight();

   void setBlockLight(int blockLight);

   int getSkyLight();

   void setSkyLight(int skyLight);

   default int getBrightness() {
      int blockLight = this.getBlockLight();
      int skyLight = this.getSkyLight();
      if (blockLight < 0 && skyLight < 0) {
         return -1;
      }

      blockLight = Math.max(blockLight, 0);
      skyLight = Math.max(skyLight, 0);
      return blockLight << 4 | skyLight << 20;
   }

   boolean isVisible();

   void setVisible(boolean visible);

   default void useModel(String modelId, String boneId) {
      ModelBlueprint blueprint = ModelEngineAPI.getBlueprint(modelId);
      if (blueprint != null) {
         BlueprintBone bone = blueprint.getFlatMap().get(boneId);
         if (bone != null && bone.isRenderer()) {
            Collection<ItemStack> stacks = bone.getModelData().createItemStack();
            if (!stacks.isEmpty()) {
               this.setModel(stacks.iterator().next());
            }
         }
      }
   }

   default void registerSelf() {
      ModelEngineAPI.getAPI().getVFXUpdater().registerVFX(this.getBase(), this);
      this.getBase().registerData();
   }
}
