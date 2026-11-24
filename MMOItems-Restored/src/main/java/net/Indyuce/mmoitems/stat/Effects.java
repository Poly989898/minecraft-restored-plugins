package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.api.util.message.FFPMMOItems;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.PotionEffectData;
import net.Indyuce.mmoitems.stat.data.PotionEffectListData;
import net.Indyuce.mmoitems.stat.data.random.RandomPotionEffectData;
import net.Indyuce.mmoitems.stat.data.random.RandomPotionEffectListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.PlayerConsumable;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Effects extends ItemStat<RandomPotionEffectListData, PotionEffectListData> implements PlayerConsumable {
   public Effects() {
      super("EFFECTS", Material.POTION, "Effects", new String[]{"The potion effects your", "consumable item grants."}, new String[]{"consumable"});
   }

   public RandomPotionEffectListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a config section");
      return new RandomPotionEffectListData((ConfigurationSection)var1);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.EFFECTS)
            .enable(
               "Write in the chat the permanent potion effect you want to add.",
               ChatColor.AQUA + "Format: {Potion Effect Name}|{Duration Numeric Formula}|{Amplifier Numeric Formula}",
               ChatColor.DARK_RED + "Note: " + ChatColor.RED + "The '|' lines are literal."
            );
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains("effects")) {
         Set<String> var3 = var1.getEditedSection().getConfigurationSection("effects").getKeys(false);
         String var4 = Arrays.asList(var3.toArray(new String[0])).get(var3.size() - 1);
         var1.getEditedSection().set("effects." + var4, null);
         if (var3.size() <= 1) {
            var1.getEditedSection().set("effects", null);
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
      String[] var4 = var2.split("\\|");
      Validate.isTrue(
         var4.length > 1,
         FriendlyFeedbackProvider.quickForConsole(
            FFPMMOItems.get(), "Use this format: $e{Potion Effect Name}|{Duration Numeric Formula}|{Amplifier Numeric Formula}$b.", new String[0]
         )
      );
      PotionEffectType var5 = PotionEffectType.getByName(var4[0].replace("-", "_").replace(" ", "_").toUpperCase());
      Validate.notNull(
         var5,
         var4[0]
            + FriendlyFeedbackProvider.quickForConsole(
               FFPMMOItems.get(),
               " is not a valid potion effect. All potion effects can be found here:$e https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html",
               new String[0]
            )
      );
      NumericStatFormula var6 = new NumericStatFormula(var4[1]);
      NumericStatFormula var7 = var4.length > 2 ? new NumericStatFormula(var4[2]) : new NumericStatFormula(1.0, 0.0, 0.0, 0.0);
      var6.fillConfigurationSection(var1.getEditedSection(), "effects." + var5.getName() + ".duration");
      var7.fillConfigurationSection(var1.getEditedSection(), "effects." + var5.getName() + ".amplifier");
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + var5.getName() + " " + var7 + " successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<RandomPotionEffectListData> var2) {
      var2.ifPresentOrElse(
         var1x -> {
            var1.add(ChatColor.GRAY + "Current Value:");

            for (RandomPotionEffectData var3 : var1x.getEffects()) {
               var1.add(
                  ChatColor.GRAY
                     + "* "
                     + ChatColor.GREEN
                     + UtilityMethods.caseOnWords(var3.getType().getName().toLowerCase().replace("_", " "))
                     + ChatColor.GRAY
                     + " Level: "
                     + ChatColor.GREEN
                     + var3.getAmplifier()
                     + ChatColor.GRAY
                     + " Duration: "
                     + ChatColor.GREEN
                     + var3.getDuration()
               );
            }
         },
         () -> var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None")
      );
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
                        + (var2x.getLevel() < 2 ? "" : " " + MMOUtils.intToRoman(var2x.getLevel()))
                  )
                  .replace("{duration}", MythicLib.plugin.getMMOConfig().decimal.format(var2x.getDuration()))
            )
         );
      var1.getLore().insert("effects", var3);
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @Override
   public String getLegacyTranslationPath() {
      return "effect";
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull PotionEffectListData var1) {
      JsonArray var2 = new JsonArray();

      for (PotionEffectData var4 : var1.getEffects()) {
         JsonObject var5 = new JsonObject();
         var5.addProperty("Type", var4.getType().getName());
         var5.addProperty("Duration", var4.getDuration());
         var5.addProperty("Level", var4.getLevel());
         var2.add(var5);
      }

      ArrayList var6 = new ArrayList();
      var6.add(new ItemTag(this.getNBTPath(), var2.toString()));
      return var6;
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

      try {
         PotionEffectListData var3 = new PotionEffectListData();

         for (JsonElement var6 : new JsonParser().parse((String)var2.getValue()).getAsJsonArray()) {
            if (var6.isJsonObject()) {
               JsonObject var7 = var6.getAsJsonObject();
               var3.add(
                  new PotionEffectData(
                     PotionEffectType.getByName(var7.get("Type").getAsString()), var7.get("Duration").getAsDouble(), var7.get("Level").getAsInt()
                  )
               );
            }
         }

         return var3;
      } catch (JsonSyntaxException | IllegalStateException var8) {
         return null;
      }
   }

   @Override
   public void onConsume(@NotNull VolatileMMOItem var1, @NotNull Player var2, boolean var3) {
      if (var1.hasData(ItemStats.EFFECTS)) {
         PotionEffectListData var4 = (PotionEffectListData)var1.getData(ItemStats.EFFECTS);

         for (PotionEffectData var6 : var4.getEffects()) {
            if (var6 != null) {
               var2.removePotionEffect(var6.getType());
               var2.addPotionEffect(var6.toEffect());
            }
         }
      }
   }
}
