package net.Indyuce.inventory.manager.data.yaml;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.data.DefaultOfflineDataHolder;
import io.lumine.mythic.lib.data.yaml.YAMLSynchronizedDataHandler;
import java.util.UUID;
import java.util.logging.Level;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.inventory.CustomInventoryHandler;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class YAMLDataHandler extends YAMLSynchronizedDataHandler<InventoryHandler, DefaultOfflineDataHolder> {
   public YAMLDataHandler() {
      super(MMOInventory.plugin);
   }

   public void setup() {
   }

   public void saveInSection(@NotNull InventoryHandler data, ConfigurationSection config) {
      Validate.isInstanceOf(CustomInventoryHandler.class, data, "Custom inventory is disabled", new Object[0]);
      CustomInventoryHandler custom = (CustomInventoryHandler)data;
      config.set("inventory", null);

      for (CustomSlot slot : custom.getFilledSlots()) {
         ItemStack item = custom.getItem(slot);
         config.set("inventory." + slot.getIndex(), UtilityMethods.isAir(item) ? null : item);
      }
   }

   public void loadFromSection(@NotNull InventoryHandler data, ConfigurationSection config) {
      Validate.isInstanceOf(CustomInventoryHandler.class, data, "Custom inventory is disabled", new Object[0]);
      if (config.contains("inventory")) {
         for (String key : config.getConfigurationSection("inventory").getKeys(false)) {
            try {
               CustomSlot customSlot = MMOInventory.plugin.getSlotManager().get(Integer.parseInt(key));
               data.setItem(customSlot, config.getItemStack("inventory." + key));
            } catch (IllegalArgumentException exception) {
               MMOInventory.plugin
                  .getLogger()
                  .log(Level.SEVERE, "Could not read inventory item indexed " + key + " of " + data.getPlayer().getName() + ": " + exception.getMessage());
            }
         }
      }
   }

   public DefaultOfflineDataHolder getOffline(UUID uuid) {
      return new DefaultOfflineDataHolder(uuid);
   }

   public void close() {
   }
}
