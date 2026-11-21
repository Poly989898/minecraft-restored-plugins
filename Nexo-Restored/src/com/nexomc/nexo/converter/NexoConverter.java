package com.nexomc.nexo.converter;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.items.ItemTemplate;
import com.nexomc.nexo.recipes.RecipeType;
import com.nexomc.nexo.recipes.RecipesManager;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.inventories.NexoInventory;
import com.nexomc.nexo.utils.logs.Logs;
import java.io.File;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import kotlin.time.DurationKt;
import kotlin.time.DurationUnit;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007J\f\u0010\u000b\u001a\u00020\f*\u00020\u0007H\u0002J\u000e\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\u0012\u001a\u00020\u0005J\u000e\u0010\u0013\u001a\u00020\u00052\u0006\u0010\u0014\u001a\u00020\u0015J\u000e\u0010\u0016\u001a\u00020\u00052\u0006\u0010\u0017\u001a\u00020\u0015J\u000e\u0010\u0018\u001a\u00020\u00052\u0006\u0010\u0019\u001a\u00020\u0015R\u001a\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\f0\u000eX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001a"},
   d2 = {"Lcom/nexomc/nexo/converter/NexoConverter;", "", "<init>", "()V", "processItemConfigs", "", "configSection", "Lorg/bukkit/configuration/ConfigurationSection;", "processItemConfig", "", "section", "saveToYamlString", "", "ITEM_FLAG_TO_COMPONENT", "", "processGlyphConfigs", "glyphFile", "Ljava/io/File;", "processRecipes", "processSettings", "settings", "Lorg/bukkit/configuration/file/YamlConfiguration;", "processInventory", "inventory", "processSounds", "sounds", "com.nexomc:core"}
)
public final class NexoConverter {
   @NotNull
   public static final NexoConverter INSTANCE = new NexoConverter();
   @NotNull
   private static final Map<String, String> ITEM_FLAG_TO_COMPONENT;

   private NexoConverter() {
   }

