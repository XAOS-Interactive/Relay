package co.fusionx.relay.event.server;

import co.fusionx.relay.conversation.Server;

public class StopEvent extends StatusChangeEvent {

    public StopEvent(final Server server) {
        super(server);
    }
}