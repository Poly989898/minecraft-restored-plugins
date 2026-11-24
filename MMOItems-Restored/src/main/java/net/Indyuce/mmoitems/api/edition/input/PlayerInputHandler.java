package net.Indyuce.mmoitems.api.edition.input;

import net.Indyuce.mmoitems.api.edition.Edition;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerInputHandler {
   private final Edition edition;

   public PlayerInputHandler(Edition edition) {
      this.edition = edition;
   }

   public Player getPlayer() {
      return this.edition.getInventory().getPlayer();
   }

   protected void registerInput(@NotNull String text) {
      Validate.isTrue(Bukkit.isPrimaryThread(), "Input must be registered on primary thread");
      if (text.equalsIgnoreCase("cancel") || this.edition.processInput(text)) {
         if (this.edition.shouldGoBack()) {
            this.edition.getInventory().open();
         }

         this.close();
      }
   }

   public abstract void close();
}
