package it.ytnoos.lpx.listeners;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketEvent;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.manager.server.VersionComparison;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTabComplete;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.config.ItemStripConfig;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

public class ServerPacketListener extends PacketListenerAbstract {
   private final LPXBukkit lpxBukkit;
   private final LPXListener lpxListener;
   private final boolean legacyServer;
   private final boolean componentServer;
   private static final String COMMAND_PREFIX = "/lpx";

   public ServerPacketListener(LPXBukkit lpxBukkit2, LPXListener lpxListener2) {
      this.lpxBukkit = lpxBukkit2;
      this.lpxListener = lpxListener2;
      this.legacyServer = PacketEvents.getAPI().getServerManager().getVersion().is(VersionComparison.OLDER_THAN_OR_EQUALS, ServerVersion.V_1_12_2);
      this.componentServer = PacketEvents.getAPI().getServerManager().getVersion().isNewerThan(ServerVersion.V_1_20_4);
   }

   public void onPacketReceive(PacketReceiveEvent packetReceiveEvent) {
      this.lpxListener.handleReceive(packetReceiveEvent, false);
   }

   public void onPacketEventExternal(PacketEvent packetEvent) {
      this.lpxListener.handleDecode(packetEvent, false);
   }

   public void onPacketSend(PacketSendEvent packetSendEvent) {
      if (packetSendEvent.getPacketType() == Server.KEEP_ALIVE) {
         this.lpxBukkit.getRegistry().getPlayer(packetSendEvent.getUser()).ifPresent(protocolPlayer -> protocolPlayer.setLastKeepAliveSent(System.currentTimeMillis()));
      }

      ItemStripConfig itemStripConfig = this.lpxBukkit.getConfigManager().getOptionsConfig().getItemStripConfig();
      if (itemStripConfig.isEnabled()) {
         this.stripWindowItems(itemStripConfig, packetSendEvent);
      }

      if (this.legacyServer) {
         if (packetSendEvent.getPacketType() == Server.TAB_COMPLETE) {
            User user = packetSendEvent.getUser();
            if (user != null && user.getUUID() != null) {
               this.lpxBukkit.getRegistry().getPlayer(user).ifPresent(protocolPlayer2 -> {
                  Player player = protocolPlayer2.getPlayer().orElse(null);
                  if (player == null || !this.lpxBukkit.getLpxCommand().hasAnyPermission(player)) {
                     boolean flag = new WrapperPlayServerTabComplete(packetSendEvent).getCommandMatches().removeIf(commandMatch -> commandMatch.getText().startsWith(COMMAND_PREFIX));
                     if (flag) {
                        packetSendEvent.markForReEncode(true);
                     }
                  }
               });
            }
         }
      }
   }

   private void stripWindowItems(ItemStripConfig itemStripConfig, PacketSendEvent packetSendEvent) {
      if (packetSendEvent.getPacketType() == Server.WINDOW_ITEMS) {
         WrapperPlayServerWindowItems wrapperPlayServerWindowItems = new WrapperPlayServerWindowItems(packetSendEvent);
         List list = wrapperPlayServerWindowItems.getItems();
         boolean flag = false;

         for (int value = 0; value < list.size(); value++) {
            ItemStack itemStack = this.stripItem((ItemStack)list.get(value), itemStripConfig);
            if (itemStack != null) {
               list.set(value, itemStack);
               flag = true;
            }
         }

         ItemStack itemStack2 = (ItemStack)wrapperPlayServerWindowItems.getCarriedItem().orElse(null);
         if (itemStack2 != null) {
            ItemStack itemStack3 = this.stripItem(itemStack2, itemStripConfig);
            if (itemStack3 != null) {
               wrapperPlayServerWindowItems.setCarriedItem(itemStack3);
               flag = true;
            }
         }

         if (flag) {
            packetSendEvent.markForReEncode(true);
         }
      } else {
         if (itemStripConfig.isSetSlot() && packetSendEvent.getPacketType() == Server.SET_SLOT) {
            WrapperPlayServerSetSlot wrapperPlayServerSetSlot = new WrapperPlayServerSetSlot(packetSendEvent);
            ItemStack itemStack4 = this.stripItem(wrapperPlayServerSetSlot.getItem(), itemStripConfig);
            if (itemStack4 != null) {
               wrapperPlayServerSetSlot.setItem(itemStack4);
               packetSendEvent.markForReEncode(true);
            }
         }
      }
   }

   private ItemStack stripItem(ItemStack itemStack, ItemStripConfig itemStripConfig) {
      boolean flag = false;
      if (itemStack != null && !itemStack.isEmpty()) {
         if (this.componentServer) {
            if (itemStack.getComponents() == null || itemStack.getComponents().getPatches() == null) {
               return null;
            }

            boolean flag2 = itemStack.getComponents().getPatches().keySet().removeIf(componentType -> itemStripConfig.getRemovedComponents().contains(componentType.getName().getKey()));
            if (flag2) {
               return itemStack;
            }

            if (!flag) {
               return null;
            }
         }

         NBTCompound nbtCompound = itemStack.getNBT();
         if (nbtCompound != null) {
            boolean flag3 = false;

            for (Object var11_o : new ArrayList(nbtCompound.getTags().keySet())) { String string = (String) var11_o;
               if (itemStripConfig.getRemovedNbt().contains(string)) {
                  nbtCompound.removeTag(string);
                  flag3 = true;
               }

               if (flag) {
                  break;
               }
            }

            if (flag3) {
               return itemStack;
            }
         }

         return null;
      } else {
         return null;
      }
   }

}
