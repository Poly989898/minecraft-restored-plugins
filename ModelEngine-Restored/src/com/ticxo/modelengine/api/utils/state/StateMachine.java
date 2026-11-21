package com.ticxo.modelengine.api.utils.state;

import java.util.function.Function;
import lombok.Getter;

@Getter
public class StateMachine<T> {
   protected StateNode<T> currentNode;

   public void setEntryNode(StateNode<T> entryNode) {
      this.currentNode = entryNode;
   }

   public StateNode<T> createNode() {
      return new StateNode<>(this);
   }

   public void execute(T target) {
      if (this.currentNode == null) {
         this.currentNode = this.getCurrentNode();
      }

      if (this.currentNode != null) {
         boolean updated = false;

         for (Function<T, StateNode<T>> processor : this.currentNode.getForceConnected()) {
            StateNode<T> node = processor.apply(target);
            if (node != null) {
               this.currentNode.acceptExit(target);
               this.currentNode = node;
               this.currentNode.acceptEntry(target);
               updated = true;
               break;
            }
         }

         if (!updated && this.currentNode.testCommonPredicate(target)) {
            for (Function<T, StateNode<T>> processor : this.currentNode.getConnected()) {
               StateNode<T> node = processor.apply(target);
               if (node != null) {
                  this.currentNode.acceptExit(target);
                  this.currentNode = node;
                  this.currentNode.acceptEntry(target);
                  break;
               }
            }
         }

         if (this.currentNode != null) {
            this.currentNode.acceptAction(target);
         }
      }
   }
}
