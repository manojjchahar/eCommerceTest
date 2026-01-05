package com.eCommerceTest.utils;

import io.restassured.response.Response;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility for validating JSON responses against JSON schema files.
 * Uses everit-json-schema for comprehensive validation.
 */
public final class SchemaValidator {

    private SchemaValidator() {}

    /**
     * Validate a RestAssured response against a schema file
     * @param response The API response to validate
     * @param schemaPath Path to schema file (e.g., "/schemas/userSchema.json")
     * @return true if valid, false otherwise
     */
    public static boolean validateResponse(Response response, String schemaPath) {
        return validate(response, schemaPath).isSuccess();
    }

    /**
     * Validate JSON string against schema
     */
    public static boolean validateJson(String jsonString, String schemaPath) {
        return validateRaw(jsonString, schemaPath).isSuccess();
    }

    /** New richer validation returning violations list */
    public static SchemaValidationReport validate(Response response, String schemaPath) {
        if (response == null) return SchemaValidationReport.failed(schemaPath, List.of("Response is null"));
        return validateRaw(response.getBody().asString(), schemaPath);
    }

    public static SchemaValidationReport validateRaw(String jsonString, String schemaPath) {
        try {
            Schema schema = loadSchema(schemaPath);
            JSONObject json = new JSONObject(jsonString);
            schema.validate(json);
            return SchemaValidationReport.passed(schemaPath);
        } catch (ValidationException ve) {
            List<String> violations = new ArrayList<>();
            violations.add(ve.getMessage());
            ve.getCausingExceptions().forEach(ex -> violations.add(ex.getMessage()));
            return SchemaValidationReport.failed(schemaPath, violations);
        } catch (Exception e) {
            return SchemaValidationReport.failed(schemaPath, List.of("Error: " + e.getMessage()));
        }
    }

    private static Schema loadSchema(String schemaPath) {
        InputStream schemaStream = SchemaValidator.class.getResourceAsStream(schemaPath);
        if (schemaStream == null) {
            throw new IllegalArgumentException("Schema file not found: " + schemaPath);
        }
        JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
        return SchemaLoader.load(rawSchema);
    }
}