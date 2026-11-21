package io.lumine.mythic.core.menus.mobs;

import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import java.util.function.Predicate;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class MobEditorButton implements Comparable, MenuData<MobEditorMenuContext> {
   private final int sortIndex;

   public MobEditorButton(MobEditorButtons parent, int sort) {
      this.sortIndex = sort;
      parent.buttons.add(this);
   }

   @Override
   public abstract Icon<MobEditorMenuContext> getIcon();

   public abstract String getLabel();

   public boolean isApplicable(ItemStack itemStack) {
      return true;
   }

   public boolean isApplicable(ItemStack itemStack, Predicate<ItemStack> predicate) {
      return predicate.test(itemStack);
   }

   @Override
   public int compareTo(Object o) {
      return o instanceof MobEditorButton button ? button.getSortIndex() - this.sortIndex : 0;
   }
}
