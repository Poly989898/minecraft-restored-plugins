package me.TechsCode.UltraPermissions.base.views.settings;

import me.TechsCode.UltraPermissions.base.AppearanceRegistry;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.translations.Words;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.base.visual.Text;
import org.bukkit.entity.Player;

public class CustomisationPane extends SettingsPane {
   private static final Phrase TITLE = Phrase.create("customisationPane.title", "Customisation");
   private final SpigotTechPlugin plugin;
   private static final Phrase PREFIX_TITLE = Phrase.create("customisationPane.prefix.title", "Prefix");
   private static final Phrase PREFIX_RESET_ACTION = Phrase.create(
      "customisationPane.prefix.resetAction", "**Left Click** to **reset** the prefix", Colors.GRAY, Colors.AQUA, Colors.GOLD
   );
   private static final Phrase PREFIX_CHANGE_ACTION = Phrase.create(
      "customisationPane.prefix.changeAction", "**Left Click** to **change** the prefix", Colors.GRAY, Colors.AQUA, Colors.GREEN
   );
   private static final Phrase PREFIX_DISABLE_ACTION = Phrase.create(
      "customisationPane.prefix.disableAction", "**Press Q** to **disable** the prefix", Colors.GRAY, Colors.AQUA, Colors.RED
   );
   private static final Phrase PREFIX_ENABLE_ACTION = Phrase.create(
      "customisationPane.prefix.enableAction", "Click to **enable** the prefix", Colors.GRAY, Colors.GREEN
   );
   private static final Phrase PREFIX_ATTRIBUTE = Phrase.create("customisationPane.prefix.attribute", "Prefix: **%prefix%**", Colors.GRAY, Colors.WHITE);
   private final Phrase UPDATER_TITLE = Phrase.create("customisationPane.updater.title", "Ingame Updater");
   private final Phrase UPDATER_ACTION = Phrase.create("customisationPane.updater.action", "**Click** to %action% ingame Updater", Colors.GRAY, Colors.AQUA);
   private final Phrase UPDATER_DESC1 = Phrase.create("customisationPane.updater.desc1", "Suggests to update the plugin if the plugin is outdated", Colors.GRAY);
   private final Phrase UPDATER_DESC2 = Phrase.create("customisationPane.updater.desc2", "and will automatically install it after confirming", Colors.GRAY);
   private static final Phrase GUIINTERVANL_TITLE = Phrase.create("customisationPane.guiUpdateInterval.title", "GUI Update Interval");
   private static final Phrase GUIINTERVANL_RESET_ACTION = Phrase.create(
      "customisationPane.guiUpdateInterval.resetAction", "**Left Click** to **reset** the GUI update interval", Colors.GRAY, Colors.AQUA, Colors.GOLD
   );
   private static final Phrase GUIINTERVANL_CHANGE_ACTION = Phrase.create(
      "customisationPane.guiUpdateInterval.changeAction", "**Left Click** to **change** the GUI update interval", Colors.GRAY, Colors.AQUA, Colors.GREEN
   );
   private static final Phrase GUIINTERVANL_ATTRIBUTE = Phrase.create(
      "customisationPane.guiUpdateInterval.attribute", "Interval: **%interval%** ticks", Colors.GRAY, Colors.WHITE
   );

   public CustomisationPane(Player player, SettingsView settingsView, SpigotTechPlugin spigotTechPlugin) {
      super(player, settingsView);
      this.plugin = spigotTechPlugin;
   }

   @Override
   public String getName() {
      return TITLE.get();
   }

   @Override
   public XMaterial getIcon() {
      return XMaterial.CRAFTING_TABLE;
   }

   @Override
   public void construct(Model model) {
      model.button(21, this::PrefixButton);
      model.button(23, this::UpdaterButton);
      model.button(25, this::GuiUpdateIntervalButton);
   }

   private void PrefixButton(Button button) {
      AppearanceRegistry appearanceRegistry = this.plugin.getAppearanceRegistry();
      button.material(XMaterial.NAME_TAG)
         .name(Animation.wave(PREFIX_TITLE.get(), Colors.Gold, Colors.YELLOW))
         .lore((appearanceRegistry.isPrefixModified() ? PREFIX_RESET_ACTION : PREFIX_CHANGE_ACTION).get());
      if (appearanceRegistry.isPrefixModified()) {
         button.item().lore(PREFIX_RESET_ACTION.get(), PREFIX_DISABLE_ACTION.get());
      } else if (appearanceRegistry.isPrefixDisabled()) {
         button.item().lore(PREFIX_ENABLE_ACTION.get());
      } else {
         button.item().lore(PREFIX_CHANGE_ACTION.get(), PREFIX_DISABLE_ACTION.get());
      }

      button.item().appendLore("", PREFIX_ATTRIBUTE.get().replace("%prefix%", appearanceRegistry.isPrefixDisabled() ? Colors.RED + Words.DISABLED.get() : appearanceRegistry.getPrefix()));
      button.action(actionType -> {
         if (appearanceRegistry.isPrefixDisabled()) {
            appearanceRegistry.enablePrefix();
         } else if (actionType == ActionType.Q) {
            appearanceRegistry.disablePrefix();
         } else {
            if (appearanceRegistry.isPrefixModified()) {
               appearanceRegistry.resetPrefix();
            } else {
               new CustomisationPane.PrefixSetDialog(this.p, this.plugin) {
                  @Override
                  void setPrefix(String text) {
                     appearanceRegistry.setPrefix(text);
                  }

                  @Override
                  void reopen() {
                     CustomisationPane.this.reopen();
                  }
               };
            }
         }
      });
   }

