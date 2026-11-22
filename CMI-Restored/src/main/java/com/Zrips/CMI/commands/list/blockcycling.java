/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  net.Zrips.CMILib.Container.CMIBlock
 *  net.Zrips.CMILib.FileHandler.ConfigReader
 *  net.Zrips.CMILib.Items.CMIMaterial
 *  net.Zrips.CMILib.Version.Version
 *  org.bukkit.Axis
 *  org.bukkit.Bukkit
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.data.Bisected$Half
 *  org.bukkit.block.data.BlockData
 *  org.bukkit.block.data.Directional
 *  org.bukkit.block.data.Orientable
 *  org.bukkit.block.data.Rotatable
 *  org.bukkit.block.data.type.Fence
 *  org.bukkit.block.data.type.Gate
 *  org.bukkit.block.data.type.Slab
 *  org.bukkit.block.data.type.Slab$Type
 *  org.bukkit.block.data.type.Stairs
 *  org.bukkit.block.data.type.Stairs$Shape
 *  org.bukkit.block.data.type.Wall
 *  org.bukkit.block.data.type.Wall$Height
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.block.BlockBreakEvent
 */
package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import net.Zrips.CMILib.Container.CMIBlock;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Fence;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.Wall;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

public class blockcycling implements Cmd {
  @Override
  public void getExtra(ConfigReader configReader) {}

