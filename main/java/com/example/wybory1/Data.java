package com.example.wybory1;

import java.util.*;

public class Data {

    public final int rok;
    public final int miesiac;
    public final int dzien_miesiaca;

    public final int dzien_tygodnia;

    public static GregorianCalendar poczatek_kalendarza =
            new GregorianCalendar(1582, 10-1, 15);

    private static int[][] dni_w_miesiacu = {
            {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31},
            {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}
    };

    private static String[] miesiace = {"",
            "stycznia"  , "lutego"  , "marca"   , "kwietnia"    , "maja"     , "czerwca",
            "lipca"     , "sierpnia", "września", "października", "listopada", "grudnia"
    };

    private static final String[] tydzien = {"",
            "niedziela", "poniedziałek", "wtorek", "środa", "czwartek", "piątek", "sobota"
    };

    public Data() {
        GregorianCalendar gk = new GregorianCalendar();

        /* wykona się, jeżeli data w komputerze będzie ustawiona na jakąś średniowieczną,
           np. na 15 lipca 1410 roku ;-) */
        if (gk.compareTo(poczatek_kalendarza) < 0)
            throw new IllegalArgumentException("Data sprzed początku "
                    + "kalendarza gregoriańskiego!");

        this.rok = gk.get(gk.YEAR);
        this.miesiac = gk.get(gk.MONTH) + 1;
        this.dzien_miesiaca = gk.get(gk.DAY_OF_MONTH);

        this.dzien_tygodnia = gk.get(gk.DAY_OF_WEEK);

    }

    public Data(int rok, int miesiac, int dzien) {

        if (miesiac < 1 || miesiac > 12)
            throw new IllegalArgumentException("Miesiąc spoza zakresu!");

        if (dzien < 1 || dzien > dni_w_miesiacu[przestepny(rok)][miesiac])
            throw new IllegalArgumentException("Dzień miesiąca spoza zakresu!");

        GregorianCalendar gk = new GregorianCalendar(rok, miesiac - 1, dzien);
        if (gk.compareTo(poczatek_kalendarza) < 0)
            throw new IllegalArgumentException("Data sprzed początku "
                    + "kalendarza gregoriańskiego!");

        this.rok = rok;
        this.miesiac = miesiac;
        this.dzien_miesiaca = dzien;

        this.dzien_tygodnia = gk.get(gk.DAY_OF_WEEK);
    }

    /**
     * @param  r - rok
     * @return 0 - jeżeli rok jest zwykły
     *         1 - jeżeli rok jest przestępny
     */
    public static int przestepny(int r) {
        if (r % 4 != 0 || (r % 100 == 0 && r % 400 != 0))
            return 0;
        else
            return 1;
    }

    @Override
    public String toString() {
        return String.format("%s, %d %s %d roku",
                tydzien[dzien_tygodnia], dzien_miesiaca, miesiace[miesiac], rok);
    }

    @Override
    public boolean equals(Object d) throws ClassCastException {
        Data data = (Data) d;
        boolean wynik =     this.rok            == data.rok
                && this.miesiac        == data.miesiac
                && this.dzien_miesiaca == data.dzien_miesiaca;
        return wynik;
    }

    public int compareTo(Data data) {
        if (    this.rok <  data.rok
                || this.rok == data.rok && this.miesiac <  data.miesiac
                || this.rok == data.rok && this.miesiac == data.miesiac && this.dzien_miesiaca < data.dzien_miesiaca
        )
            return -1;

        else if (this.equals(data))
            return 0;

        else
            return 1;
    }

    /**
     *
     * @param data - argument reprezentujący kalendarzową datę
     * @return - obiekt reprezentujący datę o 1 dzień wcześniejszą
     * @throws IllegalArgumentException - wyjątek wyrzucamy w
     *          przypadku, gdyby zwrócona data miała być
     *          wcześniejsza od daty początkowej kalendarza
     *          gregoriańskiego (15 października 1582 roku)
     *
     * W celu większej czytelności kodu, zmienne lokalne
     * rk, mies, dzien mają inne nazwy niż odpowiadające
     * im pola rok, miesiac oraz dzien_miesiaca.
     */
    public Data wczoraj(Data data) throws IllegalArgumentException{

        int rk = data.rok;
        int mies = data.miesiac;
        int dzien = data.dzien_miesiaca;

        GregorianCalendar gk = new GregorianCalendar(rk, mies - 1, dzien);
        if (gk.compareTo(poczatek_kalendarza) <= 0)
            throw new IllegalArgumentException("Wcześniejsza data byłaby "
                    + "sprzed początku kalendarza gregoriańskiego "
                    + "(15.10.1582 r.)");

        dzien--;
        if(dzien == 0) {
            if (mies == 1)
                return new Data(rok-1, 12, 31);
            else {
                mies--;
                dzien = dni_w_miesiacu[przestepny(rk)][mies];
            }
        }
        return new Data(rk, mies, dzien);
    }

