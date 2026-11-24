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
public class TrimPatternStat extends ChooseStat implements GemStoneStat {
   public TrimPatternStat() {
      super("TRIM_PATTERN", Material.LEATHER_CHESTPLATE, "Trim Pattern", new String[]{"Pattern of trimmed armor."}, new String[]{"armor", "skin"});
      if (this.isEnabled()) {
         for (TrimPattern var2 : Registry.TRIM_PATTERN) {
            this.addChoices(new StatChoice(var2.getKey().toString()));
         }
      }
   }

   public static NamespacedKey fixNamespacedKey(String var0) {
      return !var0.contains(":") ? NamespacedKey.minecraft(var0) : NamespacedKey.fromString(var0);
   }

   @Nullable
   @Override
   public StatChoice getChoice(String var1) {
      return super.getChoice(fixNamespacedKey(var1).toString());
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringData var2) {
      if (var1.getMeta() instanceof ArmorMeta) {
         TrimPattern var3 = (TrimPattern)Registry.TRIM_PATTERN.get(fixNamespacedKey(var2.toString()));
         if (var3 != null) {
            ArmorMeta var4 = (ArmorMeta)var1.getMeta();
            ArmorTrim var5 = var4.hasTrim() ? var4.getTrim() : new ArmorTrim(TrimMaterial.AMETHYST, TrimPattern.COAST);
            var4.setTrim(new ArmorTrim(var5.getMaterial(), var3));
         }
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      if (var1.getNBT().getItem().getItemMeta() instanceof ArmorMeta) {
         ArmorMeta var2 = (ArmorMeta)var1.getNBT().getItem().getItemMeta();
         if (var2.hasTrim()) {
            var1.setData(this, new StringData(var2.getTrim().getPattern().getKey().toString()));
         }
      }
   }
}
