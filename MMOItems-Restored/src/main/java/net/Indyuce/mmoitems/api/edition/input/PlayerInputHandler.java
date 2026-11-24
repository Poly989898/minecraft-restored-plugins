package net.Indyuce.mmoitems.api.edition.input;

import net.Indyuce.mmoitems.api.edition.Edition;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerInputHandler {
   private final Edition edition;

   public PlayerInputHandler(Edition var1) {
      this.edition = var1;
   }

   public Player getPlayer() {
      return this.edition.getInventory().getPlayer();
   }

   protected void registerInput(@NotNull String var1) {
      Validate.isTrue(Bukkit.isPrimaryThread(), "Input must be registered on primary thread");
      if (var1.equalsIgnoreCase("cancel") || this.edition.processInput(var1)) {
         if (this.edition.shouldGoBack()) {
            this.edition.getInventory().open();
         }

         this.close();
      }
   }

   public abstract void close();
}
