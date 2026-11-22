package com.Zrips.CMI.Modules.Selection;

import com.Zrips.CMI.Modules.Display.CMIBlockDisplay;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.World;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;

public class CMISelectionVisualBlock {
  private List<CMIBlockDisplay> virtualBlocks = new ArrayList<>();
  private CMIMaterial material = CMIMaterial.GREEN_STAINED_GLASS;
  private double size = 1.0;
  private double width = 0.05;

  public List<CMIBlockDisplay> getVirtualBlocks() {
    return this.virtualBlocks;
  }

  public CompletableFuture<Boolean> show(Player player, CMIVector3D cmiVector3D) {
    boolean flag = !this.virtualBlocks.isEmpty();
    if (this.virtualBlocks.isEmpty()) {
      this.generateVirtualBlocks(player, cmiVector3D);
    }

    if (!flag) {
      for (CMIBlockDisplay cmiBlockDisplay : this.virtualBlocks) {
        cmiBlockDisplay.show(player);
      }
    }

    return CompletableFuture.completedFuture(false);
  }

  public CompletableFuture<Boolean> destroy(Player player) {
    for (CMIBlockDisplay cmiBlockDisplay : this.virtualBlocks) {
      cmiBlockDisplay.destroy(player);
    }

    this.virtualBlocks.clear();
    return CompletableFuture.completedFuture(false);
  }

  private CompletableFuture<Boolean> generateVirtualBlocks(Player player, CMIVector3D cmiVector3D) {
    World world = player.getWorld();
    float floatValue = (float) ((this.size + this.width) * 1.0001F);
    cmiVector3D =
        new CMIVector3D(
            (int) cmiVector3D.getX() - this.width / 2.0,
            (int) cmiVector3D.getY() - this.width / 2.0,
            (int) cmiVector3D.getZ() - this.width / 2.0);
    this.virtualBlocks.add(
        this.createEdge(
            world, cmiVector3D.clone(), new CMIVector3D(floatValue, this.width, this.width)));
    this.virtualBlocks.add(
        this.createEdge(
            world,
            cmiVector3D.clone().add(0.0, 0.0, this.size),
            new CMIVector3D(floatValue, this.width, this.width)));
    this.virtualBlocks.add(
        this.createEdge(
            world, cmiVector3D.clone(), new CMIVector3D(this.width, this.width, floatValue)));
    this.virtualBlocks.add(
        this.createEdge(
            world,
            cmiVector3D.clone().add(this.size, 0.0, 0.0),
            new CMIVector3D(this.width, this.width, floatValue)));
    this.virtualBlocks.add(
        this.createEdge(
            world,
            cmiVector3D.clone().add(0.0, this.size, 0.0),
            new CMIVector3D(floatValue, this.width, this.width)));
    this.virtualBlocks.add(
        this.createEdge(
            world,
            cmiVector3D.clone().add(0.0, this.size, this.size),
            new CMIVector3D(floatValue, this.width, this.width)));
    this.virtualBlocks.add(
        this.createEdge(
            world,
            cmiVector3D.clone().add(0.0, this.size, 0.0),
            new CMIVector3D(this.width, this.width, floatValue)));
    this.virtualBlocks.add(
        this.createEdge(
            world,
            cmiVector3D.clone().add(this.size, this.size, 0.0),
            new CMIVector3D(this.width, this.width, floatValue)));
    this.virtualBlocks.add(
        this.createEdge(
            world, cmiVector3D.clone(), new CMIVector3D(this.width, floatValue, this.width)));
    this.virtualBlocks.add(
        this.createEdge(
            world,
            cmiVector3D.clone().add(this.size, 0.0, 0.0),
            new CMIVector3D(this.width, floatValue, this.width)));
    this.virtualBlocks.add(
        this.createEdge(
            world,
            cmiVector3D.clone().add(0.0, 0.0, this.size),
            new CMIVector3D(this.width, floatValue, this.width)));
    this.virtualBlocks.add(
        this.createEdge(
            world,
            cmiVector3D.clone().add(this.size, 0.0, this.size),
            new CMIVector3D(this.width, floatValue, this.width)));
    return CompletableFuture.completedFuture(false);
  }

  private CMIBlockDisplay createEdge(
      World world, CMIVector3D cmiVector3D, CMIVector3D cmiVector3D2) {
    float floatValue = new Random().nextFloat() * 1.0E-4F;
    cmiVector3D.plus(floatValue, floatValue, floatValue);
    CMIBlockDisplay cmiBlockDisplay = new CMIBlockDisplay(cmiVector3D.toLocation(world));
    cmiBlockDisplay.setBlockData(this.getMaterial().getMaterial().createBlockData());
    cmiBlockDisplay.setBillboard(Billboard.FIXED);
    cmiBlockDisplay.setBrightness(15, 15);
    Transformation transformation = cmiBlockDisplay.getBlockTransform();
    transformation
        .getScale()
        .set((float) cmiVector3D2.getX(), (float) cmiVector3D2.getY(), (float) cmiVector3D2.getZ());
    cmiBlockDisplay.setTransformation(transformation);
    cmiBlockDisplay.setRange(3);
    return cmiBlockDisplay;
  }

  public CMIMaterial getMaterial() {
    return this.material;
  }

  public CMISelectionVisualBlock setMaterial(CMIMaterial cmiMaterial) {
    this.material = cmiMaterial;
    return this;
  }

  public double getSize() {
    return this.size;
  }

  public CMISelectionVisualBlock setSize(double number) {
    this.size = number;
    return this;
  }
}
