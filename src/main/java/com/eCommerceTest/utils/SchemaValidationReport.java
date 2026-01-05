package com.eCommerceTest.utils;

import java.util.Collections;
import java.util.List;

/** Simple value object representing schema validation outcome. */
public final class SchemaValidationReport {
    private final boolean success;
    private final List<String> violations;
    private final String schemaPath;

    private SchemaValidationReport(boolean success, List<String> violations, String schemaPath) {
        this.success = success;
        this.violations = violations == null ? List.of() : List.copyOf(violations);
        this.schemaPath = schemaPath;
    }

    public static SchemaValidationReport passed(String path) { return new SchemaValidationReport(true, List.of(), path); }
    public static SchemaValidationReport failed(String path, List<String> violations) { return new SchemaValidationReport(false, violations, path); }

    public boolean isSuccess() { return success; }
    public List<String> getViolations() { return Collections.unmodifiableList(violations); }
    public String getSchemaPath() { return schemaPath; }

    @Override public String toString() { return "SchemaValidationReport{" + "success=" + success + ", violations=" + violations + ", schemaPath='" + schemaPath + '\'' + '}'; }
}

