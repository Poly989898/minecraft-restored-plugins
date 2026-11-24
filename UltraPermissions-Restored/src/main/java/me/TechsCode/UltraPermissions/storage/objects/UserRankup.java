package me.TechsCode.UltraPermissions.storage.objects;

import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.storage.Stored;

public class UserRankup {
   private final User user;
   private final Stored<Group> group;
   private final long expiry;

   public UserRankup(User user, Stored<Group> stored, long value) {
      this.user = user;
      this.group = stored;
      this.expiry = value;
   }

   public User getUser() {
      return this.user;
   }

   public String getGroupName() {
      return this.group.get().map(Group::getName).orElse("INVALID");
   }

   public XMaterial getIcon() {
      return this.group.get().map(Group::getIcon).orElse(XMaterial.STONE);
   }

   public int getModelId() {
      return this.group.get().map(Group::getModelId).orElse(0);
   }

   public int getDamage() {
      return this.group.get().map(Group::getDamage).orElse(0);
   }

   public Stored<Group> getGroup() {
      return this.group;
   }

   public long getExpiry() {
      return this.expiry;
   }

   public long getLeftDuration() {
      return this.expiry - System.currentTimeMillis();
   }

   public boolean isPermanent() {
      return this.expiry == 0L;
   }

   public boolean isTemporary() {
      return this.expiry != 0L;
   }

   public boolean isExpired() {
      return !this.isPermanent() && this.getLeftDuration() < 0L;
   }

   public void remove() {
      this.group.get().ifPresent(this.user::removeGroup);
   }
}
