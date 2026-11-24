package net.Indyuce.mmoitems.api.item.mmoitem;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class LiveMMOItem extends ReadMMOItem {
   public LiveMMOItem(ItemStack var1) {
      this(MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1));
   }

   public LiveMMOItem(NBTItem var1) {
      super(var1);

      for (ItemStat var3 : this.getType().getAvailableStats()) {
         try {
            var3.whenLoaded(this);
            if (!this.hasStatHistory(var3)) {
               ItemTag var4 = ItemTag.getTagAtPath("HSTRY_" + var3.getId(), this.getNBT(), SupportedNBTTagValues.STRING);
               if (var4 != null) {
                  StatHistory var5 = StatHistory.fromNBTString(this, (String)var4.getValue());
                  if (var5 != null) {
                     this.setStatHistory(var3, var5);
                  }
               }
            }
         } catch (IllegalArgumentException var6) {
            MMOItems.plugin
               .getLogger()
               .log(
                  Level.SEVERE,
                  ChatColor.GRAY
                     + "Could not load stat '"
                     + ChatColor.GOLD
                     + var3.getId()
                     + ChatColor.GRAY
                     + "'item data from '"
                     + ChatColor.RED
                     + this.getId()
                     + ChatColor.GRAY
                     + "': "
                     + ChatColor.YELLOW
                     + var6.getMessage()
               );
         }
      }
   }
}
