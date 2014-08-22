package co.fusionx.relay.parser.main.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.base.relay.RelayChannel;
import co.fusionx.relay.base.relay.RelayChannelUser;
import co.fusionx.relay.base.relay.RelayQueryUser;
import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.base.relay.RelayUserChannelInterface;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelWorldActionEvent;
import co.fusionx.relay.event.query.QueryActionWorldEvent;
import co.fusionx.relay.event.server.NewPrivateMessageEvent;
import co.fusionx.relay.event.server.VersionEvent;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.parser.main.MentionParser;
import co.fusionx.relay.sender.relay.RelayCtcpResponseSender;
import co.fusionx.relay.util.IRCUtils;
import co.fusionx.relay.util.LogUtils;

public class CTCPParser {

    private final RelayServer mServer;

    private final DCCParser mDCCParser;

    private final RelayUserChannelInterface mUserChannelInterface;

    private final RelayCtcpResponseSender mCtcpResponseSender;

    public CTCPParser(final RelayServer server, final DCCParser dccParser) {
        mServer = server;
        mDCCParser = dccParser;

        mUserChannelInterface = server.getUserChannelInterface();

        mCtcpResponseSender = new RelayCtcpResponseSender(server.getRelayPacketSender());
    }

    public static boolean isCtcp(final String message) {
        return message.startsWith("\u0001") && message.endsWith("\u0001");
    }

    // Commands start here
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String normalMessage = parsedArray.get(3);
        final String message = normalMessage.substring(1, normalMessage.length() - 1);

        final String nick = IRCUtils.getNickFromRaw(rawSource);
        // TODO - THIS IS INCOMPLETE
        if (message.startsWith("ACTION")) {
            onAction(parsedArray, rawSource);
        } else if (message.startsWith("FINGER")) {
            mCtcpResponseSender.sendFingerResponse(nick,
                    getServer().getConfiguration().getRealName());
        } else if (message.startsWith("VERSION")) {
            mCtcpResponseSender.sendVersionResponse(nick);
        } else if (message.startsWith("SOURCE")) {
        } else if (message.startsWith("USERINFO")) {
        } else if (message.startsWith("ERRMSG")) {
            final String query = message.replace("ERRMSG ", "");
            mCtcpResponseSender.sendErrMsgResponse(nick, query);
        } else if (message.startsWith("PING")) {
            final String timestamp = message.replace("PING ", "");
            mCtcpResponseSender.sendPingResponse(nick, timestamp);
        } else if (message.startsWith("TIME")) {
            mCtcpResponseSender.sendTimeResponse(nick);
        } else if (message.startsWith("DCC")) {
            final List<String> parsedDcc = IRCUtils.splitRawLineWithQuote(message);
            mDCCParser.onParseCommand(parsedDcc, rawSource);
        }
    }

    private void onAction(final List<String> parsedArray, final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        final String action = parsedArray.get(3).replace("ACTION ", "");
        final String recipient = parsedArray.get(2);
        if (RelayChannel.isChannelPrefix(recipient.charAt(0))) {
            onParseChannelAction(recipient, nick, action);
        } else {
            onParseUserAction(nick, action);
        }
    }

    private void onParseUserAction(final String nick, final String action) {
        final Optional<RelayQueryUser> optional = mUserChannelInterface.getQueryUser(nick);
        final RelayQueryUser user = optional.or(mUserChannelInterface.addQueryUser(nick));
        if (!optional.isPresent()) {
            mServer.postAndStoreEvent(new NewPrivateMessageEvent(user));
        }
        user.postAndStoreEvent(new QueryActionWorldEvent(user, action));
    }

    private void onParseChannelAction(final String channelName, final String sendingNick,
            final String action) {
        final Optional<RelayChannel> optChannel = mUserChannelInterface.getChannel(channelName);

        LogUtils.logOptionalBug(optChannel, mServer);
        Optionals.ifPresent(optChannel, channel -> {
            final Optional<RelayChannelUser> optUser = mUserChannelInterface.getUser(sendingNick);
            final boolean mention = MentionParser.onMentionableCommand(action,
                    getServer().getUser().getNick().getNickAsString());

            final ChannelEvent event;
            if (optUser.isPresent()) {
                event = new ChannelWorldActionEvent(channel, action, optUser.get(), mention);
            } else {
                event = new ChannelWorldActionEvent(channel, action, sendingNick, mention);
            }
            channel.postAndStoreEvent(event);
        });
    }
    // Commands End Here

    // Replies start here
    public void onParseReply(final List<String> parsedArray, final String rawSource) {
        final String normalMessage = parsedArray.get(3);
        final String message = normalMessage.substring(1, normalMessage.length() - 1);

        // TODO - THIS IS INCOMPLETE
        if (message.startsWith("ACTION")) {
            // Nothing should be done for an action reply - it is technically invalid
        } else if (message.startsWith("FINGER")) {
        } else if (message.startsWith("VERSION")) {
            final String nick = IRCUtils.getNickFromRaw(rawSource);
            final String version = message.replace("VERSION", "");
            mServer.postAndStoreEvent(new VersionEvent(mServer, nick, version));
        } else if (message.startsWith("SOURCE")) {
        } else if (message.startsWith("USERINFO")) {
        } else if (message.startsWith("ERRMSG")) {
        } else if (message.startsWith("PING")) {
        } else if (message.startsWith("TIME")) {
        }
    }
    // Replies end here

    private RelayServer getServer() {
        return mServer;
    }
}