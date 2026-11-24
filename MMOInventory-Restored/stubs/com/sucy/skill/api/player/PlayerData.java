package com.sucy.skill.api.player;
public class PlayerData {
    public boolean hasClass() { return false; }
    public PlayerClass getMainClass() { return null; }
    public static class PlayerClass { public int getLevel() { return 0; } public ClassData getData() { return null; } }
    public static class ClassData { public String getName() { return null; } }
}
