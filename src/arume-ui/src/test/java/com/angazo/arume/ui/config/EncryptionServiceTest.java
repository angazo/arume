package com.angazo.arume.ui.config;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class EncryptionServiceTest {

    @TempDir
    Path tempDir;

    private EncryptionService service;

    @BeforeEach
    void setUp() {
        service = new EncryptionService(tempDir);
    }

    @Test
    void shouldEncryptAndDecryptRoundtrip() {
        var plaintext = "jdbc:h2:file:/tmp/data/arume;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;CIPHER=AES;USER=admin;PASSWORD=secret123456 secret123456";

        var encrypted = service.encrypt(plaintext);

        assertNotNull(encrypted);
        assertTrue(EncryptionService.isEncrypted(encrypted));
        assertTrue(encrypted.startsWith("ENC("));
        assertTrue(encrypted.endsWith(")"));
        assertNotEquals(plaintext, encrypted);

        var decrypted = service.decrypt(encrypted);
        assertEquals(plaintext, decrypted);
    }

    @Test
    void shouldProduceDifferentCiphertextForSamePlaintext() {
        var plaintext = "jdbc:h2:file:/tmp/db";

        var enc1 = service.encrypt(plaintext);
        var enc2 = service.encrypt(plaintext);

        assertNotEquals(enc1, enc2, "Same plaintext should produce different ciphertext due to random IV");
        assertEquals(plaintext, service.decrypt(enc1));
        assertEquals(plaintext, service.decrypt(enc2));
    }

    @Test
    void shouldProduceStableKeyForSamePath() {
        var service2 = new EncryptionService(tempDir);
        var plaintext = "test-secret";

        var encrypted = service.encrypt(plaintext);
        var decrypted = service2.decrypt(encrypted);

        assertEquals(plaintext, decrypted, "Same path should derive same key");
    }

    @Test
    void shouldProduceSameKeyForSameFilesystem() throws Exception {
        var otherDir = tempDir.resolve("subdir");
        java.nio.file.Files.createDirectories(otherDir);
        var otherService = new EncryptionService(otherDir);

        var plaintext = "test-secret";
        var encrypted = service.encrypt(plaintext);

        assertEquals(plaintext, otherService.decrypt(encrypted),
            "Same filesystem should derive same key");
    }

    @Test
    void shouldDetectEncryptedValue() {
        var encrypted = service.encrypt("hello");
        assertTrue(EncryptionService.isEncrypted(encrypted));
        assertFalse(EncryptionService.isEncrypted("hello"));
        assertFalse(EncryptionService.isEncrypted(null));
        assertFalse(EncryptionService.isEncrypted("ENC(incomplete"));
    }

    @Test
    void shouldDetectTamperedData() {
        var plaintext = "jdbc:h2:file:/tmp/db";
        var encrypted = service.encrypt(plaintext);

        var tampered = encrypted.substring(0, encrypted.length() - 3) + "XXX)";
        assertThrows(ConfigException.class, () -> service.decrypt(tampered));
    }

    @Test
    void shouldPassThroughPlaintextInDecrypt() {
        var plaintext = "jdbc:h2:file:/tmp/db";
        assertEquals(plaintext, service.decrypt(plaintext));
    }

    @Test
    void shouldEncodeSpecialCharacters() {
        var url = "jdbc:h2:file:/tmp/data/arume;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;CIPHER=AES;USER=admin;PASSWORD=s3cr3tP@ss! s3cr3tP@ss!";

        var encrypted = service.encrypt(url);
        var decrypted = service.decrypt(encrypted);

        assertEquals(url, decrypted);
    }

    @Test
    void shouldResolveFallbackKey() throws Exception {
        var fileStore = java.nio.file.Files.getFileStore(tempDir);
        var fallback = EncryptionService.fallbackKey(fileStore);

        assertNotNull(fallback);
        assertTrue(fallback.startsWith("fallback:"));
        assertTrue(fallback.contains(fileStore.name()));
    }

    @Test
    void shouldResolvePlatformKey() {
        var key = EncryptionService.resolvePlatformKey(tempDir);

        assertNotNull(key);
        assertFalse(key.isEmpty());
    }
}
