package com.Zrips.CMI.Modules.Selection;

import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Container.CMIVectorInt3D;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class CMISelectionPoint {
  private World selectionWorld = null;
  private CMIVector3D point = null;
  private CMISelectionVisualBlock selectionVisualBlock = null;

  public CMISelectionPoint() {}

  public CMISelectionPoint(World world, CMIVectorInt3D cmiVectorInt3D) {
    this(
        world,
        new CMIVector3D(cmiVectorInt3D.getX(), cmiVectorInt3D.getY(), cmiVectorInt3D.getZ()));
  }

  public CMISelectionPoint(World world, CMIVector3D cmiVector3D) {
    this.setSelectionWorld(world);
    this.point = cmiVector3D;
  }

  @Nullable
  public CMIVector3D getPoint() {
    return this.point;
  }

  @Nullable
  public Location toLocation() {
    return !this.isValid() ? null : this.point.toLocation(this.selectionWorld);
  }

  public boolean isValid() {
    return this.selectionWorld != null && this.point != null;
  }

  public World getSelectionWorld() {
    return this.selectionWorld;
  }

  public CMISelectionPoint setSelectionWorld(World world) {
    this.selectionWorld = world;
    return this;
  }

  public CompletableFuture<Boolean> showSelectionVisualBlock(Player player) {
    return !Version.isCurrentEqualOrHigher(Version.v1_19_R3)
        ? CompletableFuture.completedFuture(false)
        : this.getSelectionVisualBlock().show(player, this.getPoint());
  }

  public CompletableFuture<Boolean> destroySelectionVisualBlock(Player player) {
    return !Version.isCurrentEqualOrHigher(Version.v1_19_R3)
        ? CompletableFuture.completedFuture(false)
        : this.getSelectionVisualBlock().destroy(player);
  }

  public CMISelectionVisualBlock getSelectionVisualBlock() {
    if (this.selectionVisualBlock == null) {
      this.selectionVisualBlock = new CMISelectionVisualBlock();
    }

    return this.selectionVisualBlock;
  }
}
