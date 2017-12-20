package cms.domain.features

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("classpath:features"),
  glue = Array("classpath:cms.domain.features.steps"),
  plugin = Array("pretty")
)
class FeaturesTestRunner {}