   public final void processItemConfigs(@NotNull ConfigurationSection configSection) {
      Map map = NexoYamlKt.childSections(configSection);

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value = 0;
         String itemId = (String)element.getKey();
         ConfigurationSection section = (ConfigurationSection)element.getValue();
         INSTANCE.processItemConfig(section);
      }

   }

   public final boolean processItemConfig(@NotNull ConfigurationSection section) {
      String itemId = NexoYamlKt.getRootId(section);
      ConfigurationSection original = NexoYamlKt.clone(section);
      if (section.getBoolean("template")) {
         NexoYamlKt.remove(section, "template");
      }

      String str = NexoYamlKt.stringOrNull(section, "material");
      if (str != null) {
         str = str.toUpperCase(Locale.ROOT);
      } else {
         str = null;
      }

      section.set("material", str);
      ConfigurationSection configurationSection = NexoYamlKt.section(section, "Components.durability");
      if (configurationSection != null) {
         ConfigurationSection durability = configurationSection;
         int value = 0;
         configurationSection = durability.getParent();
         configurationSection.set(durability.getName(), durability.getInt("value"));
         Unit unit = Unit.INSTANCE;
         unit = Unit.INSTANCE;
      } else {
         Object obj = null;
      }

      NexoYamlKt.rename(section, "Components.jukebox_playable.song_key", "Components.jukebox_playable");
      NexoYamlKt.rename(section, "Components.durability", "Components.max_damage");
      NexoYamlKt.rename(section, "Components.equippable.model", "Components.equippable.asset_id");
      NexoYamlKt.rename(section, "Components.equippable.allowed_entities", "Components.equippable.allowed_entity_types");
      Duration.Companion companion = Duration.Companion;
      Duration it = Duration.box_impl(DurationKt.toDuration(section.getDouble("Components.use_cooldown.seconds"), DurationUnit.SECONDS));
      long p0 = it.unbox_impl();
      int value25 = 0;
      Duration duration5 = Duration.isPositive_impl(p0) ? it : null;
      if (duration5 != null) {
         double it_34 = Duration.toDouble_impl(duration5.unbox_impl(), DurationUnit.SECONDS);
         int value2 = 0;
         NexoYamlKt.remove(section, "Components.use_cooldown.seconds");
         section.set("Components.use_cooldown.duration", it_34 + "s");
         Unit unit2 = Unit.INSTANCE;
         unit2 = Unit.INSTANCE;
      } else {
         Object obj2 = null;
      }

      companion = Duration.Companion;
      it = Duration.box_impl(DurationKt.toDuration(section.getDouble("Components.food.eat_seconds"), DurationUnit.SECONDS));
      p0 = it.unbox_impl();
      value25 = 0;
      duration5 = Duration.isPositive_impl(p0) ? it : null;
      if (duration5 != null) {
         double it_33 = Duration.toDouble_impl(duration5.unbox_impl(), DurationUnit.SECONDS);
         int value3 = 0;
         NexoYamlKt.remove(section, "Components.food.eat_seconds");
         section.set("Components.food.eat_duration", it_33 + "s");
         Unit unit3 = Unit.INSTANCE;
         unit3 = Unit.INSTANCE;
      } else {
         Object obj3 = null;
      }

      companion = Duration.Companion;
      it = Duration.box_impl(DurationKt.toDuration(section.getDouble("Components.consumable.consume_seconds"), DurationUnit.SECONDS));
      p0 = it.unbox_impl();
      value25 = 0;
      duration5 = Duration.isPositive_impl(p0) ? it : null;
      if (duration5 != null) {
         double it_32 = Duration.toDouble_impl(duration5.unbox_impl(), DurationUnit.SECONDS);
         int value4 = 0;
         NexoYamlKt.remove(section, "Components.consumable.consume_seconds");
         section.set("Components.consumable.consume_duration", it_32 + "s");
         Unit unit4 = Unit.INSTANCE;
         unit4 = Unit.INSTANCE;
      } else {
         Object obj4 = null;
      }

      companion = Duration.Companion;
      it = Duration.box_impl(DurationKt.toDuration(section.getLong("Mechanics.commands.cooldown"), DurationUnit.MILLISECONDS));
      p0 = it.unbox_impl();
      value25 = 0;
      duration5 = Duration.isPositive_impl(p0) ? it : null;
      if (duration5 != null) {
         float it_31 = DurationUtilsKt.toFloat_VtjQ1oo(duration5.unbox_impl(), DurationUnit.SECONDS);
         value25 = 0;
         section.set("Mechanics.commands.cooldown", it_31 + "s");
         Unit unit5 = Unit.INSTANCE;
         unit5 = Unit.INSTANCE;
      } else {
         Object obj5 = null;
      }

      block: {
         ConfigurationSection configurationSection2 = NexoYamlKt.section(section, "Mechanics.custom");
         if (configurationSection2 != null) {
            Map map = NexoYamlKt.childSections(configurationSection2);
            if (map != null) {
               Map map4 = map;

               for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map4.entrySet()) {
                  int value5 = 0;
                  ConfigurationSection section_30 = (ConfigurationSection)element.getValue();
                  Integer number = NexoYamlKt.intOrNull(section_30, "cooldown");
                  if (number != null) {
                     int it_29 = ((Number)number).intValue();
                     int value6 = 0;
                     Duration.Companion companion2 = Duration.Companion;
                     long value7 = DurationKt.toDuration(it_29, DurationUnit.MILLISECONDS);
                     String seconds = DurationUtilsKt.toFloat_VtjQ1oo(value7, DurationUnit.SECONDS) + "s";
                     section_30.set("cooldown", seconds);
                     Unit unit6 = Unit.INSTANCE;
                     unit6 = Unit.INSTANCE;
                  } else {
                     Object obj6 = null;
                  }
               }

               Unit unit7 = Unit.INSTANCE;
               break block;
            }
         }

         Object obj7 = null;
      }

      Integer number2 = NexoYamlKt.intOrNull(section, "Mechanics.commands.cooldown");
      if (number2 != null) {
         int it_28 = ((Number)number2).intValue();
         int value8 = 0;
         Duration.Companion companion3 = Duration.Companion;
         long value9 = DurationKt.toDuration(it_28, DurationUnit.MILLISECONDS);
         String seconds = DurationUtilsKt.toFloat_VtjQ1oo(value9, DurationUnit.SECONDS) + "s";
         section.set("Mechanics.commands.cooldown", seconds);
         Unit unit8 = Unit.INSTANCE;
         unit8 = Unit.INSTANCE;
      } else {
         Object obj8 = null;
      }

      block: {
         ConfigurationSection configurationSection3 = NexoYamlKt.section(section, "Mechanics.armor_effects");
         if (configurationSection3 != null) {
            Map map2 = NexoYamlKt.childSections(configurationSection3);
            if (map2 != null) {
               Map map5 = map2;

               for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map5.entrySet()) {
                  int value10 = 0;
                  ConfigurationSection section_27 = (ConfigurationSection)entry.getValue();
                  Integer number3 = section_27.getInt("duration");
                  int it_26 = ((Number)number3).intValue();
                  int value11 = 0;
                  Integer number4 = it_26 != 0 ? number3 : null;
                  if ((it_26 != 0 ? number3 : null) != null) {
                     int ticks = number4;
                     byte index = 20;
                     it_26 = ticks % index;
                     if (it_26 + (index & ((it_26 ^ index) & (it_26 | -it_26)) >> 31) == 0) {
                        section_27.set("duration", ticks / 20 + "s");
                     } else {
                        section_27.set("duration", ticks + "t");
                     }
                  }
               }

               Unit unit9 = Unit.INSTANCE;
               break block;
            }
         }

         Object obj9 = null;
      }

      number2 = NexoYamlKt.intOrNull(section, "Mechanics.furntiure.evolution.delay");
      if (number2 != null) {
         int it_25 = ((Number)number2).intValue();
         int value12 = 0;
         Duration.Companion companion4 = Duration.Companion;
         long value13 = DurationKt.toDuration(it_25, DurationUnit.MILLISECONDS);
         String seconds = DurationUtilsKt.toFloat_VtjQ1oo(value13, DurationUnit.SECONDS) + "s";
         section.set("Mechanics.furniture.evolution.delay", seconds);
         Unit unit10 = Unit.INSTANCE;
         unit10 = Unit.INSTANCE;
      } else {
         Object obj10 = null;
      }

      block: {
         ConfigurationSection configurationSection4 = NexoYamlKt.section(section, "Mechanics.misc");
         if (configurationSection4 != null) {
            ConfigurationSection it_24 = configurationSection4;
            value25 = 0;
            configurationSection4 = it_24.contains("burns_in_lava") || it_24.contains("burns_in_fire") ? it_24 : null;
            if (configurationSection4 != null) {
               ConfigurationSection it_23 = configurationSection4;
               value25 = 0;
               NexoYamlKt.remove(NexoYamlKt.remove(it_23, "burns_in_lava"), "burns_in_fire");
               if (it_23.getKeys(false).isEmpty()) {
                  NexoYamlKt.remove(section, "Mechanics.misc");
               }

               configurationSection4 = NexoYamlKt.section(section, "Mechanics");
               Collection collection = (Collection)(configurationSection4 != null ? configurationSection4.getKeys(false) : null);
               if (collection == null || collection.isEmpty()) {
                  NexoYamlKt.remove(section, "Mechanics");
               }

               section.set("Components.damage_resistant", "is_fire");
               Unit unit11 = Unit.INSTANCE;
               unit11 = Unit.INSTANCE;
               break block;
            }
         }

         Object obj11 = null;
      }

      block: {
         NexoYamlKt.remove(section, "Pack.generate_model");
         List list = NexoYamlKt.stringListOrNull(section, "Pack.textures");
         if (list != null) {
            list = CollectionsKt.distinct((Iterable)list);
            if (list != null) {
               String str2 = (String)CollectionsKt.singleOrNull(list);
               if (str2 != null) {
                  String it_22 = str2;
                  int value26 = 0;
                  NexoYamlKt.rename(section, "Pack.textures", "Pack.texture").set("Pack.texture", it_22);
                  Unit unit12 = Unit.INSTANCE;
                  unit12 = Unit.INSTANCE;
                  break block;
               }
            }
         }

         Object obj12 = null;
      }

      NexoYamlKt.rename(section, "Mechanics.custom_block.directional.directional_type", "Mechanics.custom_block.directional.type");
      NexoConverter nexoConverter = this;

      Object result1;
      try {
         NexoConverter nexoConverter2 = nexoConverter;
         int it_19 = 0;
         ConfigurationSection configurationSection5 = NexoYamlKt.section(section, "Mechanics.furniture");
         if (configurationSection5 != null) {
            block: {
               ConfigurationSection furnitureSection = configurationSection5;
               NexoYamlKt.rename(furnitureSection, "display_entity_properties", "properties");
               NexoYamlKt.rename(furnitureSection, "hitbox.barrierHitboxes", "hitbox.barriers");
               if (furnitureSection.getBoolean("barrier") || furnitureSection.getBoolean("barriers")) {
                  NexoYamlKt.remove(furnitureSection, "barrier");
                  NexoYamlKt.remove(furnitureSection, "barriers");
                  if (furnitureSection.contains("hitbox.barriers")) {
                     furnitureSection.set("hitbox.barriers", "0,0,0");
                  }
               }

               NexoYamlKt.remove(furnitureSection, "type");
               List list2 = NexoYamlKt.stringListOrNull(furnitureSection, "lights");
               if (list2 != null) {
                  List list4 = list2;
                  int value14 = 0;
                  furnitureSection.set("lights", (Object)null);
                  furnitureSection.set("lights.lights", list4);
               } else {
                  Object obj13 = null;
               }

               ConfigurationSection propertiesSection;
               block2: {
                  NexoYamlKt.rename(furnitureSection, "lights_model", "lights.toggled_model");
                  NexoYamlKt.rename(furnitureSection, "lights_item_model", "lights.toggled_item_model");
                  NexoYamlKt.rename(furnitureSection, "lights_toggleable", "lights.toggleable");
                  propertiesSection = NexoYamlKt.section(furnitureSection, "properties");
                  if (propertiesSection != null) {
                     ConfigurationSection configurationSection6 = NexoYamlKt.section(propertiesSection, "scale");
                     if (configurationSection6 != null) {
                        ConfigurationSection it_17 = configurationSection6;
                        int value15 = 0;
                        String destination = Intrinsics.areEqual(propertiesSection.getString("display_transform"), "FIXED") ? "1.0" : "0.5";
                        Object obj14 = it_17.get("x", destination);
                        String scale = obj14 + "," + it_17.get("y", destination) + "," + it_17.get("z", destination);
                        propertiesSection.set("scale", scale);
                        Unit unit13 = Unit.INSTANCE;
                        unit13 = Unit.INSTANCE;
                        break block2;
                     }
                  }

                  Object obj15 = null;
               }

               block2: {
                  if (propertiesSection != null) {
                     Integer number5 = NexoYamlKt.intOrNull(propertiesSection, "delay");
                     if (number5 != null) {
                        Duration duration = Duration.box_impl(DurationUtilsKt.getTicks(number5));
                        long p0_16 = duration.unbox_impl();
                        int value16 = 0;
                        Duration duration2 = Duration.isPositive_impl(p0_16) ? duration : null;
                        if (duration2 != null) {
                           int it_15 = DurationUtilsKt.getTicks_LRDsOJo(duration2.unbox_impl());
                           value16 = 0;
                           propertiesSection.set("delay", it_15 + "t");
                           Unit unit14 = Unit.INSTANCE;
                           unit14 = Unit.INSTANCE;
                           break block2;
                        }
                     }
                  }

                  Object obj16 = null;
               }

               Integer number6 = NexoYamlKt.intOrNull(furnitureSection, "door.open_properties.delay");
               if (number6 != null) {
                  Duration duration3 = Duration.box_impl(DurationUtilsKt.getTicks(number6));
                  long p0_14 = duration3.unbox_impl();
                  int value17 = 0;
                  Duration duration4 = Duration.isPositive_impl(p0_14) ? duration3 : null;
                  if (duration4 != null) {
                     int it_13 = DurationUtilsKt.getTicks_LRDsOJo(duration4.unbox_impl());
                     value17 = 0;
                     furnitureSection.set("door.open_properties.delay", it_13 + "t");
                     Unit unit15 = Unit.INSTANCE;
                     unit15 = Unit.INSTANCE;
                     break block;
                  }
               }

               Object obj17 = null;
            }
         }

         result1 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         result1 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(result1);
      if (throwable != null) {
         Throwable it_12 = throwable;
         int furnitureSection = 0;
         Logs.logError("Failed to convert " + itemId + ": " + it_12.getMessage(), false);
         if (Settings.DEBUG.toBool()) {
            Logs.logError(ExceptionsKt.stackTraceToString(it_12), false);
         }
      }

      if (section.contains("AttributeModifiers")) {
         Iterable iterable = (Iterable)NexoYamlKt.sectionListOrSingle(section, "AttributeModifiers");
         String str3 = "AttributeModifiers";
         value25 = 0;

         for(Object obj21 : iterable) {
            ConfigurationSection modifier = (ConfigurationSection)obj21;
            int value18 = 0;
            Integer number7 = NexoYamlKt.intOrNull(modifier, "operation");
            if (number7 != null) {
               int value19 = number7;
               AttributeModifier.Operation orNull = (AttributeModifier.Operation)CollectionsKt.getOrNull((List)NexoConverter.EntriesMappings.entries$0, value19);
               if (orNull == null) {
                  orNull = Operation.ADD_NUMBER;
               }

               AttributeModifier.Operation operation = orNull;
               modifier.set("operation", operation.name());
               ConfigurationSection configurationSection7 = NexoYamlKt.remove(NexoYamlKt.remove(modifier, "uuid"), "name");
               String key = "key";
               String str4 = "nexo:modifier";
               Object obj18 = configurationSection7.get(key);
               if (!(obj18 instanceof String)) {
                  obj18 = null;
               }

               String str5 = (String)obj18;
               if (str5 != null) {
                  Object obj22 = str5;
                  int value20 = 0;
                  int value21 = 0;
                  if (str4.equals(obj22)) {
                     configurationSection7.set(key, (Object)null);
                  }

                  Unit unit16 = Unit.INSTANCE;
                  unit16 = Unit.INSTANCE;
               } else {
                  Object obj19 = null;
               }

               str5 = NexoYamlKt.stringOrNull(modifier, "slot");
               if (str5 != null) {
                  String slot = str5;
                  if (Intrinsics.areEqual(slot, "HAND")) {
                     modifier.set("slot", "MAINHAND");
                  }
               }
            }
         }

         section.set(str3, iterable);
      }

      List list3 = NexoYamlKt.stringListOrNull(section, "ItemFlags");
      if (list3 != null) {
         Iterable iterable2 = (Iterable)list3;
         if (CollectionsKt.count(iterable2) > 0) {
            List flags = (List)iterable2;
            value25 = 0;
            Iterable existing = (Iterable)flags;
            Map map3 = ITEM_FLAG_TO_COMPONENT;
            Collection collection2 = (Collection)(new ArrayList());

            for(Object obj23 : existing) {
               int value22 = 0;
               String p0_10 = (String)obj23;
               int value23 = 0;
               String str6 = (String)map3.get(p0_10);
               if (str6 != null) {
                  Object obj24 = str6;
                  int value24 = 0;
                  collection2.add(obj24);
               }
            }

            List mapped = (List)collection2;
            list3 = NexoYamlKt.stringListOrNull(section, "Components.tooltip_display");
            if (list3 == null) {
               list3 = CollectionsKt.emptyList();
            }

            List existing_9 = list3;
            NexoYamlKt.remove(section, "ItemFlags");
            if (!((Collection)mapped).isEmpty()) {
               section.set("Components.tooltip_display", CollectionsKt.distinct((Iterable)CollectionsKt.plus((Collection)existing_9, (Iterable)mapped)));
            }
         }

         Unit unit17 = Unit.INSTANCE;
      } else {
         Object obj20 = null;
      }

      if (ItemTemplate.INSTANCE.isTemplate(itemId)) {
         NexoYamlKt.removeIf(NexoYamlKt.remove(section, "injectId"), "template", section.isBoolean("template"));
      }

      if (NexoYaml.Companion.equals(original, section)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.saveToYamlString(original), this.saveToYamlString(section));
      }
   }

   private final String saveToYamlString(ConfigurationSection configurationSection2) {
      YamlConfiguration it = new YamlConfiguration();
      int value = 0;
      ConfigurationSection configurationSection = it.createSection(configurationSection2.getName());
      NexoYamlKt.copyTo(configurationSection2, configurationSection);
      String saveToString = it.saveToString();
      return saveToString;
   }

   public final void processGlyphConfigs(@NotNull File glyphFile) {
      try {
      if (Intrinsics.areEqual(FilesKt.getExtension(glyphFile), "yml")) {
         YamlConfiguration glyphConfig;
         String original;
         String str;
         block: {
            glyphConfig = NexoYaml.Companion.loadConfiguration(glyphFile);
            str = glyphConfig.saveToString();
            original = str;
            Path resourcePath = NexoPlugin.Companion.instance().getDataFolder().toPath().relativize(glyphFile.toPath());
            NexoPlugin nexoPlugin = NexoPlugin.Companion.instance();
            InputStream inputStream = nexoPlugin.getResource(resourcePath.toString());
            if (inputStream != null) {
               byte[] readAllBytes = inputStream.readAllBytes();
               if (readAllBytes != null) {
                  str = StringsKt.decodeToString(readAllBytes);
                  if (str != null) {
                     break block;
                  }
               }
            }

            str = "";
         }

         String resource = str;
         YamlConfiguration it = new YamlConfiguration();
         int value = 0;
         it.loadFromString(resource);
         Map map = NexoYamlKt.childSections((ConfigurationSection)it);

         for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
            int value2 = 0;
            String key = (String)element.getKey();
            ConfigurationSection section = (ConfigurationSection)element.getValue();
            Object obj = glyphConfig.get(key + ".char");
            glyphConfig.set(key, section);
            glyphConfig.set(key + ".char", obj);
         }

         Iterable iterable = (Iterable)NexoYamlKt.childSections((ConfigurationSection)glyphConfig).values();

         for(Object obj2 : iterable) {
            ConfigurationSection section = (ConfigurationSection)obj2;
            int value3 = 0;
            ConfigurationSection configurationSection = NexoYamlKt.section(section, "chat");
            if (configurationSection != null) {
               NexoYamlKt.moveTo(configurationSection, section);
            }
         }

         if (!Intrinsics.areEqual(glyphConfig.saveToString(), original)) {
            glyphConfig.save(glyphFile);
         }

      }
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   public final void processRecipes() {
      try {
      Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(RecipeType.values());

      for(Object element : iterable) {
         RecipeType it = (RecipeType)element;
         int value = 0;
         File recipesFolder = RecipesManager.INSTANCE.getRecipesFolder();
         String[] it_7 = new String[1];
         String id = it.getId();
         it_7[0] = id + ".yml";
         Path it_6 = FileUtilsKt.resolve(recipesFolder, it_7).toPath();
         int value2 = 0;
         LinkOption[] linkOptions = new LinkOption[0];
         Path path = Files.exists(it_6, Arrays.copyOf(linkOptions, linkOptions.length)) ? it_6 : null;
         if (path != null) {
            Path recipeFile = path;
            File file = RecipesManager.INSTANCE.getRecipesFolder();
            String[] it_5 = new String[]{it.getId()};
            File it_4 = FileUtilsKt.resolve(file, it_5);
            value2 = 0;
            it_4.mkdirs();
            file = it_4;
            String[] it_3 = new String[]{it.getId() + ".yml"};
            File targetFile = FileUtilsKt.resolve(file, it_3);
            if (targetFile.exists()) {
               ConfigurationSection configurationSection = (ConfigurationSection)NexoYaml.Companion.loadConfiguration(targetFile);
               NexoYaml.Companion companion = NexoYaml.Companion;
               File toFile = recipeFile.toFile();
               ConfigurationSection newRecipe = NexoYamlKt.copyFrom(configurationSection, (ConfigurationSection)companion.loadConfiguration(toFile));
               NexoYaml.Companion.saveConfig(targetFile, newRecipe);
               Files.delete(recipeFile);
               Unit unit = Unit.INSTANCE;
            } else {
               Path toPath = targetFile.toPath();
               Path it_2 = toPath;
               CopyOption[] copyOptions = new CopyOption[]{StandardCopyOption.REPLACE_EXISTING};
               Intrinsics.checkNotNullExpressionValue(Files.move(recipeFile, it_2, Arrays.copyOf(copyOptions, copyOptions.length)), "move(...)");
            }
         }
      }

      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   public final void processSettings(@NotNull YamlConfiguration settings) {
      try {
      String string = settings.getString(Settings.GLYPH_DEFAULT_PERMISSION.getPath());
      if (string != null ? StringsKt.contains((CharSequence)string, (CharSequence)":", false) : false) {
         settings.set(Settings.GLYPH_DEFAULT_PERMISSION.getPath(), (Object)null);
      }

      if (settings.contains("NexoInventory")) {
         NexoYamlKt.rename((ConfigurationSection)settings, "NexoInventory.menu_layout", "NexoInventory.layout");
         Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(NexoInventory.values());

         for(Object element : iterable) {
            NexoInventory it = (NexoInventory)element;
            int value = 0;
            Object obj = settings.get(it.getPath());
            if (obj != null) {
               Object setting = obj;
               NexoPlugin.Companion.instance().configsManager().inventory().set(it.getPath(), setting);
            }
         }

         NexoYamlKt.remove((ConfigurationSection)settings, "NexoInventory");
         YamlConfiguration inventory = NexoPlugin.Companion.instance().configsManager().inventory();
         File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
         String[] strs = new String[]{"inventory.yml"};
         inventory.save(FileUtilsKt.resolve(dataFolder, strs));
      }

      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   public final void processInventory(@NotNull YamlConfiguration inventory) {
      NexoYamlKt.rename((ConfigurationSection)inventory, "NexoInventory.menu_layout", "NexoInventory.layout");
      ConfigurationSection configurationSection = NexoYamlKt.section((ConfigurationSection)inventory, "NexoInventory.layout");
      if (configurationSection != null) {
         Map map = NexoYamlKt.childSections(configurationSection);
         if (map != null) {
            Map map2 = map;

            for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
               int value = 0;
               ConfigurationSection section = (ConfigurationSection)element.getValue();
               NexoYamlKt.rename(section, "displayname", "itemname");
            }
         }
      }

   }

   public final void processSounds(@NotNull YamlConfiguration sounds) {
      java.lang.Object obj;
      Collection collection = (Collection)NexoYamlKt.sectionList((ConfigurationSection)sounds, "sounds");
      if (collection.isEmpty()) {
         int value = 0;
      } else {
         List soundEntries = (List)collection;
         Iterable iterable = (Iterable)soundEntries;
         boolean flag;
         if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
            flag = true;
         } else {
            Iterator iterator = iterable.iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  flag = true;
                  break;
               }

               Object element = iterator.next();
               ConfigurationSection it = (ConfigurationSection)element;
               int value2 = 0;
               if (it.contains("jukebox_playable.length_in_seconds")) {
                  flag = false;
                  break;
               }
            }
         }

         if (!flag) {
            Iterable iterable2 = (Iterable)soundEntries;
            int value3 = 0;

            for(Object obj3 : iterable2) {
               ConfigurationSection it = (ConfigurationSection)obj3;
               int value4 = 0;
               ConfigurationSection configurationSection = NexoYamlKt.section(it, "jukebox_playable");
               if (configurationSection != null) {
                  ConfigurationSection jukebox = configurationSection;
                  Object obj2 = jukebox.get("length_in_seconds");
                  if (obj2 != null) {
                     String str = obj2.toString();
                     if (str != null) {
                        str = StringUtilsKt.appendIfMissing(str, "s");
                        if (str != null) {
                           String seconds = str;
                           NexoYamlKt.remove(jukebox, "length_in_seconds").set("duration", seconds);
                        }
                     }
                  }
               }
            }

            List newSounds = (List)iterable2;
            sounds.set("sounds", newSounds);
            NexoConverter nexoConverter = this;

            try {
               NexoConverter nexoConverter2 = nexoConverter;
               int value5 = 0;
               File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
               String[] strs = new String[]{"sounds.yml"};
               sounds.save(FileUtilsKt.resolve(dataFolder, strs));
               obj = ResultShim.ok(Unit.INSTANCE);
            } catch (Throwable e) {
               obj = ResultShim.ok(ResultKt.createFailure(e));
            }

            UtilsKt.printOnFailure(obj, true);
         }
      }
   }

   static {
      Pair[] pairs = new Pair[]{TuplesKt.to("HIDE_ATTRIBUTES", "minecraft:attribute_modifiers"), TuplesKt.to("HIDE_UNBREAKABLE", "minecraft:unbreakable"), TuplesKt.to("HIDE_ENCHANTS", "minecraft:enchantments"), TuplesKt.to("HIDE_DYE", "minecraft:dyed_color"), TuplesKt.to("HIDE_ARMOR_TRIM", "minecraft:trim"), TuplesKt.to("HIDE_PLACED_ON", "minecraft:can_place_on"), TuplesKt.to("HIDE_DESTROYS", "minecraft:can_break")};
      ITEM_FLAG_TO_COMPONENT = MapsKt.mapOf(pairs);
   }

   public static final class EntriesMappings {
      public static final kotlin.enums.EnumEntries entries$0 = kotlin.enums.EnumEntriesKt.enumEntries(org.bukkit.attribute.AttributeModifier.Operation.values());
   }
}
