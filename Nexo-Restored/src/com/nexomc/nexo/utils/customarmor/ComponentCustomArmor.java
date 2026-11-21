package com.nexomc.nexo.utils.customarmor;
import com.nexomc.nexo.utils.customarmor.CustomArmorType;
import kotlin.Pair;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import team.unnamed.creative.overlay.ResourceContainer;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.items.ComponentParser;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.items.customarmor.CustomArmorTextures;
import com.nexomc.nexo.pack.NexoOverlay;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.logs.Logs;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Triple;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.equipment.Equipment;
import team.unnamed.creative.equipment.EquipmentLayer;
import team.unnamed.creative.equipment.EquipmentLayerType;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.texture.Texture;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\u001dB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J(\u0010\b\u001a\u00020\u00072\u001e\u0010\t\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0\u000b0\nH\u0002J\b\u0010\u000f\u001a\u00020\u0007H\u0002J,\u0010\u0010\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0012\u001a\u00020\r2\u0006\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\f2\b\b\u0002\u0010\u0015\u001a\u00020\u000eH\u0002J\u0016\u0010\u0016\u001a\u00020\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\f0\nH\u0002J\u0018\u0010\u0017\u001a\n\u0012\u0004\u0012\u00020\u0019\u0018\u00010\u00182\u0006\u0010\u001a\u001a\u00020\fH\u0002J\u0012\u0010\u001b\u001a\u0004\u0018\u00010\u001c2\u0006\u0010\u001a\u001a\u00020\fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e"},
   d2 = {"Lcom/nexomc/nexo/utils/customarmor/ComponentCustomArmor;", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "<init>", "(Lteam/unnamed/creative/ResourcePack;)V", "generatePackFiles", "", "writeArmorModels", "armorPrefixes", "", "Lkotlin/Triple;", "", "Lnet/kyori/adventure/key/Key;", "", "copyEquipmentTextures", "fetchTexture", "Lteam/unnamed/creative/texture/Texture;", "texture", "fallbackName", "itemId", "warn", "parseNexoArmorItems", "entityTypesFromItem", "", "Lorg/bukkit/entity/EntityType;", "suffix", "slotFromItem", "Lorg/bukkit/inventory/EquipmentSlot;", "CustomArmorProperties", "com.nexomc:core"}
)
public final class ComponentCustomArmor {
   @NotNull
   private final ResourcePack resourcePack;

   public ComponentCustomArmor(@NotNull ResourcePack resourcePack) {
      super();
      this.resourcePack = resourcePack;
   }

   public final void generatePackFiles() {
      net.kyori.adventure.key.Key key;
      Iterable entrySet = (Iterable)NexoItems.entries().entrySet();
      Collection destination = (Collection)(new ObjectOpenHashSet((entrySet instanceof Collection ? (Collection)entrySet : null) != null ? (entrySet instanceof Collection ? (Collection)entrySet : null).size() : 10));

      for(Object element : entrySet) {
         int value = 0;
         Map.Entry entry = (Map.Entry)element;
         int value2 = 0;
         String itemId = (String)entry.getKey();
         ItemBuilder builder = (ItemBuilder)entry.getValue();
         CharSequence charSequence = (CharSequence)itemId;
         Regex itemIdRegex = CustomArmorType.Companion.getItemIdRegex();
         String str = "$1";
         String prefix = itemIdRegex.replace(charSequence, str);
         Triple triple;
         boolean isElytra;
         Key defaultKey;
         if (builder.getNexoMeta().getCustomArmorTextures() == null) {
            triple = null;
         } else {
            block: {
               block2: {
                  isElytra = builder.getType() == Material.ELYTRA || builder.isGlider().isTrue();
                  Key key2 = Key.key("nexo", isElytra ? prefix + "_elytra" : prefix);
                  defaultKey = key2;
                  Equippable data = ItemStack.of(builder.getType()).getData(DataComponentTypes.EQUIPPABLE);
                  if (data != null) {
                     Key assetId = data.assetId();
                     if (assetId != null) {
                        key = assetId.key();
                        break block2;
                     }
                  }

                  key = null;
               }

               Key vanillaAssetId = key;
               Equippable equippable = builder.getEquippable();
               if (equippable != null) {
                  Key key3 = equippable.assetId();
                  if (key3 != null) {
                     key3 = key3.key();
                     if (key3 != null) {
                        Key it = key3;
                        int value3 = 0;
                        key3 = !Intrinsics.areEqual(it, vanillaAssetId) ? it : null;
                        if (key3 != null) {
                           Key modelKey = key3;
                           if (this.resourcePack.equipment(modelKey) != null) {
                              triple = null;
                           } else {
                              ResourcePack resourcePack2 = this.resourcePack;
                              String namespace = modelKey.namespace();
                              if (resourcePack2.unknownFile("assets/" + namespace + "/equipment/" + modelKey.value()) != null) {
                                 triple = null;
                              } else {
                                 resourcePack2 = this.resourcePack;
                                 namespace = modelKey.namespace();
                                 triple = resourcePack2.unknownFile("assets/" + namespace + "/models/equipment/" + modelKey.value()) != null ? null : new Triple(prefix, modelKey, isElytra);
                              }
                           }
                           break block;
                        }
                     }
                  }
               }

               triple = new Triple(prefix, defaultKey, isElytra);
            }
         }

         if (triple != null) {
            Object obj = triple;
            int value4 = 0;
            destination.add(obj);
         }
      }

      ObjectOpenHashSet armorPrefixes = (ObjectOpenHashSet)destination;
      this.writeArmorModels((Set)armorPrefixes);
      entrySet = (Iterable)armorPrefixes;
      destination = (Collection)(new ObjectOpenHashSet((entrySet instanceof Collection ? (Collection)entrySet : null) != null ? (entrySet instanceof Collection ? (Collection)entrySet : null).size() : 10));

      for(Object item : entrySet) {
         Triple triple2 = (Triple)item;
         int value5 = 0;
         destination.add((String)triple2.getFirst());
      }

      this.parseNexoArmorItems((Set)((ObjectOpenHashSet)destination));
      this.copyEquipmentTextures();
   }

