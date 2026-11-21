package com.nexomc.nexo.mechanics.furniture.bed;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.LocationUtilsKt;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.VectorUtils;
import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0007\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u0000 \u001e2\u00020\u0001:\u0001\u001eB#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bB\u0011\b\u0016\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u0007\u0010\u000bJ\u000e\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0011\u001a\u00020\u0012J\u0010\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u0012H\u0002J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0005HÆ\u0003J'\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\u0019\u001a\u00020\u00052\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u001b\u001a\u00020\u001cHÖ\u0081\u0004J\n\u0010\u001d\u001a\u00020\nHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000f¨\u0006\u001f"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "", "offset", "Lorg/bukkit/util/Vector;", "skipNight", "", "resetPhantoms", "<init>", "(Lorg/bukkit/util/Vector;ZZ)V", "bedString", "", "(Ljava/lang/String;)V", "getOffset", "()Lorg/bukkit/util/Vector;", "getSkipNight", "()Z", "getResetPhantoms", "yaw", "", "rotateOffset", "angle", "component1", "component2", "component3", "copy", "equals", "other", "hashCode", "", "toString", "Companion", "com.nexomc:core"}
)
public final class FurnitureBed {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Vector offset;
   private final boolean skipNight;
   private final boolean resetPhantoms;
   @NotNull
   private static final NamespacedKey BED_KEY;

   public FurnitureBed(@NotNull Vector offset, boolean skipNight, boolean resetPhantoms) {
      super();
      this.offset = offset;
      this.skipNight = skipNight;
      this.resetPhantoms = resetPhantoms;
   }

   @NotNull
   public final Vector getOffset() {
      return this.offset;
   }

   public final boolean getSkipNight() {
      return this.skipNight;
   }

   public final boolean getResetPhantoms() {
      return this.resetPhantoms;
   }

   public FurnitureBed(@NotNull String bedString) {
      org.bukkit.util.Vector vector;
      java.lang.Boolean flag;
      VectorUtils vectorUtils = VectorUtils.INSTANCE;
      CharSequence charSequence = (CharSequence)bedString;
      String[] strs = new String[]{" "};
      String str = CollectionsKt.firstOrNull(StringsKt.split(charSequence, strs, false, 0));
      if (str == null) {
         str = "";
      }

      block: {
         vector = vectorUtils.vectorFromString(str, 0.0F);
         CharSequence charSequence2 = (CharSequence)bedString;
         strs = new String[]{" "};
         String orNull = CollectionsKt.getOrNull(StringsKt.split(charSequence2, strs, false, 0), 1);
         if (orNull != null) {
            Boolean flag2 = StringsKt.toBooleanStrictOrNull(orNull);
            if (flag2 != null) {
               flag = flag2;
               break block;
            }
         }

         flag = true;
      }

      boolean flag3;
      block: {
         CharSequence charSequence3 = (CharSequence)bedString;
         strs = new String[]{" "};
         String str2 = CollectionsKt.getOrNull(StringsKt.split(charSequence3, strs, false, 0), 2);
         if (str2 != null) {
            Boolean flag4 = StringsKt.toBooleanStrictOrNull(str2);
            if (flag4 != null) {
               flag3 = flag4;
               break block;
            }
         }

         flag3 = true;
      }

      this(vector, flag, flag3);
   }

   @NotNull
   public final Vector offset(float yaw) {
      return this.rotateOffset(yaw);
   }

   private final Vector rotateOffset(float angle) {
      float angle_5 = angle;
      if (angle_5 < 0.0F) {
         angle_5 = angle_5 + 360.0F;
      }

      float radians = UtilsKt.getRadians(angle_5);
      double x = this.offset.getX() * (double)((float)Math.cos((double)radians)) - -this.offset.getZ() * (double)((float)Math.sin((double)radians));
      double z = this.offset.getX() * (double)((float)Math.sin((double)radians)) + -this.offset.getZ() * (double)((float)Math.cos((double)radians));
      return new Vector(x, this.offset.getY(), z);
   }

   @NotNull
   public final Vector component1() {
      return this.offset;
   }

