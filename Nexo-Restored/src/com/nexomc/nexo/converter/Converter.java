package com.nexomc.nexo.converter;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.Reloadable;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001:\u0003\u0014\u0015\u0016B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\u0006\u0010\u0013\u001a\u00020\u0012R\u001e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005@BX\u0086.¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001e\u0010\n\u001a\u00020\t2\u0006\u0010\u0004\u001a\u00020\t@BX\u0086.¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001e\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0004\u001a\u00020\r@BX\u0086.¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u0017"},
   d2 = {"Lcom/nexomc/nexo/converter/Converter;", "Lcom/nexomc/nexo/utils/Reloadable;", "<init>", "()V", "value", "Lcom/nexomc/nexo/converter/Converter$OraxenConverter;", "oraxenConverter", "getOraxenConverter", "()Lcom/nexomc/nexo/converter/Converter$OraxenConverter;", "Lcom/nexomc/nexo/converter/Converter$ItemsAdderConverter;", "itemsadderConverter", "getItemsadderConverter", "()Lcom/nexomc/nexo/converter/Converter$ItemsAdderConverter;", "Lcom/nexomc/nexo/converter/Converter$NexoConverter;", "nexoConverter", "getNexoConverter", "()Lcom/nexomc/nexo/converter/Converter$NexoConverter;", "reload", "", "save", "NexoConverter", "OraxenConverter", "ItemsAdderConverter", "com.nexomc:core"}
)
public final class Converter implements Reloadable {
   private OraxenConverter oraxenConverter;
   private ItemsAdderConverter itemsadderConverter;
   private NexoConverter nexoConverter;

   public Converter() {
      this.reload();
   }

   @NotNull
   public final OraxenConverter getOraxenConverter() {
      OraxenConverter oraxenConverter2 = this.oraxenConverter;
      if (oraxenConverter2 != null) {
         return oraxenConverter2;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("oraxenConverter");
         return null;
      }
   }

   @NotNull
   public final ItemsAdderConverter getItemsadderConverter() {
      ItemsAdderConverter itemsAdderConverter = this.itemsadderConverter;
      if (itemsAdderConverter != null) {
         return itemsAdderConverter;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("itemsadderConverter");
         return null;
      }
   }

   @NotNull
   public final NexoConverter getNexoConverter() {
      NexoConverter nexoConverter2 = this.nexoConverter;
      if (nexoConverter2 != null) {
         return nexoConverter2;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("nexoConverter");
         return null;
      }
   }

   public void reload() {
      YamlConfiguration config = NexoPlugin.Companion.instance().configsManager().getConverter();
      this.oraxenConverter = new OraxenConverter(NexoYamlKt.section((ConfigurationSection)config, "oraxenConverter"));
      this.itemsadderConverter = new ItemsAdderConverter(NexoYamlKt.section((ConfigurationSection)config, "itemsadderConverter"));
      this.nexoConverter = new NexoConverter(NexoYamlKt.section((ConfigurationSection)config, "nexoConverter"));
   }

   public final void save() {
      Converter converter = this;

      Object obj;
      try {
         Converter converter2 = converter;
         int value = 0;
         File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
         File it = FilesKt.resolve(dataFolder, "converter.yml");
         int value2 = 0;
         YamlConfiguration c = NexoPlugin.Companion.instance().configsManager().getConverter();
         int value3 = 0;
         ConfigurationSection configurationSection = NexoYamlKt.section((ConfigurationSection)c, "itemsadderConverter");
         if (configurationSection != null) {
            ConfigurationSection configurationSection2 = NexoYamlKt.plus(configurationSection, "changedItemIds", converter2.getItemsadderConverter().getChangedItemIds());
            if (configurationSection2 != null) {
               NexoYamlKt.plus(configurationSection2, "hasBeenConverted", (Object)null);
            }
         }

         c.set("oraxenConverter.hasBeenConverted", (Object)null);
         c.set("nexoConverter.furnitureConverter", converter2.getNexoConverter().getFurnitureConverter());
         c.save(it);
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj);
      if (throwable != null) {
         Throwable it = throwable;
         int value4 = 0;
         it.printStackTrace();
      }

   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0014\b\u0002\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006B\u0013\b\u0016\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\u0005\u0010\tJ\u0015\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u001f\u0010\r\u001a\u00020\u00002\u0014\b\u0002\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0001J\u0014\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0011\u001a\u00020\u0012HÖ\u0081\u0004J\n\u0010\u0013\u001a\u00020\u0004HÖ\u0081\u0004R\u001d\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0014"},
      d2 = {"Lcom/nexomc/nexo/converter/Converter$NexoConverter;", "", "furnitureConverter", "", "", "<init>", "(Ljava/util/Map;)V", "config", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getFurnitureConverter", "()Ljava/util/Map;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "com.nexomc:core"}
   )
   public static final class NexoConverter {
      @NotNull
      private final Map<String, String> furnitureConverter;

