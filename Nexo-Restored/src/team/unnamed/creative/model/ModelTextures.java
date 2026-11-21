package team.unnamed.creative.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.util.MoreCollections;

public class ModelTextures {
   static final ModelTextures EMPTY = new ModelTextures(Collections.emptyList(), (ModelTexture)null, Collections.emptyMap());
   private final @Unmodifiable List<ModelTexture> layers;
   private final @Nullable ModelTexture particle;
   private final @Unmodifiable Map<String, ModelTexture> variables;

   private ModelTextures(List<ModelTexture> layers, @Nullable ModelTexture particle, Map<String, ModelTexture> variables) {
      Objects.requireNonNull(layers, "layers");
      Objects.requireNonNull(variables, "variables");
      this.layers = MoreCollections.<ModelTexture>immutableListOf(layers);
      this.particle = particle;
      this.variables = MoreCollections.<String, ModelTexture>immutableMapOf(variables);
   }

   public @Unmodifiable List<ModelTexture> layers() {
      return this.layers;
   }

   public @Nullable ModelTexture particle() {
      return this.particle;
   }

   public @Unmodifiable Map<String, ModelTexture> variables() {
      return this.variables;
   }

   public @NotNull Builder toBuilder() {
      return builder().layers(this.layers).particle(this.particle).variables(this.variables);
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{layers=" + String.valueOf(this.layers) + ", particle=" + String.valueOf(this.particle) + ", variables=" + String.valueOf(this.variables) + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ModelTextures that = (ModelTextures)o;
         return this.layers.equals(that.layers) && Objects.equals(this.particle, that.particle) && this.variables.equals(that.variables);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.layers, this.particle, this.variables});
   }

   public static ModelTextures of(List<ModelTexture> layers, @Nullable ModelTexture particle, Map<String, ModelTexture> variables) {
      return new ModelTextures(layers, particle, variables);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private List<ModelTexture> layers = null;
      private ModelTexture particle = null;
      private Map<String, ModelTexture> variables = null;

      private Builder() {
      }

      @Contract("_ -> this")
      public @NotNull Builder layers(@NotNull List<ModelTexture> layers) {
         Objects.requireNonNull(layers, "layers");
         this.layers = new ArrayList(layers);
         return this;
      }

      @Contract("_ -> this")
      public @NotNull Builder layers(@NotNull ModelTexture... layers) {
         Objects.requireNonNull(layers, "layers");
         this.layers = new ArrayList(Arrays.asList(layers));
         return this;
      }

      @Contract("_ -> this")
      public @NotNull Builder addLayer(@NotNull ModelTexture layer) {
         Objects.requireNonNull(layer, "layer");
         if (this.layers == null) {
            this.layers = new ArrayList();
         }

         this.layers.add(layer);
         return this;
      }

      @Contract("_ -> this")
      public @NotNull Builder addLayers(@NotNull ModelTexture... layers) {
         Objects.requireNonNull(layers, "layers");
         if (this.layers == null) {
            this.layers = new ArrayList();
         }

         Collections.addAll(this.layers, layers);
         return this;
      }

      @Contract("_ -> this")
      public @NotNull Builder particle(@Nullable ModelTexture particle) {
         this.particle = particle;
         return this;
      }

      @Contract("_ -> this")
      public @NotNull Builder variables(@NotNull Map<String, ModelTexture> variables) {
         Objects.requireNonNull(variables, "variables");
         this.variables = new LinkedHashMap(variables);
         return this;
      }

      @Contract("_, _ -> this")
      public @NotNull Builder addVariable(@NotNull String id, @NotNull ModelTexture texture) {
         Objects.requireNonNull(id, "id");
         Objects.requireNonNull(texture, "texture");
         if (this.variables == null) {
            this.variables = new LinkedHashMap();
         }

         this.variables.put(id, texture);
         return this;
      }

      @Contract("-> new")
      public @NotNull ModelTextures build() {
         return new ModelTextures(this.layers == null ? Collections.emptyList() : this.layers, this.particle, this.variables == null ? Collections.emptyMap() : this.variables);
      }
   }
}
