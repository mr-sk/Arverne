import spock.lang.*
import arverne.ArverneModel

class ModelTest extends spock.lang.Specification {
    def "cdf-model-test"() {
        given: "a new Model class is created"
        def m = new ArverneModel();

        expect: "130 price, 103.60 strike 50 option will net 3"
        m.execute(ArverneModel.Models.CDF, 103.00, 103.06, 50.00) == 3

        and: "130 price, 103.60 strike 50 option will net 23"
        m.execute(ArverneModel.Models.CDF, 103.00, 103.06, 30.00) == 23
    }

    def "unknown-model-test"() {
        given: "an unknown model is executed"
        def m = new ArverneModel();

        when: "A RuntimeException is thrown"
        m.execute('jibberish', 100.00, 100.01, 10.00)

        then:
        thrown(RuntimeException)
    }
}