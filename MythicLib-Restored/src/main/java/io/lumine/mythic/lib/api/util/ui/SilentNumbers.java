package io.lumine.mythic.lib.api.util.ui;

import io.lumine.mythic.lib.api.util.ToStringLambda;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SilentNumbers {
   private static final TreeMap<Integer, String> romanNumeralValues = new TreeMap<Integer, String>() {
      {
         this.put(1000, "M");
         this.put(900, "CM");
         this.put(500, "D");
         this.put(400, "CD");
         this.put(100, "C");
         this.put(90, "XC");
         this.put(50, "L");
         this.put(40, "XL");
         this.put(10, "X");
         this.put(9, "IX");
         this.put(5, "V");
         this.put(4, "IV");
         this.put(1, "I");
      }
   };

   public static boolean BooleanTryParse(@Nullable String b) {
      return b == null ? false : b.equalsIgnoreCase("true") || b.equalsIgnoreCase("false");
   }

   public static boolean DoubleTryParse(@Nullable String d) {
      return DoubleParse(d) != null;
   }

   public static boolean IntTryParse(@Nullable String i) {
      return IntegerParse(i) != null;
   }

   @Nullable
   public static Boolean BooleanParse(@Nullable String b) {
      if (b == null) {
         return null;
      } else if (b.equalsIgnoreCase("true")) {
         return true;
      } else {
         return b.equalsIgnoreCase("false") ? false : null;
      }
   }

   @Nullable
   public static Double DoubleParse(@Nullable String d) {
      if (d == null) {
         return null;
      }

      try {
         return Double.parseDouble(d);
      } catch (NumberFormatException e) {
         return null;
      }
   }

   @Nullable
   public static Integer IntegerParse(@Nullable String i) {
      if (i == null) {
         return null;
      }

      try {
         return Integer.parseInt(removeDecimalZeros(i));
      } catch (NumberFormatException ignored) {
         return null;
      }
   }

   public static double round(double number, int decimals) {
      long rounded = Math.round(number * Math.pow(10.0, decimals));
      return rounded / Math.pow(10.0, decimals);
   }

   public static int round(double number) {
      return (int)Math.round(number);
   }

   public static double floor(double number, int decimals) {
      double rounded = Math.floor(number * Math.pow(10.0, decimals));
      return rounded / Math.pow(10.0, decimals);
   }

   public static int floor(double number) {
      return (int)Math.floor(number);
   }

   public static double ceil(double number, int decimals) {
      double rounded = Math.ceil(number * Math.pow(10.0, decimals));
      return rounded / Math.pow(10.0, decimals);
   }

   public static int ceil(double number) {
      return (int)Math.ceil(number);
   }

   public static int IntegerParse(boolean value) {
      return value ? 1 : 0;
   }

   public static boolean BooleanParse(int value) {
      return value != 0;
   }

   public static boolean BooleanParse(double value) {
      return value != 0.0;
   }

   public static boolean rollSuccess(double decimal) {
      return Math.random() <= decimal;
   }

   public static boolean rollSuccessPercent(double percent) {
      return Math.random() <= percent / 100.0;
   }

   public static double randomRange(double minInclusive, double maxExclusive) {
      return Math.random() * (maxExclusive - minInclusive) + minInclusive;
   }

   @NotNull
   public static String removeDecimalZeros(@NotNull String source) {
      if (source.contains(".")) {
         String decimals = source.substring(source.lastIndexOf("."));
         int lC = -1;

         for (int c = 1; c < decimals.length(); c++) {
            char ch = decimals.charAt(c);
            if (ch != '0') {
               lC = c;
            }
         }

         return source.substring(0, source.lastIndexOf(".") + lC + 1);
      } else {
         return source;
      }
   }

   @NotNull
   public static String readableRounding(double something, int decimals) {
      return removeDecimalZeros(String.valueOf(round(something, decimals)));
   }

   @NotNull
   public static String nicestTimeValueFrom(double seconds) {
      if (seconds > 60.0) {
         if (seconds > 1800.0) {
            double div1800 = seconds / 1800.0;
            double difference = Math.round(div1800) - div1800;
            if (difference < 0.34) {
               return readableRounding(round(seconds / 3600.0, 1), 1) + "h";
            }

            if (seconds > 60000.0) {
               return readableRounding(round(seconds / 3600.0, 2), 1) + "h";
            }
         }

         double div30 = seconds / 30.0;
         double difference = Math.round(div30) - div30;
         if (difference < 0.34) {
            return readableRounding(round(seconds / 60.0, 1), 1) + "m";
         }

         if (seconds > 1000.0) {
            return readableRounding(round(seconds / 60.0, 2), 1) + "m";
         }
      }

      return readableRounding(round(seconds, 1), 1) + "s";
   }

   @Nullable
   public static UUID UUIDParse(@Nullable String anything) {
      if (anything == null) {
         return null;
      } else {
         return anything.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}") ? UUID.fromString(anything) : null;
      }
   }

   @NotNull
   public static ArrayList<String> chop(@NotNull String longString, int paragraphWide, @NotNull String colorPrefix) {
      ArrayList<String> ret = new ArrayList<>();
      boolean skip = false;

      while (longString.length() > paragraphWide) {
         skip = true;
         int idx = longString.lastIndexOf(" ", paragraphWide + 1);
         ret.add(colorPrefix + longString.substring(0, idx));
         longString = longString.substring(idx + 1);
         if (longString.length() <= paragraphWide) {
            ret.add(colorPrefix + longString);
         }
      }

      if (!skip) {
         ret.add(colorPrefix + longString);
      }

      return ret;
   }

   @NotNull
   public static ArrayList<String> smartFilter(@NotNull ArrayList<String> source, @NotNull String filter, boolean ignoreCaps) {
      ArrayList<String> starts = new ArrayList<>();
      ArrayList<String> contains = new ArrayList<>();
      if (ignoreCaps) {
         filter = filter.toLowerCase();
      }

      for (String str : source) {
         if (ignoreCaps) {
            str = str.toLowerCase();
         }

         if (str.startsWith(filter)) {
            starts.add(str);
         } else if (str.contains(filter)) {
            contains.add(str);
         }
      }

      starts.addAll(contains);
      return starts;
   }

   @SafeVarargs
   @NotNull
   public static <S> ArrayList<S> toArrayList(S... components) {
      ArrayList<S> ret = new ArrayList<>();

      for (S comp : components) {
         if (comp != null) {
            ret.add(comp);
         }
      }

      return ret;
   }

   @SafeVarargs
   @NotNull
   public static <S> ArrayList<S> addAll(@Nullable ArrayList<S> list, @Nullable S... append) {
      if (list == null) {
         list = new ArrayList<>();
      }

      ArrayList<S> finalList = list;
      if (append == null) {
         return finalList;
      }

      Arrays.asList(append).forEach(s -> {
         if (s != null) {
            finalList.add((S)s);
         }
      });
      return finalList;
   }

   @Contract("null -> null")
   @Nullable
   public static Enchantment getEnchantment(@Nullable String name) {
      return name == null ? null : Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase().replace(" ", "_").replace("-", "_")));
   }

   @NotNull
   public static String toRomanNumerals(int value) {
      if (value == 0) {
         return "0";
      }

      if (value < 0) {
         return "-" + toRomanNumerals(-value);
      }

      int gr8st = romanNumeralValues.floorKey(value);
      return value == gr8st ? romanNumeralValues.get(gr8st) : romanNumeralValues.get(gr8st) + toRomanNumerals(value - gr8st);
   }

   public static <S> boolean hasAll(@NotNull List<S> source, @NotNull List<S> required) {
      for (S str : required) {
         if (!source.contains(str)) {
            return false;
         }
      }

      return true;
   }

   @NotNull
   public static String getItemName(@Nullable ItemStack item, boolean includeAmount) {
      if (item == null) {
         return "null";
      }

      String amountText = "";
      if (includeAmount) {
         amountText = "§f" + item.getAmount() + "x";
      }

      return item.hasItemMeta() && item.getItemMeta().hasDisplayName()
         ? amountText + "§r§f" + item.getItemMeta().getDisplayName()
         : amountText + "§r§f" + titleCaseConversion(item.getType().toString().replace("_", " "));
   }

   @NotNull
   public static String getItemName(@Nullable ItemStack item) {
      return getItemName(item, true);
   }

   @Nullable
   public static String findItemName(@Nullable ItemStack item) {
      if (item == null) {
         return null;
      } else if (!item.hasItemMeta()) {
         return null;
      } else {
         return !item.getItemMeta().hasDisplayName() ? null : item.getItemMeta().getDisplayName();
      }
   }

   @NotNull
   public static ItemStack setItemName(@NotNull ItemStack item, @Nullable String name) {
      if (!item.hasItemMeta()) {
         return item;
      }

      ItemMeta itemMeta = item.getItemMeta();
      itemMeta.setDisplayName(name);
      item.setItemMeta(itemMeta);
      return item;
   }

   @NotNull
   public static String titleCaseConversion(@NotNull String inputString) {
      if (StringUtils.isBlank(inputString)) {
         return "";
      }

      if (StringUtils.length(inputString) == 1) {
         return inputString.toUpperCase();
      }

      StringBuffer resultPlaceHolder = new StringBuffer(inputString.length());
      Stream.of(inputString.split(" ")).forEach(stringPart -> {
         if (stringPart.length() > 1) {
            resultPlaceHolder.append(stringPart.substring(0, 1).toUpperCase()).append(stringPart.substring(1).toLowerCase());
         } else {
            resultPlaceHolder.append(stringPart.toUpperCase());
         }

         resultPlaceHolder.append(" ");
      });
      return StringUtils.trim(resultPlaceHolder.toString());
   }

   @NotNull
   public static String collapseList(@NotNull ArrayList<String> list, @NotNull String separator) {
      StringBuilder sb = new StringBuilder();
      boolean af = false;

      for (String str : list) {
         if (str == null) {
            str = "null";
         }

         if (af) {
            sb.append(separator);
         }

         af = true;
         sb.append(str);
      }

      return sb.toString();
   }

   @NotNull
   public static ArrayList<String> transcribeList(@NotNull List<?> list, @NotNull ToStringLambda conversion) {
      ArrayList<String> ret = new ArrayList<>();

      for (Object str : list) {
         ret.add(conversion.rewrite(str));
      }

      return ret;
   }

   @NotNull
   public static ArrayList<String> split(@NotNull String str, @NotNull String seq) {
      if (!str.contains(seq)) {
         ArrayList<String> ret = new ArrayList<>();
         ret.add(str);
         return ret;
      } else {
         return toArrayList(str.split(Pattern.quote(seq)));
      }
   }

   public static boolean isAir(@Nullable ItemStack item) {
      if (item == null) {
         return true;
      }

      switch (item.getType()) {
         case AIR:
         case CAVE_AIR:
         case VOID_AIR:
            return true;
         default:
            return false;
      }
   }

   public static void executeCommand(@NotNull CommandSender sender, @NotNull String command, @Nullable Location senderLocation) {
      Bukkit.dispatchCommand(sender, command);
   }

   @NotNull
   public static String unwrapFromCurlyBrackets(@NotNull String source) {
      if (source.endsWith("}")) {
         source = source.substring(0, source.length() - 1);
      }

      if (source.startsWith("{")) {
         source = source.substring(1);
      }

      return source;
   }

   @Nullable
   public static Integer integerFromBracketsTab(@NotNull String source, String tag) {
      return IntegerParse(valueFromBracketsTab(source, tag));
   }

   @Nullable
   public static QuickNumberRange rangeFromBracketsTab(@NotNull String source, @NotNull String tag) {
      return QuickNumberRange.getFromString(valueFromBracketsTab(source, tag));
   }

   @Nullable
   public static String valueFromBracketsTab(@NotNull String source, @NotNull String tag) {
      int limitSt = source.indexOf(tag + "=");
      if (limitSt >= 0) {
         String limitCropB4 = source.substring(limitSt + tag.length() + 1);
         int limitCropEnd = -1;
         int curlyIndex = -1;
         if (limitCropB4.startsWith("{")) {
            curlyIndex = limitCropB4.indexOf("}");
         }

         if (curlyIndex == -1) {
            curlyIndex = 0;
         }

         int limitCropComma = limitCropB4.indexOf(",", curlyIndex);
         int limitCropClose = limitCropB4.indexOf("]");
         if (limitCropComma > 0) {
            limitCropEnd = limitCropComma;
         }

         if (limitCropClose > 0) {
            if (limitCropEnd > 0) {
               if (limitCropClose < limitCropEnd) {
                  limitCropEnd = limitCropClose;
               }
            } else {
               limitCropEnd = limitCropClose;
            }
         }

         return limitCropEnd == -1 ? limitCropB4 : limitCropB4.substring(0, limitCropEnd);
      } else {
         return null;
      }
   }

   @Nullable
   public static Material getMaterial(@Nullable String mat) {
      if (mat == null) {
         return null;
      }

      try {
         return Material.valueOf(mat.toUpperCase().replace(" ", "_").replace("-", "_"));
      } catch (IllegalArgumentException ignored) {
         return null;
      }
   }
}
