package me.TechsCode.UltraPermissions.storage.objects;

import java.util.Optional;
import me.TechsCode.UltraPermissions.StorageController;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.storage.Stored;
import com.google.gson.JsonElement;

public class Holder {
   private final Stored<User> userHolder;
   private final Stored<Group> groupHolder;

   public static Holder fromUser(User user) {
      return new Holder(user.toStored(), null);
   }

   public static Holder fromGroup(Group group) {
      return new Holder(null, group.toStored());
   }

   public static Holder fromKey(JsonElement jsonElement, TechPlugin<?> techPlugin) {
      StorageController storageController = (StorageController)techPlugin;
      return jsonElement.getAsString().length() > 10
         ? new Holder(new Stored<>(jsonElement, storageController::getUserStorage), null)
         : new Holder(null, new Stored<>(jsonElement, storageController::getGroupStorage));
   }

   private Holder(Stored<User> stored, Stored<Group> groupStored) {
      this.userHolder = stored;
      this.groupHolder = groupStored;
   }

   public boolean isUser() {
      return this.userHolder != null;
   }

   public boolean isGroup() {
      return this.groupHolder != null;
   }

   public Optional<PermissionHolder> get() {
      return this.userHolder != null ? Optional.ofNullable(this.userHolder.get().orElse(null)) : Optional.ofNullable(this.groupHolder.get().orElse(null));
   }

   public String getName() {
      return this.get().map(PermissionHolder::getName).orElse("INVALID");
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof Holder holder) ? false : this.toStored().equals(holder.toStored());
   }

   public Stored<?> toStored() {
      return this.userHolder != null ? this.userHolder : this.groupHolder;
   }
}
