package com.nexomc.nexo.configs;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.compatibilities.mmoitems.WrappedMMOItem;
import com.nexomc.nexo.compatibilities.mythiccrucible.WrappedCrucibleItem;
import com.nexomc.nexo.converter.NexoConverter;
import com.nexomc.nexo.glyphs.AnimatedGlyph;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.glyphs.ReferenceGlyph;
import com.nexomc.nexo.glyphs.RequiredGlyph;
import com.nexomc.nexo.glyphs.ShaderAnimatedGlyph;
import com.nexomc.nexo.glyphs.SpriteAnimatedGlyph;
import com.nexomc.nexo.items.CustomModelData;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.items.ItemParser;
import com.nexomc.nexo.items.ItemTemplate;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockRegistry;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock;
import com.nexomc.nexo.mechanics.custom_block.stringblock.stackable.StackableBlock;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.RangeUtilsKt;
import com.nexomc.nexo.utils.Reloadable;
import com.nexomc.nexo.utils.ResourceManager;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.inventories.NexoInventory;
import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntSortedMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.ExceptionsKt;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010&\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 :2\u00020\u0001:\u0001:B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\u0006\u0010\u0014\u001a\u00020\u0005J\u0006\u0010\u0004\u001a\u00020\u0005J\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016J%\u0010\u001f\u001a\u001a\u0012\u0004\u0012\u00020\u001a\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020\"0 0 H\u0000¢\u0006\u0002\b#J\u0006\u0010$\u001a\u00020\u0013J\u0006\u0010%\u001a\u00020\u0013J\u0006\u0010&\u001a\u00020\u0013J(\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020\"0 2\u0012\u0010,\u001a\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u00050-H\u0002J$\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020\"0 2\u0006\u0010.\u001a\u00020\u001a2\u0006\u0010/\u001a\u00020\u0005H\u0002J\u000e\u00100\u001a\b\u0012\u0004\u0012\u00020\u001a01H\u0002J\u000e\u00102\u001a\b\u0012\u0004\u0012\u00020\u001a01H\u0002J\u0018\u00103\u001a\u00020\u00132\u0006\u00104\u001a\u00020!2\u0006\u00105\u001a\u000206H\u0002J\u0018\u00107\u001a\u00020\u00052\u0006\u00108\u001a\u00020!2\u0006\u00109\u001a\u00020\u0005H\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u0007\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001e\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tR\u001e\u0010\f\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\tR\u001e\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\tR\u001e\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\tR(\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u00050\u00198@X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001b\u0010'\u001a\u00020\"8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b*\u0010+\u001a\u0004\b(\u0010)¨\u0006;"},
   d2 = {"Lcom/nexomc/nexo/configs/ConfigsManager;", "Lcom/nexomc/nexo/utils/Reloadable;", "<init>", "()V", "inventory", "Lorg/bukkit/configuration/file/YamlConfiguration;", "value", "mechanics", "getMechanics", "()Lorg/bukkit/configuration/file/YamlConfiguration;", "converter", "getConverter", "sounds", "getSounds", "languages", "getLanguages", "messages", "getMessages", "reload", "", "settings", "parseGlyphConfigs", "", "Lcom/nexomc/nexo/glyphs/Glyph;", "itemConfigs", "", "Ljava/io/File;", "getItemConfigs$com_nexomc_core", "()Ljava/util/Map;", "setItemConfigs$com_nexomc_core", "(Ljava/util/Map;)V", "parseItemConfig", "Lit/unimi/dsi/fastutil/objects/Object2ObjectLinkedOpenHashMap;", "", "Lcom/nexomc/nexo/items/ItemBuilder;", "parseItemConfig$com_nexomc_core", "assignAllUsedCustomVariations", "assignAllUsedCustomModelDatas", "parseAllItemTemplates", "ERROR_ITEM", "getERROR_ITEM", "()Lcom/nexomc/nexo/items/ItemBuilder;", "ERROR_ITEM$delegate", "Lkotlin/Lazy;", "entry", "", "itemFile", "config", "itemFiles", "", "glyphFiles", "validatePackSection", "itemId", "packSection", "Lorg/bukkit/configuration/ConfigurationSection;", "validate", "configName", "defaultConfiguration", "Companion", "com.nexomc:core"}
)
public final class ConfigsManager implements Reloadable {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private YamlConfiguration inventory;
   @NotNull
   private YamlConfiguration mechanics;
   @NotNull
   private YamlConfiguration converter;
   @NotNull
   private YamlConfiguration sounds;
   @NotNull
   private YamlConfiguration languages;
   @NotNull
   private YamlConfiguration messages;
   @NotNull
   private Map<File, YamlConfiguration> itemConfigs;
   @NotNull
   private final Lazy ERROR_ITEM$delegate;
   @NotNull
   private static final YamlConfiguration defaultMechanics;
   @NotNull
   private static final YamlConfiguration defaultConverter;
   @NotNull
   private static final YamlConfiguration defaultSounds;
   @NotNull
   private static final YamlConfiguration defaultLanguages;
   @NotNull
   private static final YamlConfiguration defaultMessages;
   @NotNull
   private static final Map<String, String> movedYamlKeys;
   @NotNull
   private static final List<String> removedYamlKeys;

   public ConfigsManager() {
      this.mechanics = defaultMechanics;
      this.converter = defaultConverter;
      this.sounds = defaultSounds;
      this.languages = defaultLanguages;
      this.messages = defaultMessages;
      this.itemConfigs = (Map)(new LinkedHashMap());
      this.ERROR_ITEM$delegate = LazyKt.lazy((() -> ConfigsManager.ERROR_ITEM_delegate$lambda$0(this)));
   }

