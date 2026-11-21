package com.nexomc.nexo.mechanics.furniture.hitbox;

import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.LocationUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.RangeUtilsKt;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.VectorUtilsKt;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000|\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u001e\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u0000 ;2\u00020\u0001:\u0001;BG\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u0012\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0003\u0012\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003¢\u0006\u0004\b\u000b\u0010\fB\u0011\b\u0016\u0012\u0006\u0010\r\u001a\u00020\u000e¢\u0006\u0004\b\u000b\u0010\u000fB\u0017\b\u0016\u0012\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011¢\u0006\u0004\b\u000b\u0010\u0013BA\b\u0016\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0011\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0011\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0011\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0011¢\u0006\u0004\b\u000b\u0010\u0014J\u0006\u0010\u001a\u001a\u00020\u001bJ\u0016\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!J.\u0010\"\u001a\u001a\u0012\u0016\u0012\u0014 &*\t\u0018\u00010$¢\u0006\u0002\b%0$¢\u0006\u0002\b%0#2\u0006\u0010'\u001a\u00020$2\u0006\u0010(\u001a\u00020\u001bJ!\u0010)\u001a\r\u0012\t\u0012\u00070$¢\u0006\u0002\b%0#2\u0006\u0010'\u001a\u00020$2\u0006\u0010*\u001a\u00020\u001bJ\u001c\u0010+\u001a\b\u0012\u0004\u0012\u00020$0#2\u0006\u0010'\u001a\u00020$2\u0006\u0010*\u001a\u00020\u001bJ\u001c\u0010,\u001a\b\u0012\u0004\u0012\u00020-0#2\u0006\u0010'\u001a\u00020$2\u0006\u0010*\u001a\u00020\u001bJ\u001c\u0010.\u001a\b\u0012\u0004\u0012\u00020$0#2\u0006\u0010'\u001a\u00020$2\u0006\u0010*\u001a\u00020\u001bJ\u001c\u0010/\u001a\b\u0012\u0004\u0012\u00020$0#2\u0006\u0010'\u001a\u00020$2\u0006\u0010*\u001a\u00020\u001bJ\u000f\u00100\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u000f\u00101\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003HÆ\u0003J\u000f\u00102\u001a\b\u0012\u0004\u0012\u00020\b0\u0003HÆ\u0003J\u000f\u00103\u001a\b\u0012\u0004\u0012\u00020\n0\u0003HÆ\u0003JI\u00104\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00032\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003HÆ\u0001J\u0014\u00105\u001a\u0002062\b\u00107\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u00108\u001a\u000209HÖ\u0081\u0004J\n\u0010:\u001a\u00020\u0012HÖ\u0081\u0004R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0016R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0016R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0016¨\u0006<"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/hitbox/FurnitureHitbox;", "", "barriers", "Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "Lcom/nexomc/nexo/mechanics/furniture/hitbox/BarrierHitbox;", "interactions", "Lcom/nexomc/nexo/mechanics/furniture/hitbox/InteractionHitbox;", "shulkers", "Lcom/nexomc/nexo/mechanics/furniture/hitbox/ShulkerHitbox;", "ghasts", "Lcom/nexomc/nexo/mechanics/furniture/hitbox/GhastHitbox;", "<init>", "(Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;)V", "hitboxSection", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "hitboxList", "", "", "(Ljava/util/Collection;)V", "(Ljava/util/Collection;Ljava/util/Collection;Ljava/util/Collection;Ljava/util/Collection;)V", "getBarriers", "()Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "getInteractions", "getShulkers", "getGhasts", "hitboxHeight", "", "refreshHitboxes", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "hitboxLocations", "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", "Lorg/bukkit/Location;", "Lorg/jetbrains/annotations/NotNull;", "kotlin.jvm.PlatformType", "center", "yaw", "barrierLocations", "rotation", "interactionLocations", "interactionBoundingBoxes", "Lorg/bukkit/util/BoundingBox;", "shulkerLocations", "ghastLocations", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "Companion", "com.nexomc:core"}
)
public final class FurnitureHitbox {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ObjectOpenHashSet<BarrierHitbox> barriers;
   @NotNull
   private final ObjectOpenHashSet<InteractionHitbox> interactions;
   @NotNull
   private final ObjectOpenHashSet<ShulkerHitbox> shulkers;
   @NotNull
   private final ObjectOpenHashSet<GhastHitbox> ghasts;
   @NotNull
   private static final FurnitureHitbox EMPTY = new FurnitureHitbox(new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet());

