package protocols;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parties.PartyA;
import parties.PartyB;

@Service
public class SecureBranch {
    @Autowired
    private PartyA partyA;

    @Autowired
    private PartyB partyB;

    @Autowired
    private OfflineShuffling offlineShuffling;

    @Autowired
    private OnlineShuffling onlineShuffling;

}
