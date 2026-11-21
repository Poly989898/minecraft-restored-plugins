package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LiteAInterface {
   LiteHelper11 liteHelper = LiteHelper11.LITE_HELPER;
   boolean flag = false;
   boolean flag2 = true;

   boolean checkUserRecord(@Nullable UserRecord userRecord, @NotNull SyncMessageType syncMessageType, @NotNull String text);
}
