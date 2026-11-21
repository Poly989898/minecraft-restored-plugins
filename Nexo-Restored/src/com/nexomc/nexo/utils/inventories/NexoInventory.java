package com.nexomc.nexo.utils.inventories;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.converter.NexoConverter;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000z\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0019\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u0000 K2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0002JKB\u0011\b\u0012\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\u001b\b\u0012\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\u0004\u0010\bB/\b\u0012\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0012\u0010\t\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00030\n\"\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u000bB=\b\u0012\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\f\u0012\u0012\u0010\r\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00030\n\"\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u000eB#\b\u0012\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\u000f\u001a\u00020\u0010¢\u0006\u0004\b\u0004\u0010\u0011J\u0018\u0010(\u001a\u00020*2\b\u0010%\u001a\u0004\u0018\u00010\u00072\u0006\u0010+\u001a\u00020,J\n\u0010-\u001a\u00020\u0003H\u0096\u0080\u0004J\u000e\u0010-\u001a\u00020\u00032\u0006\u0010.\u001a\u00020\u0003J\u000b\u0010/\u001a\u000700¢\u0006\u0002\b1J\u0013\u0010/\u001a\u000700¢\u0006\u0002\b12\u0006\u0010.\u001a\u000200J1\u00102\u001a\u0002H3\"\u000e\b\u0000\u00103*\b\u0012\u0004\u0012\u0002H30\u00012\f\u00104\u001a\b\u0012\u0004\u0012\u0002H3052\u0006\u0010\u0006\u001a\u0002H3¢\u0006\u0002\u00106J=\u00107\u001a\u0002H3\"\u000e\b\u0000\u00103*\b\u0012\u0004\u0012\u0002H30\u00012\f\u00104\u001a\b\u0012\u0004\u0012\u0002H3052\u0012\u00108\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u0002H309¢\u0006\u0002\u0010:J\u000b\u0010;\u001a\u00070\u0010¢\u0006\u0002\b1J\u0006\u0010<\u001a\u00020=J\u000e\u0010<\u001a\u00020=2\u0006\u0010.\u001a\u00020=J\u000e\u0010>\u001a\u00020,2\u0006\u0010\u0006\u001a\u00020,J\u0006\u0010>\u001a\u00020,J2\u0010?\u001a.\u0012\f\u0012\n A*\u0004\u0018\u00010\u00030\u0003 A*\u0015\u0012\f\u0012\n A*\u0004\u0018\u00010\u00030\u00030\f¢\u0006\u0002\b10@¢\u0006\u0002\b1J\f\u0010B\u001a\b\u0012\u0004\u0012\u00020\u00030\fJ\b\u0010C\u001a\u0004\u0018\u00010DJ\f\u0010E\u001a\b\u0012\u0004\u0012\u00020D0\fJ\r\u0010F\u001a\u00020G¢\u0006\u0004\bH\u0010IR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00030\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00030\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010#\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R(\u0010%\u001a\u0004\u0018\u00010\u00072\b\u0010$\u001a\u0004\u0018\u00010\u00078F@FX\u0086\u000e¢\u0006\f\u001a\u0004\b&\u0010'\"\u0004\b(\u0010)j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016j\u0002\b\u0017j\u0002\b\u0018j\u0002\b\u0019j\u0002\b\u001aj\u0002\b\u001bj\u0002\b\u001cj\u0002\b\u001dj\u0002\b\u001ej\u0002\b\u001fj\u0002\b ¨\u0006L"},
   d2 = {"Lcom/nexomc/nexo/utils/inventories/NexoInventory;", "", "path", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;)V", "defaultValue", "", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Object;)V", "comments", "", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Object;[Ljava/lang/String;)V", "", "inlineComments", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Object;Ljava/util/List;[Ljava/lang/String;)V", "richComment", "Lnet/kyori/adventure/text/Component;", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/Object;Lnet/kyori/adventure/text/Component;)V", "TYPE", "STYLE_DEFAULT_NAMES", "LAYOUT", "ROWS", "SIZE", "TITLE", "SEARCH_TITLE", "NEXT_ICON", "PREVIOUS_ICON", "EXIT_ICON", "SEARCH_ICON", "DIRECTORY_ICON", "RECIPE_SHOWCASE_TITLE", "CREATIVE_PACKET_SIZE", "CREATIVE_LAYOUT", "getPath", "()Ljava/lang/String;", "_value", "newValue", "value", "getValue", "()Ljava/lang/Object;", "setValue", "(Ljava/lang/Object;)V", "", "save", "", "toString", "optionalDefault", "toKey", "Lnet/kyori/adventure/key/Key;", "Lorg/jetbrains/annotations/NotNull;", "toEnum", "E", "enumClass", "Ljava/lang/Class;", "(Ljava/lang/Class;Ljava/lang/Enum;)Ljava/lang/Enum;", "toEnumOrGet", "fallback", "Lkotlin/Function1;", "(Ljava/lang/Class;Lkotlin/jvm/functions/Function1;)Ljava/lang/Enum;", "toComponent", "toInt", "", "toBool", "toStringList", "", "kotlin.jvm.PlatformType", "toStringListOrSingle", "toSection", "Lorg/bukkit/configuration/ConfigurationSection;", "toSectionList", "toDuration", "Lkotlin/time/Duration;", "toDuration-UwyO8pc", "()J", "Type", "Companion", "com.nexomc:core"}
)
public enum NexoInventory {
   TYPE("NexoInventory.type", "FILE"),
   STYLE_DEFAULT_NAMES("NexoInventory.style_default_names", true),
   LAYOUT("NexoInventory.layout", layoutDefault()),
   ROWS("NexoInventory.menu_rows", 6),
   SIZE("NexoInventory.menu_size", 45),
   TITLE("NexoInventory.menu_title", "<shift:-37><glyph:menu_items_search>"),
   SEARCH_TITLE("NexoInventory.search_title", "<shift:256>NexoInventory Search<shift:256>"),
   NEXT_ICON("NexoInventory.next_page_icon", "nexo:next_page_icon"),
   PREVIOUS_ICON("NexoInventory.previous_page_icon", "nexo:previous_page_icon"),
   EXIT_ICON("NexoInventory.exit_icon", "nexo:cancel_icon"),
   SEARCH_ICON("NexoInventory.search_icon", "nexo:search_icon"),
   DIRECTORY_ICON("NexoInventory.directory_icon", "nexo:directory_icon"),
   RECIPE_SHOWCASE_TITLE("NexoInventory.recipe_showcase_title", "<shift:-7><glyph:menu_recipe>"),
   CREATIVE_PACKET_SIZE("NexoInventory.creative.items_per_packet", 20),
   CREATIVE_LAYOUT("NexoInventory.creative.layout", MapsKt.emptyMap());

   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final String path;
   @Nullable
   private final Object defaultValue;
   @NotNull
   private List<String> comments = CollectionsKt.emptyList();
   @NotNull
   private List<String> inlineComments = CollectionsKt.emptyList();
   @NotNull
   private Component richComment;
   @Nullable
   private Object _value;