    /**
     * @param data - argument reprezentujący kalendarzową datę
     * @return - obiekt reprezentujący datę o 1 dzień późniejszą
     *
     * W celu większej czytelności kodu, zmienne lokalne
     * rk, mies, dzien mają inne nazwy niż odpowiadające
     * im pola rok, miesiac oraz dzien_miesiaca.
     */
    public Data jutro(Data data) {

        int rk = data.rok;
        int mies = data.miesiac;
        int dzien = data.dzien_miesiaca;

        dzien++;
        if(dni_w_miesiacu[przestepny(rk)][mies] < dzien) {
            dzien = 1;
            mies++;
        }
        if(mies > 12) {
            mies = 1;
            rk++;
        }
        return new Data(rk, mies, dzien);
    }

    public int ileDni(Data data1, Data data2) {
        int wynik = 0;
        if (data1.compareTo(data2) <= 0) {
            while (data1.equals(data2) == false) {
                data1 = data1.jutro(data1);
                wynik++;
            }
        } else {
            while (data1.equals(data2) == false) {
                data1 = data1.wczoraj(data1);
                wynik--;
            }
        }
        return wynik;
    }

    public static void main(String args[]) {

        System.out.println("Dziś jest " + new Data() + ".");

        System.out.println("\nPodaj dwie daty. Powinny być one podane w jednej linii i oddzielone białymi znakami");
        System.out.println("(spacjami lub tabulatorami). Format każdej z dat to dwucyfrowy dzień, kropka,");
        System.out.println("dwucyfrowy miesiąc, kropka i przynajmniej czterocyfrowy rok.");

        System.out.println("Linia poniżej - miejsce na wprowadzenie dat:");
        String linia = new Scanner(System.in).nextLine().trim();

        /*
            Poniżej, w wyrażeniu regularnym w if-ie, sprawdzam m.in. czy cyfra dziesiątek dnia jest
            większa od 3 i czy cyfra dziesiątek miesiąca jest większa od 1. Jeżeli tak - to od razu
            wiadomo, że data jest źle wprowadzona.
        */
        if(linia.matches("[0-3][0-9][.][0,1][0-9][.][0-9]{4,}[ ,\t]+[0-3][0-9][.][0,1][0-9][.][0-9]{4,}") == false) {
            System.err.println("Niewłaściwie wprowadzone daty!");
            return;
        }
        String[] daty = linia.split("[, \t]+");

        String[] data = daty[0].split("[.]");
        int dzien1    = Integer.parseInt(data[0]);
        int miesiac1  = Integer.parseInt(data[1]);
        int rok1      = Integer.parseInt(data[2]);

        data = daty[1].split("[.]");
        int dzien2   = Integer.parseInt(data[0]);
        int miesiac2 = Integer.parseInt(data[1]);
        int rok2     = Integer.parseInt(data[2]);

        System.out.print("\ndata pierwsza to: ");
        Data data1 = new Data(rok1, miesiac1, dzien1);
        System.out.println(data1);
        System.out.print("data druga to: ");
        Data data2 = new Data(rok2, miesiac2, dzien2);
        System.out.println(data2);

        System.out.print("\nWynik porównania: ");
        try {
            if (data1.equals(data2))
                System.out.println("te dwie daty to ten sam dzień.");
            else
                System.out.println("te dwie daty to różne dni.");
        } catch (ClassCastException ex) {
            ex.printStackTrace();
            System.err.println("Nie można porównać daty do czegoś, co nie jest datą!");
            return;
        }

        int porownanie = data1.compareTo(data2);
        switch (porownanie) {

            case -1: System.out.println("Data pierwsza jest wcześniejsza od daty drugiej.");
                break;

            case  0: break; // nie powtarzam na konsoli informacji, że to ten sam dzień

            case  1: System.out.println("Data pierwsza jest późniejsza od daty drugiej.");
                break;
        }

        try {
            System.out.print("\nDzień przed pierwszą datą to ");
            System.out.println(data1.wczoraj(data1) + ".");
            System.out.print("Dzień przed drugą datą to ");
            System.out.println(data2.wczoraj(data2) + ".");
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return;
        }
        System.out.println("Dzień po pierwszej dacie to " + data1.jutro(data1) + ".");
        System.out.println("Dzień po drugiej dacie to "   + data2.jutro(data2) + ".");

        System.out.println("\nMiędzy tymi dwiema datami jest różnica dni: "
                + data1.ileDni(data1, data2) + ".");
    }

}