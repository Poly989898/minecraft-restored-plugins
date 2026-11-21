package io.lumine.mythic.bukkit.utils.items.nbt.jnbt;

public abstract class DynamicTag<T> extends Tag {
   private final T value;

   public DynamicTag(T value) {
      this.value = value;
   }

   @Override
   public T getValue() {
      return this.value;
   }

   public abstract Tag build();

   @Override
   public String toString() {
      return "TAG_Dynamic(" + this.value + ")";
   }
}
