package com.nexomc.nexo.compatibilities.tab;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.glyphs.GlyphShadow;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.PluginUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.event.EventBus;
import me.neznamy.tab.api.event.plugin.PlaceholderRegisterEvent;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005¨\u0006\u0006"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/tab/TABCompatibility;", "", "<init>", "()V", "registerPlaceholders", "", "com.nexomc:core"}
)
public final class TABCompatibility {
   @NotNull
   public static final TABCompatibility INSTANCE = new TABCompatibility();

   private TABCompatibility() {
   }

   public final void registerPlaceholders() {
      TABCompatibility tabCompatibility = this;

      try {
         TABCompatibility tabCompatibility2 = tabCompatibility;
         int value = 0;
         if (PluginUtils.INSTANCE.isEnabled("TAB")) {
            EventBus eventBus = TabAPI.getInstance().getEventBus();
            if (eventBus != null) {
               eventBus.register(PlaceholderRegisterEvent.class, TABCompatibility::registerPlaceholders$lambda$0$0);
            }
         }

         Object obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

   }

   private static final String registerPlaceholders$lambda$0$0$1(String placeholder) {
      return placeholder;
   }

   private static final void registerPlaceholders$lambda$0$0(PlaceholderRegisterEvent event) {
      Iterable glyphs = (Iterable)NexoPlugin.Companion.instance().fontManager().glyphs();
      int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(glyphs, 10)), 16);
      Map destination = (Map)(new LinkedHashMap(capacity));

      for(Object element : glyphs) {
         Glyph it = (Glyph)element;
         int value = 0;
         Pair pair = TuplesKt.to("%nexo_" + it.getId() + "%", AdventureUtilsKt.serializeStrict(it.glyphComponent(false, it.getDefaultShadow(), kotlin.ranges.IntRange.Companion.getEMPTY())));
         destination.put(pair.getFirst(), pair.getSecond());
      }

      String str = (String)destination.get(event.getIdentifier());
      if (str != null) {
         String placeholder = str;
         event.setServerPlaceholder(() -> registerPlaceholders$lambda$0$0$1(placeholder));
      }
   }
}
