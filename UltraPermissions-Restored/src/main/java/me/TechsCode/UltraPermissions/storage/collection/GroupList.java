package me.TechsCode.UltraPermissions.storage.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.storage.objects.Group;
import me.TechsCode.UltraPermissions.storage.objects.Holder;

public class GroupList extends ArrayList<Group> {
   public GroupList(int count) {
      super(count);
   }

   public GroupList() {
   }

   public GroupList(Collection<? extends Group> groups) {
      super(groups);
   }

   public GroupList servers(boolean flag, List<String> texts) {
      return this.stream().filter(group -> {
         Optional server = group.getServer();
         return flag && !server.isPresent() || server.isPresent() && texts.contains(server.get());
      }).collect(Collectors.toCollection(GroupList::new));
   }

   public GroupList worlds(boolean flag, List<String> texts) {
      return this.stream().filter(group -> {
         Optional world = group.getWorld();
         return flag && !world.isPresent() || world.isPresent() && texts.contains(world.get());
      }).collect(Collectors.toCollection(GroupList::new));
   }

   public GroupList servers(boolean flag, String... texts) {
      return this.servers(flag, Arrays.asList(texts));
   }

   public GroupList worlds(boolean flag, String... texts) {
      return this.worlds(flag, Arrays.asList(texts));
   }

   public Optional<Group> key(String text) {
      return this.stream().filter(group -> group.getKey().equals(text)).findFirst();
   }

   public GroupList worstToBest() {
      this.sort(Comparator.comparing(Group::getPriority, Comparator.reverseOrder()).thenComparing(Group::getKey));
      return this;
   }

   public GroupList bestToWorst() {
      this.sort(Comparator.comparing(Group::getPriority).thenComparing(Group::getKey));
      return this;
   }

   public GroupList defaults(boolean flag) {
      return this.stream().filter(group -> group.isDefault() == flag).collect(Collectors.toCollection(GroupList::new));
   }

   public Optional<Group> name(String text) {
      return this.stream().filter(group -> group.getName().equalsIgnoreCase(text)).findFirst();
   }

   public List<Holder> holders() {
      return this.stream().map(Holder::fromGroup).collect(Collectors.toList());
   }

   public GroupList getWithRecursiveInherits() {
      GroupList list = new GroupList(this);

      for (Group thi : this) {
         this.collectGroups(list, thi);
      }

      return list;
   }

   private void collectGroups(List<Group> groups, Group group) {
      for (Group activeInheritedGroup : group.getActiveInheritedGroups()) {
         if (!groups.contains(activeInheritedGroup)) {
            groups.add(activeInheritedGroup);
            this.collectGroups(groups, activeInheritedGroup);
         }
      }
   }
}
