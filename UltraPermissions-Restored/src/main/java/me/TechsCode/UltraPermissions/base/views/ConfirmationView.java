package me.TechsCode.UltraPermissions.base.views;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.GUI;
import me.TechsCode.UltraPermissions.base.gui.Model;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public abstract class ConfirmationView extends GUI {
   private static final Phrase CONFIRM_TITLE = Phrase.create("confirmationView.confirm.title", "Confirm");
   private static final Phrase CONFIRM_ACTION = Phrase.create("confirmationView.confirm.action", "Click to confirm", Colors.GRAY);
   private static final Phrase DENY_TITLE = Phrase.create("confirmationView.deny.title", "Abort");
   private static final Phrase DENY_ACTION = Phrase.create("confirmationView.deny.action", "Click to abort", Colors.GRAY);
   private String title;

   public ConfirmationView(Player player, SpigotTechPlugin spigotTechPlugin, String text) {
      super(player, spigotTechPlugin);
      this.title = text;
   }

   @Override
   protected void construct(Model model) {
      model.setTitle(this.title);
      model.setSlots(27);
      model.button(12, this::ConfirmButton);
      model.button(16, this::DenyButton);
   }

   private void ConfirmButton(Button button) {
      button.material(XMaterial.EMERALD_BLOCK).name(Animation.wave(CONFIRM_TITLE.get(), Colors.GREEN, Colors.WHITE)).lore(CONFIRM_ACTION.get());
      button.action(actionType -> this.choose(true));
   }

   private void DenyButton(Button button) {
      button.material(XMaterial.REDSTONE_BLOCK).name(Animation.wave(DENY_TITLE.get(), Colors.RED, Colors.WHITE)).lore(DENY_ACTION.get());
      button.action(actionType -> this.choose(false));
   }

   public abstract void choose(boolean flag);
}
