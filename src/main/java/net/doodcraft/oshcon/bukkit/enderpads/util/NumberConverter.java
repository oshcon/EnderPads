package net.doodcraft.oshcon.bukkit.enderpads.util;

import net.doodcraft.oshcon.bukkit.enderpads.config.Settings;

public class NumberConverter {

    public static String convert(final int n) {

        if (n < 0) {
            return Settings.numbersNegative + " " + convert(-n);
        }

        if (n < 20) {
            return units[n];
        }

        if (n < 100) {
            return tens[n / 10] + ((n % 10 != 0) ? " " : "") + units[n % 10];
        }

        if (n < 1000) {
            return units[n / 100] + " " + Settings.numbersHundred + ((n % 100 != 0) ? " " : "") + convert(n % 100);
        }

        if (n < 1000000) {
            return convert(n / 1000) + " " + Settings.numbersThousand + ((n % 1000 != 0) ? " " : "") + convert(n % 1000);
        }

        if (n < 1000000000) {
            return convert(n / 1000000) + " " + Settings.numbersMillion + ((n % 1000000 != 0) ? " " : "") + convert(n % 1000000);
        }

        return convert(n / 1000000000) + " " + Settings.numbersBillion + ((n % 1000000000 != 0) ? " " : "") + convert(n % 1000000000);
    }

    private static final String[] units = {
            Settings.numbersZero,
            Settings.numbersOne,
            Settings.numbersTwo,
            Settings.numbersThree,
            Settings.numbersFour,
            Settings.numbersFive,
            Settings.numbersSix,
            Settings.numbersSeven,
            Settings.numbersEight,
            Settings.numbersNine,
            Settings.numbersTen,
            Settings.numbersEleven,
            Settings.numbersTwelve,
            Settings.numbersThirteen,
            Settings.numbersFourteen,
            Settings.numbersFifteen,
            Settings.numbersSixteen,
            Settings.numbersSeventeen,
            Settings.numbersEighteen,
            Settings.numbersNineteen
    };

    private static final String[] tens = {
            "",
            "",
            Settings.numbersTwenty,
            Settings.numbersThirty,
            Settings.numbersForty,
            Settings.numbersFifty,
            Settings.numbersSixty,
            Settings.numbersSeventy,
            Settings.numbersEighty,
            Settings.numbersNinety
    };
}