package com.nexomc.nexo.dialog;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\f\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\f¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/dialog/DialogActionTypes;", "", "<init>", "(Ljava/lang/String;I)V", "OPEN_URL", "RUN_COMMAND", "SUGGEST_COMMAND", "CHANGE_PAGE", "CLIPBOARD", "SHOW_DIALOG", "CUSTOM", "DYNAMIC_RUN_COMMAND", "DYNAMIC_CUSTOM", "com.nexomc:core"}
)
public enum DialogActionTypes {
   OPEN_URL,
   RUN_COMMAND,
   SUGGEST_COMMAND,
   CHANGE_PAGE,
   CLIPBOARD,
   SHOW_DIALOG,
   CUSTOM,
   DYNAMIC_RUN_COMMAND,
   DYNAMIC_CUSTOM;

}
