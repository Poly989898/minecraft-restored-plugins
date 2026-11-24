package net.Indyuce.mmocore.skilltree.display;

import java.util.Objects;
import net.Indyuce.mmocore.skilltree.NodeState;
import net.Indyuce.mmocore.skilltree.PathType;

public class PathDisplayInfo extends DisplayInfo {
   private final PathType type;

   public PathDisplayInfo(PathType type, NodeState status) {
      super(status);
      this.type = type;
   }

   public PathType getType() {
      return this.type;
   }

   public NodeState getStatus() {
      return this.state;
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.state);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PathDisplayInfo that = (PathDisplayInfo)o;
         return this.type == that.type && this.state == that.state;
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "PathDisplayInfo{type=" + this.type + ", status=" + this.state + "}";
   }
}
