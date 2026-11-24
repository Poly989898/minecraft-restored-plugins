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
   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringData stringData) {
      itemStackBuilder.getMMOItem().computeStatHistory(this);
      ItemTier tier = itemStackBuilder.getMMOItem().getTier();
      AdventureParser adventureParser = MythicLib.plugin.getAdventureParser();
      String text = stringData.toString();
      text = text.replace("<tier-name>", tier != null ? adventureParser.stripColors(tier.getUnparsedName()) : "")
         .replace("<tier-color>", tier != null ? adventureParser.lastColor(tier.getUnparsedName(), true) : "&f")
         .replace("<tier-color-cleaned>", tier != null ? adventureParser.lastColor(tier.getUnparsedName(), false) : "");
      text = this.cropUpgrade(text);
      if (itemStackBuilder.getMMOItem().hasUpgradeTemplate()) {
         text = appendUpgradeLevel(text, itemStackBuilder.getMMOItem().getUpgradeLevel());
      }

      itemStackBuilder.getMeta().setDisplayName(text);
      itemStackBuilder.addItemTag(this.getAppliedNBT(stringData));
   }

   @NotNull
   String cropUpgrade(@NotNull String text) {
      String text2 = MMOItems.plugin.getConfig().getString("item-upgrading.name-suffix", " &8(&e+#lvl#&8)");
      if (text2 != null && !text2.isEmpty()) {
         int count = text2.indexOf("#lvl#");
         if (count < 0) {
            return text;
         }

         String text3 = text2.substring(0, count);
         String text4 = text2.substring(count + "#lvl#".length());
         String text5 = text3.replace("+", "-");
         String text6 = text4.replace("+", "-");
         if (text.contains(text3)) {
            int count2 = text.indexOf(text3);
            int count3 = text.lastIndexOf(text4);
            if (count3 < 0) {
               count3 = text.length();
            } else {
               count3 += text4.length();
            }

            String text7 = text.substring(0, count2);
            String text8 = text.substring(count3);
            text = text7 + text8;
         }

         if (text.contains(text5)) {
            int count2 = text.indexOf(text5);
            int count3 = text.lastIndexOf(text6);
            if (count3 < 0) {
               count3 = text.length();
            } else {
               count3 += text6.length();
            }

            String text7 = text.substring(0, count2);
            String text8 = text.substring(count3);
            text = text7 + text8;
         }

         return text;
      } else {
         return text;
      }
   }

   @NotNull
   public static String appendUpgradeLevel(@NotNull String text, int count) {
      String text2 = MMOItems.plugin.getConfig().getString("item-upgrading.name-suffix");
      if (text2 != null && count != 0) {
         String text3 = levelPrefix(text2, count);
         return text + text3;
      } else {
         return text;
      }
   }

   @NotNull
   public static String levelPrefix(@NotNull String text, int count) {
      return text.replace("#lvl#", String.valueOf(count)).replace("+-", "-");
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringData stringData) {
      Validate.isTrue(stringData instanceof NameData, "Data is not name data");
      NameData nameData = (NameData)stringData;
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), nameData.getMainName()));
      if (nameData.hasPrefixes()) {
         list.add(nameData.compressPrefixes(this.getNBTPath() + "_PRE"));
      }

      if (nameData.hasSuffixes()) {
         list.add(nameData.compressSuffixes(this.getNBTPath() + "_SUF"));
      }

      return list;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      boolean flag = false;
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING);
      if (tagAtPath != null) {
         ItemTag tagAtPath2 = ItemTag.getTagAtPath(this.getNBTPath() + "_PRE", readMMOItem.getNBT(), SupportedNBTTagValues.STRING);
         ItemTag tagAtPath3 = ItemTag.getTagAtPath(this.getNBTPath() + "_SUF", readMMOItem.getNBT(), SupportedNBTTagValues.STRING);
         list.add(tagAtPath);
         list.add(tagAtPath2);
         list.add(tagAtPath3);
         if (readMMOItem.getNBT().getItem().getItemMeta().hasDisplayName()) {
            flag = true;
         }
      } else {
         if (!readMMOItem.getNBT().getItem().getItemMeta().hasDisplayName()) {
            return;
         }

         list.add(new ItemTag(this.getNBTPath(), this.cropUpgrade(readMMOItem.getNBT().getItem().getItemMeta().getDisplayName())));
      }

      NameData loadedNBT = (NameData)this.getLoadedNBT(list);
      if (loadedNBT != null) {
         String text = null;
         if (flag) {
            text = readMMOItem.getNBT().getItem().getItemMeta().getDisplayName();
            String text2 = ChatColor.stripColor(text);
            if (!text.equals(text2)) {
               text = null;
            } else {
               loadedNBT.setString(text);
            }
         }

         readMMOItem.setData(this, loadedNBT);
         if (flag && text != null && !readMMOItem.hasStatHistory(this)) {
            ItemTag tagAtPath2 = ItemTag.getTagAtPath("HSTRY_" + this.getId(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING);
            if (tagAtPath2 != null) {
               StatHistory statHistory = StatHistory.fromNBTString(readMMOItem, (String)tagAtPath2.getValue());
               if (statHistory != null) {
                  NameData nameData = (NameData)statHistory.getOriginalData();
                  nameData.setString(text);
                  readMMOItem.setStatHistory(this, statHistory);
               }
            }
         }
      }
   }

   @Nullable
   @Override
   public StringData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         String text = (String)tagAtPath.getValue();
         NameData nameData = new NameData(text);
         nameData.readPrefixes(ItemTag.getTagAtPath(this.getNBTPath() + "_PRE", itemTags));
         nameData.readSuffixes(ItemTag.getTagAtPath(this.getNBTPath() + "_SUF", itemTags));
         return nameData;
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
   public StringData whenInitialized(Object obj) {
      return new NameData(obj.toString());
   }
}
