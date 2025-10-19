package com.example.covault.utils;

import ua_parser.Client;
import ua_parser.Parser;

public class UserAgentUtility {

    private static final Parser parser = new Parser();

    public static String getBrowser(String userAgent) {
        Client client = parser.parse(userAgent);
        return client.userAgent.family + " " + client.userAgent.major;
    }

    public static String getOS(String userAgent) {
        Client client = parser.parse(userAgent);
        return client.os.family + " " + client.os.major;
    }

    public static String getDevice(String userAgent) {
        Client client = parser.parse(userAgent);
        return client.device.family != null ? client.device.family : "Unknown Device";
    }

    public static String getSummary(String userAgent) {
        Client client = parser.parse(userAgent);
        return String.format("%s / %s / %s",
                getDevice(userAgent),
                getBrowser(userAgent),
                getOS(userAgent));
    }
}
