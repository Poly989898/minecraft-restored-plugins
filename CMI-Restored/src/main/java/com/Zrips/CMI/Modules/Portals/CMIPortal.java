package com.Zrips.CMI.Modules.Portals;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Teleportations.CMITeleportType;
import com.Zrips.CMI.events.CMIPortalUseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CMIPortal {
  private CuboidArea area;
  private Location tpLoc;
  private Location safeLoc;
  private String bungeeServer;
  private String bungeeLocation;
  private boolean toExactBungeeLocation = true;
  private boolean performCommandsWithoutTp = PortalManager.performCmd;
  private World world;
  private String worldName;
  private boolean enabled = true;
  private boolean showParticles = true;
  private CMIPortalParticleOrigin particleOrigin = CMIPortalParticleOrigin.Any;
  private int particleAmount = 10;
  private boolean requiresPermission = false;
  private boolean kickBack = true;
  private boolean informOnMissingPerm = false;
  private boolean cancelCmdWarmup = false;
  private int percentToHide = 0;
  private int activationRange = 16;
  private CMIEffect cmiEffect = null;
  private List<String> commands = null;
  private String name;
  private boolean particlesByPermission = false;
  private Set<UUID> particleForPlayers = Collections.synchronizedSet(new HashSet<>());

  public CMIPortal() {
    this.area = new CuboidArea(null);
  }

  public boolean containsLoc(Location location) {
    return this.area.containsLoc(location);
  }

  public boolean containsLoc(Location location, int value) {
    return this.area.containsLoc(location, value);
  }

  public CuboidArea getArea() {
    return this.area;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String string) {
    this.name = string;
  }

  public World getWorld() {
    return this.world == null && this.getWorldName() != null
        ? CMI.getInstance().getUtilManager().getWorld(this.worldName)
        : this.world;
  }

  public void setWorld(World newWorld) {
    this.world = newWorld;
    if (newWorld != null) {
      this.worldName = newWorld.getName();
    }
  }

  public boolean checkCollision(CuboidArea cuboidArea) {
    return this.area != null && this.area.checkCollision(cuboidArea);
  }

  public boolean teleport(Player player) {
    if (player != null && player.isOnline()) {
      CMIPortalUseEvent cmiPortalUseEvent = new CMIPortalUseEvent(player, this);
      Bukkit.getServer().getPluginManager().callEvent(cmiPortalUseEvent);
      if (cmiPortalUseEvent.isCancelled()) {
        return false;
      }

      boolean flag = true;
      if (this.tpLoc != null && this.getBungeeServer() == null) {
        CMI.getInstance()
            .getTeleportHandler()
            .teleportPlayer(player, this.tpLoc, false, CMITeleportType.Portal)
            .thenAccept(
                cmiTeleportFeedback -> {
                  if (cmiTeleportFeedback.isSuccess()) {
                    CMIScheduler.runAtEntity(
                        CMI.getInstance(),
                        player,
                        () ->
                            CMI.getInstance()
                                .getSpecializedCommandManager()
                                .processCmds(
                                    "CMIPortal-" + this.getName(),
                                    this.getCommands(player),
                                    player));
                  }
                });
        return true;
      }

      if (this.getBungeeServer() != null) {
        if (this.isToExactBungeeLocation()) {
          CMI.getInstance()
              .getBungeeCordManager()
              .teleporToLocation(
                  this.getBungeeServer(),
                  player.getUniqueId(),
                  this.getBungeeLocation(),
                  player.getLocation(),
                  CMIList.listToString(this.getCommands(player), ";;"));
        }

        CMI.getInstance().getBungeeCordManager().connectToServer(player, this.getBungeeServer());
        return true;
      } else {
        if (this.getPerformCommandsWithoutTp()) {
          CMI.getInstance()
              .getSpecializedCommandManager()
              .processCmds("CMIPortal-" + this.getName(), this.getCommands(player), player);
        }

        return flag;
      }
    } else {
      return false;
    }
  }

  public Location loadTpLoc(Object object) {
    this.tpLoc = (Location) object;
    return this.tpLoc;
  }

  public CuboidArea loadBounds(String string) throws Exception {
    if (string != null && string.contains(":")) {
      String[] strings = string.split(":");

      try {
        double number = Double.parseDouble(strings[0]);
        double firstY = Double.parseDouble(strings[1]);
        double firstZ = Double.parseDouble(strings[2]);
        double secondX = Double.parseDouble(strings[3]);
        double secondY = Double.parseDouble(strings[4]);
        double secondZ = Double.parseDouble(strings[5]);
        CuboidArea cuboidArea =
            new CuboidArea(
                new Location(this.world, number, firstY, firstZ),
                new Location(this.world, secondX, secondY, secondZ));
        this.setArea(cuboidArea, false);
      } catch (Exception exception) {
        exception.printStackTrace();
        throw new Exception("Invalid portal physical location...");
      }

      return this.area;
    } else {
      throw new Exception("Invalid portal physical location...");
    }
  }

  public Location getTpLoc() {
    return this.tpLoc;
  }

  public void setTpLoc(Location location) {
    this.tpLoc = location;
  }

  public void setArea(CuboidArea cuboidArea, boolean flag) {
    this.area = cuboidArea;
    if (cuboidArea.getWorld() != null) {
      this.world = cuboidArea.getWorld();
      this.worldName = cuboidArea.getWorld().getName();
    }

    if (flag) {
      this.particleAmount = (int) cuboidArea.getSize() / 3;
      if (this.particleAmount < 20) {
        this.particleAmount = 20;
      }

      if (this.particleAmount > 300) {
        this.particleAmount = 300;
      }
    }
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean flag) {
    this.enabled = flag;
  }

  public boolean isShowParticles() {
    return this.showParticles;
  }

  public void setShowParticles(boolean flag) {
    this.showParticles = flag;
  }

  public int getParticleAmount() {
    return this.particleAmount;
  }

  public void setParticleAmount(int value) {
    this.particleAmount = value;
    if (this.particleAmount < 0) {
      this.particleAmount = 0;
    }

    if (this.particleAmount > 300) {
      this.particleAmount = 300;
    }
  }

  public int getPercentToHide() {
    return this.percentToHide;
  }

  public void setPercentToHide(int value) {
    this.percentToHide = value;
    if (this.percentToHide < 0) {
      this.percentToHide = 0;
    }

    if (this.percentToHide > 99) {
      this.percentToHide = 99;
    }
  }

  public int getActivationRange() {
    return this.activationRange;
  }

  public void setActivationRange(int value) {
    this.activationRange = value;
    if (this.activationRange < 2) {
      this.activationRange = 2;
    }

    if (this.activationRange > 128) {
      this.activationRange = 128;
    }
  }

  public List<String> getCommands() {
    if (this.commands == null) {
      this.setCommands(CMI.getInstance().getPortalManager().getCommandsOnTeleport());
    }

    return this.commands;
  }

  public List<String> getCommands(Player player) {
    Snd snd = new Snd().setSender(player).setTarget(player);
    ArrayList<String> arrayList = new ArrayList();

    for (String string : this.getCommands()) {
      arrayList.add(CMI.getInstance().getLM().updateSnd(snd, string));
    }

    return arrayList;
  }

  public void setCommands(List<String> list) {
    if (this.commands == null) {
      this.commands = new ArrayList<>();
    }

    this.commands.addAll(list);
  }

  public boolean getPerformCommandsWithoutTp() {
    return this.performCommandsWithoutTp;
  }

  public void setPerformCommandsWithoutTp(boolean flag) {
    this.performCommandsWithoutTp = flag;
  }

  public String getBungeeServer() {
    return this.bungeeServer;
  }

  public void setBungeeServer(String string) {
    this.bungeeServer = string;
  }

  public String getBungeeLocation() {
    return this.bungeeLocation;
  }

  public void setBungeeLocation(String string) {
    this.bungeeLocation = string;
  }

  public Location getSafeLoc() {
    return this.safeLoc;
  }

  public void setSafeLoc(Location location) {
    this.safeLoc = location;
  }

  public boolean isToExactBungeeLocation() {
    return this.toExactBungeeLocation;
  }

  public void setToExactBungeeLocation(boolean flag) {
    this.toExactBungeeLocation = flag;
  }

  public boolean isRequiresPermission() {
    return this.requiresPermission;
  }

  public void setRequiresPermission(boolean flag) {
    this.requiresPermission = flag;
  }

  public boolean isInformOnMissingPerm() {
    return this.informOnMissingPerm;
  }

  public void setInformOnMissingPerm(boolean flag) {
    this.informOnMissingPerm = flag;
  }

  public String getWorldName() {
    return this.worldName == null && this.world != null ? this.world.getName() : this.worldName;
  }

  public void setWorldName(String string) {
    this.worldName = string;
    this.world = CMI.getInstance().getUtilManager().getWorld(string);
  }

  public synchronized Set<UUID> getParticleForPlayers() {
    return new HashSet<>(this.particleForPlayers);
  }

  public synchronized void setParticleForPlayers(Set<UUID> set) {
    this.particleForPlayers = set;
  }

  public Set<UUID> removeParticleLimitations(Player player) {
    return this.removeParticleLimitations(player.getUniqueId());
  }

  public synchronized Set<UUID> removeParticleLimitations(UUID uuid) {
    this.particleForPlayers.remove(uuid);
    return this.particleForPlayers;
  }

  public synchronized Set<UUID> updateParticleLimitations(Player player) {
    if (player != null
        && (!this.isParticlesByPermission()
            || player.isOnline()
                && PermissionsManager.CMIPerm.command_portal_$1.hasPermission(
                    player, this.getName()))) {
      this.particleForPlayers.add(player.getUniqueId());
    }

    return this.particleForPlayers;
  }

  public boolean isParticlesByPermission() {
    return this.particlesByPermission;
  }

  public void setParticlesByPermission(boolean flag) {
    this.particlesByPermission = flag;
  }

  public boolean isKickBack() {
    return this.kickBack;
  }

  public void setKickBack(boolean flag) {
    this.kickBack = flag;
  }

  @Deprecated
  public CMIParticle getEffect() {
    return this.getCMIEffect().getParticle();
  }

  @Deprecated
  public void setEffect(CMIParticle cmiParticle) {
    this.getCMIEffect().setParticle(cmiParticle);
  }

  public CMIEffect getCMIEffect() {
    if (this.cmiEffect == null) {
      this.cmiEffect = new CMIEffect(CMIParticle.DUST);
      this.cmiEffect.setColor(Color.fromRGB(125, 125, 125));
    }

    return this.cmiEffect;
  }

  public void setCMIEffect(CMIEffect effect) {
    this.cmiEffect = effect;
  }

  @Deprecated
  public CMIPortal setEffectColor(Color color) {
    this.getCMIEffect().setColor(color);
    return this;
  }

  public boolean isCancelCmdWarmup() {
    return this.cancelCmdWarmup;
  }

  public void setCancelCmdWarmup(boolean flag) {
    this.cancelCmdWarmup = flag;
  }

  public CMIPortalParticleOrigin getParticleOrigin() {
    return this.particleOrigin;
  }

  public void setParticleOrigin(CMIPortalParticleOrigin cmiPortalParticleOrigin) {
    this.particleOrigin =
        cmiPortalParticleOrigin == null ? CMIPortalParticleOrigin.Any : cmiPortalParticleOrigin;
  }
}
