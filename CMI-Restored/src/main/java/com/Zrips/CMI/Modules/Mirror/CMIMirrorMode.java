package com.Zrips.CMI.Modules.Mirror;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Container.CMINumber;

public class CMIMirrorMode {
  private boolean enabled = false;
  private CMIMirrorType type = null;
  private int sections = 2;

  public CMIMirrorMode(CMIMirrorType cmiMirrorType) {
    this.type = cmiMirrorType;
  }

  public int getSections() {
    return this.sections;
  }

  public void setSections(int value) {
    this.sections = CMINumber.clamp(value, 2, CMI.getInstance().getMirrorManager().getMaxBlocks());
  }

  public CMIMirrorType getType() {
    return this.type;
  }

  public void setType(CMIMirrorType cmiMirrorType) {
    this.type = cmiMirrorType;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean flag) {
    this.enabled = flag;
  }
}
