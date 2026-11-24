package me.TechsCode.UltraPermissions.gui.settings.formatPane;

import java.util.Arrays;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.dialog.UserInput;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.visual.VisualType;
import org.bukkit.entity.Player;

public abstract class FormatDialog extends UserInput {
   private static final Phrase TITLE = Phrase.create("formatDialog.title", "Set Format", Colors.AQUA);
   private static final Phrase SUBTITLE = Phrase.create("formatDialog.subtitle", "Type in a new format", Colors.GRAY);
   private static final Phrase ACTION_BAR = Phrase.create("formatDialog.actionBar", "Use the placeholders in the chat", Colors.GRAY);
   private static final Phrase PLACEHOLDERS_TITLE = Phrase.create("formatDialog.placeholders.title", "Placeholders:", Colors.GREEN);
   private static final Phrase PLACEHOLDERS_ENTRY = Phrase.create(
      "formatDialog.placeholders.entry", "**>** %placeholder% **-** %description%", Colors.GRAY, Colors.BLUE, Colors.YELLOW
   );
   private static final Phrase PLACEHOLDERS_OTHER = Phrase.create(
      "formatDialog.placeholders.other", "and **{Player}** or **{DisplayName}**", Colors.GRAY, Colors.BLUE, Colors.BLUE
   );
   private static final Phrase DEFAULT = Phrase.create("formatDialog.default", "Default: **%format%**", Colors.GOLD, Colors.WHITE);
   private final UltraPermissions plugin;
   private final VisualType type;

   public FormatDialog(Player player, UltraPermissions ultraPermissions, VisualType visualType) {
      super(player, ultraPermissions, TITLE, SUBTITLE, ACTION_BAR);
      this.plugin = ultraPermissions;
      this.type = visualType;
      player.sendMessage("");
      player.sendMessage(PLACEHOLDERS_TITLE.get());
      Arrays.stream(UltraPermissions.placeholders).forEach(upermsPlaceholder -> {
         String text = PLACEHOLDERS_ENTRY.get().replace("%placeholder%", upermsPlaceholder.getNativePlaceholder()).replace("%description%", upermsPlaceholder.getDescription());
         player.sendMessage(text);
      });
      player.sendMessage(PLACEHOLDERS_OTHER.get());
      player.sendMessage("");
      player.sendMessage(DEFAULT.get().replace("%format%", visualType.getDefaultFormat()));
   }

   @Override
   public boolean onResult(String text) {
      this.plugin.getVisualRegistry().get().setFormat(this.type, text);
      this.reopen();
      return true;
   }

   @Override
   public void onClose(Player player) {
      this.reopen();
   }

   public abstract void reopen();
}
