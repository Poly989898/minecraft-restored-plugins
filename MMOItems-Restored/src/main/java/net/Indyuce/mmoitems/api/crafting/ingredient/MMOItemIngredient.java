package net.Indyuce.mmoitems.api.crafting.ingredient;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.api.util.ui.QuickNumberRange;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.crafting.ConfigMMOItem;
import net.Indyuce.mmoitems.api.crafting.ingredient.inventory.MMOItemPlayerIngredient;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.DisplayName;
import net.Indyuce.mmoitems.stat.data.MaterialData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class MMOItemIngredient extends Ingredient<MMOItemPlayerIngredient> {
   private final MMOItemTemplate template;
   @NotNull
   private final QuickNumberRange level;
   private final String display;

   public MMOItemIngredient(MMOLineConfig mmoLineConfig) {
      super("mmoitem", mmoLineConfig);
      mmoLineConfig.validate(new String[]{"type", "id"});
      Type type = MMOItems.plugin.getTypes().getOrThrow(mmoLineConfig.getString("type").toUpperCase().replace("-", "_").replace(" ", "_"));
      this.template = MMOItems.plugin.getTemplates().getTemplateOrThrow(type, mmoLineConfig.getString("id"));
      QuickNumberRange quickNumberRange = QuickNumberRange.getFromString(mmoLineConfig.getString("level", ".."));
      if (quickNumberRange != null) {
         this.level = quickNumberRange;
      } else {
         this.level = new QuickNumberRange(null, null);
      }

      this.display = mmoLineConfig.contains("display") ? mmoLineConfig.getString("display") : this.findName();
   }

   public MMOItemIngredient(ConfigMMOItem configMMOItem) {
      super("mmoitem", configMMOItem.getAmount());
      this.template = configMMOItem.getTemplate();
      this.level = new QuickNumberRange(null, null);
      this.display = this.findName();
   }

   public MMOItemTemplate getTemplate() {
      return this.template;
   }

   @Override
   public String getKey() {
      return "mmoitem:"
         + this.template.getType().getId().toLowerCase()
         + (!this.level.hasMax() && !this.level.hasMax() ? "" : "-" + this.level.toString())
         + "_"
         + this.template.getId().toLowerCase();
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#item#", this.display)
         .replace("#level#", !this.level.hasMax() && !this.level.hasMax() ? "" : "lvl." + this.level.toString() + " ")
         .replace("#amount#", String.valueOf(this.getAmount()));
   }

   public boolean matches(MMOItemPlayerIngredient mmoItemPlayerIngredient) {
      if (!mmoItemPlayerIngredient.getType().equals(this.template.getType().getId())) {
         return false;
      } else {
         return !mmoItemPlayerIngredient.getId().equals(this.template.getId())
            ? false
            : SilentNumbers.floor(this.level.getAsDouble(0.0)) == 0 || this.level.inRange(mmoItemPlayerIngredient.getUpgradeLevel());
      }
   }

   @NotNull
   @Override
   public ItemStack generateItemStack(@NotNull RPGPlayer rpgPlayer, boolean flag) {
      MMOItem mmoItem = this.template.newBuilder(rpgPlayer, flag).build();
      ItemStack item = mmoItem.newBuilder().build(flag);
      int count = SilentNumbers.floor(this.level.getAsDouble(0.0));
      if (count != 0 && item.getItemMeta() != null) {
         ItemMeta itemMeta = item.getItemMeta();
         itemMeta.setDisplayName(MythicLib.plugin.parseColors(this.findName()));
         item.setItemMeta(itemMeta);
      }

      item.setAmount(this.getAmount());
      return item;
   }

   @Override
   public String toString() {
      return this.getKey();
   }

   private String findName() {
      String text;
      if (this.template.getBaseItemData().containsKey(ItemStats.NAME)) {
         text = this.template
            .getBaseItemData()
            .get(ItemStats.NAME)
            .toString()
            .replace("<tier-color>", "")
            .replace("<tier-name>", "")
            .replace("<tier-color-cleaned>", "");
      } else if (this.template.getBaseItemData().containsKey(ItemStats.MATERIAL)) {
         text = UtilityMethods.caseOnWords(
            ((MaterialData)this.template.getBaseItemData().get(ItemStats.MATERIAL)).getMaterial().name().toLowerCase().replace("_", " ")
         );
      } else {
         text = "Unrecognized Item";
      }

      return SilentNumbers.floor(this.level.getAsDouble(0.0)) != 0
         ? DisplayName.appendUpgradeLevel(text, SilentNumbers.floor(this.level.getAsDouble(0.0)))
         : text;
   }
}