   private final void writeArmorModels(Set<? extends Triple<String, ? extends Key, Boolean>> armorPrefixes) {
      Iterable iterable = (Iterable)armorPrefixes;

      for(Object element : iterable) {
         Triple triple = (Triple)element;
         int value = 0;
         String armorPrefix = (String)triple.component1();
         Key key = (Key)triple.component2();
         boolean isElytra = (Boolean)triple.component3();
         List equipmentLayer = Collections.singletonList(EquipmentLayer.layer(Key.key("nexo", armorPrefix)));
         if (isElytra) {
            if (this.resourcePack.equipment(key) == null) {
               this.resourcePack.equipment(Equipment.equipment(key, Collections.singletonMap(EquipmentLayerType.WINGS, (List<EquipmentLayer>)equipmentLayer)));
            }
         } else {
            Key elytraKey = KeyUtils.INSTANCE.appendSuffix(key, "_elytra");
            List equipmentEntries = CollectionsKt.minus((Iterable)ComponentCustomArmor.EntriesMappings.entries$0, EquipmentLayerType.WINGS);
            if (this.resourcePack.equipment(key) == null) {
               ResourcePack resourcePack2;
               List list;
               block: {
                  resourcePack2 = this.resourcePack;
                  if (!equipmentEntries.isEmpty()) {
                     ListIterator listIterator = equipmentEntries.listIterator(equipmentEntries.size());

                     while(listIterator.hasPrevious()) {
                        EquipmentLayerType it = (EquipmentLayerType)listIterator.previous();
                        int value2 = 0;
                        if (it == EquipmentLayerType.LLAMA_BODY) {
                           list = CollectionsKt.take((Iterable)equipmentEntries, listIterator.nextIndex() + 1);
                           break block;
                        }
                     }
                  }

                  list = CollectionsKt.emptyList();
               }

               List list2 = list;
               Iterable iterable2 = (Iterable)list2;
               LinkedHashMap result = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable2, 10)), 16));
               Map destination = (Map)result;

               for(Object obj : iterable2) {
                  EquipmentLayerType equipmentLayerType = (EquipmentLayerType)obj;
                  int value3 = 0;
                  destination.put(obj, equipmentLayer);
               }

