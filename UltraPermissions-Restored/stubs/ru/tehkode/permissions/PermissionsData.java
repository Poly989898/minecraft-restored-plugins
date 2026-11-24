package ru.tehkode.permissions;

import java.util.List;
import java.util.Map;

public interface PermissionsData {
    Map<String, List<String>> getPermissionsMap();
}
