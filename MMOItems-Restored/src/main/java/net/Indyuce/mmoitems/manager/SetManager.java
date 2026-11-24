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
      ConfigFile configFile = new ConfigFile("item-sets");

      for (String key : configFile.getConfig().getKeys(false)) {
         try {
            ConfigurationSection section = configFile.getConfig().getConfigurationSection(key);
            if (section == null) {
               throw new IllegalStateException(String.format("Item set '%s' is not a valid configuration section.", key));
            }

            this.itemSets.put(key, new ItemSet(section));
         } catch (IllegalArgumentException exception) {
            MMOItems.plugin.getLogger().log(Level.SEVERE, String.format("Could not load item set '%s': %s", key, exception.getMessage()));
         }
      }
   }

   public void register(ItemSet itemSet) {
      this.itemSets.put(itemSet.getId(), itemSet);
   }

   public boolean has(String text) {
      return this.itemSets.containsKey(text);
   }

   public Collection<ItemSet> getAll() {
      return this.itemSets.values();
   }

   public ItemSet get(String text) {
      return this.itemSets.getOrDefault(text, null);
   }
}
