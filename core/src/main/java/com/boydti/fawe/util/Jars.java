package com.boydti.fawe.util;

import com.boydti.fawe.Fawe;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum Jars {
    WE_B_6_1_7_2("https://addons.cursecdn.com/files/2431/372/worldedit-bukkit-6.1.7.2.jar",
            "711be37301a327aba4e347131875d0564dbfdc2f41053a12db97f0234661778b", 1726340),

    VS_B_5_171_0("https://addons-origin.cursecdn.com/files/912/511/VoxelSniper-5.171.0-SNAPSHOT.jar",
            "292c3b38238e0d8e5f036381d28bccfeb15df67cae53d28b52d066bc6238208f", 3632776),

    MM_v1_4_0("https://github.com/InventivetalentDev/MapManager/releases/download/1.4.0-SNAPSHOT/MapManager_v1.4.0-SNAPSHOT.jar",
            "004A39B0A06E80DE3226B4BCC6080D2DB9B6411CCFB48D647F4FF55B5B91B600", 163279),

    PL_v3_6_0("https://github.com/InventivetalentDev/PacketListenerAPI/releases/download/3.6.0-SNAPSHOT/PacketListenerAPI_v3.6.0-SNAPSHOT.jar",
            "3B26C4EF9BE253E9E7C07AD5AC46CB0C047003EFD36DD433D6B739EB6AAE9410", 166508),

    ;

    public final String url;
    public final int filesize;
    public final String digest;

    /**
     * @param url
     *            Where this jar can be found and downloaded
     * @param digest
     *            The SHA-256 hexadecimal digest
     * @param filesize
     *            Size of this jar in bytes
     */
    Jars(String url, String digest, int filesize) {
        this.url = url;
        this.digest = digest.toUpperCase();
        this.filesize = filesize;
    }

    /** download a jar, verify hash, return byte[] containing the jar */
    public byte[] download() throws IOException {
        byte[] jarBytes = new byte[this.filesize];
        URL url = new URL(this.url);
        try (DataInputStream dis = new DataInputStream(url.openConnection().getInputStream());) {
            dis.readFully(jarBytes);
            if (dis.read() != -1) { // assert that we've read everything
                throw new IllegalStateException("downloaded jar is longer than expected");
            }
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] jarDigestBytes = md.digest(jarBytes);
            String jarDigest = javax.xml.bind.DatatypeConverter.printHexBinary(jarDigestBytes).toUpperCase();

            if (this.digest.equals(jarDigest)) {
                Fawe.debug("++++ HASH CHECK ++++");
                Fawe.debug(this.url);
                Fawe.debug(this.digest);
                return jarBytes;
            } else {
                throw new IllegalStateException("downloaded jar does not match the hash");
            }
        } catch (NoSuchAlgorithmException e) {
            // Shouldn't ever happen, Minecraft won't even run on such a JRE
            throw new IllegalStateException("Your JRE does not support SHA-256");
        }
    }
}
