package runners;

import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectDirectories;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectDirectories("target/test-classes/features")
@IncludeTags("reg")
@ExcludeTags({
    "deprecated",
    "ignore"
})
public class RegressionTestRunner {
}