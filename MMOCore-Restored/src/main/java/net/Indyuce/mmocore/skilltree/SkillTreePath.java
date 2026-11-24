package net.Indyuce.mmocore.skilltree;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.skilltree.tree.SkillTree;

public class SkillTreePath {
   private final SkillTree tree;
   private final IntegerCoordinates coordinates;
   private final SkillTreeNode from;
   private final SkillTreeNode to;

   public SkillTreePath(SkillTree tree, IntegerCoordinates coordinates, SkillTreeNode from, SkillTreeNode skillTreeNode) {
      this.tree = tree;
      this.coordinates = coordinates;
      this.from = from;
      this.to = skillTreeNode;
   }

   public NodeState getStatus(PlayerData playerData) {
      NodeState from = playerData.getNodeState(this.from);
      NodeState to = playerData.getNodeState(this.to);
      if (from == NodeState.FULLY_LOCKED || to == NodeState.FULLY_LOCKED) {
         return NodeState.FULLY_LOCKED;
      } else if (from.isUnlocked() && to.isUnlocked()) {
         return NodeState.UNLOCKED;
      } else {
         return (from != NodeState.UNLOCKABLE || !to.isUnlocked()) && (!from.isUnlocked() || to != NodeState.UNLOCKABLE)
            ? NodeState.LOCKED
            : NodeState.UNLOCKABLE;
      }
   }

   public PathType getPathType() {
      IntegerCoordinates upCoor = new IntegerCoordinates(this.coordinates.getX(), this.coordinates.getY() - 1);
      IntegerCoordinates downCoor = new IntegerCoordinates(this.coordinates.getX(), this.coordinates.getY() + 1);
      IntegerCoordinates rightCoor = new IntegerCoordinates(this.coordinates.getX() + 1, this.coordinates.getY());
      IntegerCoordinates leftCoor = new IntegerCoordinates(this.coordinates.getX() - 1, this.coordinates.getY());
      boolean hasUp = this.tree.isPath(upCoor) || upCoor.equals(this.from.getCoordinates()) || upCoor.equals(this.to.getCoordinates());
      boolean hasDown = this.tree.isPath(downCoor) || downCoor.equals(this.from.getCoordinates()) || downCoor.equals(this.to.getCoordinates());
      boolean hasRight = this.tree.isPath(rightCoor) || rightCoor.equals(this.from.getCoordinates()) || rightCoor.equals(this.to.getCoordinates());
      boolean hasLeft = this.tree.isPath(leftCoor) || leftCoor.equals(this.from.getCoordinates()) || leftCoor.equals(this.to.getCoordinates());
      if ((hasUp || hasDown) && !hasLeft && !hasRight) {
         return PathType.UP;
      } else if ((hasRight || hasLeft) && !hasUp && !hasDown) {
         return PathType.RIGHT;
      } else if (hasUp && hasRight) {
         return PathType.UP_RIGHT;
      } else if (hasUp && hasLeft) {
         return PathType.UP_LEFT;
      } else if (hasDown && hasRight) {
         return PathType.DOWN_RIGHT;
      } else {
         return hasDown && hasLeft ? PathType.DOWN_LEFT : PathType.DEFAULT;
      }
   }
}
