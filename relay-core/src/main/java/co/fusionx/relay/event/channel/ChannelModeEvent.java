package co.fusionx.relay.event.channel;

import com.google.common.base.Optional;

import co.fusionx.relay.conversation.Channel;
import co.fusionx.relay.core.ChannelUser;

public class ChannelModeEvent extends ChannelEvent {

    public final Optional<? extends ChannelUser> sendingUser;

    public final String sendingNick;

    public final String mode;

    public ChannelModeEvent(final Channel channel,
            final Optional<? extends ChannelUser> sendingUser, final String sendingNick,
            final String mode) {
        super(channel);

        this.sendingUser = sendingUser;
        this.sendingNick = sendingNick;
        this.mode = mode;
    }
}