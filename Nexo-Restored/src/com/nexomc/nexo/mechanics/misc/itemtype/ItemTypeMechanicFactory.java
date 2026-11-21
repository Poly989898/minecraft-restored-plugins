package com.nexomc.nexo.mechanics.misc.itemtype;

import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \r2\u00020\u0001:\u0001\rB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0002\u001a\u00020\u0003H\u0016J\u0014\u0010\b\u001a\u0004\u0018\u00010\u00072\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016J\u0014\u0010\b\u001a\u0004\u0018\u00010\u00072\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016¨\u0006\u000e"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/itemtype/ItemTypeMechanicFactory;", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "parse", "Lcom/nexomc/nexo/mechanics/misc/itemtype/ItemTypeMechanic;", "getMechanic", "itemID", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "Companion", "com.nexomc:core"}
)
public final class ItemTypeMechanicFactory extends MechanicFactory {
   @NotNull
   public static final Companion Companion = new Companion();
   private static ItemTypeMechanicFactory instance;

   public ItemTypeMechanicFactory(@NotNull ConfigurationSection section) {
      super(section);
      instance = this;
   }

   @NotNull
   public ItemTypeMechanic parse(@NotNull ConfigurationSection section) {
      ItemTypeMechanic itemTypeMechanic = new ItemTypeMechanic(this, section, java.util.Objects.requireNonNull(section.getString("value")));
      Mechanic p0 = itemTypeMechanic;
      int value = 0;
      this.addToImplemented(p0);
      return itemTypeMechanic;
   }

   @Nullable
   public ItemTypeMechanic getMechanic(@Nullable String itemID) {
      Mechanic mechanic = super.getMechanic(itemID);
      return mechanic instanceof ItemTypeMechanic ? (ItemTypeMechanic)mechanic : null;
   }

   @Nullable
   public ItemTypeMechanic getMechanic(@Nullable ItemStack itemStack) {
      Mechanic mechanic = super.getMechanic(itemStack);
      return mechanic instanceof ItemTypeMechanic ? (ItemTypeMechanic)mechanic : null;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lcom/nexomc/nexo/mechanics/misc/itemtype/ItemTypeMechanicFactory$Companion;", "", "<init>", "()V", "instance", "Lcom/nexomc/nexo/mechanics/misc/itemtype/ItemTypeMechanicFactory;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final ItemTypeMechanicFactory instance() {
         ItemTypeMechanicFactory itemTypeMechanicFactory = ItemTypeMechanicFactory.instance;
         if (itemTypeMechanicFactory == null) {
            Intrinsics.throwUninitializedPropertyAccessException("instance");
            itemTypeMechanicFactory = null;
         }

         return itemTypeMechanicFactory;
      }
   }
}
