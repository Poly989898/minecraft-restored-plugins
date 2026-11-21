package com.nexomc.nexo.mechanics.farming.harvesting;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \r2\u00020\u0001:\u0001\rB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0002\u001a\u00020\u0003H\u0016J\u0014\u0010\b\u001a\u0004\u0018\u00010\u00072\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016J\u0014\u0010\b\u001a\u0004\u0018\u00010\u00072\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016¨\u0006\u000e"},
   d2 = {"Lcom/nexomc/nexo/mechanics/farming/harvesting/HarvestingMechanicFactory;", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "parse", "Lcom/nexomc/nexo/mechanics/farming/harvesting/HarvestingMechanic;", "getMechanic", "itemID", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "Companion", "com.nexomc:core"}
)
public final class HarvestingMechanicFactory extends MechanicFactory {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private static HarvestingMechanicFactory instance;

   public HarvestingMechanicFactory(@NotNull ConfigurationSection section) {
      super(section);
      instance = this;
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      JavaPlugin javaPlugin = NexoPlugin.Companion.instance();
      String mechanicID = this.getMechanicID();
      Listener[] listeners = new Listener[]{new HarvestingMechanicListener()};
      mechanicsManager.registerListeners(javaPlugin, mechanicID, listeners);
   }

   @NotNull
   public HarvestingMechanic parse(@NotNull ConfigurationSection section) {
      HarvestingMechanic harvestingMechanic = new HarvestingMechanic(this, section);
      Mechanic p0 = harvestingMechanic;
      int value = 0;
      this.addToImplemented(p0);
      return harvestingMechanic;
   }

   @Nullable
   public HarvestingMechanic getMechanic(@Nullable String itemID) {
      Mechanic mechanic = super.getMechanic(itemID);
      return mechanic instanceof HarvestingMechanic ? (HarvestingMechanic)mechanic : null;
   }

   @Nullable
   public HarvestingMechanic getMechanic(@Nullable ItemStack itemStack) {
      Mechanic mechanic = super.getMechanic(itemStack);
      return mechanic instanceof HarvestingMechanic ? (HarvestingMechanic)mechanic : null;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JJ\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0007b0\b\u0006\u0012\b\b\u0007\u0012\u0004\b\b(\b\u0012\"\b\t\u0012\u001e\b\u000bB\u001a\b\n\u0012\b\b\u000b\u0012\u0004\b\b(\f\u0012\f\b\r\u0012\b\b\fJ\u0004\b\b(\u000eb\f\b\u000f\u0012\b\b\u0010\u0012\u0004\b\b(\u0011R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0012"},
      d2 = {"Lcom/nexomc/nexo/mechanics/farming/harvesting/HarvestingMechanicFactory$Companion;", "", "<init>", "()V", "instance", "Lcom/nexomc/nexo/mechanics/farming/harvesting/HarvestingMechanicFactory;", "Lkotlin/Deprecated;", "message", "Use MechanicsManager.factory<HarvestingMechanicFactory>() instead.", "replaceWith", "Lkotlin/ReplaceWith;", "expression", "MechanicsManager.factory<HarvestingMechanicFactory>()", "imports", "com.nexomc.nexo.mechanics.MechanicsManager", "Lorg/jetbrains/annotations/ApiStatus$ScheduledForRemoval;", "inVersion", "1.24.0", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      /** @deprecated */
      @Deprecated(
         message = "Use MechanicsManager.factory<HarvestingMechanicFactory>() instead.",
         replaceWith = @ReplaceWith(
   expression = "MechanicsManager.factory<HarvestingMechanicFactory>()",
   imports = {"com.nexomc.nexo.mechanics.MechanicsManager"}
)
      )
      @ScheduledForRemoval(
         inVersion = "1.24.0"
      )
      @Nullable
      public final HarvestingMechanicFactory instance() {
         return HarvestingMechanicFactory.instance;
      }
   }
}
