package co.fusionx.relay.parser.command;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.List;

import co.fusionx.relay.ChannelUser;
import co.fusionx.relay.RelayChannel;
import co.fusionx.relay.RelayChannelUser;
import co.fusionx.relay.RelayServer;
import co.fusionx.relay.event.channel.ChannelWorldKickEvent;
import co.fusionx.relay.event.channel.ChannelWorldUserEvent;
import co.fusionx.relay.event.server.KickEvent;
import co.fusionx.relay.util.IRCUtils;

class KickParser extends RemoveUserParser {

    public KickParser(RelayServer server) {
        super(server);
    }

    /**
     * Called when a user who is not our user is kicked from the channel
     *
     * @param parsedArray the raw line which is split
     * @param rawSource   the the source of the person who kicked the other user - unused in this
     *                    method
     * @return the WorldUser object associated with the nick
     */
    @Override
    public Optional<RelayChannelUser> getRemovedUser(final List<String> parsedArray,
            final String rawSource) {
        final String kickedNick = parsedArray.get(3);
        return mUserChannelInterface.getUser(kickedNick);
    }

    @Override
    public ChannelWorldUserEvent getEvent(final List<String> parsedArray,
            final String rawSource, final RelayChannel channel, final ChannelUser kickedUser) {
        final String kickingNick = IRCUtils.getNickFromRaw(rawSource);
        final Optional<RelayChannelUser> optKickUser = mUserChannelInterface.getUser(kickingNick);
        final String reason = parsedArray.size() == 5 ? parsedArray.get(4).replace("\"", "") : "";

        return new ChannelWorldKickEvent(channel, kickedUser, optKickUser, kickingNick, reason);
    }

    /**
     * Method called when the user is kicked from the channel
     *
     * @param parsedArray the raw line which is split
     * @param rawSource   the source of the person who kicked us
     * @param channel     the channel we were kicked from
     */
    @Override
    void onRemoved(final List<String> parsedArray, final String rawSource,
            final RelayChannel channel) {
        final String kickingNick = IRCUtils.getNickFromRaw(rawSource);
        final Optional<RelayChannelUser> optKickUser = mUserChannelInterface.getUser(kickingNick);

        // Remove the channel only after we've finished with it
        final Collection<RelayChannelUser> users = mUserChannelInterface.removeChannel(channel);
        for (final RelayChannelUser user : users) {
            mUserChannelInterface.removeChannelFromUser(channel, user);
        }

        final String reason = parsedArray.size() == 5 ? parsedArray.get(4).replace("\"", "") : "";
        final KickEvent event = new KickEvent(channel, optKickUser, kickingNick, reason);

        mServerEventBus.postAndStoreEvent(event);
    }
}