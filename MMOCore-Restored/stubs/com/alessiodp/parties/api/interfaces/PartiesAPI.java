package com.alessiodp.parties.api.interfaces;

import java.util.UUID;

public interface PartiesAPI {
    PartyPlayer getPartyPlayer(UUID uuid);
    Party getParty(UUID partyId);
}
