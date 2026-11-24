package net.Indyuce.mmocore.skilltree.display;

import net.Indyuce.mmocore.skilltree.NodeState;

public abstract class DisplayInfo {
   protected final NodeState state;

   protected DisplayInfo(NodeState state) {
      this.state = state;
   }

   public NodeState getState() {
      return this.state;
   }
}
