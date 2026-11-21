package com.ticxo.modelengine.api.utils.state;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class StateNode<T> {
   private final StateMachine<T> machine;
   private final List<Function<T, StateNode<T>>> forceConnected = new ArrayList<>();
   private final List<Function<T, StateNode<T>>> connected = new ArrayList<>();
   private Consumer<T> entryAction;
   private Consumer<T> action;
   private Consumer<T> exitAction;
   private Predicate<T> commonPredicate;
   private Predicate<T> entryPredicate;

   public void addForceConnectedNode(Predicate<T> condition, StateNode<T> node) {
      this.addForceConnectedNode(condition, t -> node);
   }

   public void addForceConnectedNode(Predicate<T> condition, Function<T, StateNode<T>> node) {
      this.addForceNodeProcessor(t -> {
         if (!condition.test(t)) {
            return null;
         }

         StateNode<T> n = node.apply(t);
         return n.testEntryPredicate(t) ? n : null;
      });
   }

   public void addForceNodeProcessor(Function<T, StateNode<T>> node) {
      this.forceConnected.add(node);
   }

   public void clearForceConnectedNodes() {
      this.forceConnected.clear();
   }

   public void addConnectedNode(Predicate<T> condition, StateNode<T> node) {
      this.addConnectedNode(condition, t -> node);
   }

   public void addConnectedNode(Predicate<T> condition, Function<T, StateNode<T>> node) {
      this.addNodeProcessor(t -> {
         if (!condition.test(t)) {
            return null;
         }

         StateNode<T> n = node.apply(t);
         return n.testEntryPredicate(t) ? n : null;
      });
   }

   public void addNodeProcessor(Function<T, StateNode<T>> node) {
      this.connected.add(node);
   }

   public void clearConnectedNodes() {
      this.connected.clear();
   }

   public void acceptAction(T target) {
      if (this.action != null) {
         this.action.accept(target);
      }
   }

   public void acceptEntry(T target) {
      if (this.entryAction != null) {
         this.entryAction.accept(target);
      }
   }

   public void acceptExit(T target) {
      if (this.exitAction != null) {
         this.exitAction.accept(target);
      }
   }

   public boolean testCommonPredicate(T target) {
      return this.commonPredicate == null || this.commonPredicate.test(target);
   }

   public boolean testEntryPredicate(T target) {
      return this.entryPredicate == null || this.entryPredicate.test(target);
   }
}
