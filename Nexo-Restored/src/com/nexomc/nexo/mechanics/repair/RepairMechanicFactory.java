package com.nexomc.nexo.mechanics.repair;

import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import kotlin.Metadata;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0014\u0010\t\u001a\u0004\u0018\u00010\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016J\u0014\u0010\t\u001a\u0004\u0018\u00010\n2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016J\u0010\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u0003H\u0016R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\b¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/mechanics/repair/RepairMechanicFactory;", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "isNexoDurabilityOnly", "", "()Z", "getMechanic", "Lcom/nexomc/nexo/mechanics/repair/RepairMechanic;", "itemID", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "parse", "mechanicSection", "com.nexomc:core"}
)
public final class RepairMechanicFactory extends MechanicFactory {
   private final boolean isNexoDurabilityOnly;

   public RepairMechanicFactory(@NotNull ConfigurationSection section) {
      super(section);
      this.isNexoDurabilityOnly = section.getBoolean("nexo_durability_only");
      Listener[] listeners = new Listener[]{new RepairMechanicListener(this)};
      this.registerListeners(listeners);
   }

   public final boolean isNexoDurabilityOnly() {
      return this.isNexoDurabilityOnly;
   }

   @Nullable
   public RepairMechanic getMechanic(@Nullable String itemID) {
      Mechanic mechanic = super.getMechanic(itemID);
      return mechanic instanceof RepairMechanic ? (RepairMechanic)mechanic : null;
   }

   @Nullable
   public RepairMechanic getMechanic(@Nullable ItemStack itemStack) {
      Mechanic mechanic = super.getMechanic(itemStack);
      return mechanic instanceof RepairMechanic ? (RepairMechanic)mechanic : null;
   }

   @NotNull
   public RepairMechanic parse(@NotNull ConfigurationSection mechanicSection) {
      RepairMechanic repairMechanic = new RepairMechanic(this, mechanicSection);
      Mechanic p0 = repairMechanic;
      int value = 0;
      this.addToImplemented(p0);
      return repairMechanic;
   }
}
