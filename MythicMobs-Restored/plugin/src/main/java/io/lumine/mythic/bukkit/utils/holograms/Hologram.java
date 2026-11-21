package io.lumine.mythic.bukkit.utils.holograms;

import com.google.gson.JsonElement;
import io.lumine.mythic.bukkit.utils.Services;
import io.lumine.mythic.bukkit.utils.gson.GsonSerializable;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import java.util.List;
import javax.annotation.Nonnull;

public interface Hologram extends BaseHologram, GsonSerializable {
   @Nonnull
   static Hologram create(@Nonnull Position position, @Nonnull List<String> lines) {
      return Services.load(HologramFactory.class).newHologram(position, lines);
   }

   static Hologram deserialize(JsonElement element) {
      return Services.load(HologramFactory.class).deserialize(element);
   }

   void updateLines(@Nonnull List<String> strs);

   Position getPosition();
}
