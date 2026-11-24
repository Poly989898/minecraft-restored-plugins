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
import net.Indyuce.mmoitems.api.util.NumericStatFormula;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.PotionEffectData;
import net.Indyuce.mmoitems.stat.data.PotionEffectListData;
import net.Indyuce.mmoitems.stat.data.random.RandomPotionEffectData;
import net.Indyuce.mmoitems.stat.data.random.RandomPotionEffectListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotionEffects extends ItemStat<RandomPotionEffectListData, PotionEffectListData> {
   public PotionEffects() {
      super(
         "POTION_EFFECT",
         Material.POTION,
         "Potion Effects",
         new String[]{"The effects of your potion.", "(May have an impact on color).", "Does NOT support tipped arrows."},
         new String[0],
         Material.POTION,
         Material.SPLASH_POTION,
         Material.LINGERING_POTION
      );
   }

   public RandomPotionEffectListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof ConfigurationSection, "Must specify a config section");
      return new RandomPotionEffectListData((ConfigurationSection)var1);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.POTION_EFFECTS)
            .enable(
               "Write in the chat the potion effect you want to add.",
               ChatColor.AQUA + "Format: {Effect Name} {Duration} {Amplifier}",
               ChatColor.AQUA + "Other Format: {Effect Name}|{Duration Numeric Formula}|{Amplifier Numeric Formula}"
            );
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains("potion-effect")) {
         Set<String> var3 = var1.getEditedSection().getConfigurationSection("potion-effect").getKeys(false);
         String var4 = new ArrayList<>(var3).get(var3.size() - 1);
         var1.getEditedSection().set("potion-effect." + var4, null);
         if (var3.size() <= 1) {
            var1.getEditedSection().set("potion-effect", null);
         }

         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + ChatColor.GOLD + this.formatName(var4) + ChatColor.GRAY + ".");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      if (var2.contains("|")) {
         String[] var9 = var2.split("\\|");
         PotionEffectType var10 = PotionEffectType.getByName(var9[0].replace("-", "_"));
         Validate.notNull(
            var10,
            var9[0]
               + " is not a valid potion effect. All potion effects can be found here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html"
         );
         NumericStatFormula var11 = new NumericStatFormula(var9[1]);
         NumericStatFormula var7 = new NumericStatFormula(var9[2]);
         var11.fillConfigurationSection(var1.getEditedSection(), "potion-effect." + var10.getName() + ".duration");
         var7.fillConfigurationSection(var1.getEditedSection(), "potion-effect." + var10.getName() + ".amplifier");
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + ChatColor.GOLD + this.formatName(var10) + ChatColor.GRAY + " successfully added.");
      } else {
         String[] var4 = var2.split(" ");
         Validate.isTrue(
            var4.length == 3, var2 + " is not a valid {Effect Name} {Duration} {Amplifier}. Example: 'FAST_DIGGING 30 3' stands for Haste 3 for 30 seconds."
         );
         PotionEffectType var5 = PotionEffectType.getByName(var4[0].replace("-", "_"));
         Validate.notNull(
            var5,
            var4[0]
               + " is not a valid potion effect. All potion effects can be found here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html"
         );
         double var6 = MMOUtils.parseDouble(var4[1]);
         int var8 = (int)MMOUtils.parseDouble(var4[2]);
         var1.getEditedSection().set("potion-effect." + var5.getName() + ".duration", var6);
         var1.getEditedSection().set("potion-effect." + var5.getName() + ".amplifier", var8);
         var1.registerTemplateEdition();
         var1.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + ChatColor.GOLD + this.formatName(var5) + " " + var8 + ChatColor.GRAY + " successfully added.");
      }
   }

   private String formatName(PotionEffectType var1) {
      return this.formatName(var1.getName());
   }

   private String formatName(String var1) {
      return UtilityMethods.caseOnWords(var1.replace("_", " ").toLowerCase());
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
                  + " "
                  + var5.getAmplifier().toString()
                  + " "
                  + ChatColor.GRAY
                  + "("
                  + ChatColor.GREEN
                  + var5.getDuration().toString()
                  + ChatColor.GRAY
                  + "s)"
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
      if (var1.getItemStack().getType().name().contains("POTION")) {
         for (PotionEffectData var4 : var2.getEffects()) {
            ((PotionMeta)var1.getMeta()).addCustomEffect(var4.toEffect(), false);
         }
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull PotionEffectListData var1) {
      throw new NotImplementedException();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      if (var1.getNBT().getItem().getItemMeta() instanceof PotionMeta) {
         PotionEffectListData var2 = new PotionEffectListData();

         for (PotionEffect var4 : ((PotionMeta)var1.getNBT().getItem().getItemMeta()).getCustomEffects()) {
            var2.add(new PotionEffectData(var4));
         }

         var1.setData(this, var2);
      }
   }

   @Nullable
   public PotionEffectListData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      throw new NotImplementedException();
   }
}
