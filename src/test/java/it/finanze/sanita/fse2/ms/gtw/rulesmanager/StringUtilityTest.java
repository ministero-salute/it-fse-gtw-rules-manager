package it.finanze.sanita.fse2.ms.gtw.rulesmanager;

import it.finanze.sanita.fse2.ms.gtw.rulesmanager.config.Constants;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.CurrentApplicationLogEnum;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.enums.ErrorLogEnum;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.exceptions.BusinessException;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.FileUtility;
import it.finanze.sanita.fse2.ms.gtw.rulesmanager.utility.StringUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ComponentScan(basePackages = {Constants.ComponentScan.BASE})
@ActiveProfiles(Constants.Profile.TEST)
class StringUtilityTest {
	
    @Test
	void stringCheckTest() {						
    	String stringTest = "string";
    	String stringTestEmpty = null;
    	
		assertTrue(StringUtility.isNullOrEmpty(stringTestEmpty));
		assertFalse(StringUtility.isNullOrEmpty(stringTest));	
		}

	@Test
	@DisplayName("String utility null -> exception")
	void encodeSHA256B64Exception() throws Exception{
		
		//exception encode
		String convertion = null;
		try {
			convertion = StringUtility.encodeSHA256B64(null);
		} catch (Exception e) {
			Assertions.assertNull(convertion);
		}
		assertThrows(BusinessException.class, () -> StringUtility.encodeSHA256B64(null));
		//encode OK
		String convertion1 = null;
		convertion1 = StringUtility.encodeSHA256B64("aaa");
		assertNotNull(convertion1);
		
	}
	
	@Test
	@DisplayName("String utility -> OK")
	void encodeSHA256B64Ok() {
		//encode OK
		String convertion1 = null;
		convertion1 = StringUtility.encodeSHA256B64("aaa");
		assertNotNull(convertion1);
	}
	
	
	@Test
	@DisplayName("String utility HEX null -> exception")
	void encodeSHA256HEXException() throws Exception{
		
		//exception encode
		String convertion = null;
		try {
			convertion = StringUtility.encodeSHA256Hex(null);
		} catch (Exception e) {
			Assertions.assertNull(convertion);
		}
		assertThrows(BusinessException.class, () -> StringUtility.encodeSHA256Hex(null));
	}
	
	@Test
	@DisplayName("String utility 256 HEX-> OK")
	void encodeSHA256HEXOk() {
		//encode OK
		String strToConvert = "0";
		String convertion = StringUtility.encodeSHA256Hex(strToConvert);
		assertNotNull(convertion);
		//encode wrong case
		String strToConvert2 = "1aa";
		String convertion2 = StringUtility.encodeSHA256Hex(strToConvert2);
		assertNotNull(convertion2);
		
	} 
	
	@Test
	@DisplayName("Encode Base 64 Test") 
	void encodeBase64Test() {
		String stringToEncode = "testString"; 
		String encodedString = StringUtility.encodeBase64(stringToEncode.getBytes()); 
		
		assertEquals("dGVzdFN0cmluZw==", encodedString); 
	} 
	 
	
	@Test
	@DisplayName("Generate UUID Test")
	void generateUuidTest() {
		String uuid = StringUtility.generateUUID(); 
		
		assertEquals(String.class, uuid.getClass()); 
	} 
	
    @Test
	@DisplayName("enumeration tests")
	void logEnumTest() {						
			for(ErrorLogEnum entry : Arrays.asList(ErrorLogEnum.values())) {
				assertNotNull(entry.getCode());
				assertNotNull(entry.getDescription());
				}
			
			for(CurrentApplicationLogEnum entry : Arrays.asList(CurrentApplicationLogEnum.values())) {
				assertNotNull(entry.getCode());
				assertNotNull(entry.getDescription());
				}
		}
    
    @Test
    void fileUtilityTest() {
    	byte[] bytes = "\u00e0\u004f\u00d0\u0020\u00ea\u003a\u0069\u0010\u00a2\u00d8\u0008\u0000\u002b\u0030\u0030\u009d".getBytes();    
    	String fileName = "string";
    	String testFile= System.getProperty("user.dir") + "/src/test/resources/Files/schematron/schematronFSE.sch.xsl"; 
    	byte[] bytesReturn = FileUtility.getFileFromFS(fileName);
    	byte[] bytesReturnTestFile = FileUtility.getFileFromFS(testFile); 


    	assertDoesNotThrow(()->FileUtility.saveToFile(bytes,fileName));
    	assertDoesNotThrow(()->FileUtility.saveToFile(bytes,null));
    	assertDoesNotThrow(()->FileUtility.saveToFile(bytesReturn,null)); 
    	
    	assertNotNull(bytesReturnTestFile); 
    	

		try {
			Files.deleteIfExists(Paths.get(fileName));
		} catch (IOException e) {
			log.warn("Unable to delete {} file", fileName);
		}
	} 
    
    @Test
    void getFileFromInternalResourcesKo() {
    	assertNull(FileUtility.getFileFromInternalResources("fileKo.example")); 
    }

}