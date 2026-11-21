package com.nexomc.nexo.items;

import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.RangeUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/items/CustomModelData;", "", "type", "Lorg/bukkit/Material;", "customModelData", "", "nexoMeta", "Lcom/nexomc/nexo/items/NexoMeta;", "<init>", "(Lorg/bukkit/Material;ILcom/nexomc/nexo/items/NexoMeta;)V", "getType", "()Lorg/bukkit/Material;", "getCustomModelData", "()I", "Companion", "com.nexomc:core"}
)
public final class CustomModelData {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Material type;
   private final int customModelData;
   @NotNull
   private static final Object2ObjectOpenHashMap<Material, Object2IntLinkedOpenHashMap<Key>> DATAS = new Object2ObjectOpenHashMap();

   public CustomModelData(@NotNull Material type, int customModelData, @NotNull NexoMeta nexoMeta) {
      super();
      this.type = type;
      this.customModelData = customModelData;
      Object2IntLinkedOpenHashMap map = (Object2IntLinkedOpenHashMap)DATAS.computeIfAbsent(this.type, CustomModelData::_init_$lambda$0);
      Key model = nexoMeta.getModel();
      if (model != null) {
         Key it = model;
         int value = 0;
         ((Map)map).put(it, this.customModelData);
      } else {
         Logs.logWarn("Failed to assign customModelData due to invalid model", false);
      }

   }

   @NotNull
   public final Material getType() {
      return this.type;
   }

   public final int getCustomModelData() {
      return this.customModelData;
   }

   private static final Object2IntLinkedOpenHashMap _init_$lambda$0(Object it) {
      return new Object2IntLinkedOpenHashMap();
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\u0006J\u0018\u0010\u000f\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\fH\u0002J\u0016\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\f0\u00122\u0006\u0010\u000e\u001a\u00020\u0006H\u0002R#\u0010\u0004\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0005¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n¨\u0006\u0013"},
      d2 = {"Lcom/nexomc/nexo/items/CustomModelData$Companion;", "", "<init>", "()V", "DATAS", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "Lorg/bukkit/Material;", "Lit/unimi/dsi/fastutil/objects/Object2IntLinkedOpenHashMap;", "Lnet/kyori/adventure/key/Key;", "getDATAS", "()Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "generateId", "", "model", "type", "nextNotSkippedCustomModelData", "start", "skippedCustomModelData", "Ljava/util/SortedSet;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Object2ObjectOpenHashMap<Material, Object2IntLinkedOpenHashMap<Key>> getDATAS() {
         return CustomModelData.DATAS;
      }

      public final int generateId(@NotNull Key model, @NotNull Material type) {
         int startingCMD = Settings.INITIAL_CUSTOM_MODEL_DATA.toInt(1000);
         Map datas = (Map)this.getDATAS();
         int value3 = 0;
         Object obj2 = datas.get(type);
         Object obj;
         if (obj2 == null) {
            int value = 0;
            Object answer = new Object2IntLinkedOpenHashMap();
            datas.put(type, answer);
            obj = answer;
         } else {
            obj = obj2;
         }

         Object2IntLinkedOpenHashMap usedModelDatas = (Object2IntLinkedOpenHashMap)obj;
         if (((Map)usedModelDatas).containsKey(model)) {
            return usedModelDatas.getInt(model);
         } else {
            Collection collection = usedModelDatas.values();
            Set usedCmds = CollectionsKt.toSet((Iterable)collection);
            SortedSet skipped = this.skippedCustomModelData(type);
            Integer number = (Integer)CollectionsKt.maxOrNull((Iterable<Integer>)usedCmds);
            value3 = RangesKt.coerceAtLeast(number != null ? number : startingCMD, startingCMD);
            int cmd = startingCMD;
            if (startingCMD <= value3) {
               while(true) {
                  if (!usedCmds.contains(cmd) && !skipped.contains(cmd)) {
                     int value2 = 0;
                     ((Map)usedModelDatas).put(model, cmd);
                     return cmd;
                  }

                  if (cmd == value3) {
                     break;
                  }

                  ++cmd;
               }
            }

            cmd = this.nextNotSkippedCustomModelData(type, value3 + 1);
            int it = 0;
            ((Map)usedModelDatas).put(model, cmd);
            return cmd;
         }
      }

      private final int nextNotSkippedCustomModelData(Material type, int start) {
         SortedSet skipped = this.skippedCustomModelData(type);

         int candidate;
         for(candidate = start; skipped.contains(candidate); ++candidate) {
         }

         return candidate;
      }

      private final SortedSet<Integer> skippedCustomModelData(Material type) {
         TreeSet skippedData = SetsKt.sortedSetOf(new Integer[0]);
         ConfigurationSection toConfigSection = Settings.SKIPPED_MODEL_DATA_NUMBERS.toConfigSection();
         if (toConfigSection == null) {
            return (SortedSet)skippedData;
         } else {
            ConfigurationSection section = toConfigSection;
            String str = type.name().toLowerCase(Locale.ROOT);
            String string = section.getString(str);
            if (string != null) {
               String p0 = string;
               int value = 0;
               skippedCustomModelData$parseString(skippedData, type, p0);
            }

            string = section.getString(type.name());
            if (string != null) {
               String p0 = string;
               int value2 = 0;
               skippedCustomModelData$parseString(skippedData, type, p0);
            }

            str = type.name().toLowerCase(Locale.ROOT);
            List stringList = section.getStringList(str);
            Iterable iterable = (Iterable)stringList;

            for(Object element : iterable) {
               String p0 = (String)element;
               int value3 = 0;
               skippedCustomModelData$parseString(skippedData, type, p0);
            }

            return (SortedSet)skippedData;
         }
      }

      private static final void skippedCustomModelData$parseString(TreeSet<Integer> skippedData, Material material, String input) {
         if (StringsKt.contains((CharSequence)input, (CharSequence)"..", false)) {
            IntRange intRange = RangeUtilsKt.toIntRangeOrNull(input);
            if (intRange != null) {
               Iterable iterable = (Iterable)intRange;

               for(Object element : iterable) {
                  Integer p0 = (Integer)element;
                  int value = 0;
                  skippedData.add(p0);
               }
            } else {
               Logs.logError("Invalid range for " + material.name() + " in settings.yml", false);
            }
         } else {
            Integer number = StringsKt.toIntOrNull(input);
            if (number != null) {
               Integer p0 = number;
               int value2 = 0;
               skippedData.add(p0);
            } else {
               Logs.logError("Invalid number for " + material.name() + " in settings.yml", false);
            }
         }

      }
   }
}
