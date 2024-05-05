package io.loqee.kairos.ui.fragments;

import androidx.fragment.app.Fragment;

public abstract class KairosFragment extends Fragment {
    public static String capitalizeFirstLetter(String sentence) {
        if (sentence == null || sentence.isEmpty()) {
            return sentence;
        }
        return sentence.substring(0, 1).toUpperCase() + sentence.substring(1);
    }
}