   @NotNull
   public final YamlConfiguration getMechanics() {
      return this.mechanics;
   }

   @NotNull
   public final YamlConfiguration getConverter() {
      return this.converter;
   }

   @NotNull
   public final YamlConfiguration getSounds() {
      return this.sounds;
   }

   @NotNull
   public final YamlConfiguration getLanguages() {
      return this.languages;
   }

   @NotNull
   public final YamlConfiguration getMessages() {
      return this.messages;
   }

   public void reload() {
      Settings.Companion.reload();
      this.inventory = NexoInventory.Companion.validateInventory();
      this.mechanics = this.validate("mechanics.yml", defaultMechanics);
      this.converter = this.validate("converter.yml", defaultConverter);
      this.sounds = this.validate("sounds.yml", defaultSounds);
      this.languages = this.validate("languages.yml", defaultLanguages);
      File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
      File file = FilesKt.resolve(dataFolder, "messages");
      int value = 0;
      file.mkdirs();
      dataFolder = NexoPlugin.Companion.instance().getDataFolder();
      File file2 = FilesKt.resolve(dataFolder, "languages");
      int value2 = 0;
      dataFolder = file2.exists() && file2.isDirectory() ? file2 : null;
      if (dataFolder != null) {
         dataFolder.renameTo(file2);
      }

      this.messages = this.validate("messages/" + Settings.PLUGIN_LANGUAGE + ".yml", defaultMessages);
      AdventureUtils.INSTANCE.reload();
      String[] list = Companion.getItemsFolder$com_nexomc_core().list();
      if ((list == null || list.length == 0) && Settings.GENERATE_DEFAULT_CONFIGS.toBool()) {
         ResourceManager.INSTANCE.extractConfigsInFolder("items", "yml");
      }

      list = Companion.getGlyphsFolder().list();
      if (list == null || list.length == 0) {
         if (Settings.GENERATE_DEFAULT_CONFIGS.toBool()) {
            ResourceManager.INSTANCE.extractConfigsInFolder("glyphs", "yml");
         } else {
            ResourceManager.INSTANCE.extractConfiguration("glyphs/nexo_defaults/interface.yml");
         }
      }

      if (!Companion.getSchematicsFolder().exists() && Settings.GENERATE_DEFAULT_CONFIGS.toBool()) {
         ResourceManager.INSTANCE.extractConfigsInFolder("schematics", "schem");
      }

      this.getItemConfigs$com_nexomc_core().clear();
   }

   @NotNull
   public final YamlConfiguration settings() {
      return Settings.Companion.settings();
   }

   @NotNull
   public final YamlConfiguration inventory() {
      YamlConfiguration yamlConfiguration = this.inventory;
      if (yamlConfiguration == null) {
         YamlConfiguration it = NexoInventory.Companion.validateInventory();
         int value = 0;
         this.inventory = it;
         yamlConfiguration = it;
      }

      return yamlConfiguration;
   }

