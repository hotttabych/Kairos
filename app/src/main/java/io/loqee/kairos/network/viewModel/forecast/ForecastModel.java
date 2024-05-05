package io.loqee.kairos.network.viewModel.forecast;

public class ForecastModel {
    private final int id;
    private final String dateTime;
    private final String weather;
    private final String temperature;
    private final String feelsLike;
    private final String pressure;
    private final String windSpeed;
    private final String humidity;

    public ForecastModel(int id, String dateTime, String weather, String temperature, String feelsLike, String pressure, String windSpeed, String humidity) {
        this.id = id;
        this.dateTime = dateTime;
        this.weather = weather;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
    }

    public int getId() {
        return id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getWeather() {
        return weather;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public String getPressure() {
        return pressure;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getHumidity() {
        return humidity;
    }

}
