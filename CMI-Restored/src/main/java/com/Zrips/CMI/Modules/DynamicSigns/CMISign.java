package com.Zrips.CMI.Modules.DynamicSigns;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Portals.CuboidArea;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIBlock;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Version.PaperMethods.PaperLib;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class CMISign {
  private CuboidArea area;
  private List<String> lines = new ArrayList<>();
  private Sign sign;
  private double updateIntervalSec = 5.0;
  private Long lastUpdate = 0L;
  private boolean personal = true;
  private int activationRange = 8;
  private int currentLine = 0;
  private CMILocation loc;

  public CMISign(CMILocation cmiLocation) {
    this.loc = cmiLocation;
  }

  public World getWorld() {
    return this.loc.getWorld();
  }

  public CMILocation getLoc() {
    return this.loc;
  }

  public List<String> getLines() {
    return this.lines;
  }

  public String getLine(int value) {
    return this.lines.size() - 1 >= value && value >= 0
        ? (this.lines.get(value) == null ? "" : this.lines.get(value))
        : "";
  }

  public List<String> getLinesAsList() {
    ArrayList<String> arrayList = new ArrayList();

    for (String string : this.lines) {
      arrayList.add(string);
    }

    return arrayList;
  }

  public void setLines(List<String> list) {
    this.lines = list;
  }

  public void setLines(String[] strings) {
    this.lines = Arrays.asList(strings);
  }

  public CuboidArea getBasicArea() {
    return this.area != null
        ? this.area
        : new CuboidArea(
            this.getLoc()
                .clone()
                .add(-this.activationRange, -this.activationRange, -this.activationRange),
            this.getLoc()
                .clone()
                .add(this.activationRange, this.activationRange, this.activationRange));
  }

  public CompletableFuture<CuboidArea> getArea() {
    if (this.area != null) {
      return CompletableFuture.completedFuture(this.area);
    }

    var completableFuture = PaperLib.getChunkAtAsync(this.getLoc());
    return completableFuture.thenCompose(
        object -> {
          int value = this.getLoc().getBlockX() & 15;
          int localZ = this.getLoc().getBlockZ() & 15;
          Block block = object.getBlock(value, this.getLoc().getBlockY(), localZ);
          if (block.getState() instanceof Sign) {
            try {
              CMIBlock cmiBlock = new CMIBlock(block);
              BlockFace blockFace = cmiBlock.getFacing();
              if (blockFace != null) {
                this.area =
                    new CuboidArea(
                        this.getLoc()
                            .clone()
                            .add(
                                blockFace.getModX() == 0 ? -this.activationRange / 2 : 0,
                                -this.activationRange / 2,
                                blockFace.getModZ() == 0 ? -this.activationRange / 2 : 0),
                        this.getLoc()
                            .clone()
                            .add(
                                blockFace.getModX() == 0
                                    ? this.activationRange / 2
                                    : this.activationRange * blockFace.getModX(),
                                this.activationRange / 2,
                                blockFace.getModZ() == 0
                                    ? this.activationRange / 2
                                    : this.activationRange * blockFace.getModZ()));
              } else {
                this.area =
                    new CuboidArea(
                        this.getLoc()
                            .clone()
                            .add(
                                -this.activationRange / 2,
                                -this.activationRange / 2,
                                -this.activationRange / 2),
                        this.getLoc()
                            .clone()
                            .add(
                                this.activationRange / 2,
                                this.activationRange / 2,
                                this.activationRange / 2));
              }
            } catch (Exception | Error value3) {
              this.area =
                  new CuboidArea(
                      this.getLoc()
                          .clone()
                          .add(-this.activationRange, -this.activationRange, -this.activationRange),
                      this.getLoc()
                          .clone()
                          .add(this.activationRange, this.activationRange, this.activationRange));
            }
          } else {
            this.area =
                new CuboidArea(
                    this.getLoc()
                        .clone()
                        .add(-this.activationRange, -this.activationRange, -this.activationRange),
                    this.getLoc()
                        .clone()
                        .add(this.activationRange, this.activationRange, this.activationRange));
          }

          return CompletableFuture.completedFuture(this.area);
        });
  }

  public void setArea(CuboidArea cuboidArea) {
    this.area = cuboidArea;
  }

  public void setSign(Sign signState) {
    this.sign = signState;
  }

  public boolean isTimeToUpdate() {
    return this.lastUpdate.longValue() + this.updateIntervalSec * 1000.0 - 10.0
        <= System.currentTimeMillis();
  }

  public void setAsUpdated() {
    this.lastUpdate = System.currentTimeMillis();
  }

  public void update() {
    this.update(null);
  }

  public CompletableFuture<Sign> getSign() {
    CompletableFuture completableFuture = new CompletableFuture();
    CMIScheduler.runAtLocation(
        CMI.getInstance(),
        this.loc,
        () -> {
          try {
            if (this.sign == null) {
              Block block = this.loc.getBlock();
              if (!(block.getState() instanceof Sign)) {
                completableFuture.complete(null);
                return;
              }

              this.sign = (Sign) block.getState();
            }

            completableFuture.complete(this.sign);
          } catch (Exception exception) {
            completableFuture.completeExceptionally(exception);
          }
        });
    return completableFuture;
  }

  public void update(Player player) {
    try {
      CMIScheduler.runTaskAsynchronously(
          CMI.getInstance(),
          () -> {
            String[] strings = new String[4];
            int value = this.currentLine;
            if (value >= this.lines.size()) {
              value = this.lines.size() - 1;
            }

            if (value - 1 >= this.lines.size()) {
              value = 0;
            }

            int lineCount = 0;

            for (int lineSlot = 0; lineSlot < 4; lineSlot++) {
              String string = CMIChatColor.colorize(this.getLine(value));
              if (string == null) {
                string = "";
              }

              if (string.equals("_")) {
                string = "";
              }

              string =
                  CMI.getInstance().getPlaceholderAPIManager().updatePlaceHolders(player, string);
              string = CMIChatColor.colorize(string);
              strings[lineSlot] = string;
              if (++value >= this.lines.size()) {
                value = 0;
              }

              if (++lineCount >= this.lines.size()) {
                break;
              }
            }

            this.getSign()
                .thenAccept(
                    targetSign -> {
                      if (player == null) {
                        for (int lineSlot = 0; lineSlot < 4; lineSlot++) {
                          targetSign.setLine(lineSlot, strings[lineSlot]);
                        }

                        CMIScheduler.runTask(
                            CMI.getInstance(),
                            () -> {
                              if (targetSign.getLocation().getChunk().isLoaded()) {
                                targetSign.update();
                              }
                            });
                      } else {
                        player.sendSignChange(targetSign.getLocation(), strings);
                      }
                    });
          });
    } catch (Exception | Error value5) {
      value5.printStackTrace();
    }
  }

  public double getUpdateIntervalSec() {
    return this.updateIntervalSec;
  }

  public void setUpdateIntervalSec(double number) {
    this.updateIntervalSec = number;
    if (this.updateIntervalSec < 0.0) {
      this.updateIntervalSec = 0.0;
    }
  }

  public boolean isPersonal() {
    return this.personal;
  }

  public void setPersonal(boolean flag) {
    this.personal = flag;
  }

  public int getActivationRange() {
    return this.activationRange;
  }

  public void setActivationRange(int value) {
    this.activationRange = value;
    if (this.activationRange < 1) {
      this.activationRange = 1;
    }
  }

  public void updateCurrentLine() {
    if (this.lines.size() > 4) {
      this.currentLine++;
    }

    if (this.currentLine >= this.lines.size()) {
      this.currentLine = 0;
    }

    if (this.lines.size() <= 4) {
      this.currentLine = 0;
    }
  }
}
