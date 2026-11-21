package io.lumine.mythic.core.menus.spawners;

import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import java.util.function.Predicate;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class SpawnerEditorButton implements Comparable, MenuData<SpawnerEditorMenuContext> {
   private final int sortIndex;

   public SpawnerEditorButton(SpawnerEditorButtons parent, int sort) {
      this.sortIndex = sort;
      parent.buttons.add(this);
   }

   @Override
   public abstract Icon<SpawnerEditorMenuContext> getIcon();

   public abstract String getLabel();

   public boolean isApplicable(ItemStack itemStack) {
      return true;
   }

   public boolean isApplicable(ItemStack itemStack, Predicate<ItemStack> predicate) {
      return predicate.test(itemStack);
   }

   @Override
   public int compareTo(Object o) {
      return o instanceof SpawnerEditorButton button ? button.getSortIndex() - this.sortIndex : 0;
   }
}
