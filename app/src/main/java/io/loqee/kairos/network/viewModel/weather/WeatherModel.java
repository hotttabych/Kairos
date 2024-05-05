package io.loqee.kairos.network.viewModel.weather;

public class WeatherModel {
    private final String weather;
    private final String temperature;
    private final String feelsLike;
    private final String pressure;
    private final String windSpeed;
    private final String humidity;
    private final int sunrise;
    private final int sunset;

    public WeatherModel(String weather, String temperature, String feelsLike, String pressure, String windSpeed, String humidity, int sunrise, int sunset) {
        this.weather = weather;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.sunrise = sunrise;
        this.sunset = sunset;
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

    public int getSunrise() {
        return sunrise;
    }

    public int getSunset() {
        return sunset;
    }
}
