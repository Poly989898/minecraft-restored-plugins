package com.nexomc.nexo.mechanics.custom_block;

import com.nexomc.nexo.items.ItemTemplate;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0013\u001a\n\u0012\u0004\u0012\u00020\u000f\u0018\u00010\u000e\"\b\b\u0000\u0010\u0014*\u00020\u000f2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u0002H\u00140\u0016J\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0018\u001a\u00020\u0006J\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0019\u001a\u00020\u001aJ\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u001b\u001a\u00020\u001cJ\u001e\u0010\u001d\u001a\u00020\u001e\"\b\b\u0000\u0010\u0014*\u00020\u000f2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u0002H\u00140\u000eJ\u001a\u0010 \u001a\f\u0012\u0006\b\u0001\u0012\u00020\u000f\u0018\u00010\u000e2\b\u0010!\u001a\u0004\u0018\u00010\u0006J\u0014\u0010(\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u000e2\u0006\u0010)\u001a\u00020*J\u0015\u0010+\u001a\u0004\u0018\u00010,2\u0006\u0010)\u001a\u00020*¢\u0006\u0002\u0010-J$\u0010+\u001a\u00020,2\u0006\u0010.\u001a\u00020\t2\n\u0010/\u001a\u0006\u0012\u0002\b\u00030\u000e2\b\b\u0002\u00100\u001a\u000201R'\u0010\u0004\u001a\u0018\u0012\b\u0012\u00060\u0006j\u0002`\u0007\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bRE\u0010\f\u001a6\u0012\b\u0012\u00060\u0006j\u0002`\u0007\u0012\f\u0012\n\u0012\u0006\b\u0001\u0012\u00020\u000f0\u000e0\rj\u001a\u0012\b\u0012\u00060\u0006j\u0002`\u0007\u0012\f\u0012\n\u0012\u0006\b\u0001\u0012\u00020\u000f0\u000e`\u0010¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00060#8F¢\u0006\u0006\u001a\u0004\b$\u0010%R\u001b\u0010&\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u000e0#8F¢\u0006\u0006\u001a\u0004\b'\u0010%¨\u00062"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockRegistry;", "", "<init>", "()V", "DATAS", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockName;", "Lit/unimi/dsi/fastutil/ints/Int2ObjectOpenHashMap;", "Lnet/kyori/adventure/key/Key;", "getDATAS", "()Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "registry", "Ljava/util/LinkedHashMap;", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockType;", "Lcom/nexomc/nexo/mechanics/custom_block/CustomBlockMechanic;", "Lkotlin/collections/LinkedHashMap;", "getRegistry", "()Ljava/util/LinkedHashMap;", "getByClass", "T", "mechanicClass", "Ljava/lang/Class;", "getMechanic", "itemId", "block", "Lorg/bukkit/block/Block;", "blockData", "Lorg/bukkit/block/data/BlockData;", "register", "", "blockType", "get", "name", "names", "", "getNames", "()Ljava/util/List;", "types", "getTypes", "fromMechanicSection", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "generateVariation", "", "(Lorg/bukkit/configuration/ConfigurationSection;)Ljava/lang/Integer;", "model", "type", "reuse", "", "com.nexomc:core"}
)
public final class CustomBlockRegistry {
   @NotNull
   public static final CustomBlockRegistry INSTANCE = new CustomBlockRegistry();
   @NotNull
   private static final Object2ObjectOpenHashMap<String, Int2ObjectOpenHashMap<Key>> DATAS = new Object2ObjectOpenHashMap();
   @NotNull
   private static final LinkedHashMap<String, CustomBlockType<? extends CustomBlockMechanic>> registry = new LinkedHashMap();

   private CustomBlockRegistry() {
   }

   @NotNull
   public final Object2ObjectOpenHashMap<String, Int2ObjectOpenHashMap<Key>> getDATAS() {
      return DATAS;
   }

   @NotNull
   public final LinkedHashMap<String, CustomBlockType<? extends CustomBlockMechanic>> getRegistry() {
      return registry;
   }

   @Nullable
   public final <T extends CustomBlockMechanic> CustomBlockType<CustomBlockMechanic> getByClass(@NotNull Class<T> mechanicClass) {
      java.lang.Object obj;
      Collection collection = registry.values();
      Iterable iterable = (Iterable)collection;
      Iterator iterator = iterable.iterator();

      while(true) {
         if (iterator.hasNext()) {
            Object obj2 = iterator.next();
            CustomBlockType it = (CustomBlockType)obj2;
            int value = 0;
            if (!it.getClazz().isAssignableFrom(mechanicClass)) {
               continue;
            }

            obj = obj2;
            break;
         }

         obj = null;
         break;
      }

      Object obj4 = obj;
      Object obj3 = obj4;
      if (!(obj4 instanceof CustomBlockType)) {
         obj3 = null;
      }

      return (CustomBlockType)obj3;
   }

   @Nullable
   public final CustomBlockMechanic getMechanic(@NotNull String itemId) {
      Iterator iterator = ((Map)registry).entrySet().iterator();

      CustomBlockMechanic customBlockMechanic;
      while(true) {
         if (iterator.hasNext()) {
            Map.Entry it = (Map.Entry)iterator.next();
            int value = 0;
            MechanicFactory factory = ((CustomBlockType)it.getValue()).factory();
            Mechanic mechanic = factory != null ? factory.getMechanic(itemId) : null;
            CustomBlockMechanic customBlockMechanic2 = mechanic instanceof CustomBlockMechanic ? (CustomBlockMechanic)mechanic : null;
            if (customBlockMechanic2 == null) {
               continue;
            }

            customBlockMechanic = customBlockMechanic2;
            break;
         }

         customBlockMechanic = null;
         break;
      }

      return customBlockMechanic;
   }

