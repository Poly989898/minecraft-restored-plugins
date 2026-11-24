package net.Indyuce.mmoitems.stat;

import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.stat.annotation.VersionDependant;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.ChooseStat;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.util.StatChoice;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@VersionDependant(version = {1, 20})
public class TrimMaterialStat extends ChooseStat implements GemStoneStat {
   public TrimMaterialStat() {
      super("TRIM_MATERIAL", Material.LEATHER_CHESTPLATE, "Trim Material", new String[]{"Material to trim your armor with."}, new String[]{"armor", "skin"});
      if (this.isEnabled()) {
         for (TrimMaterial trimMaterial : Registry.TRIM_MATERIAL) {
            this.addChoices(new StatChoice(trimMaterial.getKey().toString()));
         }
      }
   }

   @Nullable
   @Override
   public StatChoice getChoice(String text) {
      return super.getChoice(TrimPatternStat.fixNamespacedKey(text).toString());
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringData stringData) {
      if (itemStackBuilder.getMeta() instanceof ArmorMeta) {
         TrimMaterial trimMaterial = (TrimMaterial)Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(stringData.toString().toLowerCase()));
         if (trimMaterial != null) {
            ArmorMeta meta = (ArmorMeta)itemStackBuilder.getMeta();
            ArmorTrim armorTrim = meta.hasTrim() ? meta.getTrim() : new ArmorTrim(TrimMaterial.AMETHYST, TrimPattern.COAST);
            meta.setTrim(new ArmorTrim(trimMaterial, armorTrim.getPattern()));
         }
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      if (readMMOItem.getNBT().getItem().getItemMeta() instanceof ArmorMeta) {
         ArmorMeta itemMeta = (ArmorMeta)readMMOItem.getNBT().getItem().getItemMeta();
         if (itemMeta.hasTrim()) {
            readMMOItem.setData(this, new StringData(itemMeta.getTrim().getMaterial().getKey().toString()));
         }
      }
   }
}
