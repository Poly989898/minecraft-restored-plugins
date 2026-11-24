package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.interaction.projectile.ArrowPotionEffectArrayItem;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.PotionEffectData;
import net.Indyuce.mmoitems.stat.data.PotionEffectListData;
import net.Indyuce.mmoitems.stat.data.random.RandomPotionEffectData;
import net.Indyuce.mmoitems.stat.data.random.RandomPotionEffectListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArrowPotionEffects extends ItemStat<RandomPotionEffectListData, PotionEffectListData> {
   public ArrowPotionEffects() {
      super(
         "ARROW_POTION_EFFECTS",
         Material.TIPPED_ARROW,
         "Arrow Potion Effects",
         new String[]{"The effects to be applied when", "entities are shot by this bow"},
         new String[]{"bow", "crossbow"}
      );
   }

   public RandomPotionEffectListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a config section");
      return new RandomPotionEffectListData((ConfigurationSection)var1);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.ARROW_POTION_EFFECTS)
            .enable("Write in the chat the potion effect you want to add.", ChatColor.AQUA + "Format: [POTION_EFFECT] [DURATION] [AMPLIFIER]");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains("arrow-potion-effects")) {
         Set<String> var3 = var1.getEditedSection().getConfigurationSection("arrow-potion-effects").getKeys(false);
         String var4 = Arrays.asList(var3.toArray(new String[0])).get(var3.size() - 1);
         var1.getEditedSection().set("arrow-potion-effects." + var4, null);
         if (var3.size() <= 1) {
            var1.getEditedSection().set("arrow-potion-effects", null);
         }

         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(
               MMOItems.plugin.getPrefix()
                  + "Successfully removed "
                  + var4.substring(0, 1).toUpperCase()
                  + var4.substring(1).toLowerCase()
                  + ChatColor.GRAY
                  + "."
            );
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String[] var4 = var2.split(" ");
      Validate.isTrue(
         var4.length == 3, var2 + " is not a valid [POTION_EFFECT] [DURATION] [AMPLIFIER]. Example: 'FAST_DIGGING 30 3' stands for Haste 3 for 30 seconds."
      );
      PotionEffectType var5 = null;

      for (PotionEffectType var9 : PotionEffectType.values()) {
         if (var9 != null && var9.getName().equalsIgnoreCase(var4[0].replace("-", "_"))) {
            var5 = var9;
            break;
         }
      }

      Validate.notNull(var5, var4[0] + " is not a valid potion effect.");
      double var10 = MMOUtils.parseDouble(var4[1]);
      int var11 = (int)MMOUtils.parseDouble(var4[2]);
      ConfigurationSection var12 = var1.getEditedSection().createSection("arrow-potion-effects." + var5.getName());
      var12.set("duration", var10);
      var12.set("amplifier", var11);
      var1.getEditedSection().set("arrow-potion-effects." + var5.getName(), var12);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + var5.getName() + " " + var11 + " successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<RandomPotionEffectListData> var2) {
      if (var2.isPresent()) {
         var1.add(ChatColor.GRAY + "Current Value:");
         RandomPotionEffectListData var3 = (RandomPotionEffectListData)var2.get();

         for (RandomPotionEffectData var5 : var3.getEffects()) {
            var1.add(
               ChatColor.GRAY
                  + "* "
                  + ChatColor.GREEN
                  + UtilityMethods.caseOnWords(var5.getType().getName().toLowerCase().replace("_", " "))
                  + ChatColor.GRAY
                  + " Level: "
                  + ChatColor.GREEN
                  + var5.getAmplifier()
                  + ChatColor.GRAY
                  + " Duration: "
                  + ChatColor.GREEN
                  + var5.getDuration()
            );
         }
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to add an effect.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove the last effect.");
   }

   @NotNull
   public PotionEffectListData getClearStatData() {
      return new PotionEffectListData();
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull PotionEffectListData var2) {
      ArrayList var3 = new ArrayList();
      String var4 = this.getGeneralStatFormat();
      var2.getEffects()
         .forEach(
            var2x -> var3.add(
               var4.replace(
                  "{effect}",
                  MMOItems.plugin.getLanguage().getPotionEffectName(var2x.getType())
                     + " "
                     + MMOUtils.intToRoman(var2x.getLevel())
                     + "("
                     + MythicLib.plugin.getMMOConfig().decimal.format(var2x.getDuration())
                     + "s)"
               )
            )
         );
      var1.getLore().insert(this.getPath(), var3);
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull PotionEffectListData var1) {
      ArrayList var2 = new ArrayList();
      JsonArray var3 = new JsonArray();
      var1.getEffects().forEach(var1x -> {
         JsonObject var2x = new JsonObject();
         var2x.addProperty("type", var1x.getType().getName());
         var2x.addProperty("level", var1x.getLevel());
         var2x.addProperty("duration", var1x.getDuration());
         var3.add(var2x);
      });
      var2.add(new ItemTag(this.getNBTPath(), var3.toString()));
      return var2;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING));
      }

      PotionEffectListData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public PotionEffectListData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 == null) {
         return null;
      }

      PotionEffectListData var3 = new PotionEffectListData();

      for (ArrowPotionEffectArrayItem var7 : (ArrowPotionEffectArrayItem[])MythicLib.plugin
         .getJson()
         .parse((String)var2.getValue(), ArrowPotionEffectArrayItem[].class)) {
         var3.add(new PotionEffectData(PotionEffectType.getByName(var7.type), var7.duration, var7.level));
      }

      return var3;
   }
}
