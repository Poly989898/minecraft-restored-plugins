package com.nexomc.nexo.mechanics.furniture;

import com.nexomc.libs.morepersistentdatatypes.datatypes.serializable.ConfigurationSerializableDataType;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.TuplesKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Triple;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.text.StringsKt;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import org.joml.Vector3f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010%\n\u0002\b\u0003\b\u0016\u0018\u0000 12\u00020\u0001:\u00011B%\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003¢\u0006\u0004\b\u0006\u0010\u0007B\u0011\b\u0016\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\u0006\u0010\nB\u0011\b\u0016\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\u0006\u0010\rB\u0011\b\u0016\u0012\u0006\u0010\b\u001a\u00020\u000e¢\u0006\u0004\b\u0006\u0010\u000fJ\n\u0010\u0018\u001a\u00020\u000eH\u0096\u0080\u0004J\u0006\u0010\u0019\u001a\u00020\u001aJ\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u0000J\u000e\u0010 \u001a\u00020\u00002\u0006\u0010!\u001a\u00020\u0000J\u001e\u0010 \u001a\u00020\u00002\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0003J\u0013\u0010 \u001a\u00070\t¢\u0006\u0002\b\"2\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010#\u001a\u00020\u00002\u0006\u0010!\u001a\u00020\u0000J\u001e\u0010#\u001a\u00020\u00002\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0003J\u0013\u0010#\u001a\u00070\t¢\u0006\u0002\b\"2\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010$\u001a\u00020\t2\u0006\u0010%\u001a\u00020&J\u000e\u0010'\u001a\u00020\u00002\u0006\u0010(\u001a\u00020)J\u0014\u0010*\u001a\u00020+2\b\u0010,\u001a\u0004\u0018\u00010-H\u0096\u0082\u0004J\u0014\u0010.\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00030/H\u0016J\n\u00100\u001a\u00020\u0003H\u0096\u0080\u0004R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0011\"\u0004\b\u0015\u0010\u0013R\u001a\u0010\u0005\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0011\"\u0004\b\u0017\u0010\u0013¨\u00062"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/BlockLocation;", "Lorg/bukkit/configuration/serialization/ConfigurationSerializable;", "x", "", "y", "z", "<init>", "(III)V", "location", "Lorg/bukkit/Location;", "(Lorg/bukkit/Location;)V", "block", "Lorg/bukkit/block/Block;", "(Lorg/bukkit/block/Block;)V", "", "(Ljava/lang/String;)V", "getX", "()I", "setX", "(I)V", "getY", "setY", "getZ", "setZ", "toString", "toVector3f", "Lorg/joml/Vector3f;", "toVector", "Lorg/bukkit/util/Vector;", "distanceTo", "", "other", "add", "blockLocation", "Lorg/jetbrains/annotations/NotNull;", "subtract", "toLocation", "world", "Lorg/bukkit/World;", "groundRotate", "angle", "", "equals", "", "obj", "", "serialize", "", "hashCode", "Companion", "com.nexomc:core"}
)
public class BlockLocation implements ConfigurationSerializable {
   @NotNull
   public static final Companion Companion = new Companion();
   private int x;
   private int y;
   private int z;
   @NotNull
   private static ConfigurationSerializableDataType<BlockLocation> dataType = new ConfigurationSerializableDataType<BlockLocation>(BlockLocation.class);
   @NotNull
   private static BlockLocation ZERO = new BlockLocation(0, 0, 0);

   public BlockLocation(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public final int getX() {
      return this.x;
   }

   public final void setX(int value) {
      this.x = value;
   }

   public final int getY() {
      return this.y;
   }

   public final void setY(int value) {
      this.y = value;
   }

   public final int getZ() {
      return this.z;
   }

   public final void setZ(int value) {
      this.z = value;
   }

   public BlockLocation(@NotNull Location location) {
      this(location.getBlockX(), location.getBlockY(), location.getBlockZ());
   }

   public BlockLocation(@NotNull Block block) {
      this(block.getX(), block.getY(), block.getZ());
   }

   public BlockLocation(@NotNull String location) {
      this(0, 0, 0);
      CharSequence charSequence = (CharSequence)location;
      String[] strs = new String[]{","};
      Iterable iterable = (Iterable)StringsKt.split(charSequence, strs, false, 0);
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         String it = (String)item;
         int value = 0;
         Integer number = StringsKt.toIntOrNull(StringUtilsKt.removeSpaces(it));
         destination.add(number != null ? number : 0);
      }

      List split = CollectionsKt.toMutableList((Collection)((List)destination));

      while(split.size() < 3) {
         ((Collection)split).add(0);
      }

      this.x = ((Number)split.get(0)).intValue();
      this.y = ((Number)split.get(1)).intValue();
      this.z = ((Number)split.get(2)).intValue();
   }

   @NotNull
   public String toString() {
      return this.x + "," + this.y + "," + this.z;
   }