   @NotNull
   public final Collection<Glyph> parseGlyphConfigs() {
      java.lang.Object obj;
      java.lang.Object obj2;
      List output = (List)(new ArrayList());
      Map referenceGlyphs = (Map)(new LinkedHashMap());
      Map gifGlyphs = (Map)(new LinkedHashMap());
      AnimatedGlyph.Type type2 = VersionUtil.atleast("1.21.10") ? AnimatedGlyph.Type.SPRITE : AnimatedGlyph.Type.SHADER;
      AnimatedGlyph.Type defaultAnimatedType = Settings.DEFAULT_GIF_TYPE.toEnum(AnimatedGlyph.Type.class, type2);
      Glyph.Companion.getAssignedGlyphUnicodes$com_nexomc_core().clear();
      Iterable glyphFiles = (Iterable)this.glyphFiles();
      NexoConverter nexoConverter = NexoConverter.INSTANCE;
      int value = 0;

      for(Object element : glyphFiles) {
         File p0 = (File)element;
         int value2 = 0;
         nexoConverter.processGlyphConfigs(p0);
      }

      Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((glyphFiles instanceof Collection ? (Collection)glyphFiles : null) != null ? (glyphFiles instanceof Collection ? (Collection)glyphFiles : null).size() : 10), 16));
      Map destination = (Map)result;
      value = 0;

      for(Object obj5 : glyphFiles) {
         File it = (File)obj5;
         int value3 = 0;
         YamlConfiguration yamlConfiguration = NexoYaml.Companion.loadConfiguration(it);
         destination.put(obj5, yamlConfiguration);
      }

      Map map2 = (Map)((Object2ObjectLinkedOpenHashMap)destination);
      int value20 = 0;

      for(Map.Entry entry4 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
         int value4 = 0;
         YamlConfiguration section = (YamlConfiguration)entry4.getValue();
         Map map3 = NexoYamlKt.childSections((ConfigurationSection)section);

         for(Map.Entry entry5 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map3.entrySet()) {
            int value5 = 0;
            String glyphId = (String)entry5.getKey();
            ConfigurationSection glyphSection = (ConfigurationSection)entry5.getValue();
            if (glyphSection.contains("reference")) {
               referenceGlyphs.put(glyphId, glyphSection);
            }

            if (glyphSection.contains("gif")) {
               gifGlyphs.put(glyphId, glyphSection);
            } else {
               Map map = Glyph.Companion.getAssignedGlyphUnicodes$com_nexomc_core();
               Collection collection = (Collection)NexoYamlKt.stringListOrSingle(glyphSection, "char", (String)null);
               if (collection.isEmpty()) {
                  int value6 = 0;
               } else {
                  map.put(glyphId, collection);
               }
            }
         }
      }

      value20 = 0;

      for(Map.Entry entry6 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
         int value7 = 0;
         File file = (File)entry6.getKey();
         YamlConfiguration section = (YamlConfiguration)entry6.getValue();
         boolean fileChanged = false;
         Iterable entrySet = (Iterable)NexoYamlKt.childSections((ConfigurationSection)section).entrySet();
         int value8 = 0;

         for(Object obj6 : entrySet) {
            Map.Entry entry = (Map.Entry)obj6;
            int value9 = 0;
            String glyphId = (String)entry.getKey();
            ConfigurationSection glyphSection = (ConfigurationSection)entry.getValue();
            if (!referenceGlyphs.containsKey(glyphId) && !gifGlyphs.containsKey(glyphId)) {
               ConfigsManager it = this;

               Object obj7;
               try {
                  ConfigsManager configsManager3 = it;
                  int value10 = 0;
                  if (!Glyph.Companion.getAssignedGlyphUnicodes$com_nexomc_core().containsKey(glyphId)) {
                     fileChanged = true;
                  }

                  ((Collection)output).add(new Glyph(glyphSection));
                  obj7 = ResultShim.ok(Unit.INSTANCE);
               } catch (Throwable e) {
                  obj7 = ResultShim.ok(ResultKt.createFailure(e));
               }

               Throwable throwable = ResultShim.exceptionOrNull(obj7);
               if (throwable != null) {
                  Throwable it_33 = throwable;
                  int it_32 = 0;
                  Logs.logWarn("Failed to load glyph " + glyphId, false);
                  if (Settings.DEBUG.toBool()) {
                     Logs.logWarn(ExceptionsKt.stackTraceToString(it_33), false);
                  }
               }
            }
         }

         value8 = 0;

         for(Object obj8 : entrySet) {
            Map.Entry entry2 = (Map.Entry)obj8;
            int value11 = 0;
            String referenceId = (String)entry2.getKey();
            ConfigurationSection configurationSection = (ConfigurationSection)referenceGlyphs.get(referenceId);
            if (configurationSection != null) {
               ConfigurationSection referenceSection = configurationSection;
               String string = referenceSection.getString("index");
               if (string != null) {
                  IntRange intRange = RangeUtilsKt.toIntRangeOrNull(string);
                  if (intRange != null) {
                     IntRange index = intRange;
                     String str = referenceSection.getString("reference");
                     if (str != null) {
                        String glyphId = str;
                        Iterable it = (Iterable)output;
                        Iterator it_30 = it.iterator();

                        while(true) {
                           if (it_30.hasNext()) {
                              Object obj3 = it_30.next();
                              Glyph it_29 = (Glyph)obj3;
                              int value12 = 0;
                              if (!Intrinsics.areEqual(it_29.getId(), glyphId)) {
                                 continue;
                              }

                              obj2 = obj3;
                              break;
                           }

                           obj2 = null;
                           break;
                        }

                        Glyph glyph2 = (Glyph)obj2;
                        if (glyph2 == null) {
                           Logs.logError("Reference-Glyph " + referenceId + " tried referencing a Glyph " + glyphId + ", but it does not exist...", false);
                        } else {
                           Glyph glyph = glyph2;
                           String str2 = referenceSection.getString("permission");
                           if (str2 == null) {
                              str2 = glyph.getPermission();
                           }

                           String permission = str2;
                           Collection i = (Collection)NexoYamlKt.stringListOrSingle(referenceSection, "placeholder", (String)null);
                           Object obj4;
                           if (i.isEmpty()) {
                              int value21 = 0;
                              obj4 = glyph.getPlaceholders();
                           } else {
                              obj4 = i;
                           }

                           List placeholders = (List)obj4;
                           if (((CharSequence)CollectionsKt.joinToString((Iterable)glyph.getUnicodes(), (CharSequence)"", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null)).length() >= index.getLast() && index.getFirst() > 0) {
                              ConfigsManager configsManager = this;

                              Object result4;
                              try {
                                 ConfigsManager configsManager4 = configsManager;
                                 int it_28 = 0;
                                 ((Collection)output).add(new ReferenceGlyph(glyph, referenceId, index, permission, placeholders));
                                 result4 = ResultShim.ok(Unit.INSTANCE);
                              } catch (Throwable ex) {
                                 result4 = ResultShim.ok(ResultKt.createFailure(ex));
                              }

                              Throwable throwable2 = ResultShim.exceptionOrNull(result4);
                              if (throwable2 != null) {
                                 Throwable it_27 = throwable2;
                                 int value13 = 0;
                                 Logs.logWarn("Failed to load Reference Glyph " + glyphId, false);
                                 if (Settings.DEBUG.toBool()) {
                                    Logs.logWarn(ExceptionsKt.stackTraceToString(it_27), false);
                                 }
                              }
                           } else {
                              Object i_26 = CollectionsKt.count((Iterable)index) == 1 ? index.getFirst() : index;
                              Logs.logError("Reference-Glyph " + referenceId + " used invalid index " + i_26 + ", " + glyphId + " has indexes " + index, false);
                           }
                        }
                     }
                  }
               }
            }
         }

         value8 = 0;

         for(Object obj9 : entrySet) {
            Map.Entry entry3 = (Map.Entry)obj9;
            int value14 = 0;
            String gifId = (String)entry3.getKey();
            ConfigsManager configsManager2 = this;

            Object it;
            try {
               ConfigsManager configsManager5 = configsManager2;
               int value15 = 0;
               ConfigurationSection configurationSection2 = (ConfigurationSection)gifGlyphs.get(gifId);
               if (configurationSection2 == null) {
                  continue;
               }

               ConfigurationSection section_24 = configurationSection2;
               String key = "type";
               AnimatedGlyph.Type type3 = EnumUtils.getEnum(AnimatedGlyph.Type.class, NexoYamlKt.stringOrNull(section_24, key));
               if (type3 == null) {
                  int value16 = 0;
                  type3 = !section_24.contains("height") && !section_24.contains("ascent") ? defaultAnimatedType : null;
                  if (type3 == null) {
                     type3 = AnimatedGlyph.Type.SHADER;
                  }
               }

               AnimatedGlyph.Type type = type3;
               Collection collection2 = (Collection)output;
               AnimatedGlyph it_23 = ConfigsManager.WhenMappings.$EnumSwitchMapping$0[type.ordinal()] == 1 ? (AnimatedGlyph)(new SpriteAnimatedGlyph(section_24)) : (AnimatedGlyph)(new ShaderAnimatedGlyph(section_24));
               collection2.add(it_23);
               it = ResultShim.ok(Unit.INSTANCE);
            } catch (Throwable ex2) {
               it = ResultShim.ok(ResultKt.createFailure(ex2));
            }

            Throwable throwable3 = ResultShim.exceptionOrNull(it);
            if (throwable3 != null) {
               Throwable it_22 = throwable3;
               int value17 = 0;
               Logs.logWarn("Failed to load AnimatedGlyph " + gifId, false);
               if (Settings.DEBUG.toBool()) {
                  Logs.logWarn(ExceptionsKt.stackTraceToString(it_22), false);
               }
            }
         }

         if (fileChanged && !Settings.DISABLE_AUTOMATIC_GLYPH_CODE.toBool()) {
            ConfigsManager configsManager6 = this;

            Object result3;
            try {
               ConfigsManager configsManager7 = configsManager6;
               int value18 = 0;
               section.save(file);
               result3 = ResultShim.ok(Unit.INSTANCE);
            } catch (Throwable ex3) {
               result3 = ResultShim.ok(ResultKt.createFailure(ex3));
            }

            Throwable throwable4 = ResultShim.exceptionOrNull(result3);
            if (throwable4 != null) {
               Throwable it = throwable4;
               int value22 = 0;
               Logs.logWarn("Failed to save updated glyph file: " + file.getName(), false);
               if (Settings.DEBUG.toBool()) {
                  Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
               }
            }
         }
      }

      ConfigsManager configsManager8 = this;

      try {
         ConfigsManager configsManager9 = configsManager8;
         int value19 = 0;
         ((Collection)output).add(RequiredGlyph.INSTANCE);
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable ex4) {
         obj = ResultShim.ok(ResultKt.createFailure(ex4));
      }

      UtilsKt.printOnFailure(obj, false);
      return (Collection)output;
   }

   @NotNull
   public final Map<File, YamlConfiguration> getItemConfigs$com_nexomc_core() {
      if (this.itemConfigs.isEmpty()) {
         Iterable itemFiles = (Iterable)this.itemFiles();
         NexoYaml.Companion companion = NexoYaml.Companion;
         Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((itemFiles instanceof Collection ? (Collection)itemFiles : null) != null ? (itemFiles instanceof Collection ? (Collection)itemFiles : null).size() : 10), 16));
         Map destination = (Map)result;

         for(Object element : itemFiles) {
            File p0 = (File)element;
            int value = 0;
            YamlConfiguration yamlConfiguration = companion.loadConfiguration(p0);
            destination.put(element, yamlConfiguration);
         }

         this.itemConfigs = destination;
      }

      return this.itemConfigs;
   }

   public final void setItemConfigs$com_nexomc_core(@NotNull Map<File, YamlConfiguration> map) {
      this.itemConfigs = map;
   }

   @NotNull
   public final Object2ObjectLinkedOpenHashMap<File, Object2ObjectLinkedOpenHashMap<String, ItemBuilder>> parseItemConfig$com_nexomc_core() {
      Iterable entrySet = (Iterable)this.getItemConfigs$com_nexomc_core().entrySet();
      Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((entrySet instanceof Collection ? (Collection)entrySet : null) != null ? (entrySet instanceof Collection ? (Collection)entrySet : null).size() : 10), 16));
      Map destination = (Map)result;

      for(Object element : entrySet) {
         Map.Entry it = (Map.Entry)element;
         int value = 0;
         Pair pair = TuplesKt.to(it.getKey(), this.parseItemConfig(it));
         destination.put(pair.getFirst(), pair.getSecond());
      }

      return (Object2ObjectLinkedOpenHashMap)destination;
   }

   public final void assignAllUsedCustomVariations() {
      it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry entry;
      Map itemConfigs$com_nexomc_core = this.getItemConfigs$com_nexomc_core();

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)itemConfigs$com_nexomc_core.entrySet()) {
         int value = 0;
         File file = (File)element.getKey();
         YamlConfiguration config = (YamlConfiguration)element.getValue();
         Map map = NexoYamlKt.childSections((ConfigurationSection)config);

         for(Map.Entry entry2 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
            int value2 = 0;
            String itemId = (String)entry2.getKey();
            ConfigurationSection section = (ConfigurationSection)entry2.getValue();
            ConfigurationSection configurationSection = NexoYamlKt.section(section, "Mechanics.custom_block");
            if (configurationSection != null) {
               ConfigurationSection blockSection = configurationSection;
               String str = NexoYamlKt.stringOrNull(blockSection, "type");
               if (str != null) {
                  String blockType = str;
                  Integer number = section.getInt("Mechanics.custom_block.custom_variation");
                  int it = ((Number)number).intValue();
                  int value3 = 0;
                  Integer number2 = it > 0 ? number : null;
                  if ((it > 0 ? number : null) != null) {
                     int customVariation = number2;
                     Key key = NexoYamlKt.key(section, "Mechanics.custom_block.model");
                     if (key == null) {
                        key = NexoYamlKt.key(section, "Pack.model");
                        if (key == null) {
                           key = Key.key(itemId);
                        }
                     }

                     Key model;
                     List customVariations;
                     block: {
                        model = key;
                        customVariations = CollectionsKt.plus((Collection)CollectionsKt.plus((Collection)DirectionalBlock.Companion.extraVariations(blockSection), (Iterable)StackableBlock.Companion.extraVariations(blockSection)), customVariation);
                        Int2ObjectOpenHashMap int2ObjectOpenHashMap = (Int2ObjectOpenHashMap)CustomBlockRegistry.INSTANCE.getDATAS().get(blockType);
                        if (int2ObjectOpenHashMap != null) {
                           Int2ObjectMap.FastEntrySet int2ObjectEntrySet = int2ObjectOpenHashMap.int2ObjectEntrySet();
                           if (int2ObjectEntrySet != null) {
                              Iterable iterable = (Iterable)int2ObjectEntrySet;
                              Iterator iterator = iterable.iterator();

                              Object obj;
                              while(true) {
                                 if (!iterator.hasNext()) {
                                    obj = null;
                                    break;
                                 }

                                 Object obj2 = iterator.next();
                                 Int2ObjectMap.Entry it_16 = (Int2ObjectMap.Entry)obj2;
                                 int value4 = 0;
                                 if (customVariations.contains(it_16.getIntKey()) && !Intrinsics.areEqual(it_16.getValue(), model)) {
                                    obj = obj2;
                                    break;
                                 }
                              }

                              entry = (Int2ObjectMap.Entry)obj;
                              break block;
                           }
                        }

                        entry = null;
                     }

                     Int2ObjectMap.Entry existingBlock = entry;
                     if (existingBlock == null) {
                        Int2ObjectOpenHashMap datas = CustomBlockRegistry.INSTANCE.getDATAS().computeIfAbsent(blockType, ConfigsManager::assignAllUsedCustomVariations$lambda$0$0$2);
                        Iterable iterable2 = (Iterable)customVariations;

                        for(Object obj3 : iterable2) {
                           int it_12 = ((Number)obj3).intValue();
                           int value5 = 0;
                           ((Map)datas).put(it_12, model);
                        }
                     } else if (!section.contains("Mechanics.custom_block.directional") && !section.contains("Mechanics.custom_block.stackable")) {
                        Logs.logError("<red>" + itemId + "</red> in <red>" + file.getPath() + "</red> is using CustomVariation <yellow>" + customVariation + "</yellow>, which is already assigned to <red>" + existingBlock, false);
                     }
                  }
               }
            }
         }
      }

   }

   public final void assignAllUsedCustomModelDatas() {
      java.lang.String str;
      org.bukkit.Material material2;
      Map itemConfigs$com_nexomc_core = this.getItemConfigs$com_nexomc_core();

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)itemConfigs$com_nexomc_core.entrySet()) {
         int value = 0;
         File file = (File)element.getKey();
         YamlConfiguration config = (YamlConfiguration)element.getValue();
         Map map2 = NexoYamlKt.childSections((ConfigurationSection)config);

         for(Map.Entry entry3 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
            int value2 = 0;
            String itemId = (String)entry3.getKey();
            ConfigurationSection itemSection = (ConfigurationSection)entry3.getValue();
            ConfigurationSection configurationSection = NexoYamlKt.section(itemSection, "Pack");
            if (configurationSection != null) {
               ConfigurationSection packSection;
               block: {
                  packSection = configurationSection;
                  String string = itemSection.getString("material");
                  if (string != null) {
                     String p0 = string;
                     int value3 = 0;
                     material2 = Material.matchMaterial(p0);
                     if (material2 != null) {
                        break block;
                     }
                  }

                  material2 = (new WrappedCrucibleItem(itemSection)).getMaterial();
                  if (material2 == null) {
                     material2 = (new WrappedMMOItem(itemSection, true)).getMaterial();
                     if (material2 == null) {
                        material2 = Material.PAPER;
                     }
                  }
               }

               Material material = material2;
               if (!ItemTemplate.INSTANCE.isTemplate(itemId)) {
                  this.validatePackSection(itemId, packSection);
               }

               Integer number = NexoYamlKt.intOrNull(packSection, "custom_model_data");
               if (number != null) {
                  int modelData = number;
                  Key key = NexoYamlKt.key(packSection, "model");
                  if (key == null) {
                     key = KeyUtils.parseKey(itemId, false);
                  }

                  Key model;
                  block: {
                     model = key;
                     Object2IntLinkedOpenHashMap object2IntLinkedOpenHashMap = (Object2IntLinkedOpenHashMap)CustomModelData.Companion.getDATAS().get(material);
                     if (object2IntLinkedOpenHashMap != null) {
                        Object2IntSortedMap.FastSortedEntrySet object2IntEntrySet = object2IntLinkedOpenHashMap.object2IntEntrySet();
                        if (object2IntEntrySet != null) {
                           Iterable iterable = (Iterable)object2IntEntrySet;
                           Iterator iterator = iterable.iterator();

                           Object2IntMap.Entry entry;
                           while(true) {
                              if (!iterator.hasNext()) {
                                 entry = null;
                                 break;
                              }

                              Object obj = iterator.next();
                              Object2IntMap.Entry it = (Object2IntMap.Entry)obj;
                              int value4 = 0;
                              if (it.getIntValue() == modelData && !Intrinsics.areEqual(it.getKey(), model)) {
                                 entry = (Object2IntMap.Entry)obj;
                                 break;
                              }
                           }

                           Object2IntMap.Entry entry2 = entry;
                           if (entry2 != null) {
                              Key key2 = (Key)entry2.getKey();
                              if (key2 != null) {
                                 key2 = key2.key();
                                 if (key2 != null) {
                                    str = key2.asString();
                                    break block;
                                 }
                              }
                           }
                        }
                     }

                     str = null;
                  }

                  String existingModel = str;
                  if (existingModel == null) {
                     Map datas = (Map)CustomModelData.Companion.getDATAS();
                     Object obj3 = datas.get(material);
                     Object obj2;
                     if (obj3 == null) {
                        int answer = 0;
                        Object obj4 = new Object2IntLinkedOpenHashMap();
                        datas.put(material, obj4);
                        obj2 = obj4;
                     } else {
                        obj2 = obj3;
                     }

                     Map map = (Map)obj2;
                     Integer number2 = modelData;
                     map.put(model, number2);
                  } else {
                     Logs.logError("<red>" + itemId + "</red> in <red>" + file.getPath() + "</red> is using CustomModelData <yellow>" + modelData + "</yellow>, which is already assigned to <red>" + existingModel, false);
                  }
               }
            }
         }
      }

   }

   public final void parseAllItemTemplates() {
      ItemTemplate.INSTANCE.getItemTemplates().clear();
      Iterable iterable = (Iterable)this.getItemConfigs$com_nexomc_core().values();
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         YamlConfiguration it = (YamlConfiguration)element;
         int value = 0;
         Iterable list = (Iterable)MapsKt.toList(NexoYamlKt.childSections((ConfigurationSection)it));
         CollectionsKt.addAll(destination, list);
      }

      Map itemConfigs = MapsKt.toMap((Iterable)((List)destination));
      iterable = (Iterable)itemConfigs.values();
      destination = (Collection)(new ArrayList());

      for(Object obj : iterable) {
         ConfigurationSection it = (ConfigurationSection)obj;
         int value2 = 0;
         Iterable iterable2 = (Iterable)NexoYamlKt.stringListOrSingle(it, "template", (String)null);
         CollectionsKt.addAll(destination, iterable2);
      }

      iterable = (Iterable)CollectionsKt.distinct((Iterable)((List)destination));

      for(Object obj2 : iterable) {
         String it = (String)obj2;
         int value3 = 0;
         ConfigurationSection configurationSection = (ConfigurationSection)itemConfigs.get(it);
         if (configurationSection != null) {
            ConfigurationSection p0 = configurationSection;
            ItemTemplate itemTemplate = ItemTemplate.INSTANCE;
            int value4 = 0;
            itemTemplate.register(p0);
         }
      }

   }

   @NotNull
   public final ItemBuilder getERROR_ITEM() {
      Lazy lazy = this.ERROR_ITEM$delegate;
      return (ItemBuilder)lazy.getValue();
   }

   private final Object2ObjectLinkedOpenHashMap<String, ItemBuilder> parseItemConfig(Map.Entry<? extends File, ? extends YamlConfiguration> entry) {
      return this.parseItemConfig((File)entry.getKey(), (YamlConfiguration)entry.getValue());
   }

   private final Object2ObjectLinkedOpenHashMap<String, ItemBuilder> parseItemConfig(File itemFile, YamlConfiguration config) {
      Map map4 = NexoYamlKt.childSections((ConfigurationSection)config);
      int capacity = RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity(map4.size()), 16);
      Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(capacity);

      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map4.entrySet()) {
         Object key = entry.getKey();
         Object obj2 = entry.getValue();
         Map map = (Map)result;
         ConfigurationSection section = (ConfigurationSection)obj2;
         String itemId = (String)key;
         int value = 0;
         ItemParser itemParser2 = !ItemTemplate.INSTANCE.isTemplate(itemId) ? new ItemParser(section) : null;
         if (itemParser2 != null) {
            ItemParser itemId_7 = itemParser2;
            map.put(key, itemId_7);
         }
      }

      Object2ObjectLinkedOpenHashMap itemParsers = result;
      Map map5 = (Map)result;
      capacity = 0;
      int value7 = RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity(map5.size()), 16);
      Object2ObjectLinkedOpenHashMap object2ObjectLinkedOpenHashMap = new Object2ObjectLinkedOpenHashMap(value7);

      for(Map.Entry entry2 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map5.entrySet()) {
         Object obj3 = entry2.getKey();
         Object obj4 = entry2.getValue();
         Map map2 = (Map)object2ObjectLinkedOpenHashMap;
         ItemParser itemParser = (ItemParser)obj4;
         String str = (String)obj3;
         Map map3 = map2;
         int value2 = 0;
         ConfigsManager configsManager = this;

         Object obj5;
         try {
            ConfigsManager configsManager3 = configsManager;
            int value3 = 0;
            obj5 = ResultShim.ok(itemParser.buildItem());
         } catch (Throwable e) {
            obj5 = ResultShim.ok(ResultKt.createFailure(e));
         }

         Object obj = obj5;
         Throwable throwable = ResultShim.exceptionOrNull(obj5);
         if (throwable != null) {
            Throwable it = throwable;
            int value4 = 0;
            Logs.logError("ERROR BUILDING ITEM \"" + str + "\" from file " + itemFile.getPath(), false);
            if (Settings.DEBUG.toBool()) {
               Logs.logError(ExceptionsKt.stackTraceToString(it), false);
            } else {
               String message = it.getMessage();
               if (message != null) {
                  String p0 = message;
                  Logs logs = Logs.INSTANCE;
                  int value5 = 0;
                  Logs.logWarn(p0, false);
               }
            }
         }

         ItemBuilder itemBuilder = (ItemBuilder)(ResultShim.isFailure(obj) ? null : obj);
         if (itemBuilder == null) {
            itemBuilder = this.getERROR_ITEM().itemName((Component)Component.text(str));
         }

         ItemBuilder itemBuilder2 = itemBuilder;
         map3.put(obj3, itemBuilder2);
      }

      Object2ObjectLinkedOpenHashMap items = object2ObjectLinkedOpenHashMap;
      Collection collection = itemParsers.values();
      Iterable iterable = (Iterable)collection;
      value7 = 0;
      boolean flag;
      if (((Collection)iterable).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator = iterable.iterator();

         while(true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            Object element = iterator.next();
            ItemParser p0 = (ItemParser)element;
            int value6 = 0;
            if (p0.isConfigUpdated()) {
               flag = true;
               break;
            }
         }
      }

      if (flag) {
         ConfigsManager configsManager2 = this;

         Object result2;
         try {
            ConfigsManager configsManager4 = configsManager2;
            value7 = 0;
            config.save(itemFile);
            result2 = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable ex) {
            result2 = ResultShim.ok(ResultKt.createFailure(ex));
         }

         UtilsKt.printOnFailure(result2, true);
      }

      return items;
   }

   private final List<File> itemFiles() {
      Iterable iterable = (Iterable)FileUtilsKt.listYamlFiles(Companion.getItemsFolder$com_nexomc_core(), true);
      NexoYaml.Companion companion = NexoYaml.Companion;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         File p0 = (File)element;
         int value = 0;
         if (companion.isValidYaml(p0)) {
            destination.add(element);
         }
      }

      iterable = (Iterable)((List)destination);
      return CollectionsKt.sortedWith(iterable, new ConfigsManager$itemFiles$$inlined$sortedBy$1());
   }

   private final List<File> glyphFiles() {
      Iterable iterable = (Iterable)FileUtilsKt.listYamlFiles(Companion.getGlyphsFolder(), true);
      NexoYaml.Companion companion = NexoYaml.Companion;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         File p0 = (File)element;
         int value = 0;
         if (companion.isValidYaml(p0)) {
            destination.add(element);
         }
      }

      iterable = (Iterable)((List)destination);
      return CollectionsKt.sortedWith(iterable, new ConfigsManager$glyphFiles$$inlined$sortedBy$1());
   }

   private final void validatePackSection(String itemId, ConfigurationSection packSection) {
      String str;
      block: {
         str = packSection.getString("model");
         if (str != null) {
            String it = str;
            int value = 0;
            str = ((CharSequence)it).length() != 0 ? it : null;
            if (str != null) {
               break block;
            }
         }

         str = itemId;
      }

      String model = str;
      if (!Key.parseable(model)) {
         Logs.logWarn("Found invalid model in NexoItem <blue>" + itemId + "</blue>: <aqua>" + model, false);
         Logs.logWarn("Model-paths must only contain characters <yellow>[a-z0-9/._-]", false);
      }

      Iterable iterable = (Iterable)NexoYamlKt.stringListOrSingle(packSection, "texture", (String)null);

      for(Object element : iterable) {
         String texture = (String)element;
         int value2 = 0;
         if (!Key.parseable(texture)) {
            Logs.logWarn("Found invalid texture in NexoItem <blue>" + itemId + "</blue>: <aqua>" + texture, false);
            Logs.logWarn("Texture-paths must only contain characters <yellow>[a-z0-9/._-]", false);
         }
      }

   }

   private final YamlConfiguration validate(String configName, YamlConfiguration defaultConfiguration) {
      File configFile = ResourceManager.INSTANCE.extractConfiguration(configName);
      YamlConfiguration config = NexoYaml.Companion.loadConfiguration(configFile);
      boolean updated = false;
      Set keys = defaultConfiguration.getKeys(true);
      Iterable iterable = (Iterable)keys;

      for(Object element : iterable) {
         String key = (String)element;
         int value = 0;
         if (!StringsKt.startsWith(key, NexoInventory.LAYOUT.getPath(), false) && config.get(key) == null) {
            updated = true;
            Message message = Message.UPDATING_CONFIG;
            TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("option", key)};
            message.log(tagResolvers);
            config.set(key, defaultConfiguration.get(key));
         }
      }

      keys = config.getKeys(true);
      iterable = (Iterable)keys;
      Collection destination = (Collection)(new ArrayList());

      for(Object obj : iterable) {
         String it = (String)obj;
         int value2 = 0;
         if (removedYamlKeys.contains(it)) {
            destination.add(obj);
         }
      }

      iterable = (Iterable)((List)destination);

      for(Object obj2 : iterable) {
         String key = (String)obj2;
         updated = true;
         Message message2 = Message.REMOVING_CONFIG;
         TagResolver[] tagResolvers2 = new TagResolver[]{AdventureUtils.tagResolver("option", key)};
         message2.log(tagResolvers2);
         config.set(key, (Object)null);
      }

      keys = config.getKeys(false);
      iterable = (Iterable)keys;
      LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16));
      Map map = (Map)result;

      for(Object obj3 : iterable) {
         String it = (String)obj3;
         int value3 = 0;
         String str = movedYamlKeys.get(it);
         map.put(obj3, str);
      }


      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         String old = (String)entry.getKey();
         String str2 = (String)entry.getValue();
         if (str2 != null) {
            updated = true;
            Message message3 = Message.UPDATING_CONFIG;
            TagResolver[] tagResolvers3 = new TagResolver[]{AdventureUtils.tagResolver("option", str2)};
            message3.log(tagResolvers3);
            config.set(str2, config.get(old));
            config.set(old, (Object)null);
         }
      }

      ConfigsManager configsManager = this;

      Object result1;
      try {
         ConfigsManager configsManager2 = configsManager;
         int value4 = 0;
         if (updated) {
            config.save(configFile);
         }

         result1 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         result1 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(result1);
      if (throwable != null) {
         Throwable it = throwable;
         int value5 = 0;
         Logs.logError("Failed to save updated configuration file: " + configFile.getName(), false);
         if (Settings.DEBUG.toBool()) {
            Logs.logError(ExceptionsKt.stackTraceToString(it), false);
         }
      }

      return config;
   }

   private static final Int2ObjectOpenHashMap assignAllUsedCustomVariations$lambda$0$0$2(Object it) {
      return new Int2ObjectOpenHashMap();
   }

   private static final ItemBuilder ERROR_ITEM_delegate$lambda$0(ConfigsManager configsManager) {
      Object obj2;
      try {
         int value = 0;
         ConfigurationSection toConfigSection = Settings.ERROR_ITEM.toConfigSection();
         obj2 = ResultShim.ok((new ItemParser(toConfigSection)).buildItem());
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj2;
      ItemBuilder itemBuilder = new ItemBuilder(Material.PODZOL);
      return (ItemBuilder)(ResultShim.isFailure(obj) ? itemBuilder : obj);
   }

   static {
      defaultMechanics = Companion.extractDefault("mechanics.yml");
      defaultConverter = Companion.extractDefault("converter.yml");
      defaultSounds = Companion.extractDefault("sounds.yml");
      defaultLanguages = Companion.extractDefault("languages.yml");
      defaultMessages = Companion.extractDefault("messages/english.yml");
      Pair[] pairs = new Pair[]{TuplesKt.to("noteblock", "custom_blocks.noteblock"), TuplesKt.to("stringblock", "custom_blocks.stringblock"), TuplesKt.to("chorusblock", "custom_blocks.chorusblock")};
      movedYamlKeys = MapsKt.mapOf(pairs);
      removedYamlKeys = CollectionsKt.emptyList();
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0010 \n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0014H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\u00020\u000b8@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0014\u0010\u000e\u001a\u00020\u000b8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0010\u001a\u00020\u000b8F¢\u0006\u0006\u001a\u0004\b\u0011\u0010\rR\u001a\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00140\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00140\u0018X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"},
      d2 = {"Lcom/nexomc/nexo/configs/ConfigsManager$Companion;", "", "<init>", "()V", "defaultMechanics", "Lorg/bukkit/configuration/file/YamlConfiguration;", "defaultConverter", "defaultSounds", "defaultLanguages", "defaultMessages", "itemsFolder", "Ljava/io/File;", "getItemsFolder$com_nexomc_core", "()Ljava/io/File;", "glyphsFolder", "getGlyphsFolder", "schematicsFolder", "getSchematicsFolder", "extractDefault", "source", "", "movedYamlKeys", "", "removedYamlKeys", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final File getItemsFolder$com_nexomc_core() {
         return FilesKt.resolve(Settings.Companion.getDataFolder(), "items");
      }

      private final File getGlyphsFolder() {
         return FilesKt.resolve(Settings.Companion.getDataFolder(), "glyphs");
      }

      @NotNull
      public final File getSchematicsFolder() {
         return FilesKt.resolve(Settings.Companion.getDataFolder(), "schematics");
      }

      private final YamlConfiguration extractDefault(String source) {
         org.bukkit.configuration.file.YamlConfiguration yamlConfiguration;
         InputStream resource = NexoPlugin.Companion.instance().getResource(source);
         YamlConfiguration yamlConfiguration2;
         if (resource != null) {
            InputStream inputStream = resource;
            Charset charset = Charsets.UTF_8;
            Reader reader = (Reader)(new InputStreamReader(inputStream, charset));
            short value = 8192;
            Closeable closeable = (Closeable)(reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader, value));
            Throwable throwable = null;

            try {
               BufferedReader it = (BufferedReader)closeable;
               int value2 = 0;
               Companion companion = ConfigsManager.Companion;

               Object obj2;
               try {
                  int value3 = 0;
                  obj2 = ResultShim.ok(YamlConfiguration.loadConfiguration((Reader)it));
               } catch (Throwable e) {
                  obj2 = ResultShim.ok(ResultKt.createFailure(e));
               }

               Object obj = obj2;
               Throwable throwable2 = ResultShim.exceptionOrNull(obj2);
               if (throwable2 != null) {
                  Throwable it_1 = throwable2;
                  int value4 = 0;
                  Logs.logError("Failed to extract default file: " + source, false);
                  if (Settings.DEBUG.toBool()) {
                     Logs.logError(ExceptionsKt.stackTraceToString(it_1), false);
                  }
               }

               yamlConfiguration = (YamlConfiguration)(ResultShim.isFailure(obj) ? null : obj);
            } catch (Throwable ex) {
               throwable = ex;
               throw ex;
            } finally {
               CloseableKt.closeFinally(closeable, throwable);
            }

            yamlConfiguration2 = yamlConfiguration;
            if (yamlConfiguration != null) {
               return yamlConfiguration2;
            }
         }

         yamlConfiguration2 = new YamlConfiguration();
         return yamlConfiguration2;
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[com.nexomc.nexo.glyphs.AnimatedGlyph.Type.values().length];
         try { a_0[com.nexomc.nexo.glyphs.AnimatedGlyph.Type.SPRITE.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
