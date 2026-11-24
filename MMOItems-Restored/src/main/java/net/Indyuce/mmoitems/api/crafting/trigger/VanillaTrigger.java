package net.Indyuce.mmoitems.api.crafting.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.util.SmartGive;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VanillaTrigger extends Trigger {
   private final Material material;
   private final int amount;

   public VanillaTrigger(MMOLineConfig var1) {
      super("vanilla");
      var1.validate(new String[]{"type"});
      this.material = Material.valueOf(var1.getString("type").toUpperCase().replace("-", "_"));
      this.amount = var1.contains("amount") ? Math.max(1, var1.getInt("amount")) : 1;
   }

   @Override
   public void whenCrafting(PlayerData var1) {
      if (var1.isOnline()) {
         new SmartGive(var1.getPlayer()).give(new ItemStack[]{new ItemStack(this.material, this.amount)});
      }
   }
}
