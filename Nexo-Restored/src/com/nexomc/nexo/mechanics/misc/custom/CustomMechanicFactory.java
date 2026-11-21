package com.nexomc.nexo.mechanics.misc.custom;

import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import kotlin.Metadata;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0002\u001a\u00020\u0003H\u0016J\u0014\u0010\b\u001a\u0004\u0018\u00010\u00072\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016J\u0014\u0010\b\u001a\u0004\u0018\u00010\u00072\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/custom/CustomMechanicFactory;", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "parse", "Lcom/nexomc/nexo/mechanics/misc/custom/CustomMechanic;", "getMechanic", "itemID", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "com.nexomc:core"}
)
public final class CustomMechanicFactory extends MechanicFactory {
   public CustomMechanicFactory(@NotNull ConfigurationSection section) {
      super(section);
   }

   @NotNull
   public CustomMechanic parse(@NotNull ConfigurationSection section) {
      CustomMechanic customMechanic = new CustomMechanic(this, section);
      Mechanic p0 = customMechanic;
      int value = 0;
      this.addToImplemented(p0);
      return customMechanic;
   }

   @Nullable
   public CustomMechanic getMechanic(@Nullable String itemID) {
      Mechanic mechanic = super.getMechanic(itemID);
      return mechanic instanceof CustomMechanic ? (CustomMechanic)mechanic : null;
   }

   @Nullable
   public CustomMechanic getMechanic(@Nullable ItemStack itemStack) {
      Mechanic mechanic = super.getMechanic(itemStack);
      return mechanic instanceof CustomMechanic ? (CustomMechanic)mechanic : null;
   }
}
