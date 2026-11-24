package cc.javajobs.factionsbridge.bridge.infrastructure.struct;

import java.util.List;

public interface Faction {
    Relationship getRelationshipTo(FPlayer other);
    List<FPlayer> getMembers();
}
