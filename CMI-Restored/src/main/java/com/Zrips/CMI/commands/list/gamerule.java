package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIGameRule;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.util.ArrayList;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Container.PageInfo;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class gamerule implements Cmd {
  static final int perPage = 45;

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "updated",
        "{gcp}Game rule ({gcs}[rule]{gcp}) changed for {gcs}[world] {gcp}to {gcs}[value]");
  }

  @CAnnotation(
      info = "Manage gamerules",
      args = "(world) (gamerule) (value)",
      regVar = {0, 1, 2, 3},
      consoleVar = 3,
      others = false,
      multiTab = "[worlds] [gamerule] [gamerulevalue]")
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    World world = null;
    Integer integer = null;
    CMIGameRule cmiGameRule = null;
    String string = null;
    String[] argsCopy = strings;
    int value = strings.length;

    for (int argIndex = 0; argIndex < value; argIndex++) {
      String arg = argsCopy[argIndex];
      if (arg.startsWith("-p:")) {
        try {
          integer = Integer.parseInt(arg.substring(3));
          continue;
        } catch (Exception exception) {
        }
      }

      if (world == null) {
        world = cmi.getUtilManager().getWorld(arg);
        if (world != null) {
          continue;
        }
      }

      if (cmiGameRule == null) {
        cmiGameRule = CMIGameRule.get(arg);
        if (cmiGameRule != null) {
          continue;
        }
      }

      string = arg;
    }

    if (integer != null && Bukkit.getWorlds().size() >= integer && integer > 0) {
      world = (World) Bukkit.getWorlds().get(integer - 1);
    }

    if (world != null && integer == null) {
      integer = getWorldPage(world);
    }

    if (integer == null) {
      integer = 1;
    }

    if (world == null) {
      world = (World) Bukkit.getWorlds().get(0);
      integer = 1;
    }

    World targetWorld = world;
    CMIGameRule gameRule = cmiGameRule;
    if (gameRule != null && string != null) {
      Object ruleValue = gameRule.get(targetWorld);
      if (ruleValue == null) {
        return null;
      }

      if (gameRule.getType() == Boolean.class) {
        try {
          boolean flag = Boolean.parseBoolean(string);
          CMIScheduler.runTask(
              cmi,
              () -> {
                gameRule.set(targetWorld, flag);
                CMILC.info(
                    gamerule.class,
                    commandSender,
                    "updated",
                    "[rule]",
                    gameRule.getName(),
                    "[world]",
                    targetWorld.getName(),
                    "[value]",
                    flag);
              });
          return true;
        } catch (Exception exception2) {
        }
      } else if (gameRule.getType() == Integer.class) {
        try {
          int numeric = Integer.parseInt(string);
          CMIScheduler.runTask(
              cmi,
              () -> {
                gameRule.set(targetWorld, numeric);
                CMILC.info(
                    gamerule.class,
                    commandSender,
                    "updated",
                    "[rule]",
                    gameRule.getName(),
                    "[world]",
                    targetWorld.getName(),
                    "[value]",
                    numeric);
              });
          return true;
        } catch (Exception exception3) {
        }
      }
    }

    if (commandSender instanceof Player) {
      this.openGui((Player) commandSender, integer);
    }

    return true;
  }

  private static int getPageCount() {
    int value = 0;

    for (int index = 0; index < Bukkit.getWorlds().size(); index++) {
      World world = (World) Bukkit.getWorlds().get(index);
      value = (int) (value + Math.ceil(world.getGameRules().length / 45.0));
    }

    return value;
  }

  private static World getWorldFromPage(int value) {
    int index = 0;

    for (int worldIndex = 0; worldIndex < Bukkit.getWorlds().size(); worldIndex++) {
      World world = (World) Bukkit.getWorlds().get(worldIndex);
      index = (int) (index + Math.ceil(world.getGameRules().length / 45.0));
      if (index >= value) {
        return world;
      }
    }

    return (World) Bukkit.getWorlds().get(0);
  }

  private static int getWorldNumber(World world) {
    for (int value = 0; value < Bukkit.getWorlds().size(); value++) {
      if (Bukkit.getWorlds().get(value) == world) {
        return value;
      }
    }

    return 0;
  }

  private static int getWorldPage(World world) {
    int value = 0;

    for (int index = 0; index < Bukkit.getWorlds().size(); index++) {
      value++;
      if (Bukkit.getWorlds().get(index) == world) {
        return value;
      }

      value = (int) (value + (Math.ceil(world.getGameRules().length / 45.0) - 1.0));
    }

    return 1;
  }

  private static CMIGameRule<?>[] getGameRulesFromPage(int value) {
    World world = getWorldFromPage(value);
    int worldNumber = getWorldNumber(world);
    int startSlot = (value - 1) * 45;
    startSlot -= (int) (worldNumber * (Math.ceil(CMIGameRule.getValues().length / 45.0) * 45.0));
    int endSlot = startSlot + 45;
    ArrayList<CMIGameRule<?>> arrayList = new ArrayList();

    for (int slot = startSlot; slot < endSlot && slot < CMIGameRule.getValues().length; slot++) {
      arrayList.add(CMIGameRule.getValues()[slot]);
    }

    return arrayList.toArray(new CMIGameRule[arrayList.size()]);
  }

  private void openGui(final Player player, int value) {
    value =
        CMINumber.clamp(
            value,
            1,
            (int)
                (Bukkit.getWorlds().size()
                    * Math.ceil(((World) Bukkit.getWorlds().get(0)).getGameRules().length / 45.0)));
    final World world = getWorldFromPage(value);
    CMIGui cmiGui =
        new CMIGui(player) {
          public void pageChange(int page) {
            gamerule.this.openGui(player, page);
          }
        };
    PageInfo pageInfo = new PageInfo(1, getPageCount(), value);
    cmiGui.setInvSize(GUIRows.r6);
    cmiGui.setTitle(world.getName());
    CMIGameRule[] cmiGameRules;
    int count = (cmiGameRules = getGameRulesFromPage(value)).length;

    for (int ruleIndex = 0; ruleIndex < count; ruleIndex++) {
      final CMIGameRule cmiGameRule = cmiGameRules[ruleIndex];
      Object object = cmiGameRule.get(world);
      ItemStack itemStack = CMIMaterial.WHITE_WOOL.newItemStack();
      if (object != null) {
        if (cmiGameRule.getType() == Boolean.class) {
          if ((Boolean) object) {
            itemStack = CMIMaterial.GREEN_WOOL.newItemStack();
          } else {
            itemStack = CMIMaterial.RED_WOOL.newItemStack();
          }
        } else if (cmiGameRule.getType() == Integer.class) {
          itemStack = CMIMaterial.BLUE_WOOL.newItemStack();
        }

        CMIGuiButton cmiGuiButton =
            new CMIGuiButton(itemStack) {
              public void click(GUIClickType guiClickType) {
                CMIScheduler.runTask(
                    CMI.getInstance(),
                    () -> {
                      if (cmiGameRule.getType() == Integer.class) {
                        Integer integer = null;

                        try {
                          integer = (Integer) cmiGameRule.get(world);
                        } catch (Throwable throwable) {
                          return;
                        }

                        switch (guiClickType) {
                          case Left:
                            cmiGameRule.set(world, integer + 1);
                            break;
                          case LeftShift:
                            cmiGameRule.set(world, integer + 10);
                            break;
                          case Right:
                            cmiGameRule.set(world, integer - 1);
                            break;
                          case RightShift:
                            cmiGameRule.set(world, integer - 10);
                        }
                      } else if (cmiGameRule.getType() == Boolean.class) {
                        Boolean currentValue = null;

                        try {
                          currentValue = (Boolean) cmiGameRule.get(world);
                        } catch (Throwable throwable2) {
                          return;
                        }

                        cmiGameRule.set(world, !currentValue);
                        if (!currentValue) {
                          this.setItem(CMIMaterial.GREEN_WOOL);
                        } else {
                          this.setItem(CMIMaterial.RED_WOOL);
                        }
                      }

                      this.updateLooks();
                      this.update();
                    });
              }

              public void updateLooks() {
                this.setName(
                    "&r&f"
                        + CMIText.everyFirstToUpperCase(cmiGameRule.getName().replace("_", " ")));
                this.clearLore();
                this.addLore("&7" + cmiGameRule.get(world));
              }
            };
        cmiGui.addButton(cmiGuiButton);
      }
    }

    cmiGui.addPagination(pageInfo);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }
}
