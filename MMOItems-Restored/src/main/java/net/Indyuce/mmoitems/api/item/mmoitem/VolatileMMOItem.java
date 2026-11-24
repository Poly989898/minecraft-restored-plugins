package net.Indyuce.mmoitems.api.item.mmoitem;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class VolatileMMOItem extends ReadMMOItem {
   public VolatileMMOItem(NBTItem nbtItem) {
      super(nbtItem);
   }

   @Override
   public boolean hasData(@NotNull ItemStat itemStat) {
      if (!super.hasData(itemStat)) {
         try {
            itemStat.whenLoaded(this);
         } catch (RuntimeException exception) {
            MMOItems.plugin
               .getLogger()
               .log(
                  Level.SEVERE,
                  ChatColor.GRAY
                     + "Could not load stat '"
                     + ChatColor.GOLD
                     + itemStat.getId()
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

      return super.hasData(itemStat);
   }

   @NotNull
   @Override
   public ItemStackBuilder newBuilder() {
      throw new UnsupportedOperationException("Cannot build a VolatileMMOItem");
   }
}
