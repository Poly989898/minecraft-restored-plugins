package com.nexomc.nexo.mechanics.furniture;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.api.NexoFurniture;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001e\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B5\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n¢\u0006\u0004\b\f\u0010\rJ\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005J\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001e2\u0006\u0010\u001f\u001a\u00020\bJ\u0006\u0010\u0002\u001a\u00020\u0003J\u000e\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u001eJ\u000e\u0010 \u001a\u00020!2\u0006\u0010#\u001a\u00020$R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0017¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\u001a¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001c¨\u0006%"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/FurnitureHitboxEntity;", "", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "entityIds", "", "", "boundingBoxes", "", "Lorg/bukkit/util/BoundingBox;", "<init>", "(Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;Lorg/bukkit/entity/ItemDisplay;Ljava/util/Collection;Ljava/util/List;)V", "baseUuid", "Ljava/util/UUID;", "getBaseUuid", "()Ljava/util/UUID;", "baseId", "getBaseId", "()I", "worldUID", "getWorldUID", "Lit/unimi/dsi/fastutil/ints/IntArrayList;", "getEntityIds", "()Lit/unimi/dsi/fastutil/ints/IntArrayList;", "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", "getBoundingBoxes", "()Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", "hitboxLocation", "Lorg/bukkit/util/Vector;", "entityId", "overlaps", "", "vector", "location", "Lcom/nexomc/nexo/mechanics/furniture/BlockLocation;", "com.nexomc:core"}
)
public final class FurnitureHitboxEntity {
   @NotNull
   private final FurnitureMechanic mechanic;
   @NotNull
   private final UUID baseUuid;
   private final int baseId;
   @NotNull
   private final UUID worldUID;
   @NotNull
   private final IntArrayList entityIds;
   @NotNull
   private final ObjectArrayList<BoundingBox> boundingBoxes;

   @NotNull
   public final UUID getBaseUuid() {
      return this.baseUuid;
   }

   public final int getBaseId() {
      return this.baseId;
   }

   @NotNull
   public final UUID getWorldUID() {
      return this.worldUID;
   }

   @NotNull
   public final IntArrayList getEntityIds() {
      return this.entityIds;
   }

   @NotNull
   public final ObjectArrayList<BoundingBox> getBoundingBoxes() {
      return this.boundingBoxes;
   }

   public FurnitureHitboxEntity(@NotNull FurnitureMechanic mechanic, @NotNull ItemDisplay baseEntity, @NotNull Collection<Integer> entityIds, @NotNull List<? extends BoundingBox> boundingBoxes) {
      super();
      this.mechanic = mechanic;
      UUID uniqueId = baseEntity.getUniqueId();
      this.baseUuid = uniqueId;
      this.baseId = baseEntity.getEntityId();
      uniqueId = baseEntity.getWorld().getUID();
      this.worldUID = uniqueId;
      IntArrayList intArrayList = entityIds instanceof IntArrayList ? (IntArrayList)entityIds : null;
      if ((entityIds instanceof IntArrayList ? (IntArrayList)entityIds : null) == null) {
         intArrayList = new IntArrayList(entityIds);
      }

      this.entityIds = intArrayList;
      ObjectArrayList objectArrayList = boundingBoxes instanceof ObjectArrayList ? (ObjectArrayList)boundingBoxes : null;
      if ((boundingBoxes instanceof ObjectArrayList ? (ObjectArrayList)boundingBoxes : null) == null) {
         objectArrayList = new ObjectArrayList((Collection)boundingBoxes);
      }

      this.boundingBoxes = objectArrayList;
   }

   @Nullable
   public final ItemDisplay baseEntity() {
      Entity entity = Bukkit.getEntity(this.baseUuid);
      return entity instanceof ItemDisplay ? (ItemDisplay)entity : null;
   }

   @Nullable
   public final Vector hitboxLocation(int entityId) {
      FurnitureHitboxEntity furnitureHitboxEntity = this;

      Object obj;
      try {
         FurnitureHitboxEntity furnitureHitboxEntity2 = furnitureHitboxEntity;
         int value = 0;
         obj = ResultShim.ok((BoundingBox)CollectionsKt.getOrNull((List)furnitureHitboxEntity2.boundingBoxes, furnitureHitboxEntity2.entityIds.indexOf(entityId)));
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      BoundingBox boundingBox = (BoundingBox)(ResultShim.isFailure(obj) ? null : obj);
      return boundingBox != null ? boundingBox.getCenter() : null;
   }

   @NotNull
   public final FurnitureMechanic mechanic() {
      FurnitureMechanic furnitureMechanic = NexoFurniture.furnitureMechanic(this.mechanic.getSection().getName());
      if (furnitureMechanic == null) {
         furnitureMechanic = this.mechanic;
      }

      return furnitureMechanic;
   }

   public final boolean overlaps(@NotNull Vector vector) {
      Iterable iterable = (Iterable)this.boundingBoxes;
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = iterable.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object element = iterator.next();
            BoundingBox it = (BoundingBox)element;
            int value = 0;
            if (it.contains(vector)) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   public final boolean overlaps(@NotNull BlockLocation location) {
      Iterable iterable = (Iterable)this.boundingBoxes;
      boolean flag;
      if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = iterable.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object element = iterator.next();
            BoundingBox it = (BoundingBox)element;
            int value = 0;
            if (it.contains((double)location.getX(), (double)location.getY(), (double)location.getZ())) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }
}
