package com.nexomc.nexo.nms.v1_21_R10;

import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBed;
import com.nexomc.nexo.mechanics.furniture.bed.IFurnitureBedPacket;
import com.nexomc.nexo.nms.commons.furniture.bed.FurnitureBedEntity;
import com.nexomc.nexo.nms.v1_21_R10.furniture.bed.FurnitureBedMannequin;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J(\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016¨\u0006\u000e"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R10/EntityUtils;", "Lcom/nexomc/nexo/nms/commons/EntityUtils;", "<init>", "()V", "createBedPacket", "Lcom/nexomc/nexo/mechanics/furniture/bed/IFurnitureBedPacket;", "bedPlayer", "Lorg/bukkit/entity/Player;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "bedEntity", "Lcom/nexomc/nexo/nms/commons/furniture/bed/FurnitureBedEntity;", "bed", "Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "com.nexomc:v1_21_R10"}
)
public final class EntityUtils extends com.nexomc.nexo.nms.commons.EntityUtils {
   @NotNull
   public IFurnitureBedPacket createBedPacket(@NotNull Player bedPlayer, @NotNull ItemDisplay baseEntity, @NotNull FurnitureBedEntity bedEntity, @NotNull FurnitureBed bed) {
      Intrinsics.checkNotNullParameter(bedPlayer, "bedPlayer");
      Intrinsics.checkNotNullParameter(baseEntity, "baseEntity");
      Intrinsics.checkNotNullParameter(bedEntity, "bedEntity");
      Intrinsics.checkNotNullParameter(bed, "bed");
      return new FurnitureBedMannequin(bedPlayer, baseEntity, bedEntity, bed);
   }
}
