package io.lumine.mythic.bukkit.utils.menu;

import java.util.Collection;
import org.bukkit.configuration.ConfigurationSection;

public class EditableIcon<T> {
   private IconBuilder<T> builder;
   private final Collection<Integer> slots;
   private final ConfigurationSection configurationSection;

   public IconBuilder<T> getBuilder() {
      return this.builder;
   }

   public Collection<Integer> getSlots() {
      return this.slots;
   }

   public ConfigurationSection getConfigurationSection() {
      return this.configurationSection;
   }

   public void setBuilder(IconBuilder<T> builder) {
      this.builder = builder;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof EditableIcon<?> other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$builder = this.getBuilder();
         Object other$builder = other.getBuilder();
         if (this$builder == null ? other$builder == null : this$builder.equals(other$builder)) {
            Object this$slots = this.getSlots();
            Object other$slots = other.getSlots();
            if (this$slots == null ? other$slots == null : this$slots.equals(other$slots)) {
               Object this$configurationSection = this.getConfigurationSection();
               Object other$configurationSection = other.getConfigurationSection();
               return this$configurationSection == null ? other$configurationSection == null : this$configurationSection.equals(other$configurationSection);
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof EditableIcon;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $builder = this.getBuilder();
      result = result * 59 + ($builder == null ? 43 : $builder.hashCode());
      Object $slots = this.getSlots();
      result = result * 59 + ($slots == null ? 43 : $slots.hashCode());
      Object $configurationSection = this.getConfigurationSection();
      return result * 59 + ($configurationSection == null ? 43 : $configurationSection.hashCode());
   }

   @Override
   public String toString() {
      return "EditableIcon(builder=" + this.getBuilder() + ", slots=" + this.getSlots() + ", configurationSection=" + this.getConfigurationSection() + ")";
   }

   public EditableIcon(IconBuilder<T> builder, Collection<Integer> slots, ConfigurationSection configurationSection) {
      this.builder = builder;
      this.slots = slots;
      this.configurationSection = configurationSection;
   }
}
