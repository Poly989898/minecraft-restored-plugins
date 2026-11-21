package team.unnamed.creative.item;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.item.property.ItemBooleanProperty;
import team.unnamed.creative.item.property.ItemNumericProperty;
import team.unnamed.creative.item.property.ItemStringProperty;
import team.unnamed.creative.item.special.SpecialRender;
import team.unnamed.creative.item.tint.TintSource;

public interface ItemModel {
   @Nullable Transformation transformation();

   static @NotNull ReferenceItemModel reference(@NotNull Key model, @NotNull List<TintSource> tints, @Nullable Transformation transformation) {
      return new ReferenceItemModelImpl(model, tints, transformation);
   }

   static @NotNull ReferenceItemModel reference(@NotNull Key model, @NotNull List<TintSource> tints) {
      return new ReferenceItemModelImpl(model, tints, (Transformation)null);
   }

   static @NotNull ReferenceItemModel reference(@NotNull Key model, @NotNull TintSource... tints) {
      return reference(model, Arrays.asList(tints));
   }

   static @NotNull ReferenceItemModel reference(@NotNull Key model) {
      return reference(model, Collections.emptyList());
   }

   static @NotNull EmptyItemModel empty() {
      return EmptyItemModelImpl.INSTANCE;
   }

   static @NotNull BundleSelectedItemModel bundleSelectedItem() {
      return BundleSelectedItemModelImpl.INSTANCE;
   }

   static @NotNull CompositeItemModel composite(@NotNull List<ItemModel> models, @Nullable Transformation transformation) {
      return new CompositeItemModelImpl(models, transformation);
   }

   static @NotNull CompositeItemModel composite(@NotNull List<ItemModel> models) {
      return new CompositeItemModelImpl(models, (Transformation)null);
   }

   static @NotNull CompositeItemModel composite(@NotNull ItemModel... models) {
      return new CompositeItemModelImpl(Arrays.asList(models), (Transformation)null);
   }

   static @NotNull ConditionItemModel conditional(@NotNull ItemBooleanProperty condition, @NotNull ItemModel onTrue, @NotNull ItemModel onFalse) {
      return new ConditionItemModelImpl(condition, onTrue, onFalse, (Transformation)null);
   }

   static @NotNull ConditionItemModel conditional(@NotNull ItemBooleanProperty condition, @NotNull ItemModel onTrue, @NotNull ItemModel onFalse, @Nullable Transformation transformation) {
      return new ConditionItemModelImpl(condition, onTrue, onFalse, transformation);
   }

   static SelectItemModel.@NotNull Builder select() {
      return new SelectItemModelImpl.BuilderImpl();
   }

   static @NotNull SelectItemModel select(@NotNull ItemStringProperty property, @NotNull List<SelectItemModel.Case> cases, @Nullable ItemModel fallback, @Nullable Transformation transformation) {
      return new SelectItemModelImpl(property, cases, fallback, transformation);
   }

   static @NotNull SelectItemModel select(@NotNull ItemStringProperty property, @NotNull List<SelectItemModel.Case> cases, @Nullable ItemModel fallback) {
      return new SelectItemModelImpl(property, cases, fallback, (Transformation)null);
   }

   static @NotNull SelectItemModel select(@NotNull ItemStringProperty property, @NotNull List<SelectItemModel.Case> cases) {
      return select(property, cases, (ItemModel)null);
   }

   static @NotNull SelectItemModel select(@NotNull ItemStringProperty property, SelectItemModel.@NotNull Case... cases) {
      return select(property, Arrays.asList(cases));
   }

   static @NotNull SpecialItemModel special(@NotNull SpecialRender render, @NotNull Key base, @Nullable Transformation transformation) {
      return new SpecialItemModelImpl(render, base, transformation);
   }

   static @NotNull SpecialItemModel special(@NotNull SpecialRender render, @NotNull Key base) {
      return new SpecialItemModelImpl(render, base, (Transformation)null);
   }

   static @NotNull RangeDispatchItemModel rangeDispatch(@NotNull ItemNumericProperty property, float scale, @NotNull List<RangeDispatchItemModel.Entry> entries, @Nullable ItemModel fallback, @Nullable Transformation transformation) {
      return new RangeDispatchItemModelImpl(property, scale, entries, fallback, transformation);
   }

   static @NotNull RangeDispatchItemModel rangeDispatch(@NotNull ItemNumericProperty property, float scale, @NotNull List<RangeDispatchItemModel.Entry> entries, @Nullable ItemModel fallback) {
      return new RangeDispatchItemModelImpl(property, scale, entries, fallback, (Transformation)null);
   }

   static @NotNull RangeDispatchItemModel rangeDispatch(@NotNull ItemNumericProperty property, float scale, @NotNull List<RangeDispatchItemModel.Entry> entries) {
      return rangeDispatch(property, scale, entries, (ItemModel)null);
   }

   static @NotNull RangeDispatchItemModel rangeDispatch(@NotNull ItemNumericProperty property, float scale, RangeDispatchItemModel.@NotNull Entry... entries) {
      return rangeDispatch(property, scale, Arrays.asList(entries));
   }

   static @NotNull RangeDispatchItemModel rangeDispatch(@NotNull ItemNumericProperty property, RangeDispatchItemModel.@NotNull Entry... entries) {
      return rangeDispatch(property, 1.0F, Arrays.asList(entries));
   }

   static RangeDispatchItemModel.@NotNull Builder rangeDispatch() {
      return new RangeDispatchItemModelImpl.BuilderImpl();
   }
}
