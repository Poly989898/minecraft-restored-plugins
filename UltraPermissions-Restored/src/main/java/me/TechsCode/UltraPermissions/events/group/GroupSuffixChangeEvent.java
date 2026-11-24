package me.TechsCode.UltraPermissions.events.group;

import me.TechsCode.UltraPermissions.storage.objects.Group;

public class GroupSuffixChangeEvent extends GroupEvent {
   private final String oldPrefix;
   private final String newPrefix;

   public GroupSuffixChangeEvent(Group group, String text, String newSuffix) {
      super(group);
      this.oldPrefix = text;
      this.newPrefix = newSuffix;
   }

   public String getOldPrefix() {
      return this.oldPrefix;
   }

   public String getNewPrefix() {
      return this.newPrefix;
   }
}
