package net.Indyuce.mmoitems.api.droptable.item;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.inventory.ItemStack;

public class BlockDropItem extends DropItem {
   private final int blockId;

   public BlockDropItem(int var1, String var2) {
      super(var2);
      this.blockId = var1;
   }

   public int getBlockId() {
      return this.blockId;
   }

   @Override
   public ItemStack getItem(PlayerData var1, int var2) {
      return MMOItems.plugin.getCustomBlocks().getBlock(this.blockId).getItem();
   }

   @Override
   public String getKey() {
      return "block." + this.getBlockId();
   }
}
