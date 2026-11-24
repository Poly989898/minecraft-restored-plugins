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

   public MMOItemIngredient(MMOLineConfig var1) {
      super("mmoitem", var1);
      var1.validate(new String[]{"type", "id"});
      Type var2 = MMOItems.plugin.getTypes().getOrThrow(var1.getString("type").toUpperCase().replace("-", "_").replace(" ", "_"));
      this.template = MMOItems.plugin.getTemplates().getTemplateOrThrow(var2, var1.getString("id"));
      QuickNumberRange var3 = QuickNumberRange.getFromString(var1.getString("level", ".."));
      if (var3 != null) {
         this.level = var3;
      } else {
         this.level = new QuickNumberRange(null, null);
      }

      this.display = var1.contains("display") ? var1.getString("display") : this.findName();
   }

   public MMOItemIngredient(ConfigMMOItem var1) {
      super("mmoitem", var1.getAmount());
      this.template = var1.getTemplate();
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
   public String formatDisplay(String var1) {
      return var1.replace("#item#", this.display)
         .replace("#level#", !this.level.hasMax() && !this.level.hasMax() ? "" : "lvl." + this.level.toString() + " ")
         .replace("#amount#", String.valueOf(this.getAmount()));
   }

   public boolean matches(MMOItemPlayerIngredient var1) {
      if (!var1.getType().equals(this.template.getType().getId())) {
         return false;
      } else {
         return !var1.getId().equals(this.template.getId())
            ? false
            : SilentNumbers.floor(this.level.getAsDouble(0.0)) == 0 || this.level.inRange(var1.getUpgradeLevel());
      }
   }

   @NotNull
   @Override
   public ItemStack generateItemStack(@NotNull RPGPlayer var1, boolean var2) {
      MMOItem var3 = this.template.newBuilder(var1, var2).build();
      ItemStack var4 = var3.newBuilder().build(var2);
      int var5 = SilentNumbers.floor(this.level.getAsDouble(0.0));
      if (var5 != 0 && var4.getItemMeta() != null) {
         ItemMeta var6 = var4.getItemMeta();
         var6.setDisplayName(MythicLib.plugin.parseColors(this.findName()));
         var4.setItemMeta(var6);
      }

      var4.setAmount(this.getAmount());
      return var4;
   }

   @Override
   public String toString() {
      return this.getKey();
   }

   private String findName() {
      String var1;
      if (this.template.getBaseItemData().containsKey(ItemStats.NAME)) {
         var1 = this.template
            .getBaseItemData()
            .get(ItemStats.NAME)
            .toString()
            .replace("<tier-color>", "")
            .replace("<tier-name>", "")
            .replace("<tier-color-cleaned>", "");
      } else if (this.template.getBaseItemData().containsKey(ItemStats.MATERIAL)) {
         var1 = UtilityMethods.caseOnWords(
            ((MaterialData)this.template.getBaseItemData().get(ItemStats.MATERIAL)).getMaterial().name().toLowerCase().replace("_", " ")
         );
      } else {
         var1 = "Unrecognized Item";
      }

      return SilentNumbers.floor(this.level.getAsDouble(0.0)) != 0
         ? DisplayName.appendUpgradeLevel(var1, SilentNumbers.floor(this.level.getAsDouble(0.0)))
         : var1;
   }
}
