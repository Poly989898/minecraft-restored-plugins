package team.unnamed.creative.item;

import com.google.gson.JsonElement;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.item.property.ItemStringProperty;

public interface SelectItemModel extends ItemModel {
   @NotNull ItemStringProperty property();

   @NotNull List<Case> cases();

   @Nullable ItemModel fallback();

   default @NotNull Builder toBuilder() {
      return ItemModel.select().property(this.property()).fallback(this.fallback()).addCases(this.cases());
   }

   public interface Case {
      @NotNull List<JsonElement> when();

      @NotNull ItemModel model();

      @Nullable Transformation transformation();

      static @NotNull Case _case(@NotNull ItemModel model, @NotNull List<JsonElement> when, @Nullable Transformation transformation) {
         return new SelectItemModelImpl.CaseImpl(when, model, transformation);
      }

      static @NotNull Case _case(@NotNull ItemModel model, @NotNull List<JsonElement> when) {
         return new SelectItemModelImpl.CaseImpl(when, model, (Transformation)null);
      }

      static @NotNull Case _case(@NotNull ItemModel model, @NotNull JsonElement... when) {
         return _case(model, Arrays.asList(when));
      }
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder transformation(@Nullable Transformation var1);

      @Contract("_ -> this")
      @NotNull Builder property(@NotNull ItemStringProperty var1);

      @Contract("_ -> this")
      @NotNull Builder addCase(@NotNull Case var1);

      @Contract("_, _ -> this")
      default @NotNull Builder addCase(@NotNull ItemModel model, @NotNull List<JsonElement> when) {
         return this.addCase(SelectItemModel.Case._case(model, when));
      }

      @Contract("_, _ -> this")
      default @NotNull Builder addCase(@NotNull ItemModel model, @NotNull JsonElement... when) {
         return this.addCase(model, Arrays.asList(when));
      }

      @Contract("_ -> this")
      default @NotNull Builder addCases(@NotNull List<Case> cases) {
         cases.forEach(this::addCase);
         return this;
      }

      @Contract("_ -> this")
      @NotNull Builder fallback(@Nullable ItemModel var1);

      @Contract("-> new")
      @NotNull SelectItemModel build();
   }
}
