package com.Zrips.CMI.Modules.Selection;

import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.Container.CMICuboidArea;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Visualizer {
  private Player player;
  private long start;
  private VisualizerPortion mainArea = new VisualizerPortion();
  private VisualizerPortion errorArea = new VisualizerPortion();
  private boolean once = false;
  private int starting = 0;
  private Location loc = null;

  public Visualizer(Player newPlayer) {
    this.player = newPlayer;
    this.start = System.currentTimeMillis();
  }

  public void cancelAll() {
    this.getMainArea().cancel();
    this.getErrorArea().cancel();
    this.loc = null;
  }

  public boolean isSameLoc() {
    return this.loc != null
            && this.loc.getWorld() == this.player.getWorld()
            && (this.getErrorArea().getAreas().isEmpty()
                || !this.getErrorArea().getLocations().isEmpty())
        ? !(this.loc.distance(this.player.getLocation()) > 1.0)
        : false;
  }

  public long getStart() {
    return this.start;
  }

  public void setStart(long longValue) {
    this.start = longValue;
  }

  public Player getPlayer() {
    return this.player;
  }

  public void setPlayer(Player newPlayer) {
    this.player = newPlayer;
  }

  public List<CMICuboidArea> getAreas() {
    return this.getMainArea().getAreas();
  }

  public void setAreas(CMICuboidArea[] cmiCuboidAreas) {
    this.getMainArea().setAreas(cmiCuboidAreas);
  }

  public void setAreas(ArrayList<CMICuboidArea> arrayList) {
    this.getMainArea().setAreas(arrayList);
  }

  public void setAreas(CMICuboidArea cmiCuboidArea) {
    this.getMainArea().setAreas(cmiCuboidArea);
  }

  public List<CMICuboidArea> getErrorAreas() {
    return this.getErrorArea().getAreas();
  }

  public void setErrorAreas(CMICuboidArea[] cmiCuboidAreas) {
    this.getErrorArea().setAreas(cmiCuboidAreas);
  }

  public void setErrorAreas(ArrayList<CMICuboidArea> arrayList) {
    this.getErrorArea().setAreas(arrayList);
  }

  public void setErrorAreas(CMICuboidArea cmiCuboidArea) {
    this.getErrorArea().setAreas(cmiCuboidArea);
  }

  public CMITask getTask() {
    return this.getMainArea().getTask();
  }

  public void setTask(CMITask cmiTask) {
    this.getMainArea().setTask(cmiTask);
  }

  public CMITask getErrorTask() {
    return this.getErrorArea().getTask();
  }

  public void setErrorTask(CMITask cmiTask) {
    this.getErrorArea().setTask(cmiTask);
  }

  public boolean isOnce() {
    return this.once;
  }

  public void setOnce(boolean flag) {
    this.once = flag;
  }

  public List<CMIVector3D> getLocations() {
    return this.getMainArea().getLocations();
  }

  public void setLocations(List<CMIVector3D> list) {
    this.getMainArea().setLocations(list);
  }

  public List<CMIVector3D> getLocations2() {
    return this.getMainArea().getLocations2();
  }

  public void setLocations2(List<CMIVector3D> list) {
    this.getMainArea().setLocations2(list);
  }

  public List<CMIVector3D> getErrorLocations() {
    return this.getErrorArea().getLocations();
  }

  public void setErrorLocations(List<CMIVector3D> list) {
    this.getErrorArea().setLocations(list);
  }

  public List<CMIVector3D> getErrorLocations2() {
    return this.getErrorArea().getLocations2();
  }

  public void setErrorLocations2(List<CMIVector3D> list) {
    this.getErrorArea().setLocations2(list);
  }

  public Location getLoc() {
    return this.loc;
  }

  public void setLoc(Location location) {
    this.loc = location;
  }

  public int getStarting() {
    return this.starting;
  }

  public void setStarting(int value) {
    this.starting = value;
  }

  public VisualizerPortion getMainArea() {
    return this.mainArea;
  }

  public VisualizerPortion getErrorArea() {
    return this.errorArea;
  }
}
