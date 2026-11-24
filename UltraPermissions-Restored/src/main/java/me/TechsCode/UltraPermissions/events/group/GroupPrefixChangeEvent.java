package me.TechsCode.UltraPermissions.events.group;

import me.TechsCode.UltraPermissions.storage.objects.Group;

public class GroupPrefixChangeEvent extends GroupEvent {
   private final String oldPrefix;
   private final String newPrefix;

   public GroupPrefixChangeEvent(Group group, String text, String newPrefix) {
      super(group);
      this.oldPrefix = text;
      this.newPrefix = newPrefix;
   }

   public String getOldPrefix() {
      return this.oldPrefix;
   }

   public String getNewPrefix() {
      return this.newPrefix;
   }
}
