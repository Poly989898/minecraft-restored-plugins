package ru.tehkode.permissions;

public abstract class PermissionEntity {
    public String getName() { return null; }
    public String getOwnPrefix() { return null; }
    public String getOwnSuffix() { return null; }
    protected PermissionsData getData() { return null; }
}
