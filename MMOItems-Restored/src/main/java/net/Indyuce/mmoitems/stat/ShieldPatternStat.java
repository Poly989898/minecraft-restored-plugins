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

   public ShieldPatternData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a config section");
      ConfigurationSection var2 = (ConfigurationSection)var1;
      ShieldPatternData var3 = new ShieldPatternData(
         var2.contains("color") ? org.bukkit.DyeColor.valueOf(var2.getString("color").toUpperCase().replace("-", "_").replace(" ", "_")) : null
      );

      for (String var5 : var2.getKeys(false)) {
         if (!var5.equalsIgnoreCase("color")) {
            String var6 = var2.getString(var5 + ".pattern").toUpperCase().replace("-", "_").replace(" ", "_");
            PatternType var7 = PatternType.valueOf(var6);
            var6 = var2.getString(var5 + ".color").toUpperCase().replace("-", "_").replace(" ", "_");
            org.bukkit.DyeColor var8 = org.bukkit.DyeColor.valueOf(var6);
            var3.add(new Pattern(var8, var7));
         }
      }

      return var3;
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull ShieldPatternData var2) {
      BlockStateMeta var3 = (BlockStateMeta)var1.getMeta();
      Banner var4 = (Banner)var3.getBlockState();
      var4.setBaseColor(var2.getBaseColor());
      var4.setPatterns(var2.getPatterns());
      ((BlockStateMeta)var1.getMeta()).setBlockState(var4);
      var1.getMeta().addItemFlags(new ItemFlag[]{HidePotionEffects.ITEM_FLAG});
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull ShieldPatternData var1) {
      return new ArrayList<>();
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.SHIELD_PATTERN, 0).enable("Write in the chat the color of your shield.");
      }

      if (var2.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
         var1.getEditedSection().set("shield-pattern.color", null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully reset the shield color.");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF) {
         new StatEdition(var1, ItemStats.SHIELD_PATTERN, 1)
            .enable("Write in the chat the pattern you want to add.", ChatColor.AQUA + "Format: [PATTERN_TYPE] [DYE_COLOR]");
      }

      if (var2.getAction() == InventoryAction.DROP_ONE_SLOT && var1.getEditedSection().contains("shield-pattern")) {
         Set<String> var3 = var1.getEditedSection().getConfigurationSection("shield-pattern").getKeys(false);
         String var4 = new ArrayList<>(var3).get(var3.size() - 1);
         if (var4.equalsIgnoreCase("color")) {
            return;
         }

         var1.getEditedSection().set("shield-pattern." + var4, null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed the last pattern.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      int var4 = (Integer)var3[0];
      if (var4 == 1) {
         String[] var9 = var2.split(" ");
         Validate.isTrue(var9.length == 2, var2 + " is not a valid [PATTERN_TYPE] [DYE_COLOR].");
         PatternType var6 = PatternType.valueOf(var9[0].toUpperCase().replace("-", "_").replace(" ", "_"));
         org.bukkit.DyeColor var7 = org.bukkit.DyeColor.valueOf(var9[1].toUpperCase().replace("-", "_").replace(" ", "_"));
         int var8 = this.getNextAvailableKey(var1.getEditedSection().getConfigurationSection("shield-pattern"));
         Validate.isTrue(var8 >= 0, "You can have more than 100 shield patterns on a single item.");
         var1.getEditedSection().set("shield-pattern." + var8 + ".pattern", var6.name());
         var1.getEditedSection().set("shield-pattern." + var8 + ".color", var7.name());
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + UtilityMethods.caseOnWords(var6.name().toLowerCase().replace("_", " ")) + " successfully added.");
      } else {
         org.bukkit.DyeColor var5 = org.bukkit.DyeColor.valueOf(var2.toUpperCase().replace("-", "_").replace(" ", "_"));
         var1.getEditedSection().set("shield-pattern.color", var5.name());
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Shield color successfully changed.");
      }
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<ShieldPatternData> var2) {
      if (var2.isPresent()) {
         var1.add(ChatColor.GRAY + "Current Value:");
         ShieldPatternData var3 = (ShieldPatternData)var2.get();
         var1.add(
            ChatColor.GRAY
               + "* Base Color: "
               + (
                  var3.getBaseColor() != null
                     ? ChatColor.GREEN + UtilityMethods.caseOnWords(var3.getBaseColor().name().toLowerCase().replace("_", " "))
                     : ChatColor.RED + "None"
               )
         );
         var3.getPatterns()
            .forEach(
               var1x -> var1.add(
                  ChatColor.GRAY + "* " + ChatColor.GREEN + var1x.getPattern().name() + ChatColor.GRAY + " - " + ChatColor.GREEN + var1x.getColor().name()
               )
            );
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Left Click to change the shield color.");
      var1.add(ChatColor.YELLOW + "►" + " Shift Left Click to reset the shield color.");
      var1.add(ChatColor.YELLOW + "►" + " Right Click to add a pattern.");
      var1.add(ChatColor.YELLOW + "►" + " Drop to remove the last pattern.");
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      if (var1.getNBT().getItem().getItemMeta() instanceof BlockStateMeta
         && ((BlockStateMeta)var1.getNBT().getItem().getItemMeta()).hasBlockState()
         && ((BlockStateMeta)var1.getNBT().getItem().getItemMeta()).getBlockState() instanceof Banner) {
         Banner var2 = (Banner)((BlockStateMeta)var1.getNBT().getItem().getItemMeta()).getBlockState();
         ShieldPatternData var3 = new ShieldPatternData(var2.getBaseColor());
         var3.addAll(var2.getPatterns());
         var1.setData(ItemStats.SHIELD_PATTERN, var3);
      }
   }

   @NotNull
   public ShieldPatternData getClearStatData() {
      return new ShieldPatternData(org.bukkit.DyeColor.YELLOW);
   }

   @Nullable
   public ShieldPatternData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      return null;
   }

   private int getNextAvailableKey(ConfigurationSection var1) {
      for (int var2 = 0; var2 < 100; var2++) {
         if (!var1.contains("" + var2)) {
            return var2;
         }
      }

      return -1;
   }
}
