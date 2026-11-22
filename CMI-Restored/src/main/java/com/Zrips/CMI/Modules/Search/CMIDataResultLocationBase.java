package com.Zrips.CMI.Modules.Search;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMIVectorInt3D;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class CMIDataResultLocationBase extends CMIDataResultBase {
  private CMIVectorInt3D vector;
  private int amount = 0;
  protected UUID uuid = null;
  private boolean checked = false;

  public CMIDataResultLocationBase(@NotNull CMIVectorInt3D cmiVectorInt3D, int value) {
    this.vector = cmiVectorInt3D;
    this.amount = value;
  }

  @NotNull
  public UUID getUUID() {
    if (this.uuid != null) {
      return this.uuid;
    }

    String string = this.vector.getX() + "," + this.vector.getY() + "," + this.vector.getZ();
    this.uuid = UUID.nameUUIDFromBytes(string.getBytes(StandardCharsets.UTF_8));
    return this.uuid;
  }

  public int getAmount() {
    return this.amount;
  }

  public CMIDataResultLocationBase addAmount(int value) {
    this.amount += value;
    return this;
  }

  @NotNull
  public CMIVectorInt3D getVector() {
    return this.vector;
  }

  @NotNull
  public Location getLocation(@NotNull World world) {
    return new Location(world, this.vector.getX(), this.vector.getY(), this.vector.getZ());
  }

  public String getValueOutput() {
    return "";
  }

  public boolean isChecked() {
    return this.checked;
  }

  public void setChecked(boolean flag) {
    this.checked = flag;
  }
}
