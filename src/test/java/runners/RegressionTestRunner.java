package runners;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@IncludeTags("reg")
@ExcludeTags({
    "deprecated",
    "ignore"
})
@ConfigurationParameter(key = "cucumber.plugin", value = "json:target/cucumber-reports/cucumber.json,html:target/cucumber-reports/cucumber-html.html")
public class RegressionTestRunner {
}