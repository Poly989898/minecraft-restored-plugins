package com.nexomc.nexo.items;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.commands.DyeCommandKt;
import com.nexomc.nexo.compatibilities.mmoitems.WrappedMMOItem;
import com.nexomc.nexo.compatibilities.mythiccrucible.WrappedCrucibleItem;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.converter.NexoConverter;
import com.nexomc.nexo.items.customarmor.CustomArmorTextures;
import com.nexomc.nexo.mechanics.ItemModifier;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.trident.TridentFactory;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.PotionUtils;
import com.nexomc.nexo.utils.RegistryUtilsKt;
import com.nexomc.nexo.utils.TriState;
import com.nexomc.nexo.utils.logs.Logs;
import com.nexomc.nexo.utils.wrappers.AttributeWrapper;
import io.papermc.paper.registry.RegistryKey;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.model.ModelTextures;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0006\u0018\u0000 %2\u00020\u0001:\u0001%B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u001b\u001a\u00020\u001cJ\u0010\u0010\u001d\u001a\u00020\u001c2\u0006\u0010\u001e\u001a\u00020\u001cH\u0002J\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010\u001e\u001a\u00020\u001cH\u0002J\u0010\u0010!\u001a\u00020 2\u0006\u0010\u001e\u001a\u00020\u001cH\u0002J\u0010\u0010\"\u001a\u00020 2\u0006\u0010\u001e\u001a\u00020\u001cH\u0002J\u001e\u0010#\u001a\u00020\u00032\n\b\u0002\u0010$\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0002R\u001a\u0010\u0002\u001a\u00020\u0003X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\u0005R\u000e\u0010\t\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0000X\u0082\u000e¢\u0006\u0002\n\u0000R$\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0015\u001a\u00020\u0016@@X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001a¨\u0006&"},
   d2 = {"Lcom/nexomc/nexo/items/ItemParser;", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getSection$com_nexomc_core", "()Lorg/bukkit/configuration/ConfigurationSection;", "setSection$com_nexomc_core", "originalSection", "nexoMeta", "Lcom/nexomc/nexo/items/NexoMeta;", "itemId", "", "type", "Lorg/bukkit/Material;", "mmoItem", "Lcom/nexomc/nexo/compatibilities/mmoitems/WrappedMMOItem;", "crucibleItem", "Lcom/nexomc/nexo/compatibilities/mythiccrucible/WrappedCrucibleItem;", "templateItem", "value", "", "isConfigUpdated", "()Z", "setConfigUpdated$com_nexomc_core", "(Z)V", "buildItem", "Lcom/nexomc/nexo/items/ItemBuilder;", "applyConfig", "item", "parseMiscOptions", "", "parseVanillaSections", "parseNexoSection", "mergeWithTemplateSection", "templateSection", "Companion", "com.nexomc:core"}
)
public final class ItemParser {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private ConfigurationSection section;
   @NotNull
   private final ConfigurationSection originalSection;
   @NotNull
   private final NexoMeta nexoMeta;
   @NotNull
   private final String itemId;
   @NotNull
   private final Material type;
   @Nullable
   private WrappedMMOItem mmoItem;
   @Nullable
   private WrappedCrucibleItem crucibleItem;
   @Nullable
   private ItemParser templateItem;
   private boolean isConfigUpdated;
   @NotNull
   private static final Map<String, CustomModelData> CUSTOM_MODEL_DATAS_BY_ID = (Map)(new LinkedHashMap());

