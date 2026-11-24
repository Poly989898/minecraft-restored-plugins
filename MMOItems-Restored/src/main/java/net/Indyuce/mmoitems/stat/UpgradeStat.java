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

   public UpgradeData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a config section");
      return new UpgradeData((ConfigurationSection)obj);
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull UpgradeData upgradeData) {
      if (upgradeData instanceof UpgradeData) {
         itemStackBuilder.addItemTag(this.getAppliedNBT(upgradeData));
         itemStackBuilder.getLore().registerPlaceholder("upgrade_level", String.valueOf(upgradeData.getLevel()));
         if (upgradeData.getMaxUpgrades() > 0) {
            itemStackBuilder.getLore().insert(this.getPath(), this.getGeneralStatFormat().replace("{value}", String.valueOf(upgradeData.getMaxUpgrades())));
         }
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull UpgradeData upgradeData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), upgradeData.toString()));
      return list;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new UpgradingEdition(editionInventory.getPlayer(), editionInventory.getEdited()).open(editionInventory);
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains("upgrade")) {
         editionInventory.getEditedSection().set("upgrade", null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the upgrading setup.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      if (objs[0].equals("ref")) {
         editionInventory.getEditedSection().set("upgrade.reference", text);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + "Upgrading reference successfully changed to " + ChatColor.GOLD + text + ChatColor.GRAY + ".");
      } else if (objs[0].equals("max")) {
         int count = Integer.parseInt(text);
         editionInventory.getEditedSection().set("upgrade.max", count);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Max upgrades successfully set to " + ChatColor.GOLD + count + ChatColor.GRAY + ".");
      } else if (objs[0].equals("min")) {
         int count = Integer.parseInt(text);
         editionInventory.getEditedSection().set("upgrade.min", count);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Min level successfully set to " + ChatColor.GOLD + count + ChatColor.GRAY + ".");
      } else if (objs[0].equals("rate")) {
         double value = MMOUtils.parseDouble(text);
         editionInventory.getEditedSection().set("upgrade.success", value);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Upgrading rate successfully set to " + ChatColor.GOLD + value + "%" + ChatColor.GRAY + ".");
      } else {
         Validate.isTrue(MMOItems.plugin.getUpgrades().hasTemplate(text), "Could not find any upgrade template with ID '" + text + "'.");
         editionInventory.getEditedSection().set("upgrade.template", text);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + "Upgrading template successfully changed to " + ChatColor.GOLD + text + ChatColor.GRAY + ".");
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      UpgradeData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   public UpgradeData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         try {
            return new UpgradeData(new JsonParser().parse((String)tagAtPath.getValue()).getAsJsonObject());
         } catch (JsonSyntaxException | IllegalStateException exception) {
         }
      }

      return null;
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<UpgradeData> optional) {
      texts.add(ChatColor.YELLOW + "►" + " Left click to setup upgrading.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to reset.");
   }

   @NotNull
   public UpgradeData getClearStatData() {
      return new UpgradeData(null, null, false, false, 0, 0, 0.0);
   }

   @Override
   public boolean handleConsumableEffect(
      @NotNull InventoryClickEvent event, @NotNull PlayerData playerData, @NotNull Consumable consumable, @NotNull NBTItem nbtItem, Type type
   ) {
      VolatileMMOItem mMOItem = consumable.getMMOItem();
      Player player = playerData.getPlayer();
      if (mMOItem.hasData(ItemStats.UPGRADE) && nbtItem.hasTag(ItemStats.UPGRADE.getNBTPath())) {
         if (nbtItem.getItem().getAmount() > 1) {
            Message.CANT_UPGRADED_STACK.format(ChatColor.RED).send(player);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 2.0F);
            return false;
         }

         LiveMMOItem liveMMOItem = new LiveMMOItem(nbtItem);
         UpgradeData data = (UpgradeData)liveMMOItem.getData(ItemStats.UPGRADE);
         if (data.isWorkbench()) {
            return false;
         }

         if (!data.canLevelUp()) {
            Message.MAX_UPGRADES_HIT.format(ChatColor.RED).send(player);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 2.0F);
            return false;
         }

         UpgradeData upgradeData = (UpgradeData)mMOItem.getData(ItemStats.UPGRADE);
         if (!MMOUtils.checkReference(upgradeData.getReference(), data.getReference())) {
            Message.WRONG_UPGRADE_REFERENCE.format(ChatColor.RED).send(player);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 2.0F);
            return false;
         }

         UpgradeItemEvent upgradeEvent = new UpgradeItemEvent(playerData, mMOItem, liveMMOItem, upgradeData, data);
         Bukkit.getPluginManager().callEvent(upgradeEvent);
         if (upgradeEvent.isCancelled()) {
            return false;
         }

         data.upgrade(liveMMOItem);
         NBTItem nBT = liveMMOItem.newBuilder().buildNBT();
         if (MMOItems.plugin.getLanguage().upgradeRequirementsCheck && !playerData.getRPG().canUse(nBT, false)) {
            Message.UPGRADE_REQUIREMENT_SAFE_CHECK.format(ChatColor.RED).send(player);
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 2.0F);
            return false;
         }

         if (RANDOM.nextDouble() > upgradeData.getSuccess() * data.getSuccess()) {
            Message.UPGRADE_FAIL.format(ChatColor.RED).send(player);
            if (data.destroysOnFail()) {
               event.getCurrentItem().setAmount(0);
            }

            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 2.0F);
            return true;
         } else {
            Message.UPGRADE_SUCCESS.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(event.getCurrentItem())).send(player);
            event.getCurrentItem().setItemMeta(nBT.toItem().getItemMeta());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
            return true;
         }
      } else {
         return false;
      }
   }
}
