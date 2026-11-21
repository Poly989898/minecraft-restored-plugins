package io.lumine.mythic.bukkit.utils.gson.converters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@NonnullByDefault
public interface GsonConverter {
   Map<String, Object> unwrapObject(JsonObject jsonObject);

   List<Object> unwrapArray(JsonArray jsonArray);

   Set<Object> unwrapArrayToSet(JsonArray jsonArray);

   Object unwarpPrimitive(JsonPrimitive jsonPrimitive);

   @Nullable
   Object unwrapElement(JsonElement jsonElement);

   JsonElement wrap(Object obj);
}
