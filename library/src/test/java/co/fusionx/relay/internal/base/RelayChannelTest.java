package co.fusionx.relay.internal.base;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import co.fusionx.relay.base.Channel;

import static org.assertj.core.api.Assertions.assertThat;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class RelayChannelTest {

    public static RelayChannel getTestChannel() {
        return getTestChannel("#relay");
    }

    public static RelayChannel getTestChannel(final String channelName) {
        return null;
    }

    public static void populateTestChannel(final Channel channel) {
    }

    @Test
    public void testIsChannelPrefix() {
        assertThat(RelayChannel.isChannelPrefix('+'))
                .isTrue();
        assertThat(RelayChannel.isChannelPrefix('#'))
                .isTrue();
        assertThat(RelayChannel.isChannelPrefix('&'))
                .isTrue();
        assertThat(RelayChannel.isChannelPrefix('!'))
                .isTrue();
        // TODO - any other character should be false
    }

    @Test
    public void testWipeChannelData() {

    }

    @Test
    public void testOnChannelEvent() {

    }
}