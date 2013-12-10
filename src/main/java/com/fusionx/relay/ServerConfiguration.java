package com.fusionx.relay;

import com.fusionx.relay.misc.NickStorage;

import org.apache.commons.lang3.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ServerConfiguration {

    /**
     * The informal name of the server that is being connected to
     */
    private final String mTitle;

    /**
     * The URL/hostname of the server that is being connected to
     */
    private final String mUrl;

    /**
     * The port to connect to the server on
     */
    private final int mPort;

    /**
     * Whether SSL should be used for the connection
     */
    private final boolean mSsl;

    /**
     * Whether all SSL certificates should be accepted rather than just those trusted on the device
     * This option is meaningless without SSL being enabled
     */
    private final boolean mSslAcceptAllCertificates;

    /**
     * The object containing the first, second and third choice nicks
     */
    private final NickStorage mNickStorage;

    /**
     * The real name of the user
     */
    private final String mRealName;

    /**
     * Whether the library can change the nick on the user's behalf if needed
     */
    private final boolean mNickChangable;

    /**
     * The username to connect to the server with - unless the server is password protected this is
     * meaningless in most cases
     */
    private final String mServerUserName;

    /**
     * The password to connect to the server with
     */
    private final String mServerPassword;

    /**
     * The username for SASL authentication
     */
    private final String mSaslUsername;

    /**
     * The password for SASL authentication
     */
    private final String mSaslPassword;

    /**
     * The password for NickServ authentication
     */
    private final String mNickservPassword;

    /**
     * The list of all the channels that will be joined when connected to the server
     */
    private final ArrayList<String> mAutoJoinChannels;

    private ServerConfiguration(final Builder builder) {
        mTitle = builder.getTitle();
        mUrl = builder.getUrl();
        mPort = builder.getPort();

        mSsl = builder.isSsl();
        mSslAcceptAllCertificates = builder.isSslAcceptAllCertificates();

        mNickStorage = builder.getNickStorage();
        mRealName = builder.getRealName();
        mNickChangable = builder.isNickChangeable();

        mServerUserName = builder.getServerUserName();
        mServerPassword = builder.getServerPassword();

        mSaslUsername = builder.getSaslUsername();
        mSaslPassword = builder.getSaslPassword();

        mNickservPassword = builder.getNickservPassword();

        mAutoJoinChannels = builder.getAutoJoinChannels();
    }

    // Helper methods
    public boolean isSaslAvailable() {
        return StringUtils.isNotEmpty(mSaslUsername) && StringUtils.isNotEmpty(mSaslPassword);
    }

    // Getters and setters
    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getPort() {
        return mPort;
    }

    public boolean isSslEnabled() {
        return mSsl;
    }

    public boolean shouldAcceptAllSSLCertificates() {
        return mSslAcceptAllCertificates;
    }

    public NickStorage getNickStorage() {
        return mNickStorage;
    }

    public String getRealName() {
        return mRealName;
    }

    public boolean isNickChangable() {
        return mNickChangable;
    }

    public String getServerUserName() {
        return mServerUserName;
    }

    public String getServerPassword() {
        return mServerPassword;
    }

    public String getSaslUsername() {
        return mSaslUsername;
    }

    public String getSaslPassword() {
        return mSaslPassword;
    }

    public String getNickservPassword() {
        return mNickservPassword;
    }

    public ArrayList<String> getAutoJoinChannels() {
        return mAutoJoinChannels;
    }

    public static class Builder implements Parcelable {

        public static final Parcelable.Creator<Builder> CREATOR =
                new Parcelable.Creator<Builder>() {
                    public Builder createFromParcel(Parcel in) {
                        return new Builder(in);
                    }

                    public Builder[] newArray(int size) {
                        return new Builder[size];
                    }
                };

        /**
         * The informal name of the server that is being connected to
         */
        private String mTitle;

        /**
         * The URL/hostname of the server that is being connected to
         */
        private String mUrl;

        /**
         * The port to connect to the server on
         */
        private int mPort;

        /**
         * Whether SSL should be used for the connection
         */
        private boolean mSsl;

        /**
         * Whether all SSL certificates should be accepted rather than just those trusted on the
         * device This option is meaningless without SSL being enabled
         */
        private boolean mSslAcceptAllCertificates;

        /**
         * The object containing the first, second and third choice nicks
         */
        private NickStorage mNickStorage;

        /**
         * The real name of the user
         */
        private String mRealName;

        /**
         * Whether the library can change the nick on the user's behalf if needed
         */
        private boolean mNickChangeable;

        /**
         * The username to connect to the server with - unless the server is password protected this
         * is meaningless in most cases
         */
        private String mServerUserName;

        /**
         * The password to connect to the server with
         */
        private String mServerPassword;

        /**
         * The username for SASL authentication
         */
        private String mSaslUsername;

        /**
         * The password for SASL authentication
         */
        private String mSaslPassword;

        /**
         * The password for NickServ authentication
         */
        private String mNickservPassword;

        /**
         * The list of all the channels that will be joined when connected to the server
         */
        private final ArrayList<String> mAutoJoinChannels;

        public Builder() {
            mTitle = "";
            mUrl = "";
            mPort = 6667;

            mSsl = false;
            mSslAcceptAllCertificates = false;

            mNickStorage = new NickStorage("androidirclibrary", "", "");
            mRealName = "";
            mNickChangeable = true;

            mServerUserName = "androidirclibrary";
            mServerPassword = "";

            mSaslUsername = "";
            mSaslPassword = "";

            mNickservPassword = "";

            mAutoJoinChannels = new ArrayList<String>();
        }

        private Builder(final Parcel in) {
            mTitle = in.readString();
            mUrl = in.readString();
            mPort = in.readInt();

            mSsl = in.readInt() == 1;
            mSslAcceptAllCertificates = in.readInt() == 1;

            mNickStorage = in.readParcelable(NickStorage.class.getClassLoader());
            mRealName = in.readString();
            mNickChangeable = in.readInt() == 1;

            mServerUserName = in.readString();
            mServerPassword = in.readString();

            mSaslUsername = in.readString();
            mSaslPassword = in.readString();

            mNickservPassword = in.readString();

            mAutoJoinChannels = new ArrayList<String>();
            in.readStringList(mAutoJoinChannels);
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeString(mTitle);
            out.writeString(mUrl);
            out.writeInt(mPort);

            out.writeInt(mSsl ? 1 : 0);
            out.writeInt(mSslAcceptAllCertificates ? 1 : 0);

            out.writeParcelable(mNickStorage, 0);
            out.writeString(mRealName);
            out.writeInt(mNickChangeable ? 1 : 0);

            out.writeString(mServerUserName);
            out.writeString(mServerPassword);

            out.writeString(mSaslUsername);
            out.writeString(mSaslPassword);

            out.writeString(mNickservPassword);

            out.writeStringList(mAutoJoinChannels);
        }

        public ServerConfiguration build() {
            if (StringUtils.isEmpty(mTitle)) {
                throw new IllegalArgumentException("The server title cannot be empty");
            } else if (StringUtils.isEmpty(mUrl)) {
                throw new IllegalArgumentException("The server URL cannot be empty");
            }
            return new ServerConfiguration(this);
        }

        // Getters and setters
        public String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String url) {
            mUrl = url;
        }

        public int getPort() {
            return mPort;
        }

        public void setPort(int port) {
            mPort = port;
        }

        public boolean isSsl() {
            return mSsl;
        }

        public void setSsl(boolean ssl) {
            mSsl = ssl;
        }

        public boolean isSslAcceptAllCertificates() {
            return mSslAcceptAllCertificates;
        }

        public void setSslAcceptAllCertificates(boolean sslAcceptAllCertificates) {
            mSslAcceptAllCertificates = sslAcceptAllCertificates;
        }

        public NickStorage getNickStorage() {
            return mNickStorage;
        }

        public void setNickStorage(NickStorage nickStorage) {
            mNickStorage = nickStorage;
        }

        public String getRealName() {
            return mRealName;
        }

        public void setRealName(String realName) {
            mRealName = realName;
        }

        public boolean isNickChangeable() {
            return mNickChangeable;
        }

        public void setNickChangeable(boolean nickChangeable) {
            mNickChangeable = nickChangeable;
        }

        public String getServerUserName() {
            return mServerUserName;
        }

        public void setServerUserName(String serverUserName) {
            mServerUserName = serverUserName;
        }

        public String getServerPassword() {
            return mServerPassword;
        }

        public void setServerPassword(String serverPassword) {
            mServerPassword = serverPassword;
        }

        public String getSaslUsername() {
            return mSaslUsername;
        }

        public void setSaslUsername(String saslUsername) {
            mSaslUsername = saslUsername;
        }

        public String getSaslPassword() {
            return mSaslPassword;
        }

        public void setSaslPassword(String saslPassword) {
            mSaslPassword = saslPassword;
        }

        public String getNickservPassword() {
            return mNickservPassword;
        }

        public void setNickservPassword(String nickservPassword) {
            mNickservPassword = nickservPassword;
        }

        public ArrayList<String> getAutoJoinChannels() {
            return mAutoJoinChannels;
        }
    }
}