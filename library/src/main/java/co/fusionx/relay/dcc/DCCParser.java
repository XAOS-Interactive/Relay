package co.fusionx.relay.dcc;

import java.util.List;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.dcc.pending.DCCPendingChatConnection;
import co.fusionx.relay.dcc.pending.DCCPendingFileConnection;
import co.fusionx.relay.event.server.DCCChatRequestEvent;
import co.fusionx.relay.event.server.DCCSendRequestEvent;
import co.fusionx.relay.util.IRCUtils;

public class DCCParser {

    private final RelayServer mServer;

    public DCCParser(final RelayServer server) {
        mServer = server;
    }

    // Examples of parsedArray lines
    // DCC CHAT chat <server IP int> <server port>
    // DCC FILE <file name> <server IP int> <server port> <file size>
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        // Get the nick of the person who requested this
        final String nick = IRCUtils.getNickFromRaw(rawSource);

        // Remove the DCC prefix
        parsedArray.remove(0);

        // Now get the type of DCC
        final String type = parsedArray.remove(0);

        // Remove the argument
        final String argument = parsedArray.remove(0);

        // Retrieve the ip address as an integer and the port
        final long ipDecimal = Long.parseLong(parsedArray.remove(0));
        final int port = Integer.parseInt(parsedArray.remove(0));

        // Convert the address to a normal representation
        final String ipAddress = IRCUtils.ipDecimalToString(ipDecimal);

        switch (type) {
            case "CHAT":
                parseChatCommand(nick, ipAddress, port);
                break;
            case "SEND":
                parseFileCommand(nick, argument, ipAddress, port, parsedArray);
                break;
        }
    }

    private void parseChatCommand(final String nick, final String ipAddress, final int port) {
        // Send the event
        final DCCPendingChatConnection connection = new DCCPendingChatConnection(nick,
                mServer.getDCCManager(), ipAddress, port, "chat", 0);
        mServer.postAndStoreEvent(new DCCChatRequestEvent(mServer, connection));
    }

    private void parseFileCommand(final String nick, final String fileName, final String ipAddress,
            final int port, final List<String> parsedArray) {
        // Retrieve the file size - file size is optional from the spec
        final long size = parsedArray.size() > 0 ? Long.parseLong(parsedArray.remove(0)) : 0;

        // Send the event
        final DCCPendingFileConnection connection = new DCCPendingFileConnection(nick,
                mServer.getDCCManager(), ipAddress, port, fileName, size);
        mServer.postAndStoreEvent(new DCCSendRequestEvent(mServer, connection));
    }
}