   @NotNull
   public final String getPath() {
      return this.path;
   }

   private NexoInventory(String path) {
      TextComponent textComponent = Component.empty();
      this.richComment = (Component)textComponent;
      this.path = path;
      this.defaultValue = null;
   }

   private NexoInventory(String path, Object defaultValue) {
      TextComponent textComponent = Component.empty();
      this.richComment = (Component)textComponent;
      this.path = path;
      this.defaultValue = defaultValue;
   }

   private NexoInventory(String path, Object defaultValue, String... comments) {
      TextComponent textComponent = Component.empty();
      this.richComment = (Component)textComponent;
      this.path = path;
      this.defaultValue = defaultValue;
      this.comments = CollectionsKt.listOf(Arrays.copyOf(comments, comments.length));
   }

   private NexoInventory(String path, Object defaultValue, List<String> comments, String... inlineComments) {
      TextComponent textComponent = Component.empty();
      this.richComment = (Component)textComponent;
      this.path = path;
      this.defaultValue = defaultValue;
      this.comments = comments;
      this.inlineComments = CollectionsKt.listOf(Arrays.copyOf(inlineComments, inlineComments.length));
   }

   private NexoInventory(String path, Object defaultValue, Component richComment) {
      TextComponent textComponent = Component.empty();
      this.richComment = (Component)textComponent;
      this.path = path;
      this.defaultValue = defaultValue;
      this.richComment = richComment;
   }

   @Nullable
   public final Object getValue() {
      if (this._value == null) {
         this._value = NexoPlugin.Companion.instance().configsManager().inventory().get(this.path);
      }

      return this._value;
   }

   public final void setValue(@Nullable Object newValue) {
      this.setValue(newValue, true);
   }