      public NexoConverter(@NotNull Map<String, String> furnitureConverter) {
         super();
         this.furnitureConverter = furnitureConverter;
      }

      @NotNull
      public final Map<String, String> getFurnitureConverter() {
         return this.furnitureConverter;
      }

      public NexoConverter(@Nullable ConfigurationSection config) {
         Map map;
         block: {
            if (config != null) {
               ConfigurationSection configurationSection = NexoYamlKt.section(config, "furnitureConverter");
               if (configurationSection != null) {
                  map = NexoYamlKt.toTypedMap(configurationSection);
                  if (map != null) {
                     break block;
                  }
               }
            }

            map = MapsKt.emptyMap();
         }

         this(map);
      }

      @NotNull
      public final Map<String, String> component1() {
         return this.furnitureConverter;
      }

      @NotNull
      public final NexoConverter copy(@NotNull Map<String, String> furnitureConverter) {
         return new NexoConverter(furnitureConverter);
      }

      @NotNull
      public String toString() {
         return "NexoConverter(furnitureConverter=" + this.furnitureConverter + ")";
      }

      public int hashCode() {
         return this.furnitureConverter.hashCode();
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof NexoConverter)) {
            return false;
         } else {
            NexoConverter nexoConverter2 = (NexoConverter)other;
            return Intrinsics.areEqual(this.furnitureConverter, nexoConverter2.furnitureConverter);
         }
      }

      public NexoConverter() {
         this(kotlin.collections.MapsKt.emptyMap());
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003¢\u0006\u0004\b\u0007\u0010\bB\u0013\b\u0016\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n¢\u0006\u0004\b\u0007\u0010\u000bJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\u0016\u001a\u00020\u00032\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0018\u001a\u00020\u0019HÖ\u0081\u0004J\n\u0010\u001a\u001a\u00020\u001bHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\r¨\u0006\u001c"},
      d2 = {"Lcom/nexomc/nexo/converter/Converter$OraxenConverter;", "", "convertItems", "", "convertResourcePack", "convertSettings", "convertFurnitureOnLoad", "<init>", "(ZZZZ)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getConvertItems", "()Z", "getConvertResourcePack", "getConvertSettings", "getConvertFurnitureOnLoad", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   public static final class OraxenConverter {
      private final boolean convertItems;
      private final boolean convertResourcePack;
      private final boolean convertSettings;
      private final boolean convertFurnitureOnLoad;

      public OraxenConverter(boolean convertItems, boolean convertResourcePack, boolean convertSettings, boolean convertFurnitureOnLoad) {
         this.convertItems = convertItems;
         this.convertResourcePack = convertResourcePack;
         this.convertSettings = convertSettings;
         this.convertFurnitureOnLoad = convertFurnitureOnLoad;
      }

      public final boolean getConvertItems() {
         return this.convertItems;
      }

      public final boolean getConvertResourcePack() {
         return this.convertResourcePack;
      }

      public final boolean getConvertSettings() {
         return this.convertSettings;
      }

      public final boolean getConvertFurnitureOnLoad() {
         return this.convertFurnitureOnLoad;
      }

      public OraxenConverter(@Nullable ConfigurationSection section) {
         this(section != null ? section.getBoolean("convertItems") : true, section != null ? section.getBoolean("convertResourcePack") : true, section != null ? section.getBoolean("convertSettings") : true, section != null ? section.getBoolean("convertFurnitureOnLoad") : true);
      }

      public final boolean component1() {
         return this.convertItems;
      }

      public final boolean component2() {
         return this.convertResourcePack;
      }

      public final boolean component3() {
         return this.convertSettings;
      }

      public final boolean component4() {
         return this.convertFurnitureOnLoad;
      }

      @NotNull
      public final OraxenConverter copy(boolean convertItems, boolean convertResourcePack, boolean convertSettings, boolean convertFurnitureOnLoad) {
         return new OraxenConverter(convertItems, convertResourcePack, convertSettings, convertFurnitureOnLoad);
      }

      @NotNull
      public String toString() {
         return "OraxenConverter(convertItems=" + this.convertItems + ", convertResourcePack=" + this.convertResourcePack + ", convertSettings=" + this.convertSettings + ", convertFurnitureOnLoad=" + this.convertFurnitureOnLoad + ")";
      }

      public int hashCode() {
         int result = Boolean.hashCode(this.convertItems);
         result = result * 31 + Boolean.hashCode(this.convertResourcePack);
         result = result * 31 + Boolean.hashCode(this.convertSettings);
         result = result * 31 + Boolean.hashCode(this.convertFurnitureOnLoad);
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof OraxenConverter)) {
            return false;
         } else {
            OraxenConverter oraxenConverter2 = (OraxenConverter)other;
            if (this.convertItems != oraxenConverter2.convertItems) {
               return false;
            } else if (this.convertResourcePack != oraxenConverter2.convertResourcePack) {
               return false;
            } else if (this.convertSettings != oraxenConverter2.convertSettings) {
               return false;
            } else {
               return this.convertFurnitureOnLoad == oraxenConverter2.convertFurnitureOnLoad;
            }
         }
      }

      public OraxenConverter() {
         this(true, true, true, true);
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BE\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\u0014\b\u0002\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\b¢\u0006\u0004\b\n\u0010\u000bB\u0013\b\u0016\u0012\b\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\u0004\b\n\u0010\u000eJ\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\u0015\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\bHÆ\u0003JG\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\u0014\b\u0002\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\bHÆ\u0001J\u0014\u0010\u001c\u001a\u00020\u00032\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u001e\u001a\u00020\u001fHÖ\u0081\u0004J\n\u0010 \u001a\u00020\tHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u001d\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\t0\b¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015¨\u0006!"},
      d2 = {"Lcom/nexomc/nexo/converter/Converter$ItemsAdderConverter;", "", "convertItems", "", "convertResourcePack", "convertSettings", "convertFurnitureOnLoad", "changedItemIds", "", "", "<init>", "(ZZZZLjava/util/Map;)V", "config", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getConvertItems", "()Z", "getConvertResourcePack", "getConvertSettings", "getConvertFurnitureOnLoad", "getChangedItemIds", "()Ljava/util/Map;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "", "toString", "com.nexomc:core"}
   )
   public static final class ItemsAdderConverter {
      private final boolean convertItems;
      private final boolean convertResourcePack;
      private final boolean convertSettings;
      private final boolean convertFurnitureOnLoad;
      @NotNull
      private final Map<String, String> changedItemIds;

      public ItemsAdderConverter(boolean convertItems, boolean convertResourcePack, boolean convertSettings, boolean convertFurnitureOnLoad, @NotNull Map<String, String> changedItemIds) {
         super();
         this.convertItems = convertItems;
         this.convertResourcePack = convertResourcePack;
         this.convertSettings = convertSettings;
         this.convertFurnitureOnLoad = convertFurnitureOnLoad;
         this.changedItemIds = changedItemIds;
      }

      public final boolean getConvertItems() {
         return this.convertItems;
      }

      public final boolean getConvertResourcePack() {
         return this.convertResourcePack;
      }

      public final boolean getConvertSettings() {
         return this.convertSettings;
      }

      public final boolean getConvertFurnitureOnLoad() {
         return this.convertFurnitureOnLoad;
      }

      @NotNull
      public final Map<String, String> getChangedItemIds() {
         return this.changedItemIds;
      }

      public ItemsAdderConverter(@Nullable ConfigurationSection config) {
         Map map;
         ItemsAdderConverter itemsAdderConverter;
         boolean flag;
         boolean flag2;
         boolean flag3;
         boolean flag4;
         block: {
            /* this-alias removed */
            flag = config != null ? config.getBoolean("convertItems") : true;
            flag2 = config != null ? config.getBoolean("convertResourcePack") : true;
            flag3 = config != null ? config.getBoolean("convertSettings") : true;
            flag4 = config != null ? config.getBoolean("convertFurnitureOnLoad") : true;
            if (config != null) {
               ConfigurationSection configurationSection = NexoYamlKt.section(config, "changedItemIds");
               if (configurationSection != null) {
                  ConfigurationSection it = configurationSection;
                  boolean flag5 = flag4;
                  boolean flag6 = flag3;
                  boolean flag7 = flag2;
                  boolean flag8 = flag;
                  int value = 0;
                  Set keys = it.getKeys(false);
                  Iterable iterable = (Iterable)keys;
                  LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16));
                  Map destination = (Map)result;

                  for(Object element : iterable) {
                     String s = (String)element;
                     int value2 = 0;
                     String string = it.getString(s);
                     String str = string;
                     destination.put(element, str);
                  }

                  Map map2 = MapsKt.toMutableMap(destination);
                  /* this-alias removed */
                  flag = flag8;
                  flag2 = flag7;
                  flag3 = flag6;
                  flag4 = flag5;
                  map = map2;
                  if (map2 != null) {
                     break block;
                  }
               }
            }

            map = (Map)(new LinkedHashMap());
         }

         this(flag, flag2, flag3, flag4, map);
      }

      public final boolean component1() {
         return this.convertItems;
      }

      public final boolean component2() {
         return this.convertResourcePack;
      }

      public final boolean component3() {
         return this.convertSettings;
      }

      public final boolean component4() {
         return this.convertFurnitureOnLoad;
      }

      @NotNull
      public final Map<String, String> component5() {
         return this.changedItemIds;
      }

      @NotNull
      public final ItemsAdderConverter copy(boolean convertItems, boolean convertResourcePack, boolean convertSettings, boolean convertFurnitureOnLoad, @NotNull Map<String, String> changedItemIds) {
         return new ItemsAdderConverter(convertItems, convertResourcePack, convertSettings, convertFurnitureOnLoad, changedItemIds);
      }

      @NotNull
      public String toString() {
         return "ItemsAdderConverter(convertItems=" + this.convertItems + ", convertResourcePack=" + this.convertResourcePack + ", convertSettings=" + this.convertSettings + ", convertFurnitureOnLoad=" + this.convertFurnitureOnLoad + ", changedItemIds=" + this.changedItemIds + ")";
      }

      public int hashCode() {
         int result = Boolean.hashCode(this.convertItems);
         result = result * 31 + Boolean.hashCode(this.convertResourcePack);
         result = result * 31 + Boolean.hashCode(this.convertSettings);
         result = result * 31 + Boolean.hashCode(this.convertFurnitureOnLoad);
         result = result * 31 + this.changedItemIds.hashCode();
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof ItemsAdderConverter)) {
            return false;
         } else {
            ItemsAdderConverter itemsAdderConverter = (ItemsAdderConverter)other;
            if (this.convertItems != itemsAdderConverter.convertItems) {
               return false;
            } else if (this.convertResourcePack != itemsAdderConverter.convertResourcePack) {
               return false;
            } else if (this.convertSettings != itemsAdderConverter.convertSettings) {
               return false;
            } else if (this.convertFurnitureOnLoad != itemsAdderConverter.convertFurnitureOnLoad) {
               return false;
            } else {
               return Intrinsics.areEqual(this.changedItemIds, itemsAdderConverter.changedItemIds);
            }
         }
      }

      public ItemsAdderConverter() {
         this(true, true, true, true, new java.util.LinkedHashMap());
      }
   }
}
