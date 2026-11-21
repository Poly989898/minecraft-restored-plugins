package com.nexomc.nexo.mechanics.trident;

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
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0014\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016J\u0014\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0016J\u0010\u0010\f\u001a\u00020\u00072\u0006\u0010\u0002\u001a\u00020\u0003H\u0016¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/mechanics/trident/TridentFactory;", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getMechanic", "Lcom/nexomc/nexo/mechanics/trident/TridentMechanic;", "itemId", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "parse", "com.nexomc:core"}
)
public final class TridentFactory extends MechanicFactory {
   public TridentFactory(@NotNull ConfigurationSection section) {
      super(section);
      Listener[] listeners = new Listener[]{new CustomTridentListener()};
      this.registerListeners(listeners);
   }

   @Nullable
   public TridentMechanic getMechanic(@Nullable String itemId) {
      Object mechanic = super.getMechanic(itemId);
      Object obj = mechanic;
      if (!(mechanic instanceof TridentMechanic)) {
         obj = null;
      }

      return (TridentMechanic)obj;
   }

   @Nullable
   public TridentMechanic getMechanic(@Nullable ItemStack itemStack) {
      Object mechanic = super.getMechanic(itemStack);
      Object obj = mechanic;
      if (!(mechanic instanceof TridentMechanic)) {
         obj = null;
      }

      return (TridentMechanic)obj;
   }

   @NotNull
   public TridentMechanic parse(@NotNull ConfigurationSection section) {
      TridentMechanic tridentMechanic = new TridentMechanic(this, section);
      Mechanic p0 = tridentMechanic;
      int value = 0;
      this.addToImplemented(p0);
      return tridentMechanic;
   }
}
