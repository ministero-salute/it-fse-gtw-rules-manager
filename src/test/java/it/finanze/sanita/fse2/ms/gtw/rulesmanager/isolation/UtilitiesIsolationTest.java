package it.finanze.sanita.fse2.ms.gtw.rulesmanager.isolation;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.*;

@Tag("ISOLATION")
class UtilitiesIsolationTest {
    @TempDir Path directory;

    @Test
    void hashesKnownInputWithStandardSha256Vectors() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                StringUtility.encodeSHA256Hex("abc"));
        assertEquals("ungWv48Bz+pBQUDeXa4iI7ADYaOWF3qctBD/YfIAFa0=", StringUtility.encodeSHA256B64("abc"));
        assertThrows(BusinessException.class, () -> StringUtility.encodeSHA256Hex(null));
        assertThrows(BusinessException.class, () -> StringUtility.encodeSHA256B64(null));
    }

    @Test
    void encodesBinaryContentAndRejectsInvalidBase64() {
        byte[] bytes = new byte[]{0, 1, -1};
        assertEquals("AAH/", StringUtility.encodeBase64(bytes));
        assertArrayEquals(bytes, StringUtility.decodeBase64("AAH/"));
        assertEquals("0001ff", StringUtility.encodeHex(bytes));
        assertThrows(IllegalArgumentException.class, () -> StringUtility.decodeBase64("!invalid!"));
    }

    @Test
    void distinguishesEmptyFromWhitespaceAndSerializesJson() {
        assertTrue(StringUtility.isNullOrEmpty(null));
        assertTrue(StringUtility.isNullOrEmpty(""));
        assertFalse(StringUtility.isNullOrEmpty(" "));
        assertEquals("{\"flag\":true}", StringUtility.toJSON(Map.of("flag", true)));
        assertEquals(4, UUID.fromString(StringUtility.generateUUID()).version());
    }

    @Test
    void savesExactBytesAndOverwritesExistingFile() throws Exception {
        Path file = directory.resolve("data");
        FileUtility.saveToFile("original".getBytes(StandardCharsets.UTF_8), file.toString());
        assertEquals("original", Files.readString(file));
        FileUtility.saveToFile(new byte[]{0, 1, -1}, file.toString());
        assertArrayEquals(new byte[]{0, 1, -1}, Files.readAllBytes(file));
    }

    @Test
    void preservesDocumentedNonThrowingBehaviorOnFileFailures() {
        assertDoesNotThrow(() -> FileUtility.saveToFile(new byte[]{1}, directory.toString()));
        assertNull(FileUtility.getFileFromInternalResources("isolation/does-not-exist"));
    }

    @Test
    void readsClasspathResourceWithoutExternalFilesystemDependency() {
        byte[] resource = FileUtility.getFileFromInternalResources("application-test.properties");
        assertNotNull(resource);
        assertTrue(new String(resource, StandardCharsets.UTF_8).contains("spring.data.mongodb.database=FSE_GTW"));
    }
}
