package com.nexomc.nexo.items;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.UtilsKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.model.Model;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001:\u0001\u001bB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0007J\u0010\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u0006J\u0016\u0010\u0010\u001a\u0004\u0018\u00010\u000e2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00060\u0012J\u0010\u0010\u0013\u001a\u00020\u00142\b\u0010\u000f\u001a\u0004\u0018\u00010\u0006J\u0016\u0010\u0015\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u0007R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/items/ItemTemplate;", "", "<init>", "()V", "itemTemplates", "", "", "Lorg/bukkit/configuration/ConfigurationSection;", "getItemTemplates", "()Ljava/util/Map;", "register", "", "section", "parserTemplate", "Lcom/nexomc/nexo/items/ItemParser;", "id", "parserTemplates", "ids", "", "isTemplate", "", "replacePlaceholders", "templateSection", "placeholders", "Lcom/nexomc/nexo/items/ItemTemplate$TemplatePlaceholder;", "getPlaceholders", "()Ljava/util/List;", "TemplatePlaceholder", "com.nexomc:core"}
)
public final class ItemTemplate {
   @NotNull
   public static final ItemTemplate INSTANCE = new ItemTemplate();
   @NotNull
   private static final Map<String, ConfigurationSection> itemTemplates = (Map)(new LinkedHashMap());
   @NotNull
   private static final List<TemplatePlaceholder> placeholders;

   private ItemTemplate() {
   }

   @NotNull
   public final Map<String, ConfigurationSection> getItemTemplates() {
      return itemTemplates;
   }

   public final void register(@NotNull ConfigurationSection section) {
      itemTemplates.put(section.getName(), section);
   }

   @Nullable
   public final ItemParser parserTemplate(@NotNull String id) {
      ConfigurationSection configurationSection = (ConfigurationSection)itemTemplates.get(id);
      ItemParser itemParser;
      if (configurationSection != null) {
         ConfigurationSection p0 = configurationSection;
         int value = 0;
         itemParser = new ItemParser(p0);
      } else {
         itemParser = null;
      }

      return itemParser;
   }

   @Nullable
   public final ItemParser parserTemplates(@NotNull List<String> ids) {
      Iterable iterable = (Iterable)ids;
      Map map = itemTemplates;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value = 0;
         String p0 = (String)element;
         int value2 = 0;
         ConfigurationSection configurationSection = (ConfigurationSection)map.get(p0);
         if (configurationSection != null) {
            Object it = configurationSection;
            int value3 = 0;
            destination.add(it);
         }
      }

