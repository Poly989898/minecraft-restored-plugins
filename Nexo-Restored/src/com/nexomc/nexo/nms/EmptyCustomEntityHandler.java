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
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0016J\"\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\b\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0016J\"\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\b\u001a\u00020\u000e2\u0006\u0010\u0013\u001a\u00020\u0014H\u0016¨\u0006\u0015"},
   d2 = {"Lcom/nexomc/nexo/nms/EmptyCustomEntityHandler;", "Lcom/nexomc/nexo/nms/ICustomEntityHandler;", "<init>", "()V", "createTridentEntity", "Lcom/nexomc/nexo/mechanics/trident/ICustomTridentEntity;", "tridentProjectile", "Lorg/bukkit/entity/Trident;", "mechanic", "Lcom/nexomc/nexo/mechanics/trident/TridentMechanic;", "createBedEntity", "Lcom/nexomc/nexo/mechanics/furniture/bed/IFurnitureBedEntity;", "baseEntity", "Lorg/bukkit/entity/ItemDisplay;", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "bed", "Lcom/nexomc/nexo/mechanics/furniture/bed/FurnitureBed;", "createSeatEntity", "Lcom/nexomc/nexo/mechanics/furniture/seats/IFurnitureSeatEntity;", "seat", "Lcom/nexomc/nexo/mechanics/furniture/seats/FurnitureSeat;", "com.nexomc:core"}
)
public final class EmptyCustomEntityHandler implements ICustomEntityHandler {
   @Nullable
   public ICustomTridentEntity createTridentEntity(@NotNull Trident tridentProjectile, @NotNull TridentMechanic mechanic) {
      return null;
   }

   @Nullable
   public IFurnitureBedEntity createBedEntity(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull FurnitureBed bed) {
      return null;
   }

   @Nullable
   public IFurnitureSeatEntity createSeatEntity(@NotNull ItemDisplay baseEntity, @NotNull FurnitureMechanic mechanic, @NotNull FurnitureSeat seat) {
      return null;
   }
}
