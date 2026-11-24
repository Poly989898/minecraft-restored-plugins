package me.TechsCode.UltraPermissions.base.views.settings;

import java.util.Iterator;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.debug.DebugRegistery;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.translations.Words;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public class DebuggerPane extends SettingsPane {
   private static final Phrase LEFT_CLICK = Phrase.create(
      "debugger.select.indicator", "**Left Click** to %action% this debug message", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase NEXT_TITLE = Phrase.create("debugger.next.title", "Next");
   private static final Phrase NEXT_ACTION = Phrase.create("debugger.next.action", "Click to go to the next page", Colors.GRAY);
   private static final Phrase PREVIOUS_TITLE = Phrase.create("debugger.previous.title", "Previous");
   private static final Phrase PREVIOUS_ACTION = Phrase.create("debugger.previous.action", "Click to go to the previous page", Colors.GRAY);
   private final SpigotTechPlugin plugin;
   private final int maxObjectsPerPage = 36;
   private int page = 0;
   private int pages;

   public DebuggerPane(Player player, SettingsView settingsView, SpigotTechPlugin spigotTechPlugin) {
      super(player, settingsView);
      this.plugin = spigotTechPlugin;
   }

   @Override
   public String getName() {
      return "Debugger";
   }

   @Override
   public XMaterial getIcon() {
      return XMaterial.REPEATING_COMMAND_BLOCK;
   }

   @Override
   public void construct(Model model) {
      int[] bigInnerContainerSlots = this.getBigInnerContainerSlots();
      DebugRegistery debugRegistery = this.plugin.getDebugRegistery();
      int count = Math.min(debugRegistery.getAvailableDebuggers().size(), this.page * 36);
      int end = Math.min(debugRegistery.getAvailableDebuggers().size(), count + 36);
      Iterator iterator = debugRegistery.getAvailableDebuggers().subList(count, end).iterator();

      for (int slot : bigInnerContainerSlots) {
         if (iterator.hasNext()) {
            String text = (String)iterator.next();
            boolean isDebugging = debugRegistery.isDebugging(text);
            model.button(slot, button -> this.DebugButton(button, text, isDebugging));
         } else {
            model.button(slot, button -> button.material(XMaterial.WHITE_STAINED_GLASS_PANE).name());
         }
      }

      this.pages = (int)Math.round(Math.ceil(debugRegistery.getAvailableDebuggers().size() / 36.0));
      if (this.page != 0) {
         model.button(button -> {
            button.material(XMaterial.ARROW).name(Animation.fading(PREVIOUS_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(PREVIOUS_ACTION.get());
            button.action(actionType -> {
               this.page--;
               if (this.page < 0) {
                  this.page = 0;
               }
            });
         }, 46);
      }

      if (this.page + 1 < this.pages) {
         model.button(button -> {
            button.material(XMaterial.ARROW).name(Animation.fading(NEXT_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(NEXT_ACTION.get());
            button.action(actionType -> this.page++);
         }, 54);
      }
   }

   private void DebugButton(Button button, String text, boolean flag) {
      button.material(XMaterial.PAPER)
         .name(Animation.wave(text, Colors.GOLD, Colors.YELLOW))
         .lore(LEFT_CLICK.get().replace("%action%", flag ? Colors.RED + Words.DISABLE.get() + Colors.GRAY : Colors.GREEN + Words.ENABLE.get() + Colors.GRAY));
      if (flag) {
         button.item().material(XMaterial.LIME_STAINED_GLASS_PANE.getBukkitMaterial().get());
      } else {
         button.item().material(XMaterial.RED_STAINED_GLASS_PANE.getBukkitMaterial().get());
      }

      button.action(actionType -> {
         if (!flag) {
            this.plugin.getDebugRegistery().addDebugger(text);
         } else {
            this.plugin.getDebugRegistery().removeDebugger(text);
         }
      });
   }
}
