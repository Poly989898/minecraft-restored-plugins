package com.ticxo.modelengine.core.model.render;

import com.ticxo.modelengine.api.lod.BoneSnapshotHandler;
import com.ticxo.modelengine.api.model.bone.BoneItems;
import com.ticxo.modelengine.api.model.render.DisplayBone;
import com.ticxo.modelengine.api.nms.entity.EntityHandler;
import com.ticxo.modelengine.api.utils.data.UpdateScheme;
import com.ticxo.modelengine.api.utils.data.tracker.CollectionDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.DataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.QuaternionfDataTracker;
import com.ticxo.modelengine.api.utils.data.tracker.Vector3fDataTracker;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
@AllArgsConstructor
public class DisplayBoneImpl implements DisplayBone {
   private final String boneId;
   private final BoneSnapshotHandler snapshotHandler = new BoneSnapshotHandler(this);
   private final DataTracker<Boolean> render = new DataTracker<>(true);
   private final DataTracker<Boolean> step = new DataTracker<>(false);
   private final DataTracker<Vector3f> position = new Vector3fDataTracker();
   private final DataTracker<Quaternionf> leftRotation = new QuaternionfDataTracker();
   private final DataTracker<Vector3f> scale = new Vector3fDataTracker();
   private final DataTracker<Quaternionf> rightRotation = new QuaternionfDataTracker();
   private final DataTracker<Billboard> billboard = new DataTracker<>(Billboard.FIXED);
   private final DataTracker<ItemDisplayTransform> display = new DataTracker<>(ItemDisplayTransform.NONE);
   private final DataTracker<Boolean> visibility = new DataTracker<>(true);
   private final DataTracker<Boolean> glowing = new DataTracker<>(false);
   private final DataTracker<Integer> glowColor = new DataTracker<>(-1);
   private final DataTracker<Integer> brightness = new DataTracker<>(-1);
   private final DataTracker<Set<UUID>> glowAudience = new DataTracker<>();
   private final DataTracker<Boolean> audienceGlowing = new DataTracker<>(false);
   private final DataTracker<Integer> audienceGlowColor = new DataTracker<>(-1);
   private final Int2ObjectMap<DisplayBone.BoneData> model = new Int2ObjectOpenHashMap();
   private final UpdateScheme<DisplayBone.BoneData> modelUpdateScheme = new UpdateScheme<>();

   protected void updateBoneData(EntityHandler entityHandler, CollectionDataTracker<Integer> passengerCollector, BoneItems boneItems) {
      Map<Integer, ItemStack> newItems = boneItems.getItems();
      IntSet oldKeys = this.model.keySet();
      IntOpenHashSet added = new IntOpenHashSet(newItems.keySet());
      IntOpenHashSet removed = new IntOpenHashSet(oldKeys);
      removed.removeAll(added);
      added.removeAll(oldKeys);
      HashSet<DisplayBone.BoneData> reusableData = new HashSet<>();
      IntIterator it = removed.iterator();

      while (it.hasNext()) {
         reusableData.add(this.model.remove(it.nextInt()));
      }

      Iterator<DisplayBone.BoneData> iterator = reusableData.iterator();
      IntIterator itx = added.iterator();

      while (itx.hasNext()) {
         int hash = itx.nextInt();
         ItemStack stack = newItems.get(hash).clone();
         if (iterator.hasNext()) {
            DisplayBone.BoneData data = iterator.next();
            int oldHash = data.getModelHash();
            data.getModel().set(stack);
            this.modelUpdateScheme.addUpdated(data);
            this.model.remove(oldHash);
            this.model.put(hash, data);
         } else {
            DisplayBoneImpl.BoneDataImpl data = new DisplayBoneImpl.BoneDataImpl(entityHandler.getNextEntityId(), this);
            data.getModel().set(stack);
            this.model.put(hash, data);
            this.modelUpdateScheme.addAdded(data);
            passengerCollector.add(data.getId());
         }
      }

      while (iterator.hasNext()) {
         DisplayBone.BoneData data = iterator.next();
         this.modelUpdateScheme.addRemove(data);
         passengerCollector.remove(data.getId());
      }
   }

   @Getter
   @AllArgsConstructor
   public static class BoneDataImpl implements DisplayBone.BoneData {
      private final int id;
      private final UUID uuid = UUID.randomUUID();
      private final DisplayBone bone;
      private final DataTracker<ItemStack> model = new DataTracker<>();

      @Override
      public boolean equals(Object object) {
         if (object != null && this.getClass() == object.getClass()) {
            DisplayBoneImpl.BoneDataImpl boneData = (DisplayBoneImpl.BoneDataImpl)object;
            return Objects.equals(this.model, boneData.model);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.model);
      }

      @Override
      public int getModelHash() {
         return this.model.get().hashCode();
      }
   }
}
