package io.lumine.mythic.core.skills.conditions;

import com.google.common.collect.Sets;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.spawning.random.RandomSpawnPoint;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;
import io.lumine.mythic.core.utils.MythicUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import lombok.Generated;

public class CompositeCondition extends SkillCondition {
   private CompositeCondition.TreeNode root;
   private SkillExecutor manager;

   public CompositeCondition(SkillExecutor manager, String line) {
      super(line);
      this.manager = manager;
      line = line.trim();
      List<String> splitList = MythicUtil.splitIgnoringParentheses(line);
      this.root = this.buildTree(splitList.get(0));
   }

   private CompositeCondition.TreeNode buildTree(String input) {
      if (input != null && !input.isEmpty()) {
         input = this.removeOuterParentheses(input);
         int depth = 0;
         List<String> parts = new ArrayList<>();
         List<Character> operators = new ArrayList<>();
         int lastSplit = -1;

         for (int i = 0; i < input.length() - 1; i++) {
            char c = input.charAt(i);
            char nextC = input.charAt(i + 1);
            if (c == '(') {
               depth++;
            } else if (c == ')') {
               depth--;
            } else if ((c == '&' && nextC == '&' || c == '|' && nextC == '|') && depth == 0) {
               parts.add(input.substring(lastSplit + 1, i).trim());
               operators.add(c);
               lastSplit = i + 1;
               i++;
            }
         }

         parts.add(input.substring(lastSplit + 1).trim());
         if (parts.size() == 1) {
            return new CompositeCondition.ConditionNode(this.manager.getCondition(parts.get(0)));
         } else {
            CompositeCondition.TreeNode rootNode = null;
            CompositeCondition.TreeNode currentNode = null;

            for (int ix = 0; ix < parts.size() - 1; ix++) {
               String leftPart = parts.get(ix);
               String rightPart = parts.get(ix + 1);
               char operator = operators.get(ix);
               CompositeCondition.TreeNode left = this.buildTree(leftPart);
               CompositeCondition.TreeNode right = this.buildTree(rightPart);
               CompositeCondition.OperatorNode operatorNode = new CompositeCondition.OperatorNode(
                  operator == '&' ? CompositeCondition.Operator.AND : CompositeCondition.Operator.OR
               );
               operatorNode.setLeft(left);
               operatorNode.setRight(right);
               if (rootNode == null) {
                  rootNode = operatorNode;
               } else {
                  currentNode.setRight(operatorNode);
               }

               currentNode = operatorNode;
            }

            return rootNode;
         }
      } else {
         return null;
      }
   }

   private String removeOuterParentheses(String input) {
      int openParentheses = 0;
      int closeParentheses = 0;
      int startIndex = -1;
      int endIndex = -1;

      for (int i = 0; i < input.length(); i++) {
         char c = input.charAt(i);
         if (c == '(') {
            openParentheses++;
            if (startIndex == -1) {
               startIndex = i;
            }
         } else if (c == ')') {
            if (++closeParentheses == openParentheses) {
               endIndex = i;
               break;
            }
         }
      }

      return startIndex == 0 && endIndex == input.length() - 1 ? input.substring(1, input.length() - 1) : input;
   }

   @Override
   public boolean evaluateEntity(AbstractEntity target) {
      return this.handleOutcome(this.root.evaluateEntity(target));
   }

   @Override
   public boolean evaluateToEntity(SkillMetadata data, AbstractEntity t) {
      return this.handleOutcome(this.root.evaluateToEntity(data, t));
   }

   @Override
   public boolean evaluateToEntity(AbstractEntity base, AbstractEntity t) {
      return this.handleOutcome(this.root.evaluateToEntity(base, t));
   }

   @Override
   public boolean evaluateToLocation(AbstractEntity base, AbstractLocation location) {
      return this.handleOutcome(this.root.evaluateToLocation(base, location));
   }

   @Override
   public boolean evaluateCaster(SkillMetadata meta) {
      return this.handleOutcome(meta, this.root.evaluateCaster(meta));
   }

