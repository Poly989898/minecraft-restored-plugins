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
   public Object createObject(Object var1, boolean var2) {
      return InputReader.readStringList(var1);
   }

   public boolean validityCheck(String var1, Object var2) {
      if (var2 == null && var2 instanceof List) {
         ClassManager.manager
            .getBugFinder()
            .severe("Couldn't load the MMOItems reward type" + var1 + ". The reward object needs to be a list of types & IDs (format: [ITEM_TYPE].[ITEM_ID]).");
         return false;
      } else {
         return true;
      }
   }

   public boolean canBuy(Player var1, BSBuy var2, boolean var3, Object var4, ClickType var5) {
      return true;
   }

   public void giveReward(Player var1, BSBuy var2, Object var3, ClickType var4) {
      for (String var6 : (List<String>)var3) {
         try {
            String[] var7 = var6.split("\\.");
            Type var8 = MMOItems.plugin.getTypes().get(var7[0].toUpperCase().replace("-", "_"));

            for (ItemStack var10 : var1.getInventory().addItem(new ItemStack[]{MMOItems.plugin.getItem(var8, var7[1], PlayerData.get(var1))}).values()) {
               var1.getWorld().dropItem(var1.getLocation(), var10);
            }
         } catch (Exception var11) {
            ClassManager.manager.getBugFinder().severe("Couldn't load the MMOItems reward type" + var6 + ". Format: [ITEM_TYPE].[ITEM_ID]).");
         }
      }
   }

   public String getDisplayReward(Player var1, BSBuy var2, Object var3, ClickType var4) {
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
