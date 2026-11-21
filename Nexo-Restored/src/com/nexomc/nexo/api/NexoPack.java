package com.nexomc.nexo.api;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.pack.creative.NexoPackReader;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.blockstate.BlockState;
import team.unnamed.creative.equipment.Equipment;
import team.unnamed.creative.equipment.EquipmentLayerType;
import team.unnamed.creative.font.Font;
import team.unnamed.creative.font.FontProvider;
import team.unnamed.creative.item.CompositeItemModel;
import team.unnamed.creative.item.ConditionItemModel;
import team.unnamed.creative.item.EmptyItemModel;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.item.ItemModel;
import team.unnamed.creative.item.RangeDispatchItemModel;
import team.unnamed.creative.item.ReferenceItemModel;
import team.unnamed.creative.item.SelectItemModel;
import team.unnamed.creative.lang.Language;
import team.unnamed.creative.metadata.overlays.OverlayEntry;
import team.unnamed.creative.metadata.overlays.OverlaysMeta;
import team.unnamed.creative.metadata.pack.PackMeta;
import team.unnamed.creative.metadata.sodium.SodiumMeta;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.sound.Sound;
import team.unnamed.creative.sound.SoundRegistry;
import team.unnamed.creative.texture.Texture;
import team.unnamed.creative.waypoint.WaypointStyle;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007b\u0002\b\bJ\u0011\u0010\t\u001a\u00070\n¢\u0006\u0002\b\u000bH\u0007b\u0002\b\bJ\u000e\u0010\f\u001a\u0004\u0018\u00010\rH\u0007b\u0002\b\bJ\u0014\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0010H\u0007b\u0002\b\bJ\u0014\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u0010H\u0007b\u0002\b\bJ\u001c\u0010\u0013\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0013\u001a\u00020\nH\u0007b\u0002\b\bJ\u0014\u0010\u0014\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\u0015H\u0007b\u0002\b\bJ\u001c\u0010\u0016\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0017\u001a\u00020\nH\u0007b\u0002\b\bJ\u0014\u0010\u0018\u001a\u00020\u00192\u0006\u0010\t\u001a\u00020\u0015H\u0007b\u0002\b\bJ\u001d\u0010\u001a\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\u0015H\u0000¢\u0006\u0002\b\u001d¨\u0006\u001e"},
   d2 = {"Lcom/nexomc/nexo/api/NexoPack;", "", "<init>", "()V", "sendPack", "", "player", "Lorg/bukkit/entity/Player;", "Lkotlin/jvm/JvmStatic;", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "Lorg/jetbrains/annotations/NotNull;", "builtResourcePack", "Lteam/unnamed/creative/BuiltResourcePack;", "mergePackFromZip", "zipFile", "Ljava/io/File;", "mergePackFromDirectory", "directory", "overwritePack", "clearPack", "Lteam/unnamed/creative/overlay/ResourceContainer;", "mergePack", "importedPack", "isEmpty", "", "mergeContainers", "container", "importedContainer", "mergeContainers$com_nexomc_core", "com.nexomc:core"}
)
public final class NexoPack {
   @NotNull
   public static final NexoPack INSTANCE = new NexoPack();

   private NexoPack() {
   }

   /** Sends the resource pack to the player through the pack server. */
   @JvmStatic
   public static final void sendPack(@NotNull Player player) {
      NexoPlugin.Companion.instance().packServer().sendPack(player);
   }

   /** Returns the resource pack the pack generator is assembling. */
   @JvmStatic
   @NotNull
   public static final ResourcePack resourcePack() {
      return NexoPlugin.Companion.instance().packGenerator().resourcePack();
   }

   /** Returns the built resource pack, or null if it has not been built yet. */
   @JvmStatic
   @Nullable
   public static final BuiltResourcePack builtResourcePack() {
      return NexoPlugin.Companion.instance().packGenerator().builtPack();
   }