   private void UpdaterButton(Button button) {
      AppearanceRegistry appearanceRegistry = this.plugin.getAppearanceRegistry();
      button.material(XMaterial.WRITABLE_BOOK)
         .name(Animation.wave(this.UPDATER_TITLE.get(), Colors.Gold, Colors.YELLOW))
         .lore(
            this.UPDATER_ACTION
               .get()
               .replace("%action%", appearanceRegistry.isUpdaterEnabled() ? "\u00a7c" + Words.DISABLE.get() + "\u00a77" : "\u00a7a" + Words.ENABLE.get() + "\u00a77"),
            "",
            this.UPDATER_DESC1.get(),
            this.UPDATER_DESC2.get()
         );
      button.action(actionType -> appearanceRegistry.toggleUpdater());
   }

   private void GuiUpdateIntervalButton(Button button) {
      AppearanceRegistry appearanceRegistry = this.plugin.getAppearanceRegistry();
      button.material(XMaterial.CLOCK)
         .name(Animation.wave(GUIINTERVANL_TITLE.get(), Colors.Gold, Colors.YELLOW))
         .lore((appearanceRegistry.isPrefixModified() ? GUIINTERVANL_RESET_ACTION : GUIINTERVANL_CHANGE_ACTION).get());
      if (appearanceRegistry.isGuiUpdateIntervalModified()) {
         button.item().lore(GUIINTERVANL_RESET_ACTION.get());
      } else {
         button.item().lore(GUIINTERVANL_CHANGE_ACTION.get());
      }

      button.item().appendLore("", GUIINTERVANL_ATTRIBUTE.get().replace("%interval%", String.valueOf(appearanceRegistry.getGuiUpdateInterval())));
      button.action(actionType -> {
         if (appearanceRegistry.isGuiUpdateIntervalModified()) {
            appearanceRegistry.resetGuiUpdateInterval();
         } else {
            new CustomisationPane.GuiUpdateIntervalSetDialog(this.p, this.plugin) {
               @Override
               void setInterval(int count) {
                  appearanceRegistry.setGuiUpdateInterval(count);
               }

               @Override
               void reopen() {
                  CustomisationPane.this.reopen();
               }
            };
         }
      });
   }

   abstract static class GuiUpdateIntervalSetDialog extends UserInput {
      private static final Phrase TITLE = Phrase.create("customisationPane.guiUpdateInterval.change.title", "GUI Update Interval", Colors.AQUA);
      private static final Phrase SUBTITLE = Phrase.create(
         "customisationPane.guiUpdateInterval.change.subtitle", "Type in a interval (ticks) Min: 2 Max: 20", Colors.GRAY
      );

      public GuiUpdateIntervalSetDialog(Player dialogPlayer, SpigotTechPlugin dialogPlugin) {
         super(dialogPlayer, dialogPlugin, TITLE, SUBTITLE);
      }

      @Override
      public boolean onResult(String text) {
         int count = (int)Math.round(Double.parseDouble(text));
         if (count < 2) {
            count = 2;
         }

         if (count > 20) {
            count = 20;
         }

         this.setInterval(count);
         this.reopen();
         return true;
      }

      @Override
      public void onClose(Player closingPlayer) {
         this.reopen();
      }

      abstract void setInterval(int count);

      abstract void reopen();
   }

   abstract static class PrefixSetDialog extends UserInput {
      private static final Phrase TITLE = Phrase.create("customisationPane.prefix.change.title", "Prefix", Colors.AQUA);
      private static final Phrase SUBTITLE = Phrase.create("customisationPane.prefix.change.subtitle", "Type in a prefix", Colors.GRAY);

      public PrefixSetDialog(Player dialogPlayer, SpigotTechPlugin dialogPlugin) {
         super(dialogPlayer, dialogPlugin, TITLE, SUBTITLE);
      }

      @Override
      public boolean onResult(String text) {
         this.setPrefix(Text.color(text));
         this.reopen();
         return true;
      }

      @Override
      public void onClose(Player closingPlayer) {
         this.reopen();
      }

      abstract void setPrefix(String text);

      abstract void reopen();
   }
}
