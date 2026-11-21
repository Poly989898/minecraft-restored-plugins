package com.nexomc.nexo.converter;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteMechanicHelpers;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringMechanicHelpers;
import com.nexomc.nexo.mechanics.furniture.seats.FurnitureSeat;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.FileUtils;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.customarmor.CustomArmorType;
import com.nexomc.nexo.utils.logs.Logs;
import io.leangen.geantyref.TypeToken;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.io.FilesKt;
import kotlin.io.OnErrorAction;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.text.StringsKt;
import net.kyori.adventure.text.Component;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ScopedConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0007\u001a\u00020\bJ\u000e\u0010\u000b\u001a\u00020\b2\u0006\u0010\u0006\u001a\u00020\u0005J\u000e\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u0005J\u000e\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u0005J\u000e\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0012"},
   d2 = {"Lcom/nexomc/nexo/converter/OraxenConverter;", "", "<init>", "()V", "nexoFolder", "Ljava/io/File;", "oraxenFolder", "convert", "", "customArmorType", "Lcom/nexomc/nexo/utils/customarmor/CustomArmorType;", "processSettings", "processRecipes", "recipeFile", "processItemConfigs", "itemFile", "processPackFolder", "packFolder", "com.nexomc:core"}
)
public final class OraxenConverter {
   @NotNull
   public static final OraxenConverter INSTANCE = new OraxenConverter();
   @NotNull
   private static final File nexoFolder;
   @NotNull
   private static final File oraxenFolder;
   @NotNull
   private static CustomArmorType customArmorType;

   private OraxenConverter() {
   }