   public FurnitureHitbox(@NotNull ObjectOpenHashSet<BarrierHitbox> barriers, @NotNull ObjectOpenHashSet<InteractionHitbox> interactions, @NotNull ObjectOpenHashSet<ShulkerHitbox> shulkers, @NotNull ObjectOpenHashSet<GhastHitbox> ghasts) {
      super();
      this.barriers = barriers;
      this.interactions = interactions;
      this.shulkers = shulkers;
      this.ghasts = ghasts;
   }

   @NotNull
   public final ObjectOpenHashSet<BarrierHitbox> getBarriers() {
      return this.barriers;
   }

   @NotNull
   public final ObjectOpenHashSet<InteractionHitbox> getInteractions() {
      return this.interactions;
   }

   @NotNull
   public final ObjectOpenHashSet<ShulkerHitbox> getShulkers() {
      return this.shulkers;
   }

   @NotNull
   public final ObjectOpenHashSet<GhastHitbox> getGhasts() {
      return this.ghasts;
   }

   public FurnitureHitbox(@NotNull ConfigurationSection hitboxSection) {
      Iterable iterable = (Iterable)NexoYamlKt.stringListOrSingle(hitboxSection, "barrier", (String)null);
      Companion companion = Companion;
      Collection destination = (Collection)(new ObjectLinkedOpenHashSet());

      for(Object element : iterable) {
         String p0 = (String)element;
         int value = 0;
         Iterable iterable2 = (Iterable)companion.parseHitbox(p0);
         CollectionsKt.addAll(destination, iterable2);
      }

      Collection collection = (Collection)((ObjectLinkedOpenHashSet)destination);
      iterable = (Iterable)NexoYamlKt.stringListOrSingle(hitboxSection, "interaction", (String)null);
      Collection collection2 = collection;
      Collection collection7 = (Collection)(new ObjectOpenHashSet((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10));

      for(Object item : iterable) {
         String p0 = (String)item;
         int value2 = 0;
         collection7.add(new InteractionHitbox(p0));
      }

      ObjectOpenHashSet objectOpenHashSet = (ObjectOpenHashSet)collection7;
      Collection collection3 = (Collection)objectOpenHashSet;
      iterable = (Iterable)NexoYamlKt.stringListOrSingle(hitboxSection, "shulker", (String)null);
      Collection collection4 = collection3;
      collection7 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj : iterable) {
         String p0 = (String)obj;
         int value3 = 0;
         collection7.add(new ShulkerHitbox(p0));
      }

      List list = (List)collection7;
      Collection collection5 = (Collection)list;
      iterable = (Iterable)NexoYamlKt.stringListOrSingle(hitboxSection, "ghast", (String)null);
      Collection collection6 = collection5;
      collection7 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj2 : iterable) {
         String p0 = (String)obj2;
         int value4 = 0;
         collection7.add(new GhastHitbox(p0));
      }

