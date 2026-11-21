package io.lumine.mythic.bukkit.utils.tasks.threadlock;

import io.lumine.mythic.bukkit.utils.terminable.Terminable;

public interface ServerThreadLock extends Terminable {
   static ServerThreadLock obtain() {
      return new ServerThreadLockImpl();
   }

   @Override
   void close();
}
