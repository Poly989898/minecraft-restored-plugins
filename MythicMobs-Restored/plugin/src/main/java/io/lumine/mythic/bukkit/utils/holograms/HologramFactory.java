package io.lumine.mythic.bukkit.utils.holograms;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public interface HologramFactory {
   @Nonnull
   Hologram newHologram(@Nonnull Position position, @Nonnull List<String> strs);

   @Nonnull
   default Hologram deserialize(JsonElement element) {
      Preconditions.checkArgument(element.isJsonObject());
      JsonObject object = element.getAsJsonObject();
      Preconditions.checkArgument(object.has("position"));
      Preconditions.checkArgument(object.has("lines"));
      Position position = Position.deserialize(object.get("position"));
      JsonArray lineArray = object.get("lines").getAsJsonArray();
      List<String> lines = new ArrayList<>();

      for (JsonElement e : lineArray) {
         lines.add(e.getAsString());
      }

      return this.newHologram(position, lines);
   }
}
