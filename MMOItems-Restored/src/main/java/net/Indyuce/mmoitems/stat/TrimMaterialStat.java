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
         for (TrimMaterial var2 : Registry.TRIM_MATERIAL) {
            this.addChoices(new StatChoice(var2.getKey().toString()));
         }
      }
   }

   @Nullable
   @Override
   public StatChoice getChoice(String var1) {
      return super.getChoice(TrimPatternStat.fixNamespacedKey(var1).toString());
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringData var2) {
      if (var1.getMeta() instanceof ArmorMeta) {
         TrimMaterial var3 = (TrimMaterial)Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(var2.toString().toLowerCase()));
         if (var3 != null) {
            ArmorMeta var4 = (ArmorMeta)var1.getMeta();
            ArmorTrim var5 = var4.hasTrim() ? var4.getTrim() : new ArmorTrim(TrimMaterial.AMETHYST, TrimPattern.COAST);
            var4.setTrim(new ArmorTrim(var3, var5.getPattern()));
         }
      }
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      if (var1.getNBT().getItem().getItemMeta() instanceof ArmorMeta) {
         ArmorMeta var2 = (ArmorMeta)var1.getNBT().getItem().getItemMeta();
         if (var2.hasTrim()) {
            var1.setData(this, new StringData(var2.getTrim().getMaterial().getKey().toString()));
         }
      }
   }
}
