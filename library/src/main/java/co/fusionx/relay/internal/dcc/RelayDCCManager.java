package co.fusionx.relay.internal.dcc;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import co.fusionx.relay.dcc.DCCManager;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.dcc.chat.DCCChatConversation;
import co.fusionx.relay.dcc.event.file.DCCFileConversationStartedEvent;
import co.fusionx.relay.dcc.file.DCCFileConversation;
import co.fusionx.relay.dcc.pending.DCCPendingChatConnection;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.dcc.pending.DCCPendingSendConnection;
import co.fusionx.relay.internal.sender.BaseSender;

import static co.fusionx.relay.misc.RelayConfigurationProvider.getPreferences;

public class RelayDCCManager implements DCCManager {

    private final Map<String, DCCChatConversation> mChatConversations;

    private final Map<String, DCCFileConversation> mFileConversations;

    private final Set<DCCPendingConnection> mPendingConnections;

    private final RelayServer mServer;

    private final BaseSender mBaseSender;

    public RelayDCCManager(final RelayServer relayServer, final BaseSender baseSender) {
        mServer = relayServer;
        mBaseSender = baseSender;

        mChatConversations = new HashMap<>();
        mFileConversations = new HashMap<>();
        mPendingConnections = new HashSet<>();
    }

    public void addPendingConnection(final DCCPendingConnection pendingConnection) {
        mPendingConnections.add(pendingConnection);
    }

    @Override
    public Collection<DCCChatConversation> getChatConversations() {
        return ImmutableSet.copyOf(mChatConversations.values());
    }

    @Override
    public Collection<DCCFileConversation> getFileConversations() {
        return ImmutableSet.copyOf(mFileConversations.values());
    }

    @Override
    public Collection<DCCPendingConnection> getPendingConnections() {
        return ImmutableSet.copyOf(mPendingConnections);
    }

    public void acceptDCCConnection(final DCCPendingSendConnection connection, final File file) {
        if (!mPendingConnections.contains(connection)) {
            // TODO - Maybe send an event instead?
            getPreferences().logServerLine("DCC Connection not managed by this server");
            return;
        }
        // This chat is no longer pending - remove it
        mPendingConnections.remove(connection);

        // Check if we have an existing conversation
        final Optional<DCCFileConversation> optConversation = FluentIterable
                .from(mFileConversations.values())
                .filter(f -> f.getId().equals(connection.getDccRequestNick()))
                .first();
        // Get the conversation or a new one if it does not exist
        final DCCFileConversation conversation = optConversation
                .or(new DCCFileConversation(mServer, mBaseSender, connection.getDccRequestNick()));
        // If the conversation was not present add it
        if (!optConversation.isPresent()) {
            mFileConversations.put(connection.getDccRequestNick(), conversation);
        }
        // A pending send becomes a get here
        conversation.getFile(connection, file);

        // The conversation has been started
        mServer.getServerWideBus().post(new DCCFileConversationStartedEvent(conversation));
    }

    public void acceptDCCConnection(final DCCPendingChatConnection connection) {
        if (!mPendingConnections.contains(connection)) {
            // TODO - Maybe send an event instead?
            getPreferences().logServerLine("DCC Connection not managed by this server");
            return;
        }
        // This chat is no longer pending - remove it
        mPendingConnections.remove(connection);

        final DCCChatConversation conversation = new DCCChatConversation(mServer, connection);
        mChatConversations.put(connection.getDccRequestNick(), conversation);
        conversation.startChat();
    }

    public void declineDCCConnection(final DCCPendingConnection connection) {
        mPendingConnections.remove(connection);
    }

    public DCCFileConversation getFileConversation(final String nick) {
        return mFileConversations.get(nick);
    }
}