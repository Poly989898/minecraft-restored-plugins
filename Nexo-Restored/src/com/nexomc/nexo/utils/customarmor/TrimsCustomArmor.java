package com.nexomc.nexo.utils.customarmor;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.items.customarmor.CustomArmorTextures;
import com.nexomc.nexo.utils.JsonBuilder;
import com.nexomc.nexo.utils.NexoDatapack;
import com.nexomc.nexo.utils.RegistryUtilsKt;
import com.nexomc.nexo.utils.ResourcePackUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.registry.RegistryKey;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.atlas.PalettedPermutationsAtlasSource;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.texture.Texture;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u001e\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u0002J\u0010\u0010\f\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0002J\b\u0010\r\u001a\u00020\u0005H\u0002J\b\u0010\u000e\u001a\u00020\u0005H\u0002J\u0016\u0010\u000f\u001a\u00020\u00052\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u0002¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/utils/customarmor/TrimsCustomArmor;", "Lcom/nexomc/nexo/utils/NexoDatapack;", "<init>", "()V", "generateTrimAssets", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "writeTrimAtlas", "armorPrefixes", "Lit/unimi/dsi/fastutil/objects/ObjectLinkedOpenHashSet;", "", "copyArmorLayerTextures", "parseNexoArmorItems", "writeVanillaTrimPattern", "writeCustomTrimPatterns", "Companion", "com.nexomc:core"}
)
public final class TrimsCustomArmor extends NexoDatapack {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private static final Key paletteKey;
   @NotNull
   private static final Map<String, Key> permutations;

   public TrimsCustomArmor() {
      super("nexo_custom_armor", "Datapack for Nexos Custom Armor trims");
   }

