package com.nexomc.nexo.converter;
import com.nexomc.nexo.converter.ConfigHelpersKt;
import com.nexomc.nexo.converter.Converter;
import java.io.FileFilter;
import java.lang.invoke.LambdaMetafactory;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteMechanicHelpers;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringMechanicHelpers;
import com.nexomc.nexo.pack.VanillaResourcePack;
import com.nexomc.nexo.pack.creative.NexoPackReader;
import com.nexomc.nexo.recipes.RecipeType;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.FileUtils;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import com.nexomc.nexo.utils.wrappers.AttributeWrapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.ScopedConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.texture.Texture;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0016\u001a\u00020\u0017J\f\u0010\u001a\u001a\u00020\u000e*\u00020\u000eH\u0002J\u0014\u0010\u001b\u001a\u00020\u0017*\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001cH\u0002J\u001c\u0010\u001e\u001a\u00020\u0017*\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\u001c2\u0006\u0010 \u001a\u00020!H\u0002J\u001c\u0010\"\u001a\u00020\u0017*\u00020\u001c2\u0006\u0010#\u001a\u00020\u001c2\u0006\u0010$\u001a\u00020\u001cH\u0002R\u0018\u0010\u0004\u001a\t\u0018\u00010\u0005¢\u0006\u0002\b\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R!\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u000f\u0010\u0010R!\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000e0\r8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0015\u0010\u0012\u001a\u0004\b\u0014\u0010\u0010R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006%"},
   d2 = {"Lcom/nexomc/nexo/converter/ItemsAdderConverter;", "", "<init>", "()V", "ITEMSADDER_ITEM_KEY", "Lorg/bukkit/NamespacedKey;", "Lorg/jetbrains/annotations/Nullable;", "getITEMSADDER_ITEM_KEY", "()Lorg/bukkit/NamespacedKey;", "nexoFolder", "Ljava/io/File;", "iaFolder", "vanillaModels", "Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "", "getVanillaModels", "()Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "vanillaModels$delegate", "Lkotlin/Lazy;", "vanillaTextures", "getVanillaTextures", "vanillaTextures$delegate", "convert", "", "AMPERSAND_CODE_REGEX", "Lkotlin/text/Regex;", "legacyToMiniMessage", "convertBlockSounds", "Lorg/spongepowered/configurate/ConfigurationNode;", "iaSoundParent", "convertPlaceableOn", "placeableOn", "default", "", "convertAttributeModifiers", "attributeModifiersNode", "nexoItem", "com.nexomc:core"}
)
public final class ItemsAdderConverter {
   @NotNull
   public static final ItemsAdderConverter INSTANCE = new ItemsAdderConverter();
   @Nullable
   private static final NamespacedKey ITEMSADDER_ITEM_KEY = NamespacedKey.fromString("itemsadder");
   @NotNull
   private static final File nexoFolder;
   @NotNull
   private static final File iaFolder;
   @NotNull
   private static final Lazy vanillaModels$delegate;
   @NotNull
   private static final Lazy vanillaTextures$delegate;
   @NotNull
   private static final Regex AMPERSAND_CODE_REGEX;

   private ItemsAdderConverter() {
   }

   @Nullable
   public final NamespacedKey getITEMSADDER_ITEM_KEY() {
      return ITEMSADDER_ITEM_KEY;
   }

   private final ObjectOpenHashSet<String> getVanillaModels() {
      Lazy lazy = vanillaModels$delegate;
      return (ObjectOpenHashSet)lazy.getValue();
   }

   private final ObjectOpenHashSet<String> getVanillaTextures() {
      Lazy lazy = vanillaTextures$delegate;
      return (ObjectOpenHashSet)lazy.getValue();
   }

