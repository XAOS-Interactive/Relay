package co.fusionx.relay.util;

import com.google.common.base.Optional;

import co.fusionx.relay.conversation.Server;
import co.fusionx.relay.misc.RelayConfigurationProvider;

public class LogUtils {

    private static final String TAG = "Relay";

    public static void logOptionalBug(final Optional<?> optional, final Server server) {
        RelayConfigurationProvider.getPreferences().logMissingData(server);
    }
}
