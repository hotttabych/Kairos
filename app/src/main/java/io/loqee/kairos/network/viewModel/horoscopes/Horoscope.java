package io.loqee.kairos.network.viewModel.horoscopes;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "horo", strict = false)
public class Horoscope {

    @Element(name = "aries")
    private Zodiac aries;

    @Element(name = "taurus")
    private Zodiac taurus;

    @Element(name = "gemini")
    private Zodiac gemini;

    @Element(name = "cancer")
    private Zodiac cancer;

    @Element(name = "leo")
    private Zodiac leo;

    @Element(name = "virgo")
    private Zodiac virgo;

    @Element(name = "libra")
    private Zodiac libra;

    @Element(name = "scorpio")
    private Zodiac scorpio;

    @Element(name = "sagittarius")
    private Zodiac sagittarius;

    @Element(name = "capricorn")
    private Zodiac capricorn;

    @Element(name = "aquarius")
    private Zodiac aquarius;

    @Element(name = "pisces")
    private Zodiac pisces;


    // Add other zodiac signs

    public Zodiac getAries() {
        return aries;
    }

    public Zodiac getTaurus() {
        return taurus;
    }

    public Zodiac getGemini() {
        return gemini;
    }

    public Zodiac getCancer() {
        return cancer;
    }

    public Zodiac getLeo() {
        return leo;
    }

    public Zodiac getVirgo() {
        return virgo;
    }

    public Zodiac getLibra() {
        return libra;
    }

    public Zodiac getScorpio() {
        return scorpio;
    }

    public Zodiac getSagittarius() {
        return sagittarius;
    }

    public Zodiac getCapricorn() {
        return capricorn;
    }

    public Zodiac getAquarius() {
        return aquarius;
    }

    public Zodiac getPisces() {
        return pisces;
    }

    // Add getters for other zodiac signs
}