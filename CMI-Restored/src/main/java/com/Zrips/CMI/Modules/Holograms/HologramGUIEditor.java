package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Display.CMIBillboard;
import com.Zrips.CMI.Modules.Display.CMITextAlignment;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramAnimationSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramBoardSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramIconSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramPageSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramTextSettings;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Selection.SelectionManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.commands.list.hologram;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Chat.ChatMessageEdit;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.CMIVector2D;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Container.MaterialPicker;
import net.Zrips.CMILib.Effects.CMIEffectPicker;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class HologramGUIEditor {
  private static void finalizeClick(Player player, CMIHologram cmiHologram) {
    updateAndSave(cmiHologram);
    openGui(player, cmiHologram);
  }

  private static void finalizeInteractions(Player player, CMIHologram cmiHologram) {
    updateAndSave(cmiHologram);
    editInteractions(player, cmiHologram);
  }

  private static void finalizeRanges(Player player, CMIHologram cmiHologram) {
    updateAndSave(cmiHologram);
    editLocation(player, cmiHologram);
  }

  private static void updateAndSave(
      CMIHologram cmiHologram, CMIGui cmiGui, CMIGuiButton cmiGuiButton) {
    updateAndSave(cmiHologram);
    cmiGuiButton.updateLooks();
    cmiGuiButton.update(cmiGui);
  }

  private static void updateAndSave(CMIHologram cmiHologram) {
    cmiHologram.requestUpdate(
        CMIHologramUpdateType.Billboard,
        CMIHologramUpdateType.Text,
        CMIHologramUpdateType.InstantShow,
        CMIHologramUpdateType.ShowIcon,
        CMIHologramUpdateType.Location);
    CMI.getInstance().getHologramManager().save();
  }

  public static void openGui(final Player player, final CMIHologram cmiHologram) {
    final CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            CMI.getInstance().getHologramManager().save();
          }
        };
    cmiGui.setTitle(cmiHologram.getName());
    cmiGui.setInvSize(6);
    final CMIHologramSettings cmiHologramSettings = cmiHologram.getSettings();
    final CMIHologramTextSettings cmiHologramTextSettings = cmiHologram.getTextSettings();
    CMIHologramPageSettings cmiHologramPageSettings = cmiHologram.getPageSettings();
    CMIHologramType cmiHologramType = cmiHologram.getType();
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(
            4,
            cmiHologram.getType() == CMIHologramType.TextDisplay
                ? CMIMaterial.ITEM_FRAME.newItemStack()
                : CMIMaterial.ARMOR_STAND.newItemStack()) {
          public void click(GUIClickType clickType) {
            var completableFuture = cmiHologram.instantHide();
            completableFuture.whenComplete(
                (object, error) -> {
                  if (cmiHologram.getType() == CMIHologramType.TextDisplay) {
                    cmiHologram.setType(CMIHologramType.ArmorStand);
                  } else {
                    cmiHologram.setType(CMIHologramType.TextDisplay);
                  }

                  cmiHologram
                      .requestFullUpdate()
                      .whenComplete(
                          (result, throwable) -> {
                            if (throwable != null) {
                              throwable.printStackTrace();
                            }

                            HologramGUIEditor.openGui(player, cmiHologram);
                            CMI.getInstance().getHologramManager().save();
                          });
                });
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM(hologram.class, "displayMethod", "[state]", cmiHologram.getType().toString()));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            8,
            cmiHologram.isEnabled()
                ? CMIMaterial.GREEN_WOOL.newItemStack()
                : CMIMaterial.YELLOW_WOOL.newItemStack()) {
          public void click(GUIClickType clickType) {
            if (cmiHologram.isEnabled()) {
              cmiHologram.disable();
            } else {
              cmiHologram.enable();
            }

            HologramGUIEditor.openGui(player, cmiHologram);
            cmiHologram.recheckScheduler();
            CMI.getInstance().getHologramManager().save();
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM(hologram.class, "enabled", "[state]", cmiHologram.isEnabled()));
    cmiGui.addButton(cmiGuiButton);
    backgroundButtons(cmiGui, cmiHologram);
    textButtons(cmiGui, cmiHologram);
    baseButtons(cmiGui, cmiHologram);
    boardButtons(cmiGui, cmiHologram);
    iconButtons(cmiGui, cmiHologram);
    cmiGuiButton =
        new CMIGuiButton(34, CMIMaterial.CARROT_ON_A_STICK) {
          public void click(GUIClickType clickType) {
            HologramGUIEditor.editAnimations(player, cmiHologram);
          }
        };
    cmiGuiButton.setName(CMILC.getIM(hologram.class, "animations"));
    cmiGui.addButton(cmiGuiButton);
    if (cmiHologramType == CMIHologramType.TextDisplay) {
      cmiGuiButton =
          new CMIGuiButton(0, CMIMaterial.ACACIA_LEAVES.newItemStack()) {
            public void click(GUIClickType clickType) {
              switch (cmiHologramSettings.getBillboard()) {
                case VERTICAL:
                  cmiHologramSettings.setBillboard(CMIBillboard.HORIZONTAL);
                  break;
                case CENTER:
                default:
                  cmiHologramSettings.setBillboard(CMIBillboard.VERTICAL);
                  break;
                case FIXED:
                  cmiHologramSettings.setBillboard(CMIBillboard.CENTER);
                  break;
                case HORIZONTAL:
                  cmiHologramSettings.setBillboard(CMIBillboard.FIXED);
              }

              cmiHologram.resetCache();
              HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
              HologramGUIEditor.openGui(player, cmiHologram);
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      hologram.class,
                      "followType",
                      "[type]",
                      CMIText.firstToUpperCase(cmiHologramSettings.getBillboard().toString())));
            }
          };
      cmiGui.addButton(cmiGuiButton);
      if (cmiHologramSettings.getBillboard().isCanHaveBackSide()) {
        cmiGuiButton =
            new CMIGuiButton(1, CMIMaterial.BLACK_BANNER.newItemStack()) {
              public void click(GUIClickType clickType) {
                cmiHologramTextSettings.setDoubleSided(!cmiHologramTextSettings.isDoubleSided());
                cmiHologram.resetCache();
                HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
              }

              public void updateLooks() {
                this.setName(
                    CMILC.getIM(
                        hologram.class,
                        "doubleSided",
                        "[state]",
                        cmiHologramTextSettings.isDoubleSided()));
              }
            };
        cmiGui.addButton(cmiGuiButton);
      }
    }

    if (cmiHologramType != CMIHologramType.TextDisplay) {
      cmiGuiButton =
          new CMIGuiButton(51, CMIMaterial.ENDER_EYE.newItemStack()) {
            public void click(GUIClickType clickType) {
              cmiHologramSettings.setCheckLineOfSight(!cmiHologramSettings.isCheckLineOfSight());
              HologramGUIEditor.finalizeClick(player, cmiHologram);
              cmiHologram.recheckScheduler();
            }
          };
      cmiGuiButton.setName(
          CMILC.getIM(hologram.class, "los", "[state]", cmiHologramSettings.isCheckLineOfSight()));
      cmiGui.addButton(cmiGuiButton);
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_9_R2)) {
      cmiGuiButton =
          new CMIGuiButton(44, CMIMaterial.STICK) {
            public void click(GUIClickType clickType) {
              HologramGUIEditor.editInteractions(player, cmiHologram);
            }
          };
      cmiGuiButton.setName(CMILC.getIM(hologram.class, "interactions"));
      cmiGui.addButton(cmiGuiButton);
    }

    cmiGuiButton =
        new CMIGuiButton(26, CMIMaterial.COMPASS) {
          public void click(GUIClickType clickType) {
            HologramGUIEditor.editLocation(player, cmiHologram);
          }
        };
    cmiGuiButton.setName(CMILC.getIM(hologram.class, "locationRange"));
    cmiGui.addButton(cmiGuiButton);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  private static Object[] expandArgs(Object... objects) {
    ArrayList arrayList = new ArrayList();
    Object[] formatArgs = objects;
    int value = objects.length;

    for (int index = 0; index < value; index++) {
      Object object = formatArgs[index];
      if (object instanceof Vector) {
        Vector vector = (Vector) object;
        arrayList.add("[x]");
        arrayList.add(String.format("%.2f", vector.getX()));
        arrayList.add("[y]");
        arrayList.add(String.format("%.2f", vector.getY()));
        arrayList.add("[z]");
        arrayList.add(String.format("%.2f", vector.getZ()));
      } else if (object instanceof CMIVector3D) {
        CMIVector3D cmiVector3D = (CMIVector3D) object;
        arrayList.add("[x]");
        arrayList.add(String.format("%.2f", cmiVector3D.getX()));
        arrayList.add("[y]");
        arrayList.add(String.format("%.2f", cmiVector3D.getY()));
        arrayList.add("[z]");
        arrayList.add(String.format("%.2f", cmiVector3D.getZ()));
      } else if (object instanceof CMIVector2D) {
        CMIVector2D cmiVector2D = (CMIVector2D) object;
        arrayList.add("[x]");
        arrayList.add(String.format("%.2f", cmiVector2D.getX()));
        arrayList.add("[y]");
        arrayList.add(String.format("%.2f", cmiVector2D.getY()));
      } else {
        arrayList.add(object);
      }
    }

    return arrayList.toArray();
  }

  public static String formatName(String string, String section, String key, Object... objects) {
    objects = expandArgs(objects);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(CMILC.getIM(hologram.class, string));
    if (section != null && !section.isEmpty()) {
      stringBuilder.append(" ").append(CMILC.getIM(hologram.class, section));
    }

    if (key != null && !key.isEmpty()) {
      String localized = CMILC.getIM(hologram.class, key, objects);
      stringBuilder.append(" ").append(localized);
    }

    return stringBuilder.toString();
  }

  public static void editAnimations(final Player player, final CMIHologram cmiHologram) {
    CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            CMI.getInstance().getHologramManager().save();
          }
        };
    cmiGui.setTitle(cmiHologram.getName());
    cmiGui.setInvSize(6);
    animationButtons(cmiGui, cmiHologram);
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(45, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
          public void click(GUIClickType clickType) {
            HologramGUIEditor.openGui(player, cmiHologram);
          }
        };
    cmiGuiButton.setName(LC.info_Back.getLocale(new Object[0]));
    cmiGui.addButton(cmiGuiButton);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  private static void animationButtons(final CMIGui cmiGui, final CMIHologram cmiHologram) {
    final CMIHologramAnimationSettings cmiHologramAnimationSettings =
        cmiHologram.getAnimationSettings();
    final CMIHologramPageSettings cmiHologramPageSettings = cmiHologram.getPageSettings();
    final CMIHologramSettings cmiHologramSettings = cmiHologram.getSettings();
    if (cmiHologram.getType() == CMIHologramType.TextDisplay) {
      if (cmiHologram.getSettings().getBillboard().isCanHaveBackSide()) {
        CMIGuiButton cmiGuiButton =
            new CMIGuiButton(11, CMIMaterial.CARROT.newItemStack()) {
              public void click(GUIClickType clickType) {
                cmiHologramAnimationSettings.setAutoRotate(
                    cmiHologramAnimationSettings.getAutoRotate()
                        + HologramGUIEditor.modifyValue(clickType, 1, 10, -1, -10));
                switch (clickType) {
                  case Q:
                    cmiHologramAnimationSettings.setAutoRotate(0);
                  default:
                    cmiHologram.resetScheduler();
                    cmiHologram.recheckScheduler();
                    HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                }
              }

              public void updateLooks() {
                this.setName(
                    CMILC.getIM(
                        hologram.class,
                        "autoRotate",
                        "[angle]",
                        cmiHologramAnimationSettings.getAutoRotate()));
              }
            };
        cmiGui.addButton(cmiGuiButton);
      }

      CMIGuiButton fadeButton =
          new CMIGuiButton(13, CMIMaterial.RAIL.newItemStack()) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Q:
                  cmiHologramAnimationSettings.setFadeInAnimation(0);
                  break;
                default:
                  int value = HologramGUIEditor.modifyValue(clickType, 1, 10, -1, -10);
                  if (cmiHologramAnimationSettings.getFadeInAnimationSpeed() == 0 && value > 0) {
                    cmiHologramAnimationSettings.setFadeInAnimation(5);
                  } else {
                    cmiHologramAnimationSettings.setFadeInAnimation(
                        cmiHologramAnimationSettings.getFadeInAnimationSpeed() + value);
                  }
              }

              HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      hologram.class,
                      "animationFadeIn",
                      "[value]",
                      cmiHologramAnimationSettings.getFadeInAnimationSpeed()));
            }
          };
      cmiGui.addButton(fadeButton);
      fadeButton =
          new CMIGuiButton(14, CMIMaterial.POWERED_RAIL.newItemStack()) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Q:
                  cmiHologramAnimationSettings.setFadeOutAnimation(0);
                  break;
                default:
                  int fadeOutDelta = HologramGUIEditor.modifyValue(clickType, 1, 10, -1, -10);
                  if (cmiHologramAnimationSettings.getFadeOutAnimationSpeed() == 0
                      && fadeOutDelta > 0) {
                    cmiHologramAnimationSettings.setFadeOutAnimation(5);
                  } else {
                    cmiHologramAnimationSettings.setFadeOutAnimation(
                        cmiHologramAnimationSettings.getFadeOutAnimationSpeed() + fadeOutDelta);
                  }
              }

              HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      hologram.class,
                      "animationFadeOut",
                      "[value]",
                      cmiHologramAnimationSettings.getFadeOutAnimationSpeed()));
            }
          };
      cmiGui.addButton(fadeButton);
    }

    CMIGuiButton pageIntervalButton =
        new CMIGuiButton(32, CMIMaterial.OAK_FENCE.newItemStack()) {
          public void click(GUIClickType clickType) {
            int pageIntervalDelta = HologramGUIEditor.modifyValue(clickType, 1, 10, -1, -10);
            if (cmiHologramPageSettings.getPageChangeIntervalTicks() == 0
                && pageIntervalDelta > 0) {
              cmiHologramPageSettings.setPageChangeIntervalTicks(20);
            } else {
              cmiHologramPageSettings.setPageChangeIntervalTicks(
                  cmiHologramPageSettings.getPageChangeIntervalTicks() + pageIntervalDelta);
            }

            cmiHologram.resetScheduler();
            cmiHologram.recheckScheduler();
            HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
          }

          public void updateLooks() {
            this.setName(
                CMILC.getIM(
                    hologram.class,
                    "pageInterval",
                    "[second]",
                    cmiHologramPageSettings.getPageChangeIntervalTicks() / 20.0));
            this.clearLore();
            this.addLore(CMILC.getIML(hologram.class, "pageIntervalLore"));
            if (cmiHologram.getPages().getPages().size() == 1) {
              this.addLore(CMILC.getIM(hologram.class, "pageIntervalNoPages"));
            }
          }
        };
    cmiGui.addButton(pageIntervalButton);
    pageIntervalButton =
        new CMIGuiButton(30, CMIMaterial.BLUE_WOOL.newItemStack()) {
          public void click(GUIClickType clickType) {
            int pageDelta = HologramGUIEditor.modifyValue(clickType, 1, 10, -1, -10);
            cmiHologramSettings.setUpdateIntervalTicks(
                cmiHologramSettings.getUpdateIntervalTicks() + pageDelta);
            cmiHologram.resetScheduler();
            cmiHologram.recheckScheduler();
            HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
          }

          public void updateLooks() {
            this.setName(
                CMILC.getIM(
                    hologram.class,
                    "interval",
                    "[second]",
                    cmiHologramSettings.getUpdateIntervalTicks() / 20.0));
          }
        };
    pageIntervalButton.addLore(CMILC.getIML(hologram.class, "intervalLore"));
    cmiGui.addButton(pageIntervalButton);
  }

  private static void backgroundButtons(final CMIGui cmiGui, final CMIHologram cmiHologram) {
    if (cmiHologram.getType() == CMIHologramType.TextDisplay) {
      final Player player = cmiGui.getPlayer();
      final CMIHologramSettings cmiHologramSettings = cmiHologram.getSettings();
      final CMIHologramTextSettings cmiHologramTextSettings = cmiHologram.getTextSettings();
      CMIGuiButton cmiGuiButton =
          new CMIGuiButton(9, CMIMaterial.CLOCK.newItemStack()) {
            public void click(GUIClickType clickType) {
              double number = HologramGUIEditor.modifyValue(clickType, 1, 1, -1, -1);
              switch (clickType) {
                case Left:
                case Right:
                  cmiHologramSettings.setSkyLevel(
                      CMINumber.clamp((int) (cmiHologramSettings.getSkyLevel() + number), -1, 15));
                  break;
                case LeftShift:
                case RightShift:
                  cmiHologramSettings.setBlockLevel(
                      CMINumber.clamp(
                          (int) (cmiHologramSettings.getBlockLevel() + number), -1, 15));
                case MiddleMouse:
                case QControl:
                default:
                  break;
                case Q:
                  cmiHologramSettings.setSkyLevel(15);
                  cmiHologramSettings.setBlockLevel(15);
              }

              HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      hologram.class,
                      "light",
                      "[sky]",
                      cmiHologramSettings.getSkyLevel(),
                      "[block]",
                      cmiHologramSettings.getBlockLevel()));
            }
          };
      cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(11, CMIMaterial.AZALEA.newItemStack()) {
            public void click(GUIClickType clickType) {
              cmiHologramTextSettings.setBackgroundAlpha(
                  cmiHologramTextSettings.getBackgroundAlpha()
                      + HologramGUIEditor.modifyValue(clickType, 5, 15, -5, -15));
              switch (clickType) {
                case Q:
                  cmiHologramTextSettings.setBackgroundAlpha(150);
                default:
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
              }
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      hologram.class,
                      "bgAlpha",
                      "[alpha]",
                      cmiHologramTextSettings.getBackgroundAlpha()));
            }
          };
      cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(10, CMIMaterial.PURPLE_DYE.newItemStack()) {
            public void click(GUIClickType clickType) {
              ChatMessageEdit chatMessageEdit =
                  new ChatMessageEdit(
                      player, cmiHologramTextSettings.getBackgroundColor().getColorCode()) {
                    public void run(String string) {
                      if (CMIText.isValidString(string)) {
                        CMIChatColor cmiChatColor = CMIChatColor.getColor(string);
                        if (cmiChatColor != null && cmiChatColor.isColor()) {
                          cmiHologramTextSettings.setBackgroundColor(cmiChatColor);
                          HologramGUIEditor.finalizeClick(player, cmiHologram);
                        } else {
                          CMILC.info_IncorrectColor.sendMessage(player);
                        }
                      }
                    }

                    public void onDisable() {
                      HologramGUIEditor.openGui(player, cmiHologram);
                    }
                  };
              chatMessageEdit.setCheckForCancel(true);
              chatMessageEdit.printMessage();
              CMIUser.closeInventory(this.getGui().getPlayer());
            }
          };
      cmiGuiButton.setName(
          CMILC.getIM(
              hologram.class,
              "bgColor",
              "[color]",
              cmiHologramTextSettings.getBackgroundColor().getColorCode()
                  + (cmiHologramTextSettings.getBackgroundColor().getName() == null
                      ? cmiHologramTextSettings.getBackgroundColor().getHex()
                      : cmiHologramTextSettings.getBackgroundColor().getName())));
      cmiGui.addButton(cmiGuiButton);
    }
  }

  private static void textButtons(final CMIGui cmiGui, final CMIHologram cmiHologram) {
    final CMIHologramTextSettings cmiHologramTextSettings = cmiHologram.getTextSettings();
    final CMIHologramBoardSettings cmiHologramBoardSettings = cmiHologram.getBoardSettings();
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(22, CMIMaterial.INK_SAC.newItemStack()) {
          public void click(GUIClickType clickType) {
            cmiHologramTextSettings.setFillerAmount(
                cmiHologramTextSettings.getFillerAmount()
                    + HologramGUIEditor.modifyValue(clickType, 1, 10, -1, -10));
            switch (clickType) {
              case Q:
                cmiHologramTextSettings.setFillerAmount(245);
              default:
                HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
            }
          }

          public void updateLooks() {
            this.setName(
                CMILC.getIM(
                    hologram.class,
                    "textFiller",
                    "[width]",
                    cmiHologramTextSettings.getFillerAmount()));
          }
        };
    cmiGuiButton.addLore(CMILC.getIM(hologram.class, "textFillerExplanation"));
    cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
    cmiGui.addButton(cmiGuiButton);
    if (cmiHologram.getType() == CMIHologramType.TextDisplay) {
      cmiGuiButton =
          new CMIGuiButton(23, CMIMaterial.ENDER_EYE.newItemStack()) {
            public void click(GUIClickType clickType) {
              cmiHologramTextSettings.setSeeThrough(!cmiHologramTextSettings.isSeeThrough());
              HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      hologram.class,
                      "seeThrough",
                      "[state]",
                      cmiHologramTextSettings.isSeeThrough()));
              this.clearLore();
              if (cmiHologramBoardSettings.isEnabled()) {
                this.addLore(CMILC.getIM(hologram.class, "seeThroughExtra"));
              }
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(18, CMIMaterial.BOOK.newItemStack()) {
            public void click(GUIClickType clickType) {
              cmiHologramTextSettings.setTextAlignment(
                  cmiHologramTextSettings.getTextAlignment().next());
              if (cmiHologramTextSettings.getTextAlignment().equals(CMITextAlignment.CENTER)) {
                cmiHologram.resetCache();
              }

              CMIHologramHoverData.clearData();
              HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      hologram.class,
                      "alignment",
                      "[type]",
                      CMIText.firstToUpperCase(
                          cmiHologramTextSettings.getTextAlignment().toString())));
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(19, CMIMaterial.BLAZE_ROD.newItemStack()) {
            public void click(GUIClickType clickType) {
              cmiHologramTextSettings.setShadowed(!cmiHologramTextSettings.isShadowed());
              if (!cmiHologramTextSettings.isShadowed()) {
                cmiHologram.resetCache();
              }

              HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      hologram.class, "shadow", "[state]", cmiHologramTextSettings.isShadowed()));
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(20, CMIMaterial.RAW_GOLD.newItemStack()) {
            public void click(GUIClickType clickType) {
              cmiHologramTextSettings.setTextAlpha(
                  cmiHologramTextSettings.getTextAlpha()
                      + HologramGUIEditor.modifyValue(clickType, 5, 15, -5, -15));
              switch (clickType) {
                case Q:
                  cmiHologramTextSettings.setTextAlpha(255);
                default:
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
              }
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      hologram.class,
                      "textAlpha",
                      "[alpha]",
                      cmiHologramTextSettings.getTextAlpha()));
            }
          };
      cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(21, CMIMaterial.ACACIA_FENCE_GATE.newItemStack()) {
            public void click(GUIClickType clickType) {
              cmiHologramTextSettings.setLineWidth(
                  cmiHologramTextSettings.getLineWidth()
                      + HologramGUIEditor.modifyValue(clickType, 1, 10, -1, -10));
              switch (clickType) {
                case Q:
                  cmiHologramTextSettings.setLineWidth(1000);
                default:
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
              }
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      hologram.class,
                      "textWidth",
                      "[width]",
                      cmiHologramTextSettings.getLineWidth()));
            }
          };
      cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(cmiGuiButton);
    }
  }

  private static void iconButtons(final CMIGui cmiGui, final CMIHologram cmiHologram) {
    final Player player = cmiGui.getPlayer();
    final CMIHologramIconSettings cmiHologramIconSettings = cmiHologram.getIconSettings();
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(46, CMIMaterial.CHERRY_BOAT.newItemStack()) {
          public void click(GUIClickType clickType) {
            switch (clickType) {
              case Q:
                cmiHologramIconSettings.setOffset(new CMIVector3D());
                HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                break;
              default:
                HologramGUIEditor.openVectorEditor(
                    player,
                    cmiHologramIconSettings.getOffset(),
                    true,
                    0.01,
                    0.0,
                    cmiVector3D -> HologramGUIEditor.openGui(player, cmiHologram),
                    pickedVector -> {
                      pickedVector.setX(CMINumber.clamp(pickedVector.getX(), -999.0, 999));
                      pickedVector.setY(CMINumber.clamp(pickedVector.getY(), -999.0, 999));
                      pickedVector.setZ(CMINumber.clamp(pickedVector.getZ(), -999.0, 999));
                      HologramGUIEditor.updateAndSave(cmiHologram);
                    });
            }
          }

          public void updateLooks() {
            this.setName(
                HologramGUIEditor.formatName(
                    "icons", "offset", "fullVector", cmiHologramIconSettings.getOffset()));
          }
        };
    cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
    cmiGui.addButton(cmiGuiButton);
    if (cmiHologram.getType() == CMIHologramType.TextDisplay) {
      cmiGuiButton =
          new CMIGuiButton(48, CMIMaterial.MUD_BRICK_SLAB.newItemStack()) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Q:
                  cmiHologramIconSettings.setScale(new CMIVector3D(1.0, 1.0, 1.0));
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                  break;
                default:
                  HologramGUIEditor.openVectorEditor(
                      player,
                      cmiHologramIconSettings.getScale(),
                      true,
                      0.01,
                      1.0,
                      pickedVector -> HologramGUIEditor.openGui(player, cmiHologram),
                      pickedVector -> {
                        pickedVector.setX(CMINumber.clamp(pickedVector.getX(), -999.0, 999));
                        pickedVector.setY(CMINumber.clamp(pickedVector.getY(), -999.0, 999));
                        pickedVector.setZ(CMINumber.clamp(pickedVector.getZ(), -999.0, 999));
                        HologramGUIEditor.updateAndSave(cmiHologram);
                      });
              }
            }

            public void updateLooks() {
              this.setName(
                  HologramGUIEditor.formatName(
                      "icons", "scale", "fullScale", cmiHologramIconSettings.getScale()));
            }
          };
      cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(47, CMIMaterial.BIRCH_SIGN.newItemStack()) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Q:
                  cmiHologramIconSettings.setDirection(new CMIVector3D());
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                  break;
                default:
                  HologramGUIEditor.openVectorEditor(
                      player,
                      cmiHologramIconSettings.getDirection(),
                      true,
                      1.0,
                      0.0,
                      pickedVector -> HologramGUIEditor.openGui(player, cmiHologram),
                      pickedVector -> {
                        pickedVector.setX(CMINumber.clamp(pickedVector.getX(), -180.0, 180));
                        pickedVector.setY((pickedVector.getY() + 180.0) % 360.0 - 180.0);
                        pickedVector.setZ((pickedVector.getZ() + 180.0) % 360.0 - 180.0);
                        HologramGUIEditor.updateAndSave(cmiHologram);
                      });
              }
            }

            public void updateLooks() {
              this.setName(
                  HologramGUIEditor.formatName(
                      "icons",
                      "direction",
                      "fullDirection",
                      cmiHologramIconSettings.getDirection()));
            }
          };
      cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(cmiGuiButton);
    }
  }

  private static void baseButtons(final CMIGui cmiGui, final CMIHologram cmiHologram) {
    if (cmiHologram.getType() == CMIHologramType.TextDisplay) {
      final Player player = cmiGui.getPlayer();
      final CMIHologramSettings cmiHologramSettings = cmiHologram.getSettings();
      if (cmiHologram.getSettings().getBillboard().isCanHaveBackSide()) {
        CMIGuiButton cmiGuiButton =
            new CMIGuiButton(29, CMIMaterial.OAK_SIGN.newItemStack()) {
              public void click(GUIClickType clickType) {
                switch (clickType) {
                  case Q:
                    cmiHologramSettings.getDirection().setX(0.0);
                    cmiHologramSettings.getDirection().setY(0.0);
                    HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                    break;
                  default:
                    HologramGUIEditor.openVectorEditor(
                        player,
                        cmiHologramSettings.getDirection(),
                        false,
                        1.0,
                        0.0,
                        cmiVector3D -> HologramGUIEditor.openGui(player, cmiHologram),
                        pickedVector -> {
                          pickedVector.setX(CMINumber.clamp(pickedVector.getX(), -90.0, 90));
                          pickedVector.setY((pickedVector.getY() + 180.0) % 360.0 - 180.0);
                          HologramGUIEditor.updateAndSave(cmiHologram);
                        });
                }
              }

              public void updateLooks() {
                this.setName(
                    HologramGUIEditor.formatName(
                        "base", "direction", "basicDirection", cmiHologramSettings.getDirection()));
              }
            };
        cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
        cmiGui.addButton(cmiGuiButton);
      }

      CMIGuiButton toggleButton =
          new CMIGuiButton(28, CMIMaterial.ACACIA_BOAT.newItemStack()) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Q:
                  cmiHologramSettings.setOffset(new CMIVector3D(0.0, 0.0, 0.0));
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                  break;
                default:
                  HologramGUIEditor.openVectorEditor(
                      player,
                      cmiHologramSettings.getOffset(),
                      true,
                      0.01,
                      0.0,
                      pickedVector -> HologramGUIEditor.openGui(player, cmiHologram),
                      pickedVector -> {
                        pickedVector.setX(CMINumber.clamp(pickedVector.getX(), -99.0, 99));
                        pickedVector.setY(CMINumber.clamp(pickedVector.getY(), -99.0, 99));
                        HologramGUIEditor.updateAndSave(cmiHologram);
                      });
              }
            }

            public void updateLooks() {
              this.setName(
                  HologramGUIEditor.formatName(
                      "base", "offset", "fullVector", cmiHologramSettings.getOffset()));
            }
          };
      toggleButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(toggleButton);
      toggleButton =
          new CMIGuiButton(30, CMIMaterial.STONE_BRICK_SLAB.newItemStack()) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Q:
                  cmiHologramSettings.getScale().setX(1.0);
                  cmiHologramSettings.getScale().setY(1.0);
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                  break;
                default:
                  HologramGUIEditor.openVectorEditor(
                      player,
                      cmiHologramSettings.getScale(),
                      0.01,
                      1.0,
                      cmiVector2D -> HologramGUIEditor.openGui(player, cmiHologram),
                      pickedVector -> {
                        pickedVector.setX(CMINumber.clamp(pickedVector.getX(), 0.0, 999));
                        pickedVector.setY(CMINumber.clamp(pickedVector.getY(), 0.0, 999));
                        HologramGUIEditor.updateAndSave(cmiHologram);
                      });
              }
            }

            public void updateLooks() {
              this.setName(
                  HologramGUIEditor.formatName(
                      "base", "scale", "basicScale", cmiHologramSettings.getScale()));
            }
          };
      toggleButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(toggleButton);
    }
  }

  private static void boardButtons(final CMIGui cmiGui, final CMIHologram cmiHologram) {
    if (cmiHologram.getType() == CMIHologramType.TextDisplay) {
      final Player player = cmiGui.getPlayer();
      final CMIHologramBoardSettings cmiHologramBoardSettings = cmiHologram.getBoardSettings();
      CMIGuiButton cmiGuiButton =
          new CMIGuiButton(36, CMIMaterial.NETHERITE_INGOT.newItemStack()) {
            public void click(GUIClickType clickType) {
              cmiHologramBoardSettings.setEnabled(!cmiHologramBoardSettings.isEnabled());
              if (!cmiHologramBoardSettings.isEnabled()) {
                ((CMIHologramProcessorDisplay)
                        cmiHologram.getProcessor(CMIHologramType.TextDisplay))
                    .resetBoardCache();
              }

              HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
            }

            public void updateLooks() {
              this.setName(
                  HologramGUIEditor.formatName(
                      "board", null, "state", "[state]", cmiHologramBoardSettings.isEnabled()));
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(37, CMIMaterial.BIRCH_BOAT.newItemStack()) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Q:
                  cmiHologramBoardSettings.setOffset(new CMIVector3D(0.0, 0.0, 0.0));
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                  break;
                default:
                  HologramGUIEditor.openVectorEditor(
                      player,
                      cmiHologramBoardSettings.getOffset(),
                      true,
                      0.01,
                      0.0,
                      cmiVector3D -> HologramGUIEditor.openGui(player, cmiHologram),
                      pickedVector -> {
                        pickedVector.setX(CMINumber.clamp(pickedVector.getX(), -99.0, 99));
                        pickedVector.setY(CMINumber.clamp(pickedVector.getY(), -99.0, 99));
                        pickedVector.setZ(CMINumber.clamp(pickedVector.getZ(), -99.0, 99));
                        HologramGUIEditor.updateAndSave(cmiHologram);
                      });
              }
            }

            public void updateLooks() {
              this.setName(
                  HologramGUIEditor.formatName(
                      "board", "offset", "fullVector", cmiHologramBoardSettings.getOffset()));
            }
          };
      cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(38, CMIMaterial.ACACIA_SIGN.newItemStack()) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Q:
                  cmiHologramBoardSettings.setDirection(new CMIVector3D(0.0, 0.0, 0.0));
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                  break;
                default:
                  HologramGUIEditor.openVectorEditor(
                      player,
                      cmiHologramBoardSettings.getDirection(),
                      true,
                      90.0,
                      0.0,
                      pickedVector -> HologramGUIEditor.openGui(player, cmiHologram),
                      pickedVector -> {
                        pickedVector.setX((int) pickedVector.getX() % 360);
                        pickedVector.setY((int) pickedVector.getY() % 360);
                        pickedVector.setZ((int) pickedVector.getZ() % 360);
                        HologramGUIEditor.updateAndSave(cmiHologram);
                      });
              }
            }

            public void updateLooks() {
              this.setName(
                  HologramGUIEditor.formatName(
                      "board",
                      "direction",
                      "fullDirection",
                      cmiHologramBoardSettings.getDirection()));
            }
          };
      cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(39, CMIMaterial.BRICK_SLAB.newItemStack()) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Q:
                  cmiHologramBoardSettings.setExtraDimensions(new CMIVector3D(0.0, 0.0, 0.01));
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                  break;
                default:
                  HologramGUIEditor.openVectorEditor(
                      player,
                      cmiHologramBoardSettings.getExtraDimensions(),
                      true,
                      0.01,
                      0.0,
                      pickedVector -> HologramGUIEditor.openGui(player, cmiHologram),
                      pickedVector -> {
                        pickedVector.setX(CMINumber.clamp(pickedVector.getX(), -999.0, 999));
                        pickedVector.setY(CMINumber.clamp(pickedVector.getY(), -999.0, 999));
                        pickedVector.setZ(CMINumber.clamp(pickedVector.getZ(), -999.0, 999));
                        HologramGUIEditor.updateAndSave(cmiHologram);
                      });
              }
            }

            public void updateLooks() {
              this.setName(
                  HologramGUIEditor.formatName(
                      "board",
                      "scale",
                      "fullScale",
                      cmiHologramBoardSettings.getExtraDimensions()));
            }
          };
      cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(40, cmiHologramBoardSettings.getMaterial().newItemStack()) {
            public void click(GUIClickType clickType) {
              MaterialPicker materialPicker =
                  new MaterialPicker(player) {
                    public void pickedMaterial(GUIClickType clickType, CMIMaterial cmiMaterial) {
                      if (cmiMaterial != null) {
                        if (cmiMaterial.getMaterial().isBlock()) {
                          cmiHologramBoardSettings.setMaterial(cmiMaterial);
                          HologramGUIEditor.updateAndSave(cmiHologram);
                        }
                      }
                    }

                    public boolean validMaterial(CMIMaterial material) {
                      if (material.equals(CMIMaterial.STONE)
                          && Material.STONE.equals(material.getMaterial())) {
                        return true;
                      } else {
                        return !material.equals(CMIMaterial.STONE)
                                && Material.STONE.equals(material.getMaterial())
                            ? false
                            : material.getMaterial() != null && material.getMaterial().isBlock();
                      }
                    }

                    public void onUIClose() {
                      CMIScheduler.runTask(
                          CMI.getInstance(), () -> HologramGUIEditor.openGui(player, cmiHologram));
                    }
                  };
              materialPicker.openPicker(1, LC.info_pickIcon.getLocale(new Object[0]));
            }

            public void updateLooks() {
              this.setName(
                  HologramGUIEditor.formatName(
                      "board",
                      "",
                      "material",
                      "[material]",
                      cmiHologramBoardSettings.getMaterial().toString()));
            }
          };
      cmiGui.addButton(cmiGuiButton);
    }
  }

  private static void updateShowCorners(Player player) {
    CMIHologramHoverData cmiHologramHoverData = CMIHologramHoverData.getData(player);
    if (cmiHologramHoverData != null) {
      cmiHologramHoverData.setShowCornersUntil(System.currentTimeMillis() + 10000L);
    }
  }

  public static void editInteractions(final Player player, final CMIHologram cmiHologram) {
    final CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            CMI.getInstance().getHologramManager().save();
          }
        };
    updateShowCorners(player);
    cmiGui.setTitle(cmiHologram.getName());
    cmiGui.setInvSize(6);
    final CMIHologramInteraction cmiHologramInteraction = cmiHologram.getInteractionSettings();
    final CMIHologramPageSettings cmiHologramPageSettings = cmiHologram.getPageSettings();
    CMIHologramType cmiHologramType = cmiHologram.getData(player).getHologramType();
    if (cmiHologramType == null) {
      cmiHologram.getType();
    }

    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(4, CMIMaterial.STICK.newItemStack()) {
          public void click(GUIClickType clickType) {
            cmiHologramInteraction.setInteractable(!cmiHologramInteraction.isInteractable());
            if (cmiHologramInteraction.isInteractable()) {
              CMI.getInstance().getPacketInjector().addPlayer(player);
            }

            cmiHologram.update();
            HologramGUIEditor.finalizeInteractions(player, cmiHologram);
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM(
            hologram.class, "interact", "[state]", cmiHologramInteraction.isInteractable()));
    cmiGui.addButton(cmiGuiButton);
    if (cmiHologramInteraction.isInteractable()) {
      cmiGuiButton =
          new CMIGuiButton(16, CMIMaterial.BOW.newItemStack()) {
            public void click(GUIClickType clickType) {
              hologram.startEditingCommands(player, cmiHologram);
              this.setCloseInv(true);
            }
          };
      cmiGuiButton.setName(CMILC.getIM(hologram.class, "commands"));

      for (String string : cmiHologram.getInteractionSettings().getCommands()) {
        cmiGuiButton.addLore("&7/" + string);
      }

      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(25, CMIMaterial.PAPER.newItemStack()) {
            public void click(GUIClickType clickType) {
              cmiHologramInteraction.setShowClickParticle(
                  !cmiHologramInteraction.isShowClickParticle());
              HologramGUIEditor.finalizeInteractions(player, cmiHologram);
              HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      hologram.class,
                      "particles",
                      "[state]",
                      cmiHologramInteraction.isShowClickParticle()));
            }
          };
      cmiGui.addButton(cmiGuiButton);
      if (Version.isCurrentEqualOrHigher(Version.v1_9_R2)) {
        cmiGuiButton =
            new CMIGuiButton(
                11,
                cmiHologramInteraction.isShowHoverParticle()
                    ? CMIMaterial.GREEN_CARPET
                    : CMIMaterial.YELLOW_CARPET) {
              public void click(GUIClickType clickType) {
                cmiHologramInteraction.setShowHoverParticle(
                    !cmiHologramInteraction.isShowHoverParticle());
                HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
              }

              public void updateLooks() {
                this.setItem(
                    cmiHologramInteraction.isShowHoverParticle()
                        ? CMIMaterial.GREEN_CARPET
                        : CMIMaterial.YELLOW_CARPET);
                this.setName(
                    CMILC.getIM(
                        hologram.class,
                        "hoverParticles",
                        "[state]",
                        cmiHologramInteraction.isShowHoverParticle()));
              }
            };
        cmiGui.addButton(cmiGuiButton);
        if (cmiHologramInteraction.isShowHoverParticle()) {
          cmiGuiButton =
              new CMIGuiButton(28, CMIMaterial.OAK_FENCE) {
                public void click(GUIClickType clickType) {
                  int value = HologramGUIEditor.modifyValue(clickType, 1, 10, -1, -10);
                  cmiHologramInteraction.setParticleCount(
                      cmiHologramInteraction.getParticleCount() + value);
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                }

                public void updateLooks() {
                  this.setName(
                      CMILC.getIM(
                          hologram.class,
                          "particleCount",
                          "[count]",
                          cmiHologramInteraction.getParticleCount()));
                }
              };
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(
                  29, cmiHologramInteraction.getHoverEffect().getParticle().getIcon()) {
                public void click(GUIClickType clickType) {
                  CMIEffectPicker cmiEffectPicker =
                      new CMIEffectPicker(player, cmiHologramInteraction.getHoverEffect()) {
                        public void onGoingBack() {
                          HologramGUIEditor.updateAndSave(cmiHologram);
                          HologramGUIEditor.editInteractions(player, cmiHologram);
                        }

                        public void onUIClose() {
                          HologramGUIEditor.updateAndSave(cmiHologram);
                        }

                        public void onSerializeClick() {}

                        public void onUpdate() {
                          cmiHologramInteraction.setHoverEffect(this.getEffect());
                        }
                      };
                  cmiEffectPicker.setShowBackButton(true);
                  cmiEffectPicker.open();
                }

                public void updateLooks() {
                  this.setName(
                      CMILC.getIM(
                          hologram.class,
                          "particleType",
                          "[type]",
                          cmiHologramInteraction.getHoverEffect().getParticle().getName()));
                }
              };
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(30, CMIMaterial.LADDER) {
                public void click(GUIClickType clickType) {
                  cmiHologramInteraction.setParticleSpacing(
                      CMINumber.sum(
                          cmiHologramInteraction.getParticleSpacing(),
                          HologramGUIEditor.modifyValue(clickType, 0.01, 0.1, -0.01, -0.1)));
                  switch (clickType) {
                    case MiddleMouse:
                    case Q:
                      cmiHologramInteraction.setParticleSpacing(0.0);
                    case QControl:
                    default:
                      HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                  }
                }

                public void updateLooks() {
                  this.setName(
                      CMILC.getIM(
                          hologram.class,
                          "particleSpacing",
                          "[amount]",
                          cmiHologramInteraction.getParticleSpacing()));
                }
              };
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(38, CMIMaterial.BRICK_WALL) {
                public void click(GUIClickType clickType) {
                  switch (clickType) {
                    case Q:
                      cmiHologramInteraction.setParticleDimensions(new CMIVector2D(0.0, 0.0));
                      HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                      break;
                    default:
                      HologramGUIEditor.openVectorEditor(
                          player,
                          cmiHologramInteraction.getParticleDimensions(),
                          0.01,
                          0.0,
                          cmiVector2D -> HologramGUIEditor.editInteractions(player, cmiHologram),
                          pickedVector -> {
                            pickedVector.setX(CMINumber.clamp(pickedVector.getX(), -999.0, 999));
                            pickedVector.setY(CMINumber.clamp(pickedVector.getY(), -999.0, 999));
                            HologramGUIEditor.updateAndSave(cmiHologram);
                          });
                  }
                }

                public void updateLooks() {
                  this.setName(
                      HologramGUIEditor.formatName(
                          "particle",
                          "scale",
                          "basicVector",
                          cmiHologramInteraction.getParticleDimensions()));
                }
              };
          cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(39, CMIMaterial.STONE_BRICK_WALL) {
                public void click(GUIClickType clickType) {
                  switch (clickType) {
                    case Q:
                      cmiHologramInteraction.setParticleOffset(new CMIVector3D());
                      HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                      break;
                    default:
                      HologramGUIEditor.openVectorEditor(
                          player,
                          cmiHologramInteraction.getParticleOffset(),
                          true,
                          0.01,
                          0.0,
                          cmiVector3D -> HologramGUIEditor.editInteractions(player, cmiHologram),
                          pickedVector -> {
                            pickedVector.setX(CMINumber.clamp(pickedVector.getX(), -999.0, 999));
                            pickedVector.setY(CMINumber.clamp(pickedVector.getY(), -999.0, 999));
                            pickedVector.setZ(CMINumber.clamp(pickedVector.getZ(), -999.0, 999));
                            HologramGUIEditor.updateAndSave(cmiHologram);
                          });
                  }
                }

                public void updateLooks() {
                  this.setName(
                      HologramGUIEditor.formatName(
                          "particle",
                          "offset",
                          "fullVector",
                          cmiHologramInteraction.getParticleOffset()));
                }
              };
          cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(32, CMIMaterial.OAK_SLAB) {
                public void click(GUIClickType clickType) {
                  switch (clickType) {
                    case Q:
                      cmiHologramInteraction.setOffset(new CMIVector3D());
                      HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                      HologramGUIEditor.updateShowCorners(player);
                      break;
                    default:
                      HologramGUIEditor.openVectorEditor(
                          player,
                          cmiHologramInteraction.getOffset(),
                          true,
                          0.01,
                          0.0,
                          pickedVector -> HologramGUIEditor.editInteractions(player, cmiHologram),
                          pickedVector -> {
                            pickedVector.setX(CMINumber.clamp(pickedVector.getX(), -999.0, 999));
                            pickedVector.setY(CMINumber.clamp(pickedVector.getY(), -999.0, 999));
                            pickedVector.setZ(CMINumber.clamp(pickedVector.getZ(), -999.0, 999));
                            HologramGUIEditor.updateAndSave(cmiHologram);
                            HologramGUIEditor.updateShowCorners(player);
                          });
                  }
                }

                public void updateLooks() {
                  this.setName(
                      HologramGUIEditor.formatName(
                          "interaction",
                          "offset",
                          "fullVector",
                          cmiHologramInteraction.getOffset()));
                }
              };
          cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
          cmiGui.addButton(cmiGuiButton);
          cmiGuiButton =
              new CMIGuiButton(41, CMIMaterial.ACACIA_SLAB) {
                public void click(GUIClickType clickType) {
                  switch (clickType) {
                    case Q:
                      cmiHologramInteraction.setDimensions(new CMIVector3D());
                      HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                      HologramGUIEditor.updateShowCorners(player);
                      break;
                    default:
                      HologramGUIEditor.openVectorEditor(
                          player,
                          cmiHologramInteraction.getDimensions(),
                          0.01,
                          0.0,
                          pickedVector -> HologramGUIEditor.editInteractions(player, cmiHologram),
                          pickedVector -> {
                            pickedVector.setX(CMINumber.clamp(pickedVector.getX(), -999.0, 999));
                            pickedVector.setY(CMINumber.clamp(pickedVector.getY(), -999.0, 999));
                            HologramGUIEditor.updateAndSave(cmiHologram);
                            HologramGUIEditor.updateShowCorners(player);
                          });
                  }
                }

                public void updateLooks() {
                  this.setName(
                      HologramGUIEditor.formatName(
                          "interaction",
                          "scale",
                          "basicScale",
                          cmiHologramInteraction.getDimensions()));
                }
              };
          cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
          cmiGui.addButton(cmiGuiButton);
        }
      }

      cmiGuiButton =
          new CMIGuiButton(14, CMIMaterial.BOOK.newItemStack()) {
            public void click(GUIClickType clickType) {
              cmiHologramPageSettings.setAutoPagination(
                  !cmiHologramPageSettings.isAutoPagination());
              HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
            }

            public void updateLooks() {
              this.setName(
                  CMILC.getIM(
                      hologram.class,
                      "embeddedAutoButton",
                      "[state]",
                      cmiHologramPageSettings.isAutoPagination()));
            }
          };
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(43, CMIMaterial.ACACIA_LOG.newItemStack()) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Q:
                  cmiHologramInteraction.setBasePrefix("");
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                  break;
                default:
                  CMIUser.closeInventory(player);
                  ChatMessageEdit chatMessageEdit =
                      new ChatMessageEdit(player, cmiHologramInteraction.getBasePrefix()) {
                        public void run(String input) {
                          input = CMIText.getFirstVariable(input);
                          if (CMIText.isValidString(input)) {
                            if (input.equalsIgnoreCase("remove")) {
                              cmiHologramInteraction.setBasePrefix("");
                            } else {
                              cmiHologramInteraction.setBasePrefix(input);
                            }

                            HologramGUIEditor.finalizeInteractions(player, cmiHologram);
                          }
                        }

                        public void onDisable() {
                          HologramGUIEditor.finalizeInteractions(player, cmiHologram);
                        }
                      };
                  chatMessageEdit.setCheckForCancel(true);
                  chatMessageEdit.printMessage();
              }
            }

            public void updateLooks() {
              String basePrefix = cmiHologramInteraction.getBasePrefix();
              if (basePrefix.isEmpty()) {
                basePrefix = HologramManager.getBasePrefix();
              }

              this.setName(CMILC.getIM(hologram.class, "basePrefix", "[prefix]", basePrefix));
            }
          };
      cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(52, CMIMaterial.ACACIA_PLANKS.newItemStack()) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Q:
                  cmiHologramInteraction.setHoverPrefix("");
                  HologramGUIEditor.updateAndSave(cmiHologram, cmiGui, this);
                  break;
                default:
                  CMIUser.closeInventory(player);
                  ChatMessageEdit chatEdit =
                      new ChatMessageEdit(player, cmiHologramInteraction.getHoverPrefix()) {
                        public void run(String input) {
                          input = CMIText.getFirstVariable(input);
                          if (CMIText.isValidString(input)) {
                            if (input.equalsIgnoreCase("remove")) {
                              cmiHologramInteraction.setHoverPrefix("");
                            } else {
                              cmiHologramInteraction.setHoverPrefix(input);
                            }

                            HologramGUIEditor.finalizeInteractions(player, cmiHologram);
                          }
                        }

                        public void onDisable() {
                          HologramGUIEditor.finalizeInteractions(player, cmiHologram);
                        }
                      };
                  chatEdit.setCheckForCancel(true);
                  chatEdit.printMessage();
              }
            }

            public void updateLooks() {
              String hoverPrefix = cmiHologramInteraction.getHoverPrefix();
              if (hoverPrefix.isEmpty()) {
                hoverPrefix = HologramManager.getHoverPrefix();
              }

              this.setName(CMILC.getIM(hologram.class, "hoverPrefix", "[prefix]", hoverPrefix));
            }
          };
      cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(cmiGuiButton);
    }

    cmiGuiButton =
        new CMIGuiButton(45, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
          public void click(GUIClickType clickType) {
            HologramGUIEditor.openGui(player, cmiHologram);
          }
        };
    cmiGuiButton.setName(LC.info_Back.getLocale(new Object[0]));
    cmiGui.addButton(cmiGuiButton);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  public static void editLocation(final Player player, final CMIHologram cmiHologram) {
    CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {
            CMI.getInstance().getHologramManager().save();
          }
        };
    cmiGui.setTitle(cmiHologram.getName());
    cmiGui.setInvSize(6);
    final CMIHologramSettings cmiHologramSettings = cmiHologram.getSettings();
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(0, CMIMaterial.COMPASS.newItemStack()) {
          public void click(GUIClickType clickType) {
            CMIUser.closeInventory(player);
            CMI.getInstance()
                .getTeleportHandler()
                .teleportPlayer(player, cmiHologram.getLocation(), CMITeleportType.HoloEdit);
          }
        };
    cmiGuiButton.setName(CMILC.getIM(hologram.class, "teleport"));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(10, CMIMaterial.LIME_CARPET.newItemStack()) {
          public void click(GUIClickType clickType) {
            cmiHologramSettings.setUpdateRange(
                cmiHologramSettings.getUpdateRange()
                    - HologramGUIEditor.modifyValue(clickType, -1, -10, 1, 10));
            cmiHologram.resetUpdateArea();
            HologramGUIEditor.finalizeRanges(player, cmiHologram);
            HologramGUIEditor.fullHoloReset(cmiHologram);
            CMI.getInstance()
                .getSelectionManager()
                .setSelectionCuboid(player, cmiHologram.getUpdateArea());
            SelectionManager.getPlayerSelection(player).showFull();
            HologramGUIEditor.editLocation(player, cmiHologram);
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM(
            hologram.class, "updateRange", "[blocks]", cmiHologramSettings.getUpdateRange()));
    cmiGuiButton.addLore(CMILC.getIML(hologram.class, "updateRangeLore"));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(19, CMIMaterial.MAGENTA_CARPET.newItemStack()) {
          public void click(GUIClickType clickType) {
            cmiHologramSettings.setVisibilityRange(
                cmiHologramSettings.getVisibilityRange()
                    - HologramGUIEditor.modifyValue(clickType, -1, -10, 1, 10));
            cmiHologram.resetVisibilityArea();
            HologramGUIEditor.finalizeRanges(player, cmiHologram);
            HologramGUIEditor.fullHoloReset(cmiHologram);
            CMI.getInstance()
                .getSelectionManager()
                .setSelectionCuboid(player, cmiHologram.getVisibilityArea());
            SelectionManager.getPlayerSelection(player).showFull();
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM(hologram.class, "range", "[blocks]", cmiHologramSettings.getVisibilityRange()));
    cmiGuiButton.addLore(CMILC.getIML(hologram.class, "rangeLore"));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(22, CMIMaterial.CLOCK.newItemStack()) {
          public void click(GUIClickType clickType) {
            Location location = player.getLocation().clone();
            location.add(0.0, 1.0, 0.0);
            switch (clickType) {
              case Left:
              case Right:
                cmiHologram.setLocation(location);
                break;
              case LeftShift:
              case RightShift:
                location.setX(location.getBlockX());
                location.setZ(location.getBlockZ());
                cmiHologram.setLocation(location.add(0.5, 0.0, 0.5));
            }

            CMI.getInstance().getHologramManager().recalculateChunks(cmiHologram);
            CMI.getInstance().getHologramManager().reset(cmiHologram);
            HologramGUIEditor.finalizeRanges(player, cmiHologram);
            CMI.getInstance()
                .getSelectionManager()
                .setSelectionCuboid(player, cmiHologram.getVisibilityArea());
            SelectionManager.getPlayerSelection(player).showFull();
          }
        };
    cmiGuiButton.setName(CMILC.getIM(hologram.class, "relocate"));
    cmiGuiButton.addLore(CMILC.getIML(hologram.class, "relocateLore"));
    cmiGui.addButton(cmiGuiButton);
    if (cmiHologramSettings.getGroup() != null) {
      cmiGuiButton =
          new CMIGuiButton(49, CMIMaterial.END_CRYSTAL.newItemStack()) {
            public void click(GUIClickType clickType) {
              Location playerLocation = player.getLocation().clone();
              cmiHologram.relocateGroup(playerLocation);
            }
          };
      cmiGuiButton.setName(
          CMILC.getIM(hologram.class, "groupRelocate", "[group]", cmiHologramSettings.getGroup()));
      var set =
          CMI.getInstance()
              .getHologramManager()
              .getHologramsByGroup(cmiHologramSettings.getGroup());
      cmiGuiButton.addLore(CMILC.getIM(hologram.class, "groupCount", "[count]", set.size()));
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(39, CMIMaterial.YELLOW_WOOL.newItemStack()) {
            public void click(GUIClickType clickType) {
              CMI.getInstance()
                  .getHologramManager()
                  .shiftHologramGroup(
                      new Vector(
                          HologramGUIEditor.modifyValue(clickType, -0.01, -0.1, 0.01, 0.1),
                          0.0,
                          0.0),
                      cmiHologramSettings.getGroup());
              HologramGUIEditor.editLocation(player, cmiHologram);
            }
          };
      cmiGuiButton.setName(
          CMILC.getIM(
              hologram.class,
              "moveX",
              "[x]",
              (int) (cmiHologram.getLocation().getX() * 100.0) / 100.0));
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(40, CMIMaterial.MAGENTA_WOOL.newItemStack()) {
            public void click(GUIClickType clickType) {
              CMI.getInstance()
                  .getHologramManager()
                  .shiftHologramGroup(
                      new Vector(
                          0.0,
                          HologramGUIEditor.modifyValue(clickType, -0.01, -0.1, 0.01, 0.1),
                          0.0),
                      cmiHologramSettings.getGroup());
              HologramGUIEditor.editLocation(player, cmiHologram);
            }
          };
      cmiGuiButton.setName(
          CMILC.getIM(
              hologram.class,
              "moveY",
              "[y]",
              (int) (cmiHologram.getLocation().getY() * 100.0) / 100.0));
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(41, CMIMaterial.RED_WOOL.newItemStack()) {
            public void click(GUIClickType clickType) {
              CMI.getInstance()
                  .getHologramManager()
                  .shiftHologramGroup(
                      new Vector(
                          0.0,
                          0.0,
                          HologramGUIEditor.modifyValue(clickType, -0.01, -0.1, 0.01, 0.1)),
                      cmiHologramSettings.getGroup());
              HologramGUIEditor.editLocation(player, cmiHologram);
            }
          };
      cmiGuiButton.setName(
          CMILC.getIM(
              hologram.class,
              "moveZ",
              "[z]",
              (int) (cmiHologram.getLocation().getZ() * 100.0) / 100.0));
      cmiGui.addButton(cmiGuiButton);
      cmiGuiButton =
          new CMIGuiButton(50, CMIMaterial.BAMBOO_SIGN.newItemStack()) {
            public void click(GUIClickType clickType) {
              CMI.getInstance()
                  .getHologramManager()
                  .rotateHologramYawGroup(
                      HologramGUIEditor.modifyValue(clickType, -1, -10, 1, 10),
                      cmiHologramSettings.getGroup(),
                      cmiHologram.getLocation());
              HologramGUIEditor.editLocation(player, cmiHologram);
            }
          };
      cmiGuiButton.setName(
          CMILC.getIM(hologram.class, "holoYaw", "[angle]", cmiHologramSettings.getYaw()));
      cmiGui.addButton(cmiGuiButton);
    }

    cmiGuiButton =
        new CMIGuiButton(48, CMIMaterial.ENDER_EYE.newItemStack()) {
          public void click(GUIClickType clickType) {
            CMIUser.closeInventory(player);
            ChatMessageEdit chatMessageEdit =
                new ChatMessageEdit(
                    player,
                    cmiHologramSettings.getGroup() == null ? "" : cmiHologramSettings.getGroup()) {
                  public void run(String string) {
                    string = CMIChatColor.stripColor(CMIText.getFirstVariable(string));
                    if (CMIText.isValidString(string)) {
                      if (string.equalsIgnoreCase("remove")) {
                        cmiHologramSettings.setGroup(null);
                      } else {
                        cmiHologramSettings.setGroup(string.replace(" ", "_"));
                      }

                      CMI.getInstance()
                          .getHologramManager()
                          .updateHologramGroup(cmiHologram, cmiHologramSettings.getGroup());
                      HologramGUIEditor.finalizeRanges(player, cmiHologram);
                    }
                  }

                  public void onDisable() {
                    HologramGUIEditor.editLocation(player, cmiHologram);
                  }
                };
            chatMessageEdit.setCheckForCancel(true);
            chatMessageEdit.printMessage();
          }
        };
    cmiGuiButton.setName("&7Assign group name");
    if (cmiHologramSettings.getGroup() != null) {
      cmiGuiButton.addLore("&7Current group: &f" + cmiHologramSettings.getGroup());
    }

    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(12, CMIMaterial.BLUE_WOOL.newItemStack()) {
          public void click(GUIClickType clickType) {
            CMILocation cmiLocation = cmiHologram.getLocation().clone();
            cmiLocation.add(
                HologramGUIEditor.modifyValue(clickType, 0.01, 0.1, -0.01, -0.1), 0.0, 0.0);
            cmiHologram.setLocation(cmiLocation);
            HologramGUIEditor.finalizeRanges(player, cmiHologram);
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM(
            hologram.class,
            "moveX",
            "[x]",
            (int) (cmiHologram.getLocation().getX() * 100.0) / 100.0));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(13, CMIMaterial.BROWN_WOOL.newItemStack()) {
          public void click(GUIClickType clickType) {
            CMILocation hologramLocation = cmiHologram.getLocation().clone();
            hologramLocation.add(
                0.0, HologramGUIEditor.modifyValue(clickType, 0.01, 0.1, -0.01, -0.1), 0.0);
            cmiHologram.setLocation(hologramLocation);
            HologramGUIEditor.finalizeRanges(player, cmiHologram);
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM(
            hologram.class,
            "moveY",
            "[y]",
            (int) (cmiHologram.getLocation().getY() * 100.0) / 100.0));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(14, CMIMaterial.GREEN_WOOL.newItemStack()) {
          public void click(GUIClickType clickType) {
            CMILocation nudgedLocation = cmiHologram.getLocation().clone();
            nudgedLocation.add(
                0.0, 0.0, HologramGUIEditor.modifyValue(clickType, 0.01, 0.1, -0.01, -0.1));
            cmiHologram.setLocation(nudgedLocation);
            HologramGUIEditor.finalizeRanges(player, cmiHologram);
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM(
            hologram.class,
            "moveZ",
            "[z]",
            (int) (cmiHologram.getLocation().getZ() * 100.0) / 100.0));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(45, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
          public void click(GUIClickType clickType) {
            HologramGUIEditor.openGui(player, cmiHologram);
          }
        };
    cmiGuiButton.setName(LC.info_Back.getLocale(new Object[0]));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(8, CMIMaterial.LEVER.newItemStack()) {
          public void click(GUIClickType clickType) {
            cmiHologramSettings.setPermissionRequirement(
                !cmiHologramSettings.isRequiresPermission());
            cmiHologram
                .removePlayers()
                .whenComplete((result, throwable) -> cmiHologram.updatePlayers());
            HologramGUIEditor.editLocation(player, cmiHologram);
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM(
            hologram.class,
            "reqPermission",
            "[state]",
            cmiHologramSettings.isRequiresPermission()));
    if (cmiHologramSettings.isRequiresPermission()) {
      cmiGuiButton.addLore(
          "&6"
              + PermissionsManager.CMIPerm.hologram_$1.getPermission(
                  cmiHologram.getName().toLowerCase()));
    }

    cmiGui.addButton(cmiGuiButton);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  private static void fullHoloReset(CMIHologram cmiHologram) {
    cmiHologram.recalculateChunks();
    cmiHologram.updatePlayers();
    cmiHologram.requestUpdate(
        CMIHologramUpdateType.Billboard,
        CMIHologramUpdateType.Text,
        CMIHologramUpdateType.Show,
        CMIHologramUpdateType.ShowIcon,
        CMIHologramUpdateType.Location);
  }

  private static int modifyValue(
      GUIClickType clickType, int value, int shiftStep, int rightStep, int rightShiftStep) {
    switch (clickType) {
      case Left:
        return value;
      case LeftShift:
        return shiftStep;
      case Right:
        return rightStep;
      case RightShift:
        return rightShiftStep;
      default:
        return 0;
    }
  }

  private static double modifyValue(
      GUIClickType clickType,
      double number,
      double shiftStep,
      double rightStep,
      double rightShiftStep) {
    switch (clickType) {
      case Left:
        return number;
      case LeftShift:
        return shiftStep;
      case Right:
        return rightStep;
      case RightShift:
        return rightShiftStep;
      default:
        return 0.0;
    }
  }

  public static boolean openVectorEditor(
      Player player,
      final CMIVector2D cmiVector2D,
      final double number,
      final double step,
      final Consumer<CMIVector2D> consumer,
      final Consumer<CMIVector2D> consumerSecond) {
    CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {}
        };
    cmiGui.setTitle("Vector modification");
    cmiGui.setInvSize(6);
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(
            20,
            CMIItemStack.deserialize(
                "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2I4YjcxYjAwOGFkMTQyOGE5NDQxMmZhNTUzMTg3ZWY0MmVmZWFhYzVhNzBkZGY3MTQ4ZGFjZDY4NjMyYTQyMCJ9fX0=")) {
          public void click(GUIClickType clickType) {
            switch (clickType) {
              case Q:
                cmiVector2D.setX(step);
                break;
              default:
                cmiVector2D.setX(
                    CMINumber.sum(
                        cmiVector2D.getX(),
                        HologramGUIEditor.modifyValue(
                            clickType, number, number * 10.0, -number, -number * 10.0)));
            }

            consumer.accept(cmiVector2D);
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setName("&7X: &f" + String.format("%.2f", cmiVector2D.getX()));
          }
        };
    cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(
            24,
            CMIItemStack.deserialize(
                "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGMwZWI2MjZmYTQ0YTAyMTFjZTEyNzc0YzRjYzQ0ZTZkYTUyMzFhYWJhNGE1MzBkYzJlMjE0MDlmNzk1YTY3YiJ9fX0=")) {
          public void click(GUIClickType clickType) {
            switch (clickType) {
              case Q:
                cmiVector2D.setY(step);
                break;
              default:
                cmiVector2D.setY(
                    CMINumber.sum(
                        cmiVector2D.getY(),
                        HologramGUIEditor.modifyValue(
                            clickType, number, number * 10.0, -number, -number * 10.0)));
            }

            consumer.accept(cmiVector2D);
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setName("&7Y: &f" + String.format("%.2f", cmiVector2D.getY()));
          }
        };
    cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(45, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
          public void click(GUIClickType clickType) {
            consumer.accept(cmiVector2D);
          }
        };
    cmiGuiButton.setName(LC.info_Back.getLocale(new Object[0]));
    cmiGui.addButton(cmiGuiButton);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
    return true;
  }

  public static boolean openVectorEditor(
      Player player,
      final CMIVector3D cmiVector3D,
      boolean flag,
      final double number,
      final double step,
      final Consumer<CMIVector3D> consumer,
      final Consumer<CMIVector3D> consumerSecond) {
    CMIGui cmiGui =
        new CMIGui(player) {
          public void onClose() {}
        };
    cmiGui.setTitle("Vector modification");
    cmiGui.setInvSize(6);
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(
            20,
            CMIItemStack.deserialize(
                "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2I4YjcxYjAwOGFkMTQyOGE5NDQxMmZhNTUzMTg3ZWY0MmVmZWFhYzVhNzBkZGY3MTQ4ZGFjZDY4NjMyYTQyMCJ9fX0=")) {
          public void click(GUIClickType clickType) {
            switch (clickType) {
              case Q:
                cmiVector3D.setX(step);
                break;
              default:
                cmiVector3D.setX(
                    CMINumber.sum(
                        cmiVector3D.getX(),
                        HologramGUIEditor.modifyValue(
                            clickType, number, number * 10.0, -number, -number * 10.0)));
            }

            consumer.accept(cmiVector3D);
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setName("&7X: &f" + String.format("%.2f", cmiVector3D.getX()));
          }
        };
    cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
    cmiGui.addButton(cmiGuiButton);
    if (flag) {
      cmiGuiButton =
          new CMIGuiButton(
              24,
              CMIItemStack.deserialize(
                  "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTk4MDQ3NmNhYTczYzMzNTYzMGM0MDA0NTAxZGY3NjIwMzNkNWZkOGUzNjQzNDRkN2M4YzEzNWU5NzQwNGZjMiJ9fX0=")) {
            public void click(GUIClickType clickType) {
              switch (clickType) {
                case Q:
                  cmiVector3D.setZ(step);
                  break;
                default:
                  cmiVector3D.setZ(
                      CMINumber.sum(
                          cmiVector3D.getZ(),
                          HologramGUIEditor.modifyValue(
                              clickType, number, number * 10.0, -number, -number * 10.0)));
              }

              consumer.accept(cmiVector3D);
              this.updateLooks();
              this.update();
            }

            public void updateLooks() {
              this.setName("&7Z: &f" + String.format("%.2f", cmiVector3D.getZ()));
            }
          };
      cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
      cmiGui.addButton(cmiGuiButton);
    }

    cmiGuiButton =
        new CMIGuiButton(
            22,
            CMIItemStack.deserialize(
                "head:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGMwZWI2MjZmYTQ0YTAyMTFjZTEyNzc0YzRjYzQ0ZTZkYTUyMzFhYWJhNGE1MzBkYzJlMjE0MDlmNzk1YTY3YiJ9fX0=")) {
          public void click(GUIClickType clickType) {
            switch (clickType) {
              case Q:
                cmiVector3D.setY(step);
                break;
              default:
                cmiVector3D.setY(
                    CMINumber.sum(
                        cmiVector3D.getY(),
                        HologramGUIEditor.modifyValue(
                            clickType, number, number * 10.0, -number, -number * 10.0)));
            }

            consumer.accept(cmiVector3D);
            this.updateLooks();
            this.update();
          }

          public void updateLooks() {
            this.setName("&7Y: &f" + String.format("%.2f", cmiVector3D.getY()));
          }
        };
    cmiGuiButton.addLore(CMILC.info_Reset.getLocale());
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(45, CMILib.getInstance().getConfigManager().getGUIPreviousPage()) {
          public void click(GUIClickType clickType) {
            consumer.accept(cmiVector3D);
          }
        };
    cmiGuiButton.setName(LC.info_Back.getLocale(new Object[0]));
    cmiGui.addButton(cmiGuiButton);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
    return true;
  }
}
