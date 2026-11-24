package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.ShieldPatternData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShieldPatternStat extends ItemStat<ShieldPatternData, ShieldPatternData> {
   public ShieldPatternStat() {
      super("SHIELD_PATTERN", Material.SHIELD, "Shield Pattern", new String[]{"The color & patterns", "of your shield."}, new String[0], Material.SHIELD);
   }

   public ShieldPatternData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a config section");
      ConfigurationSection section = (ConfigurationSection)obj;
      ShieldPatternData shieldPatternData = new ShieldPatternData(
         section.contains("color") ? org.bukkit.DyeColor.valueOf(section.getString("color").toUpperCase().replace("-", "_").replace(" ", "_")) : null
      );

      for (String key : section.getKeys(false)) {
         if (!key.equalsIgnoreCase("color")) {
            String text = section.getString(key + ".pattern").toUpperCase().replace("-", "_").replace(" ", "_");
            PatternType patternType = PatternType.valueOf(text);
            text = section.getString(key + ".color").toUpperCase().replace("-", "_").replace(" ", "_");
            org.bukkit.DyeColor dyeColor = org.bukkit.DyeColor.valueOf(text);
            shieldPatternData.add(new Pattern(dyeColor, patternType));
         }
      }

      return shieldPatternData;
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull ShieldPatternData shieldPatternData) {
      BlockStateMeta meta = (BlockStateMeta)itemStackBuilder.getMeta();
      Banner blockState = (Banner)meta.getBlockState();
      blockState.setBaseColor(shieldPatternData.getBaseColor());
      blockState.setPatterns(shieldPatternData.getPatterns());
      ((BlockStateMeta)itemStackBuilder.getMeta()).setBlockState(blockState);
      itemStackBuilder.getMeta().addItemFlags(new ItemFlag[]{HidePotionEffects.ITEM_FLAG});
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ShieldPatternData shieldPatternData) {
      return new ArrayList<>();
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.SHIELD_PATTERN, 0).enable("Write in the chat the color of your shield.");
      }

      if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
         editionInventory.getEditedSection().set("shield-pattern.color", null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the shield color.");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF) {
         new StatEdition(editionInventory, ItemStats.SHIELD_PATTERN, 1)
            .enable("Write in the chat the pattern you want to add.", ChatColor.AQUA + "Format: [PATTERN_TYPE] [DYE_COLOR]");
      }

      if (event.getAction() == InventoryAction.DROP_ONE_SLOT && editionInventory.getEditedSection().contains("shield-pattern")) {
         Set<String> keys = editionInventory.getEditedSection().getConfigurationSection("shield-pattern").getKeys(false);
         String text = new ArrayList<>(keys).get(keys.size() - 1);
         if (text.equalsIgnoreCase("color")) {
            return;
         }

         editionInventory.getEditedSection().set("shield-pattern." + text, null);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed the last pattern.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      int count = (Integer)objs[0];
      if (count == 1) {
         String[] texts = text.split(" ");
         Validate.isTrue(texts.length == 2, text + " is not a valid [PATTERN_TYPE] [DYE_COLOR].");
         PatternType patternType = PatternType.valueOf(texts[0].toUpperCase().replace("-", "_").replace(" ", "_"));
         org.bukkit.DyeColor dyeColor = org.bukkit.DyeColor.valueOf(texts[1].toUpperCase().replace("-", "_").replace(" ", "_"));
         int nextAvailableKey = this.getNextAvailableKey(editionInventory.getEditedSection().getConfigurationSection("shield-pattern"));
         Validate.isTrue(nextAvailableKey >= 0, "You can have more than 100 shield patterns on a single item.");
         editionInventory.getEditedSection().set("shield-pattern." + nextAvailableKey + ".pattern", patternType.name());
         editionInventory.getEditedSection().set("shield-pattern." + nextAvailableKey + ".color", dyeColor.name());
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + UtilityMethods.caseOnWords(patternType.name().toLowerCase().replace("_", " ")) + " successfully added.");
      } else {
         org.bukkit.DyeColor dyeColor = org.bukkit.DyeColor.valueOf(text.toUpperCase().replace("-", "_").replace(" ", "_"));
         editionInventory.getEditedSection().set("shield-pattern.color", dyeColor.name());
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Shield color successfully changed.");
      }
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<ShieldPatternData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         ShieldPatternData shieldPatternData = (ShieldPatternData)optional.get();
         texts.add(
            ChatColor.GRAY
               + "* Base Color: "
               + (
                  shieldPatternData.getBaseColor() != null
                     ? ChatColor.GREEN + UtilityMethods.caseOnWords(shieldPatternData.getBaseColor().name().toLowerCase().replace("_", " "))
                     : ChatColor.RED + "None"
               )
         );
         shieldPatternData.getPatterns()
            .forEach(
               patternData -> texts.add(
                  ChatColor.GRAY + "* " + ChatColor.GREEN + patternData.getPattern().name() + ChatColor.GRAY + " - " + ChatColor.GREEN + patternData.getColor().name()
               )
            );
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Left Click to change the shield color.");
      texts.add(ChatColor.YELLOW + "►" + " Shift Left Click to reset the shield color.");
      texts.add(ChatColor.YELLOW + "►" + " Right Click to add a pattern.");
      texts.add(ChatColor.YELLOW + "►" + " Drop to remove the last pattern.");
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      if (readMMOItem.getNBT().getItem().getItemMeta() instanceof BlockStateMeta
         && ((BlockStateMeta)readMMOItem.getNBT().getItem().getItemMeta()).hasBlockState()
         && ((BlockStateMeta)readMMOItem.getNBT().getItem().getItemMeta()).getBlockState() instanceof Banner) {
         Banner blockState = (Banner)((BlockStateMeta)readMMOItem.getNBT().getItem().getItemMeta()).getBlockState();
         ShieldPatternData shieldPatternData = new ShieldPatternData(blockState.getBaseColor());
         shieldPatternData.addAll(blockState.getPatterns());
         readMMOItem.setData(ItemStats.SHIELD_PATTERN, shieldPatternData);
      }
   }

   @NotNull
   public ShieldPatternData getClearStatData() {
      return new ShieldPatternData(org.bukkit.DyeColor.YELLOW);
   }

   @Nullable
   public ShieldPatternData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      return null;
   }

   private int getNextAvailableKey(ConfigurationSection section) {
      for (int i = 0; i < 100; i++) {
         if (!section.contains("" + i)) {
            return i;
         }
      }

      return -1;
   }
}
