package co.fusionx.relay.internal.base;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import java.util.Collection;
import java.util.LinkedHashSet;

import co.fusionx.relay.base.LibraryUser;
import co.fusionx.relay.base.ServerConfiguration;
import co.fusionx.relay.event.Event;
import co.fusionx.relay.internal.sender.BaseSender;
import co.fusionx.relay.misc.EventBus;

public class RelayLibraryUser extends RelayChannelUser implements LibraryUser {

    private final Collection<RelayQueryUser> mQueryUsers;

    private final EventBus<Event> mConnectionWideEventBus;

    private final ServerConfiguration mConfiguration;

    private final BaseSender mBaseSender;

    public RelayLibraryUser(final String nick, final EventBus<Event> connectionWideEventBus,
            final ServerConfiguration configuration, final BaseSender baseSender) {
        super(nick);

        mConnectionWideEventBus = connectionWideEventBus;
        mConfiguration = configuration;
        mBaseSender = baseSender;

        mQueryUsers = new LinkedHashSet<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<RelayQueryUser> getQueryUsers() {
        return mQueryUsers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<RelayQueryUser> getQueryUser(final String nick) {
        return FluentIterable.from(mQueryUsers)
                .filter(u -> nick.equals(u.getNick().getNickAsString()))
                .first();
    }

    public RelayQueryUser addQueryUser(final String nick) {
        final RelayQueryUser user = new RelayQueryUser(mConnectionWideEventBus, this,
                mConfiguration, mBaseSender, nick);
        mQueryUsers.add(user);
        return user;
    }

    public void removeQueryUser(final RelayQueryUser user) {
        mQueryUsers.remove(user);
        user.markInvalid();
    }
}