   public ItemParser(@NotNull ConfigurationSection section) {
      com.nexomc.nexo.items.NexoMeta nexoMeta2;
      com.nexomc.nexo.items.ItemParser itemParser;
      org.bukkit.Material material2;
      super();
      this.section = section;
      this.originalSection = this.section;
      String name = this.section.getName();
      this.itemId = name;
      if (NexoConverter.INSTANCE.processItemConfig(this.section)) {
         this.isConfigUpdated = true;
      }

      this.templateItem = ItemTemplate.INSTANCE.parserTemplates(NexoYamlKt.stringListOrSingle(this.section, "template", (String)null));
      this.section = this.mergeWithTemplateSection((ConfigurationSection)null, this.section);
      ConfigurationSection configurationSection = NexoYamlKt.section(this.section, "crucible");
      if (configurationSection != null) {
         ConfigurationSection it = configurationSection;
         int it_7 = 0;
         this.crucibleItem = new WrappedCrucibleItem(it);
      } else {
         configurationSection = NexoYamlKt.section(this.section, "mmoitem");
         if (configurationSection != null) {
            ConfigurationSection it = configurationSection;
            int it_6 = 0;
            this.mmoItem = new WrappedMMOItem(it);
         } else {
            String str = NexoYamlKt.stringOrNull(this.section, "crucible_id");
            if (str == null) {
               str = NexoYamlKt.stringOrNull(this.section, "crucible");
            }

            if (str != null) {
               String it = str;
               int value = 0;
               this.crucibleItem = new WrappedCrucibleItem(it, true);
            }
         }
      }

      block: {
         itemParser = this;
         name = NexoYamlKt.stringOrNull(this.section, "material");
         if (name != null) {
            String material = name;
            int value2 = 0;
            Material it = Material.matchMaterial(material);
            int value3 = 0;
            if (it == null) {
               Logs.logWarn(this.itemId + " is using invalid material " + material + ", defaulting to PAPER...", false);
            }

            material2 = it;
            itemParser = this;
            if (it != null) {
               break block;
            }
         }

         ItemParser itemParser2 = this.templateItem;
         material2 = itemParser2 != null ? itemParser2.type : Material.PAPER;
      }

      block: {
         this.type = material2;
         ItemParser itemParser3 = this.templateItem;
         if (itemParser3 != null) {
            NexoMeta nexoMeta3 = itemParser3.nexoMeta;
            if (nexoMeta3 != null) {
               nexoMeta2 = nexoMeta3.copy((Integer)null, (Key)null, (Key)null, (ItemModelBuilder)null, (Key)null, (Key)null, (List)null, (Key)null, (Key)null, (List)null, (Key)null, (Key)null, (Key)null, (List)null, (List)null, (Map)null, (ModelTextures)null, false, (TriState)null, (TriState)null, false, false, (TriState)null, (CustomArmorTextures)null, false, false, 0.0F);
               if (nexoMeta2 != null) {
                  break block;
               }
            }
         }

         nexoMeta2 = NexoMeta.Companion.getDEFAULT();
      }

      this.nexoMeta = nexoMeta2;
      configurationSection = NexoYamlKt.section(this.section, "Pack");
      if (configurationSection != null) {
         ConfigurationSection it = configurationSection;
         int value4 = 0;
         this.nexoMeta.packInfo(it);
         Integer customModelData = this.nexoMeta.getCustomModelData();
         if (customModelData != null) {
            Integer itemKey = customModelData;
            int it_5 = ((Number)itemKey).intValue();
            int it_4 = 0;
            CUSTOM_MODEL_DATAS_BY_ID.put(this.itemId, new CustomModelData(this.type, it_4, this.nexoMeta));
         }
      }

      configurationSection = NexoYamlKt.section(this.section, "ItemModel");
      if (configurationSection != null) {
         ConfigurationSection it = configurationSection;
         int value5 = 0;
         Key key = NexoYamlKt.key(this.section, "Components.item_model");
         if (key == null) {
            key = Key.key("nexo:" + NexoYamlKt.getRootId(this.section));
         }

         Key itemKey = key;
         NexoMeta nexoMeta4 = this.nexoMeta;
         PackFormat packFormat = PackFormat.format(NMSHandlers.handler().resourcepackFormat());
         nexoMeta4.setItemModel(new ItemModelBuilder(itemKey, it, packFormat));
      }

      this.nexoMeta.ensureCustomArmorTextures(this.section);
   }

   @NotNull
   public final ConfigurationSection getSection$com_nexomc_core() {
      return this.section;
   }

   public final void setSection$com_nexomc_core(@NotNull ConfigurationSection configurationSection) {
      this.section = configurationSection;
   }

   public final boolean isConfigUpdated() {
      return this.isConfigUpdated;
   }

   public final void setConfigUpdated$com_nexomc_core(boolean flag) {
      this.isConfigUpdated = flag;
   }

   @NotNull
   public final ItemBuilder buildItem() {
      WrappedCrucibleItem wrappedCrucibleItem = this.crucibleItem;
      ItemBuilder itemBuilder;
      if (wrappedCrucibleItem != null) {
         WrappedCrucibleItem p0 = wrappedCrucibleItem;
         int value = 0;
         itemBuilder = new ItemBuilder(p0);
      } else {
         WrappedMMOItem wrappedMMOItem = this.mmoItem;
         if (wrappedMMOItem != null) {
            WrappedMMOItem p0 = wrappedMMOItem;
            int value2 = 0;
            itemBuilder = new ItemBuilder(p0);
         } else {
            itemBuilder = new ItemBuilder(this.type);
         }
      }

      ItemBuilder item = itemBuilder;
      return this.applyConfig(item);
   }

