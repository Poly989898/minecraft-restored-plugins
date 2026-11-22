package com.Zrips.CMI.Containers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CMIRayTrace {
  Vector origin;
  Vector direction;
  World world;

  public CMIRayTrace(Player player) {
    this.origin = player.getEyeLocation().toVector();
    this.direction = player.getLocation().getDirection();
    this.world = player.getWorld();
  }

  public CMIRayTrace(World worldSecond, Vector vector, Vector vectorSecond) {
    this.origin = vector;
    this.direction = vectorSecond;
    this.world = worldSecond;
  }

  public Vector getPostion(double number) {
    return this.origin.clone().add(this.direction.clone().multiply(number));
  }

  public ArrayList<Vector> traverse(double number, double amount) {
    ArrayList<Vector> arrayList = new ArrayList();
    double amountSecond = 0.0;

    while (amountSecond <= number) {
      arrayList.add(this.getPostion(amountSecond));
      amountSecond += amount;
    }

    return arrayList;
  }

  public ArrayList<Block> getBlocks(double number) {
    return this.getBlocks(number, 0.01);
  }

  public ArrayList<Block> getBlocks(double number, double amount) {
    var arrayList = this.traverse(number, amount);
    LinkedHashSet<Block> linkedHashSet = new LinkedHashSet();
    CMIHitBox cmiHitBox = null;
    Vector vector = null;
    Long time = System.nanoTime();
    boolean flag = false;
    Block block = null;
    boolean flagSecond = false;
    boolean flagThird = true;

    for (Vector vectorSecond : arrayList) {
      Block blockSecond = null;
      CMIHitBox cmiHitBoxValue = null;
      if (vector != null
          && vector.getBlockX() == vectorSecond.getBlockX()
          && vector.getBlockY() == vectorSecond.getBlockY()
          && vector.getBlockZ() == vectorSecond.getBlockZ()) {
        if (flag) {
          continue;
        }

        blockSecond = block;
        cmiHitBoxValue = cmiHitBox;
      } else {
        flagThird = true;
        flagSecond = false;
      }

      if (blockSecond == null) {
        blockSecond =
            this.world.getBlockAt(
                vectorSecond.getBlockX(), vectorSecond.getBlockY(), vectorSecond.getBlockZ());
      }

      if (cmiHitBoxValue == null) {
        cmiHitBoxValue = new CMIHitBox(blockSecond);
      }

      flag = false;
      block = blockSecond;
      vector = vectorSecond;
      if (!flagSecond && CMIMaterial.isAir(blockSecond.getType())) {
        flagSecond = true;
        linkedHashSet.add(blockSecond);
        flag = true;
      } else {
        cmiHitBox = cmiHitBoxValue;
        if (flagThird) {
          flagThird =
              cmiHitBoxValue.byExactXYZ(
                  vectorSecond.getX() % 1.0,
                  vectorSecond.getY() % 1.0,
                  vectorSecond.getZ() % 1.0,
                  0.5);
        }

        if (cmiHitBoxValue.collides(vectorSecond)) {
          flag = true;
          linkedHashSet.add(blockSecond);
          break;
        }

        linkedHashSet.add(blockSecond);
      }
    }

    return new ArrayList<>(linkedHashSet);
  }

  public ArrayList<Block> getLastTwoBlocks(double number) {
    return this.getLastTwoBlocks(number, 0.05);
  }

  public ArrayList<Block> getLastTwoBlocks(double number, double amount) {
    var arrayList = this.getBlocks(number, amount);
    ArrayList<Block> items = new ArrayList();
    if (arrayList.size() > 1) {
      items.add((Block) arrayList.get(arrayList.size() - 2));
    }

    if (!arrayList.isEmpty()) {
      items.add((Block) arrayList.get(arrayList.size() - 1));
    }

    return items;
  }
}
