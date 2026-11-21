package io.lumine.mythic.core.menus.items;

import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import io.lumine.mythic.core.items.MythicItem;
import java.util.function.Predicate;
import lombok.Getter;

@Getter
public abstract class ItemEditorButton implements Comparable, MenuData<ItemEditorMenuContext> {
   private final int sortIndex;

   public ItemEditorButton(ItemEditorButtons parent, int sort) {
      this.sortIndex = sort;
      parent.buttons.add(this);
   }

   @Override
   public abstract Icon<ItemEditorMenuContext> getIcon();

   public boolean isApplicable(MythicItem mythicItem) {
      return true;
   }

   public boolean isApplicable(MythicItem mythicItem, Predicate<MythicItem> predicate) {
      return predicate.test(mythicItem);
   }

   @Override
   public int compareTo(Object o) {
      return o instanceof ItemEditorButton button ? button.getSortIndex() - this.sortIndex : 0;
   }
}
