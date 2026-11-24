package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.List;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.data.GemSocketsData;
import net.Indyuce.mmoitems.stat.type.BooleanStat;
import net.Indyuce.mmoitems.stat.type.ConsumableItemInteraction;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class CanUnsocket extends BooleanStat implements ConsumableItemInteraction {
   public CanUnsocket() {
      super(
         "CAN_UNSOCKET",
         Material.PAPER,
         "Can Unsocket?",
         new String[]{"This item, when used on another item, if", "that other item has Gem Stones", "may be used to remove those Gems."},
         new String[]{"consumable"}
      );
   }

   @Override
   public boolean handleConsumableEffect(
      @NotNull InventoryClickEvent var1, @NotNull PlayerData var2, @NotNull Consumable var3, @NotNull NBTItem var4, Type var5
   ) {
      if (var5 == null) {
         return false;
      }

      VolatileMMOItem var6 = new VolatileMMOItem(var4);
      if (!var6.hasData(ItemStats.GEM_SOCKETS)) {
         return false;
      }

      GemSocketsData var7 = (GemSocketsData)var6.getData(ItemStats.GEM_SOCKETS);
      if (var7 != null && var7.getGemstones().size() != 0) {
         Player var8 = var2.getPlayer();
         LiveMMOItem var10 = new LiveMMOItem(var4);
         List var9 = var10.extractGemstones();
         if (var9.isEmpty()) {
            Message.RANDOM_UNSOCKET_GEM_TOO_OLD.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var1.getCurrentItem())).send(var8);
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }
}
