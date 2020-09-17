package com.example.wybory1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class Controler {

    private WyborcaRepo wyborcaRepo;

    static Date pocz1tury = null;
    static Date koniec1tury = null;
    static Date pocz2tury = null;
    static Date koniec2tury = null;

    static boolean brak_drugiej_tury = false;
    static boolean mozna_zarzadzic_wybory = true;
    static boolean mozna_zmieniac_liste_kandydatow = false;
    static boolean trwa_glosowanie = false;
    static boolean mozna_odwolac_wybory = true;
    static boolean przyszykowana_druga_tura = false; // wyborcy mogą zagłosować

    static Double frekwencja1tura = 0.0;
    static Double frekwencja2tura = 0.0;

    private static int[][] dni_w_miesiacu = {
            {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31},
            {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}
    };

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

    public static boolean wlasciweParametryCzasu(int rok1, int miesiac1, int dzien1, int godzina1, int minuta1,
                                                 int rok2, int miesiac2, int dzien2, int godzina2, int minuta2,
                                                 int trwanie)
    {
        if (miesiac1 < 1 || miesiac1 > 12 || miesiac2 < 1 || miesiac2 > 12) {
            System.out.println("if nr 1");
            return false;
        }
        if (dzien1 < 1 || dzien1 > dni_w_miesiacu[przestepny(rok1)][miesiac1]) {
            System.out.println("if nr 2");
            return false;
        }
        if (dzien2 < 1 || dzien2 > dni_w_miesiacu[przestepny(rok2)][miesiac2]) {
            System.out.println("if nr 3");
            return false;
        }
        if (godzina1 < 0 || godzina1 > 23 || godzina2 < 0 || godzina2 > 23) {
            System.out.println("if nr 4");
            return false;
        }
        if (minuta1 < 0 || minuta1 > 59 || minuta2 < 0 || minuta2 > 59) {
            System.out.println("if nr 5");
            return false;
        }
        if (trwanie < 1 || trwanie > 1440) {
            System.out.println("if nr 6");
            return false;
        }
        return true;

    }

    private static void wyzerujTerminy() {
        System.out.println("Zeruję terminy.");
        pocz1tury = null;
        koniec1tury = null;
        pocz2tury = null;
        koniec2tury = null;
        frekwencja1tura = 0.0;
        frekwencja2tura = 0.0;
    }

    public String skontrolujCzas() {

        System.out.println("początek kontroli czasu");
        System.out.println("początek pierwszej tury: " + pocz1tury);
        System.out.println("koniec pierwszej tury: " + koniec1tury);
        System.out.println("początek drugiej tury: " + pocz2tury);
        System.out.println("koniec drugiej tury: " + koniec2tury);
        System.out.println("");
        System.out.println("teraz: " + new Date());

        Date teraz = new Date();
        List<Wyborca> wyborcy = wyborcaRepo.findAll();
        String wynik = "";
        if (pocz1tury == null) {
            for (Wyborca wyborca : wyborcy) {
                wyborca.setMozeZaglosowac(true);
            }
            wynik = "brak zarządzonych wyborów";
            mozna_zarzadzic_wybory = true;
            mozna_zmieniac_liste_kandydatow = false;
            trwa_glosowanie = false;
            mozna_odwolac_wybory = false;
            przyszykowana_druga_tura = false; // wyborcy mogą zagłosować
            brak_drugiej_tury = false;
        } else if (teraz.before(pocz1tury)) {
            wynik = "przed pierwszą turą";
            mozna_zarzadzic_wybory = false;
            mozna_zmieniac_liste_kandydatow = true;
            trwa_glosowanie = false;
            mozna_odwolac_wybory = true;
            przyszykowana_druga_tura = false;
            brak_drugiej_tury = false;
        } else if (teraz.before(koniec1tury)) {
            mozna_zarzadzic_wybory = false;
            mozna_zmieniac_liste_kandydatow = false;
            trwa_glosowanie = true;
            mozna_odwolac_wybory = false;
            przyszykowana_druga_tura = false;
            brak_drugiej_tury = false;
            wynik = "podczas pierwszej tury";
        } else if (pocz2tury != null && teraz.before(pocz2tury)) {
            mozna_zarzadzic_wybory = false;
            mozna_zmieniac_liste_kandydatow = false;
            trwa_glosowanie = false;
            mozna_odwolac_wybory = true;
            // przyszykowana_druga_tura = false;
            // brak_drugiej_tury = false;
            wynik = "przed drugą turą";
        } else if (pocz2tury != null && teraz.before(koniec2tury)) {
            wynik = "podczas drugiej tury";
            mozna_zarzadzic_wybory = false;
            mozna_zmieniac_liste_kandydatow = false;
            trwa_glosowanie = true;
            mozna_odwolac_wybory = false;
            // przyszykowana_druga_tura = false;
            // brak_drugiej_tury = false;
        } else { // pocz2tury == null || teraz.after(koniec2tury)
            wynik = "po wyborach";
            mozna_zarzadzic_wybory = true;
            mozna_zmieniac_liste_kandydatow = false;
            trwa_glosowanie = false;
            mozna_odwolac_wybory = false;
            przyszykowana_druga_tura = false;
            // brak_drugiej_tury = false;
        }

        if (brak_drugiej_tury == false
            && przyszykowana_druga_tura == false
            && koniec1tury != null
            && teraz.after(koniec1tury)
            && teraz.before(koniec2tury))
        {
            for (Wyborca wyborca : wyborcy) {
                wyborca.setMozeZaglosowac(true);
                wyborcaRepo.save(wyborca);
            }

            int suma_glosow = 0;
            List<Wyborca> kandydaci = new ArrayList<Wyborca>();
            for(Wyborca wyborca : wyborcy) {
                if (wyborca.isKandyduje() == true) {
                    kandydaci.add(wyborca);
                    suma_glosow += wyborca.getGlosow();
                }
            }
            kandydaci = this.posegreguj_kandydatow(kandydaci);
            int max_glosow = kandydaci.get(0).getGlosow();
            if (max_glosow * 2 > suma_glosow) {
                brak_drugiej_tury = true;
                pocz2tury = null;
                koniec2tury = null;
            } else {
                kandydaci.get(0).setKandyduje2tura(true);
                kandydaci.get(1).setKandyduje2tura(true);
                wyborcaRepo.save(kandydaci.get(0));
                wyborcaRepo.save(kandydaci.get(1));
            }
            przyszykowana_druga_tura = true;
        }

        return wynik;
    }

    /**
     *
     * @param i
     * @return lista z losową permutacją liczb od zera do i-1
     */
    public List<Integer> losowa_permutacja(int i) {
        List<Integer> wynik = new ArrayList<Integer>();
        List<Integer> porzadek = new ArrayList<Integer>();
        for(int j = 0; j < i; j++)
            porzadek.add(j);
        Random generator = new Random();
        for(int j = 0; j <i; j++) {
            int k = generator.nextInt(i-j);
            int l = porzadek.get(k);
            porzadek.remove(k);
            wynik.add(l);
        }
        return wynik;
    }

    /**
     * Metoda ma za zadanie uszeregować (metodą sortowanie bąbelkowego)
     * kandydatów w kolejności od tego, który zdobył najwięcej głosów,
     * do tego, który zdobył ich najmniej.
     * Początkowe losowe przestawienie kandydatów służy temu, żeby
     * w przypadku tej samej ilości głosów u więcej niż jednego
     * kandydata, ich kolejność w liście końcowej była losowa.
     *
     * @param kandydaci
     * @return
     */
    public List<Wyborca> posegreguj_kandydatow(List<Wyborca> kandydaci) {
        int ilosc_kandydatow = kandydaci.size();
        List<Integer> losowa_perm = this.losowa_permutacja(ilosc_kandydatow);
        List<Wyborca> kandydaci_przestawieni = new ArrayList<Wyborca>();
        for(Integer i : losowa_perm)
            kandydaci_przestawieni.add(kandydaci.get(i));
        int ilosc_przestawien = 0;
        Wyborca tymczasowy = null;
        Wyborca kandydat1 = null;
        Wyborca kandydat2 = null;
        do {
            ilosc_przestawien = 0;
            for (int i = 0; i < ilosc_kandydatow - 1; i++) {
                kandydat1 = kandydaci_przestawieni.get(i);
                kandydat2 = kandydaci_przestawieni.get(i+1);
                if (kandydat1.getGlosow() < kandydat2.getGlosow()) {
                    tymczasowy = kandydat1;
                    kandydat1 = kandydat2;
                    kandydat2 = tymczasowy;
                    ilosc_przestawien++;
                }
            }
        } while (ilosc_przestawien > 0);

        return kandydaci_przestawieni;
    }


    @Autowired
    public Controler(WyborcaRepo wyborcaRepo) {
        this.wyborcaRepo = wyborcaRepo;
    }

    @RequestMapping ("/")
    public String glowna() {

        System.out.println(new Date());

        wyzerujTerminy(); // na wszelki wypadek

        // zerowanie ilości głosów dla wszystkich
        List<Wyborca> wyborcy = wyborcaRepo.findAll();
        for (Wyborca wyborca : wyborcy) {
            wyborca.setMozeZaglosowac(true);
            wyborca.setKandyduje(false);
            wyborca.setGlosow(0);
            wyborca.setGlosow2tura(0);
            wyborcaRepo.save(wyborca);
        }
        return "start" ; }

    @RequestMapping ("/start2")
    public String start2() {
        return "start";
    }

    @RequestMapping("/lista")
    public String lista( Model model){
        model.addAttribute("wyborca", wyborcaRepo.findAll());
        return "lista";
    }

    @RequestMapping("/glosowanie")
    public String glosowanie( Model model){

        String wynik = this.skontrolujCzas();
        List<Wyborca> wyborcy = wyborcaRepo.findAll();
        List<Wyborca> kandydaci = new ArrayList<Wyborca>();

        if (wynik.equals("podczas pierwszej tury")) {
            for(Wyborca wyborca : wyborcy) {
                if (wyborca.isKandyduje() == true) {
                    kandydaci.add(wyborca);
                }
            }
            model.addAttribute("kandydat", kandydaci);
            return "glosowanie";
        } else if (wynik.equals("podczas drugiej tury")) {
            for(Wyborca wyborca : wyborcy) {
                if (wyborca.isKandyduje2tura() == true) {
                    kandydaci.add(wyborca);
                }
            }
            model.addAttribute("kandydat", kandydaci);
            return "glosowanie2tura";
        }
        else {
            return "blad_glosowanieNieTrwa";
        }
    }


    /**
     * 1. Jeżeli są niezarządzone wybory lub jest przed końcem pierwszej tury,
     *      to nie ma wyników.
     * 2. Jeżeli jest po pierwszej turze, a przed końcem drugiej (i ma być druga tura),
     *      to pokaż wyniki pierwszej tury.'
     * 3. Po drugiej turze (i przed zarządzeniem następnych wyborów)
     *      pokaż wyniki drugiej tury
     * 4. Po pierwszej turze wyborów
     *      (a) sprawdź, czy ktoś dostał więcej niż połowę wszystkich głosów.
     *      (b) jeżeli tak,
     *          to wyzeruj termin drugiej tury
     *          i pokaż wyniki pierwszej tury
     *      (c) jeżeli nie, to ustal dwóch kandydatów z największą ilością głosów.
     *
     * @param model
     * @return
     */
    @RequestMapping("/wyniki")
    public String wyniki( Model model){

        // zobaczyć można po pierwszej turze wyborów.
        String wynik = this.skontrolujCzas();
        if (    wynik.equals("brak zarządzonych wyborów")
             || wynik.equals("przed pierwszą turą")
             || wynik.equals("podczas pierwszej tury")
           )
        {
            return "blad_brakWyniku";
        }

        List<Wyborca> wyborcy = wyborcaRepo.findAll();
        List<Wyborca> kandydaci = new ArrayList<>();
        List<Wyborca> kandydaci2tura = new ArrayList<>();
        for(Wyborca wyborca : wyborcy) {
            if (wyborca.isKandyduje() == true) {
                kandydaci.add(wyborca);
            }
            if (wyborca.isKandyduje2tura() == true) {  kandydaci2tura.add(wyborca); }
        }
        model.addAttribute("kandydat", kandydaci);

        if ((wynik.equals("po wyborach") && brak_drugiej_tury == false)) {
            model.addAttribute("kandydat2tura", kandydaci2tura);
        }
        return "wynik";
    }

    @RequestMapping("/dodaj")
    public String dodajWyborce(
            @RequestParam("imie") String imie,
            @RequestParam("nazwisko") String nazwisko,
            @RequestParam("login") String login,
            Model model)
            throws Exception {

        System.out.println("Początek metody dodaj wyborcę.");
        Wyborca wyb = new Wyborca(imie, nazwisko, login, "123", true, false, false, 0, 0, true);
        wyborcaRepo.save(wyb);
        model.addAttribute("wyborca", wyborcaRepo.findAll());
        return "lista";
    }

    @RequestMapping("/kasuj")
    public String usunWyborce(@RequestParam("id") Integer id, Model model){
        wyborcaRepo.deleteById(id);
        model.addAttribute("wyborca", wyborcaRepo.findAll());
        return "lista";
    }

    @RequestMapping("/wylon")
    public String wylon(@RequestParam("id") Integer id, Model model){

        // wyłaniać kandydatów można dopiero po zarządzeniu wyborów,
        // ale przed ich rozpoczęciem
        this.skontrolujCzas();
        if(mozna_zmieniac_liste_kandydatow) {
            List<Wyborca> listaWyborcow = wyborcaRepo.findAll();
            for(Wyborca wyborca : listaWyborcow) {
                if(wyborca.getId().equals(id)) {
                    boolean czyKandyduje = wyborca.isKandyduje();
                    wyborca.setKandyduje(!czyKandyduje);
                    wyborcaRepo.save(wyborca);
                    break;
                }
            }
            model.addAttribute("wyborca", listaWyborcow);
            return "lista";
        } else {
            return "blad_wylanianiaKandydata";
        }
    }

    @RequestMapping("/glosuj")
    public String glosuj(@RequestParam("id") Integer id, Model model){

        String wynik = this.skontrolujCzas();
        if (!trwa_glosowanie) {
            return "blad_glosowanieNieTrwa";
        } else {
            List<Wyborca> listaWyborcow = wyborcaRepo.findAll();
            Wyborca kandydat = null;
            List<Wyborca> kandydaci = new ArrayList<Wyborca>();
            for (Wyborca wyborca : listaWyborcow) {
                if (wyborca.getId() == id) {
                    kandydat = wyborca;
                    break;
                }
            }
            model.addAttribute("kandydat", kandydat);
            if (wynik.equals("podczas pierwszej tury")) {
                return "zatwierdz";
            } else {
                return "zatwierdz2tura";
            }
        }
    }

    @RequestMapping("/dolicz")
    public String dolicz(@RequestParam("id") Integer id,
                         @RequestParam("login") String login,
                         @RequestParam("haslo") String haslo,
                         Model model){

        Date teraz = new Date();
        if(teraz.after(koniec1tury)) {
            return "blad_glosowanieNieTrwa";
        }

        List<Wyborca> listaWyborcow = wyborcaRepo.findAll();

        for(Wyborca wyborca : listaWyborcow) {
            if(wyborca.getLogin().equals(login)) {
                if(!wyborca.getHaslo().equals(haslo)) {
                   return "blad_zleHaslo";
                }
                else if (wyborca.isMozeZaglosowac() == false) {
                    return "blad_ponowneGlosowanie";
                } else {
                    // znalezienie w bazie danych kandydata, któremu należy doliczyć głos
                    Wyborca kandydat = null;
                    for(Wyborca kand : listaWyborcow) {
                        if (kand.getId().equals(id)) {
                            kandydat = kand;
                            break;
                        }
                    }
                    // doliczenie głosu dla określonego kandydata
                    Integer iloscGlosow = kandydat.getGlosow();
                    kandydat.setGlosow(++iloscGlosow);
                    wyborcaRepo.save(kandydat);

                    // ustawienie informacji w bazie danych, że wyborca już zagłosował
                    wyborca.setMozeZaglosowac(false);
                    wyborcaRepo.save(wyborca);

                    // pobieranie listy kandydatów z ilością głosów
                    List<Wyborca> kandydaci = new ArrayList<Wyborca>();
                    int glosujacych = 0;
                    for(Wyborca wyb : listaWyborcow) {
                        if (wyb.isKandyduje() == true) {
                            kandydaci.add(wyb);
                        }
                        if(wyb.isMozeZaglosowac() == false) {
                            glosujacych++;
                        }
                    }
                    int uprawnionych = listaWyborcow.size();
                    frekwencja1tura = (double)glosujacych * 100 / (double)uprawnionych;
                    model.addAttribute("kandydat", kandydaci);
                    return "start";
                }
            }
        }
        model.addAttribute("wyborca", listaWyborcow);
        return "blad_brakLoginu";
    }

    @RequestMapping("/dolicz2tura")
    public String dolicz2tura(@RequestParam("id") Integer id,
                         @RequestParam("login") String login,
                         @RequestParam("haslo") String haslo,
                         Model model){

        this.skontrolujCzas();
        if(!trwa_glosowanie) {
            return "blad_glosowanieNieTrwa";
        }

        List<Wyborca> listaWyborcow = wyborcaRepo.findAll();

        for(Wyborca wyborca : listaWyborcow) {
            if(wyborca.getLogin().equals(login)) {
                if(!wyborca.getHaslo().equals(haslo)) {
                    return "blad_zleHaslo";
                }
                else if (wyborca.isMozeZaglosowac() == false) {
                    return "blad_ponowneGlosowanie";
                } else {
                    // znalezienie w bazie danych kandydata, któremu należy doliczyć głos
                    Wyborca kandydat = null;
                    for(Wyborca kand : listaWyborcow) {
                        if (kand.getId().equals(id)) {
                            kandydat = kand;
                            break;
                        }
                    }
                    // doliczenie głosu dla określonego kandydata
                    Integer iloscGlosow = kandydat.getGlosow2tura();
                    kandydat.setGlosow2tura(++iloscGlosow);
                    wyborcaRepo.save(kandydat);

                    // ustawienie informacji w bazie danych, że wyborca już zagłosował
                    wyborca.setMozeZaglosowac(false);
                    wyborcaRepo.save(wyborca);

                    // pobieranie listy kandydatów z ilością głosów
                    int glosujacych = 0;
                    List<Wyborca> kandydaci = new ArrayList<Wyborca>();
                    for(Wyborca wyb : listaWyborcow) {
                        if (wyb.isKandyduje2tura() == true) { kandydaci.add(wyb);}
                        if(wyb.isMozeZaglosowac() == false) {
                            glosujacych++;
                        }
                    }
                    int uprawnionych = listaWyborcow.size();
                    frekwencja2tura = (double)glosujacych * 100 / (double)uprawnionych;
                    model.addAttribute("kandydat", kandydaci);
                    return "start";
                }
            }
        }
        model.addAttribute("wyborca", listaWyborcow);
        return "blad_brakLoginu";
    }

    /**
     * Możliwe w każdym czasie, zeruje głosy dla wszystkich kandydatów.
     * Nie ma zarządzonych wyborów - nic się nie zmienia.
     * @param model
     * @return -> spis wyborców
     */
    @RequestMapping("/odwolajWybory")
    public String odwolajWybory(Model model){

        wyzerujTerminy();
        brak_drugiej_tury = false;

        List<Wyborca> wyborcy = wyborcaRepo.findAll();
        for (Wyborca wyborca : wyborcy) {
            wyborca.setMozeZaglosowac(true);
            wyborca.setGlosow(0);
            wyborca.setGlosow2tura(0);
            wyborcaRepo.save(wyborca);
        }

        model.addAttribute("wyborca", wyborcy);
        return "lista";
    }

    @RequestMapping("/zarzadzWybory")
    public String zarzadzWybory(Model model){

        this.skontrolujCzas();
        if(mozna_zarzadzic_wybory) {
            // wyzeruj kandydowanie i ilości głosów
            // przydałaby się tabela z archiwalnymi danymi o poprzednich wyborach
            List<Wyborca> wyborcy = wyborcaRepo.findAll();
            for (Wyborca wyborca : wyborcy) {
                wyborca.setMozeZaglosowac(true);
                wyborca.setGlosow(0);
                wyborca.setGlosow2tura(0);
                wyborcaRepo.save(wyborca);
            }
            return "zarzadzWybory";
        } else {
            return "blad_zarzadzaniaWyborow";
        }
    }

    @RequestMapping("/termin")
    public String termin(@RequestParam("rok1") Integer rok1,
                         @RequestParam("miesiac1") Integer miesiac1,
                         @RequestParam("dzien1") Integer dzien1,
                         @RequestParam("godzina1") Integer godzina1,
                         @RequestParam("minuta1") Integer minuta1,
                         @RequestParam("rok2") Integer rok2,
                         @RequestParam("miesiac2") Integer miesiac2,
                         @RequestParam("dzien2") Integer dzien2,
                         @RequestParam("godzina2") Integer godzina2,
                         @RequestParam("minuta2") Integer minuta2,
                         @RequestParam("trwanie") Integer trwanie,
                         Model model){

        /*
         sprawdzenie, czy wartości mogą dać sensowną datę
         sprawdzenie, czy daty są w odpowiedniej kolejności
         sprawdzenie, czy pierwsza tura nie nachodzi na drugą
         utworzenie obiektów typu Date i zapisanie ich
             jako zmienne statyczne
             lub jako zmienne w bazie danych
         */

        if (!this.wlasciweParametryCzasu(rok1, miesiac1, dzien1, godzina1, minuta1,
                                        rok2, miesiac2, dzien2, godzina2, minuta2,
                                        trwanie))
        {
            return "blad_parametryCzasu";
        }


        pocz1tury   = new GregorianCalendar(rok1, miesiac1 - 1, dzien1, godzina1, minuta1).getTime();
        koniec1tury = new GregorianCalendar(rok1, miesiac1 - 1, dzien1, godzina1, minuta1 + trwanie).getTime();
        pocz2tury   = new GregorianCalendar(rok2, miesiac2 - 1, dzien2, godzina2, minuta2).getTime();
        koniec2tury = new GregorianCalendar(rok2, miesiac2 - 1, dzien2, godzina2, minuta2 + trwanie).getTime();

        Date teraz = new Date();

        if (    teraz.before(pocz1tury)
            &&  pocz1tury.before(koniec1tury)
            &&  koniec1tury.before(pocz2tury)
            &&  pocz2tury.before(koniec2tury))
        {

            model.addAttribute("wyborca", wyborcaRepo.findAll());
            System.out.println("Ustalone terminy:");
            System.out.println("początek pierwszej tury: " + pocz1tury);
            System.out.println("koniec pierwszej tury: " + koniec1tury);
            System.out.println("początek drugiej tury: " + pocz2tury);
            System.out.println("koniec drugiej tury: " + koniec2tury);
            System.out.println("");
            System.out.println("teraz: " + new Date());
            return "lista";
        }
        else
        {
            wyzerujTerminy();
            return "blad_zarzadzaniaWyborow";
        }

    }

    @RequestMapping("/zmien_haslo")
    public String zmien_haslo(Model model){
        return "zmien_haslo";
    }

    @RequestMapping("/zatwierdz_zmiane_hasla")
    public String zatwierdz_zmiane_hasla(
                         @RequestParam("login") String login,
                         @RequestParam("stare_haslo") String stare_haslo,
                         @RequestParam("nowe_haslo") String nowe_haslo,
                         @RequestParam("nowe_haslo_powtorzone") String nowe_haslo_powtorzone,
                         Model model){

        List<Wyborca> listaWyborcow = wyborcaRepo.findAll();

        for(Wyborca wyborca : listaWyborcow) {
            if(wyborca.getLogin().equals(login)) {
                if(!wyborca.getHaslo().equals(stare_haslo)) {
                    return "blad_zleHaslo";
                } else if (nowe_haslo.equals(nowe_haslo_powtorzone)) {
                    wyborca.setHaslo(nowe_haslo);
                    wyborcaRepo.save(wyborca);
                    return "haslo_zostalo_zmienione";
                } else {
                    return "blad_noweHaslaRozne";
                }
            }
        }
        return "blad_brakLoginu";
    }

    /**
     * 1. wybory niezarządzone - nie ma frekwencji
     * 2. Podczas pierwszej tury - suma ilości oddanych głosów przez ilość wyborców
     * 3.
     * @param model
     * @return
     */
    @RequestMapping("/zobacz_frekwencje")
    public String zobacz_frekwencje(Model model) {
        System.out.println(this.skontrolujCzas());
        model.addAttribute("frekwencja1tura", frekwencja1tura);
        model.addAttribute("frekwencja2tura", frekwencja2tura);
        return "zobacz_frekwencje";
    }


    /*
    Pomysł: programowanie wielowątkowe do obsługi przedziału czasu
    Transakcyjność przy zagłosowaniu

    Tabela wybory:
    rodzaj, termin
     */

}