   @Nullable
   public final CustomBlockMechanic getMechanic(@NotNull Block block) {
      Iterator iterator = ((Map)registry).entrySet().iterator();

      CustomBlockMechanic customBlockMechanic;
      while(true) {
         if (iterator.hasNext()) {
            Map.Entry it = (Map.Entry)iterator.next();
            int value = 0;
            CustomBlockMechanic mechanic = ((CustomBlockType)it.getValue()).getMechanic(block);
            if (mechanic == null) {
               continue;
            }

            customBlockMechanic = mechanic;
            break;
         }

         customBlockMechanic = null;
         break;
      }

      return customBlockMechanic;
   }

   @Nullable
   public final CustomBlockMechanic getMechanic(@NotNull BlockData blockData) {
      Iterator iterator = ((Map)registry).entrySet().iterator();

      CustomBlockMechanic customBlockMechanic;
      while(true) {
         if (iterator.hasNext()) {
            Map.Entry it = (Map.Entry)iterator.next();
            int value = 0;
            CustomBlockMechanic mechanic = ((CustomBlockType)it.getValue()).getMechanic(blockData);
            if (mechanic == null) {
               continue;
            }

            customBlockMechanic = mechanic;
            break;
         }

         customBlockMechanic = null;
         break;
      }

      return customBlockMechanic;
   }

   public final <T extends CustomBlockMechanic> void register(@NotNull CustomBlockType<T> blockType) {
      ((Map)registry).put(blockType.name(), blockType);
   }

   @Nullable
   public final CustomBlockType<? extends CustomBlockMechanic> get(@Nullable String name) {
      return (CustomBlockType)((Map)registry).get(name);
   }

   @NotNull
   public final List<String> getNames() {
      Set keySet = registry.keySet();
      return CollectionsKt.toList((Iterable)keySet);
   }

   @NotNull
   public final List<CustomBlockType<?>> getTypes() {
      Collection collection = registry.values();
      return CollectionsKt.toList((Iterable)collection);
   }

   @Nullable
   public final CustomBlockType<?> fromMechanicSection(@NotNull ConfigurationSection section) {
      CustomBlockType type = this.get(section.getString("type"));
      if (type == null && !ItemTemplate.INSTANCE.isTemplate(NexoYamlKt.getRootId(section))) {
         Logs.logError("No CustomBlock-type defined in " + NexoYamlKt.getRootId(section), false);
         Logs.logError("Valid types are: " + CollectionsKt.joinToString((Iterable)this.getNames(), (CharSequence)", ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null), false);
      }

      return type;
   }

   @Nullable
   public final Integer generateVariation(@NotNull ConfigurationSection section) {
      if (ItemTemplate.INSTANCE.isTemplate(NexoYamlKt.getRootId(section))) {
         return null;
      } else {
         Key key = NexoYamlKt.key(section, "model");
         if (key == null) {
            key = NexoYamlKt.key(NexoYamlKt.getRootSection(section), "Pack.model");
            if (key == null) {
               key = Key.key(NexoYamlKt.getRootId(section));
            }
         }

         Key model = key;
         CustomBlockType customBlockType = this.fromMechanicSection(section);
         Integer number;
         if (customBlockType != null) {
            CustomBlockType it = customBlockType;
            int value = 0;
            number = INSTANCE.generateVariation(model, it, true);
         } else {
            CustomBlockRegistry customBlockRegistry = this;
            int value2 = 0;
            Logs.logWarn("Could not assign <yellow><i>custom_variation</i></yellow> automatically to <red>" + NexoYamlKt.getRootId(section) + "</red> as it was missing <yellow><i>type</i></yellow>-property", false);
            number = null;
         }

         return number;
      }
   }

   public final int generateVariation(@NotNull Key model, @NotNull CustomBlockType<?> type, boolean reuse) {
      java.lang.Object obj;
      Int2ObjectOpenHashMap usedModels = (Int2ObjectOpenHashMap)DATAS.computeIfAbsent(type.name(), CustomBlockRegistry::generateVariation$lambda$2);
      if (reuse && usedModels.containsValue(model)) {
         Int2ObjectMap.FastEntrySet int2ObjectEntrySet = usedModels.int2ObjectEntrySet();
         Iterable iterable = (Iterable)int2ObjectEntrySet;
         Iterator iterator = iterable.iterator();

         while(true) {
            if (iterator.hasNext()) {
               Object obj2 = iterator.next();
               Int2ObjectMap.Entry it = (Int2ObjectMap.Entry)obj2;
               int value = 0;
               if (!Intrinsics.areEqual(it.getValue(), model)) {
                  continue;
               }

               obj = obj2;
               break;
            }

            obj = null;
            break;
         }

         return (Int2ObjectMap.Entry)obj != null ? ((Int2ObjectMap.Entry)obj).getIntKey() : 1;
      } else {
         Set keySet = usedModels.keySet();
         Set usedVariations = CollectionsKt.toSet((Iterable)keySet);
         Integer number = (Integer)CollectionsKt.maxOrNull((Iterable<Integer>)usedVariations);
         int currentMax = RangesKt.coerceAtLeast(number != null ? number : 1, 1);
         int variation = type.generateVariation(usedVariations, currentMax);
         ((Map)usedModels).put(variation, model);
         return variation;
      }
   }

   private static final Int2ObjectOpenHashMap generateVariation$lambda$2(Object it) {
      return new Int2ObjectOpenHashMap();
   }
}