   public final void convert() {
      try {
      java.lang.String[] strs;
      Converter.OraxenConverter oraxenConverter = NexoPlugin.Companion.instance().converter().getOraxenConverter();
      if (oraxenFolder.exists() && !FileUtils.INSTANCE.isEmpty(oraxenFolder)) {
         Logs.logInfo("Starting conversion of Oraxen-setup...");
         File file = oraxenFolder;
         String[] strs2 = new String[]{"messages"};
         Iterable iterable = (Iterable)FileUtilsKt.listYamlFiles(FileUtilsKt.resolve(file, strs2), false);
         int value = 0;

         for(Object element : iterable) {
            File lang = (File)element;
            int value2 = 0;
            Pair[] pairs = new Pair[]{TuplesKt.to("<gradient:#9055FF:#13E2DA>", "<gradient:#46C392:#7FC794>"), TuplesKt.to("Oraxen", "Nexo"), TuplesKt.to("/o", "/nexo"), TuplesKt.to("oraxen", "nexo")};
            FileUtilsKt.replaceText(lang, pairs);
         }

         if (oraxenConverter.getConvertSettings()) {
            this.processSettings(nexoFolder);
         }

         String[] strs3 = new String[]{"sound.yml", "hud.yml", "gestures", "font.yml"};
         Object[] objs = strs3;
         value = 0;
         int value3 = 0;

         for(int i = objs.length; value3 < i; ++value3) {
            Object obj = objs[value3];
            int value4 = 0;
            file = oraxenFolder;
            String[] strs4 = new String[]{(String)obj};
            FilesKt.deleteRecursively(FileUtilsKt.resolve(file, strs4));
            file = nexoFolder;
            strs4 = new String[]{(String)obj};
            FilesKt.deleteRecursively(FileUtilsKt.resolve(file, strs4));
         }

         if (oraxenConverter.getConvertItems()) {
            file = oraxenFolder;
            String[] strs5 = new String[]{"items"};
            Iterable iterable2 = (Iterable)FileUtilsKt.listYamlFiles(FileUtilsKt.resolve(file, strs5), false);
            value = 0;

            for(Object obj2 : iterable2) {
               File it = (File)obj2;
               File it_49 = it;
               INSTANCE.processItemConfigs(it_49);
               Logs.logSuccess("Finished converting item-config " + it_49.getName(), false);
               File parentFile = it_49.getParentFile();
               String[] strs6 = new String[]{"oraxen_items", null};
               String name = it_49.getName();
               strs6[1] = name;
               FilesKt.copyTo(it_49, FileUtilsKt.resolve(parentFile, strs6), false, 8192);
               it_49.delete();
            }

            file = oraxenFolder;
            String[] strs7 = new String[]{"recipes"};
            Iterable iterable3 = (Iterable)FileUtilsKt.listYamlFiles(FileUtilsKt.resolve(file, strs7), false);
            value = 0;

            for(Object obj3 : iterable3) {
               File it = (File)obj3;
               int value5 = 0;
               if (((CharSequence)FilesKt.readText(it, kotlin.text.Charsets.UTF_8)).length() != 0) {
                  INSTANCE.processRecipes(it);
                  Logs.logSuccess("Finished converting recipe-config " + it.getName(), false);
               }
            }
         }

         file = oraxenFolder;
         String[] strs8 = new String[]{"glyphs"};
         Iterable iterable4 = (Iterable)FileUtilsKt.listYamlFiles(FileUtilsKt.resolve(file, strs8), true);
         value = 0;

         for(Object obj4 : iterable4) {
            File it;
            block: {
               File target;
               block2: {
                  it = (File)obj4;
                  int value6 = 0;
                  File glyphsFolder = FileUtilsKt.resolve(it.getParentFile(), new String[]{"oraxen_glyphs"});
                  target = FileUtilsKt.resolve(glyphsFolder, new String[]{it.getName()});
                  String fileName = it.getName();
                  if (fileName != null) {
                     switch (fileName) {
                        case "required.yml":
                        case "interface.yml":
                           YamlConfiguration yamlConfiguration = NexoYaml.Companion.loadConfiguration(it);
                           int value7 = 0;
                           yamlConfiguration.set("menu_items", (Object)null);
                           yamlConfiguration.save(it);
                           FilesKt.copyTo(it, target, false, 8192);
                           break block;
                        case "shifts.yml":
                           break block2;
                     }
                  }

                  FilesKt.copyTo(it, target, false, 8192);
                  break block;
               }

               Logs.logWarn("Skipped " + it.getPath() + " due to Nexo providing this", false);
               Boolean delete = target.delete();
            }

            it.delete();
         }

         if (oraxenConverter.getConvertResourcePack()) {
            file = oraxenFolder;
            String[] strs9 = new String[]{"pack"};
            File packFolder = FileUtilsKt.resolve(file, strs9);
            this.processPackFolder(packFolder);
            strs9 = new String[]{"pack.mcmeta"};
            FileUtilsKt.resolve(packFolder, strs9).delete();
            strs9 = new String[]{"pack.png"};
            FileUtilsKt.resolve(packFolder, strs9).delete();
            Logs.logSuccess("Finished converting Resourcepack folders", false);
         }

         FilesKt.copyRecursively(oraxenFolder, nexoFolder, false, OraxenConverter::convert$lambda$5);
         FilesKt.deleteRecursively(oraxenFolder);
         Logs.logSuccess("Finished conversion of Oraxen- to Nexo-setup!.", false);
      }
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   public final void processSettings(@NotNull File oraxenFolder) {
      try {
      YamlConfigurationLoader.Builder builder = YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).indent(2);
      String[] strs = new String[]{"settings.yml"};
      YamlConfigurationLoader settingsLoader = ((YamlConfigurationLoader.Builder)builder.file(FileUtilsKt.resolve(oraxenFolder, strs))).build();
      OraxenConverter oraxenConverter = this;

      Object obj;
      try {
         OraxenConverter oraxenConverter2 = oraxenConverter;
         int value = 0;
         obj = ResultShim.ok((CommentedConfigurationNode)settingsLoader.load());
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj2 = UtilsKt.printOnFailure(obj, false);
      CommentedConfigurationNode commentedConfigurationNode = (CommentedConfigurationNode)(ResultShim.isFailure(obj2) ? null : obj2);
      if (commentedConfigurationNode != null) {
         CommentedConfigurationNode settings = commentedConfigurationNode;
         int value2 = 0;
         settings.removeChild("CustomBlocks");
         CommentedConfigurationNode it = null;
         ConfigurationNode configurationNode = ConfigHelpersKt.renameNode((ConfigurationNode)settings.node(new Object[]{"ConfigsTools"}), "ConfigTools");
         if (configurationNode != null) {
            Object[] objs = new Object[]{"error_item", "injectID"};
            configurationNode = configurationNode.node(objs);
            if (configurationNode != null) {
               ConfigHelpersKt.renameNode(configurationNode, "injectId");
            }
         }

         CommentedConfigurationNode customArmorNode = null;
         it = (CommentedConfigurationNode)settings.node(new Object[]{"Pack", "server", "polymath"});
         int pack = 0;
         Object[] objs2 = new Object[]{"server"};
         ((CommentedConfigurationNode)it.node(objs2)).set("atlas.nexomc.com");
         CommentedConfigurationNode commentedConfigurationNode2 = (CommentedConfigurationNode)((CommentedConfigurationNode)it.node(new Object[]{"secret"})).set("nexomc");
         ScopedConfigurationNode scopedConfigurationNode = null;
         customArmorNode = (CommentedConfigurationNode)settings.node(new Object[]{"CustomArmor", "type"});
         if (Intrinsics.areEqual(customArmorNode.getString(), "SHADER")) {
            customArmorNode.set(customArmorType.name());
         } else {
            CustomArmorType.Companion companion = CustomArmorType.Companion;
            String string = customArmorNode.getString(customArmorType.name());
            customArmorType = companion.fromString(string);
         }

         ConfigHelpersKt.removeChildNode((ConfigurationNode)settings, "oraxen_inventory");
         scopedConfigurationNode = settings.node(new Object[]{"Pack"});
         CommentedConfigurationNode pack_45 = (CommentedConfigurationNode)scopedConfigurationNode;
         int value3 = 0;
         pack_45.removeChild("upload");
         pack_45.removeChild("receive");
         pack_45.removeChild("import");
         pack_45.removeChild("generation");
         Object[] it_44 = new Object[]{"dispatch", "prompt"};
         CommentedConfigurationNode it_43 = (CommentedConfigurationNode)pack_45.node(it_44);
         int value4 = 0;
         String str = it_43.getString();
         it_43.set(str != null ? StringsKt.replace(str, "Oraxen", "Nexo", false) : null);
         settingsLoader.save((ConfigurationNode)settings);
      }

      strs = new String[]{"sound.yml"};
      File file = FileUtilsKt.resolve(oraxenFolder, strs);
      strs = new String[]{"sounds.yml"};
      file.renameTo(FileUtilsKt.resolve(oraxenFolder, strs));
      YamlConfigurationLoader.Builder builder2 = YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).indent(2);
      String[] strs2 = new String[]{"sounds.yml"};
      YamlConfigurationLoader soundsLoader = ((YamlConfigurationLoader.Builder)builder2.file(FileUtilsKt.resolve(oraxenFolder, strs2))).build();
      OraxenConverter oraxenConverter3 = this;

      Object obj3;
      try {
         OraxenConverter oraxenConverter4 = oraxenConverter3;
         int value5 = 0;
         obj3 = ResultShim.ok((CommentedConfigurationNode)soundsLoader.load());
      } catch (Throwable ex) {
         obj3 = ResultShim.ok(ResultKt.createFailure(ex));
      }

      Object obj4 = UtilsKt.printOnFailure(obj3, false);
      CommentedConfigurationNode commentedConfigurationNode3 = (CommentedConfigurationNode)(ResultShim.isFailure(obj4) ? null : obj4);
      if (commentedConfigurationNode3 != null) {
         CommentedConfigurationNode sounds = commentedConfigurationNode3;
         int value6 = 0;
         Iterable iterable = null;
         ConfigHelpersKt.renameNode((ConfigurationNode)sounds.node(new Object[]{"settings", "automatically_generate"}), "generate_sounds");
         iterable = ((CommentedConfigurationNode)sounds.node(new Object[]{"sounds"})).childrenMap().keySet();
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value7 = 0;
            String s = StringsKt.removePrefix(StringsKt.removePrefix(element.toString(), (CharSequence)"block."), (CharSequence)"required.");
            int value8 = 0;
            if (StringsKt.startsWith(s, "stone", false) || StringsKt.startsWith(s, "wood", false)) {
               destination.add(element);
            }
         }

         iterable = (Iterable)((List)destination);

         for(Object obj5 : iterable) {
            int value9 = 0;
            Object[] it = new Object[]{"sounds"};
            ((CommentedConfigurationNode)sounds.node(it)).removeChild(obj5);
         }

         soundsLoader.save((ConfigurationNode)sounds);
      }

      } catch (Exception ex2) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex2);
      }
   }

   public final void processRecipes(@NotNull File recipeFile) {
      try {
      YamlConfigurationLoader recipeLoader = ((YamlConfigurationLoader.Builder)YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).indent(2).file(recipeFile)).build();
      OraxenConverter it = this;

      Object obj;
      try {
         OraxenConverter oraxenConverter = it;
         int value = 0;
         obj = ResultShim.ok((CommentedConfigurationNode)recipeLoader.load());
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object it_40 = obj;
      Throwable throwable = ResultShim.exceptionOrNull(obj);
      if (throwable != null) {
         Throwable it_39 = throwable;
         int value2 = 0;
         Logs.logError("Failed to load " + recipeFile.getName() + " for parsing...", false);
         it_39.printStackTrace();
      }

      CommentedConfigurationNode commentedConfigurationNode = (CommentedConfigurationNode)(ResultShim.isFailure(it_40) ? null : it_40);
      if (commentedConfigurationNode != null) {
         CommentedConfigurationNode recipeYaml = commentedConfigurationNode;
         Iterable iterable = (Iterable)recipeYaml.childrenMap().values();
         int value3 = 0;

         for(Object element : iterable) {
            CommentedConfigurationNode recipeNode = (CommentedConfigurationNode)element;
            int value4 = 0;
            Object[] objs = new Object[]{"result"};
            commentedConfigurationNode = (CommentedConfigurationNode)recipeNode.node(objs);
            if (commentedConfigurationNode != null) {
               ConfigurationNode configurationNode = ConfigHelpersKt.removeChildNode((ConfigurationNode)commentedConfigurationNode, "oraxen_item");
               if (configurationNode != null) {
                  String string = configurationNode.getString();
                  if (string != null) {
                     String it_38 = string;
                     int value5 = 0;
                     Object[] objs2 = new Object[]{"result", "nexo_item"};
                     CommentedConfigurationNode commentedConfigurationNode2 = (CommentedConfigurationNode)((CommentedConfigurationNode)recipeNode.node(objs2)).set(it_38);
                  }
               }
            }

            commentedConfigurationNode = (CommentedConfigurationNode)recipeNode.node(new Object[]{"input"});
            if (commentedConfigurationNode != null) {
               ConfigurationNode configurationNode2 = ConfigHelpersKt.removeChildNode((ConfigurationNode)commentedConfigurationNode, "oraxen_item");
               if (configurationNode2 != null) {
                  String str = configurationNode2.getString();
                  if (str != null) {
                     String it_37 = str;
                     int value6 = 0;
                     Object[] objs3 = new Object[]{"input", "nexo_item"};
                     CommentedConfigurationNode commentedConfigurationNode3 = (CommentedConfigurationNode)((CommentedConfigurationNode)recipeNode.node(objs3)).set(it_37);
                  }
               }
            }

            Map childrenMap = ((CommentedConfigurationNode)recipeNode.node(new Object[]{"ingredients"})).childrenMap();
            Map map = childrenMap;

            for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
               int value7 = 0;
               CommentedConfigurationNode ingredientNode = (CommentedConfigurationNode)entry.getValue();
               ConfigurationNode configurationNode3 = ConfigHelpersKt.removeChildNode((ConfigurationNode)ingredientNode, "oraxen_item");
               if (configurationNode3 != null) {
                  String str2 = configurationNode3.getString();
                  if (str2 != null) {
                     String it_33 = str2;
                     int value8 = 0;
                     Object[] objs4 = new Object[]{"nexo_item"};
                     ((CommentedConfigurationNode)ingredientNode.node(objs4)).set(it_33);
                  }
               }
            }
         }

         OraxenConverter oraxenConverter2 = this;

         try {
            OraxenConverter oraxenConverter3 = oraxenConverter2;
            int value9 = 0;
            recipeLoader.save((ConfigurationNode)recipeYaml);
            it_40 = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable ex) {
            it_40 = ResultShim.ok(ResultKt.createFailure(ex));
         }

         Throwable throwable2 = ResultShim.exceptionOrNull(it_40);
         if (throwable2 != null) {
            Throwable it_32 = throwable2;
            int value10 = 0;
            Logs.logWarn("Failed to save " + recipeFile.getName() + " changes...", false);
            if (Settings.DEBUG.toBool()) {
               Logs.logWarn(ExceptionsKt.stackTraceToString(it_32), false);
            }
         }

      }
      } catch (Exception ex2) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex2);
      }
   }

   public final void processItemConfigs(@NotNull File itemFile) {
      if (Intrinsics.areEqual(FilesKt.getExtension(itemFile), "yml")) {
         YamlConfigurationLoader itemLoader = ((YamlConfigurationLoader.Builder)YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).indent(2).file(itemFile)).build();
         OraxenConverter it = this;

         Object obj;
         try {
            OraxenConverter oraxenConverter = it;
            int value = 0;
            obj = ResultShim.ok((CommentedConfigurationNode)itemLoader.load());
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         Object it_30 = obj;
         Throwable throwable = ResultShim.exceptionOrNull(obj);
         if (throwable != null) {
            Throwable it_29 = throwable;
            int value2 = 0;
            Logs.logError("Failed to load " + itemFile.getName() + " for parsing...", false);
            if (Settings.DEBUG.toBool()) {
               Logs.logError(ExceptionsKt.stackTraceToString(it_29), false);
            }
         }

         CommentedConfigurationNode commentedConfigurationNode = (CommentedConfigurationNode)(ResultShim.isFailure(it_30) ? null : it_30);
         if (commentedConfigurationNode != null) {
            CommentedConfigurationNode itemYaml = commentedConfigurationNode;
            Map childrenMap = itemYaml.childrenMap();
            Map map = childrenMap;
            Map destination = (Map)(new LinkedHashMap(MapsKt.mapCapacity(map.size())));
            Iterable entrySet = (Iterable)map.entrySet();
            Map map2 = destination;

            for(Object element : entrySet) {
               Map.Entry it_28 = (Map.Entry)element;
               int value3 = 0;
               String str = it_28.getKey().toString();
               Map.Entry entry = (Map.Entry)element;
               String it_27 = str;
               int value4 = 0;
               Object obj2 = entry.getValue();
               map2.put(it_27, obj2);
            }


            for(Map.Entry entry2 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
               int value5 = 0;
               String itemId = (String)entry2.getKey();
               CommentedConfigurationNode itemNode = (CommentedConfigurationNode)entry2.getValue();
               if (!itemNode.empty()) {
                  OraxenConverter oraxenConverter2 = INSTANCE;

                  Object obj3;
                  try {
                     int it_26 = 0;
                     Iterable iterable = null;
                     ((CommentedConfigurationNode)itemNode.node(new Object[]{"Pack"})).removeChild("generate_model");
                     ConfigurationNode configurationNode = null;
                     ConfigurationNode configurationNode2 = ConfigHelpersKt.renameNode((ConfigurationNode)itemNode.node(new Object[]{"displayname"}), "itemname");
                     if (configurationNode2 != null) {
                        configurationNode = configurationNode2;
                        int value6 = 0;
                        AdventureUtils adventureUtils = AdventureUtils.INSTANCE;
                        String string = configurationNode.getString();
                        if (string == null) {
                           string = "";
                        }

                        if (adventureUtils.containsLegacyCodes(string)) {
                           block: {
                              string = configurationNode.getString();
                              if (string != null) {
                                 Component component = AdventureUtilsKt.deserialize(string);
                                 if (component != null) {
                                    string = AdventureUtilsKt.serialize(component);
                                    break block;
                                 }
                              }

                              string = null;
                           }

                           configurationNode.set(string);
                        }
                     }

                     ConfigHelpersKt.renameNode((ConfigurationNode)itemNode.node(new Object[]{"injectID"}), "injectId");
                     Object[] objs = new Object[]{"trim_pattern"};
                     CommentedConfigurationNode commentedConfigurationNode2 = (CommentedConfigurationNode)itemNode.node(objs);
                     if (commentedConfigurationNode2 != null) {
                        CommentedConfigurationNode it_25 = commentedConfigurationNode2;
                        int value7 = 0;
                        String str2 = it_25.getString();
                        commentedConfigurationNode2 = (CommentedConfigurationNode)it_25.set(str2 != null ? StringsKt.replace(str2, "oraxen", "nexo", false) : null);
                     }

                     commentedConfigurationNode2 = (CommentedConfigurationNode)itemNode.node(new Object[]{"Components", "equippable", "model"});
                     if (commentedConfigurationNode2 != null) {
                        CommentedConfigurationNode commentedConfigurationNode3 = commentedConfigurationNode2;
                        int value8 = 0;
                        String str3 = commentedConfigurationNode3.getString();
                        commentedConfigurationNode3.set(str3 != null ? StringsKt.replace(str3, "oraxen", "nexo", false) : null);
                        ConfigHelpersKt.renameNode((ConfigurationNode)commentedConfigurationNode3, "asset_id");
                     }

                     List childrenList = ((CommentedConfigurationNode)itemNode.node(new Object[]{"AttributeModifiers"})).childrenList();
                     iterable = (Iterable)childrenList;

                     for(Object obj4 : iterable) {
                        CommentedConfigurationNode attributeNode = (CommentedConfigurationNode)obj4;
                        int value9 = 0;
                        attributeNode.removeChild("name");
                        attributeNode.removeChild("key");
                        attributeNode.removeChild("uuid");
                     }

                     CommentedConfigurationNode mechanicsNode = null;
                     CommentedConfigurationNode componentNode = (CommentedConfigurationNode)itemNode.node(new Object[]{"Components"});
                     CharSequence it_21 = null;
                     mechanicsNode = (CommentedConfigurationNode)itemNode.node(new Object[]{"Mechanics"});
                     if (customArmorType == CustomArmorType.TRIMS) {
                        it_21 = (CharSequence)itemId;
                        if (CustomArmorType.Companion.getItemIdRegex().matches(it_21)) {
                           ScopedConfigurationNode scopedConfigurationNode = itemNode.node(new Object[]{"material"});
                           CommentedConfigurationNode it_19 = (CommentedConfigurationNode)scopedConfigurationNode;
                           int value10 = 0;
                           String str4 = it_19.getString("");
                           CommentedConfigurationNode commentedConfigurationNode4 = (CommentedConfigurationNode)(StringsKt.contains((CharSequence)str4, (CharSequence)"_", false) ? scopedConfigurationNode : null);
                           if (commentedConfigurationNode4 != null) {
                              it_19 = commentedConfigurationNode4;
                              value10 = 0;
                              String str5 = it_19.getString();
                              str5 = str5 != null ? StringsKt.substringAfter(str5, "_", str5) : null;
                              commentedConfigurationNode4 = (CommentedConfigurationNode)it_19.set("CHAINMAIL_" + str5);
                           }
                        }
                     }

                     Object[] objs2 = new Object[]{"food"};
                     ConfigurationNode configurationNode3 = (ConfigurationNode)mechanicsNode.node(objs2);
                     Object[] objs3 = new Object[]{"food"};
                     ScopedConfigurationNode scopedConfigurationNode2 = componentNode.node(objs3);
                     configurationNode3 = ConfigHelpersKt.renameNode(configurationNode3, (ConfigurationNode)scopedConfigurationNode2);
                     if (configurationNode3 != null) {
                        configurationNode3 = (ConfigurationNode)ConfigHelpersKt.onExists(configurationNode3, OraxenConverter::processItemConfigs$lambda$3$0$6);
                     }

                     configurationNode3 = ConfigHelpersKt.removeChildNode((ConfigurationNode)mechanicsNode, "durability");
                     if (configurationNode3 != null) {
                        configurationNode3 = configurationNode3.node(new Object[]{"value"});
                        if (configurationNode3 != null) {
                           Object[] objs4 = new Object[]{"durability"};
                           scopedConfigurationNode2 = componentNode.node(objs4);
                           ConfigHelpersKt.renameNode(configurationNode3, (ConfigurationNode)scopedConfigurationNode2);
                        }
                     }

                     Iterable iterable2 = (Iterable)ConfigHelpersKt.removeChildNode((ConfigurationNode)mechanicsNode, "music_disc").childrenMap().values();

                     for(Object obj5 : iterable2) {
                        ConfigurationNode musicdisc = (ConfigurationNode)obj5;
                        int value11 = 0;
                        Object[] objs5 = new Object[]{"jukebox_playable", "song_key"};
                        CommentedConfigurationNode commentedConfigurationNode5 = (CommentedConfigurationNode)componentNode.node(objs5);
                        commentedConfigurationNode5.set(musicdisc.node(new Object[]{"song"}).getString());
                        ((CommentedConfigurationNode)componentNode.node(new Object[]{"jukebox_playable", "show_in_tooltip"})).set(true);
                     }

                     CommentedConfigurationNode commentedConfigurationNode6 = null;
                     CommentedConfigurationNode it_14 = (CommentedConfigurationNode)mechanicsNode.node(new Object[]{"noteblock"});
                     if (it_14 != null) {
                        commentedConfigurationNode6 = (CommentedConfigurationNode)ConfigHelpersKt.ifExists(it_14, OraxenConverter::processItemConfigs$lambda$3$0$8);
                        if (commentedConfigurationNode6 != null) {
                           ConfigHelpersKt.renameNode((ConfigurationNode)commentedConfigurationNode6, "custom_block");
                        }
                     }

                     CommentedConfigurationNode commentedConfigurationNode7 = null;
                     it_14 = (CommentedConfigurationNode)mechanicsNode.node(new Object[]{"stringblock"});
                     if (it_14 != null) {
                        commentedConfigurationNode7 = (CommentedConfigurationNode)ConfigHelpersKt.ifExists(it_14, OraxenConverter::processItemConfigs$lambda$3$0$9);
                        if (commentedConfigurationNode7 != null) {
                           ConfigHelpersKt.renameNode((ConfigurationNode)commentedConfigurationNode7, "custom_block");
                        }
                     }

                     Object[] it_13 = new Object[]{"furniture"};
                     ConfigHelpersKt.ifExists((ConfigurationNode)mechanicsNode.node(it_13), (a0_ -> OraxenConverter.processItemConfigs$lambda$3$0$10(itemNode, itemId, (CommentedConfigurationNode)a0_)));
                     if (mechanicsNode.empty()) {
                        itemNode.removeChild("Mechanics");
                     }

                     if (componentNode.empty()) {
                        itemNode.removeChild("Components");
                     }

                     obj3 = ResultShim.ok(Unit.INSTANCE);
                  } catch (Throwable ex) {
                     obj3 = ResultShim.ok(ResultKt.createFailure(ex));
                  }

                  Throwable throwable2 = ResultShim.exceptionOrNull(obj3);
                  if (throwable2 != null) {
                     Throwable it_12 = throwable2;
                     int value12 = 0;
                     Logs.logError("Failed to convert " + itemId + ": " + it_12.getMessage(), false);
                     if (Settings.DEBUG.toBool()) {
                        Logs.logError(ExceptionsKt.stackTraceToString(it_12), false);
                     }
                  }
               }
            }

            OraxenConverter oraxenConverter3 = this;

            try {
               OraxenConverter oraxenConverter4 = oraxenConverter3;
               int value13 = 0;
               itemLoader.save((ConfigurationNode)itemYaml);
               it_30 = ResultShim.ok(Unit.INSTANCE);
            } catch (Throwable ex2) {
               it_30 = ResultShim.ok(ResultKt.createFailure(ex2));
            }

            Throwable throwable3 = ResultShim.exceptionOrNull(it_30);
            if (throwable3 != null) {
               Throwable it_11 = throwable3;
               int value14 = 0;
               Logs.logWarn("Failed to save " + itemFile.getName() + " changes...", false);
               if (Settings.DEBUG.toBool()) {
                  Logs.logWarn(ExceptionsKt.stackTraceToString(it_11), false);
               }
            }

         }
      }
   }

   public final void processPackFolder(@NotNull File packFolder) {
      OraxenConverter oraxenConverter = this;

      Object obj;
      try {
         OraxenConverter oraxenConverter2 = oraxenConverter;
         int value = 0;
         if (!packFolder.exists() || !packFolder.isDirectory()) {
            return;
         }

         String[] strs = new String[]{"external_packs", "Oraxen", "assets"};
         File file2 = FileUtilsKt.resolve(packFolder, strs);
         strs = new String[]{"minecraft"};
         File namespaceFolder = FileUtilsKt.resolve(file2, strs);
         String[] strs2 = new String[]{"textures", "required"};
         Sequence sequence = (Sequence)FilesKt.walkBottomUp(FileUtilsKt.resolve(packFolder, strs2));
         Iterator it = sequence.iterator();

         Object obj2;
         while(true) {
            if (!it.hasNext()) {
               obj2 = null;
               break;
            }

            Object element = it.next();
            File it_9 = (File)element;
            int value2 = 0;
            if (Intrinsics.areEqual(it_9.getName(), "menu_items.png")) {
               obj2 = element;
               break;
            }
         }

         file2 = (File)obj2;
         if (file2 != null) {
            file2.delete();
         }

         strs = new String[]{"textures", "models", "armor"};
         Sequence sequence2 = (Sequence)FilesKt.walkBottomUp(FileUtilsKt.resolve(packFolder, strs));
         int value3 = 0;

         for(Object obj3 : kotlin.sequences.SequencesKt.asIterable(sequence2)) {
            File file = (File)obj3;
            int value4 = 0;
            String name = file.getName();
            if (!StringsKt.startsWith(name, "chainmail_", false)) {
               file.delete();
            }
         }

         String[] strs3 = new String[]{"models", "textures", "sounds", "font", "lang"};
         Iterable iterable = (Iterable)SetsKt.setOf(strs3);
         value3 = 0;
         Object2ObjectOpenHashMap result = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10), 16));
         Map destination = (Map)result;

         for(Object obj4 : iterable) {
            String it_8 = (String)obj4;
            int value5 = 0;
            String[] strs4 = new String[]{it_8};
            File file3 = FileUtilsKt.resolve(packFolder, strs4);
            destination.put(obj4, file3);
         }

         Map map = (Map)((Object2ObjectOpenHashMap)destination);
         value3 = 0;

         for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
            String[] strs5 = new String[1];
            Object key = entry.getKey();
            strs5[0] = (String)key;
            File dest = FileUtilsKt.resolve(namespaceFolder, strs5);
            Object obj5 = entry.getValue();
            FilesKt.copyRecursively((File)obj5, dest, false, OraxenConverter::processPackFolder$lambda$0$3$0);
            obj5 = entry.getValue();
            FilesKt.deleteRecursively((File)obj5);
         }

         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj);
      if (throwable != null) {
         Throwable it = throwable;
         int value6 = 0;
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         } else {
            String message = it.getMessage();
            Logs.logWarn(message, false);
         }
      }

   }

   private static final OnErrorAction convert$lambda$5(File file, IOException ioException) {
      File oraxenFile = FilesKt.resolve(oraxenFolder, FilesKt.relativeTo(file, nexoFolder));
      String path = oraxenFile.getPath();
      if (!StringsKt.startsWith(path, "plugins\\Oraxen\\pack", false)) {
         Logs.logWarn("Skipping copying of <gold>" + oraxenFile.getPath() + "</gold> as it already exists...", false);
      }

      return OnErrorAction.SKIP;
   }

   private static final ConfigurationNode processItemConfigs$lambda$3$0$6(ConfigurationNode foodNode) {
      Object[] objs = new Object[]{"hunger"};
      return ConfigHelpersKt.renameNode(foodNode.node(objs), "nutrition");
   }

   private static final CommentedConfigurationNode processItemConfigs$lambda$3$0$8(CommentedConfigurationNode noteNode) {
      try {
      Iterable iterable = null;
      ((CommentedConfigurationNode)noteNode.node(new Object[]{"type"})).set("NOTEBLOCK");
      Object[] objs = new Object[]{"drop"};
      List list = ConfigHelpersKt.removeChildNode((ConfigurationNode)noteNode.node(objs), "best_tools").getList(TypeToken.get(String.class));
      if (list != null) {
         String str = (String)CollectionsKt.firstOrNull(list);
         if (str != null) {
            String it = str;
            int value = 0;
            Object[] objs2 = new Object[]{"drop", "best_tool"};
            CommentedConfigurationNode commentedConfigurationNode = (CommentedConfigurationNode)((CommentedConfigurationNode)noteNode.node(objs2)).set(it);
         }
      }

      list = ((CommentedConfigurationNode)noteNode.node(new Object[]{"drop", "loots"})).childrenList();
      iterable = (Iterable)list;
      int value2 = 0;

      for(Object element : iterable) {
         CommentedConfigurationNode lootNode = (CommentedConfigurationNode)element;
         int value3 = 0;
         String string = ConfigHelpersKt.removeChildNode((ConfigurationNode)lootNode, "oraxen_item").getString();
         if (string != null) {
            String it = string;
            int value4 = 0;
            Object[] objs3 = new Object[]{"nexo_item"};
            ((CommentedConfigurationNode)lootNode.node(objs3)).set(it);
         }
      }

      ScopedConfigurationNode scopedConfigurationNode = noteNode.node(new Object[]{"custom_variation"});
      CommentedConfigurationNode cv = (CommentedConfigurationNode)scopedConfigurationNode;
      int value5 = 0;
      NoteBlock legacyData = NoteMechanicHelpers.INSTANCE.legacyBlockData(cv.getInt());
      if (legacyData != null) {
         NoteMechanicHelpers noteMechanicHelpers = NoteMechanicHelpers.INSTANCE;
         int value6 = 0;
         Integer number = noteMechanicHelpers.modernCustomVariation(legacyData);
         if (number != null) {
            int value7 = number + 1;
            cv.set(value7);
         }
      }

      return (CommentedConfigurationNode)scopedConfigurationNode;
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final ConfigurationNode processItemConfigs$lambda$3$0$9$2(CommentedConfigurationNode sapling) {
      Object[] objs = new Object[]{"canGrowNaturally"};
      ConfigHelpersKt.renameNode((ConfigurationNode)sapling.node(objs), "grows_naturally");
      ConfigHelpersKt.renameNode((ConfigurationNode)sapling.node(new Object[]{"naturalGrowthTime"}), "natural_growth_time");
      ConfigHelpersKt.renameNode((ConfigurationNode)sapling.node(new Object[]{"canGrowFromBoneMeal"}), "grows_from_bonemeal");
      ConfigHelpersKt.renameNode((ConfigurationNode)sapling.node(new Object[]{"boneMealGrowthSpeedup"}), "bonemeal_growth_speedup");
      ConfigHelpersKt.renameNode((ConfigurationNode)sapling.node(new Object[]{"growSound"}), "grow_sound");
      ConfigHelpersKt.renameNode((ConfigurationNode)sapling.node(new Object[]{"minLightLevel"}), "min_light_level");
      ConfigHelpersKt.renameNode((ConfigurationNode)sapling.node(new Object[]{"requiresWaterSource"}), "requires_water_source");
      ConfigHelpersKt.renameNode((ConfigurationNode)sapling.node(new Object[]{"schematicName"}), "schematic");
      ConfigHelpersKt.renameNode((ConfigurationNode)sapling.node(new Object[]{"shouldCopyBiomes"}), "copy_biomes");
      ConfigHelpersKt.renameNode((ConfigurationNode)sapling.node(new Object[]{"shouldCopyEntities"}), "copy_entities");
      return ConfigHelpersKt.renameNode((ConfigurationNode)sapling.node(new Object[]{"shouldReplaceBlocks"}), "replace_blocks");
   }

   private static final CommentedConfigurationNode processItemConfigs$lambda$3$0$9(CommentedConfigurationNode stringNode) {
      try {
      Iterable iterable = null;
      ((CommentedConfigurationNode)stringNode.node(new Object[]{"type"})).set("STRINGBLOCK");
      Object[] objs = new Object[]{"drop"};
      List list = ConfigHelpersKt.removeChildNode((ConfigurationNode)stringNode.node(objs), "best_tools").getList(TypeToken.get(String.class));
      if (list != null) {
         String str = (String)CollectionsKt.firstOrNull(list);
         if (str != null) {
            String it = str;
            int value = 0;
            Object[] objs2 = new Object[]{"drop", "best_tool"};
            CommentedConfigurationNode commentedConfigurationNode = (CommentedConfigurationNode)((CommentedConfigurationNode)stringNode.node(objs2)).set(it);
         }
      }

      list = ((CommentedConfigurationNode)stringNode.node(new Object[]{"drop", "loots"})).childrenList();
      iterable = (Iterable)list;
      int value2 = 0;

      for(Object element : iterable) {
         CommentedConfigurationNode lootNode = (CommentedConfigurationNode)element;
         int value3 = 0;
         String string = ConfigHelpersKt.removeChildNode((ConfigurationNode)lootNode, "oraxen_item").getString();
         if (string != null) {
            String it = string;
            int value4 = 0;
            Object[] objs3 = new Object[]{"nexo_item"};
            ((CommentedConfigurationNode)lootNode.node(objs3)).set(it);
         }
      }

      Tripwire legacyBlockData = null;
      ConfigHelpersKt.onExists(stringNode.node(new Object[]{"sapling"}), OraxenConverter::processItemConfigs$lambda$3$0$9$2);
      StringMechanicHelpers stringMechanicHelpers = StringMechanicHelpers.INSTANCE;
      Object[] objs4 = new Object[]{"custom_variation"};
      legacyBlockData = stringMechanicHelpers.legacyBlockData(((CommentedConfigurationNode)stringNode.node(objs4)).getInt());
      int modernVariation = StringMechanicHelpers.INSTANCE.modernCustomVariation(legacyBlockData);
      Object[] objs5 = new Object[]{"custom_variation"};
      return (CommentedConfigurationNode)((CommentedConfigurationNode)stringNode.node(objs5)).set(modernVariation);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final CharSequence processItemConfigs$lambda$3$0$10$3$0$0(ConfigurationNode scaleNode, boolean isFixed, String it) {
      ConfigurationNode configurationNode = (ConfigurationNode)scaleNode.childrenMap().get(it);
      CharSequence charSequence;
      if (configurationNode != null) {
         String str = String.valueOf(configurationNode.getDouble(isFixed ? (double)0.5F : (double)1.0F));
         if (str != null) {
            str = StringUtilsKt.remove(str, ".0");
            if (str != null) {
               charSequence = (CharSequence)str;
               return charSequence;
            }
         }
      }

      charSequence = isFixed ? (CharSequence)"0.5" : (CharSequence)"1";
      return charSequence;
   }

   private static final ConfigurationNode processItemConfigs$lambda$3$0$10$3(ConfigurationNode propertiesNode) {
      try {
      Object[] objs = new Object[]{"displayWidth"};
      ConfigHelpersKt.renameNode(propertiesNode.node(objs), "display_width");
      ConfigHelpersKt.renameNode(propertiesNode.node(new Object[]{"displayHeight"}), "display_height");
      ConfigurationNode p0 = propertiesNode.node(new Object[]{"scale"});
      int value = 0;
      ConfigurationNode configurationNode = p0.virtual() ? p0 : null;
      if (configurationNode != null) {
         ConfigurationNode scaleNode = configurationNode;
         value = 0;
         String scaleString = null;
         boolean isFixed = Intrinsics.areEqual(propertiesNode.node(new Object[]{"display_transform"}).getString(), "FIXED");
         String[] strs = new String[]{"x", "y", "z"};
         scaleString = CollectionsKt.joinToString((Iterable)CollectionsKt.listOf(strs), (CharSequence)",", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (a0_ -> OraxenConverter.processItemConfigs$lambda$3$0$10$3$0$0(scaleNode, isFixed, (String)a0_)));
         configurationNode = scaleNode.set(scaleString);
      } else {
         configurationNode = null;
      }

      return configurationNode;
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final ConfigurationNode processItemConfigs$lambda$3$0$10$4(CommentedConfigurationNode evoNode) {
      Object[] objs = new Object[]{"rain_boost", "boost_tick"};
      ConfigurationNode configurationNode = (ConfigurationNode)evoNode.node(objs);
      Object[] objs2 = new Object[]{"rain_boost_tick"};
      ScopedConfigurationNode scopedConfigurationNode = evoNode.node(objs2);
      ConfigHelpersKt.renameNode(configurationNode, (ConfigurationNode)scopedConfigurationNode);
      configurationNode = (ConfigurationNode)evoNode.node(new Object[]{"bone_meal", "chance"});
      scopedConfigurationNode = evoNode.node(new Object[]{"bone_meal_chance"});
      return ConfigHelpersKt.renameNode(configurationNode, (ConfigurationNode)scopedConfigurationNode);
   }

   private static final CharSequence processItemConfigs$lambda$3$0$10$5$0$0(ConfigurationNode barrierNode, String it) {
      ConfigurationNode configurationNode = (ConfigurationNode)barrierNode.childrenMap().get(it);
      CharSequence charSequence;
      if (configurationNode != null) {
         String str = String.valueOf(configurationNode.getInt(0));
         if (str != null) {
            charSequence = (CharSequence)str;
            return charSequence;
         }
      }

      charSequence = (CharSequence)"0";
      return charSequence;
   }

   private static final Unit processItemConfigs$lambda$3$0$10$5(Set barriers, ConfigurationNode barriersNode) {
      List childrenList = barriersNode.childrenList();
      Iterable iterable = (Iterable)childrenList;
      int value = 0;

      for(Object element : iterable) {
         ConfigurationNode barrierNode = (ConfigurationNode)element;
         int value2 = 0;
         Collection collection = (Collection)barriers;
         String str;
         if (Intrinsics.areEqual(barrierNode.getString(), "origin")) {
            str = "0,0,0";
         } else {
            if (!barrierNode.isMap()) {
               continue;
            }

            String[] strs = new String[]{"x", "y", "z"};
            str = CollectionsKt.joinToString((Iterable)CollectionsKt.listOf(strs), (CharSequence)",", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (a0_ -> OraxenConverter.processItemConfigs$lambda$3$0$10$5$0$0(barrierNode, (String)a0_)));
         }

         String str2 = str;
         collection.add(str2);
      }

      return Unit.INSTANCE;
   }

   private static final boolean processItemConfigs$lambda$3$0$10$7(CommentedConfigurationNode it) {
      Collection barriers = (Collection)((CommentedConfigurationNode)it.node(new Object[]{"barriers"})).childrenList();
      if (barriers == null || barriers.isEmpty()) {
         Collection interactions = (Collection)((CommentedConfigurationNode)it.node(new Object[]{"interactions"})).childrenList();
         if (interactions == null || interactions.isEmpty()) {
            return true;
         }
      }

      return false;
   }

   private static final CommentedConfigurationNode processItemConfigs$lambda$3$0$10$11(CommentedConfigurationNode furnitureNode, Set barriers, ConfigurationNode lightNode) {
      try {
      Iterable iterable = null;
      CommentedConfigurationNode commentedConfigurationNode = (CommentedConfigurationNode)furnitureNode.node(new Object[]{"lights"});
      iterable = (Iterable)barriers;
      Class clazz = String.class;
      CommentedConfigurationNode commentedConfigurationNode2 = commentedConfigurationNode;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         String it = (String)item;
         int value = 0;
         destination.add(it + " " + lightNode.raw());
      }

      List list = (List)destination;
      return (CommentedConfigurationNode)commentedConfigurationNode2.setList(clazz, list);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final CommentedConfigurationNode processItemConfigs$lambda$3$0$10$13(Set barriers, CommentedConfigurationNode furnitureNode, ConfigurationNode seatNode) {
      try {
      Double number = StringsKt.toDoubleOrNull(String.valueOf(seatNode.raw()));
      double seatOffset = (number != null ? number : (double)0.0F) - 0.6;
      Iterable iterable = (Iterable)barriers;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         String it = (String)item;
         int value = 0;
         FurnitureSeat furnitureSeat = new FurnitureSeat(it);
         int value2 = 0;
         Vector offset = furnitureSeat.getOffset();
         offset.setY(offset.getY() - seatOffset);
         destination.add(furnitureSeat.getOffset().toString());
      }

      List seats = (List)destination;
      Object[] objs = new Object[]{"seats"};
      return (CommentedConfigurationNode)((CommentedConfigurationNode)furnitureNode.node(objs)).setList(String.class, seats);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final CommentedConfigurationNode processItemConfigs$lambda$3$0$10(CommentedConfigurationNode itemNode, String itemId, CommentedConfigurationNode furnitureNode) {
      try {
      boolean flag;
      boolean flag2;
      String string = ConfigHelpersKt.removeChildNode((ConfigurationNode)furnitureNode, "type").getString();
      if (string != null) {
         String it = string;
         int value = 0;
         if ((!Intrinsics.areEqual(it, "DISPLAY_ENTITY") ? it : null) != null) {
            value = 0;
            Logs.logWarn("    Furniture <gold>" + itemId + "</gold> is using ITEM_FRAME type furniture", false);
            Logs.logWarn("    This might cause issues due to Nexo only supporting DISPLAY_ENTITY", false);
            Logs.logError(" Be sure to back-up your world folders and run the world-converter", false);
            Object[] objs = new Object[]{"properties", "display_transform"};
            ((CommentedConfigurationNode)furnitureNode.node(objs)).set("FIXED");
         }
      }

      Object[] objs2 = new Object[]{"drop", "loots"};
      List childrenList = ((CommentedConfigurationNode)furnitureNode.node(objs2)).childrenList();
      Iterable iterable = (Iterable)childrenList;
      int value2 = 0;

      for(Object element : iterable) {
         CommentedConfigurationNode lootNode = (CommentedConfigurationNode)element;
         int it = 0;
         String str = ConfigHelpersKt.removeChildNode((ConfigurationNode)lootNode, "oraxen_item").getString();
         if (str != null) {
            String it_7 = str;
            int value3 = 0;
            Object[] objs3 = new Object[]{"nexo_item"};
            ((CommentedConfigurationNode)lootNode.node(objs3)).set(it_7);
         }
      }

      ConfigurationNode configurationNode = ConfigHelpersKt.renameNode((ConfigurationNode)furnitureNode.node(new Object[]{"display_entity_properties"}), "properties");
      if (configurationNode != null) {
         configurationNode = (ConfigurationNode)ConfigHelpersKt.onExists(configurationNode, OraxenConverter::processItemConfigs$lambda$3$0$10$3);
      }

      CommentedConfigurationNode hitboxNode = null;
      ConfigHelpersKt.renameNode((ConfigurationNode)furnitureNode.node(new Object[]{"farmblock_required"}), "farmland_required");
      ConfigHelpersKt.onExists(furnitureNode.node(new Object[]{"evolution"}), OraxenConverter::processItemConfigs$lambda$3$0$10$4);
      hitboxNode = (CommentedConfigurationNode)furnitureNode.node(new Object[]{"hitbox"});
      if (((CommentedConfigurationNode)hitboxNode.node(new Object[]{"interactions"})).virtual()) {
         ConfigHelpersKt.renameNode((ConfigurationNode)hitboxNode.node(new Object[]{"interactionHitboxes"}), "interactions");
      } else {
         Boolean flag3 = hitboxNode.removeChild("interactionHitboxes");
      }

      if (((CommentedConfigurationNode)hitboxNode.node(new Object[]{"barriers"})).virtual()) {
         ConfigHelpersKt.renameNode((ConfigurationNode)hitboxNode.node(new Object[]{"barrierHitboxes"}), "barriers");
      } else {
         Boolean flag4 = hitboxNode.removeChild("barrierHitboxes");
      }

      Set barriers = (Set)(new LinkedHashSet());
      if (ConfigHelpersKt.removeChildNode((ConfigurationNode)furnitureNode, "barrier").getBoolean()) {
         ((Collection)barriers).add("0,0,0");
      }

      ConfigHelpersKt.onExists(ConfigHelpersKt.removeChildNode((ConfigurationNode)furnitureNode, "barriers"), (a0_ -> OraxenConverter.processItemConfigs$lambda$3$0$10$5(barriers, (ConfigurationNode)a0_)));
      if (!((Collection)barriers).isEmpty()) {
         Object[] it = new Object[]{"hitbox", "barriers"};
         ((CommentedConfigurationNode)furnitureNode.node(it)).set(barriers);
      }

      block: {
         CommentedConfigurationNode width = null;
         String it = ((CommentedConfigurationNode)itemNode.node(new Object[]{"template"})).getString();
         if (it != null) {
            int value4 = 0;
            CommentedConfigurationNode parent = (CommentedConfigurationNode)itemNode.parent();
            if (parent != null) {
               Object[] objs4 = new Object[]{it};
               parent = (CommentedConfigurationNode)parent.node(objs4);
            } else {
               parent = null;
            }

            width = parent;
            if (width != null) {
               Boolean it_6 = null;
               CommentedConfigurationNode commentedConfigurationNode = (CommentedConfigurationNode)width.node(new Object[]{"Mechanics", "furniture", "hitbox"});
               if (commentedConfigurationNode != null) {
                  it_6 = (Boolean)ConfigHelpersKt.onExists(commentedConfigurationNode, OraxenConverter::processItemConfigs$lambda$3$0$10$7);
                  if (it_6 != null) {
                     flag2 = !it_6;
                     break block;
                  }
               }
            }
         }

         flag2 = false;
      }

      boolean templateHasHitbox = flag2;
      Object[] objs5 = new Object[]{"hitbox"};
      CommentedConfigurationNode it = (CommentedConfigurationNode)furnitureNode.node(objs5);
      int height = 0;
      Pair pair = TuplesKt.to(ConfigHelpersKt.removeChildNode((ConfigurationNode)it, "width").getDouble((double)1.0F), ConfigHelpersKt.removeChildNode((ConfigurationNode)it, "height").getDouble((double)1.0F));
      double width = ((Number)pair.component1()).doubleValue();
      double height_5 = ((Number)pair.component2()).doubleValue();
      if (!templateHasHitbox) {
         block: {
            if (width == (double)1.0F && height_5 == (double)1.0F) {
               if (!barriers.isEmpty()) {
                  break block;
               }

               Object[] objs6 = new Object[]{"hitbox", "shulkers"};
               if (!((CommentedConfigurationNode)furnitureNode.node(objs6)).childrenList().isEmpty()) {
                  break block;
               }
            }

            ScopedConfigurationNode scopedConfigurationNode;
            block2: {
               Object[] objs7 = new Object[]{"interactions"};
               scopedConfigurationNode = hitboxNode.node(objs7);
               CommentedConfigurationNode it_4 = (CommentedConfigurationNode)scopedConfigurationNode;
               int value5 = 0;
               if (it_4.virtual()) {
                  Object[] objs8 = new Object[]{"barriers"};
                  if (((CommentedConfigurationNode)hitboxNode.node(objs8)).virtual()) {
                     flag = true;
                     break block2;
                  }
               }

               flag = false;
            }

            CommentedConfigurationNode commentedConfigurationNode2 = (CommentedConfigurationNode)(flag ? scopedConfigurationNode : null);
            if (commentedConfigurationNode2 != null) {
               CommentedConfigurationNode commentedConfigurationNode3 = (CommentedConfigurationNode)commentedConfigurationNode2.set(CollectionsKt.listOf("0,0,0 " + width + "," + height_5));
            }
         }
      }

      configurationNode = ConfigHelpersKt.removeChildNode((ConfigurationNode)furnitureNode, "light");
      if (configurationNode != null) {
         ConfigurationNode it_3 = configurationNode;
         int value6 = 0;
         configurationNode = !barriers.isEmpty() && it_3.getInt() != 0 ? it_3 : null;
         if (configurationNode != null) {
            ConfigHelpersKt.ifExists(configurationNode, (a0_ -> OraxenConverter.processItemConfigs$lambda$3$0$10$11(furnitureNode, barriers, (ConfigurationNode)a0_)));
         }
      }

      configurationNode = ConfigHelpersKt.removeChildNode((ConfigurationNode)furnitureNode, "seat");
      Object[] objs9 = new Object[]{"height"};
      configurationNode = configurationNode.node(objs9);
      if (configurationNode != null) {
         ConfigurationNode it_2 = configurationNode;
         int value7 = 0;
         configurationNode = !barriers.isEmpty() ? it_2 : null;
         if (configurationNode != null) {
            ConfigHelpersKt.ifExists(configurationNode, (a0_ -> OraxenConverter.processItemConfigs$lambda$3$0$10$13(barriers, furnitureNode, (ConfigurationNode)a0_)));
         }
      }

      List list = ConfigHelpersKt.removeChildNode((ConfigurationNode)furnitureNode.node(new Object[]{"drop"}), "best_tools").getList(TypeToken.get(String.class));
      CommentedConfigurationNode commentedConfigurationNode4;
      if (list != null) {
         String str2 = (String)CollectionsKt.firstOrNull(list);
         if (str2 != null) {
            String it_1 = str2;
            int value8 = 0;
            Object[] objs10 = new Object[]{"drop", "best_tool"};
            commentedConfigurationNode4 = (CommentedConfigurationNode)((CommentedConfigurationNode)furnitureNode.node(objs10)).set(it_1);
            return commentedConfigurationNode4;
         }
      }

      commentedConfigurationNode4 = null;
      return commentedConfigurationNode4;
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final OnErrorAction processPackFolder$lambda$0$3$0(File file, IOException ioException) {
      return OnErrorAction.SKIP;
   }

   static {
      File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
      nexoFolder = dataFolder;
      dataFolder = nexoFolder;
      String[] strs = new String[]{"converter", "Oraxen"};
      oraxenFolder = FileUtilsKt.resolve(dataFolder, strs);
      customArmorType = CustomArmorType.COMPONENT;
   }
}