   /** Merges the pack read from the zip file into the Nexo pack. Does nothing if the file does not exist. */
   @JvmStatic
   public static final void mergePackFromZip(@NotNull File zipFile) {
      if (zipFile.exists()) {
         ResourcePack resourcePack2 = resourcePack();
         ResourcePack resourcePack3 = NexoPackReader.INSTANCE.readFromZipFile(zipFile);
         mergePack(resourcePack2, resourcePack3);
      }
   }

   /** Merges the pack read from the directory into the Nexo pack. Does nothing if it is not an existing directory. */
   @JvmStatic
   public static final void mergePackFromDirectory(@NotNull File directory) {
      if (directory.exists() && directory.isDirectory()) {
         ResourcePack resourcePack2 = resourcePack();
         ResourcePack resourcePack3 = NexoPackReader.INSTANCE.readFromDirectory(directory);
         mergePack(resourcePack2, resourcePack3);
      }
   }

   @JvmStatic
   public static final void overwritePack(@NotNull ResourcePack resourcePack, @NotNull ResourcePack overwritePack) {
      clearPack(resourcePack);
      mergePack(resourcePack, overwritePack);
   }

   @JvmStatic
   public static final void clearPack(@NotNull ResourceContainer resourcePack) {
      Collection models = resourcePack.models();
      Iterable iterable = (Iterable)models;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         Model it = (Model)item;
         int value = 0;
         destination.add(it.key());
      }

      iterable = (Iterable)((List)destination);

      for(Object element : iterable) {
         Key p0 = (Key)element;
         int value2 = 0;
         resourcePack.removeModel(p0);
      }

