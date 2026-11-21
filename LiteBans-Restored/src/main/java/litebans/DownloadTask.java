package litebans;

import org.jetbrains.annotations.NotNull;

public final class DownloadTask implements Runnable {
   private final Platform platform;

   public DownloadTask(@NotNull Platform platform) {
      this.platform = platform;
   }

   @Override
   public void run() {
   }
}
