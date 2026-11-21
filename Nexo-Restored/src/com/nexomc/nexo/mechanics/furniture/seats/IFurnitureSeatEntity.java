package com.nexomc.nexo.mechanics.furniture.seats;

import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.utils.LocationUtilsKt;
import kotlin.Metadata;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0012\u001a\u00020\u0013H&R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005R\u0012\u0010\u0006\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\tR\u0012\u0010\n\u001a\u00020\u000bX¦\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0014\u0010\u000e\u001a\u00020\u000f8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u0014À\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/seats/IFurnitureSeatEntity;", "", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "getBaseEntity", "()Lorg/bukkit/entity/ItemDisplay;", "mechanic", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "getMechanic", "()Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "seat", "Lcom/nexomc/nexo/mechanics/furniture/seats/FurnitureSeat;", "getSeat", "()Lcom/nexomc/nexo/mechanics/furniture/seats/FurnitureSeat;", "seatLocation", "Lorg/bukkit/Location;", "getSeatLocation", "()Lorg/bukkit/Location;", "spawn", "Lorg/bukkit/entity/Interaction;", "com.nexomc:core"}
)
public interface IFurnitureSeatEntity {
   @NotNull
   ItemDisplay getBaseEntity();

   @NotNull
   FurnitureMechanic getMechanic();

   @NotNull
   FurnitureSeat getSeat();

   @NotNull
   default Location getSeatLocation() {
      Location location = LocationUtilsKt.getTranslatedLocation(this.getBaseEntity()).add(this.getSeat().offset(this.getBaseEntity().getYaw()));
      return location;
   }

   @NotNull
   Interaction spawn();

   @Metadata(
      mv = {2, 4, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      /** @deprecated */
      @Deprecated
      @NotNull
      public static Location getSeatLocation(@NotNull IFurnitureSeatEntity iFurnitureSeatEntity) {
         return iFurnitureSeatEntity.getSeatLocation();
      }
   }
}
