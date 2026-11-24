package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.comp.adventure.AdventureParser;
import java.util.ArrayList;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.NameData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import net.Indyuce.mmoitems.stat.type.StringStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DisplayName extends StringStat implements GemStoneStat {
   public DisplayName() {
      super("NAME", Material.NAME_TAG, "Display Name", new String[]{"The item display name."}, new String[0]);
   }

   @Override
   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringData var2) {
      var1.getMMOItem().computeStatHistory(this);
      ItemTier var3 = var1.getMMOItem().getTier();
      AdventureParser var4 = MythicLib.plugin.getAdventureParser();
      String var5 = var2.toString();
      var5 = var5.replace("<tier-name>", var3 != null ? var4.stripColors(var3.getUnparsedName()) : "")
         .replace("<tier-color>", var3 != null ? var4.lastColor(var3.getUnparsedName(), true) : "&f")
         .replace("<tier-color-cleaned>", var3 != null ? var4.lastColor(var3.getUnparsedName(), false) : "");
      var5 = this.cropUpgrade(var5);
      if (var1.getMMOItem().hasUpgradeTemplate()) {
         var5 = appendUpgradeLevel(var5, var1.getMMOItem().getUpgradeLevel());
      }

      var1.getMeta().setDisplayName(var5);
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   String cropUpgrade(@NotNull String var1) {
      String var2 = MMOItems.plugin.getConfig().getString("item-upgrading.name-suffix", " &8(&e+#lvl#&8)");
      if (var2 != null && !var2.isEmpty()) {
         int var3 = var2.indexOf("#lvl#");
         if (var3 < 0) {
            return var1;
         }

         String var4 = var2.substring(0, var3);
         String var5 = var2.substring(var3 + "#lvl#".length());
         String var6 = var4.replace("+", "-");
         String var7 = var5.replace("+", "-");
         if (var1.contains(var4)) {
            int var8 = var1.indexOf(var4);
            int var9 = var1.lastIndexOf(var5);
            if (var9 < 0) {
               var9 = var1.length();
            } else {
               var9 += var5.length();
            }

            String var10 = var1.substring(0, var8);
            String var11 = var1.substring(var9);
            var1 = var10 + var11;
         }

         if (var1.contains(var6)) {
            int var12 = var1.indexOf(var6);
            int var14 = var1.lastIndexOf(var7);
            if (var14 < 0) {
               var14 = var1.length();
            } else {
               var14 += var7.length();
            }

            String var16 = var1.substring(0, var12);
            String var17 = var1.substring(var14);
            var1 = var16 + var17;
         }

         return var1;
      } else {
         return var1;
      }
   }

   @NotNull
   public static String appendUpgradeLevel(@NotNull String var0, int var1) {
      String var2 = MMOItems.plugin.getConfig().getString("item-upgrading.name-suffix");
      if (var2 != null && var1 != 0) {
         String var3 = levelPrefix(var2, var1);
         return var0 + var3;
      } else {
         return var0;
      }
   }

   @NotNull
   public static String levelPrefix(@NotNull String var0, int var1) {
      return var0.replace("#lvl#", String.valueOf(var1)).replace("+-", "-");
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringData var1) {
      Validate.isTrue(var1 instanceof NameData, "Data is not name data");
      NameData var2 = (NameData)var1;
      ArrayList var3 = new ArrayList();
      var3.add(new ItemTag(this.getNBTPath(), var2.getMainName()));
      if (var2.hasPrefixes()) {
         var3.add(var2.compressPrefixes(this.getNBTPath() + "_PRE"));
      }

      if (var2.hasSuffixes()) {
         var3.add(var2.compressSuffixes(this.getNBTPath() + "_SUF"));
      }

      return var3;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      boolean var3 = false;
      ItemTag var4 = ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING);
      if (var4 != null) {
         ItemTag var5 = ItemTag.getTagAtPath(this.getNBTPath() + "_PRE", var1.getNBT(), SupportedNBTTagValues.STRING);
         ItemTag var6 = ItemTag.getTagAtPath(this.getNBTPath() + "_SUF", var1.getNBT(), SupportedNBTTagValues.STRING);
         var2.add(var4);
         var2.add(var5);
         var2.add(var6);
         if (var1.getNBT().getItem().getItemMeta().hasDisplayName()) {
            var3 = true;
         }
      } else {
         if (!var1.getNBT().getItem().getItemMeta().hasDisplayName()) {
            return;
         }

         var2.add(new ItemTag(this.getNBTPath(), this.cropUpgrade(var1.getNBT().getItem().getItemMeta().getDisplayName())));
      }

      NameData var10 = (NameData)this.getLoadedNBT(var2);
      if (var10 != null) {
         String var11 = null;
         if (var3) {
            var11 = var1.getNBT().getItem().getItemMeta().getDisplayName();
            String var7 = ChatColor.stripColor(var11);
            if (!var11.equals(var7)) {
               var11 = null;
            } else {
               var10.setString(var11);
            }
         }

         var1.setData(this, var10);
         if (var3 && var11 != null && !var1.hasStatHistory(this)) {
            ItemTag var12 = ItemTag.getTagAtPath("HSTRY_" + this.getId(), var1.getNBT(), SupportedNBTTagValues.STRING);
            if (var12 != null) {
               StatHistory var8 = StatHistory.fromNBTString(var1, (String)var12.getValue());
               if (var8 != null) {
                  NameData var9 = (NameData)var8.getOriginalData();
                  var9.setString(var11);
                  var1.setStatHistory(this, var8);
               }
            }
         }
      }
   }

   @Nullable
   @Override
   public StringData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         String var3 = (String)var2.getValue();
         NameData var4 = new NameData(var3);
         var4.readPrefixes(ItemTag.getTagAtPath(this.getNBTPath() + "_PRE", var1));
         var4.readSuffixes(ItemTag.getTagAtPath(this.getNBTPath() + "_SUF", var1));
         return var4;
      } else {
         return null;
      }
   }

   @NotNull
   @Override
   public StringData getClearStatData() {
      return new NameData(null);
   }

   @Override
   public StringData whenInitialized(Object var1) {
      return new NameData(var1.toString());
   }
}
