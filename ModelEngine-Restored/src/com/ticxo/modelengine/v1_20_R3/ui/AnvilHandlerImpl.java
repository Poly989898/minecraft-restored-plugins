package com.ticxo.modelengine.v1_20_R3.ui;

import com.ticxo.modelengine.api.nms.ui.AnvilHandler;
import com.ticxo.modelengine.v1_20_R3.entity.EntityUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.NetworkUtils;
import com.ticxo.modelengine.v1_20_R3.network.utils.Packets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutCloseWindow;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateTime;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Blocks;
import org.bukkit.entity.Player;

public class AnvilHandlerImpl implements AnvilHandler {
   private final Map<UUID, AnvilHandler.Menu> anvilId = new HashMap<>();

   @Override
   public void openAnvil(Player player, Function<String, Boolean> onInput, Supplier<Boolean> onClose) {
      EntityPlayer nmsPlayer = (EntityPlayer)EntityUtils.nms(player);
      int id = nmsPlayer.nextContainerCounter();
      this.anvilId.put(player.getUniqueId(), new AnvilHandler.Menu(id, onInput, onClose));
      this.reopenAnvil(player);
   }

   @Override
   public void reopenAnvil(Player player) {
      AnvilHandler.Menu menu = this.getAnvilMenu(player);
      if (menu != null) {
         ItemStack item = new ItemStack(Blocks.b).a(IChatBaseComponent.i());
         final PacketPlayOutOpenWindow anvilScreen = new PacketPlayOutOpenWindow(menu.getId(), Containers.i, IChatBaseComponent.i());
         final PacketPlayOutSetSlot setItem = new PacketPlayOutSetSlot(menu.getId(), 0, 0, item);
         NetworkUtils.sendBundled(player.getUniqueId(), new Packets() {
            {
               this.add(anvilScreen);
               this.add(setItem);
            }
         });
      }
   }

   @Override
   public void consumeConfirm(Player player) {
      AnvilHandler.Menu callback = this.anvilId.get(player.getUniqueId());
      if (callback != null && callback.confirm()) {
         this.reopenAnvil(player);
      } else {
         this.closeAnvil(player);
      }
   }

   @Override
   public void consumeClose(Player player) {
      AnvilHandler.Menu callback = this.anvilId.get(player.getUniqueId());
      if (callback != null && callback.close()) {
         this.reopenAnvil(player);
      } else {
         this.removeAnvil(player);
      }
   }

   @Override
   public void removeAnvil(Player player) {
      AnvilHandler.Menu callback = this.anvilId.remove(player.getUniqueId());
      if (callback != null) {
         callback.close();
      }

      World world = EntityUtils.nms(player).dM();
      PacketPlayOutUpdateTime packet = new PacketPlayOutUpdateTime(world.X(), world.Y(), world.Z().b(GameRules.l));
      NetworkUtils.send(player.getUniqueId(), packet);
   }

   @Override
   public void closeAnvil(Player player) {
      final AnvilHandler.Menu callback = this.anvilId.remove(player.getUniqueId());
      if (callback != null) {
         callback.close();
         final World world = EntityUtils.nms(player).dM();
         NetworkUtils.sendBundled(player.getUniqueId(), new Packets() {
            {
               this.add(new PacketPlayOutCloseWindow(callback.getId()));
               this.add(new PacketPlayOutUpdateTime(world.X(), world.Y(), world.Z().b(GameRules.l)));
            }
         });
      }
   }

   @Override
   public boolean isListening(Player player) {
      return this.anvilId.containsKey(player.getUniqueId());
   }

   @Override
   public AnvilHandler.Menu getAnvilMenu(Player player) {
      return this.anvilId.get(player.getUniqueId());
   }
}