   public final void setValue(@Nullable Object value, boolean save) {
      YamlConfiguration settingFile = NexoPlugin.Companion.instance().configsManager().inventory();
      Object oldValue = settingFile.get(this.path);
      settingFile.set(this.path, value);
      NexoInventory nexoInventory = this;

      Object obj;
      try {
         NexoInventory nexoInventory2 = nexoInventory;
         int value2 = 0;
         if (save && !Intrinsics.areEqual(oldValue, value)) {
            File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
            settingFile.save(FilesKt.resolve(dataFolder, "inventory.yml"));
         }

         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      if (ResultShim.exceptionOrNull(obj) != null) {
         int value3 = 0;
         Logs.logError("Failed to apply changes to inventory.yml", false);
      }

   }

   @NotNull
   public String toString() {
      return String.valueOf(this.getValue());
   }

   @NotNull
   public final String toString(@NotNull String optionalDefault) {
      Object value = this.getValue();
      String str = value instanceof String ? (String)value : null;
      if ((value instanceof String ? (String)value : null) == null) {
         str = optionalDefault;
      }

      return str;
   }

   @NotNull
   public final Key toKey() {
      NexoInventory nexoInventory = this;

      Object obj3;
      try {
         NexoInventory nexoInventory2 = nexoInventory;
         int value = 0;
         obj3 = ResultShim.ok(Key.key(nexoInventory2.toString()));
      } catch (Throwable e) {
         obj3 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj3;
      Key key = Key.key("minecraft:default");
      Object obj2 = ResultShim.isFailure(obj) ? key : obj;
      return (Key)obj2;
   }

   @NotNull
   public final Key toKey(@NotNull Key optionalDefault) {
      NexoInventory nexoInventory = this;

      Object obj2;
      try {
         NexoInventory nexoInventory2 = nexoInventory;
         int value = 0;
         obj2 = ResultShim.ok(Key.key(nexoInventory2.toString()));
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = ResultShim.isFailure(obj2) ? optionalDefault : obj2;
      return (Key)obj;
   }

   @NotNull
   public final <E extends Enum<E>> E toEnum(@NotNull Class<E> enumClass, @NotNull E defaultValue) {
      String str = this.toString().toUpperCase(Locale.ROOT);
      Enum enumValue2 = EnumUtils.getEnum(enumClass, str, defaultValue);
      return (E)enumValue2;
   }

   @NotNull
   public final <E extends Enum<E>> E toEnumOrGet(@NotNull Class<E> enumClass, @NotNull Function1<? super String, ? extends E> fallback) {
      return com.nexomc.nexo.utils.EnumUtils.INSTANCE.toEnumOrElse(this.toString(), enumClass, fallback);
   }

   @NotNull
   public final Component toComponent() {
      Component component = AdventureUtils.INSTANCE.getMINI_MESSAGE().deserialize(String.valueOf(this.getValue()));
      return component;
   }

   public final int toInt() {
      return this.toInt(-1);
   }

   public final int toInt(int optionalDefault) {
      NexoInventory nexoInventory = this;

      Object obj;
      try {
         NexoInventory nexoInventory2 = nexoInventory;
         int value = 0;
         obj = ResultShim.ok(StringsKt.toIntOrNull(String.valueOf(nexoInventory2.getValue())));
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Integer number = (Integer)(ResultShim.isFailure(obj) ? null : obj);
      return number != null ? number : optionalDefault;
   }

   public final boolean toBool(boolean defaultValue) {
      Object value = this.getValue();
      return (value instanceof Boolean ? (Boolean)value : null) != null ? value instanceof Boolean ? (Boolean)value : null : defaultValue;
   }

   public final boolean toBool() {
      Object value = this.getValue();
      return (value instanceof Boolean ? (Boolean)value : null) != null ? value instanceof Boolean ? (Boolean)value : null : false;
   }

   @NotNull
   public final List<String> toStringList() {
      List stringList = NexoPlugin.Companion.instance().configsManager().inventory().getStringList(this.path);
      return stringList;
   }

   @NotNull
   public final List<String> toStringListOrSingle() {
      return NexoYamlKt.stringListOrSingle((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().inventory(), this.path, (String)null);
   }

   @Nullable
   public final ConfigurationSection toSection() {
      return NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().inventory(), this.path);
   }

   @NotNull
   public final List<ConfigurationSection> toSectionList() {
      return NexoYamlKt.sectionList((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().inventory(), this.path);
   }

   public final long toDuration_UwyO8pc/* $FF was: toDuration-UwyO8pc*/() {
      String string = NexoPlugin.Companion.instance().configsManager().inventory().getString(this.path);
      long value;
      if (string != null) {
         Duration duration = DurationUtilsKt.toDuration(string, (DurationUnit)null);
         if (duration != null) {
            value = duration.unbox_impl();
            return value;
         }
      }

      value = Duration.Companion.getINFINITE_UwyO8pc();
      return value;
   }


   private static Object layoutDefault() {
      Pair[] pairs = new Pair[3];
      Pair[] pairs2 = new Pair[]{TuplesKt.to("slot", 1), TuplesKt.to("icon", "forest_helmet"), TuplesKt.to("itemname", "<green>Nexo Armor</green>")};
      pairs[0] = TuplesKt.to("nexo_armor", MapsKt.mapOf(pairs2));
      pairs2 = new Pair[]{TuplesKt.to("slot", 2), TuplesKt.to("icon", "arm_chair"), TuplesKt.to("itemname", "<green>Nexo Furniture</green>")};
      pairs[1] = TuplesKt.to("nexo_furniture", MapsKt.mapOf(pairs2));
      pairs2 = new Pair[]{TuplesKt.to("slot", 3), TuplesKt.to("icon", "forest_axe"), TuplesKt.to("itemname", "<green>Nexo Tools</green>")};
      pairs[2] = TuplesKt.to("nexo_tools", MapsKt.mapOf(pairs2));
      return MapsKt.mapOf(pairs);
   }


   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005¨\u0006\u0006"},
      d2 = {"Lcom/nexomc/nexo/utils/inventories/NexoInventory$Type;", "", "<init>", "(Ljava/lang/String;I)V", "FILE", "DIRECTORY", "com.nexomc:core"}
   )
   public static enum Type {
      FILE,
      DIRECTORY;

      @NotNull
      public static EnumEntries<Type> getEntries() {
         return EnumEntriesKt.enumEntries(values());
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\b\u0010\b\u001a\u00020\u0007H\u0002¨\u0006\t"},
      d2 = {"Lcom/nexomc/nexo/utils/inventories/NexoInventory$Companion;", "", "<init>", "()V", "reload", "", "validateInventory", "Lorg/bukkit/configuration/file/YamlConfiguration;", "defaultInventory", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final void reload() {
         Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(NexoInventory.values());

         for(Object element : iterable) {
            NexoInventory it = (NexoInventory)element;
            int value = 0;
            it._value = null;
         }

      }

      @NotNull
      public final YamlConfiguration validateInventory() {
         File inventoryFile = NexoPlugin.Companion.instance().getDataFolder().toPath().resolve("inventory.yml").toFile();
         YamlConfiguration yamlConfiguration;
         if (inventoryFile.exists()) {
            NexoYaml.Companion companion = NexoYaml.Companion;
            yamlConfiguration = companion.loadConfiguration(inventoryFile);
         } else {
            yamlConfiguration = new YamlConfiguration();
         }

         YamlConfiguration inventory = yamlConfiguration;
         inventory.options().copyDefaults(true).indent(2).parseComments(true);
         inventory.addDefaults((Configuration)this.defaultInventory());
         NexoConverter.INSTANCE.processInventory(inventory);
         Companion companion2 = this;

         Object obj;
         try {
            Companion companion3 = companion2;
            int value = 0;
            inventoryFile.createNewFile();
            inventory.save(inventoryFile);
            obj = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         Throwable throwable = ResultShim.exceptionOrNull(obj);
         if (throwable != null) {
            Throwable it = throwable;
            int value2 = 0;
            if (inventory.getBoolean("debug")) {
               Logs.logError(ExceptionsKt.stackTraceToString(it), false);
            }
         }

         return inventory;
      }

      private final YamlConfiguration defaultInventory() {
         YamlConfiguration yamlConfiguration = new YamlConfiguration();
         YamlConfiguration yamlConfiguration2 = yamlConfiguration;
         int value = 0;
         yamlConfiguration.options().copyDefaults(true).indent(4).parseComments(true);
         Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(NexoInventory.values());

         for(Object element : iterable) {
            NexoInventory setting;
            Object obj;
            String str;
            block: {
               setting = (NexoInventory)element;
               int value2 = 0;
               str = setting.getPath();
               Object obj2 = setting.defaultValue;
               Enum enumValue2 = obj2 instanceof Enum ? (Enum)obj2 : null;
               if ((obj2 instanceof Enum ? (Enum)obj2 : null) != null) {
                  obj = enumValue2.name();
                  if (obj != null) {
                     break block;
                  }
               }

               obj = setting.defaultValue;
            }

            yamlConfiguration2.set(str, obj);
            yamlConfiguration2.setComments(setting.getPath(), setting.comments);
            yamlConfiguration2.setInlineComments(setting.getPath(), setting.inlineComments);
         }

         return yamlConfiguration;
      }
   }
}
