package net.Indyuce.mmoitems.comp.mmocore.load;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Objects;
import net.Indyuce.mmocore.api.block.BlockType;
import net.Indyuce.mmocore.api.block.BlockInfo.RegeneratingBlock;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.block.CustomBlock;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class MMOItemsBlockType implements BlockType {
   private final int id;

   public MMOItemsBlockType(MMOLineConfig mmoLineConfig) {
      mmoLineConfig.validate(new String[]{"id"});
      this.id = mmoLineConfig.getInt("id");
   }

   public MMOItemsBlockType(CustomBlock customBlock) {
      this.id = customBlock.getId();
   }

   public int getBlockId() {
      return this.id;
   }

   public static boolean matches(Block block) {
      return MMOItems.plugin.getCustomBlocks().isMushroomBlock(block.getType());
   }

   @NotNull
   public String display() {
      return "MMOItems{" + this.id + "}";
   }

   public void place(RegeneratingBlock regeneratingBlock) {
      Location location = regeneratingBlock.getLocation();
      CustomBlock block = MMOItems.plugin.getCustomBlocks().getBlock(this.id);
      location.getBlock().setType(block.getState().getType());
      location.getBlock().setBlockData(block.getState().getBlockData());
   }

   public void regenerate(RegeneratingBlock regeneratingBlock) {
      this.place(regeneratingBlock);
   }

   public boolean breakRestrictions(Block block) {
      return true;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         MMOItemsBlockType mmoItemsBlockType = (MMOItemsBlockType)obj;
         return this.id == mmoItemsBlockType.id;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.id);
   }
}
