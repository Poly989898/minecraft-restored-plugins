package com.nexomc.nexo.nms;

import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBed;
import com.nexomc.nexo.mechanics.furniture.bed.IFurnitureBedEntity;
import com.nexomc.nexo.mechanics.furniture.seats.FurnitureSeat;
import com.nexomc.nexo.mechanics.furniture.seats.IFurnitureSeatEntity;
import com.nexomc.nexo.mechanics.trident.ICustomTridentEntity;
import com.nexomc.nexo.mechanics.trident.TridentMechanic;
import kotlin.Metadata;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Trident;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001a\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\"\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0006\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH&J\"\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0006\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\"\u0010\u0013\u001a\u0004\u0018\u00010\u00102\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0006\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0016¨\u0006\u0014À\u0006\u0003"},
   d2 = {"Lcom/nexomc/nexo/nms/ICustomEntityHandler;", "", "createTridentEntity", "Lcom/nexomc/nexo/mechanics/trident/ICustomTridentEntity;", "tridentProjectile", "Lorg/bukkit/entity/Trident;", "mechanic", "Lcom/nexomc/nexo/mechanics/trident/TridentMechanic;", "createBedEntity", "Lcom/nexomc/nexo/mechanics/furniture/bed/IFurnitureBedEntity;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "bed", "Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "createSeatEntity", "Lcom/nexomc/nexo/mechanics/furniture/seats/IFurnitureSeatEntity;", "seat", "Lcom/nexomc/nexo/mechanics/furniture/seats/FurnitureSeat;", "getOrCreateSeatEntity", "com.nexomc:core"}
)
public interface ICustomEntityHandler {
   @Nullable
   ICustomTridentEntity createTridentEntity(@NotNull Trident trident, @NotNull TridentMechanic tridentMechanic);

   @Nullable
   IFurnitureBedEntity createBedEntity(@NotNull ItemDisplay itemDisplay, @NotNull FurnitureMechanic furnitureMechanic, @NotNull FurnitureBed furnitureBed);

   @Nullable
   default IFurnitureSeatEntity createSeatEntity(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull FurnitureSeat seat) {
      return null;
   }

   @Nullable
   default IFurnitureSeatEntity getOrCreateSeatEntity(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull FurnitureSeat seat) {
      return null;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 3,
      xi = 48
   )
   public static final class DefaultImpls {
      /** @deprecated */
      @Deprecated
      @Nullable
      public static IFurnitureSeatEntity createSeatEntity(@NotNull ICustomEntityHandler iCustomEntityHandler, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull FurnitureSeat seat) {
         return iCustomEntityHandler.createSeatEntity(baseEntity, mechanic, seat);
      }

      /** @deprecated */
      @Deprecated
      @Nullable
      public static IFurnitureSeatEntity getOrCreateSeatEntity(@NotNull ICustomEntityHandler iCustomEntityHandler, @NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull FurnitureSeat seat) {
         return iCustomEntityHandler.getOrCreateSeatEntity(baseEntity, mechanic, seat);
      }
   }
}
