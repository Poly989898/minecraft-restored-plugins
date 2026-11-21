package com.nexomc.nexo.pack;

import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.RegistryUtilsKt;
import com.nexomc.nexo.utils.ResourcePackUtilsKt;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.logs.Logs;
import io.papermc.paper.registry.RegistryKey;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Art;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.atlas.DirectoryAtlasSource;
import team.unnamed.creative.atlas.PalettedPermutationsAtlasSource;
import team.unnamed.creative.atlas.SingleAtlasSource;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.HeadType;
import team.unnamed.creative.base.Pose;
import team.unnamed.creative.base.WoodType;
import team.unnamed.creative.blockstate.BlockState;
import team.unnamed.creative.blockstate.Condition;
import team.unnamed.creative.blockstate.MultiVariant;
import team.unnamed.creative.blockstate.Selector;
import team.unnamed.creative.blockstate.Variant;
import team.unnamed.creative.item.CompositeItemModel;
import team.unnamed.creative.item.ConditionItemModel;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.item.ItemModel;
import team.unnamed.creative.item.RangeDispatchItemModel;
import team.unnamed.creative.item.ReferenceItemModel;
import team.unnamed.creative.item.SelectItemModel;
import team.unnamed.creative.item.SpecialItemModel;
import team.unnamed.creative.item.property.ItemBooleanProperty;
import team.unnamed.creative.item.property.ItemNumericProperty;
import team.unnamed.creative.item.property.ItemStringProperty;
import team.unnamed.creative.item.special.BedSpecialRender;
import team.unnamed.creative.item.special.ChestSpecialRender;
import team.unnamed.creative.item.special.CopperGolemStatueSpecialRender;
import team.unnamed.creative.item.special.HeadSpecialRender;
import team.unnamed.creative.item.special.ShulkerBoxSpecialRender;
import team.unnamed.creative.item.special.SignSpecialRender;
import team.unnamed.creative.item.special.SpecialRender;
import team.unnamed.creative.model.Element;
import team.unnamed.creative.model.ElementFace;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.texture.Texture;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000|\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010!\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J\b\u0010\b\u001a\u00020\u0007H\u0002J\b\u0010\t\u001a\u00020\u0007H\u0002J\u000e\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\b\u0010\r\u001a\u00020\u0007H\u0002J\b\u0010\u000e\u001a\u00020\u0007H\u0002J<\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u000b2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0018H\u0002J\u001e\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u0016H\u0002J$\u0010\u001e\u001a\u00020\u00072\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00160\u000b2\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\f0!H\u0002J\f\u0010\"\u001a\u00020\u0016*\u00020\u0016H\u0002J\u0012\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015*\u00020\u0013H\u0002J\u0012\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015*\u00020\u0013H\u0002J \u0010%\u001a\u00020&*\u00020&2\u0012\u0010'\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00160(H\u0002J\u0012\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00160\u0015*\u00020*H\u0002J \u0010+\u001a\u00020**\u00020*2\u0012\u0010'\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00160(H\u0002J \u0010+\u001a\u00020,*\u00020,2\u0012\u0010'\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00160(H\u0002J \u0010-\u001a\u00020\u0013*\u00020\u00132\u0012\u0010'\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00160(H\u0002J \u0010.\u001a\u00020/*\u00020/2\u0012\u0010'\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00160(H\u0002J\u0014\u00100\u001a\u000201*\u0002012\u0006\u00102\u001a\u00020/H\u0002J\u0012\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\u000b*\u00020/H\u0002J\u0016\u00103\u001a\b\u0012\u0004\u0012\u00020\u00130\u000b2\u0006\u00104\u001a\u00020\u0016H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00065"},
   d2 = {"Lcom/nexomc/nexo/pack/AtlasGenerator;", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "<init>", "(Lteam/unnamed/creative/ResourcePack;)V", "generateAtlasFile", "", "generateLegacyAtlasSetup", "fixMissingnoTextures", "collectAllTextureSources", "", "Lteam/unnamed/creative/atlas/AtlasSource;", "generatePreModernAtlases", "generateModernAtlases", "duplicateModelsToOverlay", "overlay", "Lteam/unnamed/creative/overlay/Overlay;", "models", "Lteam/unnamed/creative/model/Model;", "overlapTextures", "", "Lnet/kyori/adventure/key/Key;", "keySuffix", "", "targetPrefix", "existingNonSingleSources", "nexoOverlay", "Lcom/nexomc/nexo/pack/NexoOverlay;", "atlasKey", "addKey", "keys", "sources", "", "removePackPrefix", "flatTextures", "resolvedFlatTextures", "remapTextures", "Lteam/unnamed/creative/model/ModelTextures;", "block", "Lkotlin/Function1;", "flatVariants", "Lteam/unnamed/creative/blockstate/BlockState;", "remapVariants", "Lteam/unnamed/creative/blockstate/MultiVariant;", "remapParent", "remapModels", "Lteam/unnamed/creative/item/ItemModel;", "clone", "Lteam/unnamed/creative/item/Item;", "itemModel", "recursiveModels", "model", "com.nexomc:core"}
)
public final class AtlasGenerator {
   @NotNull
   private final ResourcePack resourcePack;

   public AtlasGenerator(@NotNull ResourcePack resourcePack) {
      super();
      this.resourcePack = resourcePack;
   }

   public final void generateAtlasFile() {
      if (Settings.PACK_LEGACY_ATLAS.toBool()) {
         this.generateLegacyAtlasSetup();
      } else {
         if (!Settings.PACK_PREFER_ITEMMODELS.toBool()) {
            Logs.logWarn(Settings.PACK_LEGACY_ATLAS.getPath() + "is currently disabled & you seem to be using CustomModelData", false);
            Logs.logWarn("There <i>might</i> be some Models which load without textures because of this", false);
         }

         this.fixMissingnoTextures();
         this.generatePreModernAtlases();
         this.generateModernAtlases();
         this.resourcePack.removeAtlas(Atlas.BLOCKS);
      }

   }

