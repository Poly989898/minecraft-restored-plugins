package io.lumine.mythic.bukkit.commands.items.edit;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.commands.CommandHelper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.lumine.mythic.bukkit.utils.commands.Command;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.menus.items.ItemEditorMenuContext;
import io.lumine.mythic.core.menus.items.ItemMenuContext;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemEditCommand extends Command<MythicBukkit> {
   public static Cache<UUID, ItemEditorMenuContext> lastEdited = Caffeine.newBuilder().maximumSize(50L).expireAfterWrite(Duration.ofMinutes(5L)).build();

   public ItemEditCommand(Command<MythicBukkit> parent) {
      super(parent);
      this.addSubCommands(new Command[]{new EditLoreCommand(this), new EditStatsCommand(this), new EditEnchantsCommand(this)});
   }

   @Override
   public boolean onCommand(CommandSender sender, String[] args) {
      MythicBukkit mythicBukkit = this.getPlugin();
      if (!MythicBukkit.isVolatile()) {
         CommandHelper.sendError(sender, "This feature requires Mythic Premium");
         return true;
      } else if (sender instanceof Player player) {
         if (args.length == 0) {
            return true;
         } else {
            String itemName = args[0];
            Optional<MythicItem> maybeItem = this.getPlugin().getItemManager().getItem(itemName);
            if (maybeItem.isEmpty()) {
               CommandHelper.sendError(sender, "Invalid Item Name");
               return true;
            } else {
               MythicItem item = maybeItem.get();
               ItemEditorMenuContext ctx = lastEdited.getIfPresent(player.getUniqueId());
               if (ctx == null) {
                  ItemMenuContext btx = new ItemMenuContext();
                  ctx = new ItemEditorMenuContext(item, btx);
               } else {
                  lastEdited.invalidate(player.getUniqueId());
               }

               this.getPlugin().getMenuManager().getItemEditorMenu().open(player, ctx);
               return true;
            }
         }
      } else {
         return true;
      }
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, String[] args) {
      return Collections.emptyList();
   }

   @Override
   public String getPermissionNode() {
      return "mythicmobs.command.items.edit";
   }

   @Override
   public boolean isConsoleFriendly() {
      return true;
   }

   @Override
   public String getName() {
      return "edit";
   }
}
