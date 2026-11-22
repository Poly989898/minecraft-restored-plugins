package com.Zrips.CMI.Modules.TabList;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Components.CMIComponentPaper;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIPlayerConnection;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabListHeaderFooterHandler {
  private Class<?> nmsChatSerializer;
  private Class<?> IChatBaseComponent;
  private Class<?> packetType;
  private CMI plugin;

  public TabListHeaderFooterHandler(CMI cmi) {
    this.plugin = cmi;
    if (Version.getCurrent().isHigher(Version.v1_7_R4)) {
      if (!Version.isMojangMappings()) {
        if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          try {
            this.packetType =
                Class.forName(
                    "net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter");
            this.IChatBaseComponent =
                Class.forName("net.minecraft.network.chat.IChatBaseComponent");
          } catch (Throwable throwable) {
            throwable.printStackTrace();
          }
        } else {
          try {
            this.packetType = Class.forName(this.getPacketPlayOutPlayerListHeaderFooter());
            this.nmsChatSerializer = Class.forName(this.getChatSerializerClasspath());
          } catch (Exception exception) {
            Bukkit.getLogger().log(Level.SEVERE, "Error {0}", exception);
          }
        }
      }
    }
  }

  public void send(Player player, List<String> list, List<String> footer) {
    if (list == null) {
      list = new ArrayList();
    }

    if (footer == null) {
      footer = new ArrayList();
    }

    StringBuilder stringBuilder = new StringBuilder();
    StringBuilder builder = new StringBuilder();

    for (int value = 0; value < list.size(); value++) {
      String string = (String) list.get(value);
      if (value > 0) {
        stringBuilder.append("\n");
      }

      if (!string.equalsIgnoreCase(" ")) {
        stringBuilder.append(string);
      }
    }

    for (int index = 0; index < footer.size(); index++) {
      String line = (String) footer.get(index);
      if (index > 0) {
        builder.append("\n");
      }

      if (!line.equalsIgnoreCase(" ")) {
        builder.append(line);
      }
    }

    this.send(player, stringBuilder.toString(), builder.toString());
  }

  public void send(Player player, String string, String footer) {
    if (player.isOnline()) {
      if (string == null) {
        string = "";
      }

      if (footer == null) {
        footer = "";
      }

      string = string.replace("\\n", "%&n&%");
      footer = footer.replace("\\n", "%&n&%");
      string = string.replace("\\", "\\\\");
      footer = footer.replace("\\", "\\\\");
      string = string.replace("%&n&%", "\\n");
      footer = footer.replace("%&n&%", "\\n");

      try {
        if (!Version.getCurrent().isHigher(Version.v1_7_R4)) {
          return;
        }

        if (Version.isMojangMappings()) {
          if (Version.isPaperBranch()) {
            CMIComponentPaper cmiComponentPaper = new CMIComponentPaper(false);
            cmiComponentPaper.add(CMIComponentPaper.quickCreate(string));
            player.sendPlayerListHeader(cmiComponentPaper.getCombined());
            cmiComponentPaper = new CMIComponentPaper(false);
            cmiComponentPaper.add(CMIComponentPaper.quickCreate(footer));
            player.sendPlayerListFooter(cmiComponentPaper.getCombined());
          } else {
            player.setPlayerListHeaderFooter(
                CMIChatColor.translate(string), CMIChatColor.translate(footer));
          }

          return;
        }

        if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
          RawMessage rawMessage = new RawMessage();
          rawMessage.addText(string);
          Object object =
              CMILib.getInstance()
                  .getReflectionManager()
                  .textToIChatBaseComponent(CMIChatColor.translate(rawMessage.getRaw()));
          rawMessage = new RawMessage();
          rawMessage.addText(footer);
          Object packetObject =
              CMILib.getInstance()
                  .getReflectionManager()
                  .textToIChatBaseComponent(CMIChatColor.translate(rawMessage.getRaw()));
          Object packetObjectAlt =
              this.packetType
                  .getConstructor(this.IChatBaseComponent, this.IChatBaseComponent)
                  .newInstance(object, packetObject);
          if (!string.isEmpty() || !footer.isEmpty()) {
            this.sendPacket(player, packetObjectAlt);
          }

          return;
        }

        if (this.nmsChatSerializer == null) {
          return;
        }

        Object headerObject = this.packetType.newInstance();
        RawMessage message = new RawMessage();
        message.addText(string);
        Object footerObject =
            this.nmsChatSerializer
                .getMethod("a", String.class)
                .invoke(null, CMIChatColor.translate(message.getRaw()));
        Field field = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_13_R2)) {
          field = headerObject.getClass().getDeclaredField("header");
        } else {
          field = headerObject.getClass().getDeclaredField("a");
        }

        field.setAccessible(true);
        field.set(headerObject, footerObject);
        field.setAccessible(!field.isAccessible());
        message = new RawMessage();
        message.addText(footer);
        Object footerComponent =
            this.nmsChatSerializer
                .getMethod("a", String.class)
                .invoke(null, CMIChatColor.translate(message.getRaw()));
        Field footerField = null;
        if (Version.isCurrentEqualOrHigher(Version.v1_13_R2)) {
          footerField = headerObject.getClass().getDeclaredField("footer");
        } else {
          footerField = headerObject.getClass().getDeclaredField("b");
        }

        footerField.setAccessible(true);
        footerField.set(headerObject, footerComponent);
        footerField.setAccessible(!footerField.isAccessible());
        if (!string.isEmpty() || !footer.isEmpty()) {
          this.sendPacket(player, headerObject);
        }
      } catch (Exception exception) {
        Bukkit.getLogger().log(Level.SEVERE, "Error ", exception);
      }
    }
  }

  private void sendPacket(Player player, Object object) {
    CMIPlayerConnection.sendPacket(player, object);
  }

  private String getChatSerializerClasspath() {
    return !Version.isCurrentHigher(Version.v1_8_R2)
        ? "net.minecraft.server." + Version.getCurrent() + ".ChatSerializer"
        : "net.minecraft.server." + Version.getCurrent() + ".IChatBaseComponent$ChatSerializer";
  }

  private String getPacketPlayOutPlayerListHeaderFooter() {
    return "net.minecraft.server." + Version.getCurrent() + ".PacketPlayOutPlayerListHeaderFooter";
  }
}
