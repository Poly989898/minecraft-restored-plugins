package net.Indyuce.mmocore.api.util.input;

import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChatInput extends PlayerInput {
   private final PlayerInput.InputType inputType;
   private final GeneratedInventory lastOpened;

   @Deprecated
   public ChatInput(@NotNull Player player, @NotNull PlayerInput.InputType inputType, @NotNull Consumer<String> output) {
      this(player, inputType, null, output);
   }

   public ChatInput(@NotNull Player player, @NotNull PlayerInput.InputType inputType, @Nullable GeneratedInventory lastOpened, @NotNull Consumer<String> output) {
      super(player, output);
      this.inputType = inputType;
      this.lastOpened = lastOpened;
      player.closeInventory();
      ConfigMessage.fromKey("player-input.chat." + inputType.getLowerCaseName()).send(player);
   }

   @Override
   public void close() {
      AsyncPlayerChatEvent.getHandlerList().unregister(this);
      InventoryOpenEvent.getHandlerList().unregister(this);
   }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
   public void registerInput(AsyncPlayerChatEvent event) {
      if (event.getPlayer().equals(this.getPlayer())) {
         this.close();
         event.setCancelled(true);
         if (event.getMessage().equals("cancel")) {
            if (this.lastOpened != null) {
               Bukkit.getScheduler().runTask(MMOCore.plugin, () -> this.lastOpened.open());
            }

            ConfigMessage.fromKey("player-input.chat." + this.inputType.getLowerCaseName() + "-cancel").send(this.getPlayer());
         } else {
            Bukkit.getScheduler().runTask(MMOCore.plugin, () -> this.output(event.getMessage()));
         }
      }
   }

   @EventHandler
   public void b(InventoryOpenEvent event) {
      if (event.getPlayer().equals(this.getPlayer())) {
         this.close();
      }
   }
}
