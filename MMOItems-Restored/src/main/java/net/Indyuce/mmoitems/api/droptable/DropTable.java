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

   public DropTable(ConfigurationSection var1) {
      Validate.notNull(var1, "Could not read the drop table config");

      for (String var3 : var1.getKeys(false)) {
         try {
            for (int var4 = 0; var4 < var1.getInt(var3 + ".coef"); var4++) {
               this.subtablesList.add(var3);
            }

            this.subtables.put(var3, new DropTable.Subtable(var1.getConfigurationSection(var3)));
         } catch (IllegalArgumentException var5) {
            MMOItems.plugin
               .getLogger()
               .log(Level.SEVERE, "Could not read subtable '" + var3 + "' from drop table '" + var1.getName() + "': " + var5.getMessage());
         }
      }

      Validate.notEmpty(this.subtablesList, "Your droptable must contain at least one subtable");
   }

   public String getRandomSubtable() {
      return this.subtablesList.get(random.nextInt(this.subtablesList.size()));
   }

   public List<ItemStack> read(@Nullable PlayerData var1, boolean var2) {
      ArrayList var3 = new ArrayList();
      String var4 = this.getRandomSubtable();

      for (DropItem var6 : this.getSubtable(var4).getDropItems(var2)) {
         if (var6.rollDrop()) {
            ItemStack var7 = var6.getItem(var1);
            if (var7 == null) {
               MMOItems.plugin.getLogger().log(Level.SEVERE, "Couldn't read the subtable item " + var6.getKey());
            } else {
               var3.add(var7);
            }
         }
      }

      return var3;
   }

   public Collection<DropTable.Subtable> getSubtables() {
      return this.subtables.values();
   }

   public DropTable.Subtable getSubtable(String var1) {
      return this.subtables.get(var1);
   }

   public static class Subtable {
      private final List<DropItem> items = new ArrayList<>();
      private final boolean disableSilkTouch;

      public Subtable(ConfigurationSection var1) {
         Validate.notNull(var1, "Could not read subtable config");
         Validate.isTrue(var1.contains("coef"), "Could not read subtable coefficient.");
         Validate.isTrue(var1.contains("items") || var1.contains("blocks"), "Could not find item/block list");
         if (var1.contains("items")) {
            for (String var3 : var1.getConfigurationSection("items").getKeys(false)) {
               Type var4 = MMOItems.plugin.getTypes().getOrThrow(var3.toUpperCase().replace("-", "_"));

               for (String var6 : var1.getConfigurationSection("items." + var3).getKeys(false)) {
                  this.items.add(new MMOItemDropItem(var4, var6, var1.getString("items." + var3 + "." + var6)));
               }
            }
         }

         if (var1.contains("blocks")) {
            for (String var8 : var1.getConfigurationSection("blocks").getKeys(false)) {
               int var9 = Integer.parseInt(var8);
               Validate.isTrue(var9 > 0 && var9 != 54 && var9 <= 160, var9 + " is not a valid block ID");
               this.items.add(new BlockDropItem(var9, var1.getString("blocks." + var8)));
            }
         }

         this.disableSilkTouch = var1.getBoolean("disable-silk-touch");
      }

      public List<DropItem> getDropItems(boolean var1) {
         return var1 && this.disableSilkTouch ? new ArrayList<>() : this.items;
      }
   }
}
