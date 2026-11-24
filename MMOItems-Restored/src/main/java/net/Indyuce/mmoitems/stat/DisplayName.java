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
      String nameSuffix = MMOItems.plugin.getConfig().getString("item-upgrading.name-suffix", " &8(&e+#lvl#&8)");
      if (nameSuffix != null && !nameSuffix.isEmpty()) {
         int count = nameSuffix.indexOf("#lvl#");
         if (count < 0) {
            return text;
         }

         String suffixPrefix = nameSuffix.substring(0, count);
         String suffixPostfix = nameSuffix.substring(count + "#lvl#".length());
         String negatedPrefix = suffixPrefix.replace("+", "-");
         String negatedPostfix = suffixPostfix.replace("+", "-");
         if (text.contains(suffixPrefix)) {
            int prefixIndex = text.indexOf(suffixPrefix);
            int postfixIndex = text.lastIndexOf(suffixPostfix);
            if (postfixIndex < 0) {
               postfixIndex = text.length();
            } else {
               postfixIndex += suffixPostfix.length();
            }

            String baseName = text.substring(0, prefixIndex);
            String trailingText = text.substring(postfixIndex);
            text = baseName + trailingText;
         }

         if (text.contains(negatedPrefix)) {
            int negatedPrefixIndex = text.indexOf(negatedPrefix);
            int negatedPostfixIndex = text.lastIndexOf(negatedPostfix);
            if (negatedPostfixIndex < 0) {
               negatedPostfixIndex = text.length();
            } else {
               negatedPostfixIndex += negatedPostfix.length();
            }

            String baseName = text.substring(0, negatedPrefixIndex);
            String trailingText = text.substring(negatedPostfixIndex);
            text = baseName + trailingText;
         }

         return text;
      } else {
         return text;
      }
   }

   @NotNull
   public static String appendUpgradeLevel(@NotNull String text, int count) {
      String configuredSuffix = MMOItems.plugin.getConfig().getString("item-upgrading.name-suffix");
      if (configuredSuffix != null && count != 0) {
         String levelSuffix = levelPrefix(configuredSuffix, count);
         return text + levelSuffix;
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
         ItemTag prefixTag = ItemTag.getTagAtPath(this.getNBTPath() + "_PRE", readMMOItem.getNBT(), SupportedNBTTagValues.STRING);
         ItemTag suffixTag = ItemTag.getTagAtPath(this.getNBTPath() + "_SUF", readMMOItem.getNBT(), SupportedNBTTagValues.STRING);
         list.add(tagAtPath);
         list.add(prefixTag);
         list.add(suffixTag);
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
            String strippedName = ChatColor.stripColor(text);
            if (!text.equals(strippedName)) {
               text = null;
            } else {
               loadedNBT.setString(text);
            }
         }

         readMMOItem.setData(this, loadedNBT);
         if (flag && text != null && !readMMOItem.hasStatHistory(this)) {
            ItemTag historyTag = ItemTag.getTagAtPath("HSTRY_" + this.getId(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING);
            if (historyTag != null) {
               StatHistory statHistory = StatHistory.fromNBTString(readMMOItem, (String)historyTag.getValue());
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
