package com.ticxo.modelengine.api.nms.ui;

import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public interface AnvilHandler {
   void openAnvil(Player player, Function<String, Boolean> function, Supplier<Boolean> supplier);

   void reopenAnvil(Player player);

   void consumeConfirm(Player player);

   void consumeClose(Player player);

   void removeAnvil(Player anvil);

   void closeAnvil(Player player);

   boolean isListening(Player player);

   AnvilHandler.Menu getAnvilMenu(Player player);

   @Getter
   @Setter
   class Menu {
      private final int id;
      private final Function<String, Boolean> onInput;
      private final Supplier<Boolean> onClose;
      private String value;

      public boolean confirm() {
         return this.onInput.apply(this.value);
      }

      public boolean close() {
         return this.onClose.get();
      }

      @Generated
      public Menu(int id, Function<String, Boolean> onInput, Supplier<Boolean> onClose) {
         this.id = id;
         this.onInput = onInput;
         this.onClose = onClose;
      }
   }
}
