package com.nexomc.nexo.mechanics.misc.commands;

import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.clock.Clock;
import com.nexomc.nexo.utils.commands.CommandsParser;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import kotlin.Metadata;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001c\u0010\u0006\u001a\u00020\u0007*\u00020\bH\u0007b\u000e\b\t\u0012\n\b\n\u0012\u0006\b\n0\u000b8\fJ&\u0010\r\u001a\u00020\u0007*\u00020\u000eH\u0007b\u0018\b\t\u0012\n\b\n\u0012\u0006\b\n0\u000b8\u000f\u0012\b\b\u0010\u0012\u0004\b\u0007\u0010\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/commands/CommandsMechanicListener;", "Lorg/bukkit/event/Listener;", "factory", "Lcom/nexomc/nexo/mechanics/misc/commands/CommandsMechanicFactory;", "<init>", "(Lcom/nexomc/nexo/mechanics/misc/commands/CommandsMechanicFactory;)V", "onInteract", "", "Lorg/bukkit/event/player/PlayerInteractEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "NORMAL", "onConsume", "Lorg/bukkit/event/player/PlayerItemConsumeEvent;", "MONITOR", "ignoreCancelled", "com.nexomc:core"}
)
public final class CommandsMechanicListener implements Listener {
   @NotNull
   private final CommandsMechanicFactory factory;

   public CommandsMechanicListener(@NotNull CommandsMechanicFactory factory) {
      super();
      this.factory = factory;
   }

   @EventHandler(
      priority = EventPriority.NORMAL
   )
   public final void onInteract(@NotNull PlayerInteractEvent playerInteractEvent) {
      ItemStack itemStack = playerInteractEvent.getItem();
      if (itemStack != null) {
         ItemStack item = itemStack;
         CommandsMechanic mechanic = this.factory.getMechanic(item);
         if (playerInteractEvent.getAction() != Action.PHYSICAL && mechanic != null) {
            Player player = playerInteractEvent.getPlayer();
            if (!mechanic.hasPermission(player)) {
               Message message = Message.NO_PERMISSION;
               CommandSender commandSender = (CommandSender)playerInteractEvent.getPlayer();
               TagResolver[] tagResolvers = new TagResolver[1];
               String permission = mechanic.getPermission();
               if (permission == null) {
                  permission = "";
               }

               tagResolvers[0] = AdventureUtils.tagResolver("permission", permission);
               message.send(commandSender, tagResolvers);
            } else if (!item.hasData((DataComponentType)DataComponentTypes.CONSUMABLE) && !item.hasData((DataComponentType)DataComponentTypes.FOOD) && !item.getType().isEdible()) {
               player = playerInteractEvent.getPlayer();
               Clock it = mechanic.clock(player);
               int value = 0;
               int value2 = 0;
               Clock clock = it.isFinished() ? it : null;
               if (clock != null) {
                  clock.reset();
                  CommandsParser commands = mechanic.getCommands();
                  player = playerInteractEvent.getPlayer();
                  commands.perform(player);
                  if (mechanic.isOneUsage()) {
                     item.setAmount(item.getAmount() - 1);
                  }

               } else {
                  it.sendToPlayer(playerInteractEvent.getPlayer());
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public final void onConsume(@NotNull PlayerItemConsumeEvent playerItemConsumeEvent) {
      CommandsMechanic commandsMechanic = this.factory.getMechanic(playerItemConsumeEvent.getItem());
      if (commandsMechanic != null) {
         CommandsMechanic mechanic = commandsMechanic;
         Player player = playerItemConsumeEvent.getPlayer();
         if (mechanic.hasPermission(player)) {
            player = playerItemConsumeEvent.getPlayer();
            Clock it = mechanic.clock(player);
            int value = 0;
            int value2 = 0;
            Clock clock = it.isFinished() ? it : null;
            if (clock != null) {
               clock.reset();
               CommandsParser commands = mechanic.getCommands();
               player = playerItemConsumeEvent.getPlayer();
               commands.perform(player);
            } else {
               it.sendToPlayer(playerItemConsumeEvent.getPlayer());
            }
         }
      }
   }
}
