package io.lumine.mythic.lib.comp.adventure;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.comp.adventure.argument.AdventureArgument;
import io.lumine.mythic.lib.comp.adventure.argument.AdventureArgumentQueue;
import io.lumine.mythic.lib.comp.adventure.argument.EmptyArgumentQueue;
import io.lumine.mythic.lib.comp.adventure.resolver.ContextTagResolver;
import io.lumine.mythic.lib.comp.adventure.tag.AdventureTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.AdventureColorTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.GradientTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.HexColorTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.NewlineTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.RainbowTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.TransitionTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.VanillaColorTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.decorations.BoldTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.decorations.ItalicTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.decorations.ObfuscatedTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.decorations.ResetTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.decorations.StrikethroughTag;
import io.lumine.mythic.lib.comp.adventure.tag.implementation.decorations.UnderlineTag;
import org.apache.commons.math3.util.Pair;
import io.lumine.mythic.lib.util.AdventureUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public class AdventureParser {
   private static final Pattern TAG_REGEX = Pattern.compile("(?i)(?<=<).*?(?=>)");
   private static final Pattern HEX_REGEX = Pattern.compile("(?i)(#|HEX)[0-9a-f]{6}");
   private final List<AdventureTag> tags = new ArrayList<>();

   @Internal
   @TestOnly
   public AdventureParser(boolean testing) {
   }

   public AdventureParser() {
      this.add(new GradientTag());
      this.add(new RainbowTag());
      this.add(new TransitionTag());
      this.add(new VanillaColorTag());
      this.add(new HexColorTag());
      this.add(new AdventureColorTag());
      this.add(new NewlineTag());
      this.add(new BoldTag());
      this.add(new ItalicTag());
      this.add(new ObfuscatedTag());
      this.add(new ResetTag());
      this.add(new StrikethroughTag());
      this.add(new UnderlineTag());
   }

   @NotNull
   public String parse(@NotNull String src) {
      String cpy = src;
      Matcher matcher = TAG_REGEX.matcher(cpy);

      while (matcher.find()) {
         String tag = matcher.group();
         String tagName = tag.contains(":") ? tag.split(":")[0] : tag;
         String finalCpy = cpy;
         if (!tagName.isEmpty() && !tagName.startsWith("/")) {
            cpy = this.findByName(tagName).map(adventureTag -> this.parseTag(finalCpy, adventureTag, tagName, tag)).orElseGet(() -> {
               Matcher matcher1 = HEX_REGEX.matcher(tag);
               if (!matcher1.find()) {
                  return finalCpy;
               }

               String prefix = matcher1.group(1);
               return this.findByName(prefix).map(adventureTag -> this.parseTag(finalCpy, adventureTag, prefix, tag)).orElse(finalCpy);
            });
         }
      }

      cpy = this.removeUnparsedAndUselessTags(cpy);
      return this.minecraftColorization(cpy);
   }

   @NotNull
   public CompletableFuture<String> parseAsync(@NotNull String src) {
      return AdventureUtils.supplyAsync(() -> this.parse(src));
   }

   @NotNull
   public Collection<String> parse(@NotNull Collection<String> src) {
      List<String> parsed = src.stream().map(this::parse).collect(Collectors.toList());
      List<String> finalList = new ArrayList<>();

      for (String line : parsed) {
         if (!line.contains("\n")) {
            finalList.add(line);
         } else {
            finalList.addAll(Arrays.asList(line.split("\n")));
         }
      }

      return finalList;
   }

   @NotNull
   public CompletableFuture<Collection<String>> parseAsync(@NotNull Collection<String> src) {
      return AdventureUtils.supplyAsync(() -> this.parse(src));
   }

   @NotNull
   private String parseTag(@NotNull String src, @NotNull AdventureTag tag, @NotNull String tagIdentifier, @NotNull String plainTag) {
      String cpy = src;

      try {
         int firstArgIndex = plainTag.indexOf(":");
         boolean hasArgs = firstArgIndex != -1;
         boolean isHex = tagIdentifier.equals("#") || tagIdentifier.equalsIgnoreCase("HEX");
         String hexPrefix = tagIdentifier.startsWith("#") ? "#" : "HEX";
         String rawTag = isHex ? tagIdentifier : plainTag.substring(0, hasArgs ? firstArgIndex : plainTag.length());
         String rawArgs = isHex ? plainTag.substring(hexPrefix.length()) : (hasArgs ? plainTag.substring(firstArgIndex + 1) : "");
         String original = String.format("<%s%s>", rawTag, hasArgs ? ':' + rawArgs : rawArgs);
         AdventureArgumentQueue args = this.parseArguments(rawArgs);
         boolean hasContext = tag.resolver() instanceof ContextTagResolver;
         String context = hasContext ? this.getTagContent(cpy, rawTag, original) : null;
         Pair<List<String>, String> contextDecorations = hasContext ? this.processContextDecorations(context) : null;
         String resolved = hasContext
            ? ((ContextTagResolver)tag.resolver()).resolve(rawTag, args, contextDecorations.getValue(), contextDecorations.getKey())
            : tag.resolver().resolve(rawTag, args);
         cpy = cpy.replace(hasContext ? String.format("%s%s", original, context) : original, resolved != null ? resolved : "");
      } catch (Exception e) {
         e.printStackTrace();
      }

      return cpy;
   }

   @NotNull
   private String getTagContent(@NotNull String src, @NotNull String tagName, @NotNull String tagIdentifier) {
      String closeTag = String.format("</%s>", tagName);
      String content = StringUtils.substringBetween(src, tagIdentifier, closeTag);
      if (content != null) {
         return content;
      }

      String cpy = src.substring(src.indexOf(tagIdentifier) + tagIdentifier.length());
      int colorIndex = cpy.length();
      Matcher matcher = TAG_REGEX.matcher(cpy);

      for (int iterations = 0; matcher.find() && iterations++ < 20; matcher = TAG_REGEX.matcher(cpy)) {
         String rawTag = matcher.group();
         boolean isHex = rawTag.startsWith("#") || rawTag.startsWith("HEX");
         String rawTagName = isHex ? (rawTag.startsWith("#") ? "#" : "HEX") : rawTag.split(":")[0];
         Optional<AdventureTag> optTag = this.findByName(rawTagName).filter(AdventureTag::color);
         if (optTag.isPresent()) {
            colorIndex = cpy.indexOf(String.format("<%s>", rawTag));
            break;
         }
      }

      return cpy.substring(0, colorIndex);
   }

   private AdventureArgumentQueue parseArguments(@NotNull String rawArgs) {
      String[] unparsedArgs = rawArgs.split(":");
      if (unparsedArgs.length > 0 && unparsedArgs[0].isEmpty()) {
         unparsedArgs = Arrays.copyOfRange(unparsedArgs, 1, unparsedArgs.length);
      }

      return new AdventureArgumentQueue(Arrays.stream(unparsedArgs).map(AdventureArgument::new).collect(Collectors.toList()));
   }

   @NotNull
   private String removeUnparsedAndUselessTags(@NotNull String src) {
      Matcher matcher = TAG_REGEX.matcher(src);

      for (int iterations = 0; matcher.find() && iterations++ < 50; matcher = TAG_REGEX.matcher(src)) {
         String matched = matcher.group();
         String original = String.format("<%s>", matched);
         if (matched.startsWith("/")) {
            src = src.replace(original, "§r");
         }
      }

      return src;
   }

   @NotNull
   private String minecraftColorization(@NotNull String src) {
      return ChatColor.translateAlternateColorCodes('&', src);
   }

   @NotNull
   public String stripColors(@NotNull String src) {
      String cpy = this.minecraftColorization(src);
      Matcher matcher = TAG_REGEX.matcher(src);

      while (matcher.find()) {
         String tag = matcher.group();
         String original = String.format("<%s>", tag);
         cpy = cpy.replace(original, "");
      }

      return ChatColor.stripColor(cpy);
   }

   @NotNull
   public String lastColor(@NotNull String src, boolean matchDecorations) {
      String cpy = this.minecraftColorization(src);
      LinkedList<Entry<AdventureTag, String>> tags = new LinkedList<>();
      Matcher matcher = TAG_REGEX.matcher(cpy);

      while (matcher.find()) {
         String tag = matcher.group();
         String tagName = tag.contains(":") ? tag.split(":")[0] : tag;
         if (!tagName.isEmpty() && !tagName.startsWith("/")) {
            Optional<AdventureTag> optTag = this.findByName(tagName);
            if (optTag.isPresent()) {
               tags.add(new SimpleEntry<>(optTag.get(), tag));
            } else {
               Matcher matcher1 = HEX_REGEX.matcher(tag);
               if (matcher1.find()) {
                  this.findByName(matcher1.group(1)).ifPresent(adventureTag -> tags.add(new SimpleEntry<>(adventureTag, tag)));
               }
            }
         }
      }

      String vanilla = this.getLastLegacyColor(cpy, matchDecorations);
      if (tags.isEmpty()) {
         return vanilla;
      } else {
         String lastTag = matchDecorations ? this.getSurroundingDecorations(src, tags) : this.getLastColorTag(tags);
         if (lastTag == null) {
            return "";
         } else if (vanilla.isEmpty()) {
            return lastTag;
         } else {
            return cpy.indexOf(vanilla) > cpy.indexOf(lastTag) ? vanilla : lastTag;
         }
      }
   }

   @Nullable
   private String getLastColorTag(LinkedList<Entry<AdventureTag, String>> list) {
      for (int i = list.size() - 1; i >= 0; i--) {
         Entry<AdventureTag, String> entry = list.get(i);
         if (entry.getKey().color()) {
            return String.format("<%s>", entry.getValue());
         }
      }

      return null;
   }

   @Nullable
   private String getSurroundingDecorations(@NotNull String src, @NotNull LinkedList<Entry<AdventureTag, String>> list) {
      String colorTag = this.getLastColorTag(list);
      if (colorTag == null) {
         for (int i = list.size() - 1; i >= 0; i--) {
            Entry<AdventureTag, String> entry = list.get(i);
            if (!entry.getKey().color()) {
               return String.format("<%s>", entry.getValue());
            }
         }

         return null;
      } else {
         int tagIndex = src.indexOf(colorTag);
         List<String> previousTags = new ArrayList<>();
         String before = src.substring(0, tagIndex);
         if (before.length() > 3 && before.charAt(before.length() - 1) == '>') {
            while (before.length() > 3 && before.charAt(before.length() - 1) == '>') {
               int index = before.lastIndexOf(60);
               if (index == -1
                  || this.findByName(before.substring(index + 1, before.length() - 1).split(":")[0]).filter(AdventureTag::color).isPresent()
                  || before.substring(index).startsWith("</")) {
                  break;
               }

               previousTags.add(0, before.substring(index));
               before = before.substring(0, index);
            }
         }

         List<String> nextTags = new ArrayList<>();
         String after = src.substring(tagIndex + colorTag.length());
         if (after.length() > 3 && after.charAt(0) == '<') {
            while (after.length() > 3 && after.charAt(0) == '<') {
               int index = after.indexOf(62);
               if (index == -1
                  || this.findByName(after.substring(1, index).split(":")[0]).filter(AdventureTag::color).isPresent()
                  || after.substring(0, index).startsWith("</")) {
                  break;
               }

               nextTags.add(after.substring(0, index + 1));
               after = after.substring(index + 1);
            }
         }

         StringBuilder builder = new StringBuilder();
         previousTags.forEach(builder::append);
         builder.append(colorTag);
         nextTags.forEach(builder::append);
         return builder.toString();
      }
   }

   @NotNull
   private Pair<List<String>, String> processContextDecorations(@NotNull String context) {
      Map<String, String> decorations = new HashMap<>();
      String cpy = this.minecraftColorization(context);
      Matcher matcher = TAG_REGEX.matcher(cpy);

      while (matcher.find()) {
         String tag = matcher.group();
         String tagName = tag.contains(":") ? tag.split(":")[0] : tag;
         if (!tagName.isEmpty() && !tagName.startsWith("/")) {
            this.findByName(tagName)
               .filter(t -> !t.color())
               .map(t -> t.resolver().resolve("", new EmptyArgumentQueue()))
               .ifPresent(s -> decorations.put(tag, s));
         }
      }

      for (Entry<String, String> e : decorations.entrySet()) {
         context = context.replace(String.format("<%s>", e.getKey()), "");
      }

      return Pair.create(new ArrayList<>(decorations.values()), context);
   }

   @NotNull
   private String getLastLegacyColor(@NotNull String input, boolean matchDecorations) {
      StringBuilder builder = new StringBuilder();
      int length = input.length();

      for (int index = length - 1; index > -1; index--) {
         char section = input.charAt(index);
         if (section == 167 && index < length - 1) {
            char c = input.charAt(index + 1);
            ChatColor color = ChatColor.getByChar(c);
            if (color != null && (!color.isFormat() || matchDecorations)) {
               builder.insert(0, color);
               if (color.isColor() || color.equals(ChatColor.RESET)) {
                  break;
               }
            }
         }
      }

      return builder.toString();
   }

   public void add(AdventureTag tag) {
      if (tag.backwardsCompatible() && MythicLib.plugin.getVersion().isUnder(1, 16)) {
         MythicLib.plugin.getLogger().warning(String.format("The tag %s is not compatible with your server version.", tag.name()));
      } else {
         this.tags.add(tag);
      }
   }

   @Internal
   public void forceRegister(AdventureTag tag) {
      this.tags.add(tag);
   }

   public void remove(AdventureTag tag) {
      this.tags.remove(tag);
   }

   public Optional<AdventureTag> findByName(@NotNull String name) {
      return this.tags.stream().filter(tag -> tag.name().equalsIgnoreCase(name) || tag.aliases().stream().anyMatch(s -> s.equalsIgnoreCase(name))).findFirst();
   }

   public List<AdventureTag> tags() {
      return this.tags;
   }
}