   private final ItemBuilder applyConfig(ItemBuilder item) {
      String str = NexoYamlKt.stringOrNull(this.section, "itemname");
      if (str != null) {
         Component component = AdventureUtilsKt.deserialize(str);
         if (component != null) {
            Component p0 = component;
            int value = 0;
            item.itemName(p0);
         }
      }

      str = NexoYamlKt.stringOrNull(this.section, "customname");
      if (str != null) {
         Component component2 = AdventureUtilsKt.deserialize(str);
         if (component2 != null) {
            Component p0 = component2;
            int value2 = 0;
            item.customName(p0);
         }
      }

      Collection stringList = (Collection)this.section.getStringList("lore");
      Collection collection;
      if (stringList.isEmpty()) {
         int value3 = 0;
         collection = null;
      } else {
         collection = stringList;
      }

      List list = (List)collection;
      if (list != null) {
         Iterable iterable = (Iterable)list;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object obj : iterable) {
            String it = (String)obj;
            int value4 = 0;
            AdventureUtils adventureUtils = AdventureUtils.INSTANCE;
            destination.add(adventureUtils.setDefaultStyle(AdventureUtilsKt.deserialize(it), net.kyori.adventure.text.format.NamedTextColor.WHITE));
         }

         List p0 = (List)destination;
         int value8 = 0;
         item.lore(p0);
      }

      String str2 = NexoYamlKt.stringOrNull(this.section, "color");
      if (str2 != null) {
         Color color = DyeCommandKt.toColor(str2);
         if (color != null) {
            Color p0 = color;
            int value5 = 0;
            item.setColor(p0);
         }
      }

      Key key = NexoYamlKt.key(this.section, "trim_pattern");
      if (key != null) {
         Key p0 = key;
         int value6 = 0;
         item.setTrimPattern(p0);
      }

      key = NexoYamlKt.key(this.section, "trim_material");
      if (key != null) {
         Key p0 = key;
         int value7 = 0;
         item.setTrimMaterial(p0);
      }

      if (this.section.contains("unbreakable")) {
         item.setUnbreakable(this.section.getBoolean("unbreakable", false));
      }

