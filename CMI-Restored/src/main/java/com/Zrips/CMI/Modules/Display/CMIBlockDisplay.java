package com.Zrips.CMI.Modules.Display;

import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIVector3D;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CMIBlockDisplay extends CMIDisplay {
  private BlockDisplay td;

  public CMIBlockDisplay(Location location) {
    super(CMIDisplayType.Block, location);
    if (location != null) {
      try {
        this.td = (BlockDisplay) this.display;
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }

      this.setBillboard(Billboard.VERTICAL);
      this.setRange(32);
      this.loc = location;
    }
  }

  public BlockData getBlockData() {
    return this.td == null ? null : this.td.getBlock();
  }

  public void setBlockData(BlockData blockData) {
    this.td.setBlock(blockData);
  }

  public void setItemDisplayTransform(Transformation transformation) {
    this.td.setTransformation(transformation);
  }

  public Transformation getBlockTransform() {
    return this.td.getTransformation();
  }

  public void setScale(CMIVector3D cmiVector3D) {
    this.modifyScale(cmiVector3D);
  }

  private void modifyScale(CMIVector3D cmiVector3D) {
    Transformation transformation = this.td.getTransformation();
    Vector3f vector3f = transformation.getScale();
    vector3f.x = (float) CMINumber.clamp(cmiVector3D.getX(), 0.01, 1000) + 1.0E-6F;
    vector3f.y = (float) CMINumber.clamp(cmiVector3D.getY(), 0.01, 1000) + 1.0E-6F;
    vector3f.z = (float) CMINumber.clamp(cmiVector3D.getZ(), 0.01, 1000) + 1.0E-6F;
    this.td.setTransformation(transformation);
  }

  public void setFacing(double number, double yValue) {
    this.setFacing(new CMIVector3D(number, yValue, 0.0));
  }

  public void setFacing(CMIVector3D cmiVector3D) {
    this.facing(cmiVector3D);
  }

  private void facing(CMIVector3D cmiVector3D) {
    Transformation transformation = this.td.getTransformation();
    Quaternionf quaternionf = transformation.getLeftRotation();
    float floatValue = (float) Math.toRadians(cmiVector3D.getX()) + 1.0E-10F;
    float radiansY = (float) Math.toRadians(cmiVector3D.getY()) + 1.0E-10F;
    float radiansZ = (float) Math.toRadians(cmiVector3D.getZ()) + 1.0E-10F;
    quaternionf.identity().rotateY(floatValue).rotateX(radiansY).rotateZ(radiansZ);
    this.td.setTransformation(transformation);
  }

  public void setDepth(double number) {
    Transformation transformation = this.td.getTransformation();
    Vector3f vector3f = transformation.getScale();
    vector3f.z = (float) CMINumber.clamp(number, 0.0, 1000);
    this.td.setTransformation(transformation);
  }

  public void setScale(double number) {
    if (this.display != null) {
      Transformation transformation = this.td.getTransformation();
      Vector3f vector3f = transformation.getScale();
      vector3f.x = (float) CMINumber.clamp(number, 0.01, 1000);
      vector3f.y = (float) CMINumber.clamp(number, 0.01, 1000);
      vector3f.z = (float) CMINumber.clamp(number, 0.01, 1000);
      this.td.setTransformation(transformation);
    }
  }

  public float getDisplayHeight() {
    return this.td.getDisplayHeight();
  }
}
