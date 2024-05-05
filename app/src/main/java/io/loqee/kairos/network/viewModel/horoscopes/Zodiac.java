package io.loqee.kairos.network.viewModel.horoscopes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "aries", strict = false)
public class Zodiac {

    @Element(name = "yesterday")
    private String yesterday;

    @Element(name = "today")
    private String today;

    @Element(name = "tomorrow")
    private String tomorrow;

    @Element(name = "tomorrow02")
    private String tomorrow02;

    public String getYesterday() {
        return yesterday;
    }

    public String getToday() {
        return today;
    }

    public String getTomorrow() {
        return tomorrow;
    }

    public String getTomorrow02() {
        return tomorrow02;
    }

    // getters and setters
}