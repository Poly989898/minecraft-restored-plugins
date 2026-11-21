package com.nexomc.nexo.utils.wrappers;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00120\u0010J\u000f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u0019\u0010\u0014\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0001J\u0014\u0010\u0015\u001a\u00020\n2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0017\u001a\u00020\u0018HÖ\u0081\u0004J\n\u0010\u0019\u001a\u00020\u001aHÖ\u0081\u0004R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u001b"},
   d2 = {"Lcom/nexomc/nexo/utils/wrappers/AttributeWrappers;", "", "wrappers", "", "Lcom/nexomc/nexo/utils/wrappers/AttributeWrapper;", "<init>", "(Ljava/util/List;)V", "getWrappers", "()Ljava/util/List;", "isNotEmpty", "", "build", "Lio/papermc/paper/datacomponent/item/ItemAttributeModifiers;", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "buildLegacy", "Lcom/google/common/collect/Multimap;", "Lorg/bukkit/attribute/Attribute;", "Lorg/bukkit/attribute/AttributeModifier;", "component1", "copy", "equals", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
)
public final class AttributeWrappers {
   @NotNull
   private final List<AttributeWrapper> wrappers;

   public AttributeWrappers(@NotNull List<AttributeWrapper> wrappers) {
      super();
      this.wrappers = wrappers;
   }

   @NotNull
   public final List<AttributeWrapper> getWrappers() {
      return this.wrappers;
   }

   public final boolean isNotEmpty() {
      return !((Collection)this.wrappers).isEmpty();
   }

   @NotNull
   public final ItemAttributeModifiers build(@NotNull ItemStack itemStack) {
      ItemAttributeModifiers.Builder itemAttributes = ItemAttributeModifiers.itemAttributes();
      ItemAttributeModifiers.Builder builder = itemAttributes;
      Iterable iterable = (Iterable)this.wrappers;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         AttributeWrapper it = (AttributeWrapper)item;
         int value = 0;
         destination.add(it.getAttribute());
      }

      Set overridden = CollectionsKt.toSet((Iterable)((List)destination));
      ItemAttributeModifiers data = itemStack.getData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
      if (data != null) {
         List wrapper = data.modifiers();
         if (wrapper != null) {
            Iterable iterable2 = (Iterable)wrapper;
            Collection collection = (Collection)(new ArrayList());

            for(Object element : iterable2) {
               ItemAttributeModifiers.Entry it = (ItemAttributeModifiers.Entry)element;
               int value2 = 0;
               if (!overridden.contains(it.attribute())) {
                  collection.add(element);
               }
            }

            iterable2 = (Iterable)((List)collection);

            for(Object obj2 : iterable2) {
               ItemAttributeModifiers.Entry it = (ItemAttributeModifiers.Entry)obj2;
               int value3 = 0;
               builder.addModifier(it.attribute(), it.modifier());
            }
         }
      }

      for(AttributeWrapper wrapper : this.wrappers) {
         wrapper.addToBuilder(builder);
      }

      Object obj = builder.build();
      return (ItemAttributeModifiers)obj;
   }

   @NotNull
   public final Multimap<Attribute, AttributeModifier> buildLegacy() {
      LinkedHashMultimap multiMap = LinkedHashMultimap.create();

      for(AttributeWrapper wrapper : this.wrappers) {
         multiMap.put(wrapper.getAttribute(), wrapper.getModifier());
      }

      return (Multimap)multiMap;
   }

   @NotNull
   public final List<AttributeWrapper> component1() {
      return this.wrappers;
   }

   @NotNull
   public final AttributeWrappers copy(@NotNull List<AttributeWrapper> wrappers) {
      return new AttributeWrappers(wrappers);
   }

   @NotNull
   public String toString() {
      return "AttributeWrappers(wrappers=" + this.wrappers + ")";
   }

   public int hashCode() {
      return this.wrappers.hashCode();
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof AttributeWrappers)) {
         return false;
      } else {
         AttributeWrappers attributeWrappers = (AttributeWrappers)other;
         return Intrinsics.areEqual(this.wrappers, attributeWrappers.wrappers);
      }
   }

   public AttributeWrappers() {
      this(((java.util.List)new java.util.ArrayList()));
   }
}
