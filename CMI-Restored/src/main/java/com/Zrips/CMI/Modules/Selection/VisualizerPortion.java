package com.Zrips.CMI.Modules.Selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.Zrips.CMILib.Container.CMICuboidArea;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Version.Schedulers.CMITask;

public class VisualizerPortion {
  private CMITask id = null;
  private List<CMICuboidArea> areas = new ArrayList<>();
  private List<CMIVector3D> locations = new ArrayList<>();
  private List<CMIVector3D> locations2 = new ArrayList<>();

  public void cancel() {
    if (this.id != null) {
      this.id.cancel();
    }
  }

  public List<CMICuboidArea> getAreas() {
    return this.areas;
  }

  public void setAreas(CMICuboidArea[] cmiCuboidAreas) {
    this.areas = Arrays.asList(cmiCuboidAreas);
  }

  public void setAreas(ArrayList<CMICuboidArea> arrayList) {
    this.areas = arrayList;
  }

  public void setAreas(CMICuboidArea cmiCuboidArea) {
    this.areas = new ArrayList<>();
    this.areas.add(cmiCuboidArea);
  }

  public CMITask getTask() {
    return this.id;
  }

  public void setTask(CMITask cmiTask) {
    this.id = cmiTask;
  }

  public List<CMIVector3D> getLocations() {
    return this.locations;
  }

  public void setLocations(List<CMIVector3D> list) {
    this.locations = list;
  }

  public List<CMIVector3D> getLocations2() {
    return this.locations2;
  }

  public void setLocations2(List<CMIVector3D> list) {
    this.locations2 = list;
  }
}