  @Override
  @CAnnotation(
      info = "Cycle block states",
      args = "",
      tab = {"(forward/backward)"},
      regVar = {0, 1},
      consoleVar = {666},
      others = false)
  public Boolean perform(CMI cMI, CommandSender commandSender, String[] stringArray) {
    block187:
    {
      BlockState blockState;
      int n;
      block172:
      {
        BlockFace rotated;  Stairs stairs;  Gate gate;
        BlockData blockData;
        CMIMaterial cMIMaterial;
        BlockFace blockFace;
        Block block;
        block185:
        {
          block183:
          {
            block177:
            {
              block180:
              {
                block178:
                {
                  block175:
                  {
                    block173:
                    {
                      n = 1;
                      if (stringArray.length > 0 && stringArray[0].equalsIgnoreCase("backward")) {
                        n = -1;
                      }
                      Player player = (Player) commandSender;
                      block = cMI.getUtilManager().getTargetBlock(player, 50);
                      BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
                      Bukkit.getServer().getPluginManager().callEvent((Event) blockBreakEvent);
                      if (blockBreakEvent.isCancelled()) {
                        return null;
                      }
                      blockState = block.getState();
                      if (!Version.isCurrentHigher((Version) Version.v1_13_R1)) break block172;
                      CMIBlock cMIBlock = new CMIBlock(block);
                      blockFace = cMIBlock.getFacing();
                      cMIMaterial = CMIMaterial.get((Block) block);
                      if (!(block.getBlockData() instanceof Orientable)) break block173;
                      Orientable orientable = (Orientable) block.getBlockData();
                      Axis axis = null;
                      switch (orientable.getAxis().toString()) {
                        case "X":
                          {
                            axis = Axis.Y;
                            break;
                          }
                        case "Y":
                          {
                            axis = Axis.Z;
                            break;
                          }
                        case "Z":
                          {
                            axis = Axis.X;
                            break;
                          }
                      }
                      orientable.setAxis(axis);
                      try {
                        block
                            .getClass()
                            .getMethod("setBlockData", BlockData.class)
                            .invoke((Object) block, orientable);
                      } catch (IllegalAccessException
                          | IllegalArgumentException
                          | NoSuchMethodException
                          | SecurityException
                          | InvocationTargetException exception) {
                        exception.printStackTrace();
                      }
                      return true;
                    }
                    if (cMIMaterial.isFence()) {
                      Fence fence = (Fence) block.getBlockData();
                      int rotation =
                          (fence.hasFace(BlockFace.EAST) ? 1 : 0)
                              + (fence.hasFace(BlockFace.WEST) ? 2 : 0)
                              + (fence.hasFace(BlockFace.NORTH) ? 4 : 0)
                              + (fence.hasFace(BlockFace.SOUTH) ? 8 : 0);
                      if ((rotation += n) >= 16) {
                        rotation = 0;
                      }
                      if (rotation < 0) {
                        rotation = 15;
                      }
                      if (rotation >= 8) {
                        fence.setFace(BlockFace.SOUTH, true);
                        rotation -= 8;
                      } else {
                        fence.setFace(BlockFace.SOUTH, false);
                      }
                      if (rotation >= 4) {
                        fence.setFace(BlockFace.NORTH, true);
                        rotation -= 4;
                      } else {
                        fence.setFace(BlockFace.NORTH, false);
                      }
                      if (rotation >= 2) {
                        fence.setFace(BlockFace.WEST, true);
                        rotation -= 2;
                      } else {
                        fence.setFace(BlockFace.WEST, false);
                      }
                      if (rotation >= 1) {
                        fence.setFace(BlockFace.EAST, true);
                        --rotation;
                      } else {
                        fence.setFace(BlockFace.EAST, false);
                      }
                      try {
                        block
                            .getClass()
                            .getMethod("setBlockData", BlockData.class)
                            .invoke((Object) block, fence);
                      } catch (IllegalAccessException
                          | IllegalArgumentException
                          | NoSuchMethodException
                          | SecurityException
                          | InvocationTargetException exception) {
                        exception.printStackTrace();
                      }
                      return true;
                    }
                    if (cMIMaterial.isWall()) {
                      Wall wall = (Wall) block.getBlockData();
                      Random random = new Random();
                      int randomHeight = random.nextInt(3);
                      wall.setHeight(
                          BlockFace.EAST,
                          randomHeight == 1
                              ? Wall.Height.NONE
                              : (randomHeight == 2 ? Wall.Height.LOW : Wall.Height.TALL));
                      randomHeight = random.nextInt(3);
                      wall.setHeight(
                          BlockFace.WEST,
                          randomHeight == 1
                              ? Wall.Height.NONE
                              : (randomHeight == 2 ? Wall.Height.LOW : Wall.Height.TALL));
                      randomHeight = random.nextInt(3);
                      wall.setHeight(
                          BlockFace.NORTH,
                          randomHeight == 1
                              ? Wall.Height.NONE
                              : (randomHeight == 2 ? Wall.Height.LOW : Wall.Height.TALL));
                      randomHeight = random.nextInt(3);
                      wall.setHeight(
                          BlockFace.SOUTH,
                          randomHeight == 1
                              ? Wall.Height.NONE
                              : (randomHeight == 2 ? Wall.Height.LOW : Wall.Height.TALL));
                      try {
                        block
                            .getClass()
                            .getMethod("setBlockData", BlockData.class)
                            .invoke((Object) block, wall);
                      } catch (IllegalAccessException
                          | IllegalArgumentException
                          | NoSuchMethodException
                          | SecurityException
                          | InvocationTargetException exception) {
                        exception.printStackTrace();
                      }
                      return true;
                    }
                    if (!cMIMaterial.isSlab()) break block175;
                    Slab slab = (Slab) block.getBlockData();
                    switch (slab.getType().toString()) {
                      case "BOTTOM":
                        {
                          slab.setType(Slab.Type.TOP);
                          break;
                        }
                      case "DOUBLE":
                        {
                          slab.setType(Slab.Type.BOTTOM);
                          break;
                        }
                      case "TOP":
                        {
                          slab.setType(Slab.Type.BOTTOM);
                          break;
                        }
                    }
                    try {
                      block
                          .getClass()
                          .getMethod("setBlockData", BlockData.class)
                          .invoke((Object) block, slab);
                    } catch (IllegalAccessException
                        | IllegalArgumentException
                        | NoSuchMethodException
                        | SecurityException
                        | InvocationTargetException exception) {
                      exception.printStackTrace();
                    }
                    return true;
                  }
                  if (blockFace == null) {
                    return null;
                  }
                  blockData = block.getBlockData();
                  if (!(blockData instanceof Directional)) break block177;
                  if (!cMIMaterial.equals((Object) CMIMaterial.HOPPER)) break block178;
                  switch (blockFace.toString()) {
                    case "NORTH":
                      {
                        blockFace = BlockFace.DOWN;
                        break;
                      }
                    case "DOWN":
                      {
                        blockFace = BlockFace.NORTH;
                        break;
                      }
                  }
                }
                if (!cMIMaterial.equals((Object) CMIMaterial.PISTON)
                    && !cMIMaterial.equals((Object) CMIMaterial.STICKY_PISTON)
                    && !cMIMaterial.equals((Object) CMIMaterial.END_ROD)
                    && !cMIMaterial.equals((Object) CMIMaterial.DISPENSER)) break block180;
                switch (blockFace.toString()) {
                  case "NORTH":
                    {
                      blockFace = BlockFace.UP;
                      break;
                    }
                  case "UP":
                    {
                      blockFace = BlockFace.DOWN;
                      break;
                    }
                  case "DOWN":
                    {
                      blockFace = BlockFace.NORTH;
                      break;
                    }
                }
              }
              switch (blockFace.toString()) {
                case "EAST":
                  {
                    blockFace = BlockFace.SOUTH;
                    break;
                  }
                case "NORTH":
                  {
                    blockFace = BlockFace.EAST;
                    break;
                  }
                case "SOUTH":
                  {
                    blockFace = BlockFace.WEST;
                    break;
                  }
                case "WEST":
                  {
                    blockFace = BlockFace.NORTH;
                    break;
                  }
              }
              ((Directional) blockData).setFacing(blockFace);
            }
            if (!(blockData instanceof Rotatable)) break block183;
            rotated = null;
            switch (blockFace.toString()) {
              case "EAST":
                {
                  rotated = BlockFace.EAST_SOUTH_EAST;
                  break;
                }
              case "EAST_NORTH_EAST":
                {
                  rotated = BlockFace.EAST;
                  break;
                }
              case "EAST_SOUTH_EAST":
                {
                  rotated = BlockFace.SOUTH_EAST;
                  break;
                }
              case "NORTH":
                {
                  rotated = BlockFace.NORTH_NORTH_EAST;
                  break;
                }
              case "NORTH_EAST":
                {
                  rotated = BlockFace.EAST_NORTH_EAST;
                  break;
                }
              case "NORTH_NORTH_EAST":
                {
                  rotated = BlockFace.NORTH_EAST;
                  break;
                }
              case "NORTH_NORTH_WEST":
                {
                  rotated = BlockFace.NORTH;
                  break;
                }
              case "NORTH_WEST":
                {
                  rotated = BlockFace.NORTH_NORTH_WEST;
                  break;
                }
              case "SOUTH":
                {
                  rotated = BlockFace.SOUTH_SOUTH_WEST;
                  break;
                }
              case "SOUTH_EAST":
                {
                  rotated = BlockFace.SOUTH_SOUTH_EAST;
                  break;
                }
              case "SOUTH_SOUTH_EAST":
                {
                  rotated = BlockFace.SOUTH;
                  break;
                }
              case "SOUTH_SOUTH_WEST":
                {
                  rotated = BlockFace.SOUTH_WEST;
                  break;
                }
              case "SOUTH_WEST":
                {
                  rotated = BlockFace.WEST_SOUTH_WEST;
                  break;
                }
              case "WEST":
                {
                  rotated = BlockFace.WEST_NORTH_WEST;
                  break;
                }
              case "WEST_NORTH_WEST":
                {
                  rotated = BlockFace.NORTH_WEST;
                  break;
                }
              case "WEST_SOUTH_WEST":
                {
                  rotated = BlockFace.WEST;
                  break;
                }
            }
            if (rotated != null) {
              ((Rotatable) blockData).setRotation((BlockFace) rotated);
            }
          }
          if (!cMIMaterial.isStairs()) break block185;
          stairs = (Stairs) blockData;
          Stairs.Shape shape = stairs.getShape();
          switch (shape.toString()) {
            case "INNER_LEFT":
              {
                stairs.setShape(Stairs.Shape.INNER_RIGHT);
                break;
              }
            case "INNER_RIGHT":
              {
                stairs.setShape(Stairs.Shape.OUTER_LEFT);
                break;
              }
            case "OUTER_LEFT":
              {
                stairs.setShape(Stairs.Shape.OUTER_RIGHT);
                break;
              }
            case "OUTER_RIGHT":
              {
                stairs.setShape(Stairs.Shape.STRAIGHT);
                break;
              }
            case "STRAIGHT":
              {
                stairs.setShape(Stairs.Shape.INNER_LEFT);
                if (stairs.getHalf().toString().equalsIgnoreCase("BOTTOM")) {
                  stairs.setHalf(Bisected.Half.TOP);
                  break;
                }
                if (!stairs.getHalf().toString().equalsIgnoreCase("TOP")) break;
                stairs.setHalf(Bisected.Half.BOTTOM);
                break;
              }
          }
        }
        block.setBlockData(blockData);
        if (cMIMaterial.isGate() && blockFace.toString().equals("NORTH")) {
          gate = (Gate) block.getBlockData();
          gate.setInWall(!gate.isInWall());
          try {
            block
                .getClass()
                .getMethod("setBlockData", BlockData.class)
                .invoke((Object) block, gate);
          } catch (IllegalAccessException
              | IllegalArgumentException
              | NoSuchMethodException
              | SecurityException
              | InvocationTargetException exception) {
            exception.printStackTrace();
          }
        }
        break block187;
      }
      try {
        if (blockState.getData().getData() + n < 0) {
          this.lowerToMinimal(blockState, (short) 15);
        } else {
          blockState.getData().setData((byte) (blockState.getData().getData() + n));
        }
        blockState.update();
      } catch (Exception exception) {
        exception.printStackTrace();
        if (n == 1) {
          blockState.getData().setData((byte) 0);
        } else {
          this.lowerToMinimal(blockState, (short) 15);
        }
        try {
          blockState.update();
        } catch (Exception exception2) {
          exception2.printStackTrace();
        }
      }
    }
    return true;
  }

  @Deprecated
  private void lowerToMinimal(BlockState blockState, short s) {
    if (s <= 0) {
      return;
    }
    try {
      blockState.getData().setData((byte) s);
      blockState.update();
    } catch (Exception exception) {
      exception.printStackTrace();
      this.lowerToMinimal(blockState, (short) (s - 1));
    }
  }
}
