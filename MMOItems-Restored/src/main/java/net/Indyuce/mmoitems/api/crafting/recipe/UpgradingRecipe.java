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

   public UpgradingRecipe(ConfigurationSection section) {
      super(section);
      this.item = new ConfigMMOItem(section.getConfigurationSection("item"));
      this.ingredient = new MMOItemIngredient(this.item);
   }

   public ConfigMMOItem getItem() {
      return this.item;
   }

   @Override
   public boolean whenUsed(PlayerData playerData, IngredientInventory ingredientInventory, CheckedRecipe checkedRecipe, CraftingStation craftingStation) {
      if (!playerData.isOnline()) {
         return false;
      }

      UpgradingRecipe.CheckedUpgradingRecipe checkedUpgradingRecipe = (UpgradingRecipe.CheckedUpgradingRecipe)checkedRecipe;
      PlayerUseCraftingStationEvent event = new PlayerUseCraftingStationEvent(playerData, craftingStation, checkedUpgradingRecipe);
      Bukkit.getPluginManager().callEvent(event);
      if (event.isCancelled()) {
         return false;
      }

      checkedUpgradingRecipe.getUpgradeData().upgrade(checkedUpgradingRecipe.getMMOItem());
      checkedUpgradingRecipe.getUpgraded().setItemMeta(checkedUpgradingRecipe.getMMOItem().newBuilder().build().getItemMeta());
      Message.UPGRADE_SUCCESS.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(checkedUpgradingRecipe.getUpgraded())).send(playerData.getPlayer());
      if (!this.hasOption(Recipe.RecipeOption.SILENT_CRAFT)) {
         playerData.getPlayer().playSound(playerData.getPlayer().getLocation(), craftingStation.getSound(), 1.0F, 1.0F);
      }

      return true;
   }

   @Override
   public boolean canUse(PlayerData playerData, IngredientInventory ingredientInventory, CheckedRecipe checkedRecipe, CraftingStation craftingStation) {
      CheckedIngredient matching = ingredientInventory.findMatching(this.ingredient);
      if (!matching.isHad()) {
         if (!playerData.isOnline()) {
            return false;
         }

         Message.NOT_HAVE_ITEM_UPGRADE.format(ChatColor.RED).send(playerData.getPlayer());
         playerData.getPlayer().playSound(playerData.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 2.0F);
         return false;
      } else {
         NBTItem nbtItem = NBTItem.get(matching.getFound().stream().findFirst().get().getItem());
         UpgradingRecipe.CheckedUpgradingRecipe checkedUpgradingRecipe = (UpgradingRecipe.CheckedUpgradingRecipe)checkedRecipe;
         if (!(checkedUpgradingRecipe.mmoitem = new LiveMMOItem(nbtItem)).hasData(ItemStats.UPGRADE)) {
            return false;
         }

         if (!(checkedUpgradingRecipe.upgradeData = (UpgradeData)checkedUpgradingRecipe.getMMOItem().getData(ItemStats.UPGRADE)).canLevelUp()) {
            if (!playerData.isOnline()) {
               return false;
            }

            Message.MAX_UPGRADES_HIT.format(ChatColor.RED).send(playerData.getPlayer());
            playerData.getPlayer().playSound(playerData.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 2.0F);
            return false;
         } else if (random.nextDouble() > checkedUpgradingRecipe.getUpgradeData().getSuccess()) {
            if (checkedUpgradingRecipe.getUpgradeData().destroysOnFail()) {
               checkedUpgradingRecipe.getUpgraded().setAmount(checkedUpgradingRecipe.getUpgraded().getAmount() - 1);
            }

            checkedUpgradingRecipe.getIngredients().forEach(checkedIngredient -> checkedIngredient.takeAway());
            if (!playerData.isOnline()) {
               return false;
            }

            Message.UPGRADE_FAIL_STATION.format(ChatColor.RED).send(playerData.getPlayer());
            playerData.getPlayer().playSound(playerData.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 2.0F);
            return false;
         } else {
            return true;
         }
      }
   }

   @Override
   public ItemStack display(CheckedRecipe checkedRecipe) {
      return ConfigItems.UPGRADING_RECIPE_DISPLAY.newBuilder(checkedRecipe).build();
   }

   @Override
   public CheckedRecipe evaluateRecipe(PlayerData playerData, IngredientInventory ingredientInventory) {
      return new UpgradingRecipe.CheckedUpgradingRecipe(this, playerData, ingredientInventory);
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
