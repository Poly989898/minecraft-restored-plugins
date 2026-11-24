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

   public ConfigMMOItem(ConfigurationSection section) {
      Validate.notNull(section, "Could not read MMOItem config");
      Validate.isTrue(section.contains("type") && section.contains("id"), "Config must contain type and ID");
      Type type = MMOItems.plugin.getTypes().getOrThrow(section.getString("type").toUpperCase().replace("-", "_").replace(" ", "_"));
      this.template = MMOItems.plugin.getTemplates().getTemplateOrThrow(type, section.getString("id"));
      this.amount = Math.max(1, section.getInt("amount"));
   }

   public ConfigMMOItem(MMOItemTemplate mmoItemTemplate, int count) {
      Validate.notNull(mmoItemTemplate, "Could not register recipe output");
      this.template = mmoItemTemplate;
      this.amount = Math.max(1, count);
   }

   @NotNull
   public ItemStack generate(@NotNull RPGPlayer rpgPlayer) {
      ItemStack item = this.template.newBuilder(rpgPlayer).build().newBuilder().build();
      item.setAmount(this.amount);
      return item;
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
