package litebans;

import net.md_5.bungee.chat.ChatVersion;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class FeatureDetection {
   private static boolean flag = ClassUtil.classExists("org.bukkit.block.TestBlock");
   private static boolean return2 = ClassUtil.classExists("net.md_5.bungee.api.chat.BaseComponent");
   private static boolean value2 = false;
   private static boolean value = false;

   public static boolean obtainFlag() {
      return return2;
   }

   public static boolean obtainFlag2() {
      return value2;
   }

   public static boolean obtainFlag3() {
      return value;
   }

   public static void handlePlayer(Player player, String text) {
      Spigot spigot2 = player.spigot();
      if (BungeeChatSupport.obtainFlag()) {
         if (flag) {
            spigot2.sendMessage(BungeeChatConversion.computeBaseComponentArray(text, ChatVersion.V1_21_5));
         } else {
            spigot2.sendMessage(BungeeChatConversion.computeBaseComponentArray(text, ChatVersion.V1_16));
         }
      } else {
         spigot2.sendMessage(BungeeChatSupport.computeBaseComponentArray2(text));
      }
   }

   static {
      try {
         AsyncPlayerPreLoginEvent.class.getMethod("kickMessage");
         value = true;
         Class.forName("io.papermc.paper.threadedregions.scheduler.RegionScheduler");
         int activeCount2 = Thread.activeCount();
         Thread[] threads = new Thread[activeCount2];
         Thread.enumerate(threads);

         for (Thread thread : threads) {
            if (thread != null && thread.getName().startsWith("Folia Watchdog")) {
               value2 = true;
            }
         }
      } catch (LinkageError | ReflectiveOperationException e) {
      }
   }
}
