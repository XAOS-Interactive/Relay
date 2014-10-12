package co.fusionx.relay.internal.statechanger.ctcp;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.constant.ChannelPrefix;
import co.fusionx.relay.event.channel.ChannelEvent;
import co.fusionx.relay.event.channel.ChannelWorldActionEvent;
import co.fusionx.relay.event.query.QueryActionWorldEvent;
import co.fusionx.relay.event.server.VersionEvent;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.internal.core.InternalChannel;
import co.fusionx.relay.internal.core.InternalChannelUser;
import co.fusionx.relay.internal.core.InternalQueryUser;
import co.fusionx.relay.internal.core.InternalQueryUserGroup;
import co.fusionx.relay.internal.core.InternalServer;
import co.fusionx.relay.internal.core.InternalUserChannelGroup;
import co.fusionx.relay.internal.sender.CtcpResponsePacketSender;
import co.fusionx.relay.internal.sender.PacketSender;
import co.fusionx.relay.util.LogUtils;
import co.fusionx.relay.util.ParseUtils;

public class CTCPStateChanger {

    private final InternalServer mServer;

    private final InternalQueryUserGroup mQueryManager;

    private final InternalUserChannelGroup mUserChannelDao;

    private final CtcpResponsePacketSender mCtcpResponseSender;

    public CTCPStateChanger(final InternalServer server,
            final InternalUserChannelGroup userChannelGroup,
            final InternalQueryUserGroup queryManager, final PacketSender sender) {
        mServer = server;
        mQueryManager = queryManager;
        mUserChannelDao = userChannelGroup;

        mCtcpResponseSender = new CtcpResponsePacketSender(sender);
    }

    // Commands start here
    public void onParseCommand(final String prefix,
            final String recipient, final String rawMessage) {
        final String message = rawMessage.substring(1, rawMessage.length() - 1);
        final String sendingNick = ParseUtils.getNickFromPrefix(prefix);

        // TODO - THIS IS INCOMPLETE
        if (message.startsWith("ACTION")) {
            onAction(recipient, sendingNick, message);
        } else if (message.startsWith("FINGER")) {
            mCtcpResponseSender.sendFingerResponse(sendingNick,
                    mServer.getConfiguration().getConnectionConfiguration().getRealName());
        } else if (message.startsWith("VERSION")) {
            mCtcpResponseSender.sendVersionResponse(sendingNick);
        } else if (message.startsWith("SOURCE")) {
        } else if (message.startsWith("USERINFO")) {
        } else if (message.startsWith("ERRMSG")) {
            final String query = message.replace("ERRMSG ", "");
            mCtcpResponseSender.sendErrMsgResponse(sendingNick, query);
        } else if (message.startsWith("PING")) {
            final String timestamp = message.replace("PING ", "");
            mCtcpResponseSender.sendPingResponse(sendingNick, timestamp);
        } else if (message.startsWith("TIME")) {
            mCtcpResponseSender.sendTimeResponse(sendingNick);
        } else if (message.startsWith("DCC")) {
            final List<String> parsedDcc = ParseUtils.splitRawLineWithQuote(message);
            //  mDCCParser.onParseCommand(parsedDcc, prefix);
        }
    }

    private void onAction(final String recipient, final String sendingNick, final String message) {
        final String action = message.replace("ACTION ", "");
        if (ChannelPrefix.isPrefix(recipient.charAt(0))) {
            onParseChannelAction(recipient, sendingNick, action);
        } else {
            onParseUserAction(recipient, action);
        }
    }

    private void onParseUserAction(final String nick, final String action) {
        final InternalQueryUser user = mQueryManager.getOrAddQueryUser(nick);
        user.postEvent(new QueryActionWorldEvent(user, action));
    }

    private void onParseChannelAction(final String channelName, final String sendingNick,
            final String action) {
        final Optional<InternalChannel> optChannel = mUserChannelDao.getChannel(channelName);

        Optionals.run(optChannel, channel -> {
            final Optional<InternalChannelUser> optUser = mUserChannelDao.getUser(sendingNick);
            final boolean mention = false;//MentionParser.onMentionableCommand(action,
            //mUserChannelDao.getUser().getNick().getNickAsString());

            final ChannelEvent event;
            if (optUser.isPresent()) {
                event = new ChannelWorldActionEvent(channel, action, optUser.get(), mention);
            } else {
                event = new ChannelWorldActionEvent(channel, action, sendingNick, mention);
            }
            channel.postEvent(event);
        }, () -> LogUtils.logOptionalBug(mServer.getConfiguration()));
    }
    // Commands End Here

    // Replies start here
    public void onParseReply(final List<String> parsedArray, final String prefix) {
        final String normalMessage = parsedArray.get(3);
        final String message = normalMessage.substring(1, normalMessage.length() - 1);

        // TODO - THIS IS INCOMPLETE
        if (message.startsWith("ACTION")) {
            // Nothing should be done for an action reply - it is technically invalid
        } else if (message.startsWith("FINGER")) {
        } else if (message.startsWith("VERSION")) {
            final String nick = ParseUtils.getNickFromPrefix(prefix);
            final String version = message.replace("VERSION", "");
            mServer.postEvent(new VersionEvent(mServer, nick, version));
        } else if (message.startsWith("SOURCE")) {
        } else if (message.startsWith("USERINFO")) {
        } else if (message.startsWith("ERRMSG")) {
        } else if (message.startsWith("PING")) {
        } else if (message.startsWith("TIME")) {
        }
    }
    // Replies end here
}