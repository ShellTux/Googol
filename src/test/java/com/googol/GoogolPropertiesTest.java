package com.googol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GoogolPropertiesTest {

    private GoogolProperties properties;

    // Temporary properties file for testing
    private static final String TEST_PROPERTIES_FILE = "test-googol.properties";
    // Existing properties file that should not be deleted
    private static final String EXISTING_PROPERTIES_FILE = GoogolProperties.propertiesFile;

    @BeforeEach
    public void setUp() throws IOException {
        // Write test properties to a temporary file
        FileWriter writer = new FileWriter(TEST_PROPERTIES_FILE);
        writer.write("gateway_registry_port  = 8080\n");
        writer.write("barrels_registry_port  = 9090\n");
        writer.write("barrels_multicast_ip   = 239.255.0.1\n");
        writer.write("barrels_multicast_port = 12345\n");
        writer.write("number_barrels         = 5\n");
        writer.close();

        properties = new GoogolProperties(TEST_PROPERTIES_FILE);
    }

    @Test
    public void hasGatewayRegistryPort() {
        assertEquals(8080, properties.getGatewayRegistryPort());
    }

    @Test
    public void hasBarrelsRegistryPort() {
        assertEquals(9090, properties.getBarrelsRegistryPort());
    }

    @Test
    public void hasBarrelsMulticastIP() {
        assertEquals("239.255.0.1", properties.getBarrelsMulticastIP());
    }

    @Test
    public void hasBarrelsMulticastPort() {
        assertEquals(12345, properties.getBarrelsMulticastPort());
    }

    @Test
    public void hasNumberBarrels() {
        assertEquals(5, properties.getNumberBarrels());
    }

    @Test
    public void propertiesFileIsNotNull() {
        assertNotNull(GoogolProperties.getPropertiesfile());
    }

    // Test for the existing properties file
    @Test
    public void testExistingPropertiesFile() {
        GoogolProperties existingProperties = new GoogolProperties(EXISTING_PROPERTIES_FILE);
        assertTrue(existingProperties.isValid());
    }

    // Clean up temporary properties file after tests
    @AfterAll
    public static void cleanUp() {
        File file = new File(TEST_PROPERTIES_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}
