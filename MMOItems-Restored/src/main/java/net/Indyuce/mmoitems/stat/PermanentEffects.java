package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonElement;
import io.lumine.mythic.lib.gson.JsonObject;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
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

public class PermanentEffects extends ItemStat<RandomPotionEffectListData, PotionEffectListData> {
   public PermanentEffects() {
      super(
         "PERM_EFFECTS",
         Material.POTION,
         "Permanent Effects",
         new String[]{"The potion effects your", "item grants to the holder."},
         new String[]{"!miscellaneous", "!block", "all"}
      );
   }

   public RandomPotionEffectListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a config section");
      ConfigurationSection var2 = (ConfigurationSection)var1;
      RandomPotionEffectListData var3 = new RandomPotionEffectListData();

      for (String var5 : var2.getKeys(false)) {
         PotionEffectType var6 = PotionEffectType.getByName(var5.toUpperCase().replace("-", "_").replace(" ", "_"));
         Validate.notNull(var6, "Could not find potion effect type named '" + var5 + "'");
         var3.add(new RandomPotionEffectData(var6, new NumericStatFormula(var2.get(var5))));
      }

      return var3;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.PERM_EFFECTS)
            .enable("Write in the chat the permanent potion effect you want to add.", ChatColor.AQUA + "Format: {Effect Name} {Amplifier Numeric Formula}");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains("perm-effects")) {
         Set<String> var3 = var1.getEditedSection().getConfigurationSection("perm-effects").getKeys(false);
         String var4 = new ArrayList<>(var3).get(var3.size() - 1);
         var1.getEditedSection().set("perm-effects." + var4, null);
         if (var3.size() <= 1) {
            var1.getEditedSection().set("perm-effects", null);
         }

         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + var4.substring(0, 1).toUpperCase() + var4.substring(1).toLowerCase() + ".");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String[] var4 = var2.split(" ");
      Validate.isTrue(
         var4.length >= 2,
         "Use this format: {Effect Name} {Effect Amplifier Numeric Formula}. Example: 'speed 1 0.3' stands for Speed 1, plus 0.3 level per item level (rounded up to lower int)"
      );
      PotionEffectType var5 = PotionEffectType.getByName(var4[0].replace("-", "_"));
      Validate.notNull(
         var5,
         var4[0]
            + " is not a valid potion effect. All potion effects can be found here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html"
      );
      NumericStatFormula var6 = new NumericStatFormula(var2.substring(var2.indexOf(" ") + 1));
      var6.fillConfigurationSection(var1.getEditedSection(), "perm-effects." + var5.getName());
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + ChatColor.GOLD + var5.getName() + " " + var6 + ChatColor.GRAY + " successfully added.");
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
                  + UtilityMethods.caseOnWords(var5.getType().getName().replace("_", " ").toLowerCase())
                  + " "
                  + var5.getAmplifier().toString()
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
               var4.replace("{effect}", MMOItems.plugin.getLanguage().getPotionEffectName(var2x.getType()) + " " + MMOUtils.intToRoman(var2x.getLevel()))
            )
         );
      var1.getLore().insert("perm-effects", var3);
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @Override
   public String getLegacyTranslationPath() {
      return "perm-effect";
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull PotionEffectListData var1) {
      ArrayList var2 = new ArrayList();
      JsonObject var3 = new JsonObject();

      for (PotionEffectData var5 : var1.getEffects()) {
         var3.addProperty(var5.getType().getName(), var5.getLevel());
      }

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
      if (var2 != null) {
         try {
            PotionEffectListData var3 = new PotionEffectListData();
            JsonElement var4 = new JsonParser().parse((String)var2.getValue());
            var4.getAsJsonObject()
               .entrySet()
               .forEach(var1x -> var3.add(new PotionEffectData(PotionEffectType.getByName((String)var1x.getKey()), ((JsonElement)var1x.getValue()).getAsInt())));
            return var3;
         } catch (JsonSyntaxException | IllegalStateException var5) {
         }
      }

      return null;
   }
}
