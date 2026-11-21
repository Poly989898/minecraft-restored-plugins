package team.unnamed.creative.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

record ModelImpl(Key key, Key parent, boolean ambientOcclusion, Map<ItemTransform.Type, ItemTransform> display, ModelTextures textures, Model.GuiLight guiLight, List<Element> elements, List<ItemOverride> overrides) implements Model {
   ModelImpl(final @NotNull Key key, final @Nullable Key parent, final boolean ambientOcclusion, final @NotNull Map<ItemTransform.Type, ItemTransform> display, final @NotNull ModelTextures textures, final Model.@Nullable GuiLight guiLight, final @NotNull List<Element> elements, final @NotNull List<ItemOverride> overrides) {
      this.key = (Key)Objects.requireNonNull(key, "key");
      this.parent = parent;
      this.ambientOcclusion = ambientOcclusion;
      this.display = (Map)Objects.requireNonNull(display, "display");
      this.textures = (ModelTextures)Objects.requireNonNull(textures, "textures");
      this.guiLight = guiLight;
      this.elements = (List)Objects.requireNonNull(elements, "elements");
      this.overrides = (List)Objects.requireNonNull(overrides, "oveerrides");
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @Nullable Key parent() {
      return this.parent;
   }

   public @NotNull @Unmodifiable Map<ItemTransform.Type, ItemTransform> display() {
      return this.display;
   }

   public @NotNull ModelTextures textures() {
      return this.textures;
   }

   public Model.@Nullable GuiLight guiLight() {
      return this.guiLight;
   }

   public @NotNull @Unmodifiable List<Element> elements() {
      return this.elements;
   }

   public @NotNull List<ItemOverride> overrides() {
      return this.overrides;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", parent=" + String.valueOf(this.parent) + ", ambientocclusion=" + this.ambientOcclusion + ", display=" + String.valueOf(this.display) + ", textures=" + String.valueOf(this.textures) + ", guiLight=" + String.valueOf(this.guiLight) + ", elements=" + String.valueOf(this.elements) + ", overrides=" + String.valueOf(this.overrides) + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ModelImpl that = (ModelImpl)o;
         return this.key.equals(that.key) && this.ambientOcclusion == that.ambientOcclusion && Objects.equals(this.parent, that.parent) && this.display.equals(that.display) && this.textures.equals(that.textures) && this.guiLight == that.guiLight && this.elements.equals(that.elements) && this.overrides.equals(that.overrides);
      } else {
         return false;
      }
   }

   static final class BuilderImpl implements Model.Builder {
      private Key key;
      private Key parent;
      private boolean ambientOcclusion = true;
      private Map<ItemTransform.Type, ItemTransform> display = new LinkedHashMap();
      private ModelTextures textures;
      private Model.GuiLight guiLight;
      private List<Element> elements;
      private List<ItemOverride> overrides;

      BuilderImpl() {
         this.textures = ModelTextures.EMPTY;
         this.elements = new ArrayList();
         this.overrides = new ArrayList();
      }

      public Model.@NotNull Builder key(@NotNull Key key) {
         this.key = (Key)Objects.requireNonNull(key, "key");
         return this;
      }

      public Model.@NotNull Builder parent(@Nullable Key parent) {
         this.parent = parent;
         return this;
      }

      public Model.@NotNull Builder ambientOcclusion(boolean ambientOcclusion) {
         this.ambientOcclusion = ambientOcclusion;
         return this;
      }

      public Model.@NotNull Builder display(@NotNull Map<ItemTransform.Type, ItemTransform> display) {
         Objects.requireNonNull(display, "display");
         this.display = new LinkedHashMap(display);
         return this;
      }

      public Model.@NotNull Builder textures(@NotNull ModelTextures textures) {
         this.textures = (ModelTextures)Objects.requireNonNull(textures, "textures");
         return this;
      }

      public Model.@NotNull Builder guiLight(Model.@Nullable GuiLight guiLight) {
         this.guiLight = guiLight;
         return this;
      }

      public Model.@NotNull Builder elements(@NotNull List<Element> elements) {
         Objects.requireNonNull(elements, "elements");
         this.elements = new ArrayList(elements);
         return this;
      }

      public Model.@NotNull Builder addElement(@NotNull Element element) {
         Objects.requireNonNull(element, "element");
         this.elements.add(element);
         return this;
      }

      public Model.@NotNull Builder overrides(@NotNull List<ItemOverride> overrides) {
         Objects.requireNonNull(overrides, "overrides");
         this.overrides = new ArrayList(overrides);
         return this;
      }

      public Model.@NotNull Builder addOverride(@NotNull ItemOverride override) {
         Objects.requireNonNull(override, "override");
         this.overrides.add(override);
         return this;
      }

      public @NotNull Model build() {
         return new ModelImpl(this.key, this.parent, this.ambientOcclusion, this.display, this.textures, this.guiLight, this.elements, this.overrides);
      }
   }
}
