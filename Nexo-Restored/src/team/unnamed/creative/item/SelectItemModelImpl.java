package team.unnamed.creative.item;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.item.property.ItemStringProperty;

record SelectItemModelImpl(ItemStringProperty property, List<SelectItemModel.Case> cases, ItemModel fallback, Transformation transformation) implements SelectItemModel {
   SelectItemModelImpl(final @NotNull ItemStringProperty property, final @NotNull List<SelectItemModel.Case> cases, final @Nullable ItemModel fallback, final @Nullable Transformation transformation) {
      this.property = (ItemStringProperty)Objects.requireNonNull(property, "property");
      this.cases = (List)Objects.requireNonNull(cases, "cases");
      this.fallback = fallback;
      this.transformation = transformation;
   }

   public @NotNull ItemStringProperty property() {
      return this.property;
   }

   public @NotNull List<SelectItemModel.Case> cases() {
      return this.cases;
   }

   public @Nullable ItemModel fallback() {
      return this.fallback;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{property=" + String.valueOf(this.property) + ", cases=" + String.valueOf(this.cases) + ", fallback=" + String.valueOf(this.fallback) + ", transformation=" + String.valueOf(this.transformation) + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         SelectItemModelImpl that = (SelectItemModelImpl)o;
         return this.property.equals(that.property) && this.cases.equals(that.cases) && Objects.equals(this.fallback, that.fallback) && Objects.equals(this.transformation, that.transformation);
      } else {
         return false;
      }
   }

   static record CaseImpl(List<JsonElement> when, ItemModel model, Transformation transformation) implements SelectItemModel.Case {
      CaseImpl(final @NotNull List<JsonElement> when, final @NotNull ItemModel model, final @Nullable Transformation transformation) {
         this.when = (List)Objects.requireNonNull(when, "when");
         this.model = (ItemModel)Objects.requireNonNull(model, "model");
         this.transformation = transformation;
      }

      public @NotNull List<JsonElement> when() {
         return this.when;
      }

      public @NotNull ItemModel model() {
         return this.model;
      }

      public @Nullable Transformation transformation() {
         return this.transformation;
      }

      public @NotNull String toString() {
         String var10000 = this.getClass().getSimpleName();
         return var10000 + "{when=" + String.valueOf(this.when) + ", model=" + String.valueOf(this.model) + ", transformation=" + String.valueOf(this.transformation) + "}";
      }

      public boolean equals(Object o) {
         if (o != null && this.getClass() == o.getClass()) {
            CaseImpl that = (CaseImpl)o;
            return this.when.equals(that.when) && this.model.equals(that.model) && Objects.equals(this.transformation, that.transformation);
         } else {
            return false;
         }
      }
   }

   static final class BuilderImpl implements SelectItemModel.Builder {
      private ItemStringProperty property;
      private final List<SelectItemModel.Case> cases = new ArrayList();
      private ItemModel fallback;
      private Transformation transformation;

      public SelectItemModel.@NotNull Builder transformation(@Nullable Transformation transformation) {
         this.transformation = transformation;
         return this;
      }

      public SelectItemModel.@NotNull Builder property(@NotNull ItemStringProperty property) {
         this.property = (ItemStringProperty)Objects.requireNonNull(property, "property");
         return this;
      }

      public SelectItemModel.@NotNull Builder addCase(SelectItemModel.@NotNull Case _case) {
         Objects.requireNonNull(_case, "_case");
         this.cases.add(_case);
         return this;
      }

      public SelectItemModel.@NotNull Builder fallback(@Nullable ItemModel fallback) {
         this.fallback = fallback;
         return this;
      }

      public @NotNull SelectItemModel build() {
         return new SelectItemModelImpl((ItemStringProperty)Objects.requireNonNull(this.property, "property"), this.cases, this.fallback, this.transformation);
      }
   }
}
