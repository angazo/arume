package com.angazo.arume.ui.config;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncryptionService {

    private static final String ENC_PREFIX = "ENC(";
    private static final String ENC_SUFFIX = ")";
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private static final int KEY_LENGTH = 256;
    private static final int PBKDF2_ITERATIONS = 10000;
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String KEYGEN_SALT = "arume-keygen";

    private final SecretKey key;

    public EncryptionService(Path jarDir) {
        this.key = deriveKey(jarDir);
    }

    private static SecretKey deriveKey(Path jarDir) {
        var keyMaterial = resolvePlatformKey(jarDir);
        log.debug("Key material for encryption: {}", keyMaterial);
        try {
            var factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            KeySpec spec = new PBEKeySpec(
                keyMaterial.toCharArray(),
                KEYGEN_SALT.getBytes(StandardCharsets.UTF_8),
                PBKDF2_ITERATIONS,
                KEY_LENGTH
            );
            var tmpKey = factory.generateSecret(spec);
            return new SecretKeySpec(tmpKey.getEncoded(), "AES");
        } catch (Exception e) {
            throw new ConfigException("Failed to derive encryption key", e);
        }
    }

    static String resolvePlatformKey(Path jarDir) {
        try {
            var fileStore = Files.getFileStore(jarDir);
            var osName = System.getProperty("os.name", "").toLowerCase();

            if (osName.contains("linux")) {
                var uuid = resolveLinuxFsUuid(fileStore, jarDir);
                if (uuid != null) return uuid;
            } else if (osName.contains("mac")) {
                var uuid = resolveMacFsUuid(fileStore, jarDir);
                if (uuid != null) return uuid;
            } else if (osName.contains("win")) {
                var vsn = resolveWindowsVolumeSerial(fileStore);
                if (vsn != null) return vsn;
            }

            return fallbackKey(fileStore);
        } catch (Exception e) {
            try {
                return fallbackKey(Files.getFileStore(jarDir));
            } catch (Exception ex) {
                return jarDir.toAbsolutePath().toString();
            }
        }
    }

    private static String resolveLinuxFsUuid(FileStore fileStore, Path jarDir) {
        String device = resolveLinuxDevice(fileStore, jarDir);
        if (device == null) return null;
        try {
            var process = new ProcessBuilder("blkid", "-s", "UUID", "-o", "value", device)
                .redirectErrorStream(true)
                .start();
            try (var scanner = new Scanner(process.getInputStream()).useDelimiter("\\A")) {
                process.waitFor();
                if (scanner.hasNext()) {
                    var uuid = scanner.next().trim();
                    if (!uuid.isEmpty()) return "linux-uuid:" + uuid;
                }
            }
        } catch (Exception e) {
            log.debug("blkid failed, trying /dev/disk/by-uuid", e);
        }
        try {
            var uuidDir = Path.of("/dev/disk/by-uuid");
            try (var entries = Files.list(uuidDir)) {
                for (var entry : entries.toList()) {
                    if (Files.isSymbolicLink(entry) && Files.readSymbolicLink(entry).toString().endsWith(
                        device.substring(device.lastIndexOf('/') + 1))) {
                        var uuid = entry.getFileName().toString();
                        return "linux-uuid:" + uuid;
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Could not resolve UUID via /dev/disk/by-uuid", e);
        }
        return null;
    }

    private static String resolveLinuxDevice(FileStore fileStore, Path jarDir) {
        String name = fileStore.name();
        if (name != null && name.startsWith("/dev/")) return name;
        try {
            var mountContent = Files.readString(Path.of("/proc/self/mountinfo"));
            var jarPath = jarDir.toRealPath().toString();
            String bestMatch = null;
            int bestLen = -1;
            for (var line : mountContent.split("\n")) {
                var parts = line.split(" ");
                if (parts.length >= 5) {
                    var mountPoint = parts[4];
                    if (jarPath.startsWith(mountPoint) && mountPoint.length() > bestLen) {
                        bestMatch = parts[2]; // device major:minor
                        bestLen = mountPoint.length();
                    }
                }
            }
            if (bestMatch != null && bestMatch.contains(":")) {
                var majMin = bestMatch.split(":");
                var devPath = Path.of("/sys/dev/block/" + majMin[0] + ":" + majMin[1]);
                var uevent = devPath.resolve("uevent");
                if (Files.exists(uevent)) {
                    var ueventContent = Files.readString(uevent);
                    for (var line : ueventContent.split("\n")) {
                        if (line.startsWith("DEVNAME=")) {
                            return "/dev/" + line.substring(8).trim();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Could not resolve Linux device via mountinfo", e);
        }
        return name;
    }

    private static String resolveMacFsUuid(FileStore fileStore, Path jarDir) {
        try {
            var mountPoint = resolveMountPoint(fileStore, jarDir);
            if (mountPoint == null) return null;
            var process = new ProcessBuilder("diskutil", "info", mountPoint)
                .redirectErrorStream(true)
                .start();
            try (var scanner = new Scanner(process.getInputStream()).useDelimiter("\\A")) {
                process.waitFor();
                if (scanner.hasNext()) {
                    for (var line : scanner.next().split("\n")) {
                        var trimmed = line.trim();
                        if (trimmed.startsWith("Volume UUID:")) {
                            var uuid = trimmed.substring("Volume UUID:".length()).trim();
                            if (!uuid.isEmpty()) return "mac-uuid:" + uuid;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Could not resolve macOS volume UUID", e);
        }
        return null;
    }

    private static String resolveWindowsVolumeSerial(FileStore fileStore) {
        try {
            var vsn = fileStore.getAttribute("volume:vsn");
            if (vsn != null) return "win-vsn:" + vsn;
        } catch (Exception e) {
            log.debug("Could not resolve Windows volume serial", e);
        }
        return null;
    }

    private static String resolveMountPoint(FileStore fileStore, Path jarDir) {
        try {
            return jarDir.toRealPath().toString();
        } catch (Exception e) {
            return jarDir.toAbsolutePath().toString();
        }
    }

    static String fallbackKey(FileStore fileStore) {
        try {
            var name = fileStore.name() != null ? fileStore.name() : "unknown";
            var type = fileStore.type() != null ? fileStore.type() : "unknown";
            var totalSpace = fileStore.getTotalSpace();
            return "fallback:" + name + ":" + type + ":" + totalSpace;
        } catch (Exception e) {
            return "fallback:unknown";
        }
    }

    public String encrypt(String plaintext) {
        try {
            var iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            var cipher = Cipher.getInstance(ALGORITHM);
            var spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            var ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            var combined = new byte[IV_LENGTH + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(ciphertext, 0, combined, IV_LENGTH, ciphertext.length);

            var encoded = Base64.getEncoder().encodeToString(combined);
            return ENC_PREFIX + encoded + ENC_SUFFIX;
        } catch (Exception e) {
            throw new ConfigException("Failed to encrypt value", e);
        }
    }

    public String decrypt(String wrapped) {
        if (!isEncrypted(wrapped)) return wrapped;
        var encoded = wrapped.substring(ENC_PREFIX.length(), wrapped.length() - ENC_SUFFIX.length());
        try {
            var combined = Base64.getDecoder().decode(encoded);

            var iv = new byte[IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);

            var cipher = Cipher.getInstance(ALGORITHM);
            var spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            var plaintext = cipher.doFinal(combined, IV_LENGTH, combined.length - IV_LENGTH);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new ConfigException("Failed to decrypt configuration. "
                + "This typically happens when the JAR has been moved to a different machine or disk.", e);
        }
    }

    public static boolean isEncrypted(String value) {
        return value != null && value.startsWith(ENC_PREFIX) && value.endsWith(ENC_SUFFIX);
    }
}
