package ru.tehkode.permissions;

import java.util.List;

public class PermissionGroup extends PermissionEntity {
    public List<PermissionGroup> getParents() { return null; }
    public boolean isDefault(String world) { return false; }
    public int getRank() { return 0; }
}
