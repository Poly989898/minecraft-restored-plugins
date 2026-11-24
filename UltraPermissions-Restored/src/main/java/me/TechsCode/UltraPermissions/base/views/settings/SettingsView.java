package me.TechsCode.UltraPermissions.base.views.settings;

import java.util.Arrays;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XEnchantment;
import me.TechsCode.UltraPermissions.base.legacy.Common;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class SettingsView extends GUI {
   private static final Phrase TITLE = Phrase.create("settingsView.title", "Settings > %");
   private static final Phrase PAGE_SELECTED = Phrase.create("settingsView.select.indicator", "This page is currently **selected**", Colors.GRAY, Colors.GREEN);
   private static final Phrase SELECT_PAGE_ACTION = Phrase.create("settingsView.select.action", "Click to show Settings", Colors.GRAY);
   private final int[][] layouts = new int[][]{
      {5}, {4, 6}, {4, 5, 6}, {3, 4, 6, 7}, {3, 4, 5, 6, 7}, {2, 3, 4, 6, 7, 8}, {2, 3, 4, 5, 6, 7, 8}, {1, 2, 3, 4, 6, 7, 8, 9}, {1, 2, 3, 4, 5, 6, 7, 8, 9}
   };
   protected SpigotTechPlugin plugin;
   private int current;
   private SettingsPane[] panes;

   public SettingsView(Player player, SpigotTechPlugin spigotTechPlugin) {
      super(player, spigotTechPlugin);
      this.plugin = spigotTechPlugin;
      this.current = 0;
      this.panes = this.getPanes();
   }

   public abstract SettingsPane[] getPanes();

   public abstract void onBack();

   private SettingsPane getCurrent() {
      return this.panes[this.current];
   }

   @Override
   public int getCurrentSlots() {
      return 54;
   }

   @Override
   protected void construct(Model model) {
      model.setTitle(TITLE.get().replace("%", this.getCurrent().getName()));
      model.setSlots(54);
      SettingsPane settingsPane = this.panes[this.current];
      int[] counts = Arrays.stream(this.layouts).filter(layout -> layout.length == this.panes.length).findFirst().get();
      int index = 0;

      for (SettingsPane pane : this.panes) {
         int count = counts[index];
         boolean flag = index == this.current;
         int paneIndex = index;
         model.button(count, button -> {
            button.material(pane.getIcon()).name(Animation.wave(pane.getName(), Colors.Gold, Colors.YELLOW));
            if (flag) {
               button.item().lore(PAGE_SELECTED.get());
               button.item().addEnchantment(XEnchantment.LUCK_OF_THE_SEA.getEnchant(), 1).showEnchantments(false);
            } else {
               button.item().lore(SELECT_PAGE_ACTION.get());
            }

            button.action(actionType -> this.current = paneIndex);
         });
         index++;
      }

      settingsPane.construct(model);
      model.button(50, button -> Common.BackButton(button, actionType -> this.onBack()));
   }
}
