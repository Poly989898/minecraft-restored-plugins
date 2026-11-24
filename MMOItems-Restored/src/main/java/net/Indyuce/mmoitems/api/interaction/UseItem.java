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
   public UseItem(@NotNull Player var1, @NotNull NBTItem var2) {
      this(PlayerData.get(var1), var2);
   }

   public UseItem(@NotNull PlayerData var1, @NotNull NBTItem var2) {
      this.player = var1.getPlayer();
      this.playerData = var1;
      this.mmoitem = new VolatileMMOItem(var2);
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

   private void scheduleCommandExecution(CommandData var1) {
      String var2 = MythicLib.plugin.getPlaceholderParser().parse(this.player, var1.getCommand());
      if (!var1.hasDelay()) {
         this.dispatchCommand(var2, var1.isConsoleCommand(), var1.hasOpPerms());
      } else {
         Bukkit.getScheduler()
            .runTaskLater(MMOItems.plugin, () -> this.dispatchCommand(var2, var1.isConsoleCommand(), var1.hasOpPerms()), (long)var1.getDelay() * 20L);
      }
   }

   private void dispatchCommand(String var1, boolean var2, boolean var3) {
      if (var2) {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var1);
      } else {
         if (var3 && !this.player.isOp()) {
            this.player.setOp(true);

            try {
               Bukkit.dispatchCommand(this.player, var1);
            } catch (Exception var8) {
               this.player.setOp(false);
            } finally {
               this.player.setOp(false);
            }
         } else {
            Bukkit.dispatchCommand(this.player, var1);
         }
      }
   }

   @Deprecated
   @Nullable
   public static UseItem getItem(Player var0, NBTItem var1, String var2) {
      return Type.get(var2).toUseItem(var0, var1);
   }

   @Deprecated
   public static UseItem getItem(@NotNull Player var0, @NotNull NBTItem var1, @NotNull Type var2) {
      return var2.toUseItem(PlayerData.get(var0), var1);
   }
}
