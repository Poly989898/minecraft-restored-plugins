package io.lumine.mythic.bukkit.utils.menu;

import com.google.common.collect.Lists;
import io.lumine.mythic.bukkit.utils.config.properties.types.MenuProp;
import io.lumine.mythic.bukkit.utils.logging.Log;
import java.util.Collections;
import java.util.List;
import org.bukkit.entity.Player;

public abstract class ReloadableMenu<T> {
   private final MenuProp<T> menuProperty;
   private EditableMenuBuilder<T> builder;
   private Menu<T> menu;
   private boolean buildOnOpen;

   public ReloadableMenu(MenuProp<T> menu) {
      this(menu, false);
   }

   public ReloadableMenu(MenuProp<T> menu, boolean buildOnOpen) {
      this.menuProperty = menu;
      this.buildOnOpen = buildOnOpen;
   }

   public void reload() {
      this.builder = this.menuProperty.get();
      if (this.builder == null) {
         Log.error("MenuProp returned null in ReloadableMenu");
         Thread.dumpStack();
      } else {
         EditableMenuBuilder<T> build = this.build(this.builder);
         if (build == null) {
            Log.error("ReloadableMenu builder returned null");
            Thread.dumpStack();
         } else {
            this.menu = build.build();
            this.menu.setHolder(this);
         }
      }
   }

   public abstract EditableMenuBuilder<T> build(EditableMenuBuilder<T> editableMenuBuilder);

   public void open(Player player, T state) {
      this.open(player, state, null, Lists.newArrayList(), Collections.emptyList());
   }

   public void open(Player player, T state, String title) {
      this.open(player, state, title, Lists.newArrayList(), Collections.emptyList());
   }

   public void open(Player player, T state, List<?> data) {
      this.open(player, state, null, data, Collections.emptyList());
   }

   public void open(Player player, T state, String title, List<?> data) {
      try {
         if (this.buildOnOpen) {
            Menu<T> m = this.build(this.menuProperty.get()).build();
            m.setHolder(this);
            m.open(player, state, title, data);
         } else {
            this.menu.open(player, state, title, data);
         }
      } catch (ArrayIndexOutOfBoundsException e) {
         Log.error("Menu is empty or size is incorrectly configured!");
      }
   }

   public void open(Player player, T state, List<?> data, List<?> secondaryData) {
      this.open(player, state, null, data, secondaryData);
   }

   public void open(Player player, T state, String title, List<?> data, List<?> secondaryData) {
      try {
         if (this.buildOnOpen) {
            Menu<T> m = this.build(this.menuProperty.get()).build();
            m.setHolder(this);
            m.open(player, state, title, data, secondaryData);
         } else {
            this.menu.open(player, state, title, data, secondaryData);
         }
      } catch (ArrayIndexOutOfBoundsException e) {
         Log.error("Menu is empty or size is incorrectly configured!");
      }
   }

   public void setBuildOnOpen() {
      this.buildOnOpen = true;
   }

   public void setBuildOnOpen(boolean bool) {
      this.buildOnOpen = bool;
   }

   public void nextPage(Player player) {
      this.menu.nextPage(player);
   }

   public void previousPage(Player player) {
      this.menu.previousPage(player);
   }

   public void nextSecondaryPage(Player player) {
      this.menu.nextSecondaryPage(player);
   }

   public void previousSecondaryPage(Player player) {
      this.menu.previousSecondaryPage(player);
   }
}