      Collection mergedSection = (Collection)((List)destination);
      if (mergedSection.isEmpty()) {
         int value4 = 0;
         return null;
      } else {
         List templates = (List)mergedSection;
         Iterable iterable2 = (Iterable)templates;
         Object initial = (new YamlConfiguration()).createSection(((ConfigurationSection)CollectionsKt.last(templates)).getName());
         Object accumulator = initial;

         for(Object obj : iterable2) {
            ConfigurationSection configurationSection2 = (ConfigurationSection)obj;
            int value5 = 0;
            accumulator = NexoYamlKt.copyFrom((ConfigurationSection)accumulator, configurationSection2);
         }

         return new ItemParser((ConfigurationSection)accumulator);
      }
   }

   public final boolean isTemplate(@Nullable String id) {
      return itemTemplates.containsKey(id);
   }

   public final void replacePlaceholders(@NotNull ConfigurationSection templateSection, @NotNull ConfigurationSection section) {
      replacePlaceholders$transformInSection(section, templateSection);
   }

   @NotNull
   public final List<TemplatePlaceholder> getPlaceholders() {
      return placeholders;
   }

   private static final List<String> replacePlaceholders$transformString(ConfigurationSection section, String value) {
      ItemTemplate itemTemplate = INSTANCE;
      Iterable iterable = (Iterable)placeholders;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         TemplatePlaceholder it = (TemplatePlaceholder)element;
         int value2 = 0;
         if (StringsKt.contains((CharSequence)value, (CharSequence)("<" + it.getKey() + ">"), false)) {
            destination.add(element);
         }
      }

      iterable = (Iterable)((List)destination);
      Object initial = CollectionsKt.listOf(value);
      Object accumulator = initial;

      for(Object obj3 : iterable) {
         TemplatePlaceholder templatePlaceholder = (TemplatePlaceholder)obj3;
         int value3 = 0;
         String key = templatePlaceholder.component1();
         Function1 transform = templatePlaceholder.component2();
         ItemTemplate itemTemplate2 = INSTANCE;

         Object obj4;
         try {
            int value4 = 0;
            obj4 = ResultShim.ok(transform.invoke(section));
         } catch (Throwable e) {
            obj4 = ResultShim.ok(ResultKt.createFailure(e));
         }

         Object obj = UtilsKt.printOnFailure(obj4, false);
         Object obj2 = ResultShim.isFailure(obj) ? null : obj;
         if (obj2 == null) {
            obj2 = accumulator;
         } else {
            Object replacement = obj2;
            Iterable iterable2 = (Iterable)accumulator;
            Collection collection = (Collection)(new ArrayList());

            for(Object obj5 : iterable2) {
               String line = (String)obj5;
               int value5 = 0;
               List list;
               if (replacement instanceof String) {
                  list = CollectionsKt.listOf(StringsKt.replace(line, "<" + key + ">", (String)replacement, false));
               } else if (replacement instanceof List && Intrinsics.areEqual(line, "<" + key + ">")) {
                  Iterable iterable3 = (Iterable)replacement;
                  Collection collection2 = (Collection)(new ArrayList());

                  for(Object obj6 : iterable3) {
                     if (obj6 instanceof String) {
                        collection2.add(obj6);
                     }
                  }

                  list = (List)collection2;
               } else {
                  list = CollectionsKt.listOf(line);
               }

               Iterable iterable4 = (Iterable)list;
               CollectionsKt.addAll(collection, iterable4);
            }

            obj2 = (List)collection;
         }

         accumulator = obj2;
      }

      return (List<String>)accumulator;
   }

   private static final void replacePlaceholders$transformInSection(ConfigurationSection configurationSection2, ConfigurationSection section) {
      Set keys = section.getKeys(false);
      Iterable iterable = (Iterable)keys;

      for(Object element : iterable) {
         String key = (String)element;
         int value = 0;
         Iterable iterable2 = (Iterable)NexoYamlKt.sectionListOrSingle(section, key);
         if (CollectionsKt.count(iterable2) > 0) {
            List sectionList = (List)iterable2;
            int wasList = 0;
            if (sectionList.size() == 1 && section.isConfigurationSection(key)) {
               replacePlaceholders$transformInSection(configurationSection2, (ConfigurationSection)CollectionsKt.first(sectionList));
            } else {
               Iterable iterable3 = (Iterable)sectionList;
               int value2 = 0;

               for(Object obj : iterable3) {
                  ConfigurationSection p0 = (ConfigurationSection)obj;
                  int value3 = 0;
                  replacePlaceholders$transformInSection(configurationSection2, p0);
               }

               Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable3, 10)));
               value2 = 0;

               for(Object item : iterable3) {
                  ConfigurationSection configurationSection = (ConfigurationSection)item;
                  int value5 = 0;
                  destination.add(NexoYamlKt.toSerializableMap(configurationSection));
               }

               List list = (List)destination;
               section.set(key, list);
            }
         } else {
            int sectionList = 0;
            boolean wasList = section.isList(key);
            List list2;
            if (wasList) {
               list2 = section.getStringList(key);
            } else {
               list2 = NexoYamlKt.stringListOrSingle(section, key, (String)null);
            }

            List original = list2;
            Iterable iterable4 = (Iterable)original;
            Collection collection = (Collection)(new ArrayList());

            for(Object obj2 : iterable4) {
               String p0 = (String)obj2;
               int p0_5 = 0;
               Iterable iterable5 = (Iterable)replacePlaceholders$transformString(configurationSection2, p0);
               CollectionsKt.addAll(collection, iterable5);
            }

            List result = (List)collection;
            if (!((Collection)result).isEmpty()) {
               section.set(key, result.size() == 1 && !wasList ? CollectionsKt.first(result) : result);
            } else {
               Iterable iterable6 = (Iterable)original;
               boolean flag;
               if (iterable6 instanceof Collection && ((Collection)iterable6).isEmpty()) {
                  flag = false;
               } else {
                  Iterator iterator2 = iterable6.iterator();

                  while(true) {
                     if (!iterator2.hasNext()) {
                        flag = false;
                        break;
                     }

                     Object obj3 = iterator2.next();
                     String s = (String)obj3;
                     int it = 0;
                     ItemTemplate itemTemplate = INSTANCE;
                     Iterable iterable7 = (Iterable)placeholders;
                     boolean flag2;
                     if (iterable7 instanceof Collection && ((Collection)iterable7).isEmpty()) {
                        flag2 = false;
                     } else {
                        Iterator iterator = iterable7.iterator();

                        while(true) {
                           if (!iterator.hasNext()) {
                              flag2 = false;
                              break;
                           }

                           Object obj4 = iterator.next();
                           TemplatePlaceholder it_1 = (TemplatePlaceholder)obj4;
                           int value4 = 0;
                           if (StringsKt.contains((CharSequence)s, (CharSequence)("<" + it_1.getKey() + ">"), false)) {
                              flag2 = true;
                              break;
                           }
                        }
                     }

                     if (flag2) {
                        flag = true;
                        break;
                     }
                  }
               }

               if (flag) {
                  NexoYamlKt.remove(section, key);
               }
            }
         }
      }

   }

   private static final Object placeholders$lambda$0(ConfigurationSection configurationSection) {
      return NexoYamlKt.getRootId(configurationSection);
   }

   private static final CharSequence placeholders$lambda$1$0(String it) {
      String str;
      if (((CharSequence)it).length() > 0) {
         StringBuilder stringBuilder = new StringBuilder();
         char p0 = it.charAt(0);
         StringBuilder stringBuilder2 = stringBuilder;
         int value = 0;
         String str2 = String.valueOf(p0);
         str2 = str2.toUpperCase(Locale.ROOT);
         StringBuilder stringBuilder3 = stringBuilder2.append(str2);
         value = 1;
         String str3 = it.substring(value);
         str = stringBuilder3.append(str3).toString();
      } else {
         str = it;
      }

      return (CharSequence)str;
   }

   private static final Object placeholders$lambda$1(ConfigurationSection configurationSection) {
      CharSequence rootId = (CharSequence)NexoYamlKt.getRootId(configurationSection);
      String[] strs = new String[]{"_"};
      return CollectionsKt.joinToString((Iterable)StringsKt.split(rootId, strs, false, 0), (CharSequence)" ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", ItemTemplate::placeholders$lambda$1$0);
   }

   private static final Object placeholders$lambda$2(ConfigurationSection configurationSection) {
      List stringList = configurationSection.getStringList("lore");
      int value = 0;
      NexoYamlKt.remove(configurationSection, "lore");
      return stringList;
   }

   private static final Object placeholders$lambda$3(ConfigurationSection configurationSection) {
      Object obj = NexoYamlKt.stringOrNull(NexoYamlKt.getRootSection(configurationSection), "Pack.parent_model");
      if (obj == null) {
         obj = NexoYamlKt.stringOrNull(NexoYamlKt.getRootSection(configurationSection), "Pack.parent");
         if (obj == null) {
            obj = Model.ITEM_GENERATED;
         }
      }

      return obj;
   }

   private static final Object placeholders$lambda$4(ConfigurationSection configurationSection) {
      String str = NexoYamlKt.stringOrNull(NexoYamlKt.getRootSection(configurationSection), "Pack.model");
      if (str == null) {
         str = NexoYamlKt.getRootId(configurationSection);
      }

      return str;
   }

   private static final Object placeholders$lambda$5(ConfigurationSection configurationSection) {
      return NexoYamlKt.stringListOrSingle(NexoYamlKt.getRootSection(configurationSection), "Pack.texture", (String)null);
   }

   static {
      TemplatePlaceholder[] templatePlaceholders = new TemplatePlaceholder[]{new TemplatePlaceholder("item_id", ItemTemplate::placeholders$lambda$0), new TemplatePlaceholder("item_id_capitalized", ItemTemplate::placeholders$lambda$1), new TemplatePlaceholder("lore", ItemTemplate::placeholders$lambda$2), new TemplatePlaceholder("parent", ItemTemplate::placeholders$lambda$3), new TemplatePlaceholder("model", ItemTemplate::placeholders$lambda$4), new TemplatePlaceholder("texture", ItemTemplate::placeholders$lambda$5)};
      placeholders = CollectionsKt.listOf(templatePlaceholders);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B(\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0017\u0010\u0004\u001a\u0013\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0005¢\u0006\u0002\b\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\u001a\u0010\u000f\u001a\u0013\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0005¢\u0006\u0002\b\u0007HÆ\u0003J.\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0019\b\u0002\u0010\u0004\u001a\u0013\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0005¢\u0006\u0002\b\u0007HÆ\u0001J\u0014\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0014\u001a\u00020\u0015HÖ\u0081\u0004J\n\u0010\u0016\u001a\u00020\u0003HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\"\u0010\u0004\u001a\u0013\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0005¢\u0006\u0002\b\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0017"},
      d2 = {"Lcom/nexomc/nexo/items/ItemTemplate$TemplatePlaceholder;", "", "key", "", "transform", "Lkotlin/Function1;", "Lorg/bukkit/configuration/ConfigurationSection;", "Lkotlin/ExtensionFunctionType;", "<init>", "(Ljava/lang/String;Lkotlin/jvm/functions/Function1;)V", "getKey", "()Ljava/lang/String;", "getTransform", "()Lkotlin/jvm/functions/Function1;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "com.nexomc:core"}
   )
   public static final class TemplatePlaceholder {
      @NotNull
      private final String key;
      @NotNull
      private final Function1<ConfigurationSection, Object> transform;

      public TemplatePlaceholder(@NotNull String key, @NotNull Function1<? super ConfigurationSection, ? extends Object> transform) {
         super();
         this.key = key;
         this.transform = (Function1)(Object)transform;
      }

      @NotNull
      public final String getKey() {
         return this.key;
      }

      @NotNull
      public final Function1<ConfigurationSection, Object> getTransform() {
         return this.transform;
      }

      @NotNull
      public final String component1() {
         return this.key;
      }

      @NotNull
      public final Function1<ConfigurationSection, Object> component2() {
         return this.transform;
      }

      @NotNull
      public final TemplatePlaceholder copy(@NotNull String key, @NotNull Function1<? super ConfigurationSection, ? extends Object> transform) {
         return new TemplatePlaceholder(key, transform);
      }

      @NotNull
      public String toString() {
         return "TemplatePlaceholder(key=" + this.key + ", transform=" + this.transform + ")";
      }

      public int hashCode() {
         int result = this.key.hashCode();
         result = result * 31 + this.transform.hashCode();
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof TemplatePlaceholder)) {
            return false;
         } else {
            TemplatePlaceholder templatePlaceholder = (TemplatePlaceholder)other;
            if (!Intrinsics.areEqual(this.key, templatePlaceholder.key)) {
               return false;
            } else {
               return Intrinsics.areEqual(this.transform, templatePlaceholder.transform);
            }
         }
      }
   }
}
