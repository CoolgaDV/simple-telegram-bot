package cdv.stb.rates;

import cdv.stb.telegram.TelegramApiClient;
import cdv.stb.common.Trigger;
import cdv.stb.common.TriggerWithSubscription;
import cdv.stb.telegram.protocol.Message;

import java.util.List;

/**
 * Trigger that provides currency rates data.
 *
 * @author Dmitry Coolga
 *         05.02.2017 11:08
 */
public class CurrencyRatesTrigger implements Trigger, TriggerWithSubscription {

    private final CurrencyRateSource currencyRateSource;
    private final TelegramApiClient apiClient;

    public CurrencyRatesTrigger(CurrencyRateSource currencyRateSource,
                                TelegramApiClient apiClient) {
        this.currencyRateSource = currencyRateSource;
        this.apiClient = apiClient;
    }

    @Override
    public boolean match(Message message) {
        return "Курс валют".equals(message.getText());
    }

    @Override
    public void fire(Message message) {
        sendCurrenciesMessage(message.getChat().getId());
    }

    @Override
    public void handleSubscriptionDelivery(long chatId) {
        sendCurrenciesMessage(chatId);
    }

    private void sendCurrenciesMessage(long chatId) {

        List<CurrencyRate> rates = currencyRateSource.getCurrentRates();

        String message = rates.isEmpty() ? "Данные по курсам валют недоступны..." :
                formatCurrencyRate(CurrencyRateSource.USD_RUB_PAIR, "Доллар", rates) + "\n" +
                formatCurrencyRate(CurrencyRateSource.EUR_RUB_PAIR, "Евро", rates);

        apiClient.sendMessage(message, chatId);
    }

    private String formatCurrencyRate(String currencyPair,
                                      String localizedCurrencyPairName,
                                      List<CurrencyRate> rates) {
        return rates.stream()
                .filter(rate -> currencyPair.equals(rate.getPair()))
                .map(rate -> localizedCurrencyPairName + ": " + rate.getRatio())
                .findAny()
                .orElse("данные не доступны");
    }

}
