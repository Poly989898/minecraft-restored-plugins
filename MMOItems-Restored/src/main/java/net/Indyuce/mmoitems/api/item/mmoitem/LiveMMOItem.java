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
   public LiveMMOItem(ItemStack item) {
      this(MythicLib.plugin.getVersion().getWrapper().getNBTItem(item));
   }

   public LiveMMOItem(NBTItem nbtItem) {
      super(nbtItem);

      for (ItemStat availableStat : this.getType().getAvailableStats()) {
         try {
            availableStat.whenLoaded(this);
            if (!this.hasStatHistory(availableStat)) {
               ItemTag tagAtPath = ItemTag.getTagAtPath("HSTRY_" + availableStat.getId(), this.getNBT(), SupportedNBTTagValues.STRING);
               if (tagAtPath != null) {
                  StatHistory statHistory = StatHistory.fromNBTString(this, (String)tagAtPath.getValue());
                  if (statHistory != null) {
                     this.setStatHistory(availableStat, statHistory);
                  }
               }
            }
         } catch (IllegalArgumentException exception) {
            MMOItems.plugin
               .getLogger()
               .log(
                  Level.SEVERE,
                  ChatColor.GRAY
                     + "Could not load stat '"
                     + ChatColor.GOLD
                     + availableStat.getId()
                     + ChatColor.GRAY
                     + "'item data from '"
                     + ChatColor.RED
                     + this.getId()
                     + ChatColor.GRAY
                     + "': "
                     + ChatColor.YELLOW
                     + exception.getMessage()
               );
         }
      }
   }
}
