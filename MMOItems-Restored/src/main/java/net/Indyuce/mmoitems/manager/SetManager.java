package net.Indyuce.mmoitems.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ConfigFile;
import net.Indyuce.mmoitems.api.ItemSet;
import org.bukkit.configuration.ConfigurationSection;

public class SetManager implements Reloadable {
   private final Map<String, ItemSet> itemSets = new HashMap<>();

   public SetManager() {
      this.reload();
   }

   @Override
   public void reload() {
      this.itemSets.clear();
      ConfigFile var1 = new ConfigFile("item-sets");

      for (String var3 : var1.getConfig().getKeys(false)) {
         try {
            ConfigurationSection var4 = var1.getConfig().getConfigurationSection(var3);
            if (var4 == null) {
               throw new IllegalStateException(String.format("Item set '%s' is not a valid configuration section.", var3));
            }

            this.itemSets.put(var3, new ItemSet(var4));
         } catch (IllegalArgumentException var5) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, String.format("Could not load item set '%s': %s", var3, var5.getMessage()));
         }
      }
   }

   public void register(ItemSet var1) {
      this.itemSets.put(var1.getId(), var1);
   }

   public boolean has(String var1) {
      return this.itemSets.containsKey(var1);
   }

   public Collection<ItemSet> getAll() {
      return this.itemSets.values();
   }

   public ItemSet get(String var1) {
      return this.itemSets.getOrDefault(var1, null);
   }
}
