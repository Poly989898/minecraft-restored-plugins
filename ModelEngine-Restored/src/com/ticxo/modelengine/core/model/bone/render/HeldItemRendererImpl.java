package com.ticxo.modelengine.core.model.bone.render;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.BoneBehaviorTypes;
import com.ticxo.modelengine.api.model.bone.ModelBone;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.render.BehaviorRendererParser;
import com.ticxo.modelengine.api.model.bone.render.renderer.HeldItemRenderer;
import com.ticxo.modelengine.api.model.bone.type.HeldItem;
import com.ticxo.modelengine.api.nms.RenderParsers;
import com.ticxo.modelengine.api.utils.data.PositionCodec;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.UpdateDataTracker;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HeldItemRendererImpl extends AbstractBehaviorRenderer implements HeldItemRenderer {
   @Getter
   private final int id;
   @Getter
   private final UUID uuid;
   @Getter
   private final Map<String, HeldItemRenderer.Item> spawnQueue = new HashMap<>();
   @Getter
   private final Map<String, HeldItemRenderer.Item> rendered = new HashMap<>();
   @Getter
   private final Map<String, HeldItemRenderer.Item> destroyQueue = new HashMap<>();
   @Getter
   private final CollectionDataTracker<Integer> passengers = new CollectionDataTracker<>(new HashSet<>());
   @Getter
   private final PositionCodec positionCodec = new PositionCodec();
   private boolean initialized;
   private BehaviorRendererParser<HeldItemRenderer> cachedParser;
   private RenderParsers cachedParserOwner;

   public HeldItemRendererImpl(ActiveModel activeModel) {
      super(activeModel);
      this.id = this.nmsHandler.getEntityHandler().getNextEntityId();
      this.uuid = UUID.randomUUID();
   }

   @Override
   public void initialize() {
      for (Entry<String, ModelBone> boneEntry : this.activeModel.getBones().entrySet()) {
         String boneId = boneEntry.getKey();
         ModelBone modelBone = boneEntry.getValue();
         this.create(boneId, modelBone);
      }

      this.initialized = true;
   }

   private void create(String boneId, ModelBone modelBone) {
      Optional maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM);
      if (!maybeData.isEmpty()) {
         BoneBehavior itemData = (BoneBehavior)maybeData.get();
         HeldItemRendererImpl.ItemImpl item = new HeldItemRendererImpl.ItemImpl(this.nmsHandler.getEntityHandler().getNextEntityId(), UUID.randomUUID());
         item.position.set(((HeldItem)itemData).getLocation());
         item.scale.set(modelBone.getGlobalTransform().getScale());
         item.rotation.set(((HeldItem)itemData).getRotation());
         item.model.set(modelBone.isVisible() ? modelBone.getModel() : null);
         item.display.set(((HeldItem)itemData).getDisplay());
         item.glowing.set(modelBone.isGlowing());
         item.glowColor.set(modelBone.getGlowColor());
         this.spawnQueue.put(boneId, item);
         this.destroyQueue.remove(boneId);
         this.passengers.add(item.id);
      }
   }

   @Override
   public void readBoneData() {
      if (this.initialized) {
         this.destroyQueue.putAll(this.rendered);

         for (Entry<String, ModelBone> boneEntry : this.activeModel.getBones().entrySet()) {
            String boneId = boneEntry.getKey();
            ModelBone modelBone = boneEntry.getValue();
            HeldItemRenderer.Item renderer = this.getQueued(boneId);
            if (renderer != null) {
               this.read(boneId, renderer, modelBone);
            } else {
               this.create(boneId, modelBone);
            }
         }

         this.destroyQueue.forEach((s, item) -> this.passengers.remove(item.getId()));
      }
   }

   private void read(String boneId, HeldItemRenderer.Item item, ModelBone modelBone) {
      Optional maybeData = modelBone.getBoneBehavior(BoneBehaviorTypes.ITEM);
      maybeData.ifPresent(heldItem -> {
         item.getPosition().set(((HeldItem)heldItem).getLocation());
         item.getScale().set(modelBone.getGlobalTransform().getScale());
         item.getRotation().set(((HeldItem)heldItem).getRotation());
         item.getGlowing().set(modelBone.isGlowing());
         item.getGlowColor().set(modelBone.getGlowColor());
         if (!modelBone.isVisible()) {
            item.getModel().set(null);
         } else {
            item.getModel().set(modelBone.getModel());
            if (modelBone.getModelTracker().isDirty()) {
               modelBone.getModelTracker().clearDirty();
               item.getModel().markDirty();
            }
         }

         item.getDisplay().set(((HeldItem)heldItem).getDisplay());
         this.destroyQueue.remove(boneId);
      });
   }

   @Override
   public void sendToClient(RenderParsers parsers) {
      if (this.initialized) {
         this.destroyQueue.keySet().forEach(this.rendered::remove);
         this.parser(parsers).sendToClients(this);
         this.rendered.putAll(this.spawnQueue);
         this.spawnQueue.clear();
         this.destroyQueue.clear();
      }
   }

   @Override
   public void destroy(RenderParsers parsers) {
      if (this.initialized) {
         parsers.getBehaviorParser(this).destroy(this);
      }
   }

   private BehaviorRendererParser<HeldItemRenderer> parser(RenderParsers parsers) {
      if (this.cachedParser == null || this.cachedParserOwner != parsers) {
         this.cachedParserOwner = parsers;
         this.cachedParser = parsers.getBehaviorParser(this);
      }

      return this.cachedParser;
   }

   @Getter
   @AllArgsConstructor
   public static class ItemImpl implements HeldItemRenderer.Item {
      private final int id;
      private final UUID uuid;
      private final DataTracker<Vector3f> position = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
      private final DataTracker<Vector3f> scale = new UpdateDataTracker<>(new Vector3f(), Vector3f::set);
      private final DataTracker<Quaternionf> rotation = new UpdateDataTracker<>(new Quaternionf(), Quaternionf::set);
      private final DataTracker<ItemStack> model = new DataTracker<>();
      private final DataTracker<ItemDisplayTransform> display = new DataTracker<>(ItemDisplayTransform.NONE);
      private final DataTracker<Boolean> glowing = new DataTracker<>(false);
      private final DataTracker<Integer> glowColor = new DataTracker<>(-1);
   }
}
