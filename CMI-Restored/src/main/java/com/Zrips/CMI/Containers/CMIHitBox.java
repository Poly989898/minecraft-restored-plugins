package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Portals.CuboidArea;
import java.lang.reflect.Method;
import java.util.HashMap;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class CMIHitBox {
  private Vector max;
  private Vector min;
  private Block block;
  private Entity ent;
  private World world;
  private ChunkSnapshot snapshot;
  static Method getBlockTypeId = null;

  CMIHitBox(World worldIn, Vector vector, Vector maxVector) {
    this.world = worldIn;
    this.max = maxVector;
    this.min = vector;
  }

  public CMIHitBox(Block targetBlock) {
    this.block = targetBlock;
    this.world = targetBlock.getWorld();
    if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)
        && Version.isCurrentEqualOrLower(Version.v1_18_R2)) {
      this.setMin(new Vector(1, 1, 1));
      this.setMax(new Vector(0, 0, 0));
    } else {
      CMI.getInstance().getNMS().getBlockHitBox(this);
    }
  }

  public CMIHitBox(Entity entity) {
    this.ent = entity;
    if (entity != null) {
      this.world = entity.getWorld();
      CMI.getInstance().getNMS().getEntityHitBox(this);
    }
  }

  public Vector getMax() {
    if (this.max == null) {
      this.max = new Vector(0, 0, 0);
    }

    return this.max;
  }

  public Location getCenterLocation() {
    return new Location(
        this.world,
        this.getMinGlobal().getX()
            + (this.getMaxGlobal().getX() - this.getMinGlobal().getX()) / 2.0,
        this.getMinGlobal().getY(),
        this.getMinGlobal().getZ()
            + (this.getMaxGlobal().getZ() - this.getMinGlobal().getZ()) / 2.0);
  }

  public Double getHeightAtLocation(Location location) {
    double number = 0.0;
    double step = 0.01;

    for (double scanY = location.getBlockY(); scanY <= location.getBlockY() + 1; scanY += step) {
      location.setY(scanY);
      if (this.isEmptySpace(location)) {
        number = scanY - location.getBlockY();
        break;
      }
    }

    number = (int) (number * 10000.0) / 10000.0;
    CMIMaterial cmiMaterial = null;
    if (this.getSnapshot() != null) {
      int value =
          this.getSnapshot()
              .getHighestBlockYAt(location.getBlockX() & 15, location.getBlockZ() & 15);
      value =
          CMIWorld.getMinHeight(location.getWorld()) > value
              ? CMIWorld.getMinHeight(location.getWorld())
              : value;
      int clampedY =
          CMINumber.clamp(location.getBlockY(), CMIWorld.getMinHeight(location.getWorld()), value);
      cmiMaterial =
          this.getBlockType(location.getBlockX() & 15, clampedY, location.getBlockZ() & 15);
    } else {
      cmiMaterial = CMIMaterial.get(location.getBlock());
    }

    if (number <= step) {
      if (cmiMaterial.equals(CMIMaterial.SNOW)) {
        try {
          number = (((Snow) location.getBlock().getBlockData()).getLayers() - 1) * 0.125;
        } catch (Throwable throwable) {
        }
      }

      if (number <= step) {
        CMIHitBox cmiHitBox = new CMIHitBox(location.getBlock());
        if (number < cmiHitBox.getMax().getY() - cmiHitBox.getMin().getY()) {
          number = cmiHitBox.getMax().getY() - cmiHitBox.getMin().getY();
        }
      }
    }

    return number;
  }

  public Vector getMaxGlobal() {
    if (this.block != null) {
      Location location = this.block.getLocation();
      return new Vector(
          location.getX() + this.getMax().getX(),
          location.getY() + this.getMax().getY(),
          location.getZ() + this.getMax().getZ());
    } else {
      return this.ent != null
          ? new Vector(
              this.getMax().getX() - 0.01, this.getMax().getY() - 0.01, this.getMax().getZ() - 0.01)
          : new Vector(this.getMax().getX(), this.getMax().getY(), this.getMax().getZ());
    }
  }

  public Vector getMin() {
    if (this.min == null) {
      this.min = new Vector(1, 1, 1);
    }

    return this.min;
  }

  public double getHitBoxHeight() {
    return Math.abs(this.getMax().getY() - this.getMin().getY());
  }

  public double getHitBoxWidth() {
    return Math.abs(this.getMax().getX() - this.getMin().getX());
  }

  public double getHitBoxLength() {
    return Math.abs(this.getMax().getZ() - this.getMin().getZ());
  }

  public Vector getMinGlobal() {
    if (this.block != null) {
      Location location = this.block.getLocation();
      return new Vector(
          location.getX() + this.getMin().getX(),
          location.getY() + this.getMin().getY(),
          location.getZ() + this.getMin().getZ());
    } else {
      return this.ent != null
          ? new Vector(
              this.getMin().getX() + 0.01, this.getMin().getY() + 0.01, this.getMin().getZ() + 0.01)
          : new Vector(this.getMin().getX(), this.getMin().getY(), this.getMin().getZ());
    }
  }

  public void byExactXYZ(double number, double y, double z) {
    this.byExactXYZ(number, y, z, 0.2);
  }

  private static HashMap<BlockStateType, String> getBlockStates(Block targetBlock) {
    HashMap<BlockStateType, String> hashMap = new HashMap();
    if (!Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      return hashMap;
    }

    try {
      String string = targetBlock.getBlockData().getAsString();
      if (string.contains("[")) {
        String stateText = string.split("\\[", 2)[1];
        stateText = stateText.substring(0, stateText.length() - 1);
        String[] strings;
        int value = (strings = stateText.split(",")).length;

        for (int stateIndex = 0; stateIndex < value; stateIndex++) {
          String stateEntry = strings[stateIndex];
          String[] keyValue = stateEntry.split("=", 2);
          BlockStateType blockStateType = BlockStateType.getByName(keyValue[0]);
          if (blockStateType != null) {
            hashMap.put(blockStateType, keyValue[1]);
          }
        }
      }
    } catch (Throwable throwable) {
    }

    return hashMap;
  }

  public boolean byExactXYZ(double number, double y, double z, double tolerance) {
    if (number < 0.0) {
      number++;
    }

    if (z < 0.0) {
      z++;
    }

    if (CMIMaterial.isStairs(this.block.getType())) {
      var hashMap = getBlockStates(this.block);
      String string = (String) hashMap.get(BlockStateType.facing);
      String halfValue = (String) hashMap.get(BlockStateType.half);
      String shapeValue = (String) hashMap.get(BlockStateType.shape);
      boolean flag = false;
      if (string != null) {
        String facingLower;
        switch ((facingLower = string.toLowerCase()).hashCode()) {
          case 3105789:
            if (facingLower.equals("east")) {
              if (number <= tolerance
                  && shapeValue != null
                  && shapeValue.equalsIgnoreCase("straight")) {
                flag = true;
              }

              if (shapeValue != null) {
                if (shapeValue.equalsIgnoreCase("outer_left")) {
                  if (number <= tolerance || z > 1.0 - tolerance) {
                    flag = true;
                  }
                } else if (shapeValue.equalsIgnoreCase("outer_right")) {
                  if (number <= tolerance || z <= tolerance) {
                    flag = true;
                  }
                } else if (shapeValue.equalsIgnoreCase("inner_right")) {
                  if (number <= tolerance && z <= tolerance) {
                    flag = true;
                  }
                } else if (shapeValue.equalsIgnoreCase("inner_left")
                    && number <= tolerance
                    && z > 1.0 - tolerance) {
                  flag = true;
                }
              }
            }
            break;
          case 3645871:
            if (facingLower.equals("west")) {
              if (number > 1.0 - tolerance
                  && shapeValue != null
                  && shapeValue.equalsIgnoreCase("straight")) {
                flag = true;
              }

              if (shapeValue != null) {
                if (shapeValue.equalsIgnoreCase("outer_left")) {
                  if (number > 1.0 - tolerance || z <= tolerance) {
                    flag = true;
                  }
                } else if (shapeValue.equalsIgnoreCase("outer_right")) {
                  if (number > 1.0 - tolerance || z > 1.0 - tolerance) {
                    flag = true;
                  }
                } else if (shapeValue.equalsIgnoreCase("inner_right")) {
                  if (number > 1.0 - tolerance && z > 1.0 - tolerance) {
                    flag = true;
                  }
                } else if (shapeValue.equalsIgnoreCase("inner_left")
                    && number > 1.0 - tolerance
                    && z <= tolerance) {
                  flag = true;
                }
              }
            }
            break;
          case 105007365:
            if (facingLower.equals("north")) {
              if (z > 1.0 - tolerance
                  && shapeValue != null
                  && shapeValue.equalsIgnoreCase("straight")) {
                flag = true;
              }

              if (shapeValue != null) {
                if (shapeValue.equalsIgnoreCase("outer_left")) {
                  if (z > 1.0 - tolerance || number > 1.0 - tolerance) {
                    flag = true;
                  }
                } else if (shapeValue.equalsIgnoreCase("outer_right")) {
                  if (z > 1.0 - tolerance || number <= tolerance) {
                    flag = true;
                  }
                } else if (shapeValue.equalsIgnoreCase("inner_right")) {
                  if (z > 1.0 - tolerance && number <= tolerance) {
                    flag = true;
                  }
                } else if (shapeValue.equalsIgnoreCase("inner_left")
                    && z > 1.0 - tolerance
                    && number > 1.0 - tolerance) {
                  flag = true;
                }
              }
            }
            break;
          case 109627853:
            if (facingLower.equals("south")) {
              if (z < tolerance && shapeValue != null && shapeValue.equalsIgnoreCase("straight")) {
                flag = true;
              }

              if (shapeValue != null) {
                if (shapeValue.equalsIgnoreCase("outer_left")) {
                  if (z < tolerance || number < tolerance) {
                    flag = true;
                  }
                } else if (shapeValue.equalsIgnoreCase("outer_right")) {
                  if (z < tolerance || number > 1.0 - tolerance) {
                    flag = true;
                  }
                } else if (shapeValue.equalsIgnoreCase("inner_right")) {
                  if (z < tolerance && number > 1.0 - tolerance) {
                    flag = true;
                  }
                } else if (shapeValue.equalsIgnoreCase("inner_left")
                    && z < tolerance
                    && number < tolerance) {
                  flag = true;
                }
              }
            }
        }
      }

      if (flag && halfValue != null) {
        if (halfValue.equalsIgnoreCase("bottom")) {
          this.max = new Vector(this.max.getX(), 0.5, this.max.getZ());
        } else {
          this.min = new Vector(this.min.getX(), 0.5, this.min.getZ());
        }
      }

      return true;
    } else if (CMIMaterial.isFence(this.block.getType())
        || CMIMaterial.isGlassPane(this.block.getType())
        || CMIMaterial.IRON_BARS.equals(this.block.getType())
        || CMIMaterial.isWall(this.block.getType())) {
      double postWidth = 0.0625;
      if (CMIMaterial.isFence(this.block.getType())) {
        postWidth = 0.125;
      }

      if (CMIMaterial.isWall(this.block.getType())) {
        postWidth = 0.25;
      }

      tolerance = 0.5 - tolerance;
      if (number > 0.5 + postWidth + tolerance && z > 0.5 + postWidth + tolerance
          || number < 0.5 - postWidth - tolerance && z > 0.5 + postWidth + tolerance
          || number > 0.5 + postWidth + tolerance && z < 0.5 - postWidth - tolerance
          || number < 0.5 - postWidth - tolerance && z < 0.5 - postWidth - tolerance) {
        this.min = new Vector(0.5 - postWidth, 0.0, 0.5 - postWidth);
        this.max = new Vector(0.5 + postWidth, 1.0, 0.5 + postWidth);
      }

      return true;
    } else {
      if (!CMIMaterial.HOPPER.equals(this.block.getType())) {
        return false;
      }

      if (y > 0.25 || number > 0.35 && number < 0.65 && z > 0.35 && z < 0.65) {
        if (!(y > 0.6) && (!(number > 0.25) || !(number < 0.75) || !(z > 0.25) || !(z < 0.75))) {
          this.min = new Vector(1.0, 0.9, 1.0);
          this.max = new Vector(1, 1, 1);
        }
      } else {
        this.min = new Vector(1.0, 0.9, 1.0);
        this.max = new Vector(1, 1, 1);
      }

      return true;
    }
  }

  private boolean checkBlock(Block checkedBlock, boolean flag) {
    CMIMaterial cmiMaterial = null;
    if (this.getSnapshot() != null) {
      cmiMaterial =
          this.getBlockType(
              checkedBlock.getLocation().getBlockX() & 15,
              checkedBlock.getLocation().getBlockY(),
              checkedBlock.getLocation().getBlockZ() & 15);
    } else {
      cmiMaterial = CMIMaterial.get(checkedBlock);
    }

    if (!Version.isCurrentEqualOrHigher(Version.v1_13_R2)) {
      if (CMI.getInstance().getTeleportHandler().isEmpty(cmiMaterial, false)) {
        return true;
      } else {
        return flag && cmiMaterial.equals(CMIMaterial.SNOW)
            ? this.isEmptySpace(
                checkedBlock.getWorld(),
                this.getMinGlobal().clone().setY(this.getMinGlobal().getY() + 0.125),
                this.getMaxGlobal(),
                false)
            : false;
      }
    } else {
      return CMI.getInstance().getTeleportHandler().isEmpty(cmiMaterial, false);
    }
  }

  public boolean isPassable() {
    return this.getMin().getBlockX() >= this.getMax().getBlockX();
  }

  public boolean isEmptySpace(Location location) {
    Double sizeX = this.getMaxGlobal().getX() - this.getMinGlobal().getX();
    Double sizeY = this.getMaxGlobal().getY() - this.getMinGlobal().getY();
    Double sizeZ = this.getMaxGlobal().getZ() - this.getMinGlobal().getZ();
    Vector vector =
        new Vector(location.getX() - sizeX / 2.0, location.getY(), location.getZ() - sizeZ / 2.0);
    Vector maxCorner =
        new Vector(
            location.getX() + sizeX / 2.0, location.getY() + sizeY, location.getZ() + sizeZ / 2.0);
    return this.isEmptySpace(location.getWorld(), vector, maxCorner, true);
  }

  public boolean isEmptySpace() {
    return this.isEmptySpace(this.world, this.getMinGlobal(), this.getMaxGlobal(), true);
  }

  public boolean isEmptySpace(World worldIn, Vector vector, Vector maxCorner, boolean flag) {
    return this.getCollidingdBlock(worldIn, vector, maxCorner, flag) == null;
  }

  public Block getCollidingdBlock() {
    return this.getCollidingdBlock(this.world, this.getMinGlobal(), this.getMaxGlobal(), true);
  }

  public Block getCollidingdBlock(World worldIn, Vector vector, Vector maxCorner) {
    return this.getCollidingdBlock(worldIn, vector, maxCorner, true);
  }

  public Block getCollidingdBlock(World worldIn, Vector vector, Vector maxCorner, boolean flag) {
    Block hitBlock =
        CMI.getInstance().getNMS().getBlockLookingAt(worldIn, vector, maxCorner, false);
    if (hitBlock != null && !this.checkBlock(hitBlock, flag)) {
      return hitBlock;
    }

    Vector corner = vector.clone().setX(maxCorner.getX());
    Vector cornerSecond = maxCorner.clone().setX(vector.getX());
    hitBlock = CMI.getInstance().getNMS().getBlockLookingAt(worldIn, corner, cornerSecond, false);
    if (hitBlock != null && !this.checkBlock(hitBlock, flag)) {
      return hitBlock;
    }

    Vector cornerThird = vector.clone().setZ(maxCorner.getZ());
    Vector cornerFourth = maxCorner.clone().setZ(vector.getZ());
    hitBlock =
        CMI.getInstance().getNMS().getBlockLookingAt(worldIn, cornerThird, cornerFourth, false);
    if (hitBlock != null && !this.checkBlock(hitBlock, flag)) {
      return hitBlock;
    }

    Vector cornerFifth = vector.clone().setX(maxCorner.getX()).setZ(maxCorner.getZ());
    Vector cornerSixth = maxCorner.clone().setX(vector.getX()).setZ(vector.getZ());
    hitBlock =
        CMI.getInstance().getNMS().getBlockLookingAt(worldIn, cornerFifth, cornerSixth, false);
    if (hitBlock != null && !this.checkBlock(hitBlock, flag)) {
      return hitBlock;
    }

    hitBlock = CMI.getInstance().getNMS().getBlockLookingAt(worldIn, vector, cornerThird, false);
    if (hitBlock != null && !this.checkBlock(hitBlock, flag)) {
      return hitBlock;
    }

    hitBlock =
        CMI.getInstance().getNMS().getBlockLookingAt(worldIn, maxCorner, cornerFourth, false);
    if (hitBlock != null && !this.checkBlock(hitBlock, flag)) {
      return hitBlock;
    }

    Vector scanFrom = vector.clone();
    Vector scanTo = vector.clone().setX(maxCorner.getX());

    for (double number = vector.getZ(); number < maxCorner.getZ(); number += 0.1) {
      hitBlock =
          CMI.getInstance()
              .getNMS()
              .getBlockLookingAt(worldIn, scanFrom.setZ(number), scanTo.setZ(number), false);
      if (hitBlock != null && !this.checkBlock(hitBlock, flag)) {
        return hitBlock;
      }
    }

    return null;
  }

  public boolean collides(Entity entity) {
    if (entity == null) {
      return false;
    } else {
      CMIHitBox cmiHitBox = new CMIHitBox(entity);
      if (cmiHitBox.world == null || cmiHitBox.min == null || cmiHitBox.max == null) {
        return false;
      } else if (this.world != null && this.min != null && this.max != null) {
        CuboidArea cuboidArea = new CuboidArea(cmiHitBox.world, cmiHitBox.min, cmiHitBox.max);
        CuboidArea otherArea = new CuboidArea(this.world, this.min, this.max);
        return cuboidArea.checkCollision(otherArea);
      } else {
        return false;
      }
    }
  }

  public boolean canPlayerStand(Location location) {
    if (CMIMaterial.isAir(location.getBlock().getType())) {
      return true;
    }

    CMIMaterial cmiMaterial = null;
    if (this.getSnapshot() != null) {
      cmiMaterial =
          this.getBlockType(
              this.block.getLocation().getBlockX() & 15,
              this.block.getLocation().getBlockY(),
              this.block.getLocation().getBlockZ() & 15);
    } else {
      cmiMaterial = CMIMaterial.get(this.block);
    }

    if (!Version.isCurrentEqualOrHigher(Version.v1_13_R2)) {
      if (CMI.getInstance().getTeleportHandler().isEmpty(cmiMaterial, false)) {
        return true;
      }
    } else if (location.getBlock().isPassable()) {
      return true;
    }

    this.byExactXYZ(location.getX() % 1.0, location.getY() % 1.0, location.getZ() % 1.0);
    double number = location.getX() % 1.0;
    double zFrac = location.getZ() % 1.0;
    double yFrac = location.getY() % 1.0;
    if (number < 0.0) {
      number++;
    }

    if (zFrac < 0.0) {
      zFrac++;
    }

    cmiMaterial = null;
    if (this.getSnapshot() != null) {
      cmiMaterial =
          this.getBlockType(
              location.getBlockX() & 15, location.getBlockY(), location.getBlockZ() & 15);
    } else {
      cmiMaterial = CMIMaterial.get(location.getBlock());
    }

    if (cmiMaterial.equals(CMIMaterial.SNOW)) {
      yFrac += 0.125;
    }

    double minX = number - 0.3;
    double minZ = zFrac - 0.3;
    double minY = yFrac;
    double maxX = number + 0.3;
    double maxZ = zFrac + 0.3;
    double maxY = yFrac + 1.8;
    double hitMaxX = this.getMax().getX();
    double hitMinX = this.getMin().getX();
    double hitMaxY = this.getMax().getY();
    double hitMinY = this.getMin().getY();
    double hitMaxZ = this.getMax().getZ();
    double hitMinZ = this.getMin().getZ();
    return (!(maxX > hitMinX) || !(maxX < hitMaxX))
            && (!(minX > hitMinX) || !(minX < hitMaxX))
            && (!(hitMaxX > minX) || !(hitMaxX < maxX))
            && (!(hitMinX > minX) || !(hitMinX < maxX))
        || (!(maxY > hitMinY) || !(maxY < hitMaxY))
            && (!(minY > hitMinY) || !(minY < hitMaxY))
            && (!(hitMaxY > minY) || !(hitMaxY < maxY))
            && (!(hitMinY > minY) || !(hitMinY < maxY))
        || (!(maxZ > hitMinZ) || !(maxZ < hitMaxZ))
            && (!(minZ > hitMinZ) || !(minZ < hitMaxZ))
            && (!(hitMaxZ > minZ) || !(hitMaxZ < maxZ))
            && (!(hitMinZ > minZ) || !(hitMinZ < maxZ));
  }

  private CMIMaterial getBlockType(int value, int blockY, int blockZ) {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      return CMIMaterial.get(this.getSnapshot().getBlockType(value, blockY, blockZ));
    }

    if (getBlockTypeId == null) {
      try {
        getBlockTypeId =
            this.getSnapshot()
                .getClass()
                .getMethod("getBlockTypeId", int.class, int.class, int.class);
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }

    try {
      return CMIMaterial.get(
          (Integer) getBlockTypeId.invoke(this.getSnapshot(), value, blockY, blockZ));
    } catch (Throwable throwable2) {
      throwable2.printStackTrace();
      return CMIMaterial.AIR;
    }
  }

  public boolean collides(Vector vector) {
    return !(vector.getX() < this.getMinGlobal().getX())
        && !(vector.getX() > this.getMaxGlobal().getX())
        && !(vector.getY() < this.getMinGlobal().getY())
        && !(vector.getY() > this.getMaxGlobal().getY())
        && !(vector.getZ() < this.getMinGlobal().getZ())
        && !(vector.getZ() > this.getMaxGlobal().getZ());
  }

  public Block getBlock() {
    return this.block;
  }

  public void setMax(Vector vector) {
    this.max = vector;
  }

  public void setMin(Vector vector) {
    this.min = vector;
  }

  public Entity getEntity() {
    return this.ent;
  }

  public void setEntity(Entity entity) {
    this.ent = entity;
  }

  public World getWorld() {
    return this.world;
  }

  public ChunkSnapshot getSnapshot() {
    return this.snapshot;
  }

  public void setSnapshot(ChunkSnapshot chunkSnapshot) {
    this.snapshot = chunkSnapshot;
  }
}
