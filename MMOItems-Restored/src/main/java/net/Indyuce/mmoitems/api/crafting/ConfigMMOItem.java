package net.Indyuce.mmoitems.api.crafting;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ConfigMMOItem {
   private final MMOItemTemplate template;
   private final int amount;
   private ItemStack preview;

   public ConfigMMOItem(ConfigurationSection var1) {
      Validate.notNull(var1, "Could not read MMOItem config");
      Validate.isTrue(var1.contains("type") && var1.contains("id"), "Config must contain type and ID");
      Type var2 = MMOItems.plugin.getTypes().getOrThrow(var1.getString("type").toUpperCase().replace("-", "_").replace(" ", "_"));
      this.template = MMOItems.plugin.getTemplates().getTemplateOrThrow(var2, var1.getString("id"));
      this.amount = Math.max(1, var1.getInt("amount"));
   }

   public ConfigMMOItem(MMOItemTemplate var1, int var2) {
      Validate.notNull(var1, "Could not register recipe output");
      this.template = var1;
      this.amount = Math.max(1, var2);
   }

   @NotNull
   public ItemStack generate(@NotNull RPGPlayer var1) {
      ItemStack var2 = this.template.newBuilder(var1).build().newBuilder().build();
      var2.setAmount(this.amount);
      return var2;
   }

   public MMOItemTemplate getTemplate() {
      return this.template;
   }

   public ItemStack getPreview() {
      return this.preview == null
         ? (this.preview = new MMOItemBuilder(this.template, 0, null, true).build().newBuilder().build(true)).clone()
         : this.preview.clone();
   }

   public int getAmount() {
      return this.amount;
   }
}
