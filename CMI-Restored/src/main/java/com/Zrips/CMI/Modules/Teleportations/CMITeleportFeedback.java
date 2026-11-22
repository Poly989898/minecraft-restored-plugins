package com.Zrips.CMI.Modules.Teleportations;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.list.tp;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Time.CMITimeManager;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMITeleportFeedback {
  private boolean success = true;
  private Location location;
  private CMITeleportCondition condition = CMITeleportCondition.Good;
  private double fallDistance = 0.0;
  private CMIMaterial floor = null;

  public CMITeleportFeedback(Location newLocation, boolean flag) {
    this.success = flag;
    this.location = newLocation;
  }

  public boolean isSuccess() {
    return this.success;
  }

  public CMITeleportFeedback setSuccess(boolean flag) {
    this.success = flag;
    return this;
  }

  public Location getLocation() {
    return this.location;
  }

  public CMITeleportFeedback setLocation(Location newLocation) {
    this.location = newLocation;
    return this;
  }

  public void showFeedbackMessage(CommandSender commandSender, Player player) {
    if (!this.getCondition().equals(CMITeleportCondition.Good)
        && !this.getCondition().equals(CMITeleportCondition.NoPermission)) {
      if (commandSender instanceof Player) {
        CMI.getInstance()
            .getTeleportHandler()
            .addBadTeleport(((Player) commandSender).getUniqueId(), this.getLocation());
      } else {
        CMI.getInstance()
            .getTeleportHandler()
            .addBadTeleport(player.getUniqueId(), this.getLocation());
      }

      String string =
          CMILC.getIM(
              tp.class,
              "bad." + this.getCondition().name().toLowerCase(),
              "[distance]",
              this.getFallDistance(),
              "[material]",
              this.getFloor() == null ? "" : this.getFloor().getTranslatedName());
      if (commandSender != null && !commandSender.getName().equalsIgnoreCase(player.getName())) {
        CMIMessages.sendMessage(
            commandSender,
            CMILC.getIM(tp.class, "bad.msg", "[reasson]", string, "[reason]", string));
        CMIMessages.sendMessage(
            commandSender,
            CMILC.getIM(
                tp.class,
                "bad.extraMsg",
                "[time]",
                CMITimeManager.to24hourShort(
                    (long) (CMI.getInstance().getTeleportManager().getTpBypassTime() * 1000))));
      } else {
        CMIMessages.sendMessage(
            player, CMILC.getIM(tp.class, "bad.msg", "[reasson]", string, "[reason]", string));
        CMIMessages.sendMessage(
            player,
            CMILC.getIM(
                tp.class,
                "bad.extraMsg",
                "[time]",
                CMITimeManager.to24hourShort(
                    (long) (CMI.getInstance().getTeleportManager().getTpBypassTime() * 1000))));
      }
    }
  }

  public CMITeleportCondition getCondition() {
    return this.condition;
  }

  public boolean isGood() {
    return this.getCondition().equals(CMITeleportCondition.Good);
  }

  public void setCondition(CMITeleportCondition cmiTeleportCondition) {
    this.condition = cmiTeleportCondition;
  }

  public double getFallDistance() {
    return this.fallDistance;
  }

  public void setFallDistance(double number) {
    this.fallDistance = number;
  }

  public CMIMaterial getFloor() {
    return this.floor;
  }

  public CMITeleportFeedback setFloor(CMIMaterial cmiMaterial) {
    this.floor = cmiMaterial;
    return this;
  }
}
