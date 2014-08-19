package co.fusionx.relay.sender.relay;

import co.fusionx.relay.packet.server.NickServPasswordPacket;
import co.fusionx.relay.packet.server.QuitPacket;
import co.fusionx.relay.packet.server.UserPacket;
import co.fusionx.relay.packet.server.internal.PongPacket;
import co.fusionx.relay.packet.server.internal.ServerPasswordPacket;

public class RelayInternalSender {

    private final RelayPacketSender mRelayPacketSender;

    public RelayInternalSender(final RelayPacketSender relayPacketSender) {
        mRelayPacketSender = relayPacketSender;
    }

    public void pongServer(final String source) {
        mRelayPacketSender.post(new PongPacket(source));
    }

    public void sendServerPassword(final String password) {
        mRelayPacketSender.post(new ServerPasswordPacket(password));
    }

    public void sendNickServPassword(final String password) {
        mRelayPacketSender.post(new NickServPasswordPacket(password));
    }

    public void sendUser(final String serverUserName, final String realName) {
        mRelayPacketSender.post(new UserPacket(serverUserName, realName));
    }

    public void quitServer(final String quitReason) {
        mRelayPacketSender.post(new QuitPacket(quitReason));
    }
}