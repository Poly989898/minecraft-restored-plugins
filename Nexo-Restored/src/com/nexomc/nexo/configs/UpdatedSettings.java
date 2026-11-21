package com.nexomc.nexo.configs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0011\b\u0086\u0081\u0002\u0018\u0000 \u00132\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0013B\u0019\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\n\u0010\u0012\u001a\u00020\u0003H\u0096\u0080\u0004R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011¨\u0006\u0014"},
   d2 = {"Lcom/nexomc/nexo/configs/UpdatedSettings;", "", "path", "", "newPath", "<init>", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", "PACK_SEND_ON_JOIN", "GENERATE_DEFAULT_CONFIGS", "NEXO_INV", "PACK_IMPORT_MODEL_ENGINE", "REMOVE_INVALID_FURNITURE", "INVALID_FURNITURE_ITEM", "RESET_RECIPES", "ADD_RECIPES_TO_BOOK", "DEFAULT_ALLOW_IN_VANILLA_RECIPES", "FORMAT_SYSTEM_CHAT", "FORMAT_CHAT_STANDARD_TAGS", "toString", "Companion", "com.nexomc:core"}
)
public enum UpdatedSettings {
   PACK_SEND_ON_JOIN("Pack.dispatch.send_pack", "Pack.dispatch.send_on_join"),
   GENERATE_DEFAULT_CONFIGS("Plugin.default_content.default_configs", "ConfigTools.generate_default_configs"),
   NEXO_INV("nexo_inventory", "NexoInventory"),
   PACK_IMPORT_MODEL_ENGINE("Pack.import.model_engine.import_pack", "Pack.import.model_engine"),
   REMOVE_INVALID_FURNITURE("ConfigTools.remove_invalid_furniture", "Furniture.remove_invalid_furniture"),
   INVALID_FURNITURE_ITEM("ConfigTools.invalid_furniture_item", "Furniture.invalid_furniture_item"),
   RESET_RECIPES("Misc.reset_recipes", "Recipes.reset_recipes"),
   ADD_RECIPES_TO_BOOK("Misc.add_recipes_to_book", "Recipes.add_recipes_to_book"),
   DEFAULT_ALLOW_IN_VANILLA_RECIPES("Misc.default_allow_in_vanilla_recipes", "Recipes.default_allow_in_vanilla_recipes"),
   FORMAT_SYSTEM_CHAT("Plugin.formatting.chat", "Plugin.formatting.chats.system_chat"),
   FORMAT_CHAT_STANDARD_TAGS("Plugin.formatting.chat_standard_tags", "Plugin.formatting.chats.standard_tags");

   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final String path;
   @NotNull
   private final String newPath;

   private UpdatedSettings(String path, String newPath) {
      this.path = path;
      this.newPath = newPath;
   }

   @NotNull
   public String toString() {
      return this.path + ", " + this.newPath;
   }


   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00060\u0005¨\u0006\u0007"},
      d2 = {"Lcom/nexomc/nexo/configs/UpdatedSettings$Companion;", "", "<init>", "()V", "toStringMap", "", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Map<String, String> toStringMap() {
         Iterable iterable = (Iterable)kotlin.enums.EnumEntriesKt.enumEntries(UpdatedSettings.values());
         int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16);
         Map destination = (Map)(new LinkedHashMap(capacity));

         for(Object element : iterable) {
            UpdatedSettings u = (UpdatedSettings)element;
            int value = 0;
            CharSequence charSequence = (CharSequence)u.toString();
            String[] strs = new String[]{", "};
            List it = StringsKt.split(charSequence, strs, false, 2);
            int value2 = 0;
            Pair pair = TuplesKt.to(it.get(0), it.get(1));
            destination.put(pair.getFirst(), pair.getSecond());
         }

         return destination;
      }
   }
}
