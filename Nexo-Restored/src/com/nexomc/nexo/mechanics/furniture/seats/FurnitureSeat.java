package com.nexomc.nexo.mechanics.furniture.seats;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.CustomDataTypes;
import com.nexomc.nexo.utils.LocationUtilsKt;
import com.nexomc.nexo.utils.VectorUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B!\b\u0016\u0012\u0016\u0010\u0002\u001a\u0012\u0012\u0006\u0012\u0004\u0018\u00010\u0007\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0006¢\u0006\u0004\b\u0004\u0010\bB\u0011\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0007¢\u0006\u0004\b\u0004\u0010\tJ\u0006\u0010\u0002\u001a\u00020\u0003J\u000e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\rJ\u0010\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\rH\u0002J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0015\u001a\u00020\u0016HÖ\u0081\u0004J\n\u0010\u0017\u001a\u00020\u0007HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0019"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/seats/FurnitureSeat;", "", "offset", "Lorg/bukkit/util/Vector;", "<init>", "(Lorg/bukkit/util/Vector;)V", "", "", "(Ljava/util/Map;)V", "(Ljava/lang/String;)V", "getOffset", "()Lorg/bukkit/util/Vector;", "yaw", "", "rotateOffset", "angle", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "Companion", "com.nexomc:core"}
)
public final class FurnitureSeat {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Vector offset;
   @NotNull
   private static final NamespacedKey SEAT_KEY;

   public FurnitureSeat(@NotNull Vector offset) {
      super();
      this.offset = offset;
   }

   @NotNull
   public final Vector getOffset() {
      return this.offset;
   }

   public FurnitureSeat(@NotNull Map<String, Object> offset) {
      Vector vector = Vector.deserialize(offset);
      this(vector);
   }

   public FurnitureSeat(@NotNull String offset) {
      this(VectorUtils.INSTANCE.vectorFromString(offset, 0.0F));
   }

   @NotNull
   public final Vector offset() {
      return this.offset;
   }

   @NotNull
   public final Vector offset(float yaw) {
      return this.rotateOffset(yaw);
   }

   private final Vector rotateOffset(float angle) {
      float angle_3 = angle;
      if (angle_3 < 0.0F) {
         angle_3 = angle_3 + 360.0F;
      }

      double radians = (double)Math.toRadians(angle_3);
      double x = this.offset.getX() * Math.cos(radians) - -this.offset.getZ() * Math.sin(radians);
      double z = this.offset.getX() * Math.sin(radians) + -this.offset.getZ() * Math.cos(radians);
      double y = this.offset.getY();
      return new Vector(x, y, z);
   }

   @NotNull
   public final Vector component1() {
      return this.offset;
   }

   @NotNull
   public final FurnitureSeat copy(@NotNull Vector offset) {
      return new FurnitureSeat(offset);
   }

   @NotNull
   public String toString() {
      return "FurnitureSeat(offset=" + this.offset + ")";
   }

