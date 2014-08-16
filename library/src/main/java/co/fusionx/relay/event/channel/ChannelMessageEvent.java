package co.fusionx.relay.event.channel;

import co.fusionx.relay.Channel;
import co.fusionx.relay.RelayMainUser;

public class ChannelMessageEvent extends ChannelEvent {

    public final String message;

    public final RelayMainUser user;

    public ChannelMessageEvent(final Channel channel, final String message,
            final RelayMainUser user) {
        super(channel);

        this.message = message;
        this.user = user;
    }
}