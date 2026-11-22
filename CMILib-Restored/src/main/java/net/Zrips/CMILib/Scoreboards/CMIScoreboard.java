package net.Zrips.CMILib.Scoreboards;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class CMIScoreboard {
  private static ConcurrentMap<UUID, ScoreboardInfo> timerMap = new ConcurrentHashMap<>();
  private static final String objName = "CMIScoreboard";
  private static Class<?> nmsChatSerializer;
  private static Class<?> nmsIChatBaseComponent;
  private static Class<?> scoreboardObjectiveClass = null;
  private static Class<?> packetPlayOutScoreboardObjectiveClass = null;
  private static Class<?> enumScoreboardHealthDisplayClass = null;
  private static Method chatSerializerA = null;
  private static Constructor<?> chatComponentTextConstructor = null;
  private static Class<?> packetPlayOutScoreboardDisplayObjective = null;
  private static Class<?> packetPlayOutScoreboardScore = null;
  private static Class<?> scoreboardScoreAction = null;
  private static Object healthDisplayValue = null;
  private static Object scoreActionValue = null;
  private static Class<?> chatComponentText = null;
  private static Constructor<?> scoreboardObjectiveConstructor;
  private static Constructor<?> playOutScoreboardDisplayObjectiveConstructor;

  private static void runScheduler() {
    for (Entry<UUID, ScoreboardInfo> map : timerMap.entrySet()) {
      if (System.currentTimeMillis()
          > map.getValue().getTime() + map.getValue().getKeepSeconds() * 1000L) {
        Player player = Bukkit.getPlayer(map.getKey());
        if (player != null) {
          removeScoreBoard(player);
          player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
          if (map.getValue().getObj() != null) {
            try {
              Objective obj =
                  player.getScoreboard().getObjective(map.getValue().getObj().getName());
              if (obj != null) {
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
              }
            } catch (IllegalStateException var4) {
            }
          }
        }

        timerMap.remove(map.getKey());
      }
    }

    if (timerMap.size() > 0) {
      CMIScheduler.get().runTaskLater(() -> runScheduler(), 20L);
    }
  }

  private static void addNew(Player player, int keepSeconds) {
    timerMap.put(
        player.getUniqueId(),
        new ScoreboardInfo(player.getScoreboard(), DisplaySlot.SIDEBAR, keepSeconds));
    runScheduler();
  }

  public static void removeScoreBoard(Player player) {
    try {
      if (Version.isMojangMappings()) {
        Object serialized =
            CMILib.getInstance().getReflectionManager().textToIChatBaseComponent("[\"\"]");
        Object sobj =
            getScoreboardObjectiveConstructor()
                .newInstance(null, "", null, serialized, null, false, null);
        Object packet =
            packetPlayOutScoreboardObjectiveClass
                .getConstructor(scoreboardObjectiveClass, int.class)
                .newInstance(sobj, 1);
        setField(packet, "objectiveName", player.getName());
        setField(
            packet, "renderType", enumScoreboardHealthDisplayClass.getField("HEARTS").get(null));
        sendPacket(player, packet);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
        Object serialized =
            CMILib.getInstance().getReflectionManager().textToIChatBaseComponent("[\"\"]");
        Object sobj =
            getScoreboardObjectiveConstructor()
                .newInstance(null, "", null, serialized, null, false, null);
        Object packet =
            packetPlayOutScoreboardObjectiveClass
                .getConstructor(scoreboardObjectiveClass, int.class)
                .newInstance(sobj, 1);
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          setField(packet, "e", player.getName());
          setField(packet, "g", enumScoreboardHealthDisplayClass.getField("b").get(null));
        } else {
          setField(packet, "d", player.getName());
          setField(packet, "f", enumScoreboardHealthDisplayClass.getField("b").get(null));
        }

        sendPacket(player, packet);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
        Object serialized = chatSerializerA.invoke(null, "[\"\"]");
        Object sobj =
            getScoreboardObjectiveConstructor().newInstance(null, "", null, serialized, null);
        Object packet =
            packetPlayOutScoreboardObjectiveClass
                .getConstructor(scoreboardObjectiveClass, int.class)
                .newInstance(sobj, 1);
        setField(packet, "d", player.getName());
        setField(packet, "f", enumScoreboardHealthDisplayClass.getField("b").get(null));
        sendPacket(player, packet);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        Object chatComponent = chatComponentTextConstructor.newInstance(CMIChatColor.translate(""));
        Object sobj =
            getScoreboardObjectiveConstructor().newInstance(null, "", null, chatComponent, null);
        Object packet =
            packetPlayOutScoreboardObjectiveClass
                .getConstructor(scoreboardObjectiveClass, int.class)
                .newInstance(sobj, 1);
        setField(packet, "d", player.getName());
        setField(packet, "f", enumScoreboardHealthDisplayClass.getField("b").get(null));
        sendPacket(player, packet);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
        Object packet = packetPlayOutScoreboardObjectiveClass.getConstructor().newInstance();
        setField(packet, "a", player.getName());
        setField(packet, "d", 1);
        sendPacket(player, packet);
      } else {
        Object boards = getNMSClass("Scoreboard").getConstructor().newInstance();
        Object baseCriteria =
            getNMSClass("ScoreboardBaseCriteria")
                .getConstructor(String.class)
                .newInstance("JobsDummy");
        Object obj =
            boards
                .getClass()
                .getMethod("registerObjective", String.class, getNMSClass("IScoreboardCriteria"))
                .invoke(boards, "JobsDummy", baseCriteria);
        Object packet =
            getNMSClass("PacketPlayOutScoreboardObjective")
                .getConstructor(obj.getClass(), int.class)
                .newInstance(obj, 1);
        sendPacket(player, packet);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void show(Player player, String displayName, List<String> lines, int keepSeconds) {
    CMIScheduler.get()
        .runTaskAsynchronously(
            () -> {
              setScoreBoard(player, displayName, lines);
              addNew(player, keepSeconds);
            });
  }

  private static Constructor<?> getScoreboardObjectiveConstructor() {
    if (scoreboardObjectiveConstructor != null) {
      return scoreboardObjectiveConstructor;
    }

    try {
      if (Version.isMojangMappings()) {
        scoreboardObjectiveConstructor =
            scoreboardObjectiveClass.getConstructor(
                Class.forName("net.minecraft.world.scores.Scoreboard"),
                String.class,
                Class.forName("net.minecraft.world.scores.criteria.ObjectiveCriteria"),
                nmsIChatBaseComponent,
                enumScoreboardHealthDisplayClass,
                boolean.class,
                Class.forName("net.minecraft.network.chat.numbers.NumberFormat"));
      } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
        scoreboardObjectiveConstructor =
            scoreboardObjectiveClass.getConstructor(
                Class.forName("net.minecraft.world.scores.Scoreboard"),
                String.class,
                Class.forName("net.minecraft.world.scores.criteria.IScoreboardCriteria"),
                nmsIChatBaseComponent,
                enumScoreboardHealthDisplayClass,
                boolean.class,
                Class.forName("net.minecraft.network.chat.numbers.NumberFormat"));
      } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        scoreboardObjectiveConstructor =
            scoreboardObjectiveClass.getConstructor(
                Class.forName("net.minecraft.world.scores.Scoreboard"),
                String.class,
                Class.forName("net.minecraft.world.scores.criteria.IScoreboardCriteria"),
                nmsIChatBaseComponent,
                enumScoreboardHealthDisplayClass);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return scoreboardObjectiveConstructor;
  }

  private static Constructor<?> getPlayOutScoreboardDisplayObjectiveConstructor() {
    if (playOutScoreboardDisplayObjectiveConstructor != null) {
      return playOutScoreboardDisplayObjectiveConstructor;
    }

    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
        playOutScoreboardDisplayObjectiveConstructor =
            packetPlayOutScoreboardDisplayObjective.getConstructor(
                Class.forName("net.minecraft.world.scores.DisplaySlot"), scoreboardObjectiveClass);
      } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        playOutScoreboardDisplayObjectiveConstructor =
            packetPlayOutScoreboardDisplayObjective.getConstructor(
                int.class, scoreboardObjectiveClass);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return playOutScoreboardDisplayObjectiveConstructor;
  }

  private static void setScoreBoard(Player player, String displayName, List<String> lines) {
    removeScoreBoard(player);

    try {
      if (Version.isMojangMappings()) {
        Object serialized =
            CMILib.getInstance()
                .getReflectionManager()
                .textToIChatBaseComponent("[\"" + CMIChatColor.translate(displayName) + "\"]");
        Object sobj =
            getScoreboardObjectiveConstructor()
                .newInstance(null, player.getName(), null, serialized, null, false, null);
        Object objectivePacket =
            packetPlayOutScoreboardObjectiveClass
                .getConstructor(scoreboardObjectiveClass, int.class)
                .newInstance(sobj, 0);
        setField(objectivePacket, "objectiveName", player.getName());
        setField(objectivePacket, "renderType", healthDisplayValue);
        sendPacket(player, objectivePacket);
        Object obj =
            getPlayOutScoreboardDisplayObjectiveConstructor()
                .newInstance(
                    Class.forName("net.minecraft.world.scores.DisplaySlot").getEnumConstants()[1],
                    sobj);
        Field field = packetPlayOutScoreboardDisplayObjective.getDeclaredField("objectiveName");
        field.setAccessible(true);
        field.set(obj, player.getName());
        sendPacket(player, obj);
        Constructor<?> scoreCons = null;
        Object prefixValue = Optional.ofNullable(null);
        Object suffixValue = Optional.ofNullable(null);
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          scoreCons =
              packetPlayOutScoreboardScore.getConstructor(
                  String.class, String.class, int.class, Optional.class, Optional.class);
        }

        if (scoreCons == null) {
          return;
        }

        for (int i = 0; i < 15 && i < lines.size(); i++) {
          Object scorePacket =
              scoreCons.newInstance(
                  CMIChatColor.translate(lines.get(i)),
                  player.getName(),
                  15 - i,
                  prefixValue,
                  suffixValue);
          sendPacket(player, scorePacket);
        }
      } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
        String playerNameFieldName = "e";
        String healthFieldName = "g";
        String objectiveFieldName = "c";
        Object serialized = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          playerNameFieldName = "e";
          healthFieldName = "g";
          objectiveFieldName = "c";
        } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          playerNameFieldName = "d";
          healthFieldName = "f";
          objectiveFieldName = "b";
        }

        if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
          serialized =
              CMILib.getInstance()
                  .getReflectionManager()
                  .textToIChatBaseComponent("[\"" + CMIChatColor.translate(displayName) + "\"]");
        } else if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
          serialized =
              nmsChatSerializer
                  .getMethod("a", String.class)
                  .invoke(null, "[\"" + CMIChatColor.translate(displayName) + "\"]");
        }

        Object sobj = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
          sobj =
              getScoreboardObjectiveConstructor()
                  .newInstance(null, player.getName(), null, serialized, null, false, null);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          sobj =
              getScoreboardObjectiveConstructor()
                  .newInstance(null, player.getName(), null, serialized, null);
        }

        Object objectivePacket =
            packetPlayOutScoreboardObjectiveClass
                .getConstructor(scoreboardObjectiveClass, int.class)
                .newInstance(sobj, 0);
        setField(objectivePacket, playerNameFieldName, player.getName());
        setField(objectivePacket, healthFieldName, healthDisplayValue);
        sendPacket(player, objectivePacket);
        Object obj = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R2)) {
          obj =
              getPlayOutScoreboardDisplayObjectiveConstructor()
                  .newInstance(
                      Class.forName("net.minecraft.world.scores.DisplaySlot").getEnumConstants()[1],
                      sobj);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          obj = getPlayOutScoreboardDisplayObjectiveConstructor().newInstance(1, sobj);
        }

        Field field = packetPlayOutScoreboardDisplayObjective.getDeclaredField(objectiveFieldName);
        field.setAccessible(true);
        field.set(obj, player.getName());
        sendPacket(player, obj);
        Constructor<?> scoreCons = null;
        Object prefixValue = null;
        Object suffixValue = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_20_R4)) {
          scoreCons =
              packetPlayOutScoreboardScore.getConstructor(
                  String.class, String.class, int.class, Optional.class, Optional.class);
          prefixValue = Optional.ofNullable(null);
          suffixValue = Optional.ofNullable(null);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
          scoreCons =
              packetPlayOutScoreboardScore.getConstructor(
                  String.class,
                  String.class,
                  int.class,
                  nmsIChatBaseComponent,
                  Class.forName("net.minecraft.network.chat.numbers.NumberFormat"));
        } else if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          scoreCons =
              packetPlayOutScoreboardScore.getConstructor(
                  scoreboardScoreAction, String.class, String.class, int.class);
        }

        if (scoreCons == null) {
          return;
        }

        for (int i = 0; i < 15 && i < lines.size(); i++) {
          Object scorePacket = null;
          if (Version.isCurrentEqualOrHigher(Version.v1_20_R3)) {
            scorePacket =
                scoreCons.newInstance(
                    CMIChatColor.translate(lines.get(i)),
                    player.getName(),
                    15 - i,
                    prefixValue,
                    suffixValue);
          } else {
            scorePacket = scoreCons.newInstance(scoreActionValue, player.getName(), null, 0);
            setField(scorePacket, "a", CMIChatColor.translate(lines.get(i)));
            setField(scorePacket, "c", 15 - i);
          }

          sendPacket(player, scorePacket);
        }
      } else if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
        Object objectivePacket =
            getNMSClass("PacketPlayOutScoreboardObjective").getConstructor().newInstance();
        setField(objectivePacket, "a", player.getName());
        setField(objectivePacket, "d", 0);
        setField(
            objectivePacket,
            "b",
            getNMSClass("ChatComponentText")
                .getConstructor(String.class)
                .newInstance(CMIChatColor.translate(displayName)));
        setField(
            objectivePacket,
            "c",
            getNMSClass("IScoreboardCriteria$EnumScoreboardHealthDisplay").getEnumConstants()[1]);
        sendPacket(player, objectivePacket);
        Object objectiveDataPacket =
            getNMSClass("PacketPlayOutScoreboardDisplayObjective").getConstructor().newInstance();
        setField(objectiveDataPacket, "a", 1);
        setField(objectiveDataPacket, "b", player.getName());
        sendPacket(player, objectiveDataPacket);

        for (int i = 0; i < 15 && i < lines.size(); i++) {
          Object PacketPlayOutScoreboardScore =
              getNMSClass("PacketPlayOutScoreboardScore").getConstructor().newInstance();
          setField(PacketPlayOutScoreboardScore, "a", CMIChatColor.translate(lines.get(i)));
          setField(PacketPlayOutScoreboardScore, "b", player.getName());
          setField(PacketPlayOutScoreboardScore, "c", 15 - i);
          setField(
              PacketPlayOutScoreboardScore,
              "d",
              getNMSClass("ScoreboardServer$Action").getEnumConstants()[0]);
          sendPacket(player, PacketPlayOutScoreboardScore);
        }
      } else {
        Object boards = getNMSClass("Scoreboard").getConstructor().newInstance();
        Object obj =
            boards
                .getClass()
                .getMethod("registerObjective", String.class, getNMSClass("IScoreboardCriteria"))
                .invoke(
                    boards,
                    "CMIScoreboard",
                    getNMSClass("ScoreboardBaseCriteria")
                        .getConstructor(String.class)
                        .newInstance("JobsDummy"));
        obj.getClass()
            .getMethod("setDisplayName", String.class)
            .invoke(obj, CMIChatColor.translate(displayName));
        sendPacket(
            player,
            getNMSClass("PacketPlayOutScoreboardObjective")
                .getConstructor(obj.getClass(), int.class)
                .newInstance(obj, 1));
        sendPacket(
            player,
            getNMSClass("PacketPlayOutScoreboardObjective")
                .getConstructor(obj.getClass(), int.class)
                .newInstance(obj, 0));
        sendPacket(
            player,
            getNMSClass("PacketPlayOutScoreboardDisplayObjective")
                .getConstructor(int.class, getNMSClass("ScoreboardObjective"))
                .newInstance(1, obj));

        for (int i = 0; i < 15 && i < lines.size(); i++) {
          Object scoreUpdatePacket =
              getNMSClass("ScoreboardScore")
                  .getConstructor(
                      getNMSClass("Scoreboard"), getNMSClass("ScoreboardObjective"), String.class)
                  .newInstance(boards, obj, CMIChatColor.translate(lines.get(i)));
          scoreUpdatePacket
              .getClass()
              .getMethod("setScore", int.class)
              .invoke(scoreUpdatePacket, 15 - i);
          sendPacket(
              player,
              getNMSClass("PacketPlayOutScoreboardScore")
                  .getConstructor(getNMSClass("ScoreboardScore"))
                  .newInstance(scoreUpdatePacket));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void setField(Object edit, String fieldName, Object value) {
    try {
      Field field = edit.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(edit, value);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  private static void sendPacket(Player player, Object packet) {
    try {
      CMILib.getInstance().getReflectionManager().sendPlayerPacket(player, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static Class<?> getNMSClass(String nmsClassString) throws ClassNotFoundException {
    return CMILib.getInstance().getReflectionManager().getMinecraftClass(nmsClassString);
  }

  static {
    if (Version.isCurrentEqualOrHigher(Version.v1_19_R1)) {
      if (Version.isMojangMappings()) {
        try {
          nmsIChatBaseComponent = Class.forName("net.minecraft.network.chat.Component");
          scoreboardObjectiveClass = Class.forName("net.minecraft.world.scores.Objective");
          packetPlayOutScoreboardObjectiveClass =
              Class.forName("net.minecraft.network.protocol.game.ClientboundSetObjectivePacket");
          enumScoreboardHealthDisplayClass =
              Class.forName("net.minecraft.world.scores.criteria.ObjectiveCriteria$RenderType");
        } catch (Throwable var10) {
        }

        try {
          packetPlayOutScoreboardDisplayObjective =
              Class.forName(
                  "net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket");
          packetPlayOutScoreboardScore =
              Class.forName("net.minecraft.network.protocol.game.ClientboundSetScorePacket");
        } catch (Throwable var9) {
        }

        try {
          scoreboardScoreAction = Class.forName("net.minecraft.world.scores.DisplaySlot");
          scoreActionValue = scoreboardScoreAction.getEnumConstants()[0];
        } catch (Throwable var8) {
        }

        try {
          healthDisplayValue = enumScoreboardHealthDisplayClass.getEnumConstants()[1];
        } catch (Throwable var7) {
        }
      } else {
        try {
          nmsIChatBaseComponent = Class.forName("net.minecraft.network.chat.IChatBaseComponent");
          scoreboardObjectiveClass =
              Class.forName("net.minecraft.world.scores.ScoreboardObjective");
          packetPlayOutScoreboardObjectiveClass =
              Class.forName("net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective");
          enumScoreboardHealthDisplayClass =
              Class.forName(
                  "net.minecraft.world.scores.criteria.IScoreboardCriteria$EnumScoreboardHealthDisplay");
        } catch (Throwable var6) {
        }

        try {
          chatComponentText = Class.forName("net.minecraft.network.chat.ChatComponentText");
          chatComponentTextConstructor = chatComponentText.getConstructor(String.class);
        } catch (Throwable var5) {
        }

        try {
          nmsChatSerializer =
              Class.forName("net.minecraft.network.chat.IChatBaseComponent$ChatSerializer");
          chatSerializerA = nmsChatSerializer.getMethod("a", String.class);
        } catch (Throwable var4) {
        }

        try {
          packetPlayOutScoreboardDisplayObjective =
              Class.forName(
                  "net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective");
          packetPlayOutScoreboardScore =
              Class.forName("net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore");
        } catch (Throwable var3) {
        }

        try {
          scoreboardScoreAction = Class.forName("net.minecraft.server.ScoreboardServer$Action");
          scoreActionValue = scoreboardScoreAction.getEnumConstants()[0];
        } catch (Throwable var2) {
        }

        try {
          healthDisplayValue = enumScoreboardHealthDisplayClass.getEnumConstants()[1];
        } catch (Throwable var1) {
        }
      }
    }

    scoreboardObjectiveConstructor = null;
    playOutScoreboardDisplayObjectiveConstructor = null;
  }
}
