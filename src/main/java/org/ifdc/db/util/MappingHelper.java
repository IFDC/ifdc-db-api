package org.ifdc.db.util;

import au.com.bytecode.opencsv.CSVReader;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Meng Zhang
 */
public class MappingHelper {

    private static final String ICASA_PATH = "https://docs.google.com/spreadsheets/d/1MYx1ukUsCAM1pcixbVQSu49NU-LfXg-Dtt-ncLBzGAM/pub?output=csv";
    private static final String ICASA_VAR_NAME = "Variable_Name";
    private static final String ICASA_VAR_CODE = "Code_Display";
    private static final String ICASA_DESCRIPTION = "Description";
    private static final HashSet<String> ICASA_USED = new HashSet(Arrays.asList(new String[]{ICASA_VAR_CODE, ICASA_VAR_NAME, "Description", "Unit_or_type", "Dataset", "Subset", "Group", "Set_group_order"}));
    private static final HashSet<String> ICASA_IGNORE_VAR_NAMES = new HashSet(Arrays.asList(new String[]{"id", "people_level", "soil_analysis_level", "initial_conditions_level", "planting_level", "planting_level_name", "irrigation_level", "irrigation_level_name", "fertilizer_level", "fertilizer_level_name", "fertilizer_notes", "org_materials_applic_lev", "mulch_level", "mulch_level_name", "chemical_applic_level", "tillage_level", "environmental_modif_lev", "harvest_operations_level", "harvest_ops_level_name", "SA_id"}));

    // Sync Mapping from ICASA to DB
    public static void syncIcasaToDB() {

    }

    public static HashMap<String, String> readIcasaToJsonMap() {
        return readIcasaToJsonMap(false);
    }

    public static HashMap<String, String> readIcasaToJsonMap(boolean extractKeyWords) {
        HashMap<String, String> ret = new HashMap();
        URL resource;
        try {
            resource = new URL(ICASA_PATH);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return ret;
        }
        JsonFactory jsonfactory = new JsonFactory();
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(resource.openStream()));
                CSVReader reader = new CSVReader(br, ',')) {

            String[] titles = reader.readNext();
            int varNameIdx = -1;
            int varCodeIdx = -1;
            int descIdx = -1;
            if (titles == null) {
                return ret;
            } else {
                for (int i = 0; i < titles.length; i++) {
                    if (!ICASA_USED.contains(titles[i])) {
                        titles[i] = "";
                    } else if (ICASA_VAR_NAME.equals(titles[i])) {
                        varNameIdx = i;
                    } else if (ICASA_VAR_CODE.equals(titles[i])) {
                        varCodeIdx = i;
                    } else if (extractKeyWords && ICASA_DESCRIPTION.equals(titles[i])) {
                        descIdx = i;
                    }
                }
            }
            if (varNameIdx < 0 || varCodeIdx < 0) {
                return ret;
            }

            String[] line;
            while ((line = reader.readNext()) != null) {
                try (
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        JsonGenerator generator = jsonfactory.createGenerator(baos);) {

                    generator.writeStartObject();
                    int bond = Math.min(titles.length, line.length);
                    for (int i = 0; i < bond; i++) {
                        if (!titles[i].isEmpty() && !ICASA_IGNORE_VAR_NAMES.contains(titles[varNameIdx])) {
                            generator.writeStringField(titles[i].toLowerCase(), line[i]);
                        }
                    }
                    if (descIdx > 0 && descIdx < bond) {
                        generator.writeFieldName("keywords");
//                        generator.writeStartArray();
                        generator.writeRawValue(getKeywords(line[descIdx]).toString());
//                        generator.writeEndArray();
                    }
                    generator.writeEndObject();
                    generator.flush();
                    ret.put(line[varCodeIdx], new String(baos.toByteArray()));
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public static ArrayList<String> readIcasaToJsonList() {
        ArrayList<String> ret = new ArrayList(readIcasaToJsonMap().values());
        return ret;
    }

    // Read Mapping Info
    // Sync Mapping from DB
    // Mapping service
    public static ArrayList<String> resolveMapping(ArrayList<String> titles, HashMap<String, Object> mappings) {
        return titles;
    }

    public static ArrayList<String> getKeywords(String description) {
        ArrayList<String> ret = new ArrayList();
        String[] keys = description.replaceAll("\\.$", "").split("[ ,\"()=]");
        HashSet<String> ignores = new HashSet(Arrays.asList(new String[]{"when", "where", "whether", "which", "who", "from", "to", "in", "if", "over", "of", "by", "on", "at", "as", "with", "within", "etc", "etc.", "conc.", "conc", "for", "or", "and", "e.g.", "eg.", "e.g,", "the", "a", "an", "that", "this", "these", "those", "it", "they", "them", "is", "are", "was", "were", "-99"}));
        String ignoreRule = "\\d+";
        String ignoreRule2 = "eg.";
        for (String key : keys) {
            if (!key.isEmpty() && !ignores.contains(key) && !key.matches(ignoreRule)) {
                if (key.startsWith(ignoreRule2)) {
                    key = key.replaceFirst("eg.", "");
                }
                ret.add("\"" + key.toLowerCase() + "\"");
            }
        }
        return ret;
    }
}
