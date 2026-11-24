package me.TechsCode.UltraPermissions.gui.settings.formatPane;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.legacy.Tools;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.views.settings.SettingsPane;
import me.TechsCode.UltraPermissions.base.views.settings.SettingsView;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.visual.VisualType;
import org.bukkit.entity.Player;

public class FormatPane extends SettingsPane {
   private UltraPermissions plugin;
   private static final Phrase NAME = Phrase.create("formatPane.name", "Chat & Tab List Format");
   private static final Phrase ENTRY_CHANGE_ACTION = Phrase.create("formatPane.entry.changeAction", "**Left Click** to change format", Colors.GRAY, Colors.AQUA);
   private static final Phrase ENTRY_DISABLE_ACTION = Phrase.create(
      "formatPane.entry.disableAction", "**Right Click** to **disable**", Colors.GRAY, Colors.AQUA, Colors.RED
   );
   private static final Phrase ENTRY_DEFAULT_ACTION = Phrase.create(
      "formatPane.entry.defaultAction", "**Press Q** to reset format to **default**", Colors.GRAY, Colors.AQUA, Colors.GREEN
   );
   private static final Phrase ENTRY_FORMAT_TITLE = Phrase.create("formatPane.entry.format.title", "Format:", Colors.GREEN);
   private static final Phrase ENTRY_FORMAT_DISABLED = Phrase.create("formatPane.entry.format.disabled", "Feature disabled", Colors.RED);

   public FormatPane(Player player, SettingsView settingsView, UltraPermissions ultraPermissions) {
      super(player, settingsView);
      this.plugin = ultraPermissions;
   }

   @Override
   public String getName() {
      return NAME.get();
   }

   @Override
   public XMaterial getIcon() {
      return XMaterial.NAME_TAG;
   }

   @Override
   public void construct(Model model) {
      for (VisualType visualType : VisualType.values()) {
         model.button(visualType.getGuiSlot() + 9, button -> this.FormatButton(button, visualType));
      }
   }

   private void FormatButton(Button button, VisualType visualType) {
      button.material(visualType.getMaterial())
         .name(Animation.wave(Tools.getEnumName(visualType), Colors.GOLD, Colors.WHITE))
         .lore(ENTRY_CHANGE_ACTION.get(), ENTRY_DISABLE_ACTION.get());
      String text = this.plugin.getVisualRegistry().get().getFormat(visualType);
      if (!visualType.getDefaultFormat().equals("none") && (text == null || !text.equals(visualType.getDefaultFormat()))) {
         button.item().appendLore(ENTRY_DEFAULT_ACTION.get());
      }

      button.item().appendLore("", ENTRY_FORMAT_TITLE.get(), text == null ? ENTRY_FORMAT_DISABLED.get() : Colors.WHITE + text);
      button.action(actionType -> {
         if (actionType == ActionType.Q) {
            this.plugin.getVisualRegistry().get().resetFormat(visualType);
         } else if (actionType == ActionType.RIGHT) {
            this.plugin.getVisualRegistry().get().setFormat(visualType, "none");
         } else {
            new FormatDialog(this.p, this.plugin, visualType) {
               @Override
               public void reopen() {
                  FormatPane.this.reopen();
               }
            };
         }
      });
   }
}
