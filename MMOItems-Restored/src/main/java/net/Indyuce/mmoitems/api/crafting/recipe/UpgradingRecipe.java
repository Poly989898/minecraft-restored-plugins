package net.Indyuce.mmoitems.api.crafting.recipe;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.Random;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.crafting.ConfigMMOItem;
import net.Indyuce.mmoitems.api.crafting.CraftingStation;
import net.Indyuce.mmoitems.api.crafting.ingredient.CheckedIngredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.Ingredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.MMOItemIngredient;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.IngredientInventory;
import net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.util.ConfigItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.data.UpgradeData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class UpgradingRecipe extends Recipe {
   private final ConfigMMOItem item;
   private final Ingredient ingredient;
   private static final Random random = new Random();

   public UpgradingRecipe(ConfigurationSection var1) {
      super(var1);
      this.item = new ConfigMMOItem(var1.getConfigurationSection("item"));
      this.ingredient = new MMOItemIngredient(this.item);
   }

   public ConfigMMOItem getItem() {
      return this.item;
   }

   @Override
   public boolean whenUsed(PlayerData var1, IngredientInventory var2, CheckedRecipe var3, CraftingStation var4) {
      if (!var1.isOnline()) {
         return false;
      }

      UpgradingRecipe.CheckedUpgradingRecipe var5 = (UpgradingRecipe.CheckedUpgradingRecipe)var3;
      PlayerUseCraftingStationEvent var6 = new PlayerUseCraftingStationEvent(var1, var4, var5);
      Bukkit.getPluginManager().callEvent(var6);
      if (var6.isCancelled()) {
         return false;
      }

      var5.getUpgradeData().upgrade(var5.getMMOItem());
      var5.getUpgraded().setItemMeta(var5.getMMOItem().newBuilder().build().getItemMeta());
      Message.UPGRADE_SUCCESS.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var5.getUpgraded())).send(var1.getPlayer());
      if (!this.hasOption(Recipe.RecipeOption.SILENT_CRAFT)) {
         var1.getPlayer().playSound(var1.getPlayer().getLocation(), var4.getSound(), 1.0F, 1.0F);
      }

      return true;
   }

   @Override
   public boolean canUse(PlayerData var1, IngredientInventory var2, CheckedRecipe var3, CraftingStation var4) {
      CheckedIngredient var5 = var2.findMatching(this.ingredient);
      if (!var5.isHad()) {
         if (!var1.isOnline()) {
            return false;
         }

         Message.NOT_HAVE_ITEM_UPGRADE.format(ChatColor.RED).send(var1.getPlayer());
         var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 2.0F);
         return false;
      } else {
         NBTItem var6 = NBTItem.get(var5.getFound().stream().findFirst().get().getItem());
         UpgradingRecipe.CheckedUpgradingRecipe var7 = (UpgradingRecipe.CheckedUpgradingRecipe)var3;
         if (!(var7.mmoitem = new LiveMMOItem(var6)).hasData(ItemStats.UPGRADE)) {
            return false;
         }

         if (!(var7.upgradeData = (UpgradeData)var7.getMMOItem().getData(ItemStats.UPGRADE)).canLevelUp()) {
            if (!var1.isOnline()) {
               return false;
            }

            Message.MAX_UPGRADES_HIT.format(ChatColor.RED).send(var1.getPlayer());
            var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 2.0F);
            return false;
         } else if (random.nextDouble() > var7.getUpgradeData().getSuccess()) {
            if (var7.getUpgradeData().destroysOnFail()) {
               var7.getUpgraded().setAmount(var7.getUpgraded().getAmount() - 1);
            }

            var7.getIngredients().forEach(var0 -> var0.takeAway());
            if (!var1.isOnline()) {
               return false;
            }

            Message.UPGRADE_FAIL_STATION.format(ChatColor.RED).send(var1.getPlayer());
            var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 2.0F);
            return false;
         } else {
            return true;
         }
      }
   }

   @Override
   public ItemStack display(CheckedRecipe var1) {
      return ConfigItems.UPGRADING_RECIPE_DISPLAY.newBuilder(var1).build();
   }

   @Override
   public CheckedRecipe evaluateRecipe(PlayerData var1, IngredientInventory var2) {
      return new UpgradingRecipe.CheckedUpgradingRecipe(this, var1, var2);
   }

   public class CheckedUpgradingRecipe extends CheckedRecipe {
      private LiveMMOItem mmoitem;
      private UpgradeData upgradeData;

      public CheckedUpgradingRecipe(Recipe nullx, PlayerData nullxx, IngredientInventory nullxxx) {
         super(nullx, nullxx, nullxxx);
      }

      public UpgradeData getUpgradeData() {
         return this.upgradeData;
      }

      public LiveMMOItem getMMOItem() {
         return this.mmoitem;
      }

      public ItemStack getUpgraded() {
         return this.mmoitem.getNBT().getItem();
      }
   }
}