               resourcePack2.equipment(Equipment.equipment(key, destination));
            }

            if (this.resourcePack.equipment(elytraKey) == null) {
               this.resourcePack.equipment(Equipment.equipment(elytraKey, Collections.singletonMap(EquipmentLayerType.WINGS, (List<EquipmentLayer>)equipmentLayer)));
            }

            Overlay overlay;
            List list3;
            block: {
               overlay = NexoOverlay.V1_21_5.getOverlay();
               if (!equipmentEntries.isEmpty()) {
                  ListIterator listIterator2 = equipmentEntries.listIterator(equipmentEntries.size());

                  while(listIterator2.hasPrevious()) {
                     EquipmentLayerType it = (EquipmentLayerType)listIterator2.previous();
                     int value4 = 0;
                     if (it == EquipmentLayerType.ZOMBIE_HORSE_SADDLE) {
                        list3 = CollectionsKt.take((Iterable)equipmentEntries, listIterator2.nextIndex() + 1);
                        break block;
                     }
                  }
               }

               list3 = CollectionsKt.emptyList();
            }

            List list4 = list3;
            Iterable iterable3 = (Iterable)list4;
            LinkedHashMap linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable3, 10)), 16));
            Map map = (Map)linkedHashMap;

            for(Object obj2 : iterable3) {
               EquipmentLayerType it = (EquipmentLayerType)obj2;
               int value5 = 0;
               map.put(obj2, equipmentLayer);
            }

            block: {
               overlay.equipment(Equipment.equipment(key, map));
               overlay = NexoOverlay.V1_21_6.getOverlay();
               if (!equipmentEntries.isEmpty()) {
                  ListIterator listIterator3 = equipmentEntries.listIterator(equipmentEntries.size());

                  while(listIterator3.hasPrevious()) {
                     EquipmentLayerType it = (EquipmentLayerType)listIterator3.previous();
                     int value6 = 0;
                     if (it == EquipmentLayerType.HAPPY_GHAST_BODY) {
                        list3 = CollectionsKt.take((Iterable)equipmentEntries, listIterator3.nextIndex() + 1);
                        break block;
                     }
                  }
               }

               list3 = CollectionsKt.emptyList();
            }

            list4 = list3;
            iterable3 = (Iterable)list4;
            linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable3, 10)), 16));
            map = (Map)linkedHashMap;

            for(Object obj3 : iterable3) {
               EquipmentLayerType it = (EquipmentLayerType)obj3;
               int value7 = 0;
               map.put(obj3, equipmentLayer);
            }

            block: {
               overlay.equipment(Equipment.equipment(key, map));
               overlay = NexoOverlay.V1_21_11.getOverlay();
               if (!equipmentEntries.isEmpty()) {
                  ListIterator listIterator4 = equipmentEntries.listIterator(equipmentEntries.size());

                  while(listIterator4.hasPrevious()) {
                     EquipmentLayerType it = (EquipmentLayerType)listIterator4.previous();
                     int value8 = 0;
                     if (it == EquipmentLayerType.CAMEL_HUSK_SADDLE) {
                        list3 = CollectionsKt.take((Iterable)equipmentEntries, listIterator4.nextIndex() + 1);
                        break block;
                     }
                  }
               }

               list3 = CollectionsKt.emptyList();
            }

            list4 = list3;
            iterable3 = (Iterable)list4;
            linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable3, 10)), 16));
            map = (Map)linkedHashMap;

            for(Object obj4 : iterable3) {
               EquipmentLayerType it = (EquipmentLayerType)obj4;
               int value9 = 0;
               map.put(obj4, equipmentLayer);
            }

            block: {
               overlay.equipment(Equipment.equipment(key, map));
               overlay = NexoOverlay.V26_1.getOverlay();
               if (!equipmentEntries.isEmpty()) {
                  ListIterator listIterator5 = equipmentEntries.listIterator(equipmentEntries.size());

                  while(listIterator5.hasPrevious()) {
                     EquipmentLayerType it = (EquipmentLayerType)listIterator5.previous();
                     int value10 = 0;
                     if (it == EquipmentLayerType.CAMEL_HUSK_SADDLE) {
                        list3 = CollectionsKt.take((Iterable)equipmentEntries, listIterator5.nextIndex() + 1);
                        break block;
                     }
                  }
               }

               list3 = CollectionsKt.emptyList();
            }

            list4 = list3;
            iterable3 = (Iterable)list4;
            linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable3, 10)), 16));
            map = (Map)linkedHashMap;

            for(Object obj5 : iterable3) {
               EquipmentLayerType it = (EquipmentLayerType)obj5;
               int value11 = 0;
               map.put(obj5, equipmentLayer);
            }

            block: {
               overlay.equipment(Equipment.equipment(key, map));
               overlay = NexoOverlay.V26_2.getOverlay();
               if (!equipmentEntries.isEmpty()) {
                  ListIterator listIterator6 = equipmentEntries.listIterator(equipmentEntries.size());

                  while(listIterator6.hasPrevious()) {
                     EquipmentLayerType it = (EquipmentLayerType)listIterator6.previous();
                     int value12 = 0;
                     if (it == EquipmentLayerType.HUMANOID_BABY) {
                        list3 = CollectionsKt.take((Iterable)equipmentEntries, listIterator6.nextIndex() + 1);
                        break block;
                     }
                  }
               }

               list3 = CollectionsKt.emptyList();
            }

            list4 = list3;
            iterable3 = (Iterable)list4;
            linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable3, 10)), 16));
            map = (Map)linkedHashMap;

            for(Object obj6 : iterable3) {
               EquipmentLayerType it = (EquipmentLayerType)obj6;
               int value13 = 0;
               map.put(obj6, equipmentLayer);
            }

            overlay.equipment(Equipment.equipment(key, map));
         }
      }

   }

   private final void copyEquipmentTextures() {
      Set copiedDestinations = (Set)(new LinkedHashSet());
      Set consumedSources = (Set)(new LinkedHashSet());
      Iterable entrySet = (Iterable)NexoItems.entries().entrySet();
      Collection destination = (Collection)(new ArrayList());

      for(Object element : entrySet) {
         int p0 = 0;
         Map.Entry it = (Map.Entry)element;
         int value = 0;
         CustomArmorProperties armorProperties = ComponentCustomArmor.CustomArmorProperties.Companion.getArmorProperties(it);
         if (armorProperties != null) {
            Object obj = armorProperties;
            int value2 = 0;
            destination.add(obj);
         }
      }

      entrySet = (Iterable)((List)destination);

      for(Object obj2 : entrySet) {
         CustomArmorTextures customArmor;
         String prefix;
         String itemId;
         ItemBuilder item;
         List list;
         block: {
            CustomArmorProperties customArmorProperties = (CustomArmorProperties)obj2;
            int value6 = 0;
            prefix = customArmorProperties.component1();
            itemId = customArmorProperties.component2();
            item = customArmorProperties.component3();
            customArmor = customArmorProperties.component4();
            Equippable equippable = item.getEquippable();
            if (equippable != null) {
               Key assetId = equippable.assetId();
               if (assetId != null) {
                  Key key2 = assetId;
                  ResourcePack it = this.resourcePack;
                  int value3 = 0;
                  Equipment equipment = it.equipment(key2);
                  if (equipment != null) {
                     Map layers = equipment.layers();
                     if (layers != null) {
                        Collection collection = layers.values();
                        if (collection != null) {
                           list = CollectionsKt.flatten((Iterable)collection);
                           if (list != null) {
                              break block;
                           }
                        }
                     }
                  }
               }
            }

            list = CollectionsKt.emptyList();
         }

         List equipmentTextures = list;
         Iterable iterable2 = (Iterable)equipmentTextures;
         boolean flag;
         if (iterable2 instanceof Collection && ((Collection)iterable2).isEmpty()) {
            flag = true;
         } else {
            block: {
               for(Object obj3 : iterable2) {
                  EquipmentLayer it = (EquipmentLayer)obj3;
                  int p0 = 0;
                  if (this.resourcePack.texture(it.texture()) == null) {
                     flag = false;
                     break block;
                  }
               }

               flag = true;
            }
         }

         if (!flag) {
            Key primaryKey = customArmor.fromItem(item, itemId);
            Triple[] triples = new Triple[]{new Triple(customArmor.getLayer1(), "armor_layer_1", "humanoid"), new Triple(customArmor.getLayer2(), "armor_layer_2", "humanoid_leggings"), new Triple(customArmor.getElytra(), "elytra", "wings"), new Triple(customArmor.getWolfArmor(), "wolf_armor", "wolf_body"), new Triple(customArmor.getLlamaArmor(), "llama_armor", "llama_body"), new Triple(customArmor.getHorseArmor(), "horse_armor", "horse_body"), new Triple(customArmor.getCamelSaddle(), "camel_saddle", "camel_saddle"), new Triple(customArmor.getDonkeySaddle(), "donkey_saddle", "donkey_saddle"), new Triple(customArmor.getHorseSaddle(), "horse_saddle", "horse_saddle"), new Triple(customArmor.getMuleSaddle(), "mule_saddle", "mule_saddle"), new Triple(customArmor.getPigSaddle(), "pig_saddle", "pig_saddle"), new Triple(customArmor.getSkeletonHorseSaddle(), "skeleton_horse_saddle", "skeleton_horse_saddle"), new Triple(customArmor.getStriderSaddle(), "strider_saddle", "strider_saddle"), new Triple(customArmor.getZombieHorseSaddle(), "zombie_horse_saddle", "zombie_horse_saddle"), new Triple(customArmor.getHarness(), "harness", "happy_ghast_body"), new Triple(customArmor.getNautilusSaddle(), "nautilus_saddle", "nautilus_saddle"), new Triple(customArmor.getCamelHuskSaddle(), "camel_husk_saddle", "camel_husk_saddle"), new Triple(customArmor.getHumanoidBaby(), "armor_layer_baby", "humanoid_baby")};
            Iterable iterable = (Iterable)CollectionsKt.listOf(triples);

            for(Object obj4 : iterable) {
               Triple triple = (Triple)obj4;
               int value4 = 0;
               Key customArmorKey = (Key)triple.component1();
               String fallback = (String)triple.component2();
               String path = (String)triple.component3();
               if (customArmorKey != null) {
                  Key key = Key.key("nexo:entity/equipment/" + path + "/" + prefix + ".png");
                  Key destinationKey = key;
                  if (copiedDestinations.add(destinationKey)) {
                     Texture texture2 = this.fetchTexture(customArmorKey, prefix + "_" + fallback + ".png", itemId, Intrinsics.areEqual(customArmorKey, primaryKey));
                     if (texture2 != null) {
                        Texture texture = texture2;
                        ((Collection)consumedSources).add(customArmorKey);
                        this.resourcePack.texture(destinationKey, texture.data());
                     }
                  }
               }
            }
         }
      }

      entrySet = (Iterable)consumedSources;
      destination = (Collection)(new ArrayList());

      for(Object obj5 : entrySet) {
         Key p0 = (Key)obj5;
         int value5 = 0;
         if (!copiedDestinations.contains(p0)) {
            destination.add(obj5);
         }
      }

      entrySet = (Iterable)((List)destination);
      ResourcePack resourcePack2 = this.resourcePack;

      for(Object obj6 : entrySet) {
         Key p0 = (Key)obj6;
         resourcePack2.removeTexture(p0);
      }

   }

   private final Texture fetchTexture(Key texture, String fallbackName, String itemId, boolean warn) {
        Texture texture2 = this.resourcePack.texture(texture);
        if (texture2 == null) {
            Object v3;
            block4: {
                Collection<Texture> collection = this.resourcePack.textures();
                Iterable iterable = collection;
                for (Object t : iterable) {
                    Texture it = (Texture)t;
                    boolean bl = false;
                    String string = it.key().value();
                    Intrinsics.checkNotNullExpressionValue((Object)string, "value(...)");
                    if (!Intrinsics.areEqual((Object)StringsKt.substringAfterLast(string, "/", string), (Object)fallbackName)) continue;
                    v3 = t;
                    break block4;
                }
                v3 = null;
            }
            if ((texture2 = (Texture)v3) == null) {
                ComponentCustomArmor componentCustomArmor = this;
                boolean bl = false;
                if (warn) {
                    Logs.logWarn("Failed to fetch " + texture.asString() + " used by " + itemId, false);
                }
                texture2 = null;
            }
        }
        return texture2;
    }

   private final void parseNexoArmorItems(Set<String> armorPrefixes) {
      java.util.List list;
      Map entries = NexoItems.entries();

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)entries.entrySet()) {
         int value = 0;
         String itemId = (String)element.getKey();
         ItemBuilder item = (ItemBuilder)element.getValue();
         CharSequence charSequence = (CharSequence)itemId;
         if (CustomArmorType.Companion.getItemIdRegex().matches(charSequence)) {
            CharSequence charSequence2 = (CharSequence)itemId;
            Regex itemIdRegex = CustomArmorType.Companion.getItemIdRegex();
            String str = "$1";
            String armorPrefix = itemIdRegex.replace(charSequence2, str);
            boolean hasGeneratedEquipment = item.getNexoMeta().getCustomArmorTextures() != null && armorPrefixes.contains(armorPrefix) && (item.getNexoMeta().getGenerateModel().toBoolean(false) || !item.hasItemModel());
            String armorSuffix = item.getType() != Material.ELYTRA && !item.isGlider().isTrue() ? "" : "_elytra";
            String str2 = StringsKt.removePrefix(itemId, (CharSequence)(armorPrefix + "_")).toUpperCase(Locale.ROOT);
            EquipmentSlot equipmentSlot = this.slotFromItem(str2);
            if (equipmentSlot != null) {
               EquipmentSlot slot = equipmentSlot;
               Key key = Key.key("nexo:" + armorPrefix + armorSuffix);
               Key modelKey = key;
               Equippable equippable2 = item.getEquippable();
               if (equippable2 != null) {
                  Key assetId = equippable2.assetId();
                  if (assetId != null) {
                     Key key2 = assetId;
                     int it = 0;
                     if (this.resourcePack.equipment(key2.key()) != null && (!Intrinsics.areEqual(key2.key(), modelKey) || hasGeneratedEquipment)) {
                        continue;
                     }

                     ResourcePack resourcePack2 = this.resourcePack;
                     str2 = key2.namespace();
                     if (resourcePack2.unknownFile("assets/" + str2 + "/models/equipment/" + key2.value() + ".json") != null) {
                        continue;
                     }

                     resourcePack2 = this.resourcePack;
                     str2 = key2.namespace();
                     if (resourcePack2.unknownFile("assets/" + str2 + "/equipment/" + key2.value() + ".json") != null) {
                        continue;
                     }
                  }
               }

               if (!Settings.CUSTOM_ARMOR_ASSIGN.toBool()) {
                  if (hasGeneratedEquipment) {
                     equippable2 = item.getEquippable();
                     if (!Intrinsics.areEqual(equippable2 != null ? equippable2.assetId() : null, modelKey)) {
                        Logs.logWarn("Item " + itemId + " does not have an equippable-component configured properly.", false);
                        Logs.logWarn("Nexo has been configured to use Components for custom-armor due to " + Settings.CUSTOM_ARMOR_TYPE.getPath() + " setting", false);
                        Logs.logWarn("Custom Armor will not work unless an equippable-component is set.", true);
                     }
                  }
               } else {
                  equippable2 = ItemStack.of(item.getType()).getData(DataComponentTypes.EQUIPPABLE);
                  Equippable.Builder builder;
                  if (equippable2 != null) {
                     Equippable it = equippable2;
                     int value2 = 0;
                     builder = ComponentParser.Companion.setSlot(it, EquipmentSlot.HAND);
                  } else {
                     builder = null;
                  }

                  block: {
                     Equippable.Builder vanillaComponent = builder;
                     Equippable equippable3 = item.getEquippable();
                     if (equippable3 != null) {
                        Equippable it = equippable3;
                        int value3 = 0;
                        equippable3 = !Intrinsics.areEqual(it.assetId(), modelKey) ? it : null;
                        if (equippable3 != null) {
                           builder = equippable3.toBuilder();
                           if (builder != null) {
                              break block;
                           }
                        }
                     }

                     builder = vanillaComponent;
                     if (vanillaComponent == null) {
                        builder = Equippable.equippable(EquipmentSlot.HAND);
                     }
                  }

                  Equippable.Builder component = builder;
                  if (hasGeneratedEquipment) {
                     component.assetId(modelKey);
                  } else {
                     component.assetId((Key)null);
                  }

                  if (slot == EquipmentSlot.BODY) {
                     block: {
                        Equippable equippable4 = item.getEquippable();
                        if (equippable4 != null) {
                           RegistryKeySet allowedEntities = equippable4.allowedEntities();
                           if (allowedEntities != null) {
                              list = CollectionsKt.toList((Iterable)allowedEntities);
                              break block;
                           }
                        }

                        list = null;
                     }

                     Collection collection = (Collection)list;
                     if (collection == null || collection.isEmpty()) {
                        String str3 = StringsKt.substringBefore(StringsKt.removePrefix(itemId, (CharSequence)(armorPrefix + "_")), "_", StringsKt.removePrefix(itemId, (CharSequence)(armorPrefix + "_"))).toUpperCase(Locale.ROOT);
                        String suffix = str3;
                        RegistryKey registryKey = RegistryKey.ENTITY_TYPE;
                        int value4 = 0;
                        List list2 = this.entityTypesFromItem(suffix);
                        if (list2 == null) {
                           list2 = null;
                        } else {
                           Iterable iterable = (Iterable)list2;
                           Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

                           for(Object obj2 : iterable) {
                              EntityType it = (EntityType)obj2;
                              int value5 = 0;
                              destination.add(registryKey.typedKey(it.key()));
                           }

                           list2 = (List)destination;
                        }

                        List entityTypes = list2;
                        Equippable.Builder builder2 = component;
                        RegistryKeySet registryKeySet;
                        if (entityTypes != null) {
                           value4 = 0;
                           registryKeySet = RegistrySet.keySet(RegistryKey.ENTITY_TYPE, (Iterable)entityTypes);
                           builder2 = component;
                        } else {
                           registryKeySet = null;
                        }

                        builder2.allowedEntities(registryKeySet);
                     }
                  }

                  ComponentParser.Companion companion = ComponentParser.Companion;
                  Object obj = component.build();
                  Object it = companion.setSlot((Equippable)obj, slot).build();
                  Equippable it_1 = (Equippable)it;
                  int value6 = 0;
                  Equippable equippable5 = (!Intrinsics.areEqual(it_1, item.getEquippable()) ? it_1 : null);
                  if (equippable5 != null) {
                     Equippable equippable = equippable5;
                     item.setEquippableComponent(equippable);
                     item.save();
                     Logs.logWarn("Item " + itemId + " does not have an equippable-component set.", false);
                     if (hasGeneratedEquipment) {
                        Logs.logInfo("Configured Components.equippable.model to " + modelKey + " for " + itemId);
                     } else {
                        Logs.logInfo("Configured Components.equippable.slot to " + slot + " for " + itemId);
                     }
                  }
               }
            }
         }
      }

   }

   private final List<EntityType> entityTypesFromItem(String suffix) {
      return Intrinsics.areEqual(suffix, "HARNESS") ? CollectionsKt.listOf(EntityType.HAPPY_GHAST) : CollectionsKt.listOf(EntityType.valueOf(suffix));
   }

   private final EquipmentSlot slotFromItem(String suffix) {
      return switch (suffix) {
         case "HELMET" -> EquipmentSlot.HEAD;
         case "CHESTPLATE", "ELYTRA" -> EquipmentSlot.CHEST;
         case "LEGGINGS" -> EquipmentSlot.LEGS;
         case "BOOTS" -> EquipmentSlot.FEET;
         case "HARNESS", "NAUTILUS_SADDLE", "WOLF_ARMOR", "LLAMA_ARMOR", "HORSE_ARMOR",
              "CAMEL_HUSK_SADDLE", "CAMEL_SADDLE", "PIG_SADDLE", "ZOMBIE_HORSE_SADDLE", "MULE_SADDLE",
              "STRIDER_SADDLE", "DONKEY_SADDLE", "SKELETON_HORSE_SADDLE", "HORSE_SADDLE" -> EquipmentSlot.BODY;
         default -> null;
      };
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0082\b\u0018\u0000 \u001d2\u00020\u0001:\u0001\u001dB'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0015\u001a\u00020\bHÆ\u0003J1\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bHÆ\u0001J\u0014\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u001a\u001a\u00020\u001bHÖ\u0081\u0004J\n\u0010\u001c\u001a\u00020\u0003HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001e"},
      d2 = {"Lcom/nexomc/nexo/utils/customarmor/ComponentCustomArmor$CustomArmorProperties;", "", "prefix", "", "key", "item", "Lcom/nexomc/nexo/items/ItemBuilder;", "customArmor", "Lcom/nexomc/nexo/items/customarmor/CustomArmorTextures;", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lcom/nexomc/nexo/items/ItemBuilder;Lcom/nexomc/nexo/items/customarmor/CustomArmorTextures;)V", "getPrefix", "()Ljava/lang/String;", "getKey", "getItem", "()Lcom/nexomc/nexo/items/ItemBuilder;", "getCustomArmor", "()Lcom/nexomc/nexo/items/customarmor/CustomArmorTextures;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "Companion", "com.nexomc:core"}
   )
   private static final class CustomArmorProperties {
      @NotNull
      public static final Companion Companion = new Companion();
      @NotNull
      private final String prefix;
      @NotNull
      private final String key;
      @NotNull
      private final ItemBuilder item;
      @NotNull
      private final CustomArmorTextures customArmor;

      public CustomArmorProperties(@NotNull String prefix, @NotNull String key, @NotNull ItemBuilder item, @NotNull CustomArmorTextures customArmor) {
         super();
         this.prefix = prefix;
         this.key = key;
         this.item = item;
         this.customArmor = customArmor;
      }

      @NotNull
      public final String getPrefix() {
         return this.prefix;
      }

      @NotNull
      public final String getKey() {
         return this.key;
      }

      @NotNull
      public final ItemBuilder getItem() {
         return this.item;
      }

      @NotNull
      public final CustomArmorTextures getCustomArmor() {
         return this.customArmor;
      }

      @NotNull
      public final String component1() {
         return this.prefix;
      }

      @NotNull
      public final String component2() {
         return this.key;
      }

      @NotNull
      public final ItemBuilder component3() {
         return this.item;
      }

      @NotNull
      public final CustomArmorTextures component4() {
         return this.customArmor;
      }

      @NotNull
      public final CustomArmorProperties copy(@NotNull String prefix, @NotNull String key, @NotNull ItemBuilder item, @NotNull CustomArmorTextures customArmor) {
         return new CustomArmorProperties(prefix, key, item, customArmor);
      }

      @NotNull
      public String toString() {
         return "CustomArmorProperties(prefix=" + this.prefix + ", key=" + this.key + ", item=" + this.item + ", customArmor=" + this.customArmor + ")";
      }

      public int hashCode() {
         int result = this.prefix.hashCode();
         result = result * 31 + this.key.hashCode();
         result = result * 31 + this.item.hashCode();
         result = result * 31 + this.customArmor.hashCode();
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof CustomArmorProperties)) {
            return false;
         } else {
            CustomArmorProperties customArmorProperties = (CustomArmorProperties)other;
            if (!Intrinsics.areEqual(this.prefix, customArmorProperties.prefix)) {
               return false;
            } else if (!Intrinsics.areEqual(this.key, customArmorProperties.key)) {
               return false;
            } else if (!Intrinsics.areEqual(this.item, customArmorProperties.item)) {
               return false;
            } else {
               return Intrinsics.areEqual(this.customArmor, customArmorProperties.customArmor);
            }
         }
      }

      @Metadata(
         mv = {2, 4, 0},
         k = 1,
         xi = 48,
         d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010&\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R#\u0010\u0004\u001a\u0004\u0018\u00010\u0005*\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u00068F¢\u0006\u0006\u001a\u0004\b\t\u0010\n¨\u0006\u000b"},
         d2 = {"Lcom/nexomc/nexo/utils/customarmor/ComponentCustomArmor$CustomArmorProperties$Companion;", "", "<init>", "()V", "armorProperties", "Lcom/nexomc/nexo/utils/customarmor/ComponentCustomArmor$CustomArmorProperties;", "", "", "Lcom/nexomc/nexo/items/ItemBuilder;", "getArmorProperties", "(Ljava/util/Map$Entry;)Lcom/nexomc/nexo/utils/customarmor/ComponentCustomArmor$CustomArmorProperties;", "com.nexomc:core"}
      )
      public static final class Companion {
         private Companion() {
         }

         @Nullable
         public final CustomArmorProperties getArmorProperties(@NotNull Map.Entry<String, ItemBuilder> entry) {
            CustomArmorTextures customArmorTextures = (entry.getValue()).getNexoMeta().getCustomArmorTextures();
            CustomArmorProperties customArmorProperties;
            if (customArmorTextures != null) {
               CustomArmorTextures it = customArmorTextures;
               int value = 0;
               CharSequence charSequence = (CharSequence)entry.getKey();
               Regex itemIdRegex = CustomArmorType.Companion.getItemIdRegex();
               String str = "$1";
               customArmorProperties = new CustomArmorProperties(itemIdRegex.replace(charSequence, str), entry.getKey(), entry.getValue(), it);
            } else {
               customArmorProperties = null;
            }

            return customArmorProperties;
         }
      }
   }

   public static final class EntriesMappings {
      public static final kotlin.enums.EnumEntries entries$0 = kotlin.enums.EnumEntriesKt.enumEntries(team.unnamed.creative.equipment.EquipmentLayerType.values());
   }
}
