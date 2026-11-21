package litebans;

public enum GameVersion {
   V1_16,
   V1_21_5;

   public static GameVersion computeGameVersion(String text) {
      return Enum.valueOf(GameVersion.class, text);
   }

   private static final GameVersion[] obtainGameVersionArray() {
      return new GameVersion[]{V1_16, V1_21_5};
   }
}
