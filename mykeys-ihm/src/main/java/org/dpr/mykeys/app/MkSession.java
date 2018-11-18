package org.dpr.mykeys.app;

import java.time.Instant;

public class MkSession {
   static  public long timeStampMillis = Instant.now().toEpochMilli();
    public static char[] password;
    public static String user;
    public static String hashPWd;

    public synchronized static void updateCreds(String id, char[] password) {
        MkSession.password = password;
        MkSession.user = id;
    }
}
