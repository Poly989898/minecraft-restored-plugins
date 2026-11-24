package me.TechsCode.UltraPermissions.base.update.view;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class UpdateView extends GUI {
   private static final Phrase TITLE = Phrase.create("updateView.title", "Update from %from% to %to%");
   private static final Phrase YES_TITLE = Phrase.create("updateView.yes.title", "Update");
   private static final Phrase YES_ACTION = Phrase.create("updateView.yes.action", "Click to load **update**", Colors.GRAY, Colors.YELLOW);
   private static final Phrase YES_DESCRIPTION = Phrase.create(
      "updateView.yes.desc", "This feature requires **Discord** to verify your purchase", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase NO_TITLE = Phrase.create("updateView.no.title", "Stay on this Version");
   private static final Phrase NO_ACTION = Phrase.create("updateView.no.action", "Click to stay on this Version", Colors.GRAY);
   private static final Phrase NO_DESC_YOURVERSION = Phrase.create("updateView.no.desc.yourVersion", "Your Version: **%**", Colors.GRAY, Colors.RED);
   private static final Phrase NO_DESC_NEWVERSION = Phrase.create("updateView.no.desc.newVersion", "New Version: **%**", Colors.GRAY, Colors.GREEN);
   private final String localVersion;
   private final String globalVersion;

   public UpdateView(Player player, SpigotTechPlugin plugin, String localVersion, String globalVersion) {
      super(player, plugin);
      this.localVersion = localVersion;
      this.globalVersion = globalVersion;
   }

   public abstract void onResponse(boolean flag);

   @Override
   protected void construct(Model model) {
      model.button(12, this::YesButton);
      model.button(16, this::NoButton);
   }

   @Override
   public int getCurrentSlots() {
      return 27;
   }

   @Override
   public String getCurrentTitle() {
      return TITLE.get().replace("%from%", this.localVersion).replace("%to%", this.globalVersion);
   }

   private void YesButton(Button button) {
      button.material(XMaterial.EMERALD_BLOCK).name(Animation.wave(YES_TITLE.get(), Colors.GREEN, Colors.WHITE)).lore(YES_ACTION.get(), "");
      button.item().appendLore(YES_DESCRIPTION.split(25));
      button.action(actionType -> {
         this.p.closeInventory();
         this.onResponse(true);
      });
   }

   private void NoButton(Button button) {
      button.material(XMaterial.REDSTONE_BLOCK)
         .name(Animation.wave(NO_TITLE.get(), Colors.RED, Colors.WHITE))
         .lore(NO_ACTION.get(), "", NO_DESC_YOURVERSION.get().replace("%", this.localVersion), NO_DESC_NEWVERSION.get().replace("%", this.globalVersion));
      button.action(actionType -> {
         this.p.closeInventory();
         this.onResponse(false);
      });
   }
}
