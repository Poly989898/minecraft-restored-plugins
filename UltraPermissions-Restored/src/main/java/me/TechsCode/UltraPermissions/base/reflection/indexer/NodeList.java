package me.TechsCode.UltraPermissions.base.reflection.indexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class NodeList extends ArrayList<Node> {
   public NodeList(int count) {
      super(count);
   }

   public NodeList() {
   }

   public NodeList(Collection<? extends Node> nodes) {
      super(nodes);
   }

   public NodeList classes() {
      return this.stream().filter(Node::isClass).collect(Collectors.toCollection(NodeList::new));
   }
}
