package fr.phoenixdevt.mmoprofiles.bukkit.utils;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import io.lumine.mythic.lib.api.util.TemporaryListener;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.GeneratedInventory;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ChatInput extends TemporaryListener {
   private final ProfileListImpl playerData;
   private final BiPredicate<ChatInput, String> inputHandler;
   private final Consumer<ChatInput> cancelHandler;
   @Nullable
   private GeneratedInventory inv;

   public ChatInput(ProfileListImpl playerData, GeneratedInventory inv, BiPredicate<ChatInput, String> inputHandler) {
      this(playerData, inv, inputHandler, ignored -> {});
   }

   public ChatInput(ProfileListImpl playerData, GeneratedInventory inv, BiPredicate<ChatInput, String> inputHandler, Consumer<ChatInput> cancelHandler) {
      super(new HandlerList[]{AsyncPlayerChatEvent.getHandlerList(), PlayerMoveEvent.getHandlerList()});
      this.playerData = playerData;
      this.inputHandler = inputHandler;
      this.cancelHandler = cancelHandler;
      this.inv = inv;
      if (inv != null) {
         VersionUtils.getOpen(playerData.getPlayer()).close();
      }
   }

   public void killInventory() {
      this.inv = null;
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent event) {
      if (event.getPlayer().equals(this.playerData.getPlayer())) {
         event.setCancelled(true);
         Bukkit.getScheduler().runTask(MMOProfiles.plugin, () -> {
            if (event.getMessage().equalsIgnoreCase("cancel")) {
               this.cancelHandler.accept(this);
               this.close();
            } else if (this.inputHandler.test(this, event.getMessage())) {
               this.close();
            }
         });
      }
   }

   @EventHandler
   public void cancel(PlayerMoveEvent event) {
      if (event.getFrom().getBlockX() != event.getTo().getBlockX()
         || event.getFrom().getBlockY() != event.getTo().getBlockY()
         || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
         this.close();
      }
   }

   public void whenClosed() {
      if (this.inv != null) {
         this.inv.open();
      }
   }
}
