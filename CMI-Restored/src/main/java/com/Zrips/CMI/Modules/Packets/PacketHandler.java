package com.Zrips.CMI.Modules.Packets;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIInteractType;
import com.Zrips.CMI.Modules.Holograms.FakeEntityInfo;
import com.Zrips.CMI.events.CMIPlayerFakeEntityInteractEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PacketHandler extends ChannelDuplexHandler {
  private Player p;
  static ConcurrentHashMap<UUID, HashMap<PacketHandler.packetNames, Long>> map =
      new ConcurrentHashMap<>();
  public static ConcurrentHashMap<Integer, FakeInfo> fakeEntities = new ConcurrentHashMap<>();
  public static ConcurrentHashMap<Integer, FakeEntityInfo> fakeEntities_v2 =
      new ConcurrentHashMap<>();
  public static ConcurrentHashMap<UUID, HashSet<Integer>> fakeEntitiesByPlayer =
      new ConcurrentHashMap<>();
  static Field entityIdField = null;
  static Field actionField = null;
  static Field entityIDAttackField = null;

  public PacketHandler(Player player) {
    this.p = player;
  }

  @Deprecated
  public static synchronized void addFakeEntity(FakeInfo fakeInfo) {
    fakeEntities.put(fakeInfo.getId(), fakeInfo);
    if (fakeInfo.getPlayer() != null) {
      var hashSet =
          fakeEntitiesByPlayer.getOrDefault(fakeInfo.getPlayer().getUniqueId(), new HashSet<>());
      hashSet.add(fakeInfo.getId());
      fakeEntitiesByPlayer.put(fakeInfo.getPlayer().getUniqueId(), hashSet);
    }
  }

  public static synchronized void addFakeEntity(FakeEntityInfo fakeEntityInfo) {
    fakeEntities_v2.put(fakeEntityInfo.getId(), fakeEntityInfo);
    if (fakeEntityInfo.getPlayerUUID() != null) {
      fakeEntitiesByPlayer
          .computeIfAbsent(fakeEntityInfo.getPlayerUUID(), uuid -> new HashSet<>())
          .add(fakeEntityInfo.getId());
    }
  }

  @Deprecated
  public static synchronized void clearFakeEntities() {
    for (FakeInfo fakeInfo : fakeEntities.values()) {
      if (fakeInfo.getPlayer() != null && fakeInfo.getPlayer().isOnline()) {
        CMI.getInstance().getNMS().removeHologram(fakeInfo.getPlayer(), fakeInfo.getId());
      }
    }

    fakeEntities.clear();
  }

  @Deprecated
  public static synchronized void removeFakeEntity(FakeInfo fakeInfo) {
    fakeEntities.remove(fakeInfo.getId());
    if (fakeInfo.getPlayer() != null) {
      var hashSet = fakeEntitiesByPlayer.get(fakeInfo.getPlayer().getUniqueId());
      if (hashSet != null) {
        hashSet.remove(fakeInfo.getId());
        if (hashSet.isEmpty()) {
          fakeEntitiesByPlayer.remove(fakeInfo.getPlayer().getUniqueId());
        }
      }
    }
  }

  public static synchronized void removeFakeEntity(FakeEntityInfo fakeEntityInfo) {
    fakeEntities.remove(fakeEntityInfo.getId());
    if (fakeEntityInfo.getPlayerUUID() != null) {
      var hashSet = fakeEntitiesByPlayer.get(fakeEntityInfo.getPlayerUUID());
      if (hashSet != null) {
        hashSet.remove(fakeEntityInfo.getId());
        if (hashSet.isEmpty()) {
          fakeEntitiesByPlayer.remove(fakeEntityInfo.getPlayerUUID());
        }
      }
    }
  }

  public static synchronized void clearCache(UUID uuid) {
    var hashSet = fakeEntitiesByPlayer.get(uuid);
    if (hashSet != null) {
      map.remove(uuid);

      for (Integer integer : hashSet) {
        fakeEntities.remove(integer);
        fakeEntities_v2.remove(integer);
      }

      fakeEntitiesByPlayer.remove(uuid);
    }
  }

  public void write(
      ChannelHandlerContext channelHandlerContext, Object object, ChannelPromise channelPromise) {
    super.write(channelHandlerContext, object, channelPromise);
  }

  public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) {
    CMIScheduler.runTaskAsynchronously(
        CMI.getInstance(),
        () -> {
          try {
            PacketHandler.packetNames packetNamesValue =
                PacketHandler.packetNames.get(object.getClass().getSimpleName());
            if (packetNamesValue != null) {
              switch (packetNamesValue) {
                case PacketPlayInUseEntity:
                case ServerboundInteractPacket:
                  this.processEntityInteractEvent(packetNamesValue, object);
                  break;
                case ServerboundAttackPacket:
                  this.processEntityAttackEvent(packetNamesValue, object);
                case PacketHandshakingInSetProtocol:
              }
            }
          } catch (Throwable throwable) {
            throwable.printStackTrace();
          }
        });
    super.channelRead(channelHandlerContext, object);
  }

  private void processEntityAttackEvent(PacketHandler.packetNames packetNamesValue, Object object) {
    if ((!fakeEntities.isEmpty() || !fakeEntities_v2.isEmpty()) && CMI.getInstance().isEnabled()) {
      try {
        Object valueSecond = packetNamesValue.getPacket().cast(object);
        if (entityIDAttackField == null) {
          try {
            entityIDAttackField = valueSecond.getClass().getDeclaredField("entityId");
          } catch (Throwable throwable) {
          }

          entityIDAttackField.setAccessible(true);
        }

        int value = (Integer) entityIDAttackField.get(valueSecond);
        this.registerClickEvent(packetNamesValue, value, CMIInteractType.LeftClick);
      } catch (Throwable throwable2) {
        throwable2.printStackTrace();
      }
    }
  }

  private void processEntityInteractEvent(
      PacketHandler.packetNames packetNamesValue, Object object) {
    if ((!fakeEntities.isEmpty() || !fakeEntities_v2.isEmpty()) && CMI.getInstance().isEnabled()) {
      if (!Version.isCurrentEqualOrHigher(Version.v26_1_0)) {
        this.processLegacyEntityInteractEvent(packetNamesValue, object);
      } else {
        CMIScheduler.runTaskAsynchronously(
            CMI.getInstance(),
            () -> {
              try {
                Object valueSecond = packetNamesValue.getPacket().cast(object);
                if (entityIdField == null) {
                  try {
                    entityIdField = valueSecond.getClass().getDeclaredField("entityId");
                  } catch (Throwable throwable) {
                  }

                  entityIdField.setAccessible(true);
                }

                int value = (Integer) entityIdField.get(valueSecond);
                this.registerClickEvent(packetNamesValue, value, CMIInteractType.RightClick);
              } catch (Throwable throwable2) {
                throwable2.printStackTrace();
              }
            });
      }
    }
  }

  private void processLegacyEntityInteractEvent(
      PacketHandler.packetNames packetNamesValue, Object object) {
    if ((!fakeEntities.isEmpty() || !fakeEntities_v2.isEmpty()) && CMI.getInstance().isEnabled()) {
      CMIScheduler.runTaskAsynchronously(
          CMI.getInstance(),
          () -> {
            try {
              Object valueSecond = packetNamesValue.getPacket().cast(object);
              if (entityIdField == null) {
                if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
                  try {
                    entityIdField = valueSecond.getClass().getDeclaredField("entityId");
                  } catch (Throwable throwable) {
                  }

                  if (entityIdField == null) {
                    entityIdField = valueSecond.getClass().getDeclaredField("b");
                  }
                } else {
                  entityIdField = valueSecond.getClass().getDeclaredField("a");
                }

                entityIdField.setAccessible(true);
              }

              if (actionField == null) {
                if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
                  if (Version.isPaperBranch()) {
                    try {
                      actionField = valueSecond.getClass().getDeclaredField("action");
                    } catch (Throwable throwable2) {
                    }
                  }

                  if (actionField == null) {
                    actionField = valueSecond.getClass().getDeclaredField("c");
                  }
                } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
                  actionField = valueSecond.getClass().getDeclaredField("b");
                } else {
                  actionField = valueSecond.getClass().getDeclaredField("action");
                }

                actionField.setAccessible(true);
              }

              int value = (Integer) entityIdField.get(valueSecond);
              String string = null;
              if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
                string = actionField.get(valueSecond).getClass().getSimpleName();
                string = string.isEmpty() ? "" : "Interact_at";
              } else {
                string = actionField.get(valueSecond).toString();
              }

              CMIInteractType cmiInteractType = CMIInteractType.LeftClick;
              if (string.equalsIgnoreCase("Interact_at")) {
                cmiInteractType = CMIInteractType.RightClick;
              }

              this.registerClickEvent(packetNamesValue, value, cmiInteractType);
            } catch (Throwable throwable3) {
              throwable3.printStackTrace();
            }
          });
    }
  }

  private Object getFakeEntityObject(int value) {
    FakeInfo fakeInfo = fakeEntities.get(value);
    if (fakeInfo != null) {
      return fakeInfo.getObj();
    }

    FakeEntityInfo fakeEntityInfo = fakeEntities_v2.get(value);
    return fakeEntityInfo != null ? fakeEntityInfo.getObject() : null;
  }

  private void registerClickEvent(
      PacketHandler.packetNames packetNamesValue, int value, CMIInteractType cmiInteractType) {
    CMIScheduler.runTask(
        CMI.getInstance(),
        () -> {
          var hashMap = map.get(this.p.getUniqueId());
          Object object = this.getFakeEntityObject(value);
          if (object != null) {
            if (hashMap != null) {
              Long time = (Long) hashMap.get(packetNamesValue);
              if (time == null || time + 100L < System.currentTimeMillis()) {
                CMIPlayerFakeEntityInteractEvent cmiPlayerFakeEntityInteractEvent =
                    new CMIPlayerFakeEntityInteractEvent(this.p, value, object, cmiInteractType);
                Bukkit.getServer().getPluginManager().callEvent(cmiPlayerFakeEntityInteractEvent);
                hashMap.put(packetNamesValue, System.currentTimeMillis());
                map.put(this.p.getUniqueId(), hashMap);
              }
            } else {
              CMIPlayerFakeEntityInteractEvent cmiPlayerFakeEntityInteractEventValue =
                  new CMIPlayerFakeEntityInteractEvent(this.p, value, object, cmiInteractType);
              Bukkit.getServer()
                  .getPluginManager()
                  .callEvent(cmiPlayerFakeEntityInteractEventValue);
              HashMap<com.Zrips.CMI.Modules.Packets.PacketHandler.packetNames, Long> mapSecond =
                  new HashMap();
              mapSecond.put(packetNamesValue, System.currentTimeMillis());
              map.put(this.p.getUniqueId(), mapSecond);
            }
          }
        });
  }

  private enum packetNames {
    PacketPlayInSetCreativeSlot,
    PacketPlayInUseEntity,
    ServerboundInteractPacket,
    ServerboundAttackPacket,
    PacketPlayOutPlayerInfo,
    PacketPlayOutEntityVelocity,
    PacketPlayOutEntityTeleport,
    PacketPlayOutLookAt,
    PacketPlayOutCamera,
    PacketPlayOutEntityHeadRotation,
    PacketStatusOutServerInfo,
    PacketPlayOutScoreboardTeam,
    PacketPlayOutChat,
    PacketHandshakingInSetProtocol;

    private Class<?> packet = null;

    public static PacketHandler.packetNames get(String string) {
      PacketHandler.packetNames[] packetNameses;
      int value = (packetNameses = values()).length;

      for (int count = 0; count < value; count++) {
        PacketHandler.packetNames packetNamesValue = packetNameses[count];
        if (packetNamesValue.toString().startsWith(string)) {
          return packetNamesValue;
        }
      }

      return null;
    }

    public Class<?> getPacket() {
      if (this.packet == null) {
        try {
          if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
            this.packet = Class.forName("net.minecraft.network.protocol.game." + this.toString());
          } else {
            this.packet =
                Class.forName(
                    "net.minecraft.server." + Version.getCurrent() + "." + this.toString());
          }
        } catch (ClassNotFoundException classNotFoundException) {
          classNotFoundException.printStackTrace();
        }
      }

      return this.packet;
    }
  }
}