   public final void convert() {
      try {
      Converter.ItemsAdderConverter iaConverter = NexoPlugin.Companion.instance().converter().getItemsadderConverter();
      if (iaFolder.exists() && !FileUtils.INSTANCE.isEmpty(iaFolder)) {
         Logs.logInfo("Starting conversion of ItemsAdder-setup...");
         File file = iaFolder;
         String[] strs = new String[]{"contents"};
         File[] files = FileUtilsKt.resolve(file, strs).listFiles(ItemsAdderConverter::convert$lambda$0);
         List list;
         if (files != null) {
            File[] files2 = files;
            Collection destination = (Collection)(new ArrayList());
            int it = 0;

            for(int i = files.length; it < i; ++it) {
               Object element = files2[it];
               int value = 0;
               Sequence sequence = SequencesKt.filter((Sequence)FilesKt.walkBottomUp((File)element), ItemsAdderConverter::convert$lambda$1$0);
               CollectionsKt.addAll(destination, sequence);
            }

            List list2 = (List)destination;
            list = list2;
         } else {
            list = CollectionsKt.emptyList();
         }

         File file2 = iaFolder;
         String[] strs2 = new String[]{"data", "items_packs"};
         ObjectArrayList configFiles = CollectionUtilsKt.plusFast(list, (Iterable)CollectionUtilsKt.toFastList(SequencesKt.filter((Sequence)FilesKt.walkBottomUp(FileUtilsKt.resolve(file2, strs2)), ItemsAdderConverter::convert$lambda$2)));
         File file3 = iaFolder;
         strs = new String[]{"storage"};
         File storageFolder = FileUtilsKt.resolve(file3, strs);
         YamlConfigurationLoader.Builder builder = YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK);
         String[] strs3 = new String[]{"real_blocks_note_ids_cache.yml"};
         CommentedConfigurationNode noteBlockVariationCache = (CommentedConfigurationNode)((YamlConfigurationLoader.Builder)builder.file(FileUtilsKt.resolve(storageFolder, strs3))).build().load();
         builder = YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK);
         String[] glyphCharCache = new String[]{"real_wire_ids_cache.yml"};
         CommentedConfigurationNode stringBlockVariationCache = (CommentedConfigurationNode)((YamlConfigurationLoader.Builder)builder.file(FileUtilsKt.resolve(storageFolder, glyphCharCache))).build().load();
         builder = YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK);
         String[] customModelDataCache = new String[]{"font_images_unicode_cache.yml"};
         CommentedConfigurationNode glyphCharCache_97 = (CommentedConfigurationNode)((YamlConfigurationLoader.Builder)builder.file(FileUtilsKt.resolve(storageFolder, customModelDataCache))).build().load();
         builder = YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK);
         String[] nexoIaPack = new String[]{"items_ids_cache.yml"};
         CommentedConfigurationNode customModelDataCache_96 = (CommentedConfigurationNode)((YamlConfigurationLoader.Builder)builder.file(FileUtilsKt.resolve(storageFolder, nexoIaPack))).build().load();
         File file4 = nexoFolder;
         String[] strs4 = new String[]{"pack/external_packs/ItemsAdder"};
         File file5 = FileUtilsKt.resolve(file4, strs4);
         int it = 0;
         String[] it_95 = new String[]{"assets"};
         FileUtilsKt.resolve(file5, it_95).mkdirs();
         File nexoIaPack_94 = file5;
         if (iaConverter.getConvertResourcePack()) {
            file4 = iaFolder;
            String[] strs5 = new String[]{"data", "resource_pack"};
            File file6 = FileUtilsKt.resolve(file4, strs5);
            int value2 = 0;
            file6 = file6.exists() ? file6 : null;
            if (file6 != null) {
               Boolean flag = FilesKt.copyRecursively(file6, file6, true, (kotlin.jvm.functions.Function2<java.io.File,java.io.IOException,kotlin.io.OnErrorAction>)((java.io.File f_, java.io.IOException e_) -> kotlin.io.OnErrorAction.SKIP));
            } else {
               Object obj = null;
            }

            file4 = iaFolder;
            String[] strs6 = new String[]{"contents"};
            Iterable iterable = (Iterable)FileUtilsKt.listDirectories(FileUtilsKt.resolve(file4, strs6), (Function1)null);
            ObjectArrayList objectArrayList = new ObjectArrayList();

            for(Object obj2 : iterable) {
               File it_90 = (File)obj2;
               int value3 = 0;
               File[] files3 = new File[2];
               String[] strs7 = new String[]{"resourcepack"};
               files3[0] = FileUtilsKt.resolve(it_90, strs7);
               strs7 = new String[]{"resource_pack"};
               files3[1] = FileUtilsKt.resolve(it_90, strs7);

               for(Object item : (Iterable)CollectionsKt.listOf(files3)) {
                  if (item != null) {
                     objectArrayList.add(item);
                  }
               }
            }

            iterable = (Iterable)objectArrayList;

            for(Object obj3 : iterable) {
               File resourcePack = (File)obj3;
               int value4 = 0;
               int iaLoader = 0;
               String[] strs8 = new String[]{"assets"};
               file4 = FileUtilsKt.resolve(resourcePack, strs8).exists() ? nexoIaPack_94 : null;
               if (file4 == null) {
                  String[] namespace = new String[]{"assets"};
                  file4 = FileUtilsKt.resolve(nexoIaPack_94, namespace);
               }

               File target = file4;
               if (resourcePack.exists()) {
                  FilesKt.copyRecursively(resourcePack, target, true, (kotlin.jvm.functions.Function2<java.io.File,java.io.IOException,kotlin.io.OnErrorAction>)((java.io.File f_, java.io.IOException e_) -> kotlin.io.OnErrorAction.SKIP));
               }
            }

            file4 = iaFolder;
            String[] it_87 = new String[]{"contents"};
            File[] files4 = FileUtilsKt.resolve(file4, it_87).listFiles(ItemsAdderConverter::convert$lambda$7);
            if (files4 == null) {
               Object obj4 = null;
            } else {
               Object[] objs = files4;
               value2 = 0;

               for(int resourcePack = files4.length; value2 < resourcePack; ++value2) {
                  Object obj5 = objs[value2];
                  File namespace = (File)obj5;
                  int value5 = 0;
                  File[] namespaceFiles = ((File)obj5).listFiles(ItemsAdderConverter::convert$lambda$8$0);
                  if (namespaceFiles == null) {
                     Object obj6 = null;
                  } else {
                     Object[] objs2 = namespaceFiles;
                     int value6 = 0;

                     for(int j = objs2.length; value6 < j; ++value6) {
                        Object obj7 = objs2[value6];
                        int value7 = 0;
                        if (Intrinsics.areEqual(((File)obj7).getName(), "assets")) {
                           String[] strs9 = new String[]{"assets"};
                           file4 = FileUtilsKt.resolve(nexoIaPack_94, strs9);
                        } else {
                           String[] strs10 = new String[]{"assets", null, null};
                           String name = namespace.getName();
                           strs10[1] = name;
                           name = ((File)obj7).getName();
                           strs10[2] = name;
                           file4 = FileUtilsKt.resolve(nexoIaPack_94, strs10);
                        }

                        File target = file4;
                        FilesKt.copyRecursively((File)obj7, target, true, (kotlin.jvm.functions.Function2<java.io.File,java.io.IOException,kotlin.io.OnErrorAction>)((java.io.File f_, java.io.IOException e_) -> kotlin.io.OnErrorAction.SKIP));
                     }

                     Unit unit = Unit.INSTANCE;
                  }
               }

               Unit unit2 = Unit.INSTANCE;
            }

            ItemsAdderConverter itemsAdderConverter = this;

            Object result;
            try {
               ItemsAdderConverter itemsAdderConverter2 = itemsAdderConverter;
               int it_78 = 0;
               ResourcePack it_77 = NexoPackReader.INSTANCE.readFile(nexoIaPack_94, (PackFormat)null);
               int value8 = 0;
               it_77.models().removeIf((a0_ -> ItemsAdderConverter.convert$lambda$9$0$0(itemsAdderConverter2, (Model)a0_)));
               it_77.textures().removeIf((a0_ -> ItemsAdderConverter.convert$lambda$9$0$2(itemsAdderConverter2, (Texture)a0_)));
               result = ResultShim.ok(it_77);
            } catch (Throwable e) {
               result = ResultShim.ok(ResultKt.createFailure(e));
            }

            Throwable throwable = ResultShim.exceptionOrNull(result);
            if (throwable != null) {
               Throwable it_75 = throwable;
               value2 = 0;
               Logs.logWarn("Failed to read IA-Pack and filter out vanilla models & textures", false);
               if (Settings.DEBUG.toBool()) {
                  Logs.logWarn(ExceptionsKt.stackTraceToString(it_75), false);
               }
            }
         }

         List registeredItemIds = (List)(new ArrayList());
         Map recipeNodes = (Map)(new LinkedHashMap());
         if (iaConverter.getConvertItems()) {
            Iterable iterable2 = (Iterable)configFiles;
            int value9 = 0;

            for(Object obj8 : iterable2) {
               File itemFile = (File)obj8;
               int value10 = 0;
               YamlConfigurationLoader iaLoader = ((YamlConfigurationLoader.Builder)YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK).file(itemFile)).build();
               ItemsAdderConverter itemsAdderConverter3 = INSTANCE;

               Object nexoItemFile;
               try {
                  int value11 = 0;
                  nexoItemFile = ResultShim.ok((CommentedConfigurationNode)iaLoader.load());
               } catch (Throwable ex) {
                  nexoItemFile = ResultShim.ok(ResultKt.createFailure(ex));
               }

               Throwable throwable2 = ResultShim.exceptionOrNull(nexoItemFile);
               if (throwable2 != null) {
                  Throwable it_73 = throwable2;
                  int type = 0;
                  Logs.logError("Failed to load & convert " + itemFile.getPath(), false);
                  if (Settings.DEBUG.toBool()) {
                     Logs.logError(ExceptionsKt.stackTraceToString(it_73), false);
                  }
               } else {
                  CommentedConfigurationNode iaNode = (CommentedConfigurationNode)nexoItemFile;
                  File file7 = null;
                  String string = ((CommentedConfigurationNode)iaNode.node(new Object[]{"info", "namespace"})).getString();
                  File file8 = nexoFolder;
                  String[] nexoItemLoader = new String[]{"items/itemsadder/" + string + "/" + itemFile.getName()};
                  file7 = FileUtilsKt.resolve(file8, nexoItemLoader);
                  YamlConfigurationLoader nexoItemLoader_72 = ((YamlConfigurationLoader.Builder)YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK).file(file7)).build();
                  CommentedConfigurationNode dir = (CommentedConfigurationNode)nexoItemLoader_72.load();
                  Object[] objs3 = new Object[]{"armors_rendering"};
                  Map nexoRecipeNode = ((CommentedConfigurationNode)iaNode.node(objs3)).childrenMap();
                  Map childrenMap = ((CommentedConfigurationNode)iaNode.node(new Object[]{"equipments"})).childrenMap();
                  nexoRecipeNode = MapsKt.plus(nexoRecipeNode, childrenMap);
                  Collection collection = (Collection)(new ArrayList());
                  boolean flag2 = false;

                  for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)nexoRecipeNode.entrySet()) {
                     int value12 = 0;
                     int value13 = 0;
                     Object armorId = entry.getKey();
                     CommentedConfigurationNode armorNode = (CommentedConfigurationNode)entry.getValue();
                     Object[] objs4 = new Object[]{"layer_1"};
                     String str = ((CommentedConfigurationNode)armorNode.node(objs4)).getString();
                     if (str == null) {
                        str = ((CommentedConfigurationNode)armorNode.node(new Object[]{"layer_2"})).getString();
                     }

                     Object obj9;
                     block: {
                        if (str != null) {
                           str = StringUtilsKt.prependIfMissing(str, string + ":");
                           if (str != null) {
                              String layer1;
                              block2: {
                                 layer1 = str;
                                 str = ((CommentedConfigurationNode)armorNode.node(new Object[]{"layer_2"})).getString();
                                 if (str != null) {
                                    str = StringUtilsKt.prependIfMissing(str, string + ":");
                                    if (str != null) {
                                       break block2;
                                    }
                                 }

                                 str = layer1;
                              }

                              String layer2 = str;
                              obj9 = TuplesKt.to(armorId.toString(), TuplesKt.to(layer1, layer2));
                              break block;
                           }
                        }

                        obj9 = null;
                     }

                     if (obj9 != null) {
                        Object obj10 = obj9;
                        int value14 = 0;
                        collection.add(obj10);
                     }
                  }

                  block: {
                     Object2ObjectOpenHashMap armorLayerPaths = CollectionUtilsKt.toFastMap((Iterable)((List)collection));
                     ScopedConfigurationNode nexoRecipeFile = iaNode.node(new Object[]{"items"});
                     CommentedConfigurationNode it_70 = (CommentedConfigurationNode)nexoRecipeFile;
                     int value15 = 0;
                     CommentedConfigurationNode commentedConfigurationNode = (CommentedConfigurationNode)(!it_70.virtual() ? nexoRecipeFile : null);
                     if (commentedConfigurationNode != null) {
                        Map map = commentedConfigurationNode.childrenMap();
                        if (map != null) {
                           Collection collection2 = (Collection)(new ArrayList(map.size()));

                           for(Map.Entry entry2 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
                              int it_68 = 0;
                              Pair pair = TuplesKt.to(entry2.getKey().toString(), entry2.getValue());
                              collection2.add(pair);
                           }

                           List it_67 = (List)collection2;
                           Iterable iterable3 = (Iterable)it_67;

                           for(Object obj11 : iterable3) {
                              Pair pair2 = (Pair)obj11;
                              int value16 = 0;
                              String id = (String)pair2.component1();
                              CommentedConfigurationNode iaItem = (CommentedConfigurationNode)pair2.component2();
                              int value17 = 0;
                              String str2 = !registeredItemIds.contains(id) ? id : null;
                              if (str2 == null) {
                                 String p0 = string + "_" + id;
                                 value17 = 0;
                                 iaConverter.getChangedItemIds().put(string + ":" + id, p0);
                                 Logs.logWarn("Found duplicate Item-ID <i>" + id + "</i>, remapping to <i>" + p0, false);
                                 str2 = p0;
                              }

                              String nexoItem = str2;
                              int it_64 = 0;
                              registeredItemIds.add(nexoItem);
                              Ref.ObjectRef nexoItem_63 = new Ref.ObjectRef();
                              ScopedConfigurationNode it_62 = null;
                              ScopedConfigurationNode scopedConfigurationNode = dir.node(new Object[]{nexoItem});
                              nexoItem_63.element = scopedConfigurationNode;
                              Object[] it_61 = new Object[]{"display_name"};
                              ConfigHelpersKt.ifExists((ConfigurationNode)iaItem.node(it_61), (a0_ -> ItemsAdderConverter.convert$lambda$11$5$2(nexoItem_63, (CommentedConfigurationNode)a0_)));
                              ConfigurationNode configurationNode = (ConfigurationNode)nexoItem_63.element;
                              configurationNode = configurationNode.node(new Object[]{"material"});
                              configurationNode.set(((CommentedConfigurationNode)iaItem.node(new Object[]{"resource", "material"})).getString("PAPER"));
                              ConfigHelpersKt.ifExists((ConfigurationNode)iaItem.node(new Object[]{"lore"}), (a0_ -> ItemsAdderConverter.convert$lambda$11$5$3(nexoItem_63, (CommentedConfigurationNode)a0_)));
                              ScopedConfigurationNode enchantsNode = iaItem.node(new Object[]{"enchants"});
                              ConfigurationNode p0 = (ConfigurationNode)enchantsNode;
                              int value18 = 0;
                              CommentedConfigurationNode commentedConfigurationNode2 = (CommentedConfigurationNode)(!p0.virtual() ? enchantsNode : null);
                              if (commentedConfigurationNode2 == null) {
                                 ScopedConfigurationNode it_60 = iaItem.node(new Object[]{"enchantments"});
                                 p0 = (ConfigurationNode)it_60;
                                 value18 = 0;
                                 commentedConfigurationNode2 = (CommentedConfigurationNode)(!p0.virtual() ? it_60 : null);
                              }

                              if (commentedConfigurationNode2 == null) {
                                 Object obj12 = null;
                              } else {
                                 CommentedConfigurationNode enchantsNode_59 = commentedConfigurationNode2;
                                 int value19 = 0;
                                 if (enchantsNode_59.isList()) {
                                    List stringList = ConfigHelpersKt.getStringList((ConfigurationNode)enchantsNode_59);
                                    if (stringList == null) {
                                       stringList = CollectionsKt.emptyList();
                                    }

                                    Iterable iterable4 = (Iterable)stringList;

                                    for(Object obj13 : iterable4) {
                                       String enchant = (String)obj13;
                                       int p0_55 = 0;
                                       Integer level = StringsKt.toIntOrNull(StringsKt.substringAfterLast(enchant, ":", enchant));
                                       String str3 = level != null ? StringsKt.substringBeforeLast(enchant, ":", enchant) : enchant;
                                       String str4 = StringsKt.removePrefix(str3, (CharSequence)"minecraft:").toLowerCase(Locale.ROOT);
                                       String key = str4;
                                       ConfigurationNode configurationNode2 = (ConfigurationNode)nexoItem_63.element;
                                       Object[] objs5 = new Object[]{"Enchantments", key};
                                       configurationNode2.node(objs5).set(level != null ? level : 1);
                                    }
                                 } else {
                                    Map map2 = enchantsNode_59.childrenMap();
                                    Map map3 = map2;

                                    for(Map.Entry entry3 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map3.entrySet()) {
                                       int value20 = 0;
                                       Object enchant = entry3.getKey();
                                       CommentedConfigurationNode levelNode = (CommentedConfigurationNode)entry3.getValue();
                                       String str5 = StringsKt.removePrefix(enchant.toString(), (CharSequence)"minecraft:").toLowerCase(Locale.ROOT);
                                       String key = str5;
                                       ConfigurationNode configurationNode3 = (ConfigurationNode)nexoItem_63.element;
                                       Object[] objs6 = new Object[]{"Enchantments", key};
                                       configurationNode3.node(objs6).set(levelNode.getInt(1));
                                    }
                                 }

                                 Unit unit3 = Unit.INSTANCE;
                                 unit3 = Unit.INSTANCE;
                              }

                              ItemsAdderConverter itemsAdderConverter4 = INSTANCE;
                              ConfigurationNode configurationNode4 = (ConfigurationNode)iaItem;
                              ScopedConfigurationNode scopedConfigurationNode2 = iaItem.node(new Object[]{"attribute_modifiers"});
                              itemsAdderConverter4.convertAttributeModifiers(configurationNode4, (ConfigurationNode)scopedConfigurationNode2, (ConfigurationNode)nexoItem_63.element);
                              ConfigurationNode configurationNode5 = (ConfigurationNode)nexoItem_63.element;
                              configurationNode5 = configurationNode5.node(new Object[]{"unbreakable"});
                              configurationNode5.mergeFrom((ConfigurationNode)iaItem.node(new Object[]{"durability", "unbreakable"}));
                              configurationNode5 = (ConfigurationNode)nexoItem_63.element;
                              configurationNode5 = configurationNode5.node(new Object[]{"ItemFlags"});
                              configurationNode5.mergeFrom((ConfigurationNode)iaItem.node(new Object[]{"item_flags"}));
                              CommentedConfigurationNode it_51 = (CommentedConfigurationNode)iaItem.node(new Object[]{"nbt"});
                              int value21 = 0;
                              CommentedConfigurationNode commentedConfigurationNode3 = (CommentedConfigurationNode)(it_51.getString() != null ? it_51 : null);
                              if (commentedConfigurationNode3 != null) {
                                 commentedConfigurationNode3 = (CommentedConfigurationNode)ConfigHelpersKt.ifExists((ConfigurationNode)commentedConfigurationNode3, (a0_ -> ItemsAdderConverter.convert$lambda$11$5$7(nexoItem_63, string + ":" + id, (CommentedConfigurationNode)a0_)));
                              } else {
                                 Object obj14 = null;
                              }

                              ConfigHelpersKt.ifExists((ConfigurationNode)iaItem.node(new Object[]{"specific_properties", "armor"}), (a0_ -> ItemsAdderConverter.convert$lambda$11$5$8(nexoItem_63, iaConverter, string, id, string + ":" + id, (CommentedConfigurationNode)a0_)));
                              String it_50 = null;
                              String str6 = ((CommentedConfigurationNode)iaItem.node(new Object[]{"template"})).getString();
                              if (str6 == null) {
                                 str6 = ((CommentedConfigurationNode)iaItem.node(new Object[]{"variant_of"})).getString();
                              }

                              if (str6 != null) {
                                 it_50 = str6;
                                 value21 = 0;
                                 ConfigurationNode configurationNode6 = (ConfigurationNode)nexoItem_63.element;
                                 Object[] iaBlockNode = new Object[]{"template"};
                                 configurationNode6.node(iaBlockNode).set(it_50);
                              } else {
                                 Object obj15 = null;
                              }

                              ConfigurationNode configurationNode7 = (ConfigurationNode)nexoItem_63.element;
                              ConfigurationNode mechanicsNode = configurationNode7.node(new Object[]{"Mechanics"});
                              int value22 = 0;
                              ConfigurationNode prefix = null;
                              CommentedConfigurationNode iaBlockNode = (CommentedConfigurationNode)iaItem.node(new Object[]{"specific_properties", "block", "placed_model", "type"});
                              if (!iaBlockNode.virtual()) {
                                 String type = iaBlockNode.getString();
                                 if (Intrinsics.areEqual(type, "REAL_NOTE")) {
                                    ConfigurationNode furnitureNode = mechanicsNode.node(new Object[]{"custom_block"});
                                    int value23 = 0;
                                    NoteBlock legacyData = null;
                                    furnitureNode.node(new Object[]{"type"}).set("NOTEBLOCK");
                                    NoteMechanicHelpers noteMechanicHelpers = NoteMechanicHelpers.INSTANCE;
                                    Object[] modernVariation = new Object[]{string + ":" + nexoItem};
                                    legacyData = noteMechanicHelpers.legacyBlockData(((CommentedConfigurationNode)noteBlockVariationCache.node(modernVariation)).getInt() - 256);
                                    if (legacyData != null) {
                                       NoteMechanicHelpers xOffset = NoteMechanicHelpers.INSTANCE;
                                       int value24 = 0;
                                       Integer number = xOffset.modernCustomVariation(legacyData);
                                       if (number != null) {
                                          int modernVariation_49 = number;
                                          Object[] objs7 = new Object[]{"custom_variation"};
                                          furnitureNode.node(objs7).set(modernVariation_49);
                                       }
                                    }

                                    configurationNode7 = furnitureNode;
                                 } else if (Intrinsics.areEqual(type, "REAL_WIRE")) {
                                    ConfigurationNode configurationNode8 = mechanicsNode.node(new Object[]{"custom_block"});
                                    int value25 = 0;
                                    Tripwire legacyBlockData = null;
                                    configurationNode8.node(new Object[]{"type"}).set("STRINGBLOCK");
                                    configurationNode7 = configurationNode8.node(new Object[]{"is_tall"});
                                    configurationNode7.set(((CommentedConfigurationNode)iaItem.node(new Object[]{"specific_properties", "block", "shift_up"})).getBoolean());
                                    StringMechanicHelpers stringMechanicHelpers = StringMechanicHelpers.INSTANCE;
                                    Object[] transform = new Object[]{string + ":" + nexoItem};
                                    legacyBlockData = stringMechanicHelpers.legacyBlockData(((CommentedConfigurationNode)stringBlockVariationCache.node(transform)).getInt() - 1008);
                                    int modernVariation = StringMechanicHelpers.INSTANCE.modernCustomVariation(legacyBlockData);
                                    Object[] objs8 = new Object[]{"custom_variation"};
                                    configurationNode8.node(objs8).set(modernVariation);
                                    configurationNode7 = configurationNode8;
                                 } else {
                                    Logs.logError("Failed to properly migrate " + nexoItem + " to Nexo...", false);
                                    Logs.logWarn("      Item is a Custom-Block with unsupported type " + type, false);
                                    configurationNode7 = null;
                                 }

                                 prefix = configurationNode7;
                                 if (prefix != null) {
                                    int value26 = 0;
                                    Object[] iaDisplayTransform = new Object[]{"resource", "generate"};
                                    String str7;
                                    if (((CommentedConfigurationNode)iaItem.node(iaDisplayTransform)).getBoolean()) {
                                       str7 = nexoItem;
                                    } else {
                                       str7 = ((CommentedConfigurationNode)iaItem.node(new Object[]{"resource", "model_path"})).getString();
                                    }

                                    String blockModel = str7;
                                    prefix.node(new Object[]{"model"}).set(blockModel);
                                    ConfigurationNode configurationNode9 = prefix.node(new Object[]{"hardness"});
                                    configurationNode9.set(((CommentedConfigurationNode)iaItem.node(new Object[]{"block", "hardness"})).getDouble((double)2.0F));
                                    ItemsAdderConverter itemsAdderConverter5 = INSTANCE;
                                    Object[] objs9 = new Object[]{"specific_properties", "block"};
                                    scopedConfigurationNode2 = iaItem.node(objs9);
                                    itemsAdderConverter5.convertBlockSounds(prefix, (ConfigurationNode)scopedConfigurationNode2);
                                    INSTANCE.convertPlaceableOn(prefix, (ConfigurationNode)iaItem, true);
                                    Unit unit4 = Unit.INSTANCE;
                                    unit4 = Unit.INSTANCE;
                                 } else {
                                    Object obj16 = null;
                                 }
                              }

                              ConfigurationNode path = mechanicsNode.node(new Object[]{"furniture"});
                              int value27 = 0;
                              CommentedConfigurationNode iaDisplayTransform = null;
                              configurationNode7 = iaItem.hasChild(new Object[]{"behaviours", "furniture"}) ? path : null;
                              if (configurationNode7 != null) {
                                 ConfigurationNode furnitureNode = configurationNode7;
                                 value27 = 0;
                                 CommentedConfigurationNode iaFurniture = (CommentedConfigurationNode)iaItem.node(new Object[]{"behaviours", "furniture"});
                                 String transform = null;
                                 iaDisplayTransform = (CommentedConfigurationNode)iaFurniture.node(new Object[]{"display_transformation"});
                                 CommentedConfigurationNode iaHitbox = null;
                                 String str8;
                                 if (Intrinsics.areEqual(((CommentedConfigurationNode)iaFurniture.node(new Object[]{"entity"})).getString(), "ARMOR_STAND")) {
                                    str8 = "HEAD";
                                 } else {
                                    if (Intrinsics.areEqual(((CommentedConfigurationNode)iaFurniture.node(new Object[]{"entity"})).getString(), "ITEM_FRAME")) {
                                       str8 = "FIXED";
                                    } else {
                                       if (Intrinsics.areEqual(((CommentedConfigurationNode)iaDisplayTransform.node(new Object[]{"adjust_legacy_model"})).getString(), "armor_stand")) {
                                          str8 = "HEAD";
                                       } else {
                                          str8 = Intrinsics.areEqual(((CommentedConfigurationNode)iaDisplayTransform.node(new Object[]{"adjust_legacy_model"})).getString(), "item_frame") ? "FIXED" : "NONE";
                                       }
                                    }
                                 }

                                 transform = str8;
                                 ConfigurationNode configurationNode10 = furnitureNode.node(new Object[]{"properties", "display_transform"});
                                 configurationNode10.set(((CommentedConfigurationNode)iaDisplayTransform.node(new Object[]{"transform"})).getString(transform));
                                 ConfigHelpersKt.ifExists((ConfigurationNode)iaDisplayTransform.node(new Object[]{"translation"}), (a0_ -> ItemsAdderConverter.convert$lambda$11$5$10$4$0(furnitureNode, (CommentedConfigurationNode)a0_)));
                                 ConfigHelpersKt.ifExists((ConfigurationNode)iaDisplayTransform.node(new Object[]{"scale"}), (a0_ -> ItemsAdderConverter.convert$lambda$11$5$10$4$1(furnitureNode, iaFurniture, (CommentedConfigurationNode)a0_)));
                                 String it_47 = null;
                                 String str9 = ((CommentedConfigurationNode)iaDisplayTransform.node(new Object[]{"adjust_legacy_model"})).getString();
                                 if (str9 != null) {
                                    block2: {
                                       it_47 = str9;
                                       int value28 = 0;
                                       Pair pair3;
                                       if (Intrinsics.areEqual(it_47, "armor_stand")) {
                                          pair3 = TuplesKt.to("0.65,0.65,0.65", "0,1.7,0");
                                       } else {
                                          if (!Intrinsics.areEqual(it_47, "item_frame")) {
                                             break block2;
                                          }

                                          pair3 = TuplesKt.to("0.5,0.5,0.5", "0,0,0");
                                       }

                                       Pair pair4 = pair3;
                                       String scale = (String)pair4.component1();
                                       String translation = (String)pair4.component2();
                                       Object[] objs10 = new Object[]{"properties", "scale"};
                                       furnitureNode.node(objs10).set(scale);
                                       furnitureNode.node(new Object[]{"properties", "translation"}).set(translation);
                                       if (Intrinsics.areEqual(it_47, "item_frame")) {
                                          furnitureNode.node(new Object[]{"properties", "right_rotation"}).set("0.707,0,0,-0.707");
                                       }
                                    }
                                 } else {
                                    Object obj17 = null;
                                 }

                                 if (((CommentedConfigurationNode)iaFurniture.node(new Object[]{"solid"})).getBoolean()) {
                                    Object[] it_46 = new Object[]{"hitbox", "width_offset"};
                                    double xOffset = ((CommentedConfigurationNode)iaFurniture.node(it_46)).getDouble();
                                    Object[] zOffset = new Object[]{"hitbox", "width"};
                                    int xSize = ((CommentedConfigurationNode)iaFurniture.node(zOffset)).getInt();
                                    Object[] objs11 = new Object[]{"hitbox", "height_offset"};
                                    double yOffset = ((CommentedConfigurationNode)iaFurniture.node(objs11)).getDouble();
                                    Object[] objs12 = new Object[]{"hitbox", "height"};
                                    int ySize = ((CommentedConfigurationNode)iaFurniture.node(objs12)).getInt();
                                    Object[] objs13 = new Object[]{"hitbox", "length_offset"};
                                    double zOffset_45 = ((CommentedConfigurationNode)iaFurniture.node(objs13)).getDouble();
                                    List barriers = null;
                                    int zSize = ((CommentedConfigurationNode)iaFurniture.node(new Object[]{"hitbox", "length"})).getInt();
                                    barriers = (List)(new ArrayList());

                                    for(int x = 0; x < xSize; ++x) {
                                       for(int y = 0; y < ySize; ++y) {
                                          for(int z = 0; z < zSize; ++z) {
                                             ((Collection)barriers).add((double)x + xOffset + "," + ((double)y + yOffset) + "," + ((double)z + zOffset_45));
                                          }
                                       }
                                    }

                                    Iterable iterable5 = null;
                                    furnitureNode.node(new Object[]{"hitbox", "barriers"}).set(barriers);
                                    Object[] objs14 = new Object[]{"hitbox"};
                                    List childrenList = ((CommentedConfigurationNode)iaFurniture.node(objs14)).childrenList();
                                    iterable5 = (Iterable)childrenList;
                                    boolean flag3;
                                    if (iterable5 instanceof Collection && ((Collection)iterable5).isEmpty()) {
                                       flag3 = false;
                                    } else {
                                       block2: {
                                          for(Object obj18 : iterable5) {
                                             CommentedConfigurationNode it_43 = (CommentedConfigurationNode)obj18;
                                             int value29 = 0;
                                             if (it_43.getInt(1) != 1) {
                                                flag3 = true;
                                                break block2;
                                             }
                                          }

                                          flag3 = false;
                                       }
                                    }

                                    if (flag3) {
                                       ConfigurationNode configurationNode11 = furnitureNode.node(new Object[]{"hitbox"});
                                       configurationNode11.set(((CommentedConfigurationNode)iaFurniture.node(new Object[]{"hitbox"})).getList(Integer.TYPE));
                                    }

                                    Unit unit5 = Unit.INSTANCE;
                                 } else {
                                    String xOffset = null;
                                    iaHitbox = (CommentedConfigurationNode)iaFurniture.node(new Object[]{"hitbox"});
                                    String yOffset = null;
                                    xOffset = StringsKt.removeSuffix(String.valueOf(((CommentedConfigurationNode)iaHitbox.node(new Object[]{"width_offset"})).getDouble()), (CharSequence)".0");
                                    String zOffset = null;
                                    yOffset = StringsKt.removeSuffix(String.valueOf(((CommentedConfigurationNode)iaHitbox.node(new Object[]{"height_offset"})).getDouble()), (CharSequence)".0");
                                    String width = null;
                                    zOffset = StringsKt.removeSuffix(String.valueOf(((CommentedConfigurationNode)iaHitbox.node(new Object[]{"length_offset"})).getDouble()), (CharSequence)".0");
                                    String height = null;
                                    width = StringsKt.removeSuffix(String.valueOf(((CommentedConfigurationNode)iaHitbox.node(new Object[]{"width"})).getDouble((double)1.0F)), (CharSequence)".0");
                                    Object[] objs15 = new Object[]{"height"};
                                    height = StringsKt.removeSuffix(String.valueOf(((CommentedConfigurationNode)iaHitbox.node(objs15)).getDouble((double)1.0F)), (CharSequence)".0");
                                    furnitureNode.node(new Object[]{"hitbox", "interactions"}).set(CollectionsKt.listOf(xOffset + "," + yOffset + "," + zOffset + " " + width + "," + height));
                                 }

                                 Object[] objs16 = new Object[]{"light_level"};
                                 ConfigHelpersKt.ifExists((ConfigurationNode)iaFurniture.node(objs16), (a0_ -> ItemsAdderConverter.convert$lambda$11$5$10$4$4(iaFurniture, furnitureNode, (CommentedConfigurationNode)a0_)));
                                 ConfigHelpersKt.ifExists((ConfigurationNode)iaItem.node(new Object[]{"behaviours", "furniture_sit"}), (a0_ -> ItemsAdderConverter.convert$lambda$11$5$10$4$5(furnitureNode, (CommentedConfigurationNode)a0_)));
                                 INSTANCE.convertBlockSounds(furnitureNode, furnitureNode);
                                 ItemsAdderConverter itemsAdderConverter6 = INSTANCE;
                                 Object[] answer = new Object[]{"placeable_on"};
                                 ConfigurationNode configurationNode12 = furnitureNode.node(answer);
                                 itemsAdderConverter6.convertPlaceableOn(furnitureNode, configurationNode12, false);
                                 Unit unit6 = Unit.INSTANCE;
                                 unit6 = Unit.INSTANCE;
                              } else {
                                 Object obj19 = null;
                              }

                              configurationNode7 = (ConfigurationNode)nexoItem_63.element;
                              ConfigurationNode packNode = configurationNode7.node(new Object[]{"Pack"});
                              value22 = 0;
                              String prefix_42 = null;
                              String blockNode = ((CommentedConfigurationNode)iaItem.node(new Object[]{"specific_properties", "block", "placed_model", "type"})).getString();
                              if (Intrinsics.areEqual(blockNode, "REAL_NOTE")) {
                                 Object[] objs17 = new Object[]{"parent_model"};
                                 packNode.node(objs17).set("block/cube_all");
                              } else if (Intrinsics.areEqual(blockNode, "REAL_WIRE")) {
                                 Object[] objs18 = new Object[]{"parent_model"};
                                 packNode.node(objs18).set("block/cross");
                              }

                              if ((CommentedConfigurationNode)ConfigHelpersKt.ifExists((ConfigurationNode)iaItem.node(new Object[]{"resource", "model_path"}), (a0_ -> ItemsAdderConverter.convert$lambda$11$5$11$0(string, packNode, (CommentedConfigurationNode)a0_))) == null) {
                                 CommentedConfigurationNode commentedConfigurationNode4 = (CommentedConfigurationNode)ConfigHelpersKt.ifExists((ConfigurationNode)iaItem.node(new Object[]{"resource", "textures"}), (a0_ -> ItemsAdderConverter.convert$lambda$11$5$11$1(packNode, string, (CommentedConfigurationNode)a0_)));
                              }

                              String path_41 = null;
                              Object[] materialPath = new Object[1];
                              String str10 = null;
                              materialPath[0] = ((CommentedConfigurationNode)iaItem.node(new Object[]{"resource", "material"})).getString();
                              CommentedConfigurationNode commentedConfigurationNode5 = (CommentedConfigurationNode)((CommentedConfigurationNode)customModelDataCache_96.node(materialPath)).childrenMap().get(string + ":" + nexoItem);
                              if (commentedConfigurationNode5 == null || (CommentedConfigurationNode)ConfigHelpersKt.ifExists((ConfigurationNode)commentedConfigurationNode5, (a0_ -> ItemsAdderConverter.convert$lambda$11$5$11$2(packNode, (CommentedConfigurationNode)a0_))) == null) {
                                 commentedConfigurationNode5 = (CommentedConfigurationNode)ConfigHelpersKt.ifExists((ConfigurationNode)iaItem.node(new Object[]{"resource", "model_id"}), (a0_ -> ItemsAdderConverter.convert$lambda$11$5$11$3(packNode, (CommentedConfigurationNode)a0_)));
                              }

                              prefix_42 = !packNode.node(new Object[]{"model"}).empty() ? "model" : "texture";
                              String str11 = null;
                              String str12;
                              if (packNode.hasChild(new Object[]{"model"})) {
                                 str12 = ((CommentedConfigurationNode)iaItem.node(new Object[]{"resource", "model_path"})).getString();
                              } else {
                                 if (packNode.hasChild(new Object[]{"textures"})) {
                                    block2: {
                                       Object[] objs19 = new Object[]{"textures"};
                                       List list3 = packNode.node(objs19).getList(String.class);
                                       if (list3 != null) {
                                          str12 = (String)CollectionsKt.firstOrNull(list3);
                                          if (str12 != null) {
                                             break block2;
                                          }
                                       }

                                       str12 = nexoItem;
                                    }
                                 } else {
                                    str12 = nexoItem;
                                 }
                              }

                              block2: {
                                 str10 = str12;
                                 if (str10 != null) {
                                    str11 = StringUtilsKt.prependIfMissing(str10, string + ":");
                                    if (str11 != null) {
                                       String str13 = StringsKt.removeSuffix(str11, (CharSequence)".png");
                                       if (str13 != null) {
                                          str12 = str13;
                                          break block2;
                                       }
                                    }
                                 }

                                 str12 = StringUtilsKt.prependIfMissing(nexoItem, string + ":");
                              }

                              block2: {
                                 path_41 = str12;
                                 ConfigurationNode configurationNode13 = (ConfigurationNode)nexoItem_63.element;
                                 Object[] objs20 = new Object[]{"material"};
                                 str10 = configurationNode13.node(objs20).getString();
                                 if (str10 != null) {
                                    switch (str10) {
                                       case "SHIELD":
                                          packNode.node(new Object[]{"blocking_" + prefix_42}).set(path_41 + "_blocking");
                                          break block2;
                                       case "CROSSBOW":
                                          configurationNode13 = packNode.node(new Object[]{"pulling_" + prefix_42 + "s"});
                                          byte b = 3;
                                          ConfigurationNode p0_40 = configurationNode13;
                                          ArrayList arrayList = new ArrayList(b);

                                          for(int k = 0; k < b; ++k) {
                                             int value30 = 0;
                                             arrayList.add(path_41 + "_" + k);
                                          }

                                          p0_40.set((List)arrayList);
                                          Object[] objs21 = new Object[]{"charged_" + prefix_42};
                                          packNode.node(objs21).set(path_41 + "_charged");
                                          packNode.node(new Object[]{"firework_" + prefix_42}).set(path_41 + "_firework");
                                          break block2;
                                       case "BOW":
                                          configurationNode13 = packNode.node(new Object[]{"pulling_" + prefix_42 + "s"});
                                          byte b2 = 3;
                                          ConfigurationNode p0_39 = configurationNode13;
                                          ArrayList arrayList2 = new ArrayList(b2);

                                          for(int l = 0; l < b2; ++l) {
                                             int value31 = 0;
                                             arrayList2.add(path_41 + "_" + l);
                                          }

                                          p0_39.set((List)arrayList2);
                                          break block2;
                                       case "FISHING_ROD":
                                          packNode.node(new Object[]{"cast_" + prefix_42}).set(path_41 + "_cast");
                                          break block2;
                                    }
                                 }

                                 Unit unit7 = Unit.INSTANCE;
                              }

                              Object[] objs22 = new Object[]{"equipment", "id"};
                              ConfigHelpersKt.ifExists((ConfigurationNode)iaItem.node(objs22), (a0_ -> ItemsAdderConverter.convert$lambda$11$5$11$6(armorLayerPaths, packNode, (CommentedConfigurationNode)a0_)));
                              ConfigHelpersKt.ifExists((ConfigurationNode)iaItem.node(new Object[]{"specific_properties", "armor", "custom_armor"}), (a0_ -> ItemsAdderConverter.convert$lambda$11$5$11$7(armorLayerPaths, packNode, (CommentedConfigurationNode)a0_)));
                              Unit unit8 = Unit.INSTANCE;
                           }

                           Unit unit9 = Unit.INSTANCE;
                           break block;
                        }
                     }

                     Object obj20 = null;
                  }

                  file8 = nexoFolder;
                  String[] strs11 = new String[]{"glyphs/itemsadder/" + string + "/" + itemFile.getName()};
                  File nexoGlyphFile = FileUtilsKt.resolve(file8, strs11);
                  YamlConfigurationLoader nexoGlyphLoader = ((YamlConfigurationLoader.Builder)YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK).file(nexoGlyphFile)).build();
                  CommentedConfigurationNode nexoGlyphNode = (CommentedConfigurationNode)nexoGlyphLoader.load();
                  Object[] objs23 = new Object[]{"font_images"};
                  Map map4 = ((CommentedConfigurationNode)iaNode.node(objs23)).childrenMap();
                  Collection collection3 = (Collection)(new ArrayList(map4.size()));

                  for(Map.Entry entry4 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map4.entrySet()) {
                     int value32 = 0;
                     Pair pair5 = TuplesKt.to(entry4.getKey(), entry4.getValue());
                     collection3.add(pair5);
                  }

                  Iterable iterable6 = (Iterable)((List)collection3);

                  for(Object obj21 : iterable6) {
                     Pair pair6 = (Pair)obj21;
                     int value33 = 0;
                     Object fontImageId = pair6.component1();
                     CommentedConfigurationNode iaFontImage = (CommentedConfigurationNode)pair6.component2();
                     Object[] it_29 = new Object[]{fontImageId, "texture"};
                     CommentedConfigurationNode commentedConfigurationNode6 = (CommentedConfigurationNode)nexoGlyphNode.node(it_29);
                     Object[] objs24 = new Object[]{"path"};
                     String str14 = ((CommentedConfigurationNode)iaFontImage.node(objs24)).getString();
                     if (str14 != null) {
                        String str15 = str14;
                        CommentedConfigurationNode targetTypes = commentedConfigurationNode6;
                        int value34 = 0;
                        str14 = StringsKt.contains((CharSequence)str15, (CharSequence)":", false) ? str15 : string + ":" + str15;
                        commentedConfigurationNode6 = targetTypes;
                     } else {
                        str14 = null;
                     }

                     commentedConfigurationNode6.set(str14);
                     commentedConfigurationNode6 = (CommentedConfigurationNode)nexoGlyphNode.node(new Object[]{fontImageId, "height"});
                     commentedConfigurationNode6.set(iaFontImage.node(new Object[]{"scale_ratio"}));
                     commentedConfigurationNode6 = (CommentedConfigurationNode)nexoGlyphNode.node(new Object[]{fontImageId, "ascent"});
                     commentedConfigurationNode6.set(iaFontImage.node(new Object[]{"y_position"}));
                     commentedConfigurationNode6 = (CommentedConfigurationNode)nexoGlyphNode.node(new Object[]{fontImageId, "permission"});
                     commentedConfigurationNode6.set(iaFontImage.node(new Object[]{"permission"}));
                     commentedConfigurationNode6 = (CommentedConfigurationNode)nexoGlyphNode.node(new Object[]{fontImageId, "char"});
                     commentedConfigurationNode6.set(glyphCharCache_97.node(new Object[]{string + ":" + fontImageId}));
                  }

                  Object[] objs25 = new Object[]{"recipes"};
                  Map map5 = ((CommentedConfigurationNode)iaNode.node(objs25)).childrenMap();

                  for(Map.Entry entry5 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map5.entrySet()) {
                     int recipeId = 0;
                     Object type = entry5.getKey();
                     CommentedConfigurationNode node = (CommentedConfigurationNode)entry5.getValue();
                     String str16;
                     if (Intrinsics.areEqual(type, "crafting_table")) {
                        str16 = RecipeType.SHAPED.getId();
                     } else {
                        if (!Intrinsics.areEqual(type, "cooking")) {
                           Logs.logWarn("Skipping unsupported recipe-type <i>" + type + "</i> in " + itemFile.getName(), false);
                           continue;
                        }

                        str16 = "cooking";
                     }

                     String type_25 = str16;
                     recipeNodes.compute(type_25, ((a0_, a1_) -> ItemsAdderConverter.convert$lambda$11$8$0(node, (String)a0_, (List)a1_)));
                  }

                  if (!dir.empty()) {
                     nexoItemLoader_72.save((ConfigurationNode)dir);
                  }

                  if (!nexoGlyphNode.empty()) {
                     nexoGlyphLoader.save((ConfigurationNode)nexoGlyphNode);
                  }
               }
            }
         }

         file4 = nexoFolder;
         it_95 = new String[]{"recipes"};
         File nexoRecipeFiles = FileUtilsKt.resolve(file4, it_95);
         Pair[] convertedRecipes = new Pair[]{TuplesKt.to("FURNACE", RecipeType.FURNACE.getId()), TuplesKt.to("BLAST_FURNACE", RecipeType.BLASTING.getId()), TuplesKt.to("SMOKER", RecipeType.SMOKER.getId()), TuplesKt.to("CAMPFIRE", RecipeType.CAMPFIRE.getId())};
         Map machineTypes = MapsKt.mapOf(convertedRecipes);
         Map convertedRecipes_24 = (Map)(new LinkedHashMap());
         int value35 = 0;

         for(Map.Entry entry6 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)recipeNodes.entrySet()) {
            int value36 = 0;
            String type = (String)entry6.getKey();
            List recipeNodes_23 = (List)entry6.getValue();
            Iterable iterable7 = (Iterable)recipeNodes_23;

            for(Object obj22 : iterable7) {
               ConfigurationNode recipeNode = (ConfigurationNode)obj22;
               int value37 = 0;
               Map map6 = recipeNode.childrenMap();
               Map map7 = map6;

               for(Map.Entry entry7 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map7.entrySet()) {
                  int iaFontImage = 0;
                  Object recipeId = entry7.getKey();
                  ConfigurationNode recipeNode_16 = (ConfigurationNode)entry7.getValue();
                  recipeNode_16.removeChild("enabled");
                  Object[] objs26 = new Object[]{"pattern"};
                  ConfigurationNode configurationNode14 = ConfigHelpersKt.renameNode(recipeNode_16.node(objs26), "shape");
                  if (configurationNode14 != null) {
                     ConfigHelpersKt.ifExists(configurationNode14, ItemsAdderConverter::convert$lambda$13$0$0$0);
                  } else {
                     Object obj23 = null;
                  }

                  Map map8 = null;
                  ConfigHelpersKt.ifExists(recipeNode_16.node(new Object[]{"result"}), (a0_ -> ItemsAdderConverter.convert$lambda$13$0$0$1(iaConverter, (ConfigurationNode)a0_)));
                  Map map9 = recipeNode_16.node(new Object[]{"ingredients"}).childrenMap();
                  map8 = map9;

                  for(Map.Entry entry8 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map8.entrySet()) {
                     int value38 = 0;
                     ConfigurationNode valueNode = (ConfigurationNode)entry8.getValue();
                     String str17 = valueNode.getString();
                     if (str17 != null) {
                        String ingredientString = str17;
                        valueNode.set(convert$ingredientMap(iaConverter, ingredientString));
                     }
                  }

                  List targetTypes = null;
                  ConfigHelpersKt.ifExists(recipeNode_16.node(new Object[]{"ingredient"}), (a0_ -> ItemsAdderConverter.convert$lambda$13$0$0$3(recipeNode, iaConverter, (ConfigurationNode)a0_)));
                  ConfigHelpersKt.renameNode(recipeNode_16.node(new Object[]{"exp"}), "experience");
                  ConfigHelpersKt.renameNode(recipeNode_16.node(new Object[]{"cook_time"}), "cookingTime");
                  List list4;
                  if (!Intrinsics.areEqual(type, "cooking")) {
                     list4 = CollectionsKt.listOf(type);
                  } else {
                     Collection blockNode = null;
                     list4 = ConfigHelpersKt.getStringList(recipeNode_16.node(new Object[]{"machines"}));
                     if (list4 == null) {
                        list4 = CollectionsKt.emptyList();
                     }

                     blockNode = (Collection)list4;
                     Object obj24;
                     if (blockNode.isEmpty()) {
                        int value39 = 0;
                        obj24 = CollectionsKt.listOf("FURNACE");
                     } else {
                        obj24 = blockNode;
                     }

                     List machines = (List)obj24;
                     recipeNode_16.removeChild("machines");
                     Iterable iterable8 = (Iterable)machines;
                     int value40 = 0;
                     Collection collection4 = (Collection)(new ArrayList());

                     for(Object obj25 : iterable8) {
                        int value41 = 0;
                        String machine = (String)obj25;
                        int value42 = 0;
                        String str18 = machine.toUpperCase(Locale.ROOT);
                        String str19 = (String)machineTypes.get(str18);
                        if (str19 == null) {
                           ItemsAdderConverter itemsAdderConverter7 = INSTANCE;
                           int value43 = 0;
                           Logs.logWarn("Skipping unsupported cooking-machine <i>" + machine + "</i> for recipe " + recipeId, false);
                           str19 = null;
                        }

                        if (str19 != null) {
                           Object obj26 = str19;
                           int value44 = 0;
                           collection4.add(obj26);
                        }
                     }

                     list4 = (List)collection4;
                  }

                  targetTypes = list4;
                  Iterable iterable9 = (Iterable)targetTypes;
                  int index = 0;

                  for(Object obj27 : iterable9) {
                     int value45 = index++;
                     if (value45 < 0) {
                        CollectionsKt.throwIndexOverflow();
                     }

                     String targetType = (String)obj27;
                     int value46 = 0;
                     String id = value45 == 0 ? recipeId.toString() : recipeId + "_" + targetType;
                     int value47 = 0;
                     Object obj28 = convertedRecipes_24.get(targetType);
                     Object obj29;
                     if (obj28 == null) {
                        int value48 = 0;
                        Object obj30 = new LinkedHashMap();
                        convertedRecipes_24.put(targetType, obj30);
                        obj29 = obj30;
                     } else {
                        obj29 = obj28;
                     }

                     ((Map)obj29).put(id, recipeNode_16);
                  }
               }
            }
         }

         value35 = 0;

         for(Map.Entry entry9 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)convertedRecipes_24.entrySet()) {
            int value49 = 0;
            String type = (String)entry9.getKey();
            Map recipes = (Map)entry9.getValue();
            String[] strs12 = new String[]{type + ".yml"};
            File nexoRecipeFile = FileUtilsKt.resolve(nexoRecipeFiles, strs12);
            YamlConfigurationLoader nexoRecipeLoader = ((YamlConfigurationLoader.Builder)YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK).file(nexoRecipeFile)).build();
            CommentedConfigurationNode nexoRecipeNode = (CommentedConfigurationNode)nexoRecipeLoader.load();

            for(Map.Entry entry10 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)recipes.entrySet()) {
               int value50 = 0;
               String recipeId = (String)entry10.getKey();
               ConfigurationNode recipeNode = (ConfigurationNode)entry10.getValue();
               Object[] node = new Object[]{recipeId};
               ((CommentedConfigurationNode)nexoRecipeNode.node(node)).set(recipeNode);
            }

            if (!nexoRecipeNode.empty()) {
               nexoRecipeLoader.save((ConfigurationNode)nexoRecipeNode);
            }
         }

         FilesKt.deleteRecursively(iaFolder);
         Logs.logSuccess("Finished conversion of ItemsAdder- to Nexo-setup!.", false);
      }
      } catch (Exception ex2) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex2);
      }
   }

   private final String legacyToMiniMessage(String str) {
      CharSequence charSequence = (CharSequence)str;
      Regex regex = AMPERSAND_CODE_REGEX;
      String str2 = "§$1";
      return AdventureUtils.parseLegacy(regex.replace(charSequence, str2));
   }

   private final void convertBlockSounds(ConfigurationNode configurationNode, ConfigurationNode iaSoundParent) {
      try {
      Object[] objs = new Object[]{"block_sounds"};
      ConfigurationNode configurationNode2 = configurationNode.node(objs);
      configurationNode2 = configurationNode2.set(iaSoundParent.node(new Object[]{"sound"}));
      if (configurationNode2 != null) {
         List childrenList = configurationNode2.childrenList();
         if (childrenList != null) {
            Iterable iterable = (Iterable)childrenList;
            int value = 0;

            for(Object element : iterable) {
               ConfigurationNode blockSound = (ConfigurationNode)element;
               int value2 = 0;
               childrenList = blockSound.childrenList();
               Iterable iterable2 = (Iterable)childrenList;
               Collection destination = (Collection)(new ArrayList());

               for(Object obj : iterable2) {
                  ConfigurationNode it = (ConfigurationNode)obj;
                  int value3 = 0;
                  NodePath path = blockSound.path();
                  Object[] objs2 = new Object[]{"name"};
                  if (Intrinsics.areEqual(path, NodePath.path(objs2))) {
                     destination.add(obj);
                  }
               }

               int value4 = ((List)destination).size();

               for(int i = 0; i < value4; ++i) {
                  int value5 = 0;
                  Object[] objs3 = new Object[]{"sound"};
                  ConfigurationNode configurationNode3 = blockSound.node(objs3);
                  configurationNode3.set(blockSound.node(new Object[]{"name"})).removeChild("name");
               }
            }
         }
      }

      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private final void convertPlaceableOn(ConfigurationNode configurationNode, ConfigurationNode placeableOn, boolean flag) {
      Object[] objs = new Object[]{"limited_placing"};
      ConfigurationNode limitedPlacing = configurationNode.node(objs);
      ConfigHelpersKt.ifExists(placeableOn.node(new Object[]{"ceiling"}), (a0_ -> ItemsAdderConverter.convertPlaceableOn$lambda$0(limitedPlacing, (ConfigurationNode)a0_)));
      ConfigHelpersKt.ifExists(placeableOn.node(new Object[]{"walls"}), (a0_ -> ItemsAdderConverter.convertPlaceableOn$lambda$1(limitedPlacing, (ConfigurationNode)a0_)));
      ConfigHelpersKt.ifExists(placeableOn.node(new Object[]{"floor"}), (a0_ -> ItemsAdderConverter.convertPlaceableOn$lambda$2(limitedPlacing, (ConfigurationNode)a0_)));
   }

   private final void convertAttributeModifiers(ConfigurationNode configurationNode, ConfigurationNode attributeModifiersNode, ConfigurationNode nexoItem) {
        ConfigHelpersKt.ifExists(attributeModifiersNode, arg_0 -> ItemsAdderConverter.convertAttributeModifiers$lambda$0(nexoItem, attributeModifiersNode, configurationNode, arg_0));
    }

   private static final Map<String, String> convert$ingredientMap(Converter.ItemsAdderConverter iaConverter, String id) {
      Material material = Material.matchMaterial(id);
      Map map;
      if (material != null) {
         Material it = material;
         int value = 0;
         map = MapsKt.mapOf(TuplesKt.to("minecraft_type", it.name()));
         if (map != null) {
            return map;
         }
      }

      String str = (String)iaConverter.getChangedItemIds().get(id);
      if (str == null) {
         str = id;
      }

      map = MapsKt.mapOf(TuplesKt.to("nexo_item", StringsKt.substringAfter(str, ":", str)));
      return map;
   }

   private static final ObjectOpenHashSet vanillaModels_delegate$lambda$0() {
      Collection models = VanillaResourcePack.INSTANCE.getResourcePack().models();
      Iterable iterable = (Iterable)models;
      Collection destination = (Collection)(new ObjectOpenHashSet((Collection)iterable != null ? ((Collection)iterable).size() : 10));

      for(Object item : iterable) {
         Model it = (Model)item;
         int value = 0;
         String asMinimalString = it.key().asMinimalString();
         destination.add(StringsKt.removeSuffix(asMinimalString, (CharSequence)".json"));
      }

      return (ObjectOpenHashSet)destination;
   }

   private static final ObjectOpenHashSet vanillaTextures_delegate$lambda$0() {
      Collection textures = VanillaResourcePack.INSTANCE.getResourcePack().textures();
      Iterable iterable = (Iterable)textures;
      Collection destination = (Collection)(new ObjectOpenHashSet((Collection)iterable != null ? ((Collection)iterable).size() : 10));

      for(Object item : iterable) {
         Texture it = (Texture)item;
         int value = 0;
         String asMinimalString = it.key().asMinimalString();
         destination.add(StringsKt.removeSuffix(asMinimalString, (CharSequence)".png"));
      }

      return (ObjectOpenHashSet)destination;
   }

   private static final boolean convert$lambda$0(File file) {
      return file.isDirectory();
   }

   private static final boolean convert$lambda$1$0(File it) {
      return Intrinsics.areEqual(FilesKt.getExtension(it), "yml");
   }

   private static final boolean convert$lambda$2(File it) {
      return Intrinsics.areEqual(FilesKt.getExtension(it), "yml");
   }

   private static final boolean convert$lambda$7(File file) {
      return file.isDirectory() && !Intrinsics.areEqual(file.getName(), "resourcepack") && !Intrinsics.areEqual(file.getName(), "resource_pack");
   }

   private static final boolean convert$lambda$8$0(File file) {
      return file.isDirectory() && !Intrinsics.areEqual(file.getName(), "configs") && !Intrinsics.areEqual(file.getName(), "resourcepack") && !Intrinsics.areEqual(file.getName(), "resource_pack");
   }

   private static final boolean convert$lambda$9$0$0(ItemsAdderConverter itemsAdderConverter, Model it) {
      ObjectOpenHashSet vanillaModels = itemsAdderConverter.getVanillaModels();
      String asMinimalString = it.key().asMinimalString();
      return !vanillaModels.contains(StringsKt.removeSuffix(asMinimalString, (CharSequence)".json")) ? false : VanillaResourcePack.INSTANCE.getResourcePack().models().contains(it);
   }

   private static final boolean convert$lambda$9$0$1(Function1 tmp0, Object p0) {
      return (Boolean)tmp0.invoke(p0);
   }

   private static final boolean convert$lambda$9$0$2(ItemsAdderConverter itemsAdderConverter, Texture it) {
      ObjectOpenHashSet vanillaTextures = itemsAdderConverter.getVanillaTextures();
      String asMinimalString = it.key().asMinimalString();
      return !vanillaTextures.contains(StringsKt.removeSuffix(asMinimalString, (CharSequence)".png")) ? false : VanillaResourcePack.INSTANCE.getResourcePack().textures().contains(it);
   }

   private static final boolean convert$lambda$9$0$3(Function1 tmp0, Object p0) {
      return (Boolean)tmp0.invoke(p0);
   }

   private static final ConfigurationNode convert$lambda$11$5$2(Ref.ObjectRef nexoItem, CommentedConfigurationNode it) {
      try {
      ConfigurationNode element = (ConfigurationNode)nexoItem.element;
      Object[] objs = new Object[]{"itemname"};
      element = element.node(objs);
      String string = it.getString();
      return element.set(string != null ? INSTANCE.legacyToMiniMessage(string) : null);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final ConfigurationNode convert$lambda$11$5$3(Ref.ObjectRef nexoItem, CommentedConfigurationNode it) {
      try {
      ConfigurationNode element = (ConfigurationNode)nexoItem.element;
      Object[] objs = new Object[]{"lore"};
      element = element.node(objs);
      List stringListOrNull = ConfigHelpersKt.getStringListOrNull((ConfigurationNode)it);
      if (stringListOrNull != null) {
         Iterable iterable = (Iterable)stringListOrNull;
         ConfigurationNode configurationNode = element;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            String line = (String)item;
            int value = 0;
            ItemsAdderConverter itemsAdderConverter = INSTANCE;
            destination.add(itemsAdderConverter.legacyToMiniMessage(line));
         }

         stringListOrNull = (List)destination;
         element = configurationNode;
      } else {
         stringListOrNull = null;
      }

      return element.set(stringListOrNull);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final Unit convert$lambda$11$5$7(Ref.ObjectRef nexoItem, String itemId, CommentedConfigurationNode nbtNode) {
      try {
      ItemsAdderConverter itemsAdderConverter = INSTANCE;

      Object obj;
      try {
         int value2 = 0;
         obj = ResultShim.ok(JsonParser.parseString(nbtNode.getString()));
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      JsonObject jsonObject = (JsonObject)((JsonElement)(ResultShim.isFailure(obj) ? null : obj));
      if (jsonObject != null) {
         jsonObject = ((JsonElement)jsonObject).getAsJsonObject();
         if (jsonObject != null) {
            Map asMap = jsonObject.asMap();
            if (asMap != null) {
               Map nbtJson = asMap;
               ConfigurationNode configurationNode = (ConfigurationNode)nexoItem.element;
               Object[] objs = new Object[]{"PersistentData"};
               ConfigurationNode persistentData = configurationNode.node(objs);
               int value3 = 0;

               for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)nbtJson.entrySet()) {
                  int value4 = 0;
                  String key = (String)entry.getKey();
                  JsonElement element = (JsonElement)entry.getValue();
                  if (element instanceof JsonPrimitive) {
                     Pair pair;
                     if (((JsonPrimitive)element).isBoolean()) {
                        String simpleName = DataType.BOOLEAN.getComplexType().getSimpleName();
                        simpleName = simpleName.toUpperCase(Locale.ROOT);
                        pair = TuplesKt.to(simpleName, ((JsonPrimitive)element).getAsBoolean());
                     } else if (((JsonPrimitive)element).isString()) {
                        String str = DataType.STRING.getComplexType().getSimpleName();
                        str = str.toUpperCase(Locale.ROOT);
                        pair = TuplesKt.to(str, ((JsonPrimitive)element).getAsString());
                     } else {
                        if (!((JsonPrimitive)element).isNumber()) {
                           Logs.logWarn("Failed to convert NBT-Entry " + key + " in " + itemId + ", " + element, false);
                           continue;
                        }

                        String str2 = DataType.FLOAT.getComplexType().getSimpleName();
                        str2 = str2.toUpperCase(Locale.ROOT);
                        pair = TuplesKt.to(str2, ((JsonPrimitive)element).getAsFloat());
                     }

                     Pair pair2 = pair;
                     String type = (String)pair2.component1();
                     Object value = pair2.component2();
                     ConfigurationNode appendListNode = persistentData.appendListNode();
                     int value5 = 0;
                     Object[] objs2 = new Object[]{"key"};
                     appendListNode.node(objs2).set(key);
                     appendListNode.node(new Object[]{"type"}).set(type);
                     appendListNode.node(new Object[]{"value"}).set(value);
                  }
               }

               return Unit.INSTANCE;
            }
         }
      }

      return Unit.INSTANCE;
      } catch (Exception ex) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex);
      }
   }

   private static final Unit convert$lambda$11$5$8(Ref.ObjectRef nexoItem, Converter.ItemsAdderConverter iaConverter, String namespace, String id, String itemId, CommentedConfigurationNode armorNode) {
      try {
      ItemsAdderConverter itemsAdderConverter = INSTANCE;

      Object obj;
      try {
         int value = 0;
         Object[] objs = new Object[]{"slot"};
         String string = ((CommentedConfigurationNode)armorNode.node(objs)).getString();
         string = string.toUpperCase(Locale.ROOT);
         obj = ResultShim.ok(EquipmentSlot.valueOf(string));
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      EquipmentSlot equipmentSlot = (EquipmentSlot)(ResultShim.isFailure(obj) ? null : obj);
      if (equipmentSlot == null) {
         return Unit.INSTANCE;
      } else {
         EquipmentSlot slot = equipmentSlot;
         Pair pair;
         switch (ItemsAdderConverter.WhenMappings.$EnumSwitchMapping$0[slot.ordinal()]) {
            case 1 -> pair = TuplesKt.to("CHAINMAIL_HELMET", "helmet");
            case 2 -> pair = TuplesKt.to("CHAINMAIL_CHESTPLATE", "chestplate");
            case 3 -> pair = TuplesKt.to("CHAINMAIL_LEGGINGS", "leggings");
            case 4 -> pair = TuplesKt.to("CHAINMAIL_BOOTS", "boots");
            default -> {
               return Unit.INSTANCE;
            }
         }

         Pair pair2 = pair;
         String material = (String)pair2.component1();
         String suffix = (String)pair2.component2();
         ConfigurationNode element = (ConfigurationNode)nexoItem.element;
         String prefix = null;
         element.node(new Object[]{"material"}).set(material);
         String newId = String.valueOf(((ConfigurationNode)nexoItem.element).key());
         CharSequence charSequence = (CharSequence)newId;
         Appendable destination = (Appendable)(new StringBuilder());
         int index = 0;

         for(int i = charSequence.length(); index < i; ++index) {
            char ch = charSequence.charAt(index);
            int value2 = 0;
            if (Key.allowedInValue(ch)) {
               destination.append(ch);
            }
         }

         prefix = StringsKt.removeSuffix(StringsKt.substringBefore(((StringBuilder)destination).toString(), suffix, ((StringBuilder)destination).toString()), (CharSequence)"_");
         String it = prefix + "_" + suffix;
         String str = !Intrinsics.areEqual(it, itemId) ? it : null;
         if (str == null) {
            return Unit.INSTANCE;
         } else {
            newId = str;
            iaConverter.getChangedItemIds().put(namespace + ":" + id, newId);
            Logs.logWarn("Found malformed CustomArmor Item-ID <i>" + id + "</i>, remapping to <i>" + newId, false);
            ConfigurationNode configurationNode = ConfigHelpersKt.renameNode((ConfigurationNode)nexoItem.element, newId);
            if (configurationNode == null) {
               configurationNode = (ConfigurationNode)nexoItem.element;
            }

            nexoItem.element = configurationNode;
            return Unit.INSTANCE;
         }
      }
      } catch (Exception ex) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex);
      }
   }

   private static final CharSequence convert$lambda$11$5$10$4$0$0(CommentedConfigurationNode it) {
      return (CharSequence)StringsKt.removeSuffix(String.valueOf(it.getDouble()), (CharSequence)".0");
   }

   private static final ConfigurationNode convert$lambda$11$5$10$4$0(ConfigurationNode furnitureNode, CommentedConfigurationNode it) {
      try {
      Object[] objs = new Object[]{"properties", "translation"};
      return furnitureNode.node(objs).set(CollectionsKt.joinToString((Iterable)it.childrenMap().values(), (CharSequence)",", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", ItemsAdderConverter::convert$lambda$11$5$10$4$0$0));
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final CharSequence convert$lambda$11$5$10$4$1$0(CommentedConfigurationNode iaFurniture, CommentedConfigurationNode it) {
      Object[] objs = new Object[]{"entity"};
      return (CharSequence)StringsKt.removeSuffix(String.valueOf(it.getDouble(Intrinsics.areEqual(((CommentedConfigurationNode)iaFurniture.node(objs)).getString(), "ITEM_FRAME") ? (double)0.5F : (double)1.0F)), (CharSequence)".0");
   }

   private static final ConfigurationNode convert$lambda$11$5$10$4$1(ConfigurationNode furnitureNode, CommentedConfigurationNode iaFurniture, CommentedConfigurationNode it) {
      try {
      Object[] objs = new Object[]{"properties", "scale"};
      return furnitureNode.node(objs).set(CollectionsKt.joinToString((Iterable)it.childrenMap().values(), (CharSequence)",", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (a0_ -> ItemsAdderConverter.convert$lambda$11$5$10$4$1$0(iaFurniture, (CommentedConfigurationNode)a0_))));
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final ConfigurationNode convert$lambda$11$5$10$4$4(CommentedConfigurationNode iaFurniture, ConfigurationNode furnitureNode, CommentedConfigurationNode it) {
      try {
      Object[] objs = new Object[]{"hitbox", "height"};
      double value = ((CommentedConfigurationNode)iaFurniture.node(objs)).getDouble();
      double lightOffset = (new BigDecimal(value - ((CommentedConfigurationNode)iaFurniture.node(new Object[]{"hitbox", "height_offset"})).getDouble())).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
      return furnitureNode.node(new Object[]{"lights"}).set(CollectionsKt.listOf("0," + lightOffset + ",0 " + RangesKt.coerceIn(it.getInt(), 0, 15)));
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final ConfigurationNode convert$lambda$11$5$10$4$5(ConfigurationNode furnitureNode, CommentedConfigurationNode it) {
      try {
      Object[] objs = new Object[]{"sit_height"};
      double sitHeight = (new BigDecimal(((CommentedConfigurationNode)it.node(objs)).getDouble() - 0.6)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
      return furnitureNode.node(new Object[]{"seats"}).set(CollectionsKt.listOf("0," + sitHeight + ",0"));
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final Unit convert$lambda$11$5$11$0(String namespace, ConfigurationNode packNode, CommentedConfigurationNode it) {
      try {
      String string = it.getString();
      String it_8 = StringsKt.contains((CharSequence)string, (CharSequence)":", false) ? it.getString() : namespace + ":" + it.getString();
      if (it_8 != null) {
         int value = 0;
         String str = !INSTANCE.getVanillaModels().contains(it_8) ? it_8 : null;
         if (str != null) {
            String str2 = StringsKt.removePrefix(str, (CharSequence)"minecraft:");
            if (str2 != null) {
               String it_7 = StringsKt.removeSuffix(str2, (CharSequence)".json");
               if (it_7 != null) {
                  Object[] it_6 = new Object[]{"model"};
                  packNode.node(it_6).set(it_6);
                  return Unit.INSTANCE;
               }
            }
         }
      }

      return Unit.INSTANCE;
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final Unit convert$lambda$11$5$11$1(ConfigurationNode packNode, String namespace, CommentedConfigurationNode it) {
      try {
      List list = it.getList(String.class);
      Iterable iterable = (Iterable)list;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         String it_5 = (String)item;
         int value = 0;
         int value2 = 0;
         String str = StringsKt.contains((CharSequence)it_5, (CharSequence)":", false) ? it_5 : null;
         if (str == null) {
            str = namespace + ":" + it_5;
         }

         destination.add(StringsKt.removeSuffix(StringsKt.removePrefix(str, (CharSequence)"minecraft:"), (CharSequence)".png"));
      }

      iterable = (Iterable)((List)destination);
      ObjectOpenHashSet vanillaTextures = INSTANCE.getVanillaTextures();
      Collection collection = (Collection)(new ArrayList());

      for(Object element : iterable) {
         String p0 = (String)element;
         int value3 = 0;
         if (!vanillaTextures.contains(p0)) {
            collection.add(element);
         }
      }

      List textures = (List)collection;
      if (textures.isEmpty()) {
         return Unit.INSTANCE;
      } else {
         if (textures.size() == 1) {
            Object[] objs = new Object[]{"texture"};
            packNode.node(objs).set(CollectionsKt.first(textures));
         } else {
            Object[] objs2 = new Object[]{"textures"};
            packNode.node(objs2).set(textures);
         }

         return Unit.INSTANCE;
      }
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final ConfigurationNode convert$lambda$11$5$11$2(ConfigurationNode packNode, CommentedConfigurationNode it) {
      try {
      Object[] objs = new Object[]{"custom_model_data"};
      return packNode.node(objs).set(it.getInt());
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final ConfigurationNode convert$lambda$11$5$11$3(ConfigurationNode packNode, CommentedConfigurationNode it) {
      try {
      Object[] objs = new Object[]{"custom_model_data"};
      return packNode.node(objs).set(it.getInt());
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final Pair convert$lambda$11$5$11$6(Object2ObjectOpenHashMap armorLayerPaths, ConfigurationNode packNode, CommentedConfigurationNode it) {
      try {
      Pair pair = (Pair)armorLayerPaths.get(it.getString());
      if (pair != null) {
         Pair pair2 = pair;
         int value = 0;
         String layer1 = (String)pair2.component1();
         String layer2 = (String)pair2.component2();
         Object[] objs = new Object[]{"CustomArmor", "layer1"};
         packNode.node(objs).set(layer1);
         packNode.node(new Object[]{"CustomArmor", "layer2"}).set(layer2);
         pair = pair2;
      } else {
         pair = null;
      }

      return pair;
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final Pair convert$lambda$11$5$11$7(Object2ObjectOpenHashMap armorLayerPaths, ConfigurationNode packNode, CommentedConfigurationNode it) {
      try {
      Pair pair = (Pair)armorLayerPaths.get(it.getString());
      if (pair != null) {
         Pair pair2 = pair;
         int value = 0;
         String layer1 = (String)pair2.component1();
         String layer2 = (String)pair2.component2();
         Object[] objs = new Object[]{"CustomArmor", "layer1"};
         packNode.node(objs).set(layer1);
         packNode.node(new Object[]{"CustomArmor", "layer2"}).set(layer2);
         pair = pair2;
      } else {
         pair = null;
      }

      return pair;
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final List convert$lambda$11$8$0(CommentedConfigurationNode node, String str, List nodes) {
      List list = nodes;
      if (nodes == null) {
         list = CollectionsKt.emptyList();
      }

      return CollectionsKt.plus((Collection)list, node);
   }

   private static final List convert$lambda$11$8$1(Function2 tmp0, Object p0, Object p1) {
      return (List)tmp0.invoke(p0, p1);
   }

   private static final ConfigurationNode convert$lambda$13$0$0$0(ConfigurationNode it) {
      try {
      List list = it.getList(String.class);
      if (list != null) {
         Iterable iterable = (Iterable)list;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            String row = (String)item;
            int value = 0;
            destination.add(StringsKt.replace(row, "X", " ", false));
         }

         list = (List)destination;
      } else {
         list = null;
      }

      List pattern = list;
      return it.set(pattern);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final Unit convert$lambda$13$0$0$1(Converter.ItemsAdderConverter iaConverter, ConfigurationNode it) {
      try {
      String string = ConfigHelpersKt.removeChildNode(it, "item").getString();
      if (string == null) {
         return Unit.INSTANCE;
      } else {
         String itemValue = string;
         Material material = Material.matchMaterial(itemValue);
         if (material != null) {
            Object[] objs = new Object[]{"minecraft_type"};
            it.node(objs).set(material.name());
         } else {
            Object[] objs2 = new Object[]{"nexo_item"};
            ConfigurationNode configurationNode = it.node(objs2);
            String str = (String)iaConverter.getChangedItemIds().get(itemValue);
            if (str == null) {
               str = itemValue;
            }

            configurationNode.set(StringsKt.substringAfter(str, ":", str));
         }

         return Unit.INSTANCE;
      }
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final Unit convert$lambda$13$0$0$3(ConfigurationNode recipeNode, Converter.ItemsAdderConverter iaConverter, ConfigurationNode ingredient) {
      try {
      String string = ingredient.getString();
      if (string == null) {
         Object[] objs = new Object[]{"item"};
         string = ingredient.node(objs).getString();
         if (string == null) {
            return Unit.INSTANCE;
         }
      }

      String ingredientString = string;
      recipeNode.removeChild("ingredient");
      Object[] objs2 = new Object[]{"input"};
      recipeNode.node(objs2).set(convert$ingredientMap(iaConverter, ingredientString));
      return Unit.INSTANCE;
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final ConfigurationNode convertPlaceableOn$lambda$0(ConfigurationNode limitedPlacing, ConfigurationNode it) {
      try {
      Object[] objs = new Object[]{"roof"};
      return limitedPlacing.node(objs).set(it);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final ConfigurationNode convertPlaceableOn$lambda$1(ConfigurationNode limitedPlacing, ConfigurationNode it) {
      try {
      Object[] objs = new Object[]{"wall"};
      return limitedPlacing.node(objs).set(it);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final ConfigurationNode convertPlaceableOn$lambda$2(ConfigurationNode limitedPlacing, ConfigurationNode it) {
      try {
      Object[] objs = new Object[]{"floor"};
      return limitedPlacing.node(objs).set(it);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final Unit convertAttributeModifiers$lambda$0(ConfigurationNode nexoItem, ConfigurationNode attributeModifiersNode, ConfigurationNode configurationNode, ConfigurationNode configurationNode2) {
      try {
      java.lang.String str;
      Map map = null;
      ConfigurationNode nexoAttributesNode = nexoItem.node(new Object[]{"AttributeModifiers"});
      Map childrenMap = attributeModifiersNode.childrenMap();
      map = childrenMap;
      int value = 0;

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value2 = 0;
         Object slotKey = element.getKey();
         ConfigurationNode attributesNode = (ConfigurationNode)element.getValue();
         childrenMap = attributesNode.childrenMap();
         Map map2 = childrenMap;

         for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
            Object attributeKey;
            ConfigurationNode valueNode;
            block: {
               int value3 = 0;
               attributeKey = entry.getKey();
               valueNode = (ConfigurationNode)entry.getValue();
               AttributeWrapper.Companion companion = AttributeWrapper.Companion;
               CharSequence charSequence = (CharSequence)attributeKey.toString();
               Regex regex = new Regex("([a-z])([A-Z])");
               String str2 = "$1_$2";
               String str3 = regex.replace(charSequence, str2).toUpperCase(Locale.ROOT);
               Attribute attribute = companion.fromString(str3);
               if (attribute != null) {
                  Key key = attribute.key();
                  if (key != null) {
                     String str4 = key.value();
                     if (str4 != null) {
                        str = str4.toUpperCase(Locale.ROOT);
                        break block;
                     }
                  }
               }

               str = null;
            }

            String attributeName = str;
            if (attributeName == null) {
               Logs.logWarn("Failed to get attribute for " + attributeKey + " in item " + configurationNode.key(), false);
            } else {
               ConfigurationNode appendListNode = nexoAttributesNode.appendListNode();
               int value4 = 0;
               Object[] objs = new Object[]{"amount"};
               appendListNode.node(objs).set(valueNode.getDouble());
               appendListNode.node(new Object[]{"attribute"}).set(attributeName);
               appendListNode.node(new Object[]{"operation"}).set("ADD_NUMBER");
               ConfigurationNode configurationNode3 = appendListNode.node(new Object[]{"slot"});
               String str5 = slotKey.toString().toUpperCase(Locale.ROOT);
               configurationNode3.set(str5);
            }
         }
      }

      return Unit.INSTANCE;
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   static {
      File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
      nexoFolder = dataFolder;
      dataFolder = nexoFolder;
      String[] strs = new String[]{"converter", "ItemsAdder"};
      iaFolder = FileUtilsKt.resolve(dataFolder, strs);
      vanillaModels$delegate = LazyKt.lazy(ItemsAdderConverter::vanillaModels_delegate$lambda$0);
      vanillaTextures$delegate = LazyKt.lazy(ItemsAdderConverter::vanillaTextures_delegate$lambda$0);
      AMPERSAND_CODE_REGEX = new Regex("&(#[0-9a-fA-F]{6}|[0-9a-fk-orA-FK-ORxX])");
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[org.bukkit.inventory.EquipmentSlot.values().length];
         try { a_0[org.bukkit.inventory.EquipmentSlot.HEAD.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.inventory.EquipmentSlot.CHEST.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.inventory.EquipmentSlot.LEGS.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[org.bukkit.inventory.EquipmentSlot.FEET.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
