package net.Indyuce.mmoitems.api.droptable.item;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.inventory.ItemStack;

public class BlockDropItem extends DropItem {
   private final int blockId;

   public BlockDropItem(int count, String text) {
      super(text);
      this.blockId = count;
   }

   public int getBlockId() {
      return this.blockId;
   }

   @Override
   public ItemStack getItem(PlayerData playerData, int count) {
      return MMOItems.plugin.getCustomBlocks().getBlock(this.blockId).getItem();
   }

   @Override
   public String getKey() {
      return "block." + this.getBlockId();
   }
}
