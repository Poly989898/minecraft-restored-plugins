package com.nexomc.nexo.mechanics.custom_block.noteblock.logstrip;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import org.bukkit.Material;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u001a\u001a\u00020\u0007J\u0006\u0010\u001b\u001a\u00020\u0007R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001d\u0010\n\u001a\u0004\u0018\u00010\u000b8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000e\u0010\u000f\u001a\u0004\b\f\u0010\rR\u001d\u0010\u0010\u001a\u0004\u0018\u00010\u00118FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0014\u0010\u000f\u001a\u0004\b\u0012\u0010\u0013R\u001b\u0010\u0015\u001a\u00020\u00168FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0019\u0010\u000f\u001a\u0004\b\u0017\u0010\u0018¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/logstrip/LogStripping;", "", "logStripSection", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "decreaseAxeDurability", "", "getDecreaseAxeDurability", "()Z", "stripMechanic", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "getStripMechanic", "()Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "stripMechanic$delegate", "Lkotlin/Lazy;", "stripBlock", "Lorg/bukkit/block/data/type/NoteBlock;", "getStripBlock", "()Lorg/bukkit/block/data/type/NoteBlock;", "stripBlock$delegate", "logDrop", "Lorg/bukkit/inventory/ItemStack;", "getLogDrop", "()Lorg/bukkit/inventory/ItemStack;", "logDrop$delegate", "canBeStripped", "hasStrippedDrop", "com.nexomc:core"}
)
public final class LogStripping {
   private final boolean decreaseAxeDurability;
   @NotNull
   private final Lazy stripMechanic$delegate;
   @NotNull
   private final Lazy stripBlock$delegate;
   @NotNull
   private final Lazy logDrop$delegate;

   public LogStripping(@NotNull ConfigurationSection logStripSection) {
      super();
      this.decreaseAxeDurability = logStripSection.getBoolean("decrease_axe_durability");
      this.stripMechanic$delegate = LazyKt.lazy((() -> LogStripping.stripMechanic_delegate$lambda$0(logStripSection)));
      this.stripBlock$delegate = LazyKt.lazy((() -> LogStripping.stripBlock_delegate$lambda$0(this)));
      this.logDrop$delegate = LazyKt.lazy((() -> LogStripping.logDrop_delegate$lambda$0(logStripSection)));
   }

   public final boolean getDecreaseAxeDurability() {
      return this.decreaseAxeDurability;
   }

   @Nullable
   public final NoteBlockMechanic getStripMechanic() {
      Lazy lazy = this.stripMechanic$delegate;
      return (NoteBlockMechanic)lazy.getValue();
   }

   @Nullable
   public final NoteBlock getStripBlock() {
      Lazy lazy = this.stripBlock$delegate;
      return (NoteBlock)lazy.getValue();
   }

   @NotNull
   public final ItemStack getLogDrop() {
      Lazy lazy = this.logDrop$delegate;
      return (ItemStack)lazy.getValue();
   }

   public final boolean canBeStripped() {
      return this.getStripBlock() != null;
   }

   public final boolean hasStrippedDrop() {
      return !this.getLogDrop().isEmpty();
   }

   private static final NoteBlockMechanic stripMechanic_delegate$lambda$0(ConfigurationSection logStripSection) {
      return NexoBlocks.noteBlockMechanic(logStripSection.getString("stripped_log"));
   }

   private static final NoteBlock stripBlock_delegate$lambda$0(LogStripping logStripping) {
      NoteBlockMechanic stripMechanic = logStripping.getStripMechanic();
      return stripMechanic != null ? stripMechanic.getBlockData() : null;
   }

   private static final ItemStack logDrop_delegate$lambda$0(ConfigurationSection logStripSection) {
      ItemBuilder itemBuilder = NexoItems.itemFromId(logStripSection.getString("drop"));
      ItemStack itemStack;
      if (itemBuilder != null) {
         itemStack = itemBuilder.build();
         if (itemStack != null) {
            return itemStack;
         }
      }

      itemStack = ItemStack.of(Material.AIR);
      return itemStack;
   }
}