   @Override
   public boolean evaluateTrigger(SkillMetadata meta) {
      return this.handleOutcome(meta, this.root.evaluateTrigger(meta));
   }

   @Override
   public boolean evaluateTargets(SkillMetadata meta) {
      if (meta.getEntityTargets() != null && meta.getEntityTargets().size() > 0) {
         HashSet<AbstractEntity> targets = Sets.newHashSet(meta.getEntityTargets());
         targets.removeIf(
            target -> target != null && !target.isDead() && target.isValid() ? !this.handleOutcome(meta, this.root.evaluateToEntity(meta, target)) : true
         );
         meta.setEntityTargets(targets);
         if (targets.size() == 0) {
            return false;
         }
      } else {
         if (meta.getLocationTargets() == null || meta.getLocationTargets().size() <= 0) {
            return false;
         }

         HashSet<AbstractLocation> targets = Sets.newHashSet(meta.getLocationTargets());
         targets.removeIf(target -> !this.handleOutcome(meta, this.root.evaluateToLocation(meta.getCaster().getEntity(), target)));
         meta.setLocationTargets(targets);
         if (targets.size() == 0) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean evaluateRandomSpawnPoint(RandomSpawnPoint rsp) {
      return this.handleOutcome(rsp, this.root.evaluateRandomSpawnPoint(rsp));
   }

   @Override
   public boolean evaluateSpawner(MythicSpawner spawner, AbstractLocation spawnLocation) {
      return this.handleOutcome(spawner, this.root.evaluateSpawner(spawner, spawnLocation));
   }

   @Override
   public boolean evaluateDropper(DropMetadata meta) {
      return this.handleOutcome(meta, this.root.evaluateDropper(meta));
   }

   @Override
   public boolean evaluateDropCause(DropMetadata meta) {
      return this.handleOutcome(meta, this.root.evaluateDropCause(meta));
   }

   public void printTree() {
      this.printTreeHelper(this.root, 0);
   }

   private void printTreeHelper(CompositeCondition.TreeNode node, int depth) {
      if (node != null) {
         String indentation = String.join("", Collections.nCopies(depth * 2, " "));
         if (node instanceof CompositeCondition.ConditionNode conditionNode) {
            System.out.println(indentation + "Condition: " + conditionNode.getCondition());
         } else if (node instanceof CompositeCondition.OperatorNode operatorNode) {
            System.out.println(indentation + "Operator: " + operatorNode.getOperator());
         }

         this.printTreeHelper(node.getLeft(), depth + 1);
         this.printTreeHelper(node.getRight(), depth + 1);
      }
   }

   private static class ConditionNode extends CompositeCondition.TreeNode {
      private final SkillCondition condition;

      public ConditionNode(SkillCondition condition) {
         this.condition = condition;
      }

      @Override
      public boolean evaluateEntity(AbstractEntity t) {
         return this.condition.evaluateEntity(t);
      }

      @Override
      public boolean evaluateToEntity(SkillMetadata data, AbstractEntity t) {
         return this.condition.evaluateToEntity(data, t);
      }

      @Override
      public boolean evaluateToEntity(AbstractEntity base, AbstractEntity t) {
         return this.condition.evaluateToEntity(base, t);
      }

      @Override
      public boolean evaluateToLocation(AbstractEntity base, AbstractLocation location) {
         return this.condition.evaluateToLocation(base, location);
      }

      @Override
      public boolean evaluateCaster(SkillMetadata meta) {
         return this.condition.evaluateCaster(meta);
      }

      @Override
      public boolean evaluateTrigger(SkillMetadata meta) {
         return this.condition.evaluateTrigger(meta);
      }

      @Override
      public boolean evaluateTargets(SkillMetadata meta) {
         return this.condition.evaluateTargets(meta);
      }

      @Override
      public boolean evaluateRandomSpawnPoint(RandomSpawnPoint rsp) {
         return this.condition.evaluateRandomSpawnPoint(rsp);
      }

      @Override
      public boolean evaluateSpawner(MythicSpawner spawner, AbstractLocation spawnLocation) {
         return this.condition.evaluateSpawner(spawner, spawnLocation);
      }

      @Override
      public boolean evaluateDropper(DropMetadata meta) {
         return this.condition.evaluateDropper(meta);
      }

      @Override
      public boolean evaluateDropCause(DropMetadata meta) {
         return this.condition.evaluateDropCause(meta);
      }

      @Generated
      public SkillCondition getCondition() {
         return this.condition;
      }
   }

   protected static enum Operator {
      AND,
      OR;
   }

   private static class OperatorNode extends CompositeCondition.TreeNode {
      private final CompositeCondition.Operator operator;

      public OperatorNode(CompositeCondition.Operator operator) {
         this.operator = operator;
      }

      @Override
      public boolean evaluateEntity(AbstractEntity t) {
         return this.operator == CompositeCondition.Operator.AND
            ? this.left.evaluateEntity(t) && this.right.evaluateEntity(t)
            : this.left.evaluateEntity(t) || this.right.evaluateEntity(t);
      }

      @Override
      public boolean evaluateToEntity(SkillMetadata data, AbstractEntity t) {
         return this.operator == CompositeCondition.Operator.AND
            ? this.left.evaluateToEntity(data, t) && this.right.evaluateToEntity(data, t)
            : this.left.evaluateToEntity(data, t) || this.right.evaluateToEntity(data, t);
      }

      @Override
      public boolean evaluateToEntity(AbstractEntity base, AbstractEntity t) {
         return this.operator == CompositeCondition.Operator.AND
            ? this.left.evaluateToEntity(base, t) && this.right.evaluateToEntity(base, t)
            : this.left.evaluateToEntity(base, t) || this.right.evaluateToEntity(base, t);
      }

      @Override
      public boolean evaluateToLocation(AbstractEntity base, AbstractLocation location) {
         return this.operator == CompositeCondition.Operator.AND
            ? this.left.evaluateToLocation(base, location) && this.right.evaluateToLocation(base, location)
            : this.left.evaluateToLocation(base, location) || this.right.evaluateToLocation(base, location);
      }

      @Override
      public boolean evaluateCaster(SkillMetadata meta) {
         return this.operator == CompositeCondition.Operator.AND
            ? this.left.evaluateCaster(meta) && this.right.evaluateCaster(meta)
            : this.left.evaluateCaster(meta) || this.right.evaluateCaster(meta);
      }

      @Override
      public boolean evaluateTrigger(SkillMetadata meta) {
         return this.operator == CompositeCondition.Operator.AND
            ? this.left.evaluateTrigger(meta) && this.right.evaluateTrigger(meta)
            : this.left.evaluateTrigger(meta) || this.right.evaluateTrigger(meta);
      }

      @Override
      public boolean evaluateTargets(SkillMetadata meta) {
         return this.operator == CompositeCondition.Operator.AND
            ? this.left.evaluateTargets(meta) && this.right.evaluateTargets(meta)
            : this.left.evaluateTargets(meta) || this.right.evaluateTargets(meta);
      }

      @Override
      public boolean evaluateRandomSpawnPoint(RandomSpawnPoint rsp) {
         return this.operator == CompositeCondition.Operator.AND
            ? this.left.evaluateRandomSpawnPoint(rsp) && this.right.evaluateRandomSpawnPoint(rsp)
            : this.left.evaluateRandomSpawnPoint(rsp) || this.right.evaluateRandomSpawnPoint(rsp);
      }

      @Override
      public boolean evaluateSpawner(MythicSpawner spawner, AbstractLocation spawnLocation) {
         return this.operator == CompositeCondition.Operator.AND
            ? this.left.evaluateSpawner(spawner, spawnLocation) && this.right.evaluateSpawner(spawner, spawnLocation)
            : this.left.evaluateSpawner(spawner, spawnLocation) || this.right.evaluateSpawner(spawner, spawnLocation);
      }

      @Override
      public boolean evaluateDropper(DropMetadata meta) {
         return this.operator == CompositeCondition.Operator.AND
            ? this.left.evaluateDropper(meta) && this.right.evaluateDropper(meta)
            : this.left.evaluateDropper(meta) || this.right.evaluateDropper(meta);
      }

      @Override
      public boolean evaluateDropCause(DropMetadata meta) {
         return this.operator == CompositeCondition.Operator.AND
            ? this.left.evaluateDropCause(meta) && this.right.evaluateDropCause(meta)
            : this.left.evaluateDropCause(meta) || this.right.evaluateDropCause(meta);
      }

      @Generated
      public CompositeCondition.Operator getOperator() {
         return this.operator;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof CompositeCondition.OperatorNode other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$operator = this.getOperator();
            Object other$operator = other.getOperator();
            return this$operator == null ? other$operator == null : this$operator.equals(other$operator);
         }
      }

      @Generated
      @Override
      protected boolean canEqual(Object other) {
         return other instanceof CompositeCondition.OperatorNode;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $operator = this.getOperator();
         return result * 59 + ($operator == null ? 43 : $operator.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "CompositeCondition.OperatorNode(operator=" + this.getOperator() + ")";
      }
   }

   private abstract static class TreeNode {
      protected CompositeCondition.TreeNode left;
      protected CompositeCondition.TreeNode right;

      public abstract boolean evaluateEntity(AbstractEntity abstractEntity);

      public abstract boolean evaluateToEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity);

      public abstract boolean evaluateToEntity(AbstractEntity abstractEntity, AbstractEntity abstractEntity2);

      public abstract boolean evaluateToLocation(AbstractEntity abstractEntity, AbstractLocation abstractLocation);

      public abstract boolean evaluateCaster(SkillMetadata skillMetadata);

      public abstract boolean evaluateTrigger(SkillMetadata skillMetadata);

      public abstract boolean evaluateTargets(SkillMetadata skillMetadata);

      public abstract boolean evaluateRandomSpawnPoint(RandomSpawnPoint randomSpawnPoint);

      public abstract boolean evaluateSpawner(MythicSpawner mythicSpawner, AbstractLocation abstractLocation);

      public abstract boolean evaluateDropper(DropMetadata dropMetadata);

      public abstract boolean evaluateDropCause(DropMetadata dropMetadata);

      @Generated
      public TreeNode() {
      }

      @Generated
      public CompositeCondition.TreeNode getLeft() {
         return this.left;
      }

      @Generated
      public CompositeCondition.TreeNode getRight() {
         return this.right;
      }

      @Generated
      public void setLeft(CompositeCondition.TreeNode left) {
         this.left = left;
      }

      @Generated
      public void setRight(CompositeCondition.TreeNode right) {
         this.right = right;
      }

      @Generated
      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof CompositeCondition.TreeNode other)) {
            return false;
         } else if (!other.canEqual(this)) {
            return false;
         } else {
            Object this$left = this.getLeft();
            Object other$left = other.getLeft();
            if (this$left == null ? other$left == null : this$left.equals(other$left)) {
               Object this$right = this.getRight();
               Object other$right = other.getRight();
               return this$right == null ? other$right == null : this$right.equals(other$right);
            } else {
               return false;
            }
         }
      }

      @Generated
      protected boolean canEqual(Object other) {
         return other instanceof CompositeCondition.TreeNode;
      }

      @Generated
      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         Object $left = this.getLeft();
         result = result * 59 + ($left == null ? 43 : $left.hashCode());
         Object $right = this.getRight();
         return result * 59 + ($right == null ? 43 : $right.hashCode());
      }

      @Generated
      @Override
      public String toString() {
         return "CompositeCondition.TreeNode(left=" + this.getLeft() + ", right=" + this.getRight() + ")";
      }
   }
}
