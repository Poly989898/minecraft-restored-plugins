package com.nexomc.nexo.nms;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IndexedValue;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.ranges.RangesKt;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.NoteBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b&\u0018\u0000 $2\u00020\u0001:\u0002#$B3\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u001a\b\u0002\u0010\u0005\u001a\u0014\u0012\u0004\u0012\u00020\u0004\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00030\u0006¢\u0006\u0004\b\b\u0010\tJ\u0015\u0010\u0012\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0013\u001a\u00020\u0004¢\u0006\u0002\u0010\u0014J\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0016\u001a\u00020\u000eJ\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\u0018J%\u0010\u001b\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u001c\u001a\u00020\u000e2\u0006\u0010\u001d\u001a\u00020\u001e¢\u0006\u0002\u0010\u001fJ\u0010\u0010 \u001a\u0004\u0018\u00010\u00042\u0006\u0010!\u001a\u00020\"R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001a\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u000e0\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R&\u0010\u000f\u001a\u0014\u0012\u0004\u0012\u00020\u0004\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00030\u0006X\u0084\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006%"},
   d2 = {"Lcom/nexomc/nexo/nms/IBlockUtils;", "", "extraInstruments", "", "Lorg/bukkit/Instrument;", "extraInstrumentMap", "", "", "<init>", "(Ljava/util/List;Ljava/util/Map;)V", "instruments", "getInstruments", "()Ljava/util/List;", "instrumentToIndex", "", "instrumentMap", "getInstrumentMap", "()Ljava/util/Map;", "indexForInstrument", "instrument", "(Lorg/bukkit/Instrument;)Ljava/lang/Integer;", "instrumentForVariation", "customVariation", "encodeToBlockData", "Lorg/bukkit/block/data/type/NoteBlock;", "variation", "blockData", "encodeToVariation", "note", "powered", "", "(Lorg/bukkit/Instrument;IZ)Ljava/lang/Integer;", "instrumentForBlock", "block", "Lorg/bukkit/block/Block;", "DefaultBlockUtils", "Companion", "com.nexomc:core"}
)
public abstract class IBlockUtils {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final List<Instrument> instruments;
   @NotNull
   private final Map<Instrument, Integer> instrumentToIndex;
   @NotNull
   private final Map<Instrument, List<String>> instrumentMap;
   @NotNull
   private static final List<Instrument> BASE_INSTRUMENTS;
   @NotNull
   private static final Map<Instrument, List<String>> BASE_INSTRUMENT_MAP;

   public IBlockUtils(@NotNull List<? extends Instrument> extraInstruments, @NotNull Map<Instrument, ? extends List<String>> extraInstrumentMap) {
      super();
      this.instruments = CollectionsKt.plus((Collection)BASE_INSTRUMENTS, (Iterable)extraInstruments);
      Iterable iterable = CollectionsKt.withIndex((Iterable)this.instruments);
      int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16);
      Map destination = (Map)(new LinkedHashMap(capacity));

      for(Object element : iterable) {
         IndexedValue it = (IndexedValue)element;
         int value = 0;
         Pair pair = TuplesKt.to(it.getValue(), it.getIndex());
         destination.put(pair.getFirst(), pair.getSecond());
      }

