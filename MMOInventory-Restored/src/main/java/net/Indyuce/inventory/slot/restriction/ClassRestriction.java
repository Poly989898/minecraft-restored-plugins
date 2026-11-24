package net.Indyuce.inventory.slot.restriction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.util.LineConfig;
import org.bukkit.inventory.ItemStack;

public class ClassRestriction extends SlotRestriction {
   private final Set<String> classes = new HashSet<>();

   public ClassRestriction(LineConfig config) {
      config.validate("name");
      this.classes.addAll(Arrays.asList(config.getString("").split("\\,")));
   }

   @Override
   public boolean isVerified(InventoryHandler provider, CustomSlot slot, ItemStack item) {
      String playerClass = MMOInventory.plugin.getClassModule().getClass(provider);
      return playerClass != null && this.classes.contains(playerClass);
   }
}