   private final void generateLegacyAtlasSetup() {
      java.lang.Object obj;
      team.unnamed.creative.atlas.Atlas atlas2;
      ObjectArrayList sources = new ObjectArrayList();
      Collection models = this.resourcePack.models();
      Iterable iterable = (Iterable)models;

      for(Object element : iterable) {
         Model model = (Model)element;
         int value = 0;
         List layers = model.textures().layers();
         Iterable iterable2 = (Iterable)layers;
         Collection destination = (Collection)(new ObjectArrayList());

         for(Object obj5 : iterable2) {
            int value2 = 0;
            ModelTexture p0 = (ModelTexture)obj5;
            int value3 = 0;
            Key key = p0.key();
            if (key != null) {
               Object obj6 = key;
               int value4 = 0;
               destination.add(obj6);
            }
         }

         this.addKey((List)((ObjectArrayList)destination), (List)sources);
         iterable2 = (Iterable)model.textures().variables().values();
         destination = (Collection)(new ObjectArrayList());

         for(Object obj7 : iterable2) {
            int value5 = 0;
            ModelTexture p0 = (ModelTexture)obj7;
            int value6 = 0;
            Key key2 = p0.key();
            if (key2 != null) {
               Object obj8 = key2;
               int value7 = 0;
               destination.add(obj8);
            }
         }

         this.addKey((List)((ObjectArrayList)destination), (List)sources);
         ModelTexture particle = model.textures().particle();
         if (particle != null) {
            Key key3 = particle.key();
            if (key3 != null) {
               Key it = key3;
               this.addKey(CollectionsKt.listOf(it), (List)sources);
            }
         }
      }

      List list3 = (List)sources;
      if (list3.size() > 1) {
         CollectionsKt.sortWith(list3, new AtlasGenerator$generateLegacyAtlasSetup$$inlined$sortBy$1());
      }

      block: {
         Atlas atlas3 = this.resourcePack.atlas(Atlas.BLOCKS);
         if (atlas3 != null) {
            Atlas it = atlas3;
            int value8 = 0;
            atlas2 = it.toBuilder().sources(CollectionsKt.distinct((Iterable)CollectionsKt.plus((Collection)it.sources(), (Iterable)sources))).build();
            if (atlas2 != null) {
               break block;
            }
         }

         atlas2 = Atlas.atlas(Atlas.BLOCKS, CollectionsKt.distinct((Iterable)sources));
      }

      Atlas blockAtlas = atlas2;
      blockAtlas.addTo(this.resourcePack);
      if (!Settings.PACK_PREFER_ITEMMODELS.toBool()) {
         DirectoryAtlasSource directoryAtlasSource = AtlasSource.directory("item", "item/");
         DirectoryAtlasSource itemDirSource = directoryAtlasSource;
         List list4 = blockAtlas.sources();
         Iterable iterable3 = (Iterable)list4;
         int value15 = 0;
         Iterator iterator3 = iterable3.iterator();

         Object obj2;
         while(true) {
            if (!iterator3.hasNext()) {
               obj2 = null;
               break;
            }

            Object obj9 = iterator3.next();
            int value9 = 0;
            if (obj9 instanceof PalettedPermutationsAtlasSource) {
               obj2 = obj9;
               break;
            }
         }

         if (!(obj2 instanceof PalettedPermutationsAtlasSource)) {
            obj2 = null;
         }

         PalettedPermutationsAtlasSource palettedPermutationsAtlasSource = (PalettedPermutationsAtlasSource)obj2;
         if (palettedPermutationsAtlasSource == null) {
            block: {
               Atlas atlas4 = VanillaResourcePack.INSTANCE.getResourcePack().atlas(Atlas.ITEMS);
               if (atlas4 != null) {
                  List list = atlas4.sources();
                  if (list != null) {
                     List list5 = list;
                     Iterable iterable4 = (Iterable)list5;
                     Iterator iterator = iterable4.iterator();

                     Object obj3;
                     while(true) {
                        if (!iterator.hasNext()) {
                           obj3 = null;
                           break;
                        }

                        Object obj10 = iterator.next();
                        int value10 = 0;
                        if (obj10 instanceof PalettedPermutationsAtlasSource) {
                           obj3 = obj10;
                           break;
                        }
                     }

                     if (!(obj3 instanceof PalettedPermutationsAtlasSource)) {
                        obj3 = null;
                     }

                     palettedPermutationsAtlasSource = (PalettedPermutationsAtlasSource)obj3;
                     break block;
                  }
               }

               palettedPermutationsAtlasSource = null;
            }

            if (palettedPermutationsAtlasSource == null) {
               block: {
                  Atlas atlas5 = NexoOverlay.V1_21_11.overlay(this.resourcePack).atlas(Atlas.ITEMS);
                  if (atlas5 != null) {
                     List list2 = atlas5.sources();
                     if (list2 != null) {
                        List list6 = list2;
                        Iterable iterable5 = (Iterable)list6;
                        Iterator iterator2 = iterable5.iterator();

                        while(true) {
                           if (iterator2.hasNext()) {
                              Object obj11 = iterator2.next();
                              int value11 = 0;
                              if (!(obj11 instanceof PalettedPermutationsAtlasSource)) {
                                 continue;
                              }

                              obj = obj11;
                              break;
                           }

                           obj = null;
                           break;
                        }

                        if (!(obj instanceof PalettedPermutationsAtlasSource)) {
                           obj = null;
                        }

                        palettedPermutationsAtlasSource = (PalettedPermutationsAtlasSource)obj;
                        break block;
                     }
                  }

                  palettedPermutationsAtlasSource = null;
               }
            }
         }

         PalettedPermutationsAtlasSource trimsSource = palettedPermutationsAtlasSource;
         Key key4 = Atlas.ITEMS;
         AtlasSource[] blockAtlas_58 = new AtlasSource[]{itemDirSource, trimsSource};
         Atlas atlas6 = Atlas.atlas(key4, CollectionsKt.listOfNotNull(blockAtlas_58));
         Atlas itemsAtlas = atlas6;
         Collection collection = (Collection)itemsAtlas.sources();
         blockAtlas_58 = new AtlasSource[]{itemDirSource, trimsSource};
         list4 = CollectionsKt.plus(collection, (Iterable)CollectionsKt.listOfNotNull(blockAtlas_58));
         Atlas atlas7 = Atlas.atlas(Atlas.BLOCKS, list4);
         Atlas blockAtlas_57 = atlas7;
         NexoOverlay.V1_21_11.getOverlay().atlas(itemsAtlas);
         NexoOverlay.V1_21_11.getOverlay().atlas(blockAtlas_57);
      }

      if (VersionUtil.atleast("1.21.4")) {
         ResourcePack resourcePack2 = this.resourcePack;
         Key key5 = Atlas.PAINTINGS;
         Collection collection2 = (Collection)ResourcePackUtilsKt.atlasOrOverlays(resourcePack2, key5);
         Object obj4;
         if (collection2.isEmpty()) {
            int value12 = 0;
            Atlas atlas8 = VanillaResourcePack.INSTANCE.getResourcePack().atlas(Atlas.PAINTINGS);
            obj4 = atlas8 != null ? atlas8.sources() : null;
            if (obj4 == null) {
               obj4 = CollectionsKt.emptyList();
            }
         } else {
            obj4 = collection2;
         }

         List paintingSources = (List)obj4;
         Atlas atlas9 = Atlas.atlas(Atlas.PAINTINGS, paintingSources);
         Atlas paintingAtlas = atlas9;
         RegistryKey registryKey = RegistryKey.PAINTING_VARIANT;
         Iterable iterable6 = (Iterable)RegistryUtilsKt.toRegistry(registryKey);
         Collection collection3 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable6, 10)));

         for(Object item : iterable6) {
            Art art = (Art)item;
            int value16 = 0;
            KeyUtils keyUtils = KeyUtils.INSTANCE;
            key5 = art.assetId();
            collection3.add(AtlasSource.single(keyUtils.prependIfMissing(key5, "paintings/")));
         }

         iterable6 = (Iterable)((List)collection3);
         collection3 = (Collection)(new ArrayList());

         for(Object obj12 : iterable6) {
            SingleAtlasSource it = (SingleAtlasSource)obj12;
            int value13 = 0;
            if (!paintingAtlas.contains(it.resource())) {
               collection3.add(obj12);
            }
         }

         iterable6 = (Iterable)((List)collection3);
         if (CollectionsKt.count(iterable6) > 0) {
            List it = (List)iterable6;
            int value14 = 0;
            Atlas.Builder toBuilder = paintingAtlas.toBuilder();
            Atlas.Builder atlas = toBuilder;
            Iterable iterable7 = (Iterable)it;

            for(Object obj13 : iterable7) {
               AtlasSource p0 = (AtlasSource)obj13;
               int value17 = 0;
               atlas.addSource(p0);
            }

            this.resourcePack.atlas(atlas.build());
         }
      }

   }

   private final void fixMissingnoTextures() {
      Collection models = this.resourcePack.models();
      Iterable iterable = (Iterable)models;

      for(Object obj : iterable) {
         Model model = (Model)obj;
         int value = 0;
         boolean dirty = false;
         Iterable iterable2 = (Iterable)model.elements();
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

         for(Object item : iterable2) {
            Element element = (Element)item;
            int value2 = 0;
            Map faces = element.faces();
            int capacity = RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity(faces.size()), 16);
            Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(capacity);

            for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)faces.entrySet()) {
               Object key = entry.getKey();
               Object obj2 = entry.getValue();
               Map map = (Map)result;
               ElementFace face = (ElementFace)obj2;
               CubeFace cubeFace = (CubeFace)key;
               Map map2 = map;
               int value3 = 0;
               ElementFace elementFace;
               if (!Intrinsics.areEqual(face.texture(), "#missing") && !Intrinsics.areEqual(face.texture(), "#missingno")) {
                  elementFace = face;
               } else {
                  ModelTextures textures = model.textures();
                  int value4 = 0;
                  List layers = textures.layers();
                  String str = UtilsKt.takeIf("layer0", !((Collection)layers).isEmpty());
                  if (str == null) {
                     str = (String)CollectionsKt.firstOrNull((Iterable)textures.variables().keySet());
                     if (str == null) {
                        str = UtilsKt.takeIf("particle", textures.particle() != null);
                        if (str == null) {
                           str = "0";
                        }
                     }
                  }

                  String texture = "#" + str;
                  dirty = true;
                  elementFace = ElementFace.face(face.uv(), texture, face.cullFace(), face.rotation(), face.tintIndex());
               }

               ElementFace elementFace2 = elementFace;
               map2.put(key, elementFace2);
            }

            Element element2 = UtilsKt.takeUnless(element, dirty);
            if (element2 == null) {
               element2 = element.toBuilder().faces((Map)result).build();
            }

            destination.add(element2);
         }

         List elements = (List)destination;
         if (dirty) {
            this.resourcePack.model(model.toBuilder().elements(elements).build());
         }
      }

   }

   private final List<AtlasSource> collectAllTextureSources() {
      java.util.List list;
      ObjectArrayList sources = new ObjectArrayList();
      Collection models = this.resourcePack.models();
      Iterable iterable = (Iterable)models;

      for(Object element : iterable) {
         Model model = (Model)element;
         int value = 0;
         List layers = model.textures().layers();
         Iterable iterable2 = (Iterable)layers;
         Collection destination = (Collection)(new ObjectArrayList());

         for(Object obj : iterable2) {
            int value2 = 0;
            ModelTexture p0 = (ModelTexture)obj;
            int value3 = 0;
            Key key = p0.key();
            if (key != null) {
               Object obj2 = key;
               int value4 = 0;
               destination.add(obj2);
            }
         }

         this.addKey((List)((ObjectArrayList)destination), (List)sources);
         iterable2 = (Iterable)model.textures().variables().values();
         destination = (Collection)(new ObjectArrayList());

         for(Object obj3 : iterable2) {
            int value5 = 0;
            ModelTexture p0 = (ModelTexture)obj3;
            int value6 = 0;
            Key key2 = p0.key();
            if (key2 != null) {
               Object obj4 = key2;
               int value7 = 0;
               destination.add(obj4);
            }
         }

         this.addKey((List)((ObjectArrayList)destination), (List)sources);
         ModelTexture particle = model.textures().particle();
         if (particle != null) {
            Key key3 = particle.key();
            if (key3 != null) {
               Key it = key3;
               this.addKey(CollectionsKt.listOf(it), (List)sources);
            }
         }
      }

      List list3 = (List)sources;
      if (list3.size() > 1) {
         CollectionsKt.sortWith(list3, new AtlasGenerator$collectAllTextureSources$$inlined$sortBy$1());
      }

      block: {
         Atlas atlas = this.resourcePack.atlas(Atlas.BLOCKS);
         if (atlas != null) {
            List list2 = atlas.sources();
            if (list2 != null) {
               Iterable iterable3 = (Iterable)list2;
               Collection collection = (Collection)(new ArrayList());

               for(Object obj5 : iterable3) {
                  AtlasSource src = (AtlasSource)obj5;
                  int value8 = 0;
                  if (!(src instanceof SingleAtlasSource) || VanillaResourcePack.INSTANCE.getResourcePack().texture(((SingleAtlasSource)src).resource()) == null) {
                     collection.add(obj5);
                  }
               }

               list = (List)collection;
               break block;
            }
         }

         list = null;
      }

      if (list == null) {
         list = CollectionsKt.emptyList();
      }

      list3 = list;
      return CollectionsKt.distinct((Iterable)CollectionsKt.plus((Collection)list3, (Iterable)sources));
   }

   private final void generatePreModernAtlases() {
      Atlas atlas = Atlas.atlas(Atlas.BLOCKS, this.collectAllTextureSources());
      Atlas blocksAtlas = atlas;
      Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(NexoOverlay.values());
      ArrayList list = new ArrayList();

      for(Object item : iterable) {
         NexoOverlay it = (NexoOverlay)item;
         int value = 0;
         if (it == NexoOverlay.V1_21_11) {
            break;
         }

         list.add(item);
      }

      iterable = (Iterable)((List)list);

      for(Object element : iterable) {
         NexoOverlay it = (NexoOverlay)element;
         int value2 = 0;
         it.getOverlay().atlas(blocksAtlas);
      }

   }

   private final void generateModernAtlases() {
      boolean flag;
      team.unnamed.creative.atlas.PalettedPermutationsAtlasSource palettedPermutationsAtlasSource;
      Iterable iterable2 = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(NexoOverlay.values());
      boolean yielding = false;
      ArrayList arrayList = new ArrayList();

      for(Object obj4 : iterable2) {
         if (yielding) {
            arrayList.add(obj4);
         } else {
            NexoOverlay it = (NexoOverlay)obj4;
            int value = 0;
            if (it == NexoOverlay.V1_21_11) {
               arrayList.add(obj4);
               yielding = true;
            }
         }
      }

      List overlays = (List)arrayList;
      Collection items = this.resourcePack.items();
      Iterable iterable3 = (Iterable)items;
      yielding = false;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable3) {
         Item it = (Item)element;
         int value2 = 0;
         ItemModel itemModel = it.model();
         Iterable iterable4 = (Iterable)this.models(itemModel);
         CollectionsKt.addAll(destination, iterable4);
      }

      iterable3 = (Iterable)((List)destination);
      yielding = false;
      HashSet hashSet = new HashSet();
      ArrayList arrayList2 = new ArrayList();

      for(Object obj5 : iterable3) {
         Model it = (Model)obj5;
         int value3 = 0;
         Object obj6 = it.key();
         if (hashSet.add(obj6)) {
            arrayList2.add(obj5);
         }
      }

      List itemModels = (List)arrayList2;
      Iterable iterable5 = (Iterable)itemModels;
      Collection collection2 = (Collection)(new ObjectLinkedOpenHashSet());

      for(Object obj7 : iterable5) {
         Model it = (Model)obj7;
         int value4 = 0;
         Iterable iterable6 = (Iterable)this.resolvedFlatTextures(it);
         CollectionsKt.addAll(collection2, iterable6);
      }

      ObjectLinkedOpenHashSet itemTextureKeys = (ObjectLinkedOpenHashSet)collection2;
      Iterable iterable7 = (Iterable)itemTextureKeys;
      Collection collection3 = (Collection)((Set)(new LinkedHashSet()));

      for(Object obj8 : iterable7) {
         Key it = (Key)obj8;
         int value5 = 0;
         String str = it.value();
         if (StringsKt.startsWith(str, "block/", false)) {
            collection3.add(obj8);
         }
      }

      Set blockPathItemTextures = (Set)collection3;
      Iterable iterable8 = (Iterable)itemModels;
      Collection collection4 = (Collection)(new ArrayList());

      for(Object obj9 : iterable8) {
         Model model = (Model)obj9;
         int value6 = 0;
         Iterable iterable9 = (Iterable)this.resolvedFlatTextures(model);
         boolean flag2;
         if (iterable9 instanceof Collection && ((Collection)iterable9).isEmpty()) {
            flag2 = false;
         } else {
            Iterator iterator = iterable9.iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  flag2 = false;
                  break;
               }

               Object obj10 = iterator.next();
               Key it = (Key)obj10;
               int value7 = 0;
               if (blockPathItemTextures.contains(it)) {
                  flag2 = true;
                  break;
               }
            }
         }

         if (flag2) {
            collection4.add(obj9);
         }
      }

      List itemModelsWithBlockTextures = (List)collection4;
      Iterable iterable10 = (Iterable)itemModelsWithBlockTextures;
      Collection collection5 = (Collection)(new ObjectOpenHashSet((iterable10 instanceof Collection ? (Collection)iterable10 : null) != null ? (iterable10 instanceof Collection ? (Collection)iterable10 : null).size() : 10));

      for(Object obj11 : iterable10) {
         Model it = (Model)obj11;
         int value8 = 0;
         collection5.add(it.key());
      }

      ObjectOpenHashSet itemModelsWithBlockTexturesKeySet = (ObjectOpenHashSet)collection5;

      for(NexoOverlay overlay : (java.lang.Iterable<NexoOverlay>)(java.lang.Iterable)overlays) {
         this.duplicateModelsToOverlay(overlay.getOverlay(), itemModelsWithBlockTextures, blockPathItemTextures, "_item", "item/");
      }

      items = this.resourcePack.items();
      iterable10 = (Iterable)items;

      for(Object obj12 : iterable10) {
         Item item = (Item)obj12;
         int value28 = 0;
         ItemModel itemModel2 = item.model();
         ItemModel remappedModel = this.remapModels(itemModel2, (a0_ -> AtlasGenerator.generateModernAtlases$lambda$7$0(itemModelsWithBlockTexturesKeySet, a0_)));

         for(NexoOverlay overlay : (java.lang.Iterable<NexoOverlay>)(java.lang.Iterable)overlays) {
            Overlay overlay2 = overlay.getOverlay();
            overlay2.item(this.clone(item, remappedModel));
         }
      }

      Set createSetBuilder = SetsKt.createSetBuilder();
      int value9 = 0;
      Iterable iterable11 = (Iterable)itemTextureKeys;
      Collection collection6 = (Collection)(new ArrayList());

      for(Object obj13 : iterable11) {
         Key it = (Key)obj13;
         int value10 = 0;
         if (!blockPathItemTextures.contains(it)) {
            collection6.add(obj13);
         }
      }

      createSetBuilder.addAll((Collection)((List)collection6));
      iterable11 = (Iterable)blockPathItemTextures;
      collection6 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable11, 10)));

      for(Object obj14 : iterable11) {
         Key it = (Key)obj14;
         int value11 = 0;
         KeyUtils keyUtils = KeyUtils.INSTANCE;
         collection6.add(keyUtils.prependPrefix(this.removePackPrefix(it), "item/"));
      }

      createSetBuilder.addAll((Collection)((List)collection6));
      Set remappedItemTextureKeys = SetsKt.build(createSetBuilder);
      items = this.resourcePack.blockStates();
      Sequence sequence = CollectionsKt.asSequence((Iterable)items);
      Collection blockStates = VanillaResourcePack.INSTANCE.getResourcePack().blockStates();
      List blockStateModels = SequencesKt.toList(SequencesKt.distinctBy(SequencesKt.mapNotNull(SequencesKt.distinct(SequencesKt.flatMapIterable(SequencesKt.distinctBy(SequencesKt.plus(sequence, (Iterable)blockStates), AtlasGenerator::generateModernAtlases$lambda$9), (a0_ -> AtlasGenerator.generateModernAtlases$lambda$10(this, (BlockState)a0_)))), (Function1<Key, Model>)((Key p0) -> ResourcePackUtilsKt.modelOrVanilla(this.resourcePack, p0))), AtlasGenerator::generateModernAtlases$lambda$11));
      Iterable iterable12 = (Iterable)blockStateModels;
      Collection collection7 = (Collection)(new ObjectLinkedOpenHashSet());

      for(Object obj15 : iterable12) {
         Model it = (Model)obj15;
         int value12 = 0;
         Iterable iterable13 = (Iterable)this.resolvedFlatTextures(it);
         CollectionsKt.addAll(collection7, iterable13);
      }

      ObjectLinkedOpenHashSet blockTextureKeys = (ObjectLinkedOpenHashSet)collection7;
      Set overlappingTextures = CollectionsKt.intersect((Iterable)blockTextureKeys, (Iterable)remappedItemTextureKeys);
      List list;
      if (!((Collection)overlappingTextures).isEmpty()) {
         Iterable iterable14 = (Iterable)blockStateModels;
         collection7 = (Collection)(new ArrayList());

         for(Object obj16 : iterable14) {
            Model model = (Model)obj16;
            int value13 = 0;
            Iterable iterable15 = (Iterable)this.resolvedFlatTextures(model);
            boolean flag3;
            if (iterable15 instanceof Collection && ((Collection)iterable15).isEmpty()) {
               flag3 = false;
            } else {
               Iterator iterator2 = iterable15.iterator();

               while(true) {
                  if (!iterator2.hasNext()) {
                     flag3 = false;
                     break;
                  }

                  Object obj17 = iterator2.next();
                  Key it = (Key)obj17;
                  int value14 = 0;
                  if (overlappingTextures.contains(it)) {
                     flag3 = true;
                     break;
                  }
               }
            }

            if (flag3) {
               collection7.add(obj16);
            }
         }

         list = (List)collection7;
      } else {
         list = CollectionsKt.emptyList();
      }

      List overlappingBlockModels = list;
      Iterable iterable16 = (Iterable)overlappingBlockModels;
      Collection collection8 = (Collection)(new ObjectOpenHashSet((iterable16 instanceof Collection ? (Collection)iterable16 : null) != null ? (iterable16 instanceof Collection ? (Collection)iterable16 : null).size() : 10));

      for(Object obj18 : iterable16) {
         Model it = (Model)obj18;
         int value15 = 0;
         collection8.add(it.key());
      }

      ObjectOpenHashSet overlappingBlockModelsKeySet = (ObjectOpenHashSet)collection8;
      if (!((Collection)overlappingBlockModels).isEmpty()) {
         for(NexoOverlay overlay : (java.lang.Iterable<NexoOverlay>)(java.lang.Iterable)overlays) {
            this.duplicateModelsToOverlay(overlay.getOverlay(), overlappingBlockModels, overlappingTextures, "_block", "block/");
            Collection collection = this.resourcePack.blockStates();
            Iterable iterable17 = (Iterable)collection;

            for(Object obj19 : iterable17) {
               BlockState blockState = (BlockState)obj19;
               int value16 = 0;
               Overlay overlay3 = overlay.getOverlay();
               overlay3.blockState(this.remapVariants(blockState, (a0_ -> AtlasGenerator.generateModernAtlases$lambda$15$0(overlappingBlockModelsKeySet, a0_))));
            }
         }
      }

      Iterable iterable18 = (Iterable)blockPathItemTextures;
      Collection collection9 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable18, 10)));

      for(Object obj20 : iterable18) {
         Key it = (Key)obj20;
         int value17 = 0;
         KeyUtils keyUtils2 = KeyUtils.INSTANCE;
         collection9.add(keyUtils2.prependPrefix(this.removePackPrefix(it), "item/"));
      }

      List itemDupTextureKeys = (List)collection9;
      Iterable iterable19 = (Iterable)itemTextureKeys;
      Collection collection10 = (Collection)(new ArrayList());

      for(Object obj21 : iterable19) {
         block: {
            Key it = (Key)obj21;
            Key it_31 = it;
            if (!blockPathItemTextures.contains(it_31)) {
               String str2 = it_31.value();
               if (!StringsKt.startsWith(str2, "item/", false)) {
                  flag = false;
                  break block;
               }
            }

            flag = true;
         }

         if (!flag) {
            collection10.add(obj21);
         }
      }

      List itemExclusiveTextureKeys = (List)collection10;
      Iterable iterable20 = (Iterable)itemTextureKeys;
      Collection collection11 = (Collection)(new ArrayList());

      for(Object obj22 : iterable20) {
         Key it = (Key)obj22;
         int value18 = 0;
         String str3 = it.value();
         if (StringsKt.startsWith(str3, "item/", false)) {
            collection11.add(obj22);
         }
      }

      List itemPathTextureKeys;
      block: {
         itemPathTextureKeys = (List)collection11;
         Atlas atlas = VanillaResourcePack.INSTANCE.getResourcePack().atlas(Atlas.ITEMS);
         if (atlas != null) {
            List sources = atlas.sources();
            if (sources != null) {
               List list3 = sources;
               Iterable iterable21 = (Iterable)list3;
               int value29 = 0;
               Iterator iterator3 = iterable21.iterator();

               Object obj;
               while(true) {
                  if (!iterator3.hasNext()) {
                     obj = null;
                     break;
                  }

                  Object obj23 = iterator3.next();
                  int value30 = 0;
                  if (obj23 instanceof PalettedPermutationsAtlasSource) {
                     obj = obj23;
                     break;
                  }
               }

               if (!(obj instanceof PalettedPermutationsAtlasSource)) {
                  obj = null;
               }

               palettedPermutationsAtlasSource = (PalettedPermutationsAtlasSource)obj;
               if (palettedPermutationsAtlasSource != null) {
                  break block;
               }
            }
         }

         atlas = VanillaResourcePack.INSTANCE.getResourcePack().atlas(Atlas.BLOCKS);
         if (atlas != null) {
            List list2 = atlas.sources();
            if (list2 != null) {
               List list4 = list2;
               Iterable iterable22 = (Iterable)list4;
               int value31 = 0;
               Iterator iterator4 = iterable22.iterator();

               Object obj2;
               while(true) {
                  if (!iterator4.hasNext()) {
                     obj2 = null;
                     break;
                  }

                  Object obj24 = iterator4.next();
                  int value19 = 0;
                  if (obj24 instanceof PalettedPermutationsAtlasSource) {
                     obj2 = obj24;
                     break;
                  }
               }

               if (!(obj2 instanceof PalettedPermutationsAtlasSource)) {
                  obj2 = null;
               }

               palettedPermutationsAtlasSource = (PalettedPermutationsAtlasSource)obj2;
               break block;
            }
         }

         palettedPermutationsAtlasSource = null;
      }

      PalettedPermutationsAtlasSource trimsSource = palettedPermutationsAtlasSource;

      for(NexoOverlay overlay : (java.lang.Iterable<NexoOverlay>)(java.lang.Iterable)overlays) {
         List createListBuilder = CollectionsKt.createListBuilder();
         int value20 = 0;
         Key key = Atlas.ITEMS;
         createListBuilder.addAll((Collection)this.existingNonSingleSources(overlay, key));
         if (trimsSource != null) {
            AtlasSource p0 = trimsSource;
            int value32 = 0;
            Boolean flag4 = createListBuilder.add(p0);
         } else {
            Object obj3 = null;
         }

         Iterable iterable23 = (Iterable)itemExclusiveTextureKeys;
         Collection collection12 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable23, 10)));

         for(Object obj25 : iterable23) {
            Key p0 = (Key)obj25;
            int value21 = 0;
            collection12.add(AtlasSource.single(p0));
         }

         createListBuilder.addAll((Collection)((List)collection12));
         iterable23 = (Iterable)itemDupTextureKeys;
         collection12 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable23, 10)));

         for(Object obj26 : iterable23) {
            Key p0 = (Key)obj26;
            int value22 = 0;
            collection12.add(AtlasSource.single(p0));
         }

         createListBuilder.addAll((Collection)((List)collection12));
         iterable23 = (Iterable)itemPathTextureKeys;
         collection12 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable23, 10)));

         for(Object obj27 : iterable23) {
            Key p0 = (Key)obj27;
            int value23 = 0;
            collection12.add(AtlasSource.single(p0));
         }

         createListBuilder.addAll((Collection)((List)collection12));
         List itemOverlaySources = CollectionsKt.distinct((Iterable)CollectionsKt.build(createListBuilder));
         overlay.getOverlay().atlas(Atlas.atlas(Atlas.ITEMS, itemOverlaySources));
      }

      Iterable iterable24 = (Iterable)blockTextureKeys;
      Collection collection13 = (Collection)(new ArrayList());

      for(Object obj28 : iterable24) {
         Key it = (Key)obj28;
         int value24 = 0;
         String str4 = it.value();
         if (!StringsKt.startsWith(str4, "item/", false) && !overlappingTextures.contains(it)) {
            collection13.add(obj28);
         }
      }

      List blockPathTextures = (List)collection13;
      Iterable iterable = (Iterable)overlappingTextures;
      Collection collection14 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj29 : iterable) {
         Key it = (Key)obj29;
         int value25 = 0;
         KeyUtils keyUtils3 = KeyUtils.INSTANCE;
         collection14.add(keyUtils3.prependPrefix(this.removePackPrefix(it), "block/"));
      }

      List blockDupTextureKeys = (List)collection14;

      for(NexoOverlay overlay : (java.lang.Iterable<NexoOverlay>)(java.lang.Iterable)overlays) {
         List list5 = CollectionsKt.createListBuilder();
         int value33 = 0;
         Key key2 = Atlas.BLOCKS;
         list5.addAll((Collection)this.existingNonSingleSources(overlay, key2));
         Iterable iterable25 = (Iterable)blockPathTextures;
         Collection collection15 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable25, 10)));

         for(Object obj30 : iterable25) {
            Key p0 = (Key)obj30;
            int value26 = 0;
            collection15.add(AtlasSource.single(p0));
         }

         list5.addAll((Collection)((List)collection15));
         iterable25 = (Iterable)blockDupTextureKeys;
         collection15 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable25, 10)));

         for(Object obj31 : iterable25) {
            Key p0 = (Key)obj31;
            int value27 = 0;
            collection15.add(AtlasSource.single(p0));
         }

         list5.addAll((Collection)((List)collection15));
         List blockOverlaySources = CollectionsKt.distinct((Iterable)CollectionsKt.build(list5));
         overlay.getOverlay().atlas(Atlas.atlas(Atlas.BLOCKS, blockOverlaySources));
      }

   }

   private final void duplicateModelsToOverlay(Overlay overlay, List<? extends Model> models, Set<? extends Key> overlapTextures, String keySuffix, String targetPrefix) {
      Iterable iterable = (Iterable)models;
      Collection destination = (Collection)(new ObjectOpenHashSet((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10));

      for(Object item : iterable) {
         Model it = (Model)item;
         int value5 = 0;
         destination.add(it.key());
      }

      ObjectOpenHashSet modelKeySet = (ObjectOpenHashSet)destination;
      iterable = (Iterable)models;

      for(Object element : iterable) {
         Model model = (Model)element;
         int value = 0;
         KeyUtils keyUtils = KeyUtils.INSTANCE;
         Key key = model.key();
         Key dupKey = keyUtils.appendSuffix(key, keySuffix);
         ModelTextures textures = model.textures();
         ModelTextures dupTextures = this.remapTextures(textures, (a0_ -> AtlasGenerator.duplicateModelsToOverlay$lambda$1$0(overlapTextures, this, targetPrefix, a0_)));
         Key parent = model.parent();
         if (parent != null) {
            Key key3 = parent;
            Key key2 = model.parent();
            boolean condition = modelKeySet.contains(key2);
            if (condition) {
               int value2 = 0;
               parent = KeyUtils.INSTANCE.appendSuffix(key3, keySuffix);
            } else {
               parent = key3;
            }
         } else {
            parent = null;
         }

         Key dupParent = parent;
         overlay.model(model.toBuilder().key(dupKey).textures(dupTextures).parent(dupParent).build());
         Iterable iterable2 = (Iterable)this.flatTextures(model);
         Collection collection = (Collection)(new ArrayList());

         for(Object obj : iterable2) {
            Key it = (Key)obj;
            int value3 = 0;
            if (overlapTextures.contains(it)) {
               collection.add(obj);
            }
         }

         iterable2 = (Iterable)((List)collection);

         for(Object obj2 : iterable2) {
            Key textureKey = (Key)obj2;
            int value4 = 0;
            Texture texture2 = ResourcePackUtilsKt.textureOrVanilla(this.resourcePack, textureKey);
            if (texture2 != null) {
               Texture texture = texture2;
               overlay.texture(texture.toBuilder().key(KeyUtils.INSTANCE.appendSuffix(KeyUtils.INSTANCE.prependPrefix(this.removePackPrefix(textureKey), targetPrefix), ".png")).build());
            }
         }
      }

   }

   private final List<AtlasSource> existingNonSingleSources(NexoOverlay nexoOverlay, Key atlasKey) {
      Atlas atlas = nexoOverlay.overlay(this.resourcePack).atlas(atlasKey);
      List list = atlas != null ? atlas.sources() : null;
      if (list == null) {
         list = CollectionsKt.emptyList();
      }

      Iterable iterable = (Iterable)list;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value = 0;
         if (!(element instanceof SingleAtlasSource)) {
            destination.add(element);
         }
      }

      return (List)destination;
   }

   private final void addKey(List<? extends Key> keys, List<AtlasSource> sources) {
      Iterable iterable = (Iterable)keys;

      for(Object element : iterable) {
         Key key = (Key)element;
         int value = 0;
         if (VanillaResourcePack.INSTANCE.getResourcePack().texture(key) == null) {
            SingleAtlasSource singleAtlasSource = AtlasSource.single(key);
            sources.add(singleAtlasSource);
         }
      }

   }

   private final Key removePackPrefix(Key key2) {
      String namespace = key2.namespace();
      String str = key2.value();
      Key key = Key.key(namespace, StringsKt.removePrefix(StringsKt.removePrefix(str, (CharSequence)"item/"), (CharSequence)"block/"));
      return key;
   }

   private final Set<Key> flatTextures(Model model) {
      List list = model.textures().layers();
      Iterable iterable = (Iterable)list;
      Collection destination = (Collection)(new ObjectOpenHashSet((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10));

      for(Object element : iterable) {
         int value = 0;
         ModelTexture p0 = (ModelTexture)element;
         int value2 = 0;
         Key key = p0.key();
         if (key != null) {
            Object it = key;
            int value3 = 0;
            destination.add(it);
         }
      }

      ObjectOpenHashSet layers = (ObjectOpenHashSet)destination;
      Iterable iterable2 = (Iterable)model.textures().variables().values();
      Collection collection = (Collection)(new ObjectOpenHashSet((iterable2 instanceof Collection ? (Collection)iterable2 : null) != null ? (iterable2 instanceof Collection ? (Collection)iterable2 : null).size() : 10));

      for(Object obj : iterable2) {
         int value4 = 0;
         ModelTexture p0 = (ModelTexture)obj;
         int value5 = 0;
         Key key2 = p0.key();
         if (key2 != null) {
            Object obj2 = key2;
            int value6 = 0;
            collection.add(obj2);
         }
      }

      ObjectOpenHashSet variables = (ObjectOpenHashSet)collection;
      ModelTexture modelTexture = model.textures().particle();
      Key particle = modelTexture != null ? modelTexture.key() : null;
      Set set2 = SetsKt.plus((Set)layers, (Iterable)variables);
      boolean condition = particle != null;
      Set set;
      if (condition) {
         set = SetsKt.plus(set2, particle);
      } else {
         set = set2;
      }

      return set;
   }

   private final Set<Key> resolvedFlatTextures(Model model2) {
      Set textures = CollectionsKt.toMutableSet((Iterable)this.flatTextures(model2));
      Key key = model2.parent();
      Model model;
      if (key != null) {
         Key it = key;
         int value = 0;
         model = this.resourcePack.model(it);
         if (model == null) {
            model = NexoOverlay.V1_21_11.getOverlay().model(it);
         }
      } else {
         model = null;
      }

      for(Model parent = model; parent != null; parent = model) {
         CollectionsKt.addAll((Collection)textures, (Iterable)this.flatTextures(parent));
         Key key2 = parent.parent();
         if (key2 != null) {
            Key it = key2;
            int value2 = 0;
            model = this.resourcePack.model(it);
            if (model == null) {
               model = NexoOverlay.V1_21_11.getOverlay().model(it);
            }
         } else {
            model = null;
         }
      }

      return textures;
   }

   private final ModelTextures remapTextures(ModelTextures modelTextures2, Function1<? super Key, ? extends Key> block) {
      team.unnamed.creative.model.ModelTexture modelTexture;
      team.unnamed.creative.model.ModelTexture modelTexture2;
      team.unnamed.creative.model.ModelTexture modelTexture3;
      List list = modelTextures2.layers();
      Iterable iterable = (Iterable)list;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         block: {
            ModelTexture it = (ModelTexture)item;
            int value = 0;
            Key key = it.key();
            if (key != null) {
               key = (Key)block.invoke(key);
               if (key != null) {
                  Key p0 = key;
                  int value2 = 0;
                  modelTexture = ModelTexture.ofKey(p0);
                  if (modelTexture != null) {
                     break block;
                  }
               }
            }

            modelTexture = it;
         }

         destination.add(modelTexture);
      }

      List layers = (List)destination;
      Map variables = modelTextures2.variables();
      Map map2 = variables;
      int capacity = RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity(map2.size()), 16);
      Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(capacity);

      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
         Map map;
         Object obj;
         block: {
            obj = entry.getKey();
            Object obj2 = entry.getValue();
            variables = (Map)result;
            ModelTexture mt = (ModelTexture)obj2;
            String str = (String)obj;
            map = variables;
            int value3 = 0;
            Key key2 = mt.key();
            if (key2 != null) {
               key2 = (Key)block.invoke(key2);
               if (key2 != null) {
                  Key p0 = key2;
                  int value4 = 0;
                  modelTexture2 = ModelTexture.ofKey(p0);
                  if (modelTexture2 != null) {
                     break block;
                  }
               }
            }

            modelTexture2 = mt;
         }

         ModelTexture modelTexture4 = modelTexture2;
         map.put(obj, modelTexture4);
      }

      block: {
         ModelTexture modelTexture5 = modelTextures2.particle();
         if (modelTexture5 != null) {
            Key key3 = modelTexture5.key();
            if (key3 != null) {
               key3 = (Key)block.invoke(key3);
               if (key3 != null) {
                  Key p0 = key3;
                  int value5 = 0;
                  modelTexture3 = ModelTexture.ofKey(p0);
                  if (modelTexture3 != null) {
                     break block;
                  }
               }
            }
         }

         modelTexture3 = modelTextures2.particle();
      }

      ModelTexture particle = modelTexture3;
      ModelTextures modelTextures = ModelTextures.of(layers, particle, (Map)result);
      return modelTextures;
   }

   private final Set<Key> flatVariants(BlockState blockState) {
      Iterable iterable = (Iterable)blockState.variants().values();
      Collection destination = (Collection)(new ObjectLinkedOpenHashSet());

      for(Object element : iterable) {
         MultiVariant it = (MultiVariant)element;
         int value = 0;
         List variants = it.variants();
         Iterable iterable2 = (Iterable)variants;
         Collection collection = (Collection)(new ObjectOpenHashSet((iterable2 instanceof Collection ? (Collection)iterable2 : null) != null ? (iterable2 instanceof Collection ? (Collection)iterable2 : null).size() : 10));

         for(Object item : iterable2) {
            Variant it_2 = (Variant)item;
            int value2 = 0;
            collection.add(it_2.model());
         }

         Iterable list = (Iterable)((ObjectOpenHashSet)collection);
         CollectionsKt.addAll(collection, list);
      }

      return (Set)((ObjectLinkedOpenHashSet)destination);
   }

   private final BlockState remapVariants(BlockState blockState2, Function1<? super Key, ? extends Key> block) {
      Map variants = blockState2.variants();
      Map map2 = variants;
      int capacity = RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity(map2.size()), 16);
      Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(capacity);

      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
         Object key = entry.getKey();
         Object obj = entry.getValue();
         variants = (Map)result;
         MultiVariant mv = (MultiVariant)obj;
         String str = (String)key;
         Map map = variants;
         int value = 0;
         MultiVariant multiVariant = this.remapVariants(mv, (a0_ -> AtlasGenerator.remapVariants$lambda$0$0(block, a0_)));
         map.put(key, multiVariant);
      }

      List list = blockState2.multipart();
      Iterable iterable = (Iterable)list;
      capacity = 0;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         Selector selector = (Selector)item;
         int value2 = 0;
         Condition condition = selector.condition();
         MultiVariant variant = selector.variant();
         destination.add(Selector.of(condition, this.remapVariants(variant, (a0_ -> AtlasGenerator.remapVariants$lambda$1$0(block, a0_)))));
      }

      List multipart = (List)destination;
      BlockState blockState = BlockState.of(blockState2.key(), (Map)result, multipart);
      return blockState;
   }

   private final MultiVariant remapVariants(MultiVariant multiVariant2, Function1<? super Key, ? extends Key> block) {
      List variants = multiVariant2.variants();
      Iterable iterable = (Iterable)variants;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         Variant it = (Variant)item;
         int value = 0;
         Variant.Builder toBuilder = it.toBuilder();
         Key model = it.model();
         destination.add(toBuilder.model((Key)block.invoke(model)).build());
      }

      MultiVariant multiVariant = MultiVariant.of((List)destination);
      return multiVariant;
   }

   private final Model remapParent(Model model2, Function1<? super Key, ? extends Key> block) {
      Key parent = model2.parent();
      Model model;
      if (parent != null) {
         Key it = parent;
         int value = 0;
         model = model2.toBuilder().parent((Key)block.invoke(it)).build();
         if (model != null) {
            return model;
         }
      }

      model = model2;
      return model;
   }

   private final ItemModel remapModels(ItemModel itemModel6, Function1<? super Key, ? extends Key> block) {
      ItemModel itemModel;
      if (itemModel6 instanceof ReferenceItemModel) {
         Key model = ((ReferenceItemModel)itemModel6).model();
         itemModel = ItemModel.reference((Key)block.invoke(model), ((ReferenceItemModel)itemModel6).tints(), itemModel6.transformation());
         itemModel = itemModel;
      } else if (itemModel6 instanceof RangeDispatchItemModel) {
         List list = ((RangeDispatchItemModel)itemModel6).entries();
         Iterable iterable = (Iterable)list;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            RangeDispatchItemModel.Entry it = (RangeDispatchItemModel.Entry)item;
            int value = 0;
            float threshold = it.threshold();
            ItemModel itemModel2 = it.model();
            destination.add(RangeDispatchItemModel.Entry.entry(threshold, this.remapModels(itemModel2, block)));
         }

         List entries = (List)destination;
         ItemNumericProperty property = ((RangeDispatchItemModel)itemModel6).property();
         float scale = ((RangeDispatchItemModel)itemModel6).scale();
         ItemModel fallback = ((RangeDispatchItemModel)itemModel6).fallback();
         RangeDispatchItemModel rangeDispatchItemModel = ItemModel.rangeDispatch(property, scale, entries, fallback != null ? this.remapModels(fallback, block) : null, itemModel6.transformation());
         itemModel = rangeDispatchItemModel;
      } else if (itemModel6 instanceof SelectItemModel) {
         List list2 = ((SelectItemModel)itemModel6).cases();
         Iterable iterable2 = (Iterable)list2;
         Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

         for(Object obj : iterable2) {
            SelectItemModel.Case it = (SelectItemModel.Case)obj;
            int value2 = 0;
            ItemModel itemModel3 = it.model();
            collection.add(SelectItemModel.Case._case(this.remapModels(itemModel3, block), it.when()));
         }

         List cases = (List)collection;
         ItemStringProperty itemStringProperty = ((SelectItemModel)itemModel6).property();
         ItemModel itemModel4 = ((SelectItemModel)itemModel6).fallback();
         SelectItemModel selectItemModel = ItemModel.select(itemStringProperty, cases, itemModel4 != null ? this.remapModels(itemModel4, block) : null, itemModel6.transformation());
         itemModel = selectItemModel;
      } else if (itemModel6 instanceof ConditionItemModel) {
         ItemBooleanProperty condition = ((ConditionItemModel)itemModel6).condition();
         ItemModel onTrue = ((ConditionItemModel)itemModel6).onTrue();
         ItemModel itemModel5 = this.remapModels(onTrue, block);
         ItemModel onFalse = ((ConditionItemModel)itemModel6).onFalse();
         ConditionItemModel conditionItemModel = ItemModel.conditional(condition, itemModel5, this.remapModels(onFalse, block), itemModel6.transformation());
         itemModel = conditionItemModel;
      } else if (itemModel6 instanceof CompositeItemModel) {
         List models = ((CompositeItemModel)itemModel6).models();
         Iterable iterable3 = (Iterable)models;
         Collection collection2 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable3, 10)));

         for(Object obj2 : iterable3) {
            ItemModel it = (ItemModel)obj2;
            int value3 = 0;
            collection2.add(this.remapModels(it, block));
         }

         CompositeItemModel compositeItemModel = ItemModel.composite((List)collection2, itemModel6.transformation());
         itemModel = compositeItemModel;
      } else if (itemModel6 instanceof SpecialItemModel) {
         SpecialRender specialRender = ((SpecialItemModel)itemModel6).render();
         SpecialRender render = specialRender;
         if (render instanceof BedSpecialRender) {
            Key texture = ((BedSpecialRender)render).texture();
            specialRender = SpecialRender.bed((Key)block.invoke(texture));
            specialRender = specialRender;
         } else if (render instanceof ChestSpecialRender) {
            Key key = ((ChestSpecialRender)render).texture();
            ChestSpecialRender chestSpecialRender = SpecialRender.chest((Key)block.invoke(key), ((ChestSpecialRender)render).openness());
            specialRender = chestSpecialRender;
         } else if (render instanceof ShulkerBoxSpecialRender) {
            Key key2 = ((ShulkerBoxSpecialRender)render).texture();
            ShulkerBoxSpecialRender shulkerBoxSpecialRender = SpecialRender.shulkerBox((Key)block.invoke(key2), ((ShulkerBoxSpecialRender)render).openness(), ((ShulkerBoxSpecialRender)render).orientation());
            specialRender = shulkerBoxSpecialRender;
         } else if (render instanceof HeadSpecialRender) {
            HeadType kind = ((HeadSpecialRender)render).kind();
            Key key3 = ((HeadSpecialRender)render).texture();
            HeadSpecialRender headSpecialRender = SpecialRender.head(kind, key3 != null ? (Key)block.invoke(key3) : null, ((HeadSpecialRender)render).animation());
            specialRender = headSpecialRender;
         } else if (render instanceof CopperGolemStatueSpecialRender) {
            Pose pose = ((CopperGolemStatueSpecialRender)render).pose();
            Key key4 = ((CopperGolemStatueSpecialRender)render).texture();
            CopperGolemStatueSpecialRender copperGolemStatueSpecialRender = SpecialRender.copperGolemStatue(pose, (Key)block.invoke(key4));
            specialRender = copperGolemStatueSpecialRender;
         } else if (render instanceof SignSpecialRender) {
            SignSpecialRender signSpecialRender;
            if (((SignSpecialRender)render).hanging()) {
               WoodType woodType = ((SignSpecialRender)render).woodType();
               Key key5 = ((SignSpecialRender)render).texture();
               signSpecialRender = SpecialRender.hangingSign(woodType, key5 != null ? (Key)block.invoke(key5) : null);
            } else {
               WoodType woodType2 = ((SignSpecialRender)render).woodType();
               Key key6 = ((SignSpecialRender)render).texture();
               signSpecialRender = SpecialRender.standingSign(woodType2, key6 != null ? (Key)block.invoke(key6) : null);
            }

            SignSpecialRender signSpecialRender2 = signSpecialRender;
            specialRender = signSpecialRender2;
         } else {
            specialRender = render;
         }

         Key base = ((SpecialItemModel)itemModel6).base();
         SpecialItemModel specialItemModel = ItemModel.special(specialRender, (Key)block.invoke(base), itemModel6.transformation());
         itemModel = specialItemModel;
      } else {
         itemModel = itemModel6;
      }

      return itemModel;
   }

   private final Item clone(Item item2, ItemModel itemModel) {
      Item item = Item.item(item2.key(), itemModel, item2.handAnimationOnSwap(), item2.oversizedInGui(), item2.swapAnimationScale());
      return item;
   }

   private final List<Model> models(ItemModel itemModel4) {
      List list;
      if (itemModel4 instanceof ReferenceItemModel) {
         Key model = ((ReferenceItemModel)itemModel4).model();
         list = this.recursiveModels(model);
      } else if (itemModel4 instanceof SpecialItemModel) {
         Key base = ((SpecialItemModel)itemModel4).base();
         list = this.recursiveModels(base);
      } else if (itemModel4 instanceof RangeDispatchItemModel) {
         list = ((RangeDispatchItemModel)itemModel4).entries();
         Iterable iterable = (Iterable)list;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            RangeDispatchItemModel.Entry it = (RangeDispatchItemModel.Entry)element;
            int value = 0;
            ItemModel itemModel = it.model();
            Iterable iterable2 = (Iterable)this.models(itemModel);
            CollectionsKt.addAll(destination, iterable2);
         }

         Collection collection = (Collection)((List)destination);
         ItemModel fallback = ((RangeDispatchItemModel)itemModel4).fallback();
         List list2 = fallback != null ? this.models(fallback) : null;
         if (list2 == null) {
            list2 = CollectionsKt.emptyList();
         }

         list = CollectionsKt.plus(collection, (Iterable)list2);
      } else if (itemModel4 instanceof SelectItemModel) {
         list = ((SelectItemModel)itemModel4).cases();
         Iterable iterable3 = (Iterable)list;
         Collection collection4 = (Collection)(new ArrayList());

         for(Object obj : iterable3) {
            SelectItemModel.Case it = (SelectItemModel.Case)obj;
            int value2 = 0;
            ItemModel itemModel2 = it.model();
            Iterable iterable4 = (Iterable)this.models(itemModel2);
            CollectionsKt.addAll(collection4, iterable4);
         }

         Collection collection2 = (Collection)((List)collection4);
         ItemModel itemModel3 = ((SelectItemModel)itemModel4).fallback();
         List list3 = itemModel3 != null ? this.models(itemModel3) : null;
         if (list3 == null) {
            list3 = CollectionsKt.emptyList();
         }

         list = CollectionsKt.plus(collection2, (Iterable)list3);
      } else if (itemModel4 instanceof ConditionItemModel) {
         ItemModel onTrue = ((ConditionItemModel)itemModel4).onTrue();
         Collection collection3 = (Collection)this.models(onTrue);
         ItemModel onFalse = ((ConditionItemModel)itemModel4).onFalse();
         list = CollectionsKt.plus(collection3, (Iterable)this.models(onFalse));
      } else if (itemModel4 instanceof CompositeItemModel) {
         list = ((CompositeItemModel)itemModel4).models();
         Iterable iterable5 = (Iterable)list;
         Collection collection5 = (Collection)(new ArrayList());

         for(Object obj2 : iterable5) {
            ItemModel it = (ItemModel)obj2;
            int value3 = 0;
            Iterable iterable6 = (Iterable)this.models(it);
            CollectionsKt.addAll(collection5, iterable6);
         }

         list = (List)collection5;
      } else {
         list = CollectionsKt.emptyList();
      }

      return list;
   }

   private final List<Model> recursiveModels(Key model) {
      List models = (List)(new ArrayList());
      Model model2 = this.resourcePack.model(model);
      if (model2 == null) {
         model2 = NexoOverlay.V1_21_11.getOverlay().model(model);
      }

      model_1Loop:
      for(Model model_1 = model2; model_1 != null; model_1 = model2) {
         ((Collection)models).add(model_1);
         Key parent = model_1.parent();
         if (parent == null) {
            model2 = null;
         } else {
            Key it = parent;
            int value = 0;
            model2 = this.resourcePack.model(it);
            if (model2 == null) {
               model2 = NexoOverlay.V1_21_11.getOverlay().model(it);
               if (model2 == null) {
                  Collection overlays = this.resourcePack.overlays();

                  for(Overlay o : (java.lang.Iterable<Overlay>)(Iterable)overlays) {
                     int value2 = 0;
                     Model model3 = o.model(it);
                     if (model3 != null) {
                        model2 = model3;
                        continue model_1Loop;
                     }
                  }

                  model2 = null;
               }
            }
         }
      }

      return models;
   }

   private static final Key generateModernAtlases$lambda$7$0(ObjectOpenHashSet itemModelsWithBlockTexturesKeySet, Key key) {
      return itemModelsWithBlockTexturesKeySet.contains(key) ? KeyUtils.INSTANCE.appendSuffix(key, "_item") : key;
   }

   private static final String generateModernAtlases$lambda$9(BlockState it) {
      return it.key().asString();
   }

   private static final Iterable generateModernAtlases$lambda$10(AtlasGenerator atlasGenerator, BlockState it) {
      return (Iterable)atlasGenerator.flatVariants(it);
   }

   private static final Key generateModernAtlases$lambda$11(Model it) {
      return it.key();
   }

   private static final Key generateModernAtlases$lambda$15$0(ObjectOpenHashSet overlappingBlockModelsKeySet, Key key) {
      return overlappingBlockModelsKeySet.contains(key) ? KeyUtils.INSTANCE.appendSuffix(key, "_block") : key;
   }

   private static final Key duplicateModelsToOverlay$lambda$1$0(Set overlapTextures, AtlasGenerator atlasGenerator, String targetPrefix, Key textureKey) {
      boolean condition = overlapTextures.contains(textureKey);
      Key key;
      if (condition) {
         int value = 0;
         key = KeyUtils.INSTANCE.prependPrefix(atlasGenerator.removePackPrefix(textureKey), targetPrefix);
      } else {
         key = textureKey;
      }

      return key;
   }

   private static final Key remapVariants$lambda$0$0(Function1 block, Key it) {
      return (Key)block.invoke(it);
   }

   private static final Key remapVariants$lambda$1$0(Function1 block, Key it) {
      return (Key)block.invoke(it);
   }
}
