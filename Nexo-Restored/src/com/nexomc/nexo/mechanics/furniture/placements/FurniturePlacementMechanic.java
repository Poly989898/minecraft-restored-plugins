package com.nexomc.nexo.mechanics.furniture.placements;

import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import kotlin.Metadata;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/placements/FurniturePlacementMechanic;", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "factory", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/MechanicFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "com.nexomc:core"}
)
public final class FurniturePlacementMechanic extends FurnitureMechanic {
   public FurniturePlacementMechanic(@NotNull MechanicFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      this.setPlacements$com_nexomc_core((FurniturePlacement)null);
   }
}
