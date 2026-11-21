package com.nexomc.nexo.glyphs;

import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Collection;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.font.FontProvider;
import team.unnamed.creative.font.SpaceFontProvider;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\b\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J.\u0010\r\u001a$\u0012 \u0012\u001e\u0012\f\u0012\n \u0012*\u0004\u0018\u00010\u00100\u0010\u0012\f\u0012\n \u0012*\u0004\u0018\u00010\u00110\u00110\u000f0\u000e*\u00020\u0005H\u0002J\u000e\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u0011R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R&\u0010\t\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u00058F@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u0007\"\u0004\b\u000b\u0010\fR \u0010\r\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u000f0\u000eX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015"},
   d2 = {"Lcom/nexomc/nexo/glyphs/Shift;", "", "<init>", "()V", "defaultShiftProvider", "Lteam/unnamed/creative/font/SpaceFontProvider;", "getDefaultShiftProvider", "()Lteam/unnamed/creative/font/SpaceFontProvider;", "value", "fontProvider", "getFontProvider", "setFontProvider", "(Lteam/unnamed/creative/font/SpaceFontProvider;)V", "cachedPowers", "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", "Lkotlin/Pair;", "", "", "kotlin.jvm.PlatformType", "of", "shift", "com.nexomc:core"}
)
public final class Shift {
   @NotNull
   public static final Shift INSTANCE = new Shift();
   @NotNull
   private static final SpaceFontProvider defaultShiftProvider;
   @NotNull
   private static SpaceFontProvider fontProvider;
   @NotNull
   private static final ObjectArrayList<Pair<String, Integer>> cachedPowers;

   private Shift() {
   }

   @NotNull
   public final SpaceFontProvider getDefaultShiftProvider() {
      return defaultShiftProvider;
   }

   @NotNull
   public final SpaceFontProvider getFontProvider() {
      SpaceFontProvider it = fontProvider;
      int value = 0;
      SpaceFontProvider spaceFontProvider = !it.advances().isEmpty() ? it : null;
      if (spaceFontProvider == null) {
         Shift shift = this;
         value = 0;
         Logs.logWarn("Provided Shift-FontProvider was empty, defaulting to Nexo's built-in", false);
         shift.setFontProvider(defaultShiftProvider);
         spaceFontProvider = defaultShiftProvider;
      }

      return spaceFontProvider;
   }

   public final void setFontProvider(@NotNull SpaceFontProvider value) {
      int value2 = 0;
      Map advances = value.advances();
      SpaceFontProvider spaceFontProvider = !advances.isEmpty() ? value : null;
      if (spaceFontProvider == null) {
         spaceFontProvider = defaultShiftProvider;
      }

      fontProvider = spaceFontProvider;
      cachedPowers.clear();
      cachedPowers.addAll((ObjectList)this.cachedPowers(fontProvider));
   }

   private final ObjectArrayList<Pair<String, Integer>> cachedPowers(SpaceFontProvider spaceFontProvider) {
      boolean flag;
      Map advances = spaceFontProvider.advances();
      Iterable iterable = (Iterable)MapsKt.toList(advances);
      iterable = (Iterable)CollectionsKt.sortedWith(iterable, new Shift$cachedPowers$$inlined$sortedByDescending$1());
      Collection destination = (Collection)(new ObjectArrayList());

      for(Object element : iterable) {
         block: {
            Pair it = (Pair)element;
            int value = 0;
            Integer second = (Integer)it.getSecond();
            if (second != null) {
               if (second == 0) {
                  flag = false;
                  break block;
               }
            }

            flag = true;
         }

         if (flag) {
            destination.add(element);
         }
      }

      return (ObjectArrayList)destination;
   }

   @NotNull
   public final String of(int shift) {
      int remainingShift = 0;
      remainingShift = Math.abs(shift);
      StringBuilder stringBuilder = new StringBuilder();
      StringBuilder stringBuilder2 = stringBuilder;
      int value2 = 0;
      ObjectListIterator objectListIterator = cachedPowers.iterator();
      ObjectListIterator objectListIterator2 = objectListIterator;

      while(objectListIterator2.hasNext()) {
         Pair pair = (Pair)objectListIterator2.next();
         String component1 = (String)pair.component1();
         int value = ((Number)pair.component2()).intValue();
         if ((shift >= 0 || value <= 0) && (shift <= 0 || value >= 0)) {
            int absValue = Math.abs(value);
            if (remainingShift >= absValue) {
               stringBuilder2.append(component1);
               remainingShift -= absValue;
            }
         }
      }

      return stringBuilder.toString();
   }

   static {
      Pair[] pairs = new Pair[]{TuplesKt.to("\ue101", -1), TuplesKt.to("\ue102", -2), TuplesKt.to("\ue103", -4), TuplesKt.to("\ue104", -8), TuplesKt.to("\ue105", -16), TuplesKt.to("\ue106", -32), TuplesKt.to("\ue107", -64), TuplesKt.to("\ue108", -128), TuplesKt.to("\ue109", -256), TuplesKt.to("\ue110", -512), TuplesKt.to("\ue112", 1), TuplesKt.to("\ue113", 2), TuplesKt.to("\ue114", 4), TuplesKt.to("\ue115", 8), TuplesKt.to("\ue116", 16), TuplesKt.to("\ue117", 32), TuplesKt.to("\ue118", 64), TuplesKt.to("\ue119", 128), TuplesKt.to("\ue120", 256), TuplesKt.to("\ue121", 512)};
      SpaceFontProvider spaceFontProvider = FontProvider.space(MapsKt.mapOf(pairs));
      defaultShiftProvider = spaceFontProvider;
      Shift shift = INSTANCE;
      fontProvider = defaultShiftProvider;
      Shift shift2 = INSTANCE;
      cachedPowers = new ObjectArrayList((ObjectList)INSTANCE.cachedPowers(defaultShiftProvider));
   }
}
