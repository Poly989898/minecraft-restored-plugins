package net.Indyuce.mmocore.loot.droptable.dropitem;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.loot.LootBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VanillaDropItem extends DropItem {
   private final Material material;

   public VanillaDropItem(MMOLineConfig config) {
      super(config);
      config.validate(new String[]{"type"});
      this.material = Material.valueOf(config.getString("type"));
   }

   public Material getMaterial() {
      return this.material;
   }

   @Override
   public void collect(LootBuilder builder) {
      builder.addLoot(new ItemStack(this.material, this.rollAmount()));
   }
}
