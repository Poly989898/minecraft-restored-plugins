package me.TechsCode.UltraPermissions.gui;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import me.TechsCode.UltraPermissions.migration.MigrationAssistant;
import org.bukkit.entity.Player;

public class MigrationView extends GUI {
   private final UltraPermissions plugin;
   private final MigrationAssistant assistant;
   private static final Phrase TITLE = Phrase.create("migrationView.title", "Import > %plugin%");
   private static final Phrase START_TITLE = Phrase.create("migrationView.start.title", "Import Data");
   private static final Phrase START_ACTION = Phrase.create("migrationView.start.action", "Click to **convert** data", Colors.GRAY, Colors.LIGHT_PURPLE);
   private static final Phrase START_EXPLANATION = Phrase.create("migrationView.start.explanation", "Your existing data will be overwritten.", Colors.GRAY);
   private static final Phrase START_DESC = Phrase.create("migrationView.start.desc", "If you dont want to convert, delete %plugin%", Colors.GRAY);

   public MigrationView(Player player, UltraPermissions ultraPermissions, MigrationAssistant migrationAssistant) {
      super(player, ultraPermissions);
      this.plugin = ultraPermissions;
      this.assistant = migrationAssistant;
   }

   @Override
   protected void construct(Model model) {
      model.setTitle(TITLE.get().replace("%plugin%", this.assistant.getPluginName()));
      model.setSlots(45);
      model.button(
         23,
         button -> {
            button.material(XMaterial.END_PORTAL_FRAME)
               .name(Animation.wave(START_TITLE.get(), Colors.LightPink, Colors.WHITE))
               .lore(START_ACTION.get(), "", START_EXPLANATION.get(), START_DESC.get().replace("%plugin%", this.assistant.getPluginName()));
            button.action(actionType -> this.assistant.doMigration(this.plugin));
         }
      );
   }
}
