package com.ticxo.modelengine.v1_21_R4.entity;

import java.util.List;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftEntity;

public class EntityUtils {
   public static final List<DataValue<?>> DEFAULT_AREA_EFFECT_CLOUD_DATA = List.of(
      new DataValue(0, EntityDataSerializers.BYTE, (byte)32), new DataValue(8, EntityDataSerializers.FLOAT, 0.0F)
   );
   public static final List<DataValue<?>> DEFAULT_SLIME_DATA = List.of(
      new DataValue(0, EntityDataSerializers.BYTE, (byte)32),
      new DataValue(1, EntityDataSerializers.INT, Integer.MAX_VALUE),
      new DataValue(16, EntityDataSerializers.INT, 2)
   );
   public static final List<DataValue<?>> DEFAULT_BAT_DATA = List.of(
      new DataValue(0, EntityDataSerializers.BYTE, (byte)32), new DataValue(1, EntityDataSerializers.INT, Integer.MAX_VALUE)
   );
   public static final List<DataValue<?>> DEFAULT_ARMOR_STAND_DATA = List.of(
      new DataValue(0, EntityDataSerializers.BYTE, (byte)32),
      new DataValue(1, EntityDataSerializers.INT, Integer.MAX_VALUE),
      new DataValue(15, EntityDataSerializers.BYTE, (byte)16)
   );

   public static Entity nms(org.bukkit.entity.Entity entity) {
      return ((CraftEntity)entity).getHandleRaw();
   }
}
