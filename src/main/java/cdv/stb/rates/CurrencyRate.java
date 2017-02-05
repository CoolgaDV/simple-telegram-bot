package cdv.stb.rates;

/**
 * POJO with currency rate information
 *
 * @author Dmitry Coolga
 *         05.02.2017 10:20
 */
public class CurrencyRate {

    private final String pair;
    private final String ratio;
    private final String date;
    private final String time;

    CurrencyRate(String pair, String ratio, String date, String time) {
        this.pair = pair;
        this.ratio = ratio;
        this.date = date;
        this.time = time;
    }

    public String getPair() {
        return pair;
    }

    public String getRatio() {
        return ratio;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "CurrencyRate{" +
                "pair='" + pair + '\'' +
                ", ratio='" + ratio + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

}
