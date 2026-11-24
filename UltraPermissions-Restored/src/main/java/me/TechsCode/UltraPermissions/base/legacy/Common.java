package me.TechsCode.UltraPermissions.base.legacy;

import me.TechsCode.UltraPermissions.base.gui.Action;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;

public class Common {
   public static Phrase BACK_TITLE = Phrase.create("backButton.title", "Back");
   public static Phrase BACK_ACTION = Phrase.create("backButton.action", "Click to go back", Colors.GRAY);

   public Common() {
   }

   public static void BackButton(Button button, Action action) {
      button.material(XMaterial.OAK_SIGN).name(Animation.fading(BACK_TITLE.get(), Colors.DarkCyan, Colors.Cyan)).lore(BACK_ACTION.get());
      button.action(action);
   }
}