   public int hashCode() {
      return this.offset.hashCode();
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof FurnitureSeat)) {
         return false;
      } else {
         FurnitureSeat furnitureSeat = (FurnitureSeat)other;
         return Intrinsics.areEqual(this.offset, furnitureSeat.offset);
      }
   }

   static {
      SEAT_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "seat");
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\b\u001a\u0004\u0018\u00010\t2\b\u0010\n\u001a\u0004\u0018\u00010\u0001J\u001c\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J\u0010\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015J(\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bJ\u0016\u0010\u001c\u001a\u00020\u00172\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u001d"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/seats/FurnitureSeat$Companion;", "", "<init>", "()V", "SEAT_KEY", "Lorg/bukkit/NamespacedKey;", "getSEAT_KEY", "()Lorg/bukkit/NamespacedKey;", "getSeat", "Lcom/nexomc/nexo/mechanics/furniture/seats/FurnitureSeat;", "offset", "getSeats", "", "Lorg/bukkit/entity/Interaction;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "isSeat", "", "entity", "Lorg/bukkit/entity/Entity;", "sitOnSeat", "", "player", "Lorg/bukkit/entity/Player;", "interactionPoint", "Lorg/bukkit/Location;", "updateSeats", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getSEAT_KEY() {
         return FurnitureSeat.SEAT_KEY;
      }

      @Nullable
      public final FurnitureSeat getSeat(@Nullable Object offset) {
         FurnitureSeat furnitureSeat;
         if (offset instanceof Map) {
            furnitureSeat = null;
            Object obj = offset;
            if (!(offset instanceof Map)) {
               obj = null;
            }

            Map map = (Map)obj;
            furnitureSeat = new FurnitureSeat(map);
         } else {
            furnitureSeat = offset instanceof Vector ? new FurnitureSeat((Vector)offset) : (offset instanceof String ? new FurnitureSeat((String)offset) : (offset instanceof Double ? new FurnitureSeat(String.valueOf(((Number)offset).doubleValue())) : (offset instanceof Integer ? new FurnitureSeat(String.valueOf(((Number)offset).intValue())) : null)));
         }

         return furnitureSeat;
      }

      @NotNull
      public final List<Interaction> getSeats(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         int value = 0;
         ItemDisplay itemDisplay = mechanic.getHasSeats() ? baseEntity : null;
         List list;
         if (itemDisplay != null) {
            PersistentDataContainer persistentDataContainer = itemDisplay.getPersistentDataContainer();
            if (persistentDataContainer != null) {
               List it = (List)persistentDataContainer.get(this.getSEAT_KEY(), CustomDataTypes.INSTANCE.getUUID_LIST());
               if (it != null) {
                  Iterable iterable = (Iterable)it;
                  Collection destination = (Collection)(new ArrayList());

                  for(Object element : iterable) {
                     int value2 = 0;
                     UUID it_2 = (UUID)element;
                     int value3 = 0;
                     Entity entity = Bukkit.getEntity(it_2);
                     Interaction interaction = entity instanceof Interaction ? (Interaction)entity : null;
                     if ((entity instanceof Interaction ? (Interaction)entity : null) != null) {
                        Object obj = interaction;
                        int value4 = 0;
                        destination.add(obj);
                     }
                  }

                  list = (List)destination;
                  return list;
               }
            }
         }

         list = CollectionsKt.emptyList();
         return list;
      }

      public final boolean isSeat(@Nullable Entity entity) {
         boolean flag;
         if (entity != null) {
            PersistentDataContainer persistentDataContainer = entity.getPersistentDataContainer();
            if (persistentDataContainer != null) {
               flag = persistentDataContainer.has(this.getSEAT_KEY(), DataType.UUID);
               return flag;
            }
         }

         flag = false;
         return flag;
      }

      public final void sitOnSeat(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player, @Nullable Location interactionPoint) {
         java.lang.Object obj;
         Location baseLoc = LocationUtilsKt.getTranslatedLocation(baseEntity);
         Location location = interactionPoint;
         if (interactionPoint == null) {
            location = baseLoc;
         }

         location = location.clone();
         Location clickLoc = location;
         Vector vector = clickLoc.toVector().subtract(baseLoc.toVector());
         Vector clickOffsetWorld = vector;
         Iterable seats = (Iterable)mechanic.getSeats();
         Iterator iterator = ((Iterable)CollectionsKt.sortedWith(seats, new FurnitureSeat$Companion$sitOnSeat$$inlined$sortedBy$1(baseEntity, clickOffsetWorld))).iterator();

         FurnitureSeat furnitureSeat;
         while(true) {
            if (!iterator.hasNext()) {
               furnitureSeat = null;
               break;
            }

            FurnitureSeat seat = (FurnitureSeat)iterator.next();
            int value = 0;
            Location location2 = LocationUtilsKt.getTranslatedLocation(baseEntity).add(seat.offset(baseEntity.getYaw()));
            Location seatLocation = location2;
            Collection nearbyEntitiesByType = baseEntity.getWorld().getNearbyEntitiesByType(Interaction.class, seatLocation, 0.3);
            Iterable iterable = (Iterable)nearbyEntitiesByType;
            Iterator iterator2 = iterable.iterator();

            while(true) {
               if (iterator2.hasNext()) {
                  Object element = iterator2.next();
                  Interaction it = (Interaction)element;
                  int value2 = 0;
                  if (!FurnitureSeat.Companion.isSeat((Entity)it)) {
                     continue;
                  }

                  obj = element;
                  break;
               }

               obj = null;
               break;
            }

            Interaction existing = (Interaction)obj;
            if (existing != null) {
               existing.remove();
            }

            Collection collection = (Collection)(existing != null ? existing.getPassengers() : null);
            seat = collection == null || collection.isEmpty() ? seat : null;
            if (seat != null) {
               furnitureSeat = seat;
               break;
            }
         }

         if (furnitureSeat != null) {
            FurnitureSeat seat = furnitureSeat;
            IFurnitureSeatEntity iFurnitureSeatEntity = NMSHandlers.handler().customEntityHandler().createSeatEntity(baseEntity, mechanic, seat);
            Interaction seatEntity = iFurnitureSeatEntity != null ? iFurnitureSeatEntity.spawn() : null;
            if (seatEntity != null) {
               seatEntity.addPassenger((Entity)player);
            }

         }
      }

      public final void updateSeats(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         List list = (List)baseEntity.getPersistentDataContainer().get(this.getSEAT_KEY(), CustomDataTypes.INSTANCE.getUUID_LIST());
         if (list != null) {
            Iterable iterable = (Iterable)list;
            Object2ObjectOpenHashMap map = new Object2ObjectOpenHashMap();
            int index = 0;

            for(Object element : iterable) {
               int value = index++;
               UUID uuid = (UUID)element;
               int i = value;
               int value2 = 0;
               FurnitureSeat orNull = CollectionsKt.getOrNull(mechanic.getSeats(), i);
               Pair pair;
               if (orNull == null) {
                  pair = null;
               } else {
                  FurnitureSeat furnitureSeat = orNull;
                  Entity entity2 = Bukkit.getEntity(uuid);
                  Interaction interaction = entity2 instanceof Interaction ? (Interaction)entity2 : null;
                  if ((entity2 instanceof Interaction ? (Interaction)entity2 : null) == null) {
                     pair = null;
                  } else {
                     Interaction interactionEntity = interaction;
                     pair = TuplesKt.to(furnitureSeat, interactionEntity);
                  }
               }

               if (pair != null) {
                  Pair pair2 = pair;
                  ((Map)map).put(pair2.getFirst(), pair2.getSecond());
               }
            }

            if (mechanic.getSeats().isEmpty()) {
               Iterable iterable2 = (Iterable)map.values();
               index = 0;

               for(Object obj : iterable2) {
                  Entity p0 = (Entity)obj;
                  int value3 = 0;
                  p0.remove();
               }

            } else {
               Map map2 = (Map)map;

               for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
                  index = 0;
                  FurnitureSeat seat = (FurnitureSeat)entry.getKey();
                  Interaction entity = (Interaction)entry.getValue();
                  Location location = baseEntity.getLocation();
                  Vector3f translation = baseEntity.getTransformation().getTranslation();
                  location = LocationUtilsKt.add(location, translation).add(seat.offset(baseEntity.getYaw()));
                  Location newLocation = location;
                  if (!Intrinsics.areEqual(newLocation, entity.getLocation())) {
                     List list2 = entity.getPassengers();
                     Iterable iterable3 = (Iterable)CollectionsKt.toList((Iterable)list2);
                     Interaction interaction2 = entity;
                     int value4 = 0;

                     for(Object obj2 : iterable3) {
                        Entity p0 = (Entity)obj2;
                        int value5 = 0;
                        interaction2.removePassenger(p0);
                     }

                     List passengers = (List)iterable3;
                     entity.teleportAsync(newLocation).thenRun((() -> Companion.updateSeats$lambda$1$0(passengers, entity)));
                  }
               }

               Unit unit = Unit.INSTANCE;
            }

         }
      }

      private static final void updateSeats$lambda$1$0(List passengers, Interaction entity) {
         Iterable iterable = (Iterable)passengers;
         Interaction interaction = entity;
         int value = 0;

         for(Object element : iterable) {
            Entity p0 = (Entity)element;
            int value2 = 0;
            interaction.addPassenger(p0);
         }

      }
   }
}
