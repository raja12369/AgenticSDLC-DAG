package com.exam.agenticsdlc.specs;

import java.util.List;

public class TestSpec extends BaseSpec {

    private List<TestCase> unitTests;
    private List<TestCase> integrationTests;
    private int coverageEstimate;
    @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.exam.agenticsdlc.util.FlexibleStringListDeserializer.class)
    private List<String> missingTests;

    public List<TestCase> getUnitTests() { return unitTests; }
    public void setUnitTests(List<TestCase> unitTests) { this.unitTests = unitTests; }

    public List<TestCase> getIntegrationTests() { return integrationTests; }
    public void setIntegrationTests(List<TestCase> integrationTests) { this.integrationTests = integrationTests; }

    public int getCoverageEstimate() { return coverageEstimate; }
    public void setCoverageEstimate(int coverageEstimate) { this.coverageEstimate = coverageEstimate; }

    public List<String> getMissingTests() { return missingTests; }
    public void setMissingTests(List<String> missingTests) { this.missingTests = missingTests; }
}
