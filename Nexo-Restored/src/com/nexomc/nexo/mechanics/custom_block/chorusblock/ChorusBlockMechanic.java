package com.nexomc.nexo.mechanics.custom_block.chorusblock;

import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import kotlin.Metadata;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\u000f\u001a\u00020\fH\u0016R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\nR\u0014\u0010\u000b\u001a\u00020\fX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockMechanic;", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "factory", "Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/chorusblock/ChorusBlockFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "isFalling", "", "()Z", "blockData", "Lorg/bukkit/block/data/MultipleFacing;", "getBlockData", "()Lorg/bukkit/block/data/MultipleFacing;", "createBlockData", "com.nexomc:core"}
)
public final class ChorusBlockMechanic extends CustomBlockMechanic {
   private final boolean isFalling;
   @NotNull
   private final MultipleFacing blockData;

   public ChorusBlockMechanic(@NotNull ChorusBlockFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      this.isFalling = section.getBoolean("is_falling", false);
      BlockData blockData2 = super.getBlockData();
      this.blockData = (MultipleFacing)blockData2;
   }

   public final boolean isFalling() {
      return this.isFalling;
   }

   @NotNull
   public MultipleFacing getBlockData() {
      return this.blockData;
   }

   @NotNull
   public MultipleFacing createBlockData() {
      return ChorusMechanicHelpers.INSTANCE.blockData(this.getCustomVariation());
   }
}