   public final void generateTrimAssets(@NotNull ResourcePack resourcePack) {
      Map entries = NexoItems.entries();
      Collection destination = (Collection)(new ObjectLinkedOpenHashSet());

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)entries.entrySet()) {
         int value = 0;
         int value2 = 0;
         String itemId = (String)element.getKey();
         ItemBuilder builder = (ItemBuilder)element.getValue();
         String str = StringsKt.substringBeforeLast(itemId, "_", itemId);
         int value3 = 0;
         String str2 = builder.getNexoMeta().getCustomArmorTextures() != null ? str : null;
         if (str2 != null) {
            Object obj = str2;
            int value4 = 0;
            destination.add(obj);
         }
      }

      ObjectLinkedOpenHashSet armorPrefixes = (ObjectLinkedOpenHashSet)destination;
      this.writeMCMeta();
      this.writeVanillaTrimPattern();
      this.writeCustomTrimPatterns(armorPrefixes);
      this.writeTrimAtlas(resourcePack, armorPrefixes);
      TrimsCustomArmor trimsCustomArmor = this;

      Object result1;
      try {
         TrimsCustomArmor trimsCustomArmor2 = trimsCustomArmor;
         trimsCustomArmor2.copyArmorLayerTextures(resourcePack);
         result1 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         result1 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(result1);
      if (throwable != null) {
         Throwable it = throwable;
         int value5 = 0;
         Logs.logError("Failed to copy armor-layer textures", false);
         if (Settings.DEBUG.toBool()) {
            Logs.logError(ExceptionsKt.stackTraceToString(it), false);
         }
      }

      if (this.isFirstInstall()) {
         Logs.logError("Nexos's Custom-Armor datapack could not be found...", false);
         Logs.logWarn("The first time CustomArmor is enabled in settings.yml", false);
         Logs.logWarn("you need to restart your server so that the DataPack is enabled...", false);
         Logs.logWarn("Custom-Armor will not work, please restart your server once!", true);
      } else if (!this.getDatapackEnabled()) {
         Logs.logError("Nexos's Custom-Armor datapack is not enabled...", false);
         Logs.logWarn("Custom-Armor will not work, please restart your server!", true);
      } else {
         this.parseNexoArmorItems();
      }

      this.enableDatapack$com_nexomc_core(true, true);
   }

   private final void writeTrimAtlas(ResourcePack resourcePack, ObjectLinkedOpenHashSet<String> armorPrefixes) {
      Atlas trimsAtlas = resourcePack.atlas(Key.key("armor_trims"));
      if (trimsAtlas != null) {
         ArrayList sources = new ArrayList((Collection)trimsAtlas.sources());
         Iterable iterable = (Iterable)trimsAtlas.sources();

         for(Object element : iterable) {
            AtlasSource source = (AtlasSource)element;
            int value = 0;
            if (source instanceof PalettedPermutationsAtlasSource) {
               List textures = CollectionsKt.toMutableList((Collection)((PalettedPermutationsAtlasSource)source).textures());
               Iterable iterable2 = (Iterable)armorPrefixes;

               for(Object obj : iterable2) {
                  String armorPrefix = (String)obj;
                  int value2 = 0;
                  ((Collection)textures).add(Key.key("nexo:trims/entity/humanoid/" + armorPrefix));
                  ((Collection)textures).add(Key.key("nexo:trims/models/armor/" + armorPrefix));
                  ((Collection)textures).add(Key.key("nexo:trims/entity/humanoid_leggings/" + armorPrefix));
                  ((Collection)textures).add(Key.key("nexo:trims/models/armor/" + armorPrefix + "_leggings"));
               }

               ((Collection)textures).add(Key.key("minecraft:trims/entity/humanoid/chainmail"));
               ((Collection)textures).add(Key.key("minecraft:trims/models/armor/chainmail"));
               ((Collection)textures).add(Key.key("minecraft:trims/entity/humanoid_leggings/chainmail"));
               ((Collection)textures).add(Key.key("minecraft:trims/models/armor/chainmail_leggings"));
               sources.remove(source);
               ((Collection)sources).add(AtlasSource.palettedPermutations(textures, ((PalettedPermutationsAtlasSource)source).paletteKey(), ((PalettedPermutationsAtlasSource)source).permutations()));
            }
         }

         resourcePack.atlas(trimsAtlas.toBuilder().sources((List)sources).build());
      } else {
         List permutations = CollectionsKt.createListBuilder();
         List list = permutations;
         int value3 = 0;
         Iterable iterable3 = (Iterable)armorPrefixes;

         for(Object obj2 : iterable3) {
            String armorPrefix = (String)obj2;
            int value4 = 0;
            list.add(Key.key("nexo:trims/entity/humanoid/" + armorPrefix));
            list.add(Key.key("nexo:trims/models/armor/" + armorPrefix));
            list.add(Key.key("nexo:trims/entity/humanoid_leggings/" + armorPrefix));
            list.add(Key.key("nexo:trims/models/armor/" + armorPrefix + "_leggings"));
         }

         list.add(Key.key("minecraft:trims/entity/humanoid/chainmail"));
         list.add(Key.key("minecraft:trims/models/armor/chainmail"));
         list.add(Key.key("minecraft:trims/entity/humanoid_leggings/chainmail"));
         list.add(Key.key("minecraft:trims/models/armor/chainmail_leggings"));
         List textures = CollectionsKt.build(permutations);
         PalettedPermutationsAtlasSource palettedPermutationsAtlasSource = AtlasSource.palettedPermutations(textures, paletteKey, TrimsCustomArmor.permutations);
         PalettedPermutationsAtlasSource permutations_10 = palettedPermutationsAtlasSource;
         resourcePack.atlas(Atlas.atlas(Key.key("armor_trims"), permutations_10));
      }

   }

   private final void copyArmorLayerTextures(ResourcePack resourcePack) {
      try {
      team.unnamed.creative.texture.Texture texture;
      java.lang.String str;
      team.unnamed.creative.base.Writable writable2;
      java.lang.String str2;
      team.unnamed.creative.texture.Texture texture2;
      Sequence sequence = SequencesKt.distinctBy(SequencesKt.filter(CollectionsKt.asSequence((Iterable)NexoItems.entries().entrySet()), TrimsCustomArmor::copyArmorLayerTextures$lambda$0), TrimsCustomArmor::copyArmorLayerTextures$lambda$1);

      for(Object element : kotlin.sequences.SequencesKt.asIterable(sequence)) {
         Map.Entry it = (Map.Entry)element;
         int value = 0;
         String itemId = (String)it.getKey();
         ItemBuilder item = (ItemBuilder)it.getValue();
         CustomArmorTextures customArmorTextures = item.getNexoMeta().getCustomArmorTextures();
         if (customArmorTextures != null) {
            CustomArmorTextures customArmor;
            String armorPrefix;
            block: {
               customArmor = customArmorTextures;
               armorPrefix = StringsKt.substringBeforeLast(itemId, "_", itemId);
               Key key = customArmor.getLayer1();
               if (key != null) {
                  Key it_9 = key;
                  int value2 = 0;
                  texture = resourcePack.texture(it_9);
                  if (texture != null) {
                     break block;
                  }
               }

               Collection textures = resourcePack.textures();
               Iterable it_8 = (Iterable)textures;
               Iterator iterator = it_8.iterator();

               Object obj;
               while(true) {
                  if (!iterator.hasNext()) {
                     obj = null;
                     break;
                  }

                  Object obj2 = iterator.next();
                  Texture it_7 = (Texture)obj2;
                  int value3 = 0;
                  String str3 = it_7.key().value();
                  if (Intrinsics.areEqual(StringsKt.substringAfterLast(str3, "/", str3), armorPrefix + "_armor_layer_1.png")) {
                     obj = obj2;
                     break;
                  }
               }

               texture = (Texture)obj;
               if (texture == null) {
                  block2: {
                     Key key2 = customArmor.getLayer1();
                     if (key2 != null) {
                        str2 = key2.asString();
                        if (str2 != null) {
                           break block2;
                        }
                     }

                     str2 = "layer1";
                  }

                  Logs.logWarn("Failed to fetch " + str2 + " used by " + itemId, false);
                  continue;
               }
            }

            Texture layer1;
            block: {
               layer1 = texture;
               Key key3 = customArmor.getLayer2();
               if (key3 != null) {
                  Key it_6 = key3;
                  int value4 = 0;
                  texture2 = resourcePack.texture(it_6);
                  if (texture2 != null) {
                     break block;
                  }
               }

               Collection collection = resourcePack.textures();
               Iterable iterable = (Iterable)collection;
               Iterator iterator2 = iterable.iterator();

               Object obj3;
               while(true) {
                  if (!iterator2.hasNext()) {
                     obj3 = null;
                     break;
                  }

                  Object obj4 = iterator2.next();
                  Texture it_5 = (Texture)obj4;
                  int value5 = 0;
                  String str4 = it_5.key().value();
                  if (Intrinsics.areEqual(StringsKt.substringAfterLast(str4, "/", str4), armorPrefix + "_armor_layer_2.png")) {
                     obj3 = obj4;
                     break;
                  }
               }

               texture2 = (Texture)obj3;
               if (texture2 == null) {
                  block2: {
                     Key key4 = customArmor.getLayer2();
                     if (key4 != null) {
                        str = key4.asString();
                        if (str != null) {
                           break block2;
                        }
                     }

                     str = "layer2";
                  }

                  Logs.logWarn("Failed to fetch " + str + " used by " + itemId, false);
                  continue;
               }
            }

            Texture layer2 = texture2;
            resourcePack.removeTexture(layer1.key());
            resourcePack.removeTexture(layer2.key());
            resourcePack.texture(Key.key("nexo:trims/entity/humanoid/" + armorPrefix + ".png"), layer1.data());
            resourcePack.texture(Key.key("nexo:trims/models/armor/" + armorPrefix + ".png"), layer1.data());
            resourcePack.texture(Key.key("nexo:trims/entity/humanoid_leggings/" + armorPrefix + ".png"), layer2.data());
            resourcePack.texture(Key.key("nexo:trims/models/armor/" + armorPrefix + "_leggings.png"), layer2.data());
         }
      }

      String it = "minecraft:entity/equipment/humanoid%s/chainmail.png";
      int value6 = 0;
      String str5 = String.format(it, "");
      Key key5 = Key.key(str5);
      String str6 = String.format(it, "_leggings");
      Pair pair = TuplesKt.to(key5, Key.key(str6));
      Object component1 = pair.component1();
      Key first = (Key)component1;
      component1 = pair.component2();
      Key second = (Key)component1;
      Texture texture3 = ResourcePackUtilsKt.textureOrVanilla(resourcePack, first);
      if (texture3 != null) {
         Texture it_3 = texture3;
         int value7 = 0;
         resourcePack.texture(Key.key("trims/entity/humanoid/chainmail.png"), it_3.data());
         resourcePack.texture(Key.key("trims/models/armor/chainmail.png"), it_3.data());
         texture3 = it_3;
      } else {
         texture3 = null;
      }

      Texture texture4 = ResourcePackUtilsKt.textureOrVanilla(resourcePack, second);
      if (texture4 != null) {
         Texture it_2 = texture4;
         Texture texture5 = texture3;
         int value8 = 0;
         resourcePack.texture(Key.key("trims/entity/humanoid_leggings/chainmail.png"), it_2.data());
         resourcePack.texture(Key.key("trims/models/armor/chainmail_leggings.png"), it_2.data());
         texture3 = texture5;
         texture4 = it_2;
      } else {
         texture4 = null;
      }

      block: {
         TuplesKt.to(texture3, texture4);
         InputStream resource = NexoPlugin.Companion.instance().getResource("custom_armor/transparent.png");
         if (resource != null) {
            InputStream p0 = resource;
            int value9 = 0;
            writable2 = Writable.copyInputStream(p0);
            if (writable2 != null) {
               break block;
            }
         }

         writable2 = Writable.EMPTY;
      }

      Writable writable = writable2;
      String layerPattern = "minecraft:models/armor/chainmail_layer_%s.png";
      first = Key.key(String.format(layerPattern, "1"));
      second = Key.key(String.format(layerPattern, "2"));
      resourcePack.texture(first, writable);
      resourcePack.texture(second, writable);
      String equipmentPattern = "minecraft:entity/equipment/humanoid%s/chainmail.png";
      first = Key.key(String.format(equipmentPattern, ""));
      second = Key.key(String.format(equipmentPattern, "_leggings"));
      resourcePack.texture(first, writable);
      resourcePack.texture(second, writable);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private final void parseNexoArmorItems() {
      List skippedArmorType = (List)(new ArrayList());
      Iterable items = (Iterable)NexoItems.items();

      for(Object element : items) {
         ItemBuilder itemBuilder = (ItemBuilder)element;
         int value = 0;
         String itemID = NexoItems.idFromItem(itemBuilder);
         ItemStack itemStack = itemBuilder.build();
         String armorPrefix = StringUtils.substringBeforeLast(itemID, "_");
         boolean changed = false;
         if (!skippedArmorType.contains(armorPrefix) && Tag.ITEMS_TRIMMABLE_ARMOR.isTagged(itemBuilder.getType()) && itemStack.hasData((DataComponentType)DataComponentTypes.EQUIPPABLE) && StringsKt.startsWith(itemStack.getType().name(), "CHAINMAIL", false)) {
            if (!itemBuilder.hiddenComponentKeys().contains(DataComponentTypes.TRIM.key())) {
               Logs.logWarn("Item " + itemID + " does not hide the trim-component in its tooltip.", false);
               if (Settings.CUSTOM_ARMOR_ASSIGN.toBool()) {
                  Key[] keys = new Key[1];
                  Key key = DataComponentTypes.TRIM.key();
                  keys[0] = key;
                  itemBuilder.hideComponents(keys);
                  changed = true;
                  if (Settings.DEBUG.toBool()) {
                     Logs.logInfo("Hid trim-component tooltip for " + itemID, true);
                  }
               } else {
                  Logs.logWarn("Custom Armors are recommended to hide the trim-component in their tooltip.", true);
               }
            }

            Equippable equippable = itemBuilder.getEquippable();
            if ((equippable != null ? equippable.assetId() : null) != null) {
               Logs.logWarn("Item " + itemID + " is using an equippable-component with model", false);
               Logs.logWarn("CustomArmor is set to be done via TRIMS, this will break it", false);
               Logs.logWarn("Recommend swapping to COMPONENT-type or remove model-field from item", false);
            }

            if (!itemBuilder.hasTrimPattern()) {
               Registry registry = Registry.TRIM_PATTERN;
               NamespacedKey namespacedKey = NamespacedKey.fromString("nexo:" + armorPrefix);
               TrimPattern trimPattern = (TrimPattern)registry.get(namespacedKey);
               if (trimPattern == null) {
                  Logs.logError("Could not get trim-pattern for " + itemID + ": nexo:" + armorPrefix, false);
                  Logs.logWarn("Ensure that the  DataPack is enabled `/datapack list` and restart your server", false);
                  ((Collection)skippedArmorType).add(armorPrefix);
               } else if (!Settings.CUSTOM_ARMOR_ASSIGN.toBool()) {
                  Logs.logWarn("Item " + itemID + " does not have a trim pattern set.", false);
                  Logs.logWarn("Custom Armor will not work unless a trim pattern is set.", true);
                  ((Collection)skippedArmorType).add(armorPrefix);
               } else {
                  RegistryKey registryKey = RegistryKey.TRIM_PATTERN;
                  NamespacedKey namespacedKey2 = RegistryUtilsKt.toRegistry(registryKey).getKey((Keyed)trimPattern);
                  if (namespacedKey2 == null) {
                     continue;
                  }

                  NamespacedKey trimKey = namespacedKey2;
                  itemBuilder.setTrimPattern((Key)trimKey);
                  changed = true;
                  Logs.logWarn("Item " + itemID + " does not have a trim pattern set.", false);
                  Logs.logInfo("Assigned trim pattern " + trimKey.asString() + " to " + itemID, true);
               }
            }

            if (changed) {
               itemBuilder.save();
            }
         }
      }

   }

   private final void writeVanillaTrimPattern() {
      File file = FilesKt.resolve(this.getDatapackFile(), "data/minecraft/trim_pattern/chainmail.json");
      int value = 0;
      file.getParentFile().mkdirs();
      String str = JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "description", JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "translate", "trim_pattern.minecraft.chainmail")), "asset_id", "minecraft:chainmail"), "template_item", "minecraft:debug_stick").toString();
      FilesKt.writeText(file, str, kotlin.text.Charsets.UTF_8);
   }

   private final void writeCustomTrimPatterns(ObjectLinkedOpenHashSet<String> armorPrefixes) {
      Iterable iterable = (Iterable)armorPrefixes;

      for(Object element : iterable) {
         String armorPrefix = (String)element;
         int value = 0;
         File file = FilesKt.resolve(this.getDatapackFile(), "data/nexo/trim_pattern/" + armorPrefix + ".json");
         int value2 = 0;
         file.getParentFile().mkdirs();
         String str = JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "description", JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "translate", "trim_pattern.nexo." + armorPrefix)), "asset_id", "nexo:" + armorPrefix), "template_item", "minecraft:debug_stick").toString();
         FilesKt.writeText(file, str, kotlin.text.Charsets.UTF_8);
      }

   }

   private static final boolean copyArmorLayerTextures$lambda$0(Map.Entry it) {
      return ((ItemBuilder)it.getValue()).getNexoMeta().getCustomArmorTextures() != null;
   }

   private static final String copyArmorLayerTextures$lambda$1(Map.Entry it) {
      return StringsKt.substringBeforeLast((String)it.getKey(), "_", (String)it.getKey());
   }

   static {
      Key key = Key.key("trims/color_palettes/trim_palette");
      paletteKey = key;
      Map createMapBuilder = MapsKt.createMapBuilder();
      int value = 0;
      createMapBuilder.put("quartz", Key.key("trims/color_palettes/quartz"));
      createMapBuilder.put("iron", Key.key("trims/color_palettes/iron"));
      createMapBuilder.put("gold", Key.key("trims/color_palettes/gold"));
      createMapBuilder.put("diamond", Key.key("trims/color_palettes/diamond"));
      createMapBuilder.put("netherite", Key.key("trims/color_palettes/netherite"));
      createMapBuilder.put("redstone", Key.key("trims/color_palettes/redstone"));
      createMapBuilder.put("copper", Key.key("trims/color_palettes/copper"));
      createMapBuilder.put("emerald", Key.key("trims/color_palettes/emerald"));
      createMapBuilder.put("lapis", Key.key("trims/color_palettes/lapis"));
      createMapBuilder.put("amethyst", Key.key("trims/color_palettes/amethyst"));
      permutations = MapsKt.build(createMapBuilder);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R$\u0010\u0006\u001a\u0018\u0012\u0004\u0012\u00020\b\u0012\u000e\u0012\f0\u0005¢\u0006\u0002\b\t¢\u0006\u0002\b\n0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"},
      d2 = {"Lcom/nexomc/nexo/utils/customarmor/TrimsCustomArmor$Companion;", "", "<init>", "()V", "paletteKey", "Lnet/kyori/adventure/key/Key;", "permutations", "", "", "Lorg/jetbrains/annotations/NotNull;", "Lkotlin/jvm/internal/EnhancedNullability;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
