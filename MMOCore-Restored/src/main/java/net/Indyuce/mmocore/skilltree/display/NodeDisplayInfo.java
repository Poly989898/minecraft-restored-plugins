package net.Indyuce.mmocore.skilltree.display;

import java.util.Objects;
import net.Indyuce.mmocore.skilltree.NodeState;
import net.Indyuce.mmocore.skilltree.NodeType;
import org.jetbrains.annotations.NotNull;

public class NodeDisplayInfo extends DisplayInfo {
   private final NodeType type;

   public NodeDisplayInfo(@NotNull NodeType type, @NotNull NodeState status) {
      super(status);
      this.type = type;
   }

   public NodeType getType() {
      return this.type;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         NodeDisplayInfo that = (NodeDisplayInfo)o;
         return this.state == that.state && this.type == that.type;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.state, this.type);
   }

   @Override
   public String toString() {
      return "NodeDisplayInfo{status=" + this.state + ", type=" + this.type + "}";
   }
}
