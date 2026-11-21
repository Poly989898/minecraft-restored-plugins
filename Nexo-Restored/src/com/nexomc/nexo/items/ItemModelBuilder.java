package com.nexomc.nexo.items;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.JsonBuilder;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.Map;
import java.util.Set;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.item.ItemSerializer;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0007HÆ\u0003J'\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÆ\u0001J\u0014\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0019\u001a\u00020\u001aHÖ\u0081\u0004J\n\u0010\u001b\u001a\u00020\u001cHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001d"},
   d2 = {"Lcom/nexomc/nexo/items/ItemModelBuilder;", "", "key", "Lnet/kyori/adventure/key/Key;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "format", "Lteam/unnamed/creative/metadata/pack/PackFormat;", "<init>", "(Lnet/kyori/adventure/key/Key;Lorg/bukkit/configuration/ConfigurationSection;Lteam/unnamed/creative/metadata/pack/PackFormat;)V", "getKey", "()Lnet/kyori/adventure/key/Key;", "getSection", "()Lorg/bukkit/configuration/ConfigurationSection;", "getFormat", "()Lteam/unnamed/creative/metadata/pack/PackFormat;", "parse", "Lteam/unnamed/creative/item/Item;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
)
public final class ItemModelBuilder {
   @NotNull
   private final Key key;
   @NotNull
   private final ConfigurationSection section;
   @NotNull
   private final PackFormat format;

   public ItemModelBuilder(@NotNull Key key, @NotNull ConfigurationSection section, @NotNull PackFormat format) {
      super();
      this.key = key;
      this.section = section;
      this.format = format;
   }

   @NotNull
   public final Key getKey() {
      return this.key;
   }

   @NotNull
   public final ConfigurationSection getSection() {
      return this.section;
   }

   @NotNull
   public final PackFormat getFormat() {
      return this.format;
   }

   @Nullable
   public final Item parse() {
      ItemModelBuilder itemModelBuilder = this;

      Object obj2;
      try {
         ItemModelBuilder itemModelBuilder2 = itemModelBuilder;
         int value = 0;
         JsonObject jsonObject = JsonBuilder.INSTANCE.toJsonObject(itemModelBuilder2.section);
         if (!jsonObject.has("model") || jsonObject.get("model").isJsonPrimitive()) {
            JsonObject deepCopy = jsonObject.deepCopy();
            JsonObject copy = parse$lambda$0$parseModelDefRoot(deepCopy);
            copy.remove("hand_animation_on_swap");
            copy.remove("oversized_in_gui");
            copy.remove("swap_animation_scale");
            Set keySet = copy.keySet();

            for(String key : (java.lang.Iterable<String>)(java.lang.Iterable)CollectionsKt.toList((Iterable)keySet)) {
               jsonObject.remove(key);
            }

            jsonObject.add("model", copy);
         }

         obj2 = ResultShim.ok(ItemSerializer.INSTANCE.deserializeFromJson(jsonObject, itemModelBuilder2.key, itemModelBuilder2.format));
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj2;
      Throwable throwable = ResultShim.exceptionOrNull(obj2);
      if (throwable != null) {
         Throwable it = throwable;
         int value2 = 0;
         Logs.logWarn("Failed to parse ItemModel in <gold><i>" + NexoYamlKt.getRootId(this.section) + "...", false);
         if (Settings.DEBUG.toBool()) {
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         } else {
            String message = it.getMessage();
            Logs.logWarn(message, false);
         }
      }

      return (Item)(ResultShim.isFailure(obj) ? null : obj);
   }

   @NotNull
   public final Key component1() {
      return this.key;
   }

   @NotNull
   public final ConfigurationSection component2() {
      return this.section;
   }

   @NotNull
   public final PackFormat component3() {
      return this.format;
   }

   @NotNull
   public final ItemModelBuilder copy(@NotNull Key key, @NotNull ConfigurationSection section, @NotNull PackFormat format) {
      return new ItemModelBuilder(key, section, format);
   }

   @NotNull
   public String toString() {
      return "ItemModelBuilder(key=" + this.key + ", section=" + this.section + ", format=" + this.format + ")";
   }

   public int hashCode() {
      int result = this.key.hashCode();
      result = result * 31 + this.section.hashCode();
      result = result * 31 + this.format.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof ItemModelBuilder)) {
         return false;
      } else {
         ItemModelBuilder itemModelBuilder = (ItemModelBuilder)other;
         if (!Intrinsics.areEqual(this.key, itemModelBuilder.key)) {
            return false;
         } else if (!Intrinsics.areEqual(this.section, itemModelBuilder.section)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.format, itemModelBuilder.format);
         }
      }
   }

   private static final JsonObject parse$lambda$0$parseModelDefRoot(JsonObject json) {
      for(Map.Entry entry : json.entrySet()) {
         String key = (String)entry.getKey();
         JsonElement value = (JsonElement)entry.getValue();
         JsonObject jsonObject = value instanceof JsonObject ? (JsonObject)value : null;
         if ((value instanceof JsonObject ? (JsonObject)value : null) != null) {
            json.add(key, parse$lambda$0$parseModelDefRoot(jsonObject));
         }
      }

      if (!json.has("when") && !json.has("threshold")) {
         return json;
      } else if (!json.has("type")) {
         return json;
      } else {
         JsonElement type = json.remove("type");
         JsonElement model = json.remove("model");
         JsonObject modelJson = JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "type", type), "model", model);
         json.add("model", modelJson);
         return json;
      }
   }
}
