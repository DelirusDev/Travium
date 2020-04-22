package delirusdev.travium;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;

class Common {

    static Map<String, String> readSettings(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = objectMapper.readValue(file, new TypeReference<Map<String, String>>(){});
        return map;
    }

    static int[][] readFarmlist(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);
        ObjectMapper objectMapper = new ObjectMapper();
        int[][] map = objectMapper.readValue(file, new TypeReference<int[][]>(){});
        return map;
    }

    static String[] readTroopNames(String filename, String tribe) throws FileNotFoundException, IOException {
        File file = new File(filename);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String[]> map = objectMapper.readValue(file, new TypeReference<Map<String, String[]>>(){});
        return map.get(tribe);
    }

    static String timeNow() {
        LocalTime now = LocalTime.now();
        return now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

}