      this.instrumentToIndex = destination;
      this.instrumentMap = MapsKt.plus(BASE_INSTRUMENT_MAP, extraInstrumentMap);
   }

   @NotNull
   public final List<Instrument> getInstruments() {
      return this.instruments;
   }

   @NotNull
   protected final Map<Instrument, List<String>> getInstrumentMap() {
      return this.instrumentMap;
   }

   @Nullable
   public final Integer indexForInstrument(@NotNull Instrument instrument) {
      return (Integer)this.instrumentToIndex.get(instrument);
   }

   @Nullable
   public final Instrument instrumentForVariation(int customVariation) {
      return (Instrument)CollectionsKt.getOrNull(this.instruments, customVariation / 50);
   }

   @NotNull
   public final NoteBlock encodeToBlockData(int variation, @NotNull NoteBlock blockData) {
      Instrument instrument = this.instrumentForVariation(variation);
      if (instrument == null) {
         return blockData;
      } else {
         blockData.setInstrument(instrument);
         blockData.setNote(new Note(variation % 25));
         blockData.setPowered(variation % 50 >= 25);
         return blockData;
      }
   }

   @Nullable
   public final Integer encodeToVariation(@NotNull Instrument instrument, int note, boolean powered) {
      Integer number = this.indexForInstrument(instrument);
      if (number != null) {
         int index = number;
         return index * 50 + note + (powered ? 25 : 0);
      } else {
         return null;
      }
   }

   @Nullable
   public final Instrument instrumentForBlock(@NotNull Block block) {
      java.lang.Object obj;
      String str = block.getType().name().toLowerCase(Locale.ROOT);
      String blockType = str;
      Iterable entrySet = (Iterable)this.instrumentMap.entrySet();
      Iterator iterator = entrySet.iterator();

      while(true) {
         if (iterator.hasNext()) {
            Object element = iterator.next();
            Map.Entry it = (Map.Entry)element;
            int value = 0;
            if (!((List)it.getValue()).contains(blockType)) {
               continue;
            }

            obj = element;
            break;
         }

         obj = null;
         break;
      }

      return (Map.Entry)obj != null ? (Instrument)((Map.Entry)obj).getKey() : null;
   }

   public IBlockUtils() {
      this(kotlin.collections.CollectionsKt.emptyList(), kotlin.collections.MapsKt.emptyMap());
   }

   static {
      Instrument[] instruments2 = new Instrument[]{Instrument.PIANO, Instrument.BASS_DRUM, Instrument.SNARE_DRUM, Instrument.STICKS, Instrument.BASS_GUITAR, Instrument.FLUTE, Instrument.BELL, Instrument.GUITAR, Instrument.CHIME, Instrument.XYLOPHONE, Instrument.IRON_XYLOPHONE, Instrument.COW_BELL, Instrument.DIDGERIDOO, Instrument.BIT, Instrument.BANJO, Instrument.PLING, Instrument.ZOMBIE, Instrument.SKELETON, Instrument.CREEPER, Instrument.DRAGON, Instrument.WITHER_SKELETON, Instrument.PIGLIN, Instrument.CUSTOM_HEAD};
      BASE_INSTRUMENTS = CollectionsKt.listOf(instruments2);
      Pair[] pairs = new Pair[15];
      pairs[0] = TuplesKt.to(Instrument.BELL, CollectionsKt.listOf("gold_block"));
      Instrument instrument = Instrument.BASS_DRUM;
      String[] strs = new String[]{"stone", "netherrack", "bedrock", "observer", "coral", "obsidian", "anchor", "quartz"};
      pairs[1] = TuplesKt.to(instrument, CollectionsKt.listOf(strs));
      pairs[2] = TuplesKt.to(Instrument.FLUTE, CollectionsKt.listOf("clay"));
      pairs[3] = TuplesKt.to(Instrument.CHIME, CollectionsKt.listOf("packed_ice"));
      pairs[4] = TuplesKt.to(Instrument.GUITAR, CollectionsKt.listOf("wool"));
      pairs[5] = TuplesKt.to(Instrument.XYLOPHONE, CollectionsKt.listOf("bone_block"));
      pairs[6] = TuplesKt.to(Instrument.IRON_XYLOPHONE, CollectionsKt.listOf("iron_block"));
      pairs[7] = TuplesKt.to(Instrument.COW_BELL, CollectionsKt.listOf("soul_sand"));
      pairs[8] = TuplesKt.to(Instrument.DIDGERIDOO, CollectionsKt.listOf("pumpkin"));
      pairs[9] = TuplesKt.to(Instrument.BIT, CollectionsKt.listOf("emerald_block"));
      pairs[10] = TuplesKt.to(Instrument.BANJO, CollectionsKt.listOf("hay_bale"));
      pairs[11] = TuplesKt.to(Instrument.PLING, CollectionsKt.listOf("glowstone"));
      pairs[12] = TuplesKt.to(Instrument.BASS_GUITAR, CollectionsKt.listOf("wood"));
      instrument = Instrument.SNARE_DRUM;
      strs = new String[]{"sand", "gravel", "concrete_powder", "soul_soil"};
      pairs[13] = TuplesKt.to(instrument, CollectionsKt.listOf(strs));
      instrument = Instrument.STICKS;
      strs = new String[]{"glass", "sea_lantern", "beacon"};
      pairs[14] = TuplesKt.to(instrument, CollectionsKt.listOf(strs));
      BASE_INSTRUMENT_MAP = MapsKt.mapOf(pairs);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"},
      d2 = {"Lcom/nexomc/nexo/nms/IBlockUtils$DefaultBlockUtils;", "Lcom/nexomc/nexo/nms/IBlockUtils;", "<init>", "()V", "com.nexomc:core"}
   )
   public static final class DefaultBlockUtils extends IBlockUtils {
      public DefaultBlockUtils() {
         super(kotlin.collections.CollectionsKt.emptyList(), kotlin.collections.MapsKt.emptyMap());
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR#\u0010\t\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\u00050\n¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u000e"},
      d2 = {"Lcom/nexomc/nexo/nms/IBlockUtils$Companion;", "", "<init>", "()V", "BASE_INSTRUMENTS", "", "Lorg/bukkit/Instrument;", "getBASE_INSTRUMENTS", "()Ljava/util/List;", "BASE_INSTRUMENT_MAP", "", "", "getBASE_INSTRUMENT_MAP", "()Ljava/util/Map;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final List<Instrument> getBASE_INSTRUMENTS() {
         return IBlockUtils.BASE_INSTRUMENTS;
      }

      @NotNull
      public final Map<Instrument, List<String>> getBASE_INSTRUMENT_MAP() {
         return IBlockUtils.BASE_INSTRUMENT_MAP;
      }
   }
}
