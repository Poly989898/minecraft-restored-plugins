package io.lumine.mythic.bukkit.utils.menu;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;

public class EditableMenuBuilder<T> {
   private final ConfigurationSection configurationSection;
   private final MenuBuilder<T> builder;
   private final Map<String, EditableIcon<T>> icons;
   private final List<Integer> dataIcons;
   private final List<Integer> secondaryDataIcons;
   private final List<Integer> freeIcons;

   public Optional<EditableIcon<T>> getIcon(String key) {
      return Optional.ofNullable(this.icons.getOrDefault(key, null));
   }

   public void defaultClick(BiFunction<MenuHelper.OpenMenu<T>, InventoryClickEvent, Boolean> freeSlotFunc) {
      this.builder.defaultClick(freeSlotFunc);
   }

   public Menu<T> build() {
      this.icons.values().forEach(eIcon -> {
         if (eIcon.getSlots() != null) {
            eIcon.getSlots().forEach(slot -> this.builder.addIcon(slot, eIcon.getBuilder().build()));
         }
      });
      this.builder.dataSlots(this.dataIcons);
      this.builder.secondaryDataSlots(this.secondaryDataIcons);
      this.builder.unprotectedSlots(this.freeIcons);
      Menu<T> menu = this.builder.build();
      menu.setBuilder(this);
      return menu;
   }

   public void overwriteIcon(String key, EditableIcon<T> icon) {
      this.icons.put(key, icon);
   }

   public EditableMenuBuilder(
      ConfigurationSection configurationSection,
      MenuBuilder<T> builder,
      Map<String, EditableIcon<T>> icons,
      List<Integer> dataIcons,
      List<Integer> secondaryDataIcons,
      List<Integer> freeIcons
   ) {
      this.configurationSection = configurationSection;
      this.builder = builder;
      this.icons = icons;
      this.dataIcons = dataIcons;
      this.secondaryDataIcons = secondaryDataIcons;
      this.freeIcons = freeIcons;
   }

   public ConfigurationSection getConfigurationSection() {
      return this.configurationSection;
   }

   public MenuBuilder<T> getBuilder() {
      return this.builder;
   }

   public Map<String, EditableIcon<T>> getIcons() {
      return this.icons;
   }

   public List<Integer> getDataIcons() {
      return this.dataIcons;
   }

   public List<Integer> getSecondaryDataIcons() {
      return this.secondaryDataIcons;
   }

   public List<Integer> getFreeIcons() {
      return this.freeIcons;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof EditableMenuBuilder<?> other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$configurationSection = this.getConfigurationSection();
         Object other$configurationSection = other.getConfigurationSection();
         if (this$configurationSection == null ? other$configurationSection == null : this$configurationSection.equals(other$configurationSection)) {
            Object this$builder = this.getBuilder();
            Object other$builder = other.getBuilder();
            if (this$builder == null ? other$builder == null : this$builder.equals(other$builder)) {
               Object this$icons = this.getIcons();
               Object other$icons = other.getIcons();
               if (this$icons == null ? other$icons == null : this$icons.equals(other$icons)) {
                  Object this$dataIcons = this.getDataIcons();
                  Object other$dataIcons = other.getDataIcons();
                  if (this$dataIcons == null ? other$dataIcons == null : this$dataIcons.equals(other$dataIcons)) {
                     Object this$secondaryDataIcons = this.getSecondaryDataIcons();
                     Object other$secondaryDataIcons = other.getSecondaryDataIcons();
                     if (this$secondaryDataIcons == null ? other$secondaryDataIcons == null : this$secondaryDataIcons.equals(other$secondaryDataIcons)) {
                        Object this$freeIcons = this.getFreeIcons();
                        Object other$freeIcons = other.getFreeIcons();
                        return this$freeIcons == null ? other$freeIcons == null : this$freeIcons.equals(other$freeIcons);
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof EditableMenuBuilder;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $configurationSection = this.getConfigurationSection();
      result = result * 59 + ($configurationSection == null ? 43 : $configurationSection.hashCode());
      Object $builder = this.getBuilder();
      result = result * 59 + ($builder == null ? 43 : $builder.hashCode());
      Object $icons = this.getIcons();
      result = result * 59 + ($icons == null ? 43 : $icons.hashCode());
      Object $dataIcons = this.getDataIcons();
      result = result * 59 + ($dataIcons == null ? 43 : $dataIcons.hashCode());
      Object $secondaryDataIcons = this.getSecondaryDataIcons();
      result = result * 59 + ($secondaryDataIcons == null ? 43 : $secondaryDataIcons.hashCode());
      Object $freeIcons = this.getFreeIcons();
      return result * 59 + ($freeIcons == null ? 43 : $freeIcons.hashCode());
   }

   @Override
   public String toString() {
      return "EditableMenuBuilder(configurationSection="
         + this.getConfigurationSection()
         + ", builder="
         + this.getBuilder()
         + ", icons="
         + this.getIcons()
         + ", dataIcons="
         + this.getDataIcons()
         + ", secondaryDataIcons="
         + this.getSecondaryDataIcons()
         + ", freeIcons="
         + this.getFreeIcons()
         + ")";
   }
}
