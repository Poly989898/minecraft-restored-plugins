package com.alessiodp.parties.api.interfaces;

import java.util.Set;

public interface Party {
    Set<java.util.UUID> getMembers();
    Set<PartyPlayer> getOnlineMembers();
}
