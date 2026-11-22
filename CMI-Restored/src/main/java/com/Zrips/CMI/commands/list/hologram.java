package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.CMIHologramUpdateType;
import com.Zrips.CMI.Modules.Holograms.HologramGUIEditor;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.commands.CommandsHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.Chat.ChatEditorObject;
import net.Zrips.CMILib.Chat.ChatMessageListEdit;
import net.Zrips.CMILib.Chat.ChatMessageListEdit.ChatEditType;
import net.Zrips.CMILib.Chat.ChatMessageObjectEdit;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class hologram implements Cmd {
  private HashMap<UUID, CMIHologram> lastHolograms = new HashMap<>();

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get("base", "{gcp}Base");
    configReader.get("board", "{gcp}Board");
    configReader.get("icons", "{gcp}Icon");
    configReader.get("particle", "{gcp}Particle");
    configReader.get("interaction", "{gcp}Interaction");
    configReader.get("state", "{gcp}[state]");
    configReader.get("offset", "{gcs}Offset");
    configReader.get("scale", "{gcs}Scale");
    configReader.get("direction", "{gcs}Direction");
    configReader.get("material", "{gcp}Material: {gcs}[material]");
    configReader.get("light", "{gcp}Light levels Sky: {gcs}[sky] {gcp}Block: {gcs}[block]");
    configReader.get("reset", "{gcp}Press {gcs}Q {gcp}to reset");
    configReader.get("basicDirection", "{gcp}Pitch: {gcs}[x] {gcp}Yaw: {gcs}[y]");
    configReader.get(
        "fullDirection", "{gcp}Pitch: {gcs}[x] {gcp}Yaw: {gcs}[y] {gcp}Roll: {gcs}[z]");
    configReader.get("basicScale", "{gcp}Width: {gcs}[x] {gcp}Height: {gcs}[y]");
    configReader.get(
        "fullScale", "{gcp}Width: {gcs}[x] {gcp}Height: {gcs}[y] {gcp}Depth: {gcs}[z]");
    configReader.get("basicVector", "{gcp}X: {gcs}[x] {gcp}Y: {gcs}[y]");
    configReader.get("fullVector", "{gcp}X: {gcs}[x] {gcp}Y: {gcs}[y] {gcp}Z: {gcs}[z]");
    configReader.get("howTo", "&cTo create new hologram perform &e/cmi hologram new {gcs}[name]");
    configReader.get("gui", "{gcs}---- {gcp}<Open settings gui> {gcs}----");
    configReader.get("guiHover", "{gcp}Open hologram settings GUI");
    configReader.get("groupRelocate", "&7Relocate entire &f[group] &7group");
    configReader.get("groupCount", "&7Group contains &f[count] &7holograms");
    configReader.get("pageInterval", "{gcp}Change page every {gcs}[second] {gcp}seconds");
    configReader.get(
        "pageIntervalLore",
        Arrays.asList("{gcp}How often, in seconds, hologram page is changed automatically"));
    configReader.get("pageIntervalNoPages", "{gcd}This hologram doesn't have any pages yet");
    configReader.get("reqPermission", "{gcp}Requires permission to see: {gcs}[state]");
    configReader.get("teleport", "{gcp}Teleport to hologram location");
    configReader.get("range", "{gcp}Show range: {gcs}[blocks]");
    configReader.get(
        "rangeLore", Arrays.asList("{gcp}Defines area around hologram to show or hide it"));
    configReader.get("updateRange", "{gcp}Update range: {gcs}[blocks]");
    configReader.get(
        "updateRangeLore", Arrays.asList("{gcp}Defines area around hologram to trigger update"));
    configReader.get("relocate", "{gcp}Relocate");
    configReader.get(
        "relocateLore", Arrays.asList("{gcp}Relocates hologram to your current location"));
    configReader.get("holoYaw", "{gcp}Yaw: {gcs}[angle]");
    configReader.get("los", "{gcp}Consider line of sight: {gcs}[state]");
    configReader.get("interact", "{gcp}Interactable: {gcs}[state]");
    configReader.get("particles", "{gcp}Show particles: {gcs}[state]");
    configReader.get("seeThrough", "{gcp}See through: {gcs}[state]");
    configReader.get("seeThroughExtra", "{gcp}Text might not be visible while Board is enabled");
    configReader.get("commands", "{gcp}Commands:");
    configReader.get("hoverParticles", "{gcp}Show hover particles: {gcs}[state]");
    configReader.get("particleCount", "{gcp}Particles: {gcs}[count]");
    configReader.get("particleType", "{gcp}Particle type: {gcs}[type]");
    configReader.get("particleSpacing", "{gcp}Particle spacing: {gcs}[amount]");
    configReader.get("basePrefix", "{gcp}Base prefix: &r[prefix]X");
    configReader.get("hoverPrefix", "{gcp}Hover prefix: &r[prefix]X");
    configReader.get("locationRange", "{gcp}Modify location and range");
    configReader.get("interactions", "{gcp}Modify interactions");
    configReader.get("animations", "{gcp}Modify animations");
    configReader.get("displayMethod", "{gcp}Type: {gcs}[state]");
    configReader.get("enabled", "{gcp}Activated: {gcs}[state]");
    configReader.get("followType", "{gcp}Follow type: {gcs}[type]");
    configReader.get("doubleSided", "{gcp}Double sided: {gcs}[state]");
    configReader.get("autoRotate", "{gcp}Auto rotate: {gcs}[angle]");
    configReader.get("alignment", "{gcp}Text alignment: {gcs}[type]");
    configReader.get("shadow", "{gcp}Text shadow: {gcs}[state]");
    configReader.get("textAlpha", "{gcp}Text transparency: {gcs}[alpha]");
    configReader.get("textWidth", "{gcp}Text auto wrap width: {gcs}[width]");
    configReader.get("order", "{gcp}Downwards placement: {gcs}[state]");
    configReader.get("animationFadeIn", "{gcp}Fade In: {gcs}[value]");
    configReader.get("animationFadeOut", "{gcp}Fade Out: {gcs}[value]");
    configReader.get("bgColor", "{gcp}Background color: {gcs}[color]");
    configReader.get("bgAlpha", "{gcp}Background transparency: {gcs}[alpha]");
    configReader.get("textFiller", "{gcp}Text filler: {gcs}[width]");
    configReader.get("filler", " ");
    configReader.get("textFillerExplanation", "{gcp}Add %filler% to auto fill text");
    configReader.get("interval", "{gcp}Update every {gcs}[second] {gcp}seconds");
    configReader.get(
        "intervalLore",
        Arrays.asList(
            "{gcp}How often, in seconds, information on the hologram is updated",
            "{gcp}If text is not changing, set it to 0"));
    configReader.get("embeddedAutoButton", "{gcp}Enabled pagination buttons: [state]");
    configReader.get("autoPage", "<T><<<<  </T><C>[prevPage]</C><T>  >>>></T><C>[nextPage]</C>");
    configReader.get("moveX", "{gcp}X: {gcs}[x]");
    configReader.get("moveY", "{gcp}Y: {gcs}[y]");
    configReader.get("moveZ", "{gcp}Z: {gcs}[z]");
    configReader.get("listFormat", "{gcp}[name]{gcp}: &r[extra]");
  }

  @CAnnotation(
      info = "Manage holograms",
      multiTab = {
        "new,reload",
        "addline,deleteline,editline,info,update,gui [holograms]",
        "aimlines,aimgui,lastlines,lastgui"
      },
      regVar = -66,
      consoleVar = -66,
      modules = "holograms",
      others = false,
      indevelopment = true)
  @Override
  public Boolean perform(final CMI cmi, final CommandSender commandSender, String[] strings) {
    if (Version.isCurrentLower(Version.v1_8_R1)) {
      commandSender.sendMessage(ChatColor.RED + "Only for 1.8+ servers");
      return false;
    }

    Player player = null;
    if (commandSender instanceof Player) {
      player = (Player) commandSender;
    }

    hologram.Action action = null;
    int value = -1;
    String string = null;
    boolean flag = false;
    String[] argsCopy = strings;
    int argCount = strings.length;

    for (int argIndex = 0; argIndex < argCount; argIndex++) {
      String arg = argsCopy[argIndex];
      if (action == null) {
        action = hologram.Action.getByName(arg);
        if (action != null) {
          continue;
        }
      }

      if (value <= -1 && arg.startsWith("-confirmed")) {
        flag = true;
      } else {
        if (value <= -1 && arg.startsWith("-p:")) {
          try {
            value = Integer.parseInt(arg.substring("-p:".length()));
            continue;
          } catch (Exception exception) {
          }
        }

        if (string == null) {
          string = arg;
        }
      }
    }

    CMIHologram cmiHologram = cmi.getHologramManager().getByName(string);
    if (cmiHologram != null) {
      if (action == null) {
        action = hologram.Action.info;
      }

      switch (action) {
        case gui:
          HologramGUIEditor.openGui(player, cmiHologram);
          return true;
        case list:
        default:
          break;
        case delete:
          if (!flag) {
            RawMessage rawMessage = new RawMessage();
            rawMessage.addText(
                LC.info_ClickToConfirmDelete.getLocale(
                    new Object[] {"[name]", cmiHologram.getName()}));
            rawMessage.addHover(LC.info_Click.getLocale(new Object[0]));
            rawMessage.addCommand(
                CommandsHandler.getLabel()
                    + " "
                    + hologram.class.getSimpleName()
                    + " delete "
                    + cmiHologram.getName()
                    + " -confirmed");
            rawMessage.show(commandSender);
            return null;
          }

          cmi.getHologramManager().remove(cmiHologram);
          cmi.performCommand(
              commandSender, CommandsHandler.getLabel() + " " + hologram.class.getSimpleName());
          return true;
      }
    }

    if (action != null && action == hologram.Action.reload) {
      cmi.getHologramManager().reload();
      return true;
    }

    if (action == null) {
      action = hologram.Action.list;
    }

    switch (action) {
      case update:
        if (string == null) {
          LC.info_wrongName.sendMessage(commandSender, new Object[0]);
          return null;
        } else {
          CMIHologram hologramA = cmi.getHologramManager().getByName(string);
          if (hologramA != null && hologramA.getWorld() != null) {
            hologramA.recheckPermissionAccess();
            hologramA.requestFullUpdate();
            return true;
          }

          LC.info_wrongName.sendMessage(commandSender, new Object[0]);
          return null;
        }
      case editline:
        if (string == null) {
          LC.info_wrongName.sendMessage(commandSender, new Object[0]);
          return null;
        } else {
          Integer integer = null;

          try {
            integer = Integer.parseInt(strings[2]);
          } catch (Throwable throwable) {
            LC.info_UseInteger.sendMessage(commandSender, new Object[0]);
            return null;
          }

          integer = integer - 1;
          if (integer < 0) {
            integer = 0;
          }

          CMIHologram hologramB = cmi.getHologramManager().getByName(string);
          if (hologramB != null && hologramB.getWorld() != null) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int lineStart = 3; lineStart < strings.length; lineStart++) {
              if (!stringBuilder.toString().isEmpty()) {
                stringBuilder.append(" ");
              }

              stringBuilder.append(strings[lineStart]);
            }

            hologramB.getPages().setLine(integer, stringBuilder.toString());
            hologramB.requestFullUpdate();
            cmi.getHologramManager().save();
            showHologramInfo(commandSender, hologramB, 1);
            return true;
          }

          LC.info_wrongName.sendMessage(commandSender, new Object[0]);
          return null;
        }
      case moveup:
      case movedown:
      case gui:
      case delete:
      case reload:
      default:
        CMILC.info(this, commandSender, "howTo");
        return null;
      case createnew:
        if (string == null) {
          LC.info_wrongName.sendMessage(commandSender, new Object[0]);
          return null;
        } else {
          if (player == null) {
            return false;
          }

          CMIHologram hologramC = new CMIHologram(string, player.getEyeLocation().clone());
          hologramC.getSettings().setSaveToFile(true);
          cmi.getHologramManager().add(hologramC);
          cmi.getHologramManager().save();
          showHologramInfo(commandSender, hologramC, 1);
          return true;
        }
      case addline:
        if (string == null) {
          LC.info_wrongName.sendMessage(commandSender, new Object[0]);
          return null;
        } else {
          StringBuilder builder = new StringBuilder();

          for (int lineIndex = 2; lineIndex < strings.length; lineIndex++) {
            if (!builder.toString().isEmpty()) {
              builder.append(" ");
            }

            builder.append(strings[lineIndex]);
          }

          CMIHologram hologramD = cmi.getHologramManager().getByName(string);
          if (hologramD != null && hologramD.getWorld() != null) {
            hologramD.getPages().addLine(builder.toString());
            cmi.getHologramManager().save();
            showHologramInfo(commandSender, hologramD, 1);
            return true;
          }

          LC.info_wrongName.sendMessage(commandSender, new Object[0]);
          return null;
        }
      case info:
        CMIHologram hologramE =
            CMI.getInstance()
                .getHologramManager()
                .getByName(strings.length == 1 ? strings[0] : strings[1]);
        if (hologramE == null) {
          return false;
        }

        showHologramInfo(commandSender, hologramE, value);
        return true;
      case deleteLine:
        if (string == null) {
          LC.info_wrongName.sendMessage(commandSender, new Object[0]);
          return null;
        } else {
          Integer delay = null;

          try {
            delay = Integer.parseInt(strings[2]);
          } catch (Throwable throwable2) {
            LC.info_UseInteger.sendMessage(commandSender, new Object[0]);
            return null;
          }

          CMIHologram hologramF = cmi.getHologramManager().getByName(string);
          if (hologramF != null && hologramF.getWorld() != null) {
            try {
              hologramF.getPages().removeLine(delay);
            } catch (Throwable throwable3) {
              LC.info_UseInteger.sendMessage(commandSender, new Object[0]);
              return null;
            }

            hologramF.requestUpdate(CMIHologramUpdateType.Text);
            cmi.getHologramManager().save();
            showHologramInfo(commandSender, hologramF, 1);
            return true;
          }

          LC.info_wrongName.sendMessage(commandSender, new Object[0]);
          return null;
        }
      case list:
        Location location = new Location((World) Bukkit.getWorlds().get(0), 0.0, 0.0, 0.0);
        if (player != null) {
          location = player.getLocation();
        }

        var list = cmi.getHologramManager().getHologramsSortedByDistance(location);
        if (value < 1) {
          value = 1;
        }

        PageInfo pageInfo = new PageInfo(10, list.size(), value);
        LC.info_Spliter.sendMessage(commandSender, new Object[0]);
        ChatMessageObjectEdit chatMessageObjectEdit =
            new ChatMessageObjectEdit(commandSender, pageInfo) {
              public void newAdd(String newName) {
                cmi.performCommand(commandSender, hologram.class, "new " + newName);
              }
            };
        int index = 0;

        for (; index < list.size(); index++) {
          if (pageInfo.isEntryOk()) {
            final CMIHologram listedHologram = (CMIHologram) list.get(index);
            String previewText = "";

            for (String pageLine : listedHologram.getPages().getLines()) {
              if (pageLine != null) {
                previewText = pageLine;
                break;
              }
            }

            if (previewText.length() > 32) {
              previewText = previewText.substring(0, 32) + "..";
            }

            previewText = CMIChatColor.stripColor(previewText);
            ChatEditorObject chatEditorObject =
                new ChatEditorObject(
                    CMILC.getIM(
                        this,
                        "listFormat",
                        "[name]",
                        listedHologram.getName(),
                        "[extra]",
                        previewText)) {
                  public void onDelete() {
                    cmi.getHologramManager().remove(listedHologram);
                    cmi.performCommand(
                        commandSender,
                        CommandsHandler.getLabel() + " " + hologram.class.getSimpleName());
                  }

                  public void onClick() {
                    hologram.showHologramInfo(commandSender, listedHologram, 1);
                  }
                };
            chatEditorObject.setHover(
                LC.modify_editSymbolHover.getLocale(
                    new Object[] {"[text]", listedHologram.getName()}));
            chatMessageObjectEdit.addline(chatEditorObject);
          }
        }

        chatMessageObjectEdit.print();
        pageInfo.autoPagination(
            commandSender,
            CommandsHandler.getLabel() + " " + hologram.class.getSimpleName() + " list",
            "-p:");
        new PageInfo(10, list.size(), value);
        return true;
      case editCommands:
        cmiHologram = cmi.getHologramManager().getByName(strings[1]);
        startEditingCommands(commandSender, cmiHologram);
        return true;
      case aimlines:
        if (player == null) {
          return false;
        }

        UUID uuid = player.getUniqueId();
        var completableFuture = cmi.getHologramManager().getLookingAt(uuid);
        completableFuture.thenAccept(
            object -> {
              if (object != null) {
                showHologramInfo(commandSender, object, 1);
                this.lastHolograms.put(uuid, object);
              }
            });
        return true;
      case aimgui:
        if (player == null) {
          return false;
        }

        UUID playerUuid = player.getUniqueId();
        var lookingAtFuture = cmi.getHologramManager().getLookingAt(playerUuid);
        lookingAtFuture.thenAccept(
            lookedAt -> {
              if (lookedAt != null) {
                CMIScheduler.runTask(
                    cmi, () -> HologramGUIEditor.openGui((Player) commandSender, lookedAt));
                this.lastHolograms.put(playerUuid, lookedAt);
              }
            });
        return true;
      case lastlines:
        if (player == null) {
          return false;
        } else {
          CMIHologram lastHologram = this.lastHolograms.get(player.getUniqueId());
          if (lastHologram == null) {
            return false;
          }

          return showHologramInfo(commandSender, lastHologram, 1);
        }
      case lastgui:
        if (player == null) {
          return false;
        } else {
          CMIHologram lastHologram = this.lastHolograms.get(player.getUniqueId());
          if (lastHologram == null) {
            return false;
          } else {
            HologramGUIEditor.openGui(player, lastHologram);
            return true;
          }
        }
    }
  }

  public static boolean startEditingCommands(CommandSender commandSender, CMIHologram cmiHologram) {
    if (cmiHologram == null) {
      return false;
    }

    RawMessage rawMessage = new RawMessage();
    rawMessage.addText(LC.info_Spliter.getLocale(new Object[0]));
    ChatMessageListEdit chatMessageListEdit =
        new ChatMessageListEdit(
            commandSender,
            cmiHologram.getInteractionSettings().getCommands(),
            ChatEditType.String) {
          public void onUpdate() {
            CMI.getInstance().getHologramManager().save();
          }
        };
    chatMessageListEdit.setTopLine(rawMessage);
    chatMessageListEdit.print();
    return true;
  }

  public static boolean showHologramInfo(
      final CommandSender commandSender, final CMIHologram cmiHologram, int value) {
    if (cmiHologram == null) {
      return false;
    }

    RawMessage rawMessage = new RawMessage();
    rawMessage.addText(CMILC.getIM(hologram.class, "gui"));
    rawMessage.addHover(CMILC.getIM(hologram.class, "guiHover"));
    rawMessage.addCommand(
        CommandsHandler.getLabel()
            + " "
            + hologram.class.getSimpleName()
            + " gui "
            + cmiHologram.getName());
    final PageInfo pageInfo =
        new PageInfo(10, cmiHologram.getPages().getLines().size(), value) {
          public Boolean pageChange(int page) {
            hologram.showHologramInfo(commandSender, cmiHologram, page);
            return true;
          }
        };
    pageInfo.setPageChangeCommand("");
    ChatMessageListEdit chatMessageListEdit =
        new ChatMessageListEdit(
            commandSender, cmiHologram.getPages().getLines(), ChatEditType.String) {
          public void onUpdate() {
            cmiHologram.getPages().recheckLines();
            cmiHologram.requestUpdate(
                CMIHologramUpdateType.Text,
                CMIHologramUpdateType.Billboard,
                CMIHologramUpdateType.Icon,
                CMIHologramUpdateType.ShowIcon,
                CMIHologramUpdateType.Location);
            CMI.getInstance().getHologramManager().save();
            pageInfo.setTotalEntries(cmiHologram.getPages().getLines().size());
          }
        };
    chatMessageListEdit.setPageInfo(pageInfo);
    chatMessageListEdit.setFlatenLines(true);
    chatMessageListEdit.setShortenLines(40);
    chatMessageListEdit.setTopLine(rawMessage);
    chatMessageListEdit.print();
    return true;
  }

  private enum Action {
    update,
    editline,
    moveup,
    movedown,
    createnew,
    addline,
    info,
    deleteLine,
    gui,
    list,
    delete,
    reload,
    editCommands,
    aimlines,
    aimgui,
    lastlines,
    lastgui;

    public static hologram.Action getByName(String string) {
      if (string.equalsIgnoreCase("new")) {
        return createnew;
      }

      hologram.Action[] actions;
      int value = (actions = values()).length;

      for (int actionIndex = 0; actionIndex < value; actionIndex++) {
        hologram.Action action = actions[actionIndex];
        if (action.name().equalsIgnoreCase(string)) {
          return action;
        }
      }

      return null;
    }
  }
}
