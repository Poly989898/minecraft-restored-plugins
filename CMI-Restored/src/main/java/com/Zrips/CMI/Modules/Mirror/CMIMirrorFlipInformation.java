package com.Zrips.CMI.Modules.Mirror;

import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.Container.CMIBlock.FlipDirection;

public class CMIMirrorFlipInformation {
  List<FlipDirection> flip = new ArrayList<>();

  public CMIMirrorFlipInformation add(FlipDirection flipDirection) {
    this.flip.add(flipDirection);
    return this;
  }

  public List<FlipDirection> get() {
    return this.flip;
  }
}
