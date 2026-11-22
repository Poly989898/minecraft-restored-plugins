package com.Zrips.CMI.Modules.Homes;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMISoundPreset;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportFeedback;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.Zrips.CMI.commands.list.home;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class CmiHome {
  private CMILocation loc;
  private String name;
  private CMIHomeSettings settings = null;

  public CmiHome(String string, CMILocation cmiLocation) {
    this.name = string;
    this.loc = cmiLocation;
  }

  public CmiHome(String string, CMILocation cmiLocation, boolean flag) {
    this.name = string;
    this.loc = cmiLocation;
    this.setPrivate(flag);
  }

  public CMILocation getLoc() {
    if (this.loc != null) {
      this.loc.recheck();
    }

    return this.loc;
  }

  public String getName() {
    return this.name;
  }

  public boolean isPrivate() {
    return this.settings != null && this.settings.isPrivate();
  }

  public void setPrivate(boolean flag) {
    if (flag || this.settings != null) {
      this.getSettings().setPrivate(flag);
    }
  }

  public CMIHomeSettings getSettings() {
    if (this.settings == null) {
      this.settings = new CMIHomeSettings();
    }

    return this.settings;
  }

  public CMIMaterial getIconMaterial() {
    CMIMaterial cmiMaterial = this.getMaterial();
    if (cmiMaterial == null
        && Version.isCurrentEqualOrLower(Version.v1_13_R2)
        && CMI.getInstance().getHomeManager().isPickRealBlock()) {
      if (this.loc.getY() % 1.0 != 0.0 && !this.loc.getBlockCMIType().isAir()) {
        cmiMaterial = this.loc.getBlockCMIType();
      }

      if (cmiMaterial == null) {
        CMILocation cmiLocation = new CMILocation(this.loc.clone().add(0.0, -1.0, 0.0));
        CMIMaterial blockMaterial = cmiLocation.getBlockCMIType();
        if (!blockMaterial.isAir()) {
          cmiMaterial = blockMaterial;
        }
      }

      if (cmiMaterial == null) {
        CMILocation belowLocation = new CMILocation(this.loc.clone());
        belowLocation.setY(this.loc.getHighestBlockYAt() - 1);
        if (!belowLocation.getBlockCMIType().isAir()) {
          cmiMaterial = belowLocation.getBlockCMIType();
        }
      }
    }

    if (cmiMaterial == null) {
      int value =
          new BigInteger((this.getName().isEmpty() ? "a" : this.getName()).getBytes()).intValue()
              % 4;
      CMIMaterial wallMaterial = CMIMaterial.WHITE_WOOL;
      switch (value) {
        case 1:
          wallMaterial = CMIMaterial.WHITE_CARPET;
          break;
        case 2:
          wallMaterial = CMIMaterial.WHITE_STAINED_GLASS;
          break;
        case 3:
        case 4:
          wallMaterial = CMIMaterial.WHITE_BANNER;
      }

      cmiMaterial =
          CMIMaterial.getByColorId(
              wallMaterial,
              new BigInteger((this.getName().isEmpty() ? "a" : this.getName()).getBytes())
                      .intValue()
                  % 15);
      if (cmiMaterial == CMILib.getInstance().getConfigManager().getGUIEmptyField().getCMIType()) {
        cmiMaterial =
            CMIMaterial.getByColorId(
                wallMaterial,
                new BigInteger((this.getName().isEmpty() ? "a" : this.getName() + "a").getBytes())
                        .intValue()
                    % 15);
      }

      if (cmiMaterial == CMILib.getInstance().getConfigManager().getGUIEmptyField().getCMIType()) {
        cmiMaterial =
            CMIMaterial.getByColorId(
                wallMaterial,
                new BigInteger((this.getName().isEmpty() ? "a" : this.getName() + "b").getBytes())
                        .intValue()
                    % 15);
      }
    }

    if (cmiMaterial != null && cmiMaterial.equals(CMIMaterial.WATER)) {
      cmiMaterial = CMIMaterial.WATER_BUCKET;
    } else if (cmiMaterial != null && cmiMaterial.equals(CMIMaterial.LAVA)) {
      cmiMaterial = CMIMaterial.LAVA_BUCKET;
    }

    try {
      if (cmiMaterial != null && !cmiMaterial.getMaterial().isItem()) {
        cmiMaterial = CMIMaterial.getRandom(CMIMaterial.WHITE_WOOL);
      }
    } catch (Exception | Error value2) {
    }

    if (cmiMaterial == null) {
      cmiMaterial = CMIMaterial.WHITE_WOOL;
    }

    return cmiMaterial;
  }

  public CMIMaterial getMaterial() {
    return this.settings == null ? null : this.getSettings().getMaterial();
  }

  public void setMaterial(CMIMaterial cmiMaterial) {
    if (cmiMaterial == null || cmiMaterial != CMIMaterial.NONE && !cmiMaterial.isAir()) {
      this.getSettings().setMaterial(cmiMaterial);
    }
  }

  public Integer getSlot() {
    return this.settings == null ? 0 : this.getSettings().getSlot();
  }

  public void setSlot(Integer integer) {
    if (integer == null || integer >= 0) {
      this.getSettings().setSlot(integer);
    }
  }

  public boolean isBed() {
    return this.settings == null ? false : this.getSettings().isBed();
  }

  public void setBed(boolean flag) {
    if (flag || this.settings != null) {
      this.getSettings().setBed(flag);
    }
  }

  public boolean isFavorite() {
    return this.settings == null ? false : this.getSettings().isFav();
  }

  public void setFavorite(boolean flag) {
    if (flag || this.settings != null) {
      this.getSettings().setFav(flag);
    }
  }

  public void teleportPlayerTo(
      @Nullable CommandSender commandSender, Player player, CMIUser cmiUser) {
    if (CMI.getInstance()
        .getUtilManager()
        .haveBlackListedItems(player, TeleportManager.TpAction.home)) {
      CMILC.info(home.class, commandSender, "blacklisted");
    } else if (commandSender != null
        && this.isPrivate()
        && !cmiUser.getName().equals(commandSender.getName())
        && !PermissionsManager.CMIPerm.command_home_bypassprivate.hasPermission(commandSender)) {
      CMILC.info(home.class, commandSender, "private");
    } else {
      if (this.getLoc() != null && this.getLoc().getWorld() == null) {
        this.getLoc().setWorld(null);
        this.getLoc().recheck();
      }

      if (this.getLoc() != null && this.getLoc().getWorld() != null) {
        if (!CMI.getInstance().getHomeManager().canUseHomes(commandSender)) {
          CMILC.info(home.class, commandSender, "limitReached");
        } else {
          var completableFuture = CompletableFuture.completedFuture(Material.AIR);
          if (this.isBed()) {
            completableFuture =
                CMI.getInstance().getTeleportHandler().getBlockType(this.getLoc(), false);
          }

          completableFuture
              .<CMITeleportFeedback>thenCompose(
                  object -> {
                    CMILocation cmiLocation = this.getLoc();
                    if (this.isBed() && !CMIMaterial.isBed(object)) {
                      CMILC.info(home.class, commandSender, "missingBed");
                      return CompletableFuture.completedFuture(null);
                    }

                    if (this.isBed() && Version.isFolia()) {
                      cmiLocation = cmiLocation.clone();
                      cmiLocation.add(0.0, 0.45, 0.0);
                    }

                    return CMI.getInstance()
                        .getTeleportHandler()
                        .teleportPlayer(player, cmiLocation, CMITeleportType.Home);
                  })
              .thenAccept(
                  teleportResult -> {
                    if (teleportResult != null) {
                      if (!teleportResult.isSuccess()) {
                        teleportResult.showFeedbackMessage(commandSender, player);
                        if (commandSender instanceof Player) {
                          CMISoundPreset.TeleportFail.play((Player) commandSender);
                        }
                      } else {
                        if (commandSender instanceof Player) {
                          CMISoundPreset.TeleportHome.play(player, teleportResult.getLocation());
                        }

                        CMILC.info(
                            home.class, commandSender, "teleported", "[home]", this.getName());
                      }
                    }
                  });
        }
      } else {
        LC.info_worldDontExist.sendMessage(commandSender, new Object[0]);
      }
    }
  }
}
