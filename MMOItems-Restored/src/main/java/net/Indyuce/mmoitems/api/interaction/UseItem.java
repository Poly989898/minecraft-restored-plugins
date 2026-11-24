package net.Indyuce.mmoitems.api.interaction;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.comp.flags.CustomFlag;
import java.util.Random;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.stat.data.CommandData;
import net.Indyuce.mmoitems.stat.data.CommandListData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UseItem {
   protected final Player player;
   protected final PlayerData playerData;
   protected final VolatileMMOItem mmoitem;
   protected static final Random RANDOM = new Random();

   @Deprecated
   public UseItem(@NotNull Player player, @NotNull NBTItem nbtItem) {
      this(PlayerData.get(player), nbtItem);
   }

   public UseItem(@NotNull PlayerData playerData, @NotNull NBTItem nbtItem) {
      this.player = playerData.getPlayer();
      this.playerData = playerData;
      this.mmoitem = new VolatileMMOItem(nbtItem);
   }

   public Player getPlayer() {
      return this.player;
   }

   public PlayerData getPlayerData() {
      return this.playerData;
   }

   public VolatileMMOItem getMMOItem() {
      return this.mmoitem;
   }

   public NBTItem getNBTItem() {
      return this.mmoitem.getNBT();
   }

   public ItemStack getItem() {
      return this.mmoitem.getNBT().getItem();
   }

   public boolean checkItemRequirements() {
      return this.playerData.getRPG().canUse(this.mmoitem.getNBT(), true);
   }

   public void executeCommands() {
      if (MythicLib.plugin.getFlags().isFlagAllowed(this.player, CustomFlag.MI_COMMANDS) && this.mmoitem.hasData(ItemStats.COMMANDS)) {
         ((CommandListData)this.mmoitem.getData(ItemStats.COMMANDS)).getCommands().forEach(this::scheduleCommandExecution);
      }
   }

   private void scheduleCommandExecution(CommandData commandData) {
      String text = MythicLib.plugin.getPlaceholderParser().parse(this.player, commandData.getCommand());
      if (!commandData.hasDelay()) {
         this.dispatchCommand(text, commandData.isConsoleCommand(), commandData.hasOpPerms());
      } else {
         Bukkit.getScheduler()
            .runTaskLater(MMOItems.plugin, () -> this.dispatchCommand(text, commandData.isConsoleCommand(), commandData.hasOpPerms()), (long)commandData.getDelay() * 20L);
      }
   }

   private void dispatchCommand(String text, boolean flag, boolean asOp) {
      if (flag) {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), text);
      } else {
         if (asOp && !this.player.isOp()) {
            this.player.setOp(true);

            try {
               Bukkit.dispatchCommand(this.player, text);
            } catch (Exception exception) {
               this.player.setOp(false);
            } finally {
               this.player.setOp(false);
            }
         } else {
            Bukkit.dispatchCommand(this.player, text);
         }
      }
   }

   @Deprecated
   @Nullable
   public static UseItem getItem(Player player, NBTItem nbtItem, String text) {
      return Type.get(text).toUseItem(player, nbtItem);
   }

   @Deprecated
   public static UseItem getItem(@NotNull Player player, @NotNull NBTItem nbtItem, @NotNull Type type) {
      return type.toUseItem(PlayerData.get(player), nbtItem);
   }
}
