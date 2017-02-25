package cdv.stb.rates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides currency rates data and refreshes it in background
 *
 * @author Dmitry Coolga
 *         05.02.2017 09:34
 */
public class CurrencyRateSource {

    static final String USD_RUB_PAIR = "USDRUB";
    static final String EUR_RUB_PAIR = "EURRUB";

    private static final String RATES_QUERY = "http://query.yahooapis.com/v1/public/yql?q=" +
            "select * from yahoo.finance.xchange where pair in " +
            "(\"" + USD_RUB_PAIR + "\", \"" + EUR_RUB_PAIR + "\")" +
            "&env=store://datatables.org/alltableswithkeys";

    private static final Logger log = LoggerFactory.getLogger(CurrencyRateSource.class);

    private volatile List<CurrencyRate> currentRates = Collections.emptyList();

    private final RestTemplate restTemplate;

    public CurrencyRateSource(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    @Scheduled(cron = "${app.rates.refresh.cron}")
    public void refresh() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    UriComponentsBuilder.fromHttpUrl(RATES_QUERY).build().encode().toUri(),
                    String.class);
            Document xml = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(response.getBody())));
            List<CurrencyRate> newRates = new ArrayList<>();
            newRates.add(parseRate(xml, USD_RUB_PAIR));
            newRates.add(parseRate(xml, EUR_RUB_PAIR));
            currentRates = newRates;
            log.info("Currency rates are refreshed: {}", currentRates);
        } catch (Exception ex) {
            log.error("Currency rates refresh failure", ex);
        }
    }

    public List<CurrencyRate> getCurrentRates() {
        return Collections.unmodifiableList(currentRates);
    }

    private CurrencyRate parseRate(Document document, String currencyPair) throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        String prefix = "/query/results/rate[@id='" + currencyPair + "']/";
        return new CurrencyRate(
                currencyPair,
                xpath.compile(prefix + "Rate").evaluate(document),
                xpath.compile(prefix + "Date").evaluate(document),
                xpath.compile(prefix + "Time").evaluate(document));
    }

}
