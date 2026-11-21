package io.lumine.mythic.core.menus.items.editor.components;

import io.lumine.mythic.bukkit.utils.menu.Icon;
import io.lumine.mythic.bukkit.utils.menu.MenuData;
import lombok.Getter;

@Getter
public abstract class ComponentMenuButton<T> implements Comparable, MenuData<T> {
   private final int sortIndex;

   protected ComponentMenuButton(int sortIndex) {
      this.sortIndex = sortIndex;
   }

   @Override
   public abstract Icon<T> getIcon();

   @Override
   public int compareTo(Object o) {
      return o instanceof ComponentMenuButton<?> button ? button.getSortIndex() - this.sortIndex : 0;
   }
}
