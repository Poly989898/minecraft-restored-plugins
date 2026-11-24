package net.Indyuce.inventory.compat;

import io.th0rgal.oraxen.OraxenPlugin;
import java.util.Arrays;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.api.event.PlayerInteractCustomSlotsEvent;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.util.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

public class OraxenIntegration implements Listener {
   public OraxenIntegration() {
      if (MMOInventory.plugin.generateOraxenFiles) {
         ConfigFile configFile = new ConfigFile(OraxenPlugin.get(), "/items", "mmoinventory-hook");
         FileConfiguration config = configFile.getConfig();

         for (String key : config.getKeys(false)) {
            config.set(key, null);
         }

         MMOInventory.plugin.getSlotManager().getLoaded().forEach(slot -> this.generateItem(slot, config));
         if (MMOInventory.plugin.getSlotManager().hasFiller()) {
            this.generateItem(MMOInventory.plugin.getSlotManager().getFiller(), config);
         }

         configFile.save();
      }

      Bukkit.getPluginManager().registerEvents(this, MMOInventory.plugin);
   }

   private void generateItem(@NotNull CustomSlot slot, @NotNull ConfigurationSection config) {
      String key = "mmoinventory_" + this.getOraxenFormat(slot);
      config.set(key + ".material", slot.getItem().getType().name());
      config.set(key + ".excludeFromInventory", true);
      config.set(key + ".Pack.custom_model_data", slot.getItem().getItemMeta().getCustomModelData());
      config.set(key + ".Pack.generate_model", true);
      config.set(key + ".Pack.parent_model", "item/generated");
      config.set(key + ".Pack.textures", Arrays.asList("items/mmoinventory/" + this.getOraxenFormat(slot)));
   }

   @NotNull
   private String getOraxenFormat(@NotNull CustomSlot slot) {
      return slot.getId().replace("-", "_");
   }

   @EventHandler
   public void a(PlayerInteractEvent event) {
      if (event.getHand() == EquipmentSlot.HAND) {
         InventoryHandler who = (InventoryHandler)MMOInventory.plugin.getDataManager().get(event.getPlayer());
         PlayerInteractCustomSlotsEvent called = new PlayerInteractCustomSlotsEvent(who, event.getAction(), event.getClickedBlock(), event.getBlockFace());
         Bukkit.getPluginManager().callEvent(called);
      }
   }
}