      (new ComponentParser(this.section, item)).parseComponents();
      this.parseMiscOptions(item);
      this.parseVanillaSections(item);
      this.parseNexoSection(item);
      item.nexoMeta(this.nexoMeta);
      return item;
   }

   private final void parseMiscOptions(ItemBuilder item) {
      if (!ItemTemplate.INSTANCE.isTemplate(NexoYamlKt.getRootId(this.section)) && this.section.getBoolean("injectId", true)) {
         NamespacedKey iteM_ID = NexoItems.INSTANCE.getITEM_ID();
         PersistentDataType persistentDataType = PersistentDataType.STRING;
         item.customTag(iteM_ID, persistentDataType, this.itemId);
      }

      Iterable iterable = (Iterable)NexoYamlKt.stringListOrSingle(this.section, "tag", (String)null);
      if (CollectionsKt.count(iterable) > 0) {
         Collection p0 = (Collection)iterable;
         int value = 0;
         item.addNexoTags(p0);
      }

      this.nexoMeta.setNoUpdate(this.section.getBoolean("no_auto_update", false));
      this.nexoMeta.setDisableEnchanting(this.section.getBoolean("disable_enchanting", false));
      this.nexoMeta.setExcludedFromInventory(NexoYamlKt.triState(this.section, "excludeFromInventory"));
      this.nexoMeta.setExcludedFromCommands(NexoYamlKt.triState(this.section, "excludeFromCommands"));
   }

   private final void parseVanillaSections(ItemBuilder item) {
      try {
      org.bukkit.inventory.EquipmentSlotGroup equipmentSlotGroup;
      Iterable iterable = (Iterable)NexoYamlKt.linkedMapList(this.section, "PotionEffects", kotlin.collections.CollectionsKt.emptyList());

      for(Object element : iterable) {
         LinkedHashMap it = (LinkedHashMap)element;
         int value2 = 0;
         PotionUtils potionUtils = PotionUtils.INSTANCE;
         Object obj4 = it.get("type");
         Object obj = obj4;
         if (!(obj4 instanceof String)) {
            obj = null;
         }

         PotionEffectType effectType = potionUtils.getEffectType((String)obj);
         if (effectType != null) {
            PotionEffectType key = effectType;
            int value3 = 0;
            ((Map)it).put("effect", key.getKey().getKey());
         }

         item.addPotionEffect(new PotionEffect((Map)it));
      }

      iterable = (Iterable)NexoYamlKt.sectionListOrSingle(this.section, "PersistentData");

      for(Object obj5 : iterable) {
         ConfigurationSection data = (ConfigurationSection)obj5;
         int value4 = 0;
         NamespacedKey namespacedKey = NexoYamlKt.namespacedKey(data, "key");
         if (namespacedKey != null) {
            NamespacedKey key = namespacedKey;
            Field typeField = DataType.class.getDeclaredField(NexoYamlKt.stringOrEmpty(data, "type"));
            Object obj6 = typeField.get((Object)null);
            Object obj2 = obj6;
            if (!(obj6 instanceof PersistentDataType)) {
               obj2 = null;
            }

            PersistentDataType persistentDataType = (PersistentDataType)obj2;
            if (persistentDataType != null) {
               PersistentDataType dataType = persistentDataType;
               Object obj3 = data.get("value");
               if (obj3 != null) {
                  Object value = obj3;
                  item.customTag(key, dataType, value);
               }
            }
         }
      }

      iterable = (Iterable)NexoYamlKt.sectionListOrSingle(this.section, "AttributeModifiers");

      for(Object obj7 : iterable) {
         ConfigurationSection modifier = (ConfigurationSection)obj7;
         int value5 = 0;
         Attribute attribute2 = AttributeWrapper.Companion.fromString(NexoYamlKt.stringOrNull(modifier, "attribute"));
         if (attribute2 == null) {
            return;
         }

         Attribute attribute = attribute2;
         Double number = NexoYamlKt.doubleOrNull(modifier, "amount");
         if (number == null) {
            return;
         }

         double typeField = number;
         String str3 = "operation";
         AttributeModifier.Operation operation2 = (AttributeModifier.Operation)EnumUtils.getEnum(AttributeModifier.Operation.class, NexoYamlKt.stringOrNull(modifier, str3));
         if (operation2 == null) {
            operation2 = Operation.ADD_NUMBER;
         }

         AttributeModifier.Operation operation = operation2;
         NamespacedKey namespacedKey2 = NexoYamlKt.namespacedKey(modifier, "key");
         if (namespacedKey2 == null) {
            String str = this.itemId;
            namespacedKey2 = new NamespacedKey("nexo", str + "_" + attribute.key().value());
         }

         NamespacedKey key;
         block: {
            key = namespacedKey2;
            String str2 = NexoYamlKt.stringOrNull(modifier, "slot");
            if (str2 != null) {
               String p0 = str2;
               int value6 = 0;
               equipmentSlotGroup = EquipmentSlotGroup.getByName(p0);
               if (equipmentSlotGroup != null) {
                  break block;
               }
            }

            equipmentSlotGroup = EquipmentSlotGroup.ANY;
         }

         EquipmentSlotGroup slot = equipmentSlotGroup;
         ConfigurationSection display = NexoYamlKt.section(modifier, "display");
         AttributeModifier modifier_3 = new AttributeModifier(key, typeField, operation, slot);
         item.addAttributeModifier(new AttributeWrapper(attribute, modifier_3, slot, display));
      }

      ConfigurationSection configurationSection = NexoYamlKt.section(this.section, "Enchantments");
      if (configurationSection != null) {
         Set keys = configurationSection.getKeys(false);
         if (keys != null) {
            Iterable iterable2 = (Iterable)keys;

            for(Object obj8 : iterable2) {
               String enchant = (String)obj8;
               int amount = 0;
               RegistryKey registryKey = RegistryKey.ENCHANTMENT;
               Enchantment enchantment2 = (Enchantment)RegistryUtilsKt.toRegistry(registryKey).get(Key.key(enchant));
               if (enchantment2 != null) {
                  Enchantment enchantment = enchantment2;
                  item.addEnchant(enchantment, this.section.getInt("Enchantments." + enchant));
               }
            }
         }
      }

      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private final void parseNexoSection(ItemBuilder item) {
      if (item.getType() == Material.TRIDENT) {
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;

         MechanicFactory mechanicFactory;
         block: {
            for(MechanicFactory mechanicFactory3 : (java.lang.Iterable<MechanicFactory>)(java.lang.Iterable)(Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()) {
               int value = 0;
               mechanicFactory = mechanicFactory3;
               if (!(mechanicFactory3 instanceof TridentFactory)) {
                  mechanicFactory = null;
               }

               mechanicFactory3 = (TridentFactory)mechanicFactory;
               if (mechanicFactory3 != null) {
                  mechanicFactory = mechanicFactory3;
                  break block;
               }
            }

            mechanicFactory = null;
         }

         mechanicFactory = mechanicFactory;
         if ((TridentFactory)mechanicFactory != null) {
            mechanicFactory.parse(this.section);
         }
      }

      ConfigurationSection configurationSection = NexoYamlKt.section(this.section, "Mechanics");
      if (configurationSection != null) {
         Map map = NexoYamlKt.childSections(configurationSection);
         if (map != null) {
            Map map2 = map;

            for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
               int value2 = 0;
               String factoryId = (String)element.getKey();
               ConfigurationSection section = (ConfigurationSection)element.getValue();
               boolean hasVariation = section.isInt("custom_variation");
               MechanicFactory mechanicFactory2 = MechanicsManager.INSTANCE.mechanicFactory(factoryId);
               if (mechanicFactory2 != null) {
                  Mechanic mechanic2 = mechanicFactory2.parse(section);
                  if (mechanic2 != null) {
                     Mechanic mechanic = mechanic2;
                     ItemModifier[] itemModifiers = mechanic.getItemModifiers();
                     int value3 = 0;

                     for(int i = itemModifiers.length; value3 < i; ++value3) {
                        ItemModifier modifier = itemModifiers[value3];
                        modifier.modify(item);
                     }

                     if (!hasVariation && section.isInt("custom_variation")) {
                        this.section.set("Mechanics.custom_block.custom_variation", section.getInt("custom_variation"));
                        this.originalSection.set("Mechanics.custom_block.custom_variation", section.getInt("custom_variation"));
                        this.isConfigUpdated = true;
                     }
                  }
               }
            }
         }
      }

      if (this.nexoMeta.getContainsPackInfo() || this.nexoMeta.getItemModel() != null) {
         if (!item.hasItemModel() && !ItemTemplate.INSTANCE.isTemplate(this.itemId) && (Settings.PACK_PREFER_ITEMMODELS.toBool() || this.nexoMeta.getItemModel() != null)) {
            item.setItemModel(Key.key("nexo:" + this.itemId));
         }

         CustomModelData customModelData2 = (CustomModelData)CUSTOM_MODEL_DATAS_BY_ID.get(this.section.getName());
         int value4;
         if (customModelData2 != null) {
            value4 = customModelData2.getCustomModelData();
         } else {
            ItemParser it = this;
            int value5 = 0;
            Integer number;
            if (!item.hasItemModel() && !item.hasCustomModelData() && !ItemTemplate.INSTANCE.isTemplate(this.itemId)) {
               CustomModelData.Companion companion = CustomModelData.Companion;
               Key model = this.nexoMeta.getModel();
               if (model == null) {
                  Object obj = null;
                  number = (Integer)obj;
               } else {
                  Integer number2 = companion.generateId(model, this.type);
                  int cmd = ((Number)number2).intValue();
                  int value6 = 0;
                  if (!Settings.DISABLE_AUTOMATIC_MODEL_DATA.toBool()) {
                     this.section.set("Pack.custom_model_data", cmd);
                     this.originalSection.set("Pack.custom_model_data", cmd);
                     this.isConfigUpdated = true;
                  }

                  number = number2;
               }
            } else {
               number = null;
            }

            if (number == null) {
               return;
            }

            value4 = number;
         }

         int customModelData = value4;
         item.customModelData(customModelData);
         this.nexoMeta.setCustomModelData(customModelData);
      }
   }

   private final ConfigurationSection mergeWithTemplateSection(ConfigurationSection templateSection, ConfigurationSection section) {
      ConfigurationSection configurationSection = (new YamlConfiguration()).createSection(section.getName());
      ConfigurationSection sectionCopy = NexoYamlKt.copyFrom(configurationSection, section);
      configurationSection = (new YamlConfiguration()).createSection(section.getName());
      if (templateSection == null) {
         return sectionCopy;
      } else {
         ConfigurationSection it = NexoYamlKt.remove(NexoYamlKt.copyFrom(configurationSection, templateSection), "injectId");
         int value = 0;
         if (!ItemTemplate.INSTANCE.isTemplate(NexoYamlKt.getRootId(section))) {
            ItemTemplate.INSTANCE.replacePlaceholders(it, sectionCopy);
         }

         return NexoYamlKt.copyFrom(it, sectionCopy);
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/items/ItemParser$Companion;", "", "<init>", "()V", "CUSTOM_MODEL_DATAS_BY_ID", "", "", "Lcom/nexomc/nexo/items/CustomModelData;", "getCUSTOM_MODEL_DATAS_BY_ID", "()Ljava/util/Map;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Map<String, CustomModelData> getCUSTOM_MODEL_DATAS_BY_ID() {
         return ItemParser.CUSTOM_MODEL_DATAS_BY_ID;
      }
   }
}
