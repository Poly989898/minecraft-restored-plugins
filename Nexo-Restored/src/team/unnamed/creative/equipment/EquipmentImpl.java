package team.unnamed.creative.equipment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

record EquipmentImpl(Key key, Map<EquipmentLayerType, List<EquipmentLayer>> layers) implements Equipment {
   EquipmentImpl(final @NotNull Key key, final @NotNull Map<EquipmentLayerType, List<EquipmentLayer>> layers) {
      this.key = (Key)Objects.requireNonNull(key, "key");
      this.layers = (Map)Objects.requireNonNull(layers, "layers");
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @NotNull Map<EquipmentLayerType, List<EquipmentLayer>> layers() {
      return this.layers;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", layers=" + String.valueOf(this.layers) + "}";
   }

   static final class BuilderImpl implements Equipment.Builder {
      private Key key;
      private final Map<EquipmentLayerType, List<EquipmentLayer>> layers = new LinkedHashMap();

      public Equipment.@NotNull Builder key(@NotNull Key key) {
         this.key = (Key)Objects.requireNonNull(key, "key");
         return this;
      }

      public Equipment.@NotNull Builder addLayer(@NotNull EquipmentLayerType type, @NotNull EquipmentLayer layer) {
         Objects.requireNonNull(type, "type");
         Objects.requireNonNull(layer, "layer");
         ((List)this.layers.computeIfAbsent(type, (t) -> new ArrayList())).add(layer);
         return this;
      }

      public @NotNull Equipment build() {
         Objects.requireNonNull(this.key, "key");
         return new EquipmentImpl(this.key, this.layers);
      }
   }
}
