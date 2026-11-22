package net.Zrips.CMILib.RawMessages;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMIPlayerConnection;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.commands.CommandsHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RawMessageManager {
  private static Object packet;
  private static Class<?> nmsIChatBaseComponent;
  private static Class<?> packetType;
  private static Class<?> ChatMessageclz;
  private static Class<?> sub;
  private static Object[] consts;
  private static Constructor<?> constructor;
  static HashMap<UUID, Set<Long>> temp = new HashMap<>();
  private static final int MAX_ENTRIES = 5000;
  static LinkedHashMap<Long, RawMessageCommand> map =
      new LinkedHashMap<Long, RawMessageCommand>(5001, 0.75F, false) {
        @Override
        protected boolean removeEldestEntry(Entry<Long, RawMessageCommand> eldest) {
          return this.size() > 5000;
        }
      };
  public static final String rmc = "rmc";
  public static final String rmccmd = "/" + CommandsHandler.getLabel() + " " + "rmc" + " ";

  public static Long add(RawMessageCommand rmc) {
    Random rnd = new Random();
    long id = rnd.nextLong();

    while (map.containsKey(id)) {
      id = rnd.nextLong();
    }

    map.put(id, rmc);
    if (rmc.getUUID() != null) {
      Set<Long> old = temp.getOrDefault(rmc.getUUID(), new HashSet<>());
      old.add(id);
      temp.put(rmc.getUUID(), old);
    }

    return id;
  }

  public static void clearCache(UUID uuid) {
    if (uuid != null) {
      Set<Long> list = temp.get(uuid);
      if (list != null) {
        for (Long one : list) {
          map.remove(one);
        }
      }
    }
  }

  public static void delete(RawMessageCommand rmc) {
    remove(rmc.getId());
  }

  public static boolean perform(CommandSender sender, Long id) {
    RawMessageCommand rmc = map.get(id);
    if (rmc == null) {
      return false;
    }

    rmc.run(sender);
    if (!rmc.isKeep()) {
      delete(rmc);
    }

    return true;
  }

  public static RawMessageCommand get(Long id) {
    return map.get(id);
  }

  public static void remove(Long id) {
    map.remove(id);
  }

  public static void send(Set<Player> players, String json) {
    if (players != null && !players.isEmpty() && json != null) {
      Object serialized =
          CMILib.getInstance().getReflectionManager().textToIChatBaseComponent(json);

      for (Player receivingPacket : players) {
        if (!receivingPacket.isOnline()) {
          return;
        }

        try {
          if (Version.isMojangMappings()) {
            packet = constructor.newInstance(serialized, false);
          } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
            packet = constructor.newInstance(serialized, false);
          } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
            if (Version.isCurrentSubEqual(0)) {
              packet = constructor.newInstance(serialized, 1);
            } else {
              packet = constructor.newInstance(serialized, false);
            }
          } else if (Version.isCurrentHigher(Version.v1_15_R1)) {
            packet = constructor.newInstance(serialized, consts[1], new UUID(0L, 0L));
          } else if (Version.isCurrentHigher(Version.v1_11_R1)) {
            packet = constructor.newInstance(serialized, consts[1]);
          } else if (Version.isCurrentHigher(Version.v1_7_R4)) {
            packet = constructor.newInstance(serialized, (byte) 1);
          } else {
            packet = constructor.newInstance(serialized, 1);
          }

          CMIPlayerConnection.sendPacket(receivingPacket, packet);
        } catch (Exception ex) {
          ex.printStackTrace();
          CMIMessages.consoleMessage(
              "Failed to show json message with packets, using command approach");
          CMIScheduler.runTask(
              CMILib.getInstance(),
              () ->
                  Bukkit.dispatchCommand(
                      Bukkit.getConsoleSender(),
                      "minecraft:tellraw \"" + receivingPacket.getName() + "\" " + json));
        }
      }
    }
  }

  public static void send(CommandSender receivingPacket, String msg) {
    if (receivingPacket instanceof Player) {
      send((Player) receivingPacket, msg);
    } else {
      receivingPacket.sendMessage(msg);
    }
  }

  public static void send(Player receivingPacket, String json) {
    if (receivingPacket != null) {
      if (receivingPacket.isOnline()) {
        HashSet<Player> set = new HashSet<>();
        set.add(receivingPacket);
        send(set, json);
      }
    }
  }

  static {
    Version version = Version.getCurrent();
    if (Version.isMojangMappings()) {
      try {
        constructor =
            Class.forName("net.minecraft.network.protocol.game.ClientboundSystemChatPacket")
                .getConstructor(
                    Class.forName("net.minecraft.network.chat.Component"), boolean.class);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R2)) {
      try {
        constructor =
            Class.forName("net.minecraft.network.protocol.game.ClientboundSystemChatPacket")
                .getConstructor(
                    Class.forName("net.minecraft.network.chat.IChatBaseComponent"), boolean.class);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
      try {
        packetType =
            Class.forName("net.minecraft.network.protocol.game.ClientboundSystemChatPacket");
        nmsIChatBaseComponent = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
        constructor = packetType.getConstructor(nmsIChatBaseComponent, boolean.class);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
      try {
        packetType =
            Class.forName("net.minecraft.network.protocol.game.ClientboundSystemChatPacket");
        nmsIChatBaseComponent = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
        if (Version.isCurrentSubEqual(0)) {
          constructor = packetType.getConstructor(nmsIChatBaseComponent, int.class);
        } else {
          constructor = packetType.getConstructor(nmsIChatBaseComponent, boolean.class);
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
      try {
        packetType = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutChat");
        nmsIChatBaseComponent = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
        ChatMessageclz = Class.forName("net.minecraft.network.chat.ChatMessageType");
        consts = ChatMessageclz.getEnumConstants();
        sub = consts[2].getClass();
        constructor = packetType.getConstructor(nmsIChatBaseComponent, sub, UUID.class);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    } else {
      try {
        packetType = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat");
        nmsIChatBaseComponent =
            Class.forName("net.minecraft.server." + version + ".IChatBaseComponent");
        if (Version.isCurrentHigher(Version.v1_11_R1)) {
          ChatMessageclz = Class.forName("net.minecraft.server." + version + ".ChatMessageType");
          consts = ChatMessageclz.getEnumConstants();
          sub = consts[2].getClass();
        }

        if (Version.isCurrentHigher(Version.v1_15_R1)) {
          constructor = packetType.getConstructor(nmsIChatBaseComponent, sub, UUID.class);
        } else if (Version.isCurrentHigher(Version.v1_11_R1)) {
          constructor = packetType.getConstructor(nmsIChatBaseComponent, sub);
        } else if (Version.isCurrentHigher(Version.v1_7_R4)) {
          constructor = packetType.getConstructor(nmsIChatBaseComponent, byte.class);
        } else {
          constructor = packetType.getConstructor(nmsIChatBaseComponent, int.class);
        }
      } catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
        CMIMessages.consoleMessage("Error {0} ");
        CMIMessages.consoleMessage(ex.toString());
      }
    }
  }
}
