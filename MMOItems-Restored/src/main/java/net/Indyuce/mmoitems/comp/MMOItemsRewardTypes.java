package net.Indyuce.mmoitems.comp;

import java.util.List;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.core.rewards.BSRewardType;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class MMOItemsRewardTypes extends BSRewardType {
   public Object createObject(Object obj, boolean flag) {
      return InputReader.readStringList(obj);
   }

   public boolean validityCheck(String text, Object obj) {
      if (obj == null && obj instanceof List) {
         ClassManager.manager
            .getBugFinder()
            .severe("Couldn't load the MMOItems reward type" + text + ". The reward object needs to be a list of types & IDs (format: [ITEM_TYPE].[ITEM_ID]).");
         return false;
      } else {
         return true;
      }
   }

   public boolean canBuy(Player player, BSBuy bsBuy, boolean flag, Object obj, ClickType clickType) {
      return true;
   }

   public void giveReward(Player player, BSBuy bsBuy, Object obj, ClickType clickType) {
      for (String text : (List<String>)obj) {
         try {
            String[] texts = text.split("\\.");
            Type type = MMOItems.plugin.getTypes().get(texts[0].toUpperCase().replace("-", "_"));

            for (ItemStack item : player.getInventory().addItem(new ItemStack[]{MMOItems.plugin.getItem(type, texts[1], PlayerData.get(player))}).values()) {
               player.getWorld().dropItem(player.getLocation(), item);
            }
         } catch (Exception exception) {
            ClassManager.manager.getBugFinder().severe("Couldn't load the MMOItems reward type" + text + ". Format: [ITEM_TYPE].[ITEM_ID]).");
         }
      }
   }

   public String getDisplayReward(Player player, BSBuy bsBuy, Object obj, ClickType clickType) {
      return "";
   }

   public String[] createNames() {
      return new String[]{"mmoitem", "mmoitems"};
   }

   public boolean mightNeedShopUpdate() {
      return false;
   }

   public void enableType() {
   }
}
