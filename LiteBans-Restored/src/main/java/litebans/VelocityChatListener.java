package litebans;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import org.jetbrains.annotations.NotNull;

public final class VelocityChatListener implements VelocityListener {
   private final Platform platform;
   private final PunishmentLookupModule punishmentLookupModule;

   public VelocityChatListener(@NotNull Platform platform2) {
      this.platform = platform2;
      this.punishmentLookupModule = (PunishmentLookupModule)this.platform.getModule(PunishmentLookupModule.class);
   }

   public final Platform getPlatform() {
      return this.platform;
   }

   @Subscribe(order = PostOrder.EARLY)
   public final void handlePlayerChatEvent(@NotNull PlayerChatEvent playerChatEvent) {
      if (playerChatEvent.getResult().isAllowed()) {
         Player player = playerChatEvent.getPlayer();
         String message = playerChatEvent.getMessage();
         MuteChatTask muteChatTask = this.punishmentLookupModule.getMuteChatTask();
         Intrinsics.checkNotNull(message);
         muteChatTask.handleObj(playerChatEvent, message, this.platform.wrapSender(player), true);
      }
   }

   @Subscribe(order = PostOrder.EARLY)
   public final void handleCommandExecuteEvent(@NotNull CommandExecuteEvent commandExecuteEvent) {
      CommandSource commandSource = commandExecuteEvent.getCommandSource();
      String command = commandExecuteEvent.getCommand();
      if (commandExecuteEvent.getResult().isAllowed() && commandSource instanceof Player) {
         MuteChatTask.handleMuteChatTask(this.punishmentLookupModule.getMuteChatTask(), commandExecuteEvent, '/' + command, this.platform.wrapSender(commandSource), false, 8, null);
      }
   }

   public VelocityChatListener obtainLiteVelocityListenerImpl2() {
      Platform platform2 = this.platform;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform2.registerListeners(platformListeners);
      return this;
   }

   @Override
   public PlatformListener obtainPlatformListener() {
      return this.obtainLiteVelocityListenerImpl2();
   }
}