   @NotNull
   public final Vector3f toVector3f() {
      return new Vector3f((float)this.x, (float)this.y, (float)this.z);
   }

   @NotNull
   public final Vector toVector() {
      return new Vector(this.x, this.y, this.z);
   }

   public final double distanceTo(@NotNull BlockLocation other) {
      Triple triple = TuplesKt.to(kotlin.TuplesKt.to((double)this.x, (double)this.y), (double)this.z);
      double x = ((Number)triple.component1()).doubleValue();
      double y = ((Number)triple.component2()).doubleValue();
      double z = ((Number)triple.component3()).doubleValue();
      return NumberConversions.square(x - (double)other.x) + NumberConversions.square(y - (double)other.y) + NumberConversions.square(z - (double)other.z);
   }

   @NotNull
   public final BlockLocation add(@NotNull BlockLocation blockLocation) {
      this.x += blockLocation.x;
      this.y += blockLocation.y;
      this.z += blockLocation.z;
      return this;
   }

   @NotNull
   public final BlockLocation add(int x, int y, int z) {
      this.x += x;
      this.y += y;
      this.z += z;
      return this;
   }

   @NotNull
   public final Location add(@NotNull Location location) {
      Location location2 = location.clone().add((double)this.x, (double)this.y, (double)this.z);
      return location2;
   }

   @NotNull
   public final BlockLocation subtract(@NotNull BlockLocation blockLocation) {
      this.x -= blockLocation.x;
      this.y -= blockLocation.y;
      this.z -= blockLocation.z;
      return this;
   }

   @NotNull
   public final BlockLocation subtract(int x, int y, int z) {
      this.x -= x;
      this.y -= y;
      this.z -= z;
      return this;
   }

   @NotNull
   public final Location subtract(@NotNull Location location) {
      Location location2 = location.clone().subtract((double)this.x, (double)this.y, (double)this.z);
      return location2;
   }

   @NotNull
   public final Location toLocation(@NotNull World world) {
      return new Location(world, (double)this.x, (double)this.y, (double)this.z);
   }

   @NotNull
   public final BlockLocation groundRotate(float angle) {
      float angle_1 = angle;
      if (angle_1 < 0.0F) {
         angle_1 = angle_1 + 360.0F;
      }

      double radians = (double)Math.toRadians(angle_1);
      int newX = (int)java.lang.Math.rint((double)this.x * java.lang.Math.cos(radians) - (double)(-this.z) * java.lang.Math.sin(radians));
      int newZ = (int)java.lang.Math.rint((double)this.x * java.lang.Math.sin(radians) + (double)(-this.z) * java.lang.Math.cos(radians));
      return new BlockLocation(newX, this.y, newZ);
   }

   public boolean equals(@Nullable Object obj) {
      return obj instanceof BlockLocation && ((BlockLocation)obj).x == this.x && ((BlockLocation)obj).y == this.y && ((BlockLocation)obj).z == this.z;
   }

   @NotNull
   public Map<String, Object> serialize() {
      Map<String, Object> serialized = new java.util.LinkedHashMap<>();
      serialized.put("x", this.x);
      serialized.put("y", this.y);
      serialized.put("z", this.z);
      return serialized;
   }

   public int hashCode() {
      return this.getClass().hashCode();
   }

   public BlockLocation() {
      this(0, 0, 0);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R(\u0010\u0004\u001a\u0010\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00060\u00060\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001a\u0010\f\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010¨\u0006\u0011"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/BlockLocation$Companion;", "", "<init>", "()V", "dataType", "Lcom/nexomc/libs/morepersistentdatatypes/datatypes/serializable/ConfigurationSerializableDataType;", "Lcom/nexomc/nexo/mechanics/furniture/BlockLocation;", "kotlin.jvm.PlatformType", "getDataType", "()Lcom/nexomc/libs/morepersistentdatatypes/datatypes/serializable/ConfigurationSerializableDataType;", "setDataType", "(Lcom/nexomc/libs/morepersistentdatatypes/datatypes/serializable/ConfigurationSerializableDataType;)V", "ZERO", "getZERO", "()Lcom/nexomc/nexo/mechanics/furniture/BlockLocation;", "setZERO", "(Lcom/nexomc/nexo/mechanics/furniture/BlockLocation;)V", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final ConfigurationSerializableDataType<BlockLocation> getDataType() {
         return BlockLocation.dataType;
      }

      public final void setDataType(@NotNull ConfigurationSerializableDataType<BlockLocation> configurationSerializableDataType) {
         BlockLocation.dataType = configurationSerializableDataType;
      }

      @NotNull
      public final BlockLocation getZERO() {
         return BlockLocation.ZERO;
      }

      public final void setZERO(@NotNull BlockLocation blockLocation) {
         BlockLocation.ZERO = blockLocation;
      }
   }
}
