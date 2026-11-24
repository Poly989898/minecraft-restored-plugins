package me.TechsCode.UltraPermissions.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import me.TechsCode.UltraPermissions.utility.ChildrenResolver;

public class Node {
   private final String permission;

   public Node(String text) {
      this.permission = text;
   }

   public List<String> getWildcards() {
      ArrayList list = new ArrayList<>(Collections.singletonList("*"));
      StringBuilder builder = new StringBuilder();

      for (String text : this.permission.split("\\.")) {
         if (!builder.toString().equals("")) {
            builder.append(".");
         }

         builder.append(text);
         list.add(builder + ".*");
      }

      return list;
   }

   public List<String> getSelfAndWildcards() {
      ArrayList list = new ArrayList();
      list.add(this.permission);
      list.addAll(this.getWildcards());
      return list;
   }

   public Set<String> getSelfAndContainedPermissions() {
      return ChildrenResolver.retrieveChildPermissions(this.permission);
   }
}
