package com.fusionx.relay.parser.code;

import com.fusionx.relay.QueryUser;
import com.fusionx.relay.Server;
import com.fusionx.relay.event.query.QueryNoSuchNickEvent;
import com.fusionx.relay.event.server.GenericServerEvent;

import java.util.List;

import static com.fusionx.relay.constants.ServerReplyCodes.ERR_NICKNAMEINUSE;
import static com.fusionx.relay.constants.ServerReplyCodes.ERR_NOSUCHNICK;

class ErrorParser extends CodeParser {

    ErrorParser(Server server) {
        super(server);
    }

    @Override
    public void onParseCode(final int code, final List<String> parsedArray) {
        switch (code) {
            case ERR_NOSUCHNICK:
                onNoSuchNickError(parsedArray);
                break;
            case ERR_NICKNAMEINUSE:
                onNickInUse(parsedArray);
                break;
        }
    }

    // TODO - implement this
    private void onNickInUse(final List<String> parsedArray) {
    }

    /**
     * Example line:
     * :some.server.url 401 holoirc holoirctester :No such nick/channel
     *
     * @param parsedArray - the array of the line (split by spaces)
     */
    private void onNoSuchNickError(final List<String> parsedArray) {
        final String nick = parsedArray.get(0);
        final String message = parsedArray.get(1);
        final QueryUser user = mUserChannelInterface.getQueryUser(nick);

        // If the user is null then this no such nick event happened for another reason
        if (user != null) {
            mServerEventBus.postAndStoreEvent(new QueryNoSuchNickEvent(user, message), user);
        } else {
            mServerEventBus.postAndStoreEvent(new GenericServerEvent(message));
        }
    }
}