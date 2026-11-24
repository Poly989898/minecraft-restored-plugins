package net.Indyuce.mmoitems.api.crafting.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.util.SmartGive;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VanillaTrigger extends Trigger {
   private final Material material;
   private final int amount;

   public VanillaTrigger(MMOLineConfig mmoLineConfig) {
      super("vanilla");
      mmoLineConfig.validate(new String[]{"type"});
      this.material = Material.valueOf(mmoLineConfig.getString("type").toUpperCase().replace("-", "_"));
      this.amount = mmoLineConfig.contains("amount") ? Math.max(1, mmoLineConfig.getInt("amount")) : 1;
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      if (playerData.isOnline()) {
         new SmartGive(playerData.getPlayer()).give(new ItemStack[]{new ItemStack(this.material, this.amount)});
      }
   }
}
