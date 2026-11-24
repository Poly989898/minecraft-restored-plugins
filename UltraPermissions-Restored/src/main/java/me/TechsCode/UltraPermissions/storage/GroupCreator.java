package me.TechsCode.UltraPermissions.storage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.storage.objects.Group;

public class GroupCreator {
   private final GroupStorage storage;
   private final String name;
   private int id;
   private String server;
   private String world;
   private String prefix;
   private String suffix;
   private boolean default_;
   private int priority = 1;
   private XMaterial icon = XMaterial.YELLOW_STAINED_GLASS_PANE;

   public GroupCreator(GroupStorage groupStorage, String text) {
      this.storage = groupStorage;
      this.name = text;
   }

   public GroupCreator setDefault(boolean flag) {
      this.default_ = flag;
      return this;
   }

   public GroupCreator setServer(String text) {
      this.server = text;
      return this;
   }

   public GroupCreator setWorld(String text) {
      this.world = text;
      return this;
   }

   public GroupCreator setPrefix(String text) {
      this.prefix = text;
      return this;
   }

   public GroupCreator setSuffix(String text) {
      this.suffix = text;
      return this;
   }

   public GroupCreator id(int count) {
      this.id = count;
      return this;
   }

   public GroupCreator setPriority(int count) {
      this.priority = count;
      return this;
   }

   public GroupCreator setIcon(XMaterial xMaterial) {
      this.icon = xMaterial;
      return this;
   }

   public Group create() {
      return this.storage
         .create(
            new Group(
               UUID.randomUUID().toString().substring(0, 4),
               this.name,
               this.world,
               this.server,
               this.priority,
               new HashSet<>(),
               this.default_,
               this.prefix,
               this.suffix,
               this.icon.name(),
               new HashMap<>()
            )
         );
   }
}
