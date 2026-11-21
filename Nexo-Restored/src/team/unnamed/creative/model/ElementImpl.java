package team.unnamed.creative.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.util.MoreCollections;

record ElementImpl(Vector3Float from, Vector3Float to, ElementRotation rotation, boolean shade, Map<CubeFace, ElementFace> faces, int lightEmission) implements Element {
   ElementImpl(final @NotNull Vector3Float from, final @NotNull Vector3Float to, final @Nullable ElementRotation rotation, final boolean shade, final @NotNull Map<CubeFace, ElementFace> faces, final int lightEmission) {
      this.from = (Vector3Float)Objects.requireNonNull(from, "from");
      this.to = (Vector3Float)Objects.requireNonNull(to, "to");
      this.rotation = rotation;
      this.shade = shade;
      this.faces = MoreCollections.<CubeFace, ElementFace>immutableMapOf((Map)Objects.requireNonNull(faces, "faces"));
      this.lightEmission = lightEmission;
      this.validate();
   }

   private void validateBound(float value, String axisName) {
      if (value < -16.0F || value > 32.0F) {
         throw new IllegalArgumentException("Value at " + axisName + " axis (" + value + ") is out of bounds");
      }
   }

   private void validateBound(Vector3Float vec) {
      this.validateBound(vec.x(), "X");
      this.validateBound(vec.y(), "Y");
      this.validateBound(vec.z(), "Z");
   }

   private void validate() {
      this.validateBound(this.from);
      this.validateBound(this.to);
      if (this.faces.isEmpty() || this.faces.size() > 6) {
         throw new IllegalArgumentException("Invalid amount of faces (" + this.faces.size() + ")");
      }
   }

   public @NotNull Vector3Float from() {
      return this.from;
   }

   public @NotNull Vector3Float to() {
      return this.to;
   }

   public @NotNull ElementRotation rotation() {
      return this.rotation;
   }

   public @Unmodifiable @NotNull Map<CubeFace, ElementFace> faces() {
      return this.faces;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{from=" + String.valueOf(this.from) + ", to=" + String.valueOf(this.to) + ", rotation=" + String.valueOf(this.rotation) + ", shade=" + this.shade + ", faces=" + String.valueOf(this.faces) + ", light_emission=" + this.lightEmission + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ElementImpl element = (ElementImpl)o;
         return this.from.equals(element.from) && this.to.equals(element.to) && Objects.equals(this.rotation, element.rotation) && this.shade == element.shade && this.faces.equals(element.faces) && this.lightEmission == element.lightEmission;
      } else {
         return false;
      }
   }

   public Element.@NotNull Builder toBuilder() {
      return Element.element().from(this.from).to(this.to).rotation(this.rotation).shade(this.shade).faces(this.faces).lightEmission(this.lightEmission);
   }

   static final class BuilderImpl implements Element.Builder {
      private Vector3Float from;
      private Vector3Float to;
      private ElementRotation rotation = null;
      private boolean shade = true;
      private Map<CubeFace, ElementFace> faces = new LinkedHashMap();
      private int lightEmission = 0;

      public Element.@NotNull Builder from(@NotNull Vector3Float from) {
         this.from = (Vector3Float)Objects.requireNonNull(from, "from");
         return this;
      }

      public Element.@NotNull Builder to(@NotNull Vector3Float to) {
         this.to = (Vector3Float)Objects.requireNonNull(to, "to");
         return this;
      }

      public Element.@NotNull Builder rotation(@Nullable ElementRotation rotation) {
         this.rotation = rotation;
         return this;
      }

      public Element.@NotNull Builder shade(boolean shade) {
         this.shade = shade;
         return this;
      }

      public Element.@NotNull Builder faces(@NotNull Map<CubeFace, ElementFace> faces) {
         this.faces = new LinkedHashMap((Map)Objects.requireNonNull(faces, "faces"));
         return this;
      }

      public Element.@NotNull Builder addFace(@NotNull CubeFace type, @NotNull ElementFace face) {
         Objects.requireNonNull(type, "type");
         Objects.requireNonNull(face, "face");
         this.faces.put(type, face);
         return this;
      }

      public Element.@NotNull Builder lightEmission(int lightEmission) {
         this.lightEmission = lightEmission;
         return this;
      }

      public @NotNull Element build() {
         return new ElementImpl(this.from, this.to, this.rotation, this.shade, this.faces, this.lightEmission);
      }
   }
}
