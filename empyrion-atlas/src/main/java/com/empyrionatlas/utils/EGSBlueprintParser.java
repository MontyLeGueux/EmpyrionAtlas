package com.empyrionatlas.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.empyrionatlas.dto.BlueprintParseResultDTO;
import com.empyrionatlas.service.ModTradingDataService;

public class EGSBlueprintParser {
	
	public static final int HEADER_PARSE_LENGTH = 4096;
	public static final byte ZIP_HEADER_FIRST_BYTE = 0x50;
	public static final byte ZIP_HEADER_SECOND_BYTE = 0x4B;
	public static final byte BLUEPRINT_NAME_END_BYTE = 0x15;
	public static final byte BLUEPRINT_TRADER_DATA_END_BYTE = 0x00;
	
	public static final String BLUEPRINT_FILE_EXTENSION = ".epb";
	public static final String BLUEPRINT_TRADER_DATA_START_STRING = "Type";
	
	private static final Logger logger = LoggerFactory.getLogger(ModTradingDataService.class);
	
	public static BlueprintParseResultDTO parseBlueprintFile(File blueprintFile) throws IOException {
		if(blueprintFile != null && blueprintFile.exists() && blueprintFile.getName().endsWith(BLUEPRINT_FILE_EXTENSION)) {
			logger.info("Reading blueprint file : " + blueprintFile.toPath().toString());
			return new BlueprintParseResultDTO(extractBlueprintName(blueprintFile), extractTraderNames(blueprintFile));
		}
		else {
			return null;
		}
    }
	
	private static String extractBlueprintName(File blueprintFile) throws IOException {
		String result = null;
		try (FileInputStream fis = new FileInputStream(blueprintFile)) {
            byte[] buffer = fis.readNBytes(HEADER_PARSE_LENGTH);

            for (int i = 1; i < buffer.length; i++) {
            	
            	//We start by looking for the name end marker
                if (buffer[i] != BLUEPRINT_NAME_END_BYTE) continue;

                logger.info("Found 0x15 at position : " + i);
                
                int end = i;
                int start = end - 1;
                
                //Work backwards until we reach the length byte
                while (start > 0 && isValidCharacter(buffer[start])) {
                    start--;
                }
                logger.info("Found start of string at : " + start);
                
                int stringLength = end - (start + 1);
                
                logger.info("Validating size byte : " + Byte.toUnsignedInt(buffer[start]) + " against actual lenght : " + stringLength);
                if (stringLength != Byte.toUnsignedInt(buffer[start])) continue;
                
                byte[] nameBytes = Arrays.copyOfRange(buffer, start + 1, end);
                result = new String(nameBytes, StandardCharsets.UTF_8);
                
                logger.info("Validating potential name : " + result);
                if(result.chars().noneMatch(Character::isISOControl)) {
                	logger.info("Saving blueprint name : " + result);
                	return result;
                }
                else {
                	return null;
                }
            }
            return null;
        }
    }
	
	private static boolean isValidCharacter(byte b) {
	    int value = Byte.toUnsignedInt(b);
	    return value >= 32 && value <= 126; // Basic ASCII character range
	}
	
	private static List<String> extractTraderNames(File blueprintFile) throws IOException {
		byte[] fileBytes;
		
		logger.info("Looking for traders in blueprint");
		
		try (FileInputStream inputStream = new FileInputStream(blueprintFile)) {
			fileBytes = inputStream.readAllBytes();
        }
		if(fileBytes == null || !(fileBytes.length > 0)) {
			return null;
		}

		logger.info("Looking for zip start");
		
        int zipStart = findPKZipStart(fileBytes);
        if (zipStart == -1 || zipStart >= fileBytes.length) {
        	logger.info("No PK zip header found in blueprint");
            return null;
        }

        logger.info("Unzipping data");
        
        byte[] unzippedData = unzip(Arrays.copyOfRange(fileBytes, zipStart, fileBytes.length));

        logger.info("Parsing names");
        if(unzippedData != null) {
        	return extractNamesFromUnzippedData(unzippedData);
        }
        else {
        	return null;
        }
        
    }

    private static int findPKZipStart(byte[] data) {
    	int startPos = 0;
        for (startPos = 0; startPos < data.length - 1; startPos++) {
            if (data[startPos] == ZIP_HEADER_FIRST_BYTE && data[startPos + 1] == ZIP_HEADER_SECOND_BYTE) {
                return startPos;
            }
        }
        return -1;
    }

    private static byte[] unzip(byte[] zipBytes) throws IOException {
    	
    	byte[] result = null;
    	
    	if (zipBytes == null) throw new IllegalArgumentException("Byte array is null!");
    	
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
        	if (zipInputStream.getNextEntry() != null) { //There is always only one file in empyrion blueprints
        		result = zipInputStream.readAllBytes();
            }
        }
        
        return result;
    }

    private static List<String> extractNamesFromUnzippedData(byte[] data) {
        List<String> traderNames = new ArrayList<>();
        byte[] typePrefix = BLUEPRINT_TRADER_DATA_START_STRING.getBytes(StandardCharsets.UTF_8);

        for (int i = 0; i < data.length - typePrefix.length; i++) {
            if (matchesAt(data, i, typePrefix)) {
                int start = i + typePrefix.length;
                int end = start;

                while (end < data.length && data[end] != BLUEPRINT_TRADER_DATA_END_BYTE) {
                    end++;
                }

                if (end < data.length) {
                    byte[] nameBytes = Arrays.copyOfRange(data, start, end);
                    String name = new String(nameBytes, StandardCharsets.UTF_8);

                    if (!name.isBlank()) {
                        traderNames.add(name.trim());
                    }

                    i = end;
                }
            }
        }
        return traderNames;
    }

    private static boolean matchesAt(byte[] data, int offset, byte[] pattern) {
        for (int j = 0; j < pattern.length; j++) {
            if (data[offset + j] != pattern[j]) {
                return false;
            }
        }
        return true;
    }
	
}