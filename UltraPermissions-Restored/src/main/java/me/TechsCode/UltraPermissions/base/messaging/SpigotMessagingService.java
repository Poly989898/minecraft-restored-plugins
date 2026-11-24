package me.TechsCode.UltraPermissions.base.messaging;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.scheduler.RecurringTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class SpigotMessagingService implements PluginMessageListener {
   private SpigotTechPlugin plugin;
   private List<QueuedMessage> queuedMessages;
   private RecurringTask sendTask;
   private List<SpigotMessagingListener> listeners;

   public SpigotMessagingService(SpigotTechPlugin spigotTechPlugin) {
      this.plugin = spigotTechPlugin;
      this.queuedMessages = new ArrayList<>();
      this.listeners = new ArrayList<>();
      this.sendTask = spigotTechPlugin.getScheduler().runTaskTimer(this::SendTask, 1L, 1L);
      Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)spigotTechPlugin.getBootstrap(), "BungeeCord");
      Bukkit.getMessenger().registerIncomingPluginChannel((Plugin)spigotTechPlugin.getBootstrap(), "BungeeCord", this);
   }

   public void onDisable() {
      this.sendTask.stop();
   }

   private void SendTask() {
      Player first = (Player)Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
      Iterator iterator = this.queuedMessages.iterator();
      if (iterator.hasNext() && first != null) {
         QueuedMessage queuedMessage = (QueuedMessage)iterator.next();
         ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput();
         dataOutput.writeUTF(this.plugin.getName() + "//" + queuedMessage.encode());
         first.sendPluginMessage((Plugin)this.plugin.getBootstrap(), "BungeeCord", dataOutput.toByteArray());
         queuedMessage.onSend();
         this.queuedMessages.remove(queuedMessage);
      }
   }

   public void register(SpigotMessagingListener spigotMessagingListener) {
      this.listeners.add(spigotMessagingListener);
   }

   public void onPluginMessageReceived(String text, Player player, byte[] bytes) {
      try {
         DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bytes));
         if (dataInputStream.available() == 0) {
            return;
         }

         String uTF = dataInputStream.readUTF();
         if (uTF.startsWith(this.plugin.getName() + "//")) {
            Message message = Message.decode(uTF.replace(this.plugin.getName() + "//", ""));
            this.listeners.forEach(spigotMessagingListener -> spigotMessagingListener.onMessage(message));
         }
      } catch (IOException exception) {
         exception.printStackTrace();
      }
   }

   public void send(QueuedMessage queuedMessage) {
      for (QueuedMessage queued : new ArrayList<>(this.queuedMessages)) {
         if (queued.encode().equals(queuedMessage.encode())) {
            return;
         }
      }

      this.queuedMessages.add(queuedMessage);
   }
}
