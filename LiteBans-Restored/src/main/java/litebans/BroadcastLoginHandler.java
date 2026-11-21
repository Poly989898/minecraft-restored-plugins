package litebans;

import org.jetbrains.annotations.NotNull;

public final class BroadcastLoginHandler extends LoginHandler {
   public BroadcastLoginHandler(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public String getServerName(@NotNull Object obj) {
      CommandTask commandTask = this;
      return ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).getServerName();
   }
}
