package net.Indyuce.mmoitems.api.item.mmoitem;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.logging.Level;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class VolatileMMOItem extends ReadMMOItem {
   public VolatileMMOItem(NBTItem var1) {
      super(var1);
   }

   @Override
   public boolean hasData(@NotNull ItemStat var1) {
      if (!super.hasData(var1)) {
         try {
            var1.whenLoaded(this);
         } catch (RuntimeException var3) {
            MMOItems.plugin
               .getLogger()
               .log(
                  Level.SEVERE,
                  ChatColor.GRAY
                     + "Could not load stat '"
                     + ChatColor.GOLD
                     + var1.getId()
                     + ChatColor.GRAY
                     + "'item data from '"
                     + ChatColor.RED
                     + this.getId()
                     + ChatColor.GRAY
                     + "': "
                     + ChatColor.YELLOW
                     + var3.getMessage()
               );
         }
      }

      return super.hasData(var1);
   }

   @NotNull
   @Override
   public ItemStackBuilder newBuilder() {
      throw new UnsupportedOperationException("Cannot build a VolatileMMOItem");
   }
}
