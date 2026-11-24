package net.Indyuce.mmoitems.api.droptable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import javax.annotation.Nullable;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.droptable.item.BlockDropItem;
import net.Indyuce.mmoitems.api.droptable.item.DropItem;
import net.Indyuce.mmoitems.api.droptable.item.MMOItemDropItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class DropTable {
   private final List<String> subtablesList = new ArrayList<>();
   private final Map<String, DropTable.Subtable> subtables = new HashMap<>();
   private static final Random random = new Random();

   public DropTable(ConfigurationSection section) {
      Validate.notNull(section, "Could not read the drop table config");

      for (String key : section.getKeys(false)) {
         try {
            for (int i = 0; i < section.getInt(key + ".coef"); i++) {
               this.subtablesList.add(key);
            }

            this.subtables.put(key, new DropTable.Subtable(section.getConfigurationSection(key)));
         } catch (IllegalArgumentException exception) {
            MMOItems.plugin
               .getLogger()
               .log(Level.SEVERE, "Could not read subtable '" + key + "' from drop table '" + section.getName() + "': " + exception.getMessage());
         }
      }

      Validate.notEmpty(this.subtablesList, "Your droptable must contain at least one subtable");
   }

   public String getRandomSubtable() {
      return this.subtablesList.get(random.nextInt(this.subtablesList.size()));
   }

   public List<ItemStack> read(@Nullable PlayerData playerData, boolean flag) {
      ArrayList list = new ArrayList();
      String randomSubtable = this.getRandomSubtable();

      for (DropItem dropItem : this.getSubtable(randomSubtable).getDropItems(flag)) {
         if (dropItem.rollDrop()) {
            ItemStack item = dropItem.getItem(playerData);
            if (item == null) {
               MMOItems.plugin.getLogger().log(Level.SEVERE, "Couldn't read the subtable item " + dropItem.getKey());
            } else {
               list.add(item);
            }
         }
      }

      return list;
   }

   public Collection<DropTable.Subtable> getSubtables() {
      return this.subtables.values();
   }

   public DropTable.Subtable getSubtable(String text) {
      return this.subtables.get(text);
   }

   public static class Subtable {
      private final List<DropItem> items = new ArrayList<>();
      private final boolean disableSilkTouch;

      public Subtable(ConfigurationSection section) {
         Validate.notNull(section, "Could not read subtable config");
         Validate.isTrue(section.contains("coef"), "Could not read subtable coefficient.");
         Validate.isTrue(section.contains("items") || section.contains("blocks"), "Could not find item/block list");
         if (section.contains("items")) {
            for (String key : section.getConfigurationSection("items").getKeys(false)) {
               Type type = MMOItems.plugin.getTypes().getOrThrow(key.toUpperCase().replace("-", "_"));

               for (String itemKey : section.getConfigurationSection("items." + key).getKeys(false)) {
                  this.items.add(new MMOItemDropItem(type, itemKey, section.getString("items." + key + "." + itemKey)));
               }
            }
         }

         if (section.contains("blocks")) {
            for (String key : section.getConfigurationSection("blocks").getKeys(false)) {
               int count = Integer.parseInt(key);
               Validate.isTrue(count > 0 && count != 54 && count <= 160, count + " is not a valid block ID");
               this.items.add(new BlockDropItem(count, section.getString("blocks." + key)));
            }
         }

         this.disableSilkTouch = section.getBoolean("disable-silk-touch");
      }

      public List<DropItem> getDropItems(boolean flag) {
         return flag && this.disableSilkTouch ? new ArrayList<>() : this.items;
      }
   }
}
