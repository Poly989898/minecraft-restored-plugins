package io.lumine.mythic.bukkit.utils.menu.hotbar;

import io.lumine.mythic.bukkit.utils.menu.EditableIcon;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EditableHotbarMenuBuilder<T> {
   private final HotbarMenuBuilder<T> builder;
   private final Map<String, EditableIcon<T>> icons;
   private final List<Integer> freeIcons;

   public Optional<EditableIcon<T>> getIcon(String key) {
      return Optional.ofNullable(this.icons.getOrDefault(key, null));
   }

   public HotbarMenu<T> build() {
      this.icons.values().forEach(eIcon -> eIcon.getSlots().forEach(slot -> this.builder.addIcon(slot, eIcon.getBuilder().build())));
      this.builder.unprotectedSlots(this.freeIcons);
      return this.builder.build();
   }

   public EditableHotbarMenuBuilder(HotbarMenuBuilder<T> builder, Map<String, EditableIcon<T>> icons, List<Integer> freeIcons) {
      this.builder = builder;
      this.icons = icons;
      this.freeIcons = freeIcons;
   }

   public HotbarMenuBuilder<T> getBuilder() {
      return this.builder;
   }

   public Map<String, EditableIcon<T>> getIcons() {
      return this.icons;
   }

   public List<Integer> getFreeIcons() {
      return this.freeIcons;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof EditableHotbarMenuBuilder<?> other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$builder = this.getBuilder();
         Object other$builder = other.getBuilder();
         if (this$builder == null ? other$builder == null : this$builder.equals(other$builder)) {
            Object this$icons = this.getIcons();
            Object other$icons = other.getIcons();
            if (this$icons == null ? other$icons == null : this$icons.equals(other$icons)) {
               Object this$freeIcons = this.getFreeIcons();
               Object other$freeIcons = other.getFreeIcons();
               return this$freeIcons == null ? other$freeIcons == null : this$freeIcons.equals(other$freeIcons);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof EditableHotbarMenuBuilder;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $builder = this.getBuilder();
      result = result * 59 + ($builder == null ? 43 : $builder.hashCode());
      Object $icons = this.getIcons();
      result = result * 59 + ($icons == null ? 43 : $icons.hashCode());
      Object $freeIcons = this.getFreeIcons();
      return result * 59 + ($freeIcons == null ? 43 : $freeIcons.hashCode());
   }

   @Override
   public String toString() {
      return "EditableHotbarMenuBuilder(builder=" + this.getBuilder() + ", icons=" + this.getIcons() + ", freeIcons=" + this.getFreeIcons() + ")";
   }
}