      models = resourcePack.textures();
      iterable = (Iterable)models;
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj : iterable) {
         Texture it = (Texture)obj;
         int value3 = 0;
         destination.add(it.key());
      }

      iterable = (Iterable)((List)destination);

      for(Object obj2 : iterable) {
         Key p0 = (Key)obj2;
         int value4 = 0;
         resourcePack.removeTexture(p0);
      }

      models = resourcePack.atlases();
      iterable = (Iterable)models;
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj3 : iterable) {
         Atlas it = (Atlas)obj3;
         int value5 = 0;
         destination.add(it.key());
      }

      iterable = (Iterable)((List)destination);

      for(Object obj4 : iterable) {
         Key p0 = (Key)obj4;
         int value6 = 0;
         resourcePack.removeAtlas(p0);
      }

      models = resourcePack.languages();
      iterable = (Iterable)models;
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj5 : iterable) {
         Language it = (Language)obj5;
         int value7 = 0;
         destination.add(it.key());
      }

      iterable = (Iterable)((List)destination);

      for(Object obj6 : iterable) {
         Key p0 = (Key)obj6;
         int value8 = 0;
         resourcePack.removeLanguage(p0);
      }

      models = resourcePack.blockStates();
      iterable = (Iterable)models;
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj7 : iterable) {
         BlockState it = (BlockState)obj7;
         int value9 = 0;
         destination.add(it.key());
      }

      iterable = (Iterable)((List)destination);

      for(Object obj8 : iterable) {
         Key p0 = (Key)obj8;
         int value10 = 0;
         resourcePack.removeBlockState(p0);
      }

      models = resourcePack.fonts();
      iterable = (Iterable)models;
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj9 : iterable) {
         Font it = (Font)obj9;
         int value11 = 0;
         destination.add(it.key());
      }

      iterable = (Iterable)((List)destination);

      for(Object obj10 : iterable) {
         Key p0 = (Key)obj10;
         resourcePack.removeFont(p0);
      }

      models = resourcePack.sounds();
      iterable = (Iterable)models;
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj11 : iterable) {
         Sound it = (Sound)obj11;
         int value12 = 0;
         destination.add(it.key());
      }

      iterable = (Iterable)((List)destination);

      for(Object obj12 : iterable) {
         Key p0 = (Key)obj12;
         int value13 = 0;
         resourcePack.removeSound(p0);
      }

      models = resourcePack.soundRegistries();
      iterable = (Iterable)models;
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object obj13 : iterable) {
         SoundRegistry it = (SoundRegistry)obj13;
         int value14 = 0;
         destination.add(it.namespace());
      }

      iterable = (Iterable)((List)destination);

      for(Object obj14 : iterable) {
         String p0 = (String)obj14;
         int value15 = 0;
         resourcePack.removeSoundRegistry(p0);
      }

      Map unknownFiles = resourcePack.unknownFiles();
      Map map = unknownFiles;
      destination = (Collection)(new ArrayList(map.size()));

      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value16 = 0;
         destination.add((String)entry.getKey());
      }

      Iterable iterable2 = (Iterable)((List)destination);

      for(Object obj15 : iterable2) {
         String p0 = (String)obj15;
         int value17 = 0;
         resourcePack.removeUnknownFile(p0);
      }

      Collection items = resourcePack.items();
      iterable2 = (Iterable)items;
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

      for(Object obj16 : iterable2) {
         Item it = (Item)obj16;
         int value18 = 0;
         destination.add(it.key());
      }

      iterable2 = (Iterable)((List)destination);

      for(Object obj17 : iterable2) {
         Key p0 = (Key)obj17;
         int value19 = 0;
         resourcePack.removeItem(p0);
      }

      items = resourcePack.equipment();
      iterable2 = (Iterable)items;
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

      for(Object obj18 : iterable2) {
         Equipment it = (Equipment)obj18;
         int value20 = 0;
         destination.add(it.key());
      }

      iterable2 = (Iterable)((List)destination);

      for(Object obj19 : iterable2) {
         Key p0 = (Key)obj19;
         int value21 = 0;
         resourcePack.removeEquipment(p0);
      }

      if (resourcePack instanceof ResourcePack) {
         ((ResourcePack)resourcePack).icon((Writable)null);
         ((ResourcePack)resourcePack).metadata(team.unnamed.creative.metadata.Metadata.empty());
         ((ResourcePack)resourcePack).sodiumMeta(SodiumMeta.sodium(CollectionsKt.emptyList()));
         items = ((ResourcePack)resourcePack).overlays();
         iterable2 = (Iterable)items;
         NexoPack nexoPack = INSTANCE;

         for(Object obj20 : iterable2) {
            ResourceContainer p0 = (ResourceContainer)obj20;
            int value22 = 0;
            clearPack(p0);
         }
      }

   }

   @JvmStatic
   public static final void mergePack(@NotNull ResourcePack resourcePack, @NotNull ResourcePack importedPack) {
      PackMeta packMeta = importedPack.packMeta();
      if (packMeta == null) {
         packMeta = resourcePack.packMeta();
      }

      if (packMeta != null) {
         PackMeta p0 = packMeta;
         int value = 0;
         resourcePack.packMeta(p0);
      }

      Writable icon = importedPack.icon();
      if (icon == null) {
         icon = resourcePack.icon();
      }

      if (icon != null) {
         Writable p0 = icon;
         int value2 = 0;
         resourcePack.icon(p0);
      }

      Collection collection = resourcePack.overlays();
      Collection collection2 = importedPack.overlays();
      Iterable iterable = (Iterable)CollectionsKt.plus(collection, (Iterable)collection2);
      Map destination = (Map)(new LinkedHashMap());

      for(Object element : iterable) {
         Overlay it = (Overlay)element;
         int value3 = 0;
         Object obj2 = it.directory();
         Object obj3 = destination.get(obj2);
         Object obj;
         if (obj3 == null) {
            int value4 = 0;
            Object answer = (List)(new ArrayList());
            destination.put(obj2, answer);
            obj = answer;
         } else {
            obj = obj3;
         }

         List list4 = (List)obj;
         list4.add(element);
      }


      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)destination.entrySet()) {
         Object key = entry.getKey();
         String directory = (String)key;
         List overlays = (List)entry.getValue();
         Overlay overlay2 = Overlay.overlay(directory);
         Overlay newOverlay = overlay2;
         Iterable iterable2 = (Iterable)overlays;

         for(Object obj4 : iterable2) {
            Overlay overlay = (Overlay)obj4;
            int value5 = 0;
            NexoPack nexoPack = INSTANCE;
            ResourceContainer resourceContainer = newOverlay;
            nexoPack.mergeContainers$com_nexomc_core(resourceContainer, overlay);
         }

         resourcePack.overlay(newOverlay);
      }

      List overlayMetas = (List)(new ArrayList());
      OverlaysMeta overlaysMeta = resourcePack.overlaysMeta();
      if (overlaysMeta != null) {
         List entries = overlaysMeta.entries();
         if (entries != null) {
            Iterable iterable3 = (Iterable)entries;

            for(Object obj5 : iterable3) {
               OverlayEntry p0 = (OverlayEntry)obj5;
               int value6 = 0;
               overlayMetas.add(p0);
            }
         }
      }

      overlaysMeta = importedPack.overlaysMeta();
      if (overlaysMeta != null) {
         List list = overlaysMeta.entries();
         if (list != null) {
            Iterable iterable4 = (Iterable)list;

            for(Object obj6 : iterable4) {
               OverlayEntry p0 = (OverlayEntry)obj6;
               int value7 = 0;
               overlayMetas.add(p0);
            }
         }
      }

      resourcePack.overlaysMeta(OverlaysMeta.of(overlayMetas));
      List ignoredShaders = (List)(new ArrayList());
      SodiumMeta sodiumMeta = resourcePack.sodiumMeta();
      if (sodiumMeta != null) {
         List list2 = sodiumMeta.ignoredShaders();
         if (list2 != null) {
            Iterable iterable5 = (Iterable)list2;

            for(Object obj7 : iterable5) {
               String p0 = (String)obj7;
               int value8 = 0;
               ignoredShaders.add(p0);
            }
         }
      }

      sodiumMeta = importedPack.sodiumMeta();
      if (sodiumMeta != null) {
         List list3 = sodiumMeta.ignoredShaders();
         if (list3 != null) {
            Iterable iterable6 = (Iterable)list3;

            for(Object obj8 : iterable6) {
               String p0 = (String)obj8;
               int value9 = 0;
               ignoredShaders.add(p0);
            }
         }
      }

      resourcePack.sodiumMeta(SodiumMeta.sodium(ignoredShaders));
      INSTANCE.mergeContainers$com_nexomc_core(resourcePack, importedPack);
   }

   @JvmStatic
   public static final boolean isEmpty(@NotNull ResourceContainer resourcePack) {
      Collection items = resourcePack.items();
      if (!items.isEmpty()) {
         return false;
      } else {
         items = resourcePack.models();
         if (!items.isEmpty()) {
            return false;
         } else {
            items = resourcePack.equipment();
            if (!items.isEmpty()) {
               return false;
            } else {
               items = resourcePack.textures();
               if (!items.isEmpty()) {
                  return false;
               } else {
                  items = resourcePack.sounds();
                  if (!items.isEmpty()) {
                     return false;
                  } else {
                     items = resourcePack.soundRegistries();
                     if (!items.isEmpty()) {
                        return false;
                     } else {
                        items = resourcePack.atlases();
                        if (!items.isEmpty()) {
                           return false;
                        } else {
                           items = resourcePack.languages();
                           if (!items.isEmpty()) {
                              return false;
                           } else {
                              items = resourcePack.blockStates();
                              if (!items.isEmpty()) {
                                 return false;
                              } else {
                                 items = resourcePack.fonts();
                                 if (!items.isEmpty()) {
                                    return false;
                                 } else {
                                    Map unknownFiles = resourcePack.unknownFiles();
                                    if (!unknownFiles.isEmpty()) {
                                       return false;
                                    } else {
                                       Collection waypointStyles = resourcePack.waypointStyles();
                                       if (!waypointStyles.isEmpty()) {
                                          return false;
                                       } else {
                                          ResourcePack resourcePack2 = resourcePack instanceof ResourcePack ? (ResourcePack)resourcePack : null;
                                          boolean flag;
                                          if ((resourcePack instanceof ResourcePack ? (ResourcePack)resourcePack : null) != null) {
                                             Collection overlays = resourcePack2.overlays();
                                             if (overlays != null) {
                                                Iterable iterable = (Iterable)overlays;
                                                boolean flag2;
                                                if (((Collection)iterable).isEmpty()) {
                                                   flag2 = true;
                                                } else {
                                                   Iterator iterator = iterable.iterator();

                                                   while(true) {
                                                      if (!iterator.hasNext()) {
                                                         flag2 = true;
                                                         break;
                                                      }

                                                      Object element = iterator.next();
                                                      Overlay it = (Overlay)element;
                                                      int value = 0;
                                                      if (!isEmpty(it)) {
                                                         flag2 = false;
                                                         break;
                                                      }
                                                   }
                                                }

                                                flag = !flag2;
                                                return !flag;
                                             }
                                          }

                                          flag = false;
                                          return !flag;
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public final void mergeContainers$com_nexomc_core(@NotNull ResourceContainer container, @NotNull ResourceContainer importedContainer) {
      Collection textures = importedContainer.textures();
      Iterable iterable = (Iterable)textures;

      for(Object element : iterable) {
         Texture p0 = (Texture)element;
         int value2 = 0;
         container.texture(p0);
      }

      textures = importedContainer.sounds();
      iterable = (Iterable)textures;

      for(Object obj : iterable) {
         Sound p0 = (Sound)obj;
         int value3 = 0;
         container.sound(p0);
      }

      importedContainer.unknownFiles().forEach((path, data) -> { container.unknownFile(path, data); });
      textures = importedContainer.waypointStyles();
      iterable = (Iterable)textures;

      for(Object obj2 : iterable) {
         WaypointStyle p0 = (WaypointStyle)obj2;
         int value4 = 0;
         container.waypointStyle(p0);
      }

      textures = importedContainer.equipment();
      iterable = (Iterable)textures;

      for(Object obj3 : iterable) {
         Equipment equipment = (Equipment)obj3;
         int value5 = 0;
         Equipment equipment2 = container.equipment(equipment.key());
         if (equipment2 == null) {
            container.equipment(equipment);
         } else {
            Equipment oldEquipment = equipment2;
            LinkedHashMap layersByType = new LinkedHashMap(oldEquipment.layers());
            Map map = equipment.layers();
            Map map3 = map;

            for(Map.Entry entry2 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map3.entrySet()) {
               int value6 = 0;
               EquipmentLayerType type = (EquipmentLayerType)entry2.getKey();
               List layers = (List)entry2.getValue();
               layersByType.compute(type, (k, oldLayers) -> NexoPack.mergeContainers$lambda$1$0$0(layers, (EquipmentLayerType)k, (List)oldLayers));
            }

            container.equipment(oldEquipment.layers((Map)layersByType));
         }
      }

      textures = importedContainer.items();
      iterable = (Iterable)textures;

      for(Object obj4 : iterable) {
         Item item = (Item)obj4;
         int value7 = 0;
         Item item2 = container.item(item.key());
         if (item2 == null) {
            container.item(item);
         } else {
            Item oldItem = item2;
            boolean handSwap = oldItem.handAnimationOnSwap() ? item.handAnimationOnSwap() : oldItem.handAnimationOnSwap();
            boolean oversized = !oldItem.oversizedInGui() ? item.oversizedInGui() : oldItem.oversizedInGui();
            ItemModel itemModel;
            if (oldItem.model() instanceof ReferenceItemModel) {
               itemModel = item.model();
            } else if (oldItem.model() instanceof EmptyItemModel) {
               itemModel = item.model();
            } else if (Intrinsics.areEqual(oldItem.model().getClass(), item.model().getClass())) {
               ItemModel itemModel2 = oldItem.model();
               ItemModel itemModel3 = item.model();
               itemModel = mergeContainers$lambda$2$mergeItemModels(item, itemModel2, itemModel3);
            } else {
               Logs.logError("Failed to merge ItemModels " + item.key().asString() + ", ", false);
               Logs.logWarn("Existing ItemModel of incompatible type " + oldItem.model().getClass().getSimpleName() + ", keeping old ItemModel...", false);
               itemModel = null;
            }

            ItemModel itemModel4 = itemModel;
            if (itemModel4 != null) {
               int value8 = 0;
               Item item3 = Item.item(item.key(), itemModel4, handSwap, oversized);
               if (item3 != null) {
                  item3.addTo(container);
               }
            }
         }
      }

      textures = importedContainer.models();
      iterable = (Iterable)textures;

      for(Object obj5 : iterable) {
         Model model = (Model)obj5;
         int value9 = 0;
         Model.Builder baseRegistry = model.toBuilder();
         int value10 = 0;
         Model model2 = container.model(model.key());
         if (model2 != null) {
            List overrides = model2.overrides();
            if (overrides != null) {
               Iterable iterable2 = (Iterable)overrides;
               Model.Builder builder = baseRegistry;

               for(Object obj6 : iterable2) {
                  ItemOverride p0 = (ItemOverride)obj6;
                  int value11 = 0;
                  builder.addOverride(p0);
               }
            }
         }

         baseRegistry.build().addTo(container);
      }

      textures = importedContainer.fonts();
      iterable = (Iterable)textures;

      for(Object obj7 : iterable) {
         Font font = (Font)obj7;
         int value12 = 0;
         Font.Builder base = font.toBuilder();
         int value13 = 0;
         Font font2 = container.font(font.key());
         if (font2 != null) {
            List providers = font2.providers();
            if (providers != null) {
               Iterable iterable3 = (Iterable)providers;
               Font.Builder builder2 = base;

               for(Object obj8 : iterable3) {
                  FontProvider p0 = (FontProvider)obj8;
                  int value14 = 0;
                  builder2.addProvider(p0);
               }
            }
         }

         base.build().addTo(container);
      }

      textures = importedContainer.soundRegistries();
      iterable = (Iterable)textures;

      for(Object obj9 : iterable) {
         SoundRegistry soundRegistry = (SoundRegistry)obj9;
         int value15 = 0;
         SoundRegistry baseRegistry = container.soundRegistry(soundRegistry.namespace());
         if (baseRegistry != null) {
            LinkedHashSet mergedEvents = new LinkedHashSet(baseRegistry.sounds());
            mergedEvents.addAll(soundRegistry.sounds());
            SoundRegistry.soundRegistry(baseRegistry.namespace(), (Set)mergedEvents).addTo(container);
         } else {
            soundRegistry.addTo(container);
         }
      }

      textures = importedContainer.atlases();
      iterable = (Iterable)textures;

      for(Object obj10 : iterable) {
         Atlas atlas = (Atlas)obj10;
         int value16 = 0;
         Atlas.Builder toBuilder = atlas.toBuilder();
         int value17 = 0;
         Atlas atlas2 = container.atlas(atlas.key());
         if (atlas2 != null) {
            List sources = atlas2.sources();
            if (sources != null) {
               Iterable iterable4 = (Iterable)sources;
               Atlas.Builder builder3 = toBuilder;

               for(Object obj11 : iterable4) {
                  AtlasSource p0 = (AtlasSource)obj11;
                  int value18 = 0;
                  builder3.addSource(p0);
               }
            }
         }

         toBuilder.build().addTo(container);
      }

      textures = importedContainer.languages();
      iterable = (Iterable)textures;

      for(Object obj12 : iterable) {
         Language language = (Language)obj12;
         int value19 = 0;
         Language language2 = container.language(language.key());
         if (language2 != null) {
            Language base = language2;
            int value22 = 0;
            Iterable entrySet = (Iterable)base.translations().entrySet();

            for(Object obj13 : entrySet) {
               Map.Entry entry = (Map.Entry)obj13;
               int value20 = 0;
               String key = (String)entry.getKey();
               String value = (String)entry.getValue();
               language.translations().putIfAbsent(key, value);
            }
         }

         language.addTo(container);
      }

      textures = importedContainer.blockStates();
      iterable = (Iterable)textures;

      for(Object obj14 : iterable) {
         BlockState blockState = (BlockState)obj14;
         int value21 = 0;
         BlockState blockState2 = container.blockState(blockState.key());
         if (blockState2 != null) {
            BlockState base = blockState2;
            int mergedEvents = 0;
            List multipart = blockState.multipart();
            List list = base.multipart();
            multipart.addAll((Collection)list);
            Map variants = blockState.variants();
            Map map2 = base.variants();
            variants.putAll(map2);
         }

         blockState.addTo(container);
      }

   }

   private static final ItemModel mergeContainers$lambda$2$mergeItemModels(Item item, ItemModel oldItem, ItemModel newItem) {
      net.kyori.adventure.key.Key key;
      ItemModel itemModel;
      if (newItem instanceof ReferenceItemModel) {
         Collection collection;
         Iterable iterable;
         block: {
            key = ((ReferenceItemModel)newItem).model();
            List tints = ((ReferenceItemModel)newItem).tints();
            collection = (Collection)tints;
            ReferenceItemModel referenceItemModel = oldItem instanceof ReferenceItemModel ? (ReferenceItemModel)oldItem : null;
            if ((oldItem instanceof ReferenceItemModel ? (ReferenceItemModel)oldItem : null) != null) {
               List list = referenceItemModel.tints();
               if (list != null) {
                  iterable = (Iterable)list;
                  break block;
               }
            }

            iterable = (Iterable)CollectionsKt.emptyList();
         }

         itemModel = ItemModel.reference(key, CollectionsKt.plus(collection, iterable), newItem.transformation());
         itemModel = itemModel;
      } else if (newItem instanceof CompositeItemModel) {
         java.util.Collection collection2;
         Iterable iterable2;
         block: {
            List models = ((CompositeItemModel)newItem).models();
            collection2 = (Collection)models;
            CompositeItemModel compositeItemModel = oldItem instanceof CompositeItemModel ? (CompositeItemModel)oldItem : null;
            if ((oldItem instanceof CompositeItemModel ? (CompositeItemModel)oldItem : null) != null) {
               List list2 = compositeItemModel.models();
               if (list2 != null) {
                  iterable2 = (Iterable)list2;
                  break block;
               }
            }

            iterable2 = (Iterable)CollectionsKt.emptyList();
         }

         itemModel = ItemModel.composite(CollectionsKt.plus(collection2, iterable2), newItem.transformation());
         itemModel = itemModel;
      } else if (newItem instanceof SelectItemModel) {
         java.util.List<team.unnamed.creative.item.SelectItemModel.Case> cases;
         SelectItemModel.Builder toBuilder = ((SelectItemModel)newItem).toBuilder();
         ItemModel fallback = ((SelectItemModel)newItem).fallback();
         if (fallback == null) {
            fallback = (oldItem instanceof SelectItemModel ? (SelectItemModel)oldItem : null) != null ? (oldItem instanceof SelectItemModel ? (SelectItemModel)oldItem : null).fallback() : null;
         }

         block: {
            toBuilder = toBuilder.fallback(fallback);
            SelectItemModel oldSelect = oldItem instanceof SelectItemModel ? (SelectItemModel)oldItem : null;
            if (oldSelect != null) {
               cases = oldSelect.cases();
               if (cases != null) {
                  break block;
               }
            }

            cases = CollectionsKt.emptyList();
         }

         SelectItemModel selectItemModel = toBuilder.addCases(cases).transformation(newItem.transformation()).build();
         itemModel = selectItemModel;
      } else if (newItem instanceof RangeDispatchItemModel) {
         java.util.List<team.unnamed.creative.item.RangeDispatchItemModel.Entry> entries;
         RangeDispatchItemModel.Builder builder = ((RangeDispatchItemModel)newItem).toBuilder();
         ItemModel itemModel2 = ((RangeDispatchItemModel)newItem).fallback();
         if (itemModel2 == null) {
            itemModel2 = (oldItem instanceof RangeDispatchItemModel ? (RangeDispatchItemModel)oldItem : null) != null ? (oldItem instanceof RangeDispatchItemModel ? (RangeDispatchItemModel)oldItem : null).fallback() : null;
         }

         block: {
            builder = builder.fallback(itemModel2);
            RangeDispatchItemModel oldRange = oldItem instanceof RangeDispatchItemModel ? (RangeDispatchItemModel)oldItem : null;
            if (oldRange != null) {
               entries = oldRange.entries();
               if (entries != null) {
                  break block;
               }
            }

            entries = CollectionsKt.emptyList();
         }

         RangeDispatchItemModel rangeDispatchItemModel = builder.addEntries(entries).transformation(newItem.transformation()).build();
         itemModel = rangeDispatchItemModel;
      } else if (newItem instanceof ConditionItemModel) {
         itemModel = oldItem instanceof ConditionItemModel ? (ConditionItemModel)oldItem : null;
         if ((oldItem instanceof ConditionItemModel ? (ConditionItemModel)oldItem : null) != null) {
            ConditionItemModel it = (ConditionItemModel)(itemModel);
            int value = 0;
            itemModel = Intrinsics.areEqual(it.condition(), ((ConditionItemModel)newItem).condition()) ? it : null;
         } else {
            itemModel = null;
         }

         ConditionItemModel oldCondition;
         block: {
            oldCondition = (ConditionItemModel)itemModel;
            if (oldCondition != null) {
               itemModel = oldCondition.onTrue();
               if (itemModel != null) {
                  ItemModel it = itemModel;
                  int value2 = 0;
                  ItemModel onTrue = ((ConditionItemModel)newItem).onTrue();
                  itemModel = mergeContainers$lambda$2$mergeItemModels(item, it, onTrue);
                  if (itemModel != null) {
                     break block;
                  }
               }
            }

            itemModel = ((ConditionItemModel)newItem).onTrue();
         }

         ItemModel mergedTrue;
         block: {
            mergedTrue = itemModel;
            if (oldCondition != null) {
               itemModel = oldCondition.onFalse();
               if (itemModel != null) {
                  ItemModel it = itemModel;
                  int value3 = 0;
                  ItemModel onFalse = ((ConditionItemModel)newItem).onFalse();
                  itemModel = mergeContainers$lambda$2$mergeItemModels(item, it, onFalse);
                  if (itemModel != null) {
                     break block;
                  }
               }
            }

            itemModel = ((ConditionItemModel)newItem).onFalse();
         }

         ItemModel mergedFalse = itemModel;
         ConditionItemModel conditionItemModel = ItemModel.conditional(((ConditionItemModel)newItem).condition(), mergedTrue, mergedFalse, newItem.transformation());
         itemModel = conditionItemModel;
      } else {
         itemModel = item.model();
      }

      return itemModel;
   }

   private static final void mergeContainers$lambda$0(Function2 tmp0, Object p0, Object p1) {
      tmp0.invoke(p0, p1);
   }

   private static final List mergeContainers$lambda$1$0$0(List layers, EquipmentLayerType equipmentLayerType, List oldLayers) {
      List list = oldLayers;
      if (oldLayers == null) {
         list = CollectionsKt.emptyList();
      }

      Collection collection = (Collection)list;
      return CollectionsKt.plus(collection, (Iterable)layers);
   }

   private static final List mergeContainers$lambda$1$0$1(Function2 tmp0, Object p0, Object p1) {
      return (List)tmp0.invoke(p0, p1);
   }
}
