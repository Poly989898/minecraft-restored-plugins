package net.Indyuce.mmocore.skilltree;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class ParentInformation {
   private final SkillTreeNode node;
   private final ParentType type;
   private final int level;

   public ParentInformation(SkillTreeNode node, ParentType type, int level) {
      this.node = node;
      this.type = type;
      this.level = level;
   }

   @NotNull
   public SkillTreeNode getNode() {
      return this.node;
   }

   @NotNull
   public ParentType getType() {
      return this.type;
   }

   public int getLevel() {
      return this.level;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ParentInformation that = (ParentInformation)o;
         return Objects.equals(this.node, that.node) && this.type == that.type;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.node, this.type);
   }
}
