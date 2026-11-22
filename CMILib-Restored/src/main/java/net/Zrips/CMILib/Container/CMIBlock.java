package net.Zrips.CMILib.Container;

import java.util.HashMap;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Slab.Type;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Stairs.Shape;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Banner;
import org.bukkit.material.Comparator;
import org.bukkit.material.Diode;
import org.bukkit.material.Dispenser;
import org.bukkit.material.Door;
import org.bukkit.material.Gate;
import org.bukkit.material.Hopper;
import org.bukkit.material.Lever;
import org.bukkit.material.Observer;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.PistonExtensionMaterial;
import org.bukkit.material.Sign;
import org.bukkit.material.Skull;
import org.bukkit.material.TripwireHook;

public class CMIBlock {
  private Block block;
  private Integer data = null;
  private Object blockd = null;

  public CMIBlock(Block block) {
    this.block = block;
  }

  @Deprecated
  public CMIBlock.blockDirection getDirection() {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      return CMIBlock.blockDirection.none;
    } else {
      return CMIBlock.blockDirection.getByDir(this.block.getData()) == null
          ? CMIBlock.blockDirection.none
          : CMIBlock.blockDirection.getByDir(this.block.getData());
    }
  }

  public boolean isWaterlogged() {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)
        && this.block.getBlockData() instanceof Waterlogged) {
      Waterlogged wl = (Waterlogged) this.block.getBlockData();
      return wl.isWaterlogged();
    }

    CMIMaterial mat = CMIMaterial.get(this.block);
    switch (mat) {
      case WATER:
      case LEGACY_STATIONARY_WATER:
      case BUBBLE_COLUMN:
      case KELP_PLANT:
      case KELP:
      case SEAGRASS:
      case TALL_SEAGRASS:
        return true;
      default:
        return false;
    }
  }

  public CMIBlock.Bisect getBisect() {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      BlockData blockData = this.block.getBlockData();
      if (!(blockData instanceof Bisected)) {
        return null;
      }

      Bisected half = (Bisected) blockData;
      switch (half.getHalf().toString()) {
        case "TOP":
          return CMIBlock.Bisect.TOP;
        case "BOTTOM":
          return CMIBlock.Bisect.BOTTOM;
      }
    }

    return null;
  }

  public boolean isAttached() {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      BlockData blockData = this.block.getBlockData();
      if (!(blockData instanceof Attachable)) {
        return false;
      }

      Attachable half = (Attachable) blockData;
      return half.isAttached();
    } else {
      return false;
    }
  }

  public Axis getAxis() {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      BlockData blockData = this.block.getBlockData();
      if (!(blockData instanceof Orientable)) {
        return null;
      }

      Orientable half = (Orientable) blockData;
      return half.getAxis();
    } else {
      return null;
    }
  }

  public CMIBlock.BedPart getBedPart() {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      BlockData blockData = this.block.getBlockData();
      if (!(blockData instanceof Bed)) {
        return null;
      }

      Bed half = (Bed) blockData;
      switch (half.getPart().toString()) {
        case "FOOT":
          return CMIBlock.BedPart.FOOT;
        case "HEAD":
          return CMIBlock.BedPart.HEAD;
      }
    }

    return null;
  }

  public CMIBlock.StairShape getStairShape() {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      CMIMaterial cm = CMIMaterial.get(this.block);
      if (cm.isStairs()) {
        BlockData blockData = this.block.getBlockData();
        Stairs stair = (Stairs) blockData;
        Shape shape = stair.getShape();
        return CMIBlock.StairShape.getByName(shape.toString());
      }
    }

    return null;
  }

  public BlockFace getFacing() {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      BlockData blockData = this.block.getBlockData();
      if (blockData instanceof Directional) {
        Directional directional =
            this.blockd == null ? (Directional) blockData.clone() : (Directional) this.blockd;
        return directional.getFacing();
      }

      if (blockData instanceof Rotatable) {
        Rotatable directional =
            this.blockd == null ? (Rotatable) blockData.clone() : (Rotatable) this.blockd;
        return directional.getRotation();
      }
    }

    try {
      CMIMaterial cm = CMIMaterial.get(this.block);
      switch (cm) {
        case COMPARATOR:
          Comparator comparator = (Comparator) this.block.getState().getData();
          return comparator.getFacing();
        case REPEATER:
          Diode diode = (Diode) this.block.getState().getData();
          return diode.getFacing();
        case DISPENSER:
          Dispenser Dispenser = (Dispenser) this.block.getState().getData();
          return Dispenser.getFacing();
        case HOPPER:
          if (Version.isCurrentEqualOrHigher(Version.v1_9_R1)) {
            Hopper Hopper = (Hopper) this.block.getState().getData();
            return Hopper.getFacing();
          }

          return BlockFace.DOWN;
        case LEVER:
          Lever Lever = (Lever) this.block.getState().getData();
          return Lever.getFacing();
        case TRIPWIRE_HOOK:
          TripwireHook TripwireHook = (TripwireHook) this.block.getState().getData();
          return TripwireHook.getFacing();
        default:
          if (cm.isSign()) {
            Sign sign = (Sign) this.block.getState().getData();
            return sign.getFacing();
          }

          if (cm.isSkull()) {
            Skull Skull = (Skull) this.block.getState().getData();
            return Skull.getFacing();
          }

          if (cm.isBed()) {
            org.bukkit.material.Bed mat = (org.bukkit.material.Bed) this.block.getState().getData();
            return mat.getFacing();
          }

          if (cm.isDoor()) {
            Door mat = (Door) this.block.getState().getData();
            return mat.getFacing();
          }

          if (cm.isGate()) {
            Gate mat = (Gate) this.block.getState().getData();
            return mat.getFacing();
          }

          String stringname = this.block.getType().name();
          if (stringname.contains("_STAIRS")) {
            org.bukkit.material.Stairs mat =
                (org.bukkit.material.Stairs) this.block.getState().getData();
            return mat.getFacing();
          }

          if (stringname.contains("BANNER")) {
            Banner banner = (Banner) this.block.getState().getData();
            return banner.getFacing();
          }

          if (stringname.contains("OBSERVER")) {
            Observer observer = (Observer) this.block.getState().getData();
            return observer.getFacing();
          }

          if (stringname.contains("PISTON_BASE")) {
            PistonBaseMaterial piston = (PistonBaseMaterial) this.block.getState().getData();
            return piston.getFacing();
          }

          if (stringname.contains("PISTON_EXTENSION")) {
            PistonExtensionMaterial pistonExtension =
                (PistonExtensionMaterial) this.block.getState().getData();
            return pistonExtension.getFacing();
          }
      }
    } catch (Exception var7) {
    }

    return null;
  }

  public CMIBlock flip(CMIBlock.FlipDirection direction, boolean angle) {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      CMIMaterial cmat = CMIMaterial.get(this.block);
      BlockData blockData = this.block.getBlockData().clone();
      switch (direction) {
        case UP_DOWN:
          if (blockData instanceof Slab) {
            Slab slab =
                this.blockd != null && this.blockd instanceof Slab
                    ? (Slab) this.blockd
                    : (Slab) blockData.clone();
            switch (slab.getType().toString()) {
              case "TOP":
                slab.setType(Type.BOTTOM);
                break;
              case "BOTTOM":
                slab.setType(Type.TOP);
            }

            if (this.blockd == null) {
              this.blockd = slab;
            }
          } else if (blockData instanceof Bisected) {
            Bisected half =
                this.blockd != null && this.blockd instanceof Bisected
                    ? (Bisected) this.blockd
                    : (Bisected) blockData.clone();
            if (half != null) {
              switch (half.getHalf().toString()) {
                case "TOP":
                  half.setHalf(Half.BOTTOM);
                  break;
                case "BOTTOM":
                  half.setHalf(Half.TOP);
              }

              if (this.blockd == null) {
                this.blockd = half;
              }
            }
          }
        default:
          if (blockData instanceof Directional) {
            Directional directional =
                this.blockd == null ? (Directional) blockData.clone() : (Directional) this.blockd;
            if (directional != null) {
              BlockFace face = directional.getFacing();
              label443:
              switch (direction) {
                case NORTH_SOUTH:
                  if (angle) {
                    switch (face.toString()) {
                      case "NORTH":
                        directional.setFacing(BlockFace.EAST);
                        break label443;
                      case "SOUTH":
                        directional.setFacing(BlockFace.WEST);
                        break label443;
                      case "WEST":
                        directional.setFacing(BlockFace.SOUTH);
                        break label443;
                      case "EAST":
                        directional.setFacing(BlockFace.NORTH);
                    }
                  } else {
                    switch (face.toString()) {
                      case "NORTH":
                        directional.setFacing(BlockFace.SOUTH);
                        break label443;
                      case "SOUTH":
                        directional.setFacing(BlockFace.NORTH);
                    }
                  }
                  break;
                case WEST_EAST:
                  if (angle) {
                    switch (face.toString()) {
                      case "WEST":
                        directional.setFacing(BlockFace.NORTH);
                        break label443;
                      case "EAST":
                        directional.setFacing(BlockFace.SOUTH);
                        break label443;
                      case "NORTH":
                        directional.setFacing(BlockFace.WEST);
                        break label443;
                      case "SOUTH":
                        directional.setFacing(BlockFace.EAST);
                    }
                  } else {
                    switch (face.toString()) {
                      case "WEST":
                        directional.setFacing(BlockFace.EAST);
                        break label443;
                      case "EAST":
                        directional.setFacing(BlockFace.WEST);
                    }
                  }
                  break;
                case UP_DOWN:
                  switch (face.toString()) {
                    case "UP":
                      directional.setFacing(BlockFace.DOWN);
                      break label443;
                    case "DOWN":
                      directional.setFacing(BlockFace.UP);
                    default:
                      break label443;
                  }
                case ROTATE_90_CW:
                  switch (face.toString()) {
                    case "NORTH":
                      directional.setFacing(BlockFace.EAST);
                      break label443;
                    case "EAST":
                      directional.setFacing(BlockFace.SOUTH);
                      break label443;
                    case "SOUTH":
                      directional.setFacing(BlockFace.WEST);
                      break label443;
                    case "WEST":
                      directional.setFacing(BlockFace.NORTH);
                    default:
                      break label443;
                  }
                case ROTATE_90_CCW:
                  switch (face.toString()) {
                    case "NORTH":
                      directional.setFacing(BlockFace.WEST);
                      break label443;
                    case "EAST":
                      directional.setFacing(BlockFace.NORTH);
                      break label443;
                    case "SOUTH":
                      directional.setFacing(BlockFace.EAST);
                      break label443;
                    case "WEST":
                      directional.setFacing(BlockFace.SOUTH);
                    default:
                      break label443;
                  }
                case ROTATE_180:
                  switch (face.toString()) {
                    case "NORTH":
                      directional.setFacing(BlockFace.SOUTH);
                      break;
                    case "EAST":
                      directional.setFacing(BlockFace.WEST);
                      break;
                    case "SOUTH":
                      directional.setFacing(BlockFace.NORTH);
                      break;
                    case "WEST":
                      directional.setFacing(BlockFace.EAST);
                  }
              }

              if (this.blockd == null) {
                this.blockd = directional;
              }
            }
          }

          if (cmat.isStairs()) {
            Stairs stair = (Stairs) this.blockd;
            Shape shape = stair.getShape();
            BlockFace face = stair.getFacing();
            switch (direction) {
              case NORTH_SOUTH:
              case WEST_EAST:
                switch (shape.toString()) {
                  case "INNER_LEFT":
                    stair.setShape(Shape.INNER_RIGHT);
                    break;
                  case "INNER_RIGHT":
                    stair.setShape(Shape.INNER_LEFT);
                    break;
                  case "OUTER_LEFT":
                    stair.setShape(Shape.OUTER_RIGHT);
                    break;
                  case "OUTER_RIGHT":
                    stair.setShape(Shape.OUTER_LEFT);
                }
            }
          }

          if (blockData instanceof Rotatable) {
            Rotatable directional;
            directional =
                this.blockd == null ? (Rotatable) blockData.clone() : (Rotatable) this.blockd;
            label354:
            switch (direction) {
              case NORTH_SOUTH:
                switch (directional.getRotation()) {
                  case NORTH:
                    directional.setRotation(BlockFace.SOUTH);
                    break label354;
                  case NORTH_NORTH_EAST:
                    directional.setRotation(BlockFace.SOUTH_SOUTH_EAST);
                    break label354;
                  case NORTH_EAST:
                    directional.setRotation(BlockFace.SOUTH_EAST);
                    break label354;
                  case EAST_NORTH_EAST:
                    directional.setRotation(BlockFace.EAST_SOUTH_EAST);
                    break label354;
                  case EAST:
                    directional.setRotation(BlockFace.EAST);
                    break label354;
                  case EAST_SOUTH_EAST:
                    directional.setRotation(BlockFace.EAST_NORTH_EAST);
                    break label354;
                  case SOUTH_EAST:
                    directional.setRotation(BlockFace.NORTH_EAST);
                    break label354;
                  case SOUTH_SOUTH_EAST:
                    directional.setRotation(BlockFace.NORTH_NORTH_EAST);
                    break label354;
                  case SOUTH:
                    directional.setRotation(BlockFace.NORTH);
                    break label354;
                  case SOUTH_SOUTH_WEST:
                    directional.setRotation(BlockFace.NORTH_NORTH_WEST);
                    break label354;
                  case SOUTH_WEST:
                    directional.setRotation(BlockFace.NORTH_WEST);
                    break label354;
                  case WEST_SOUTH_WEST:
                    directional.setRotation(BlockFace.WEST_NORTH_WEST);
                    break label354;
                  case WEST:
                    directional.setRotation(BlockFace.WEST);
                    break label354;
                  case WEST_NORTH_WEST:
                    directional.setRotation(BlockFace.WEST_SOUTH_WEST);
                    break label354;
                  case NORTH_WEST:
                    directional.setRotation(BlockFace.SOUTH_WEST);
                    break label354;
                  case NORTH_NORTH_WEST:
                    directional.setRotation(BlockFace.SOUTH_SOUTH_WEST);
                  default:
                    break label354;
                }
              case WEST_EAST:
                switch (directional.getRotation()) {
                  case NORTH:
                    directional.setRotation(BlockFace.NORTH);
                    break;
                  case NORTH_NORTH_EAST:
                    directional.setRotation(BlockFace.NORTH_NORTH_WEST);
                    break;
                  case NORTH_EAST:
                    directional.setRotation(BlockFace.NORTH_WEST);
                    break;
                  case EAST_NORTH_EAST:
                    directional.setRotation(BlockFace.WEST_NORTH_WEST);
                    break;
                  case EAST:
                    directional.setRotation(BlockFace.WEST);
                    break;
                  case EAST_SOUTH_EAST:
                    directional.setRotation(BlockFace.WEST_SOUTH_WEST);
                    break;
                  case SOUTH_EAST:
                    directional.setRotation(BlockFace.SOUTH_WEST);
                    break;
                  case SOUTH_SOUTH_EAST:
                    directional.setRotation(BlockFace.SOUTH_SOUTH_WEST);
                    break;
                  case SOUTH:
                    directional.setRotation(BlockFace.SOUTH);
                    break;
                  case SOUTH_SOUTH_WEST:
                    directional.setRotation(BlockFace.SOUTH_SOUTH_EAST);
                    break;
                  case SOUTH_WEST:
                    directional.setRotation(BlockFace.SOUTH_EAST);
                    break;
                  case WEST_SOUTH_WEST:
                    directional.setRotation(BlockFace.EAST_SOUTH_EAST);
                    break;
                  case WEST:
                    directional.setRotation(BlockFace.EAST);
                    break;
                  case WEST_NORTH_WEST:
                    directional.setRotation(BlockFace.EAST_NORTH_EAST);
                    break;
                  case NORTH_WEST:
                    directional.setRotation(BlockFace.NORTH_EAST);
                    break;
                  case NORTH_NORTH_WEST:
                    directional.setRotation(BlockFace.NORTH_NORTH_EAST);
                }
            }

            if (this.blockd == null) {
              this.blockd = directional;
            }
          }

          if (!cmat.isWall()
              && !cmat.isGlassPane()
              && !cmat.isFence()
              && !cmat.equals(CMIMaterial.IRON_BARS)
              && this.blockd == null) {
            this.blockd = blockData.clone();
          }

          return this;
      }
    } else {
      int flipX = 0;
      int flipY = 0;
      int flipZ = 0;
      Material type = this.block.getType();
      this.data = this.data == null ? this.block.getData() : this.data;
      CMIMaterial cmat = CMIMaterial.get(this.block);

      try {
        switch (direction) {
          case NORTH_SOUTH:
            flipZ = 1;
            break;
          case WEST_EAST:
            flipX = 1;
            break;
          case UP_DOWN:
            flipY = 1;
        }

        if (cmat.isButton()) {
          switch (this.data & -9) {
            case 0:
            case 5:
              this.data = this.data ^ flipY * 5;
              break;
            case 1:
              this.data = this.data + flipX;
              break;
            case 2:
              this.data = this.data - flipX;
              break;
            case 3:
              this.data = this.data + flipZ;
              break;
            case 4:
              this.data = this.data - flipZ;
          }
        } else if (cmat.isSlab()) {
          this.data = this.data ^ flipY << 3;
        } else if (cmat.isDoor()) {
          if ((this.data & 8) == 0) {
            switch (this.data & 3) {
              case 0:
                this.data = this.data + flipX + flipZ * 3;
                break;
              case 1:
                this.data = this.data - flipX + flipZ;
                break;
              case 2:
                this.data = this.data + flipX - flipZ;
                break;
              case 3:
                this.data = this.data - flipX - flipZ * 3;
            }
          }
        } else if (cmat.isTrapDoor()) {
          switch (this.data & 3) {
            case 0:
            case 1:
              this.data = this.data ^ flipZ;
              break;
            case 2:
            case 3:
              this.data = this.data ^ flipX;
          }
        } else if (cmat.isBed()) {
          switch (this.data & 3) {
            case 0:
            case 2:
              this.data = this.data ^ flipZ << 1;
              break;
            case 1:
            case 3:
              this.data = this.data ^ flipX << 1;
          }
        } else if (cmat.isSign()) {
          switch (direction) {
            case NORTH_SOUTH:
              this.data = 16 - this.data & 15;
              break;
            case WEST_EAST:
              this.data = 8 - this.data & 15;
          }
        }

        label550:
        switch (CMIMaterial.get(this.block)) {
          case DISPENSER:
          case DROPPER:
            int dispPower = this.data & 8;
            switch (this.data & -9) {
              case 0:
              case 1:
                this.data = this.data ^ flipY | dispPower;
                break label550;
              case 2:
              case 3:
                this.data = this.data ^ flipZ | dispPower;
                break label550;
              case 4:
              case 5:
                this.data = this.data ^ flipX | dispPower;
              default:
                break label550;
            }
          case HOPPER:
          case LADDER:
          case WALL_SIGN:
          case CHEST:
          case FURNACE:
          case LEGACY_BURNING_FURNACE:
          case ENDER_CHEST:
          case TRAPPED_CHEST:
            int extra = this.data & 8;
            int withoutFlags = this.data & -9;
            switch (withoutFlags) {
              case 2:
              case 3:
                this.data = this.data ^ flipZ | extra;
                break label550;
              case 4:
              case 5:
                this.data = this.data ^ flipX | extra;
              default:
                break label550;
            }
          case LEVER:
            switch (this.data & -9) {
              case 0:
              case 6:
                this.data = this.data ^ flipY * 6;
                break label550;
              case 1:
                this.data = this.data + flipX;
                break label550;
              case 2:
                this.data = this.data - flipX;
                break label550;
              case 3:
                this.data = this.data + flipZ;
                break label550;
              case 4:
                this.data = this.data - flipZ;
                break label550;
              case 5:
              case 7:
                this.data = this.data ^ flipY << 1;
              default:
                break label550;
            }
          case TORCH:
          case REDSTONE_TORCH:
            if (this.data >= 1 && this.data <= 4) {
              switch (this.data) {
                case 1:
                  this.data = this.data + flipX;
                  break;
                case 2:
                  this.data = this.data - flipX;
                  break;
                case 3:
                  this.data = this.data + flipZ;
                  break;
                case 4:
                  this.data = this.data - flipZ;
              }
            }
            break;
          case RAIL:
            switch (this.data) {
              case 6:
                this.data = this.data + flipX + flipZ * 3;
                break;
              case 7:
                this.data = this.data - flipX + flipZ;
                break;
              case 8:
                this.data = this.data + flipX - flipZ;
                break;
              case 9:
                this.data = this.data - flipX - flipZ * 3;
            }
          case POWERED_RAIL:
          case DETECTOR_RAIL:
          case ACTIVATOR_RAIL:
            switch (this.data & 7) {
              case 0:
              case 1:
                this.data = this.data;
                break label550;
              case 2:
              case 3:
                this.data = this.data ^ flipX;
                break label550;
              case 4:
              case 5:
                this.data = this.data ^ flipZ;
              default:
                break label550;
            }
          case PUMPKIN:
          case JACK_O_LANTERN:
            if (this.data > 3) {
              break;
            }
          case COMPARATOR:
          case REPEATER:
          case TRIPWIRE_HOOK:
          case LEGACY_DIODE_BLOCK_OFF:
          case LEGACY_DIODE_BLOCK_ON:
          case LEGACY_REDSTONE_COMPARATOR_OFF:
          case LEGACY_REDSTONE_COMPARATOR_ON:
          case COCOA:
            switch (this.data & 3) {
              case 0:
              case 2:
                this.data = this.data ^ flipZ << 1;
                break label550;
              case 1:
              case 3:
                this.data = this.data ^ flipX << 1;
              default:
                break label550;
            }
          case PISTON_HEAD:
          case STICKY_PISTON:
          case MOVING_PISTON:
            switch (this.data & -9) {
              case 0:
              case 1:
                this.data = this.data ^ flipY;
                break label550;
              case 2:
              case 3:
                this.data = this.data ^ flipZ;
                break label550;
              case 4:
              case 5:
                this.data = this.data ^ flipX;
              default:
                break label550;
            }
          case RED_MUSHROOM:
          case BROWN_MUSHROOM:
            switch (this.data) {
              case 1:
              case 4:
              case 7:
                this.data = this.data + flipX * 2;
              case 2:
              case 5:
              case 8:
              default:
                break;
              case 3:
              case 6:
              case 9:
                this.data = this.data - flipX * 2;
            }

            switch (this.data) {
              case 1:
              case 2:
              case 3:
                this.data = this.data + flipZ * 6;
              case 4:
              case 5:
              case 6:
              default:
                break label550;
              case 7:
              case 8:
              case 9:
                this.data = this.data - flipZ * 6;
                break label550;
            }
          case VINE:
            int vineBitA = 0;
            int vineBitB = 0;
            switch (direction) {
              case NORTH_SOUTH:
                vineBitA = 2;
                vineBitB = 8;
                break;
              case WEST_EAST:
                vineBitA = 1;
                vineBitB = 4;
                break;
              default:
                this.data = this.data;
            }

            int newData = this.data & ~(vineBitA | vineBitB);
            if ((this.data & vineBitA) != 0) {
              newData |= vineBitB;
            }

            if ((this.data & vineBitB) != 0) {
              newData |= vineBitA;
            }

            this.data = newData;
            break;
          case LEGACY_SKULL:
            switch (this.data) {
              case 2:
              case 3:
                this.data = this.data ^ flipZ;
                break label550;
              case 4:
              case 5:
                this.data = this.data ^ flipX;
              default:
                break label550;
            }
          case ANVIL:
            switch (this.data & 3) {
              case 0:
              case 2:
                this.data = this.data ^ flipZ << 1;
                break;
              case 1:
              case 3:
                this.data = this.data ^ flipX << 1;
            }
        }

        if (cmat.isFence()) {
          switch (this.data & 3) {
            case 0:
            case 2:
              this.data = this.data ^ flipZ << 1;
              break;
            case 1:
            case 3:
              this.data = this.data ^ flipX << 1;
          }
        } else if (cmat.isStairs()) {
          this.data = this.data ^ flipY << 2;
          switch (this.data) {
            case 0:
            case 1:
            case 4:
            case 5:
              this.data = this.data ^ flipX;
              break;
            case 2:
            case 3:
            case 6:
            case 7:
              this.data = this.data ^ flipZ;
          }
        }

        if (type.name().contains("TERRACOTTA")) {
          switch (this.data & 3) {
            case 0:
            case 2:
              this.data = this.data ^ flipZ << 1;
              break;
            case 1:
            case 3:
              this.data = this.data ^ flipX << 1;
          }
        }
      } catch (Exception e) {
        this.data = this.data;
      }

      return this;
    }
  }

  public CMIBlock rotate90Reverse() {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      CMIMaterial cmat = CMIMaterial.get(this.block);
      BlockData blockData = this.block.getBlockData();
      if (blockData instanceof Directional) {
        Directional directional =
            this.blockd == null ? (Directional) blockData.clone() : (Directional) this.blockd;
        BlockFace face = directional.getFacing();
        switch (directional.getFacing()) {
          case NORTH:
            directional.setFacing(BlockFace.WEST);
            break;
          case EAST:
            directional.setFacing(BlockFace.NORTH);
            break;
          case SOUTH:
            directional.setFacing(BlockFace.EAST);
            break;
          case WEST:
            directional.setFacing(BlockFace.SOUTH);
        }

        if (this.blockd == null) {
          this.blockd = directional.clone();
        }
      }

      if (blockData instanceof Rotatable) {
        Rotatable directional =
            this.blockd == null ? (Rotatable) blockData.clone() : (Rotatable) this.blockd;
        BlockFace face = directional.getRotation();
        switch (face) {
          case NORTH:
            directional.setRotation(BlockFace.WEST);
            break;
          case NORTH_NORTH_EAST:
            directional.setRotation(BlockFace.WEST_NORTH_WEST);
            break;
          case NORTH_EAST:
            directional.setRotation(BlockFace.NORTH_WEST);
            break;
          case EAST_NORTH_EAST:
            directional.setRotation(BlockFace.NORTH_NORTH_WEST);
            break;
          case EAST:
            directional.setRotation(BlockFace.NORTH);
            break;
          case EAST_SOUTH_EAST:
            directional.setRotation(BlockFace.NORTH_NORTH_EAST);
            break;
          case SOUTH_EAST:
            directional.setRotation(BlockFace.NORTH_EAST);
            break;
          case SOUTH_SOUTH_EAST:
            directional.setRotation(BlockFace.EAST_NORTH_EAST);
            break;
          case SOUTH:
            directional.setRotation(BlockFace.EAST);
            break;
          case SOUTH_SOUTH_WEST:
            directional.setRotation(BlockFace.EAST_SOUTH_EAST);
            break;
          case SOUTH_WEST:
            directional.setRotation(BlockFace.SOUTH_EAST);
            break;
          case WEST_SOUTH_WEST:
            directional.setRotation(BlockFace.SOUTH_SOUTH_EAST);
            break;
          case WEST:
            directional.setRotation(BlockFace.SOUTH);
            break;
          case WEST_NORTH_WEST:
            directional.setRotation(BlockFace.SOUTH_SOUTH_WEST);
            break;
          case NORTH_WEST:
            directional.setRotation(BlockFace.SOUTH_WEST);
            break;
          case NORTH_NORTH_WEST:
            directional.setRotation(BlockFace.WEST_SOUTH_WEST);
        }

        if (this.blockd == null) {
          this.blockd = directional;
        }

        return this;
      } else {
        if (this.blockd == null) {
          this.blockd = blockData.clone();
        }

        return this;
      }
    } else {
      Material type = this.block.getType();
      this.data = this.data == null ? this.block.getData() : this.data;
      CMIMaterial cmat = CMIMaterial.get(this.block);

      try {
        label233:
        switch (cmat) {
          case DISPENSER:
          case DROPPER:
            int dispPower = this.data & 8;
            switch (this.data & -9) {
              case 2:
                this.data = 5 | dispPower;
                break label233;
              case 3:
                this.data = 4 | dispPower;
                break label233;
              case 4:
                this.data = 2 | dispPower;
                break label233;
              case 5:
                this.data = 3 | dispPower;
              default:
                break label233;
            }
          case HOPPER:
          case LADDER:
          case WALL_SIGN:
          case CHEST:
          case FURNACE:
          case LEGACY_BURNING_FURNACE:
          case ENDER_CHEST:
          case TRAPPED_CHEST:
            int extra = this.data & 8;
            int withoutFlags = this.data & -9;
            switch (withoutFlags) {
              case 2:
                this.data = 5 | extra;
                break label233;
              case 3:
                this.data = 4 | extra;
                break label233;
              case 4:
                this.data = 2 | extra;
                break label233;
              case 5:
                this.data = 3 | extra;
              default:
                break label233;
            }
          case LEVER:
            int thrown = this.data & 8;
            switch (this.data & -9) {
              case 0:
                this.data = 7 | thrown;
                break label233;
              case 1:
                this.data = 3 | thrown;
                break label233;
              case 2:
                this.data = 4 | thrown;
                break label233;
              case 3:
                this.data = 2 | thrown;
                break label233;
              case 4:
                this.data = 1 | thrown;
                break label233;
              case 5:
                this.data = 6 | thrown;
                break label233;
              case 6:
                this.data = 5 | thrown;
                break label233;
              case 7:
                this.data = 0 | thrown;
              default:
                break label233;
            }
          case TORCH:
          case LEGACY_REDSTONE_TORCH_OFF:
          case LEGACY_REDSTONE_TORCH_ON:
            switch (this.data) {
              case 1:
                this.data = 3;
                break;
              case 2:
                this.data = 4;
                break;
              case 3:
                this.data = 2;
                break;
              case 4:
                this.data = 1;
            }
          case RAIL:
            switch (this.data) {
              case 6:
                this.data = 7;
                break;
              case 7:
                this.data = 8;
                break;
              case 8:
                this.data = 9;
                break;
              case 9:
                this.data = 6;
            }
          case POWERED_RAIL:
          case DETECTOR_RAIL:
          case ACTIVATOR_RAIL:
            switch (this.data & 7) {
              case 0:
                this.data = 1 | this.data & -8;
                break;
              case 1:
                this.data = 0 | this.data & -8;
                break;
              case 2:
                this.data = 5 | this.data & -8;
                break;
              case 3:
                this.data = 4 | this.data & -8;
                break;
              case 4:
                this.data = 2 | this.data & -8;
                break;
              case 5:
                this.data = 3 | this.data & -8;
            }
          case REDSTONE_TORCH:
          default:
            break;
          case PUMPKIN:
          case JACK_O_LANTERN:
            switch (this.data) {
              case 0:
                this.data = 1;
                break label233;
              case 1:
                this.data = 2;
                break label233;
              case 2:
                this.data = 3;
                break label233;
              case 3:
                this.data = 0;
              default:
                break label233;
            }
          case LEGACY_DIODE_BLOCK_OFF:
          case LEGACY_DIODE_BLOCK_ON:
          case LEGACY_REDSTONE_COMPARATOR_OFF:
          case LEGACY_REDSTONE_COMPARATOR_ON:
            int dir = this.data & 3;
            int delay = this.data - dir;
            switch (dir) {
              case 0:
                this.data = 1 | delay;
                break label233;
              case 1:
                this.data = 2 | delay;
                break label233;
              case 2:
                this.data = 3 | delay;
                break label233;
              case 3:
                this.data = 0 | delay;
              default:
                break label233;
            }
          case PISTON_HEAD:
          case STICKY_PISTON:
          case MOVING_PISTON:
            int rest = this.data & -8;
            switch (this.data & 7) {
              case 2:
                this.data = 5 | rest;
                break label233;
              case 3:
                this.data = 4 | rest;
                break label233;
              case 4:
                this.data = 2 | rest;
                break label233;
              case 5:
                this.data = 3 | rest;
              default:
                break label233;
            }
          case RED_MUSHROOM:
          case BROWN_MUSHROOM:
            if (this.data >= 10) {
              this.data = this.data;
            } else {
              this.data = this.data * 3 % 10;
            }
            break;
          case VINE:
            this.data = (this.data << 1 | this.data >> 3) & 15;
            break;
          case LEGACY_SKULL:
            switch (this.data) {
              case 2:
                this.data = 5;
                break label233;
              case 3:
                this.data = 4;
                break label233;
              case 4:
                this.data = 2;
                break label233;
              case 5:
                this.data = 3;
              default:
                break label233;
            }
          case ANVIL:
            int damage = this.data & -4;
            switch (this.data & 3) {
              case 0:
                this.data = 3 | damage;
                break label233;
              case 1:
                this.data = 0 | damage;
                break label233;
              case 2:
                this.data = 1 | damage;
                break label233;
              case 3:
                this.data = 2 | damage;
              default:
                break label233;
            }
          case STONE_BUTTON:
          case OAK_BUTTON:
            int poweredBit = this.data & 8;
            switch (this.data & -9) {
              case 1:
                this.data = 3 | poweredBit;
                break label233;
              case 2:
                this.data = 4 | poweredBit;
                break label233;
              case 3:
                this.data = 2 | poweredBit;
                break label233;
              case 4:
                this.data = 1 | poweredBit;
              default:
                break label233;
            }
          case OAK_DOOR:
          case IRON_DOOR:
            if ((this.data & 8) != 0) {
              break;
            }
          case TRIPWIRE_HOOK:
          case COCOA:
            int otherBits = this.data & -4;
            int facingBits = this.data & 3;
            switch (facingBits) {
              case 0:
                this.data = 1 | otherBits;
                break label233;
              case 1:
                this.data = 2 | otherBits;
                break label233;
              case 2:
                this.data = 3 | otherBits;
                break label233;
              case 3:
                this.data = 0 | otherBits;
              default:
                break label233;
            }
          case LEGACY_SIGN_POST:
            this.data = (this.data + 4) % 16;
            break;
          case HAY_BLOCK:
          case OAK_LOG:
          case BIRCH_LOG:
          case SPRUCE_LOG:
          case JUNGLE_LOG:
            if (this.data >= 4 && this.data <= 11) {
              this.data = this.data ^ 12;
            }
            break;
          case OAK_TRAPDOOR:
          case IRON_TRAPDOOR:
            int withoutOrientation = this.data & -4;
            int orientation = this.data & 3;
            switch (orientation) {
              case 0:
                this.data = 3 | withoutOrientation;
                break;
              case 1:
                this.data = 2 | withoutOrientation;
                break;
              case 2:
                this.data = 0 | withoutOrientation;
                break;
              case 3:
                this.data = 1 | withoutOrientation;
            }
        }

        if (cmat.isBed()) {
          this.data = this.data & -4 | this.data + 1 & 3;
        } else if (cmat.isGate()) {
          this.data = this.data + 1 & 3 | this.data & -4;
        } else if (cmat.isStairs()) {
          switch (this.data) {
            case 0:
              this.data = 2;
              break;
            case 1:
              this.data = 3;
              break;
            case 2:
              this.data = 1;
              break;
            case 3:
              this.data = 0;
              break;
            case 4:
              this.data = 6;
              break;
            case 5:
              this.data = 7;
              break;
            case 6:
              this.data = 5;
              break;
            case 7:
              this.data = 4;
          }
        }

        if (type.name().contains("TERRACOTTA")) {
          int terracottaOtherBits = this.data & -4;
          int terracottaFacingBits = this.data & 3;
          switch (terracottaFacingBits) {
            case 0:
              this.data = 1 | terracottaOtherBits;
              break;
            case 1:
              this.data = 2 | terracottaOtherBits;
              break;
            case 2:
              this.data = 3 | terracottaOtherBits;
              break;
            case 3:
              this.data = 0 | terracottaOtherBits;
          }
        }
      } catch (Exception e) {
        this.data = this.data;
      }

      return this;
    }
  }

  public CMIBlock rotate90() {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      CMIMaterial cmat = CMIMaterial.get(this.block);
      BlockData blockData = this.block.getBlockData().clone();
      if (blockData instanceof Directional) {
        Directional directional =
            this.blockd == null
                ? (Directional) ((Directional) blockData).clone()
                : (Directional) this.blockd;
        BlockFace face = directional.getFacing();
        switch (face) {
          case NORTH:
            directional.setFacing(BlockFace.EAST);
            break;
          case EAST:
            directional.setFacing(BlockFace.SOUTH);
            break;
          case SOUTH:
            directional.setFacing(BlockFace.WEST);
            break;
          case WEST:
            directional.setFacing(BlockFace.NORTH);
        }

        if (this.blockd == null) {
          this.blockd = directional.clone();
        }

        return this;
      }

      if (!(blockData instanceof Rotatable)) {
        if (this.blockd == null) {
          this.blockd = blockData.clone();
        }

        return this;
      }

      Rotatable directional =
          this.blockd == null ? (Rotatable) blockData.clone() : (Rotatable) this.blockd;
      BlockFace face = directional.getRotation();
      switch (face) {
        case NORTH:
          directional.setRotation(BlockFace.EAST);
          break;
        case NORTH_NORTH_EAST:
          directional.setRotation(BlockFace.EAST_SOUTH_EAST);
          break;
        case NORTH_EAST:
          directional.setRotation(BlockFace.SOUTH_EAST);
          break;
        case EAST_NORTH_EAST:
          directional.setRotation(BlockFace.SOUTH_SOUTH_EAST);
          break;
        case EAST:
          directional.setRotation(BlockFace.SOUTH);
          break;
        case EAST_SOUTH_EAST:
          directional.setRotation(BlockFace.SOUTH_SOUTH_WEST);
          break;
        case SOUTH_EAST:
          directional.setRotation(BlockFace.SOUTH_WEST);
          break;
        case SOUTH_SOUTH_EAST:
          directional.setRotation(BlockFace.WEST_SOUTH_WEST);
          break;
        case SOUTH:
          directional.setRotation(BlockFace.WEST);
          break;
        case SOUTH_SOUTH_WEST:
          directional.setRotation(BlockFace.WEST_NORTH_WEST);
          break;
        case SOUTH_WEST:
          directional.setRotation(BlockFace.NORTH_WEST);
          break;
        case WEST_SOUTH_WEST:
          directional.setRotation(BlockFace.NORTH_NORTH_WEST);
          break;
        case WEST:
          directional.setRotation(BlockFace.NORTH);
          break;
        case WEST_NORTH_WEST:
          directional.setRotation(BlockFace.NORTH_NORTH_EAST);
          break;
        case NORTH_WEST:
          directional.setRotation(BlockFace.NORTH_EAST);
          break;
        case NORTH_NORTH_WEST:
          directional.setRotation(BlockFace.EAST_NORTH_EAST);
      }

      if (this.blockd == null) {
        this.blockd = directional;
      }
    }

    Material type = this.block.getType();
    this.data = this.data == null ? this.block.getData() : this.data;
    CMIMaterial cmat = CMIMaterial.get(this.block);

    try {
      label235:
      switch (cmat) {
        case DISPENSER:
        case DROPPER:
          int dispPower = this.data & 8;
          switch (this.data & -9) {
            case 2:
              this.data = 4 | dispPower;
              break label235;
            case 3:
              this.data = 5 | dispPower;
              break label235;
            case 4:
              this.data = 3 | dispPower;
              break label235;
            case 5:
              this.data = 2 | dispPower;
            default:
              break label235;
          }
        case HOPPER:
        case LADDER:
        case WALL_SIGN:
        case CHEST:
        case FURNACE:
        case LEGACY_BURNING_FURNACE:
        case ENDER_CHEST:
        case TRAPPED_CHEST:
          int extra = this.data & 8;
          int withoutFlags = this.data & -9;
          switch (withoutFlags) {
            case 2:
              this.data = 4 | extra;
              break label235;
            case 3:
              this.data = 5 | extra;
              break label235;
            case 4:
              this.data = 3 | extra;
              break label235;
            case 5:
              this.data = 2 | extra;
            default:
              break label235;
          }
        case LEVER:
          int thrown = this.data & 8;
          switch (this.data & -9) {
            case 0:
              this.data = 7 | thrown;
              break label235;
            case 1:
              this.data = 4 | thrown;
              break label235;
            case 2:
              this.data = 3 | thrown;
              break label235;
            case 3:
              this.data = 1 | thrown;
              break label235;
            case 4:
              this.data = 2 | thrown;
              break label235;
            case 5:
              this.data = 6 | thrown;
              break label235;
            case 6:
              this.data = 5 | thrown;
              break label235;
            case 7:
              this.data = 0 | thrown;
            default:
              break label235;
          }
        case TORCH:
        case LEGACY_REDSTONE_TORCH_OFF:
        case LEGACY_REDSTONE_TORCH_ON:
          switch (this.data) {
            case 1:
              this.data = 4;
              break;
            case 2:
              this.data = 3;
              break;
            case 3:
              this.data = 1;
              break;
            case 4:
              this.data = 2;
          }
        case REDSTONE_TORCH:
        case LEGACY_SIGN_POST:
        default:
          break;
        case RAIL:
          switch (this.data) {
            case 6:
              this.data = 9;
              break;
            case 7:
              this.data = 6;
              break;
            case 8:
              this.data = 7;
              break;
            case 9:
              this.data = 8;
          }
        case POWERED_RAIL:
        case DETECTOR_RAIL:
        case ACTIVATOR_RAIL:
          int power = this.data & -8;
          switch (this.data & 7) {
            case 0:
              this.data = 1 | power;
              break label235;
            case 1:
              this.data = 0 | power;
              break label235;
            case 2:
              this.data = 4 | power;
              break label235;
            case 3:
              this.data = 5 | power;
              break label235;
            case 4:
              this.data = 3 | power;
              break label235;
            case 5:
              this.data = 2 | power;
            default:
              break label235;
          }
        case PUMPKIN:
        case JACK_O_LANTERN:
          switch (this.data) {
            case 0:
              this.data = 3;
              break label235;
            case 1:
              this.data = 0;
              break label235;
            case 2:
              this.data = 1;
              break label235;
            case 3:
              this.data = 2;
            default:
              break label235;
          }
        case LEGACY_DIODE_BLOCK_OFF:
        case LEGACY_DIODE_BLOCK_ON:
        case LEGACY_REDSTONE_COMPARATOR_OFF:
        case LEGACY_REDSTONE_COMPARATOR_ON:
          int dir = this.data & 3;
          int delay = this.data - dir;
          switch (dir) {
            case 0:
              this.data = 3 | delay;
              break label235;
            case 1:
              this.data = 0 | delay;
              break label235;
            case 2:
              this.data = 1 | delay;
              break label235;
            case 3:
              this.data = 2 | delay;
            default:
              break label235;
          }
        case RED_MUSHROOM:
        case BROWN_MUSHROOM:
          if (this.data >= 10) {
            this.data = this.data;
            break;
          } else {
            this.data = this.data * 7 % 10;
          }
        case VINE:
          this.data = (this.data >> 1 | this.data << 3) & 15;
          break;
        case LEGACY_SKULL:
          switch (this.data) {
            case 2:
              this.data = 4;
              break label235;
            case 3:
              this.data = 5;
              break label235;
            case 4:
              this.data = 3;
              break label235;
            case 5:
              this.data = 2;
            default:
              break label235;
          }
        case ANVIL:
          int damage = this.data & -4;
          switch (this.data & 3) {
            case 0:
              this.data = 1 | damage;
              break label235;
            case 1:
              this.data = 2 | damage;
              break label235;
            case 2:
              this.data = 3 | damage;
              break label235;
            case 3:
              this.data = 0 | damage;
            default:
              break label235;
          }
        case STONE_BUTTON:
        case OAK_BUTTON:
          int poweredBit = this.data & 8;
          switch (this.data & -9) {
            case 1:
              this.data = 4 | poweredBit;
              break label235;
            case 2:
              this.data = 3 | poweredBit;
              break label235;
            case 3:
              this.data = 1 | poweredBit;
              break label235;
            case 4:
              this.data = 2 | poweredBit;
            default:
              break label235;
          }
        case OAK_DOOR:
        case IRON_DOOR:
          if ((this.data & 8) != 0) {
            break;
          }
        case TRIPWIRE_HOOK:
        case COCOA:
          int otherBits = this.data & -4;
          int facingBits = this.data & 3;
          switch (facingBits) {
            case 0:
              this.data = 3 | otherBits;
              break label235;
            case 1:
              this.data = 0 | otherBits;
              break label235;
            case 2:
              this.data = 1 | otherBits;
              break label235;
            case 3:
              this.data = 2 | otherBits;
            default:
              break label235;
          }
        case HAY_BLOCK:
        case OAK_LOG:
        case BIRCH_LOG:
        case SPRUCE_LOG:
        case JUNGLE_LOG:
          if (this.data >= 4 && this.data <= 11) {
            this.data = this.data ^ 12;
          }
          break;
        case OAK_TRAPDOOR:
        case IRON_TRAPDOOR:
          int withoutOrientation = this.data & -4;
          int orientation = this.data & 3;
          switch (orientation) {
            case 0:
              this.data = 2 | withoutOrientation;
              break;
            case 1:
              this.data = 3 | withoutOrientation;
              break;
            case 2:
              this.data = 1 | withoutOrientation;
              break;
            case 3:
              this.data = 0 | withoutOrientation;
          }
        case PISTON_HEAD:
        case STICKY_PISTON:
        case MOVING_PISTON:
          int rest = this.data & -8;
          switch (this.data & 7) {
            case 2:
              this.data = 4 | rest;
              break label235;
            case 3:
              this.data = 5 | rest;
              break label235;
            case 4:
              this.data = 3 | rest;
              break label235;
            case 5:
              this.data = 2 | rest;
            default:
              break label235;
          }
        case SIGN:
          this.data = (this.data + 12) % 16;
      }

      if (cmat.isBed()) {
        this.data = this.data & -4 | this.data - 1 & 3;
      } else if (cmat.isGate()) {
        this.data = this.data + 3 & 3 | this.data & -4;
      } else if (cmat.isStairs()) {
        switch (this.data) {
          case 0:
            this.data = 3;
            break;
          case 1:
            this.data = 2;
            break;
          case 2:
            this.data = 0;
            break;
          case 3:
            this.data = 1;
            break;
          case 4:
            this.data = 7;
            break;
          case 5:
            this.data = 6;
            break;
          case 6:
            this.data = 4;
            break;
          case 7:
            this.data = 5;
        }
      }

      if (type.name().contains("TERRACOTTA")) {
        int terracottaOtherBits = this.data & -4;
        int terracottaFacingBits = this.data & 3;
        switch (terracottaFacingBits) {
          case 0:
            this.data = 3 | terracottaOtherBits;
            break;
          case 1:
            this.data = 0 | terracottaOtherBits;
            break;
          case 2:
            this.data = 1 | terracottaOtherBits;
            break;
          case 3:
            this.data = 2 | terracottaOtherBits;
        }
      }
    } catch (Exception e) {
      this.data = this.data;
    }

    return this;
  }

  public Object getData() {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      return this.blockd == null ? this.block.getBlockData().clone() : this.blockd;
    } else {
      return this.data == null ? this.block.getData() : this.data.byteValue();
    }
  }

  public CMIBlock setData(Object data) {
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      if (data != null) {
        BlockData blockData = (BlockData) data;
        this.blockd = blockData.clone();
      } else {
        this.blockd = data;
      }
    } else {
      try {
        this.data = (Integer) data;
      } catch (ClassCastException var3) {
      }
    }

    return this;
  }

  public boolean hasInventory() {
    return this.block.getState() instanceof InventoryHolder;
  }

  public Inventory getInventory() {
    if (this.block.getState() instanceof InventoryHolder) {
      try {
        this.block.getChunk().load(false);
        this.block.getChunk().setForceLoaded(true);
      } catch (Throwable var2) {
      }

      InventoryHolder holder = (InventoryHolder) this.block.getState();
      return holder.getInventory();
    } else {
      return null;
    }
  }

  public Block getSecondaryBedBlock() {
    if (this.block != null && CMIMaterial.isBed(this.block.getType())) {
      BlockFace facing = this.getFacing();
      CMIBlock.BedPart part = this.getBedPart();
      if (facing != null && part != null) {
        Location loc = this.block.getLocation().clone();
        switch (facing) {
          case NORTH:
            switch (part) {
              case HEAD:
                return loc.add(0.0, 0.0, 1.0).getBlock();
              case FOOT:
                return loc.add(0.0, 0.0, -1.0).getBlock();
              default:
                return null;
            }
          case EAST:
            switch (part) {
              case HEAD:
                return loc.add(-1.0, 0.0, 0.0).getBlock();
              case FOOT:
                return loc.add(1.0, 0.0, 0.0).getBlock();
              default:
                return null;
            }
          case SOUTH:
            switch (part) {
              case HEAD:
                return loc.add(0.0, 0.0, -1.0).getBlock();
              case FOOT:
                return loc.add(0.0, 0.0, 1.0).getBlock();
              default:
                return null;
            }
          case WEST:
            switch (part) {
              case HEAD:
                return loc.add(1.0, 0.0, 0.0).getBlock();
              case FOOT:
                return loc.add(-1.0, 0.0, 0.0).getBlock();
            }
        }

        return null;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  public Block getBedFootBlock() {
    if (this.block != null && CMIMaterial.isBed(this.block.getType())) {
      Block sec = this.getSecondaryBedBlock();
      Location loc = this.block.getLocation();
      if (this.getBedPart() != null) {
        if (this.getBedPart() == CMIBlock.BedPart.FOOT) {
          return this.block;
        }

        if (sec != null) {
          CMIBlock cbs = new CMIBlock(sec);
          if (cbs.getBedPart() != null && cbs.getBedPart() == CMIBlock.BedPart.FOOT) {
            return sec;
          }
        }
      }

      return null;
    } else {
      return null;
    }
  }

  public Block getBlock() {
    return this.block;
  }

  public static HashMap<BlockStateType, String> getBlockStates(Block block) {
    HashMap<BlockStateType, String> map = new HashMap<>();
    if (!Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      return map;
    }

    try {
      String blockData = block.getBlockData().getAsString();
      if (blockData.contains("[")) {
        String part = blockData.split("\\[", 2)[1];
        part = part.substring(0, part.length() - 1);

        for (String one : part.split(",")) {
          String[] sp = one.split("=", 2);
          BlockStateType type = BlockStateType.getByName(sp[0]);
          if (type != null) {
            map.put(type, sp[1]);
          }
        }
      }
    } catch (Throwable var10) {
    }

    return map;
  }

  public enum BedPart {
    HEAD,
    FOOT;
  }

  public enum Bisect {
    TOP,
    BOTTOM;
  }

  public enum FlipDirection {
    NORTH_SOUTH,
    WEST_EAST,
    UP_DOWN,
    ROTATE_90_CW,
    ROTATE_90_CCW,
    ROTATE_180;
  }

  public enum SignSide {
    FRONT,
    BACK;
  }

  public enum StairShape {
    INNER_LEFT,
    INNER_RIGHT,
    OUTER_LEFT,
    OUTER_RIGHT,
    STRAIGHT;

    public static CMIBlock.StairShape getByName(String name) {
      for (CMIBlock.StairShape one : values()) {
        if (one.toString().equalsIgnoreCase(name)) {
          return one;
        }
      }

      return null;
    }
  }

  public enum blockDirection {
    none(-1),
    upWest(0),
    upEast(1),
    upNorth(2),
    upSouth(3),
    downWest(4),
    downEast(5),
    downNorth(6),
    downSouth(7);

    private int dir;

    blockDirection(int dir) {
      this.dir = dir;
    }

    public int getDir() {
      return this.dir;
    }

    public static CMIBlock.blockDirection getByDir(int dir) {
      for (CMIBlock.blockDirection one : values()) {
        if (one.getDir() == dir) {
          return one;
        }
      }

      return null;
    }
  }
}