   public final boolean component2() {
      return this.skipNight;
   }

   public final boolean component3() {
      return this.resetPhantoms;
   }

   @NotNull
   public final FurnitureBed copy(@NotNull Vector offset, boolean skipNight, boolean resetPhantoms) {
      return new FurnitureBed(offset, skipNight, resetPhantoms);
   }

   @NotNull
   public String toString() {
      return "FurnitureBed(offset=" + this.offset + ", skipNight=" + this.skipNight + ", resetPhantoms=" + this.resetPhantoms + ")";
   }

   public int hashCode() {
      int result = this.offset.hashCode();
      result = result * 31 + Boolean.hashCode(this.skipNight);
      result = result * 31 + Boolean.hashCode(this.resetPhantoms);
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof FurnitureBed)) {
         return false;
      } else {
         FurnitureBed furnitureBed = (FurnitureBed)other;
         if (!Intrinsics.areEqual(this.offset, furnitureBed.offset)) {
            return false;
         } else if (this.skipNight != furnitureBed.skipNight) {
            return false;
         } else {
            return this.resetPhantoms == furnitureBed.resetPhantoms;
         }
      }
   }

   static {
      BED_KEY = new NamespacedKey((Plugin)NexoPlugin.Companion.instance(), "bed");
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000bJ(\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015J\u0016\u0010\u0016\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010\u0017\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0018"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed$Companion;", "", "<init>", "()V", "BED_KEY", "Lorg/bukkit/NamespacedKey;", "getBED_KEY", "()Lorg/bukkit/NamespacedKey;", "isBed", "", "entity", "Lorg/bukkit/entity/Entity;", "sleepOnBed", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "player", "Lorg/bukkit/entity/Player;", "interactionPoint", "Lorg/bukkit/Location;", "updateBeds", "removeBeds", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final NamespacedKey getBED_KEY() {
         return FurnitureBed.BED_KEY;
      }

      public final boolean isBed(@Nullable Entity entity) {
         return entity instanceof Interaction && ((Interaction)entity).getPersistentDataContainer().has(this.getBED_KEY(), DataType.UUID);
      }

      public final void sleepOnBed(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull Player player, @Nullable Location interactionPoint) {
         Location location = interactionPoint;
         if (interactionPoint == null) {
            location = baseEntity.getLocation();
         }

         location = location.toCenterLocation();
         Location centeredLoc = location;
         float yaw = baseEntity.getYaw();
         Iterable beds = (Iterable)mechanic.getBeds();
         Iterator iterator = beds.iterator();
         Object obj;
         if (!iterator.hasNext()) {
            obj = null;
         } else {
            Object minElem = iterator.next();
            if (!iterator.hasNext()) {
               obj = minElem;
            } else {
               FurnitureBed bed = (FurnitureBed)minElem;
               int value = 0;
               Location location2 = LocationUtilsKt.getTranslatedLocation(baseEntity).add(bed.offset(yaw));
               Location bedLoc = location2;
               double minValue = centeredLoc.distanceSquared(bedLoc);

               do {
                  Object obj2 = iterator.next();
                  FurnitureBed bed_4 = (FurnitureBed)obj2;
                  int value2 = 0;
                  location2 = LocationUtilsKt.getTranslatedLocation(baseEntity).add(bed_4.offset(yaw));
                  Location bedLoc_3 = location2;
                  double value3 = centeredLoc.distanceSquared(bedLoc_3);
                  if (Double.compare(minValue, value3) > 0) {
                     minElem = obj2;
                     minValue = value3;
                  }
               } while(iterator.hasNext());

               obj = minElem;
            }
         }

         FurnitureBed furnitureBed = (FurnitureBed)obj;
         if (furnitureBed != null) {
            FurnitureBed closestBed = furnitureBed;
            Location location3 = LocationUtilsKt.getTranslatedLocation(baseEntity).add(closestBed.offset(yaw));
            Location spawnLocation = location3;
            IFurnitureBedEntity bedEntity = NMSHandlers.handler().customEntityHandler().createBedEntity(baseEntity, mechanic, closestBed);
            if (bedEntity != null) {
               bedEntity.spawn(spawnLocation);
            }

            if (bedEntity != null) {
               Entity bukkitEntity = bedEntity.getBukkitEntity();
               if (bukkitEntity != null) {
                  bukkitEntity.addPassenger((Entity)player);
               }
            }

         }
      }

      public final void updateBeds(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic) {
         float yaw = baseEntity.getYaw();
         Collection nearbyEntitiesByType = baseEntity.getLocation().toCenterLocation().getNearbyEntitiesByType(Interaction.class, (double)3.0F, (a0_ -> Companion.updateBeds$lambda$1((b0_ -> Companion.updateBeds$lambda$0(mechanic, (Interaction)b0_)), a0_)));
         Iterable iterable = (Iterable)nearbyEntitiesByType;

         for(Object element : iterable) {
            Interaction entity = (Interaction)element;
            int value = 0;
            if (entity.getPassengers().isEmpty()) {
               entity.remove();
            } else {
               Location location = entity.getLocation();
               Location entityLoc = location;
               Iterable beds = (Iterable)mechanic.getBeds();
               Iterator iterator = beds.iterator();
               Object obj;
               if (!iterator.hasNext()) {
                  obj = null;
               } else {
                  Object minElem = iterator.next();
                  if (!iterator.hasNext()) {
                     obj = minElem;
                  } else {
                     FurnitureBed bed = (FurnitureBed)minElem;
                     int value2 = 0;
                     Location location2 = LocationUtilsKt.getTranslatedLocation(baseEntity).add(bed.offset(yaw));
                     Location targetLoc = location2;
                     double minValue = entityLoc.distanceSquared(targetLoc);

                     do {
                        Object obj2 = iterator.next();
                        FurnitureBed bed_2 = (FurnitureBed)obj2;
                        int value3 = 0;
                        location2 = LocationUtilsKt.getTranslatedLocation(baseEntity).add(bed_2.offset(yaw));
                        Location targetLoc_1 = location2;
                        double value4 = entityLoc.distanceSquared(targetLoc_1);
                        if (Double.compare(minValue, value4) > 0) {
                           minElem = obj2;
                           minValue = value4;
                        }
                     } while(iterator.hasNext());

                     obj = minElem;
                  }
               }

               FurnitureBed furnitureBed = (FurnitureBed)obj;
               if (furnitureBed == null) {
                  entity.remove();
               } else {
                  FurnitureBed closestBed = furnitureBed;
                  Location location3 = LocationUtilsKt.getTranslatedLocation(baseEntity).add(closestBed.offset(yaw));
                  Location newLoc = location3;
                  if (entityLoc.distanceSquared(newLoc) > 1.0E-4) {
                     entity.teleport(newLoc);
                  }
               }
            }
         }

      }

      public final void removeBeds(@NotNull ItemDisplay baseEntity) {
         Collection nearbyEntitiesByType = baseEntity.getLocation().toCenterLocation().getNearbyEntitiesByType(Interaction.class, (double)3.0F, (a0_ -> Companion.removeBeds$lambda$0((Function1)((p0) -> this.isBed((Entity)p0)), a0_)));
         Iterable iterable = (Iterable)nearbyEntitiesByType;

         for(Object element : iterable) {
            Interaction it = (Interaction)element;
            int value = 0;
            it.eject();
            it.remove();
         }

      }

      private static final boolean updateBeds$lambda$0(FurnitureMechanic mechanic, Interaction it) {
         return FurnitureBed.Companion.isBed((Entity)it) && Intrinsics.areEqual(it.getPersistentDataContainer().get(FurnitureMechanic.Companion.getFURNITURE_KEY(), PersistentDataType.STRING), mechanic.getItemID());
      }

      private static final boolean updateBeds$lambda$1(Function1 tmp0, Object p0) {
         return (Boolean)tmp0.invoke(p0);
      }

      private static final boolean removeBeds$lambda$0(Function1 tmp0, Object p0) {
         return (Boolean)tmp0.invoke(p0);
      }
   }
}