      List list2 = (List)collection7;
      this(collection2, collection4, collection6, (Collection)list2);
   }

   public FurnitureHitbox(@NotNull Collection<String> hitboxList) {
      this(new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet());
      Iterable iterable = (Iterable)hitboxList;

      for(Object element : iterable) {
         String hitbox;
         block: {
            block2: {
               block3: {
                  hitbox = (String)element;
                  int value = 0;
                  String type = StringsKt.substringAfterLast(hitbox, " ", hitbox);
                  hitbox = StringUtilsKt.remove(hitbox, type);
                  switch (type.hashCode()) {
                     case -1504469236:
                        if (!type.equals("SHULKER")) {
                           continue;
                        }
                        break block3;
                     case -297901582:
                        if (!type.equals("INTERACTION")) {
                           continue;
                        }
                        break block2;
                     case 66:
                        if (!type.equals("B")) {
                           continue;
                        }
                        break block;
                     case 71:
                        if (!type.equals("G")) {
                           continue;
                        }
                        break;
                     case 73:
                        if (!type.equals("I")) {
                           continue;
                        }
                        break block2;
                     case 83:
                        if (!type.equals("S")) {
                           continue;
                        }
                        break block3;
                     case 67780065:
                        if (!type.equals("GHAST")) {
                           continue;
                        }
                        break;
                     case 384839575:
                        if (!type.equals("BARRIER")) {
                           continue;
                        }
                        break block;
                     default:
                        continue;
                  }

                  this.ghasts.add(new GhastHitbox(hitbox));
                  continue;
               }

               this.shulkers.add(new ShulkerHitbox(hitbox));
               continue;
            }

            this.interactions.add(new InteractionHitbox(hitbox));
            continue;
         }

         this.barriers.add(new BarrierHitbox(hitbox));
      }

   }

   public FurnitureHitbox(@NotNull Collection<BarrierHitbox> barriers, @NotNull Collection<InteractionHitbox> interactions, @NotNull Collection<ShulkerHitbox> shulkers, @NotNull Collection<GhastHitbox> ghasts) {
      this(new ObjectOpenHashSet(barriers), new ObjectOpenHashSet(interactions), new ObjectOpenHashSet(shulkers), new ObjectOpenHashSet(ghasts));
   }

   public final float hitboxHeight() {
      float highestBarrier = 0.0F;
      for (BarrierHitbox barrier : this.barriers) {
         highestBarrier = Math.max(highestBarrier, (float)barrier.getY() + 1.0F);
      }

      float highestInteraction = 0.0F;
      for (InteractionHitbox interaction : this.interactions) {
         float top = VectorUtilsKt.plus(interaction.getOffset(), new Vector3f(0.0F, interaction.getHeight(), 0.0F)).y;
         highestInteraction = Math.max(highestInteraction, top);
      }

      float highestShulker = 0.0F;
      for (ShulkerHitbox shulker : this.shulkers) {
         float top = VectorUtilsKt.plus(shulker.getOffset(), new Vector3f(0.0F, (float)shulker.getLength(), 0.0F)).y;
         highestShulker = Math.max(highestShulker, top);
      }

      float highestGhast = 0.0F;
      for (GhastHitbox ghast : this.ghasts) {
         float top = VectorUtilsKt.plus(ghast.getOffset(), new Vector3f(0.0F, (float)ghast.getScale(), 0.0F)).y;
         highestGhast = Math.max(highestGhast, top);
      }

      return Math.max(Math.max(Math.max(highestInteraction, highestBarrier), highestShulker), highestGhast);
   }

   public final void refreshHitboxes(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if (mechanicFactory != null) {
         IFurniturePacketManager iFurniturePacketManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (iFurniturePacketManager != null) {
            IFurniturePacketManager packetManager = iFurniturePacketManager;
            packetManager.removeHitboxEntityPacket(baseEntity, mechanic);
            packetManager.removeBarrierHitboxPacket(baseEntity, mechanic);
            packetManager.sendHitboxEntityPacket(baseEntity, mechanic);
            packetManager.sendBarrierHitboxPacket(baseEntity, mechanic);
            return;
         }
      }

   }

   @NotNull
   public final ObjectArrayList<Location> hitboxLocations(@NotNull Location center, float yaw) {
      return CollectionUtilsKt.plusFast((List)CollectionUtilsKt.plusFast((List)CollectionUtilsKt.plusFast((List)this.barrierLocations(center, yaw), (Iterable)this.interactionLocations(center, yaw)), (Iterable)this.shulkerLocations(center, yaw)), (Iterable)this.ghastLocations(center, yaw));
   }

   @NotNull
   public final ObjectArrayList<Location> barrierLocations(@NotNull Location center, float rotation) {
      Iterable iterable = (Iterable)this.barriers;
      Collection destination = (Collection)(new ObjectArrayList((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10));

      for(Object item : iterable) {
         BarrierHitbox it = (BarrierHitbox)item;
         int value = 0;
         destination.add(it.groundRotate(rotation).add(center));
      }

      return (ObjectArrayList)destination;
   }

   @NotNull
   public final ObjectArrayList<Location> interactionLocations(@NotNull Location center, float rotation) {
      Iterable iterable = (Iterable)this.interactions;
      Collection destination = (Collection)(new ObjectArrayList((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10));

      for(Object item : iterable) {
         InteractionHitbox it = (InteractionHitbox)item;
         int value = 0;
         Location toCenterLocation = center.toCenterLocation();
         destination.add(LocationUtilsKt.add(toCenterLocation, it.offset(rotation)));
      }

      return (ObjectArrayList)destination;
   }

   @NotNull
   public final ObjectArrayList<BoundingBox> interactionBoundingBoxes(@NotNull Location center, float rotation) {
      Iterable iterable = (Iterable)this.interactions;
      Collection destination = (Collection)(new ObjectArrayList((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10));

      for(Object item : iterable) {
         InteractionHitbox it = (InteractionHitbox)item;
         int value = 0;
         Location toCenterLocation = center.toCenterLocation();
         destination.add(it.boundingBox(LocationUtilsKt.add(toCenterLocation, it.offset(rotation))));
      }

      return (ObjectArrayList)destination;
   }

   @NotNull
   public final ObjectArrayList<Location> shulkerLocations(@NotNull Location center, float rotation) {
      Iterable iterable = (Iterable)this.shulkers;
      Collection destination = (Collection)(new ObjectArrayList((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10));

      for(Object item : iterable) {
         ShulkerHitbox it = (ShulkerHitbox)item;
         int value = 0;
         destination.add(LocationUtilsKt.plus(center, it.offset(rotation)));
      }

      return (ObjectArrayList)destination;
   }

   @NotNull
   public final ObjectArrayList<Location> ghastLocations(@NotNull Location center, float rotation) {
      Iterable iterable = (Iterable)this.ghasts;
      Collection destination = (Collection)(new ObjectArrayList((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10));

      for(Object item : iterable) {
         GhastHitbox it = (GhastHitbox)item;
         int value = 0;
         destination.add(LocationUtilsKt.plus(center, it.offset(rotation)));
      }

      return (ObjectArrayList)destination;
   }

   @NotNull
   public final ObjectOpenHashSet<BarrierHitbox> component1() {
      return this.barriers;
   }

   @NotNull
   public final ObjectOpenHashSet<InteractionHitbox> component2() {
      return this.interactions;
   }

   @NotNull
   public final ObjectOpenHashSet<ShulkerHitbox> component3() {
      return this.shulkers;
   }

   @NotNull
   public final ObjectOpenHashSet<GhastHitbox> component4() {
      return this.ghasts;
   }

   @NotNull
   public final FurnitureHitbox copy(@NotNull ObjectOpenHashSet<BarrierHitbox> barriers, @NotNull ObjectOpenHashSet<InteractionHitbox> interactions, @NotNull ObjectOpenHashSet<ShulkerHitbox> shulkers, @NotNull ObjectOpenHashSet<GhastHitbox> ghasts) {
      return new FurnitureHitbox(barriers, interactions, shulkers, ghasts);
   }

   @NotNull
   public String toString() {
      return "FurnitureHitbox(barriers=" + this.barriers + ", interactions=" + this.interactions + ", shulkers=" + this.shulkers + ", ghasts=" + this.ghasts + ")";
   }

   public int hashCode() {
      int result = this.barriers.hashCode();
      result = result * 31 + this.interactions.hashCode();
      result = result * 31 + this.shulkers.hashCode();
      result = result * 31 + this.ghasts.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof FurnitureHitbox)) {
         return false;
      } else {
         FurnitureHitbox furnitureHitbox = (FurnitureHitbox)other;
         if (!Intrinsics.areEqual(this.barriers, furnitureHitbox.barriers)) {
            return false;
         } else if (!Intrinsics.areEqual(this.interactions, furnitureHitbox.interactions)) {
            return false;
         } else if (!Intrinsics.areEqual(this.shulkers, furnitureHitbox.shulkers)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.ghasts, furnitureHitbox.ghasts);
         }
      }
   }

   public FurnitureHitbox() {
      this(new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet());
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\u000b\u001a\u00020\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\r"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/hitbox/FurnitureHitbox$Companion;", "", "<init>", "()V", "EMPTY", "Lcom/nexomc/nexo/mechanics/furniture/hitbox/FurnitureHitbox;", "getEMPTY", "()Lcom/nexomc/nexo/mechanics/furniture/hitbox/FurnitureHitbox;", "parseHitbox", "", "Lcom/nexomc/nexo/mechanics/furniture/hitbox/BarrierHitbox;", "hitboxString", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final FurnitureHitbox getEMPTY() {
         return FurnitureHitbox.EMPTY;
      }

      @NotNull
      public final List<BarrierHitbox> parseHitbox(@NotNull String hitboxString) {
         List list;
         if (Intrinsics.areEqual(hitboxString, "origin")) {
            list = CollectionsKt.listOf(new BarrierHitbox("0,0,0"));
         } else if (StringsKt.contains((CharSequence)hitboxString, (CharSequence)"..", false)) {
            CharSequence charSequence = (CharSequence)hitboxString;
            String[] strs = new String[]{","};
            Iterable iterable = (Iterable)StringsKt.split(charSequence, strs, false, 3);
            Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

            for(Object item : iterable) {
               String r = (String)item;
               int value = 0;
               IntRange intRange = RangeUtilsKt.toIntRangeOrNull(StringUtilsKt.removeSpaces(r));
               if (intRange == null) {
                  Integer number = StringsKt.toIntOrNull(r);
                  int it = number != null ? number : 0;
                  int value2 = 0;
                  intRange = new IntRange(it, it);
               }

               destination.add(intRange);
            }

            List coordinates = (List)destination;
            IntRange xRange = (IntRange)coordinates.get(0);
            IntRange yRange = (IntRange)coordinates.get(1);
            IntRange zRange = (IntRange)coordinates.get(2);
            List list2 = (List)(new ArrayList());
            List list3 = list2;
            int value3 = 0;
            int x = xRange.getFirst();
            int last = xRange.getLast();
            if (x <= last) {
               while(true) {
                  int y = yRange.getFirst();
                  int value4 = yRange.getLast();
                  if (y <= value4) {
                     while(true) {
                        int z = zRange.getFirst();
                        int value5 = zRange.getLast();
                        if (z <= value5) {
                           while(true) {
                              ((Collection)list3).add(new BarrierHitbox(x, y, z));
                              if (z == value5) {
                                 break;
                              }

                              ++z;
                           }
                        }

                        if (y == value4) {
                           break;
                        }

                        ++y;
                     }
                  }

                  if (x == last) {
                     break;
                  }

                  ++x;
               }
            }

            list = list2;
         } else {
            list = CollectionsKt.listOf(new BarrierHitbox(hitboxString));
         }

         return list;
      }
   }
}
