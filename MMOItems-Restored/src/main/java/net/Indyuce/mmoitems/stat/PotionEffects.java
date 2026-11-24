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

   public RandomPotionEffectListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof ConfigurationSection, "Must specify a config section");
      return new RandomPotionEffectListData((ConfigurationSection)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.POTION_EFFECTS)
            .enable(
               "Write in the chat the potion effect you want to add.",
               ChatColor.AQUA + "Format: {Effect Name} {Duration} {Amplifier}",
               ChatColor.AQUA + "Other Format: {Effect Name}|{Duration Numeric Formula}|{Amplifier Numeric Formula}"
            );
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains("potion-effect")) {
         Set<String> keys = editionInventory.getEditedSection().getConfigurationSection("potion-effect").getKeys(false);
         String text = new ArrayList<>(keys).get(keys.size() - 1);
         editionInventory.getEditedSection().set("potion-effect." + text, null);
         if (keys.size() <= 1) {
            editionInventory.getEditedSection().set("potion-effect", null);
         }

         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + ChatColor.GOLD + this.formatName(text) + ChatColor.GRAY + ".");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      if (text.contains("|")) {
         String[] texts = text.split("\\|");
         PotionEffectType potionEffectType = PotionEffectType.getByName(texts[0].replace("-", "_"));
         Validate.notNull(
            potionEffectType,
            texts[0]
               + " is not a valid potion effect. All potion effects can be found here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html"
         );
         NumericStatFormula numericStatFormula = new NumericStatFormula(texts[1]);
         NumericStatFormula durationFormula = new NumericStatFormula(texts[2]);
         numericStatFormula.fillConfigurationSection(editionInventory.getEditedSection(), "potion-effect." + potionEffectType.getName() + ".duration");
         durationFormula.fillConfigurationSection(editionInventory.getEditedSection(), "potion-effect." + potionEffectType.getName() + ".amplifier");
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + ChatColor.GOLD + this.formatName(potionEffectType) + ChatColor.GRAY + " successfully added.");
      } else {
         String[] texts = text.split(" ");
         Validate.isTrue(
            texts.length == 3, text + " is not a valid {Effect Name} {Duration} {Amplifier}. Example: 'FAST_DIGGING 30 3' stands for Haste 3 for 30 seconds."
         );
         PotionEffectType potionEffectType = PotionEffectType.getByName(texts[0].replace("-", "_"));
         Validate.notNull(
            potionEffectType,
            texts[0]
               + " is not a valid potion effect. All potion effects can be found here: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html"
         );
         double value = MMOUtils.parseDouble(texts[1]);
         int count = (int)MMOUtils.parseDouble(texts[2]);
         editionInventory.getEditedSection().set("potion-effect." + potionEffectType.getName() + ".duration", value);
         editionInventory.getEditedSection().set("potion-effect." + potionEffectType.getName() + ".amplifier", count);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer()
            .sendMessage(MMOItems.plugin.getPrefix() + ChatColor.GOLD + this.formatName(potionEffectType) + " " + count + ChatColor.GRAY + " successfully added.");
      }
   }

   private String formatName(PotionEffectType potionEffectType) {
      return this.formatName(potionEffectType.getName());
   }

   private String formatName(String text) {
      return UtilityMethods.caseOnWords(text.replace("_", " ").toLowerCase());
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<RandomPotionEffectListData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         RandomPotionEffectListData randomPotionEffectListData = (RandomPotionEffectListData)optional.get();

         for (RandomPotionEffectData effect : randomPotionEffectListData.getEffects()) {
            texts.add(
               ChatColor.GRAY
                  + "* "
                  + ChatColor.GREEN
                  + UtilityMethods.caseOnWords(effect.getType().getName().toLowerCase().replace("_", " "))
                  + " "
                  + effect.getAmplifier().toString()
                  + " "
                  + ChatColor.GRAY
                  + "("
                  + ChatColor.GREEN
                  + effect.getDuration().toString()
                  + ChatColor.GRAY
                  + "s)"
            );
         }
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to add an effect.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the last effect.");
   }

   @NotNull
   public PotionEffectListData getClearStatData() {
      return new PotionEffectListData();
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull PotionEffectListData potionEffectListData) {
      if (itemStackBuilder.getItemStack().getType().name().contains("POTION")) {
         for (PotionEffectData effect : potionEffectListData.getEffects()) {
            ((PotionMeta)itemStackBuilder.getMeta()).addCustomEffect(effect.toEffect(), false);
         }
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull PotionEffectListData potionEffectListData) {
      throw new NotImplementedException();
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      if (readMMOItem.getNBT().getItem().getItemMeta() instanceof PotionMeta) {
         PotionEffectListData potionEffectListData = new PotionEffectListData();

         for (PotionEffect customEffect : ((PotionMeta)readMMOItem.getNBT().getItem().getItemMeta()).getCustomEffects()) {
            potionEffectListData.add(new PotionEffectData(customEffect));
         }

         readMMOItem.setData(this, potionEffectListData);
      }
   }

   @Nullable
   public PotionEffectListData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      throw new NotImplementedException();
   }
}
