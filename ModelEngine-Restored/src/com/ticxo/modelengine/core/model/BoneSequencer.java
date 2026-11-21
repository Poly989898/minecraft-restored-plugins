package com.ticxo.modelengine.core.model;

import com.ticxo.modelengine.api.model.bone.ModelBone;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Generated;

public class BoneSequencer {
   private final Set<String> processedBones = new HashSet<>();
   private final List<String> boneOrders = new ArrayList<>();
   private volatile boolean dirty = true;

   protected void reorder(Map<String, ModelBone> roots) {
      if (this.dirty) {
         this.dirty = false;
         this.processedBones.clear();
         this.boneOrders.clear();

         int size;
         do {
            size = this.boneOrders.size();
            this.order(roots);
         } while (size != this.boneOrders.size());
      }
   }

   private void order(Map<String, ModelBone> roots) {
      for (ModelBone bone : roots.values()) {
         if (this.processedBones.contains(bone.getBoneId())) {
            this.order(bone.getChildren());
         } else if (bone.getTickDependencies().isEmpty() || this.processedBones.containsAll(bone.getTickDependencies())) {
            this.boneOrders.add(bone.getBoneId());
            this.processedBones.add(bone.getBoneId());
            this.order(bone.getChildren());
         }
      }
   }

   @Generated
   public List<String> getBoneOrders() {
      return this.boneOrders;
   }

   @Generated
   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }
}
