package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.item.UpgradeItemEvent;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.gui.edition.UpgradingEdition;
import net.Indyuce.mmoitems.stat.data.UpgradeData;
import net.Indyuce.mmoitems.stat.type.ConsumableItemInteraction;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UpgradeStat extends ItemStat<UpgradeData, UpgradeData> implements ConsumableItemInteraction {
   private static final Random RANDOM = new Random();

   public UpgradeStat() {
      super(
         "UPGRADE",
         Material.FLINT,
         "Item Upgrading",
         new String[]{
            "Upgrading your item improves its",
            "current stats. It requires either a",
            "consumable or a specific crafting ",
            "station. Upgrading may sometimes &cfail&7..."
         },
         new String[]{"equipment"}
      );
   }

   public UpgradeData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a config section");
      return new UpgradeData((ConfigurationSection)var1);
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull UpgradeData var2) {
      if (var2 instanceof UpgradeData) {
         var1.addItemTag(this.getAppliedNBT(var2));
         var1.getLore().registerPlaceholder("upgrade_level", String.valueOf(var2.getLevel()));
         if (var2.getMaxUpgrades() > 0) {
            var1.getLore().insert(this.getPath(), this.getGeneralStatFormat().replace("{value}", String.valueOf(var2.getMaxUpgrades())));
         }
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull UpgradeData var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new ItemTag(this.getNBTPath(), var1.toString()));
      return var2;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new UpgradingEdition(var1.getPlayer(), var1.getEdited()).open(var1);
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains("upgrade")) {
         var1.getEditedSection().set("upgrade", null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the upgrading setup.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      if (var3[0].equals("ref")) {
         var1.getEditedSection().set("upgrade.reference", var2);
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + "Upgrading reference successfully changed to " + ChatColor.GOLD + var2 + ChatColor.GRAY + ".");
      } else if (var3[0].equals("max")) {
         int var7 = Integer.parseInt(var2);
         var1.getEditedSection().set("upgrade.max", var7);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Max upgrades successfully set to " + ChatColor.GOLD + var7 + ChatColor.GRAY + ".");
      } else if (var3[0].equals("min")) {
         int var6 = Integer.parseInt(var2);
         var1.getEditedSection().set("upgrade.min", var6);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Min level successfully set to " + ChatColor.GOLD + var6 + ChatColor.GRAY + ".");
      } else if (var3[0].equals("rate")) {
         double var4 = MMOUtils.parseDouble(var2);
         var1.getEditedSection().set("upgrade.success", var4);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Upgrading rate successfully set to " + ChatColor.GOLD + var4 + "%" + ChatColor.GRAY + ".");
      } else {
         Validate.isTrue(MMOItems.plugin.getUpgrades().hasTemplate(var2), "Could not find any upgrade template with ID '" + var2 + "'.");
         var1.getEditedSection().set("upgrade.template", var2);
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + "Upgrading template successfully changed to " + ChatColor.GOLD + var2 + ChatColor.GRAY + ".");
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING));
      }

      UpgradeData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public UpgradeData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         try {
            return new UpgradeData(new JsonParser().parse((String)var2.getValue()).getAsJsonObject());
         } catch (JsonSyntaxException | IllegalStateException var4) {
         }
      }

      return null;
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<UpgradeData> var2) {
      var1.add(ChatColor.YELLOW + "►" + " Left click to setup upgrading.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to reset.");
   }

   @NotNull
   public UpgradeData getClearStatData() {
      return new UpgradeData(null, null, false, false, 0, 0, 0.0);
   }

   @Override
   public boolean handleConsumableEffect(
      @NotNull InventoryClickEvent var1, @NotNull PlayerData var2, @NotNull Consumable var3, @NotNull NBTItem var4, Type var5
   ) {
      VolatileMMOItem var6 = var3.getMMOItem();
      Player var7 = var2.getPlayer();
      if (var6.hasData(ItemStats.UPGRADE) && var4.hasTag(ItemStats.UPGRADE.getNBTPath())) {
         if (var4.getItem().getAmount() > 1) {
            Message.CANT_UPGRADED_STACK.format(ChatColor.RED).send(var7);
            var7.playSound(var7.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 2.0F);
            return false;
         }

         LiveMMOItem var8 = new LiveMMOItem(var4);
         UpgradeData var9 = (UpgradeData)var8.getData(ItemStats.UPGRADE);
         if (var9.isWorkbench()) {
            return false;
         }

         if (!var9.canLevelUp()) {
            Message.MAX_UPGRADES_HIT.format(ChatColor.RED).send(var7);
            var7.playSound(var7.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 2.0F);
            return false;
         }

         UpgradeData var10 = (UpgradeData)var6.getData(ItemStats.UPGRADE);
         if (!MMOUtils.checkReference(var10.getReference(), var9.getReference())) {
            Message.WRONG_UPGRADE_REFERENCE.format(ChatColor.RED).send(var7);
            var7.playSound(var7.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 2.0F);
            return false;
         }

         UpgradeItemEvent var11 = new UpgradeItemEvent(var2, var6, var8, var10, var9);
         Bukkit.getPluginManager().callEvent(var11);
         if (var11.isCancelled()) {
            return false;
         }

         var9.upgrade(var8);
         NBTItem var12 = var8.newBuilder().buildNBT();
         if (MMOItems.plugin.getLanguage().upgradeRequirementsCheck && !var2.getRPG().canUse(var12, false)) {
            Message.UPGRADE_REQUIREMENT_SAFE_CHECK.format(ChatColor.RED).send(var7);
            var7.playSound(var7.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 2.0F);
            return false;
         }

         if (RANDOM.nextDouble() > var10.getSuccess() * var9.getSuccess()) {
            Message.UPGRADE_FAIL.format(ChatColor.RED).send(var7);
            if (var9.destroysOnFail()) {
               var1.getCurrentItem().setAmount(0);
            }

            var7.playSound(var7.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 2.0F);
            return true;
         } else {
            Message.UPGRADE_SUCCESS.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var1.getCurrentItem())).send(var7);
            var1.getCurrentItem().setItemMeta(var12.toItem().getItemMeta());
            var7.playSound(var7.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            return true;
         }
      } else {
         return false;
      }
   }
}
