package litebans;

public enum ClickAction {
   OPEN_URL,
   OPEN_FILE,
   RUN_COMMAND,
   SUGGEST_COMMAND,
   CHANGE_PAGE,
   COPY_TO_CLIPBOARD;

   public static ClickAction computeClickAction(String text) {
      return Enum.valueOf(ClickAction.class, text);
   }

   private static final ClickAction[] obtainClickActionArray() {
      return new ClickAction[]{OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, COPY_TO_CLIPBOARD};
   }
}
