package aplikacja.wybory2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    static Integer zaglosowalo1tura = 0;
    static Integer zaglosowalo2tura = 0;
    static Integer uprawnionych1tura = 0;
    static Integer uprawnionych2tura = 0;
    static Double frekwencja1tura = 0.0;
    static Double frekwencja2tura = 0.0;

    @Autowired
    public Controler(WyborcaRepo wyborcaRepo) {
        this.wyborcaRepo = wyborcaRepo;
    }

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

    public boolean wlasciweParametryCzasu(int rok1, int miesiac1, int dzien1, int godzina1, int minuta1,
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
        //System.out.println("Zeruję terminy.");
        pocz1tury = null;
        koniec1tury = null;
        pocz2tury = null;
        koniec2tury = null;
        frekwencja1tura = 0.0;
        frekwencja2tura = 0.0;
    }

    public Wyborca znajdz_po_id(Integer id) {
        for (Wyborca wyborca : wyborcaRepo.findAll()) {
            if(wyborca.getId().equals(id)) {
                return wyborca;
            }
        }
        return null;
    }

    public String skontrolujCzas() {

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
                //System.out.println("Wybory zostały rozstrzygnięte w pierwszej turze.");
                wynik = "po wyborach";
            } else {
                kandydaci.get(0).setKandyduje2tura(true);
                kandydaci.get(1).setKandyduje2tura(true);
                wyborcaRepo.save(kandydaci.get(0));
                wyborcaRepo.save(kandydaci.get(1));
                //System.out.println(kandydaci.get(0));
                //System.out.println(kandydaci.get(0));
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
     * kandydata, kolejność kandydatów będących ex aequo na tym samym
     * miejscu w liście końcowej była losowa.
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

    /**
     * Jeżeli wybory są rozstrzygnięte w pierwszej turze, to ich zwycięzcą
     * jest kandydat z maksymalną ilością głosów.
     *
     * Jeżeli z kolei wybory są rozstrzygnięte dopiero w drugiej turze,
     * to program sprawdza, czy obydwaj kandydaci mają tyle samo głosów.
     * Jeżeli nie, to zwycięzcą wyborów zostaje ten z większą ilością głosów.
     * Jeżeli tak, to o zwycięstwie kandydata decyduje losowanie.
     *
     * @param kandydaci - lista kandydatów
     * @param tura - w której turze są rozstrzygnięte wybory, w pierwszej czy w drugiej
     * @return
     */
    public Wyborca pokazZwyciezce(List<Wyborca> kandydaci, int tura) {

        if (tura == 1) { // wybory rozstrzygnięte w pierwszej turze
            int max_glosow = 0;
            int id_max = 0;
            for(Wyborca kandydat : kandydaci) {
                if(kandydat.getGlosow() > max_glosow) {
                    max_glosow = kandydat.getGlosow();
                    id_max = kandydat.getId();
                }
            }
            return this.znajdz_po_id(id_max);
        } else { // wybory rozstrzygnięte w drugiej turze
            List<Wyborca> kandydaci2tura = new ArrayList<Wyborca>();
            for (Wyborca kandydat : kandydaci)
                if (kandydat.isKandyduje2tura())
                    kandydaci2tura.add(kandydat);

            Wyborca kandydat1 = kandydaci2tura.get(0);
            Wyborca kandydat2 = kandydaci2tura.get(1);
            int glosow_na_kandydata1 = kandydat1.getGlosow2tura();
            int glosow_na_kandydata2 = kandydat2.getGlosow2tura();

            if (glosow_na_kandydata1 > glosow_na_kandydata2)
                return kandydat1;
            else if (glosow_na_kandydata2 > glosow_na_kandydata1)
                return kandydat2;
            else {
                if (Math.random() < 0.5) return kandydat1;
                else return kandydat2;
            }
        }
    }

    @RequestMapping ("/")
    public String glowna() {

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
        return "start";
    }

    /**
     * @return strona startowa, bez "resetowania" do ustawień początkowych
     *   (ilość głosów, kadydowanie, prawo zagłosowania)
     */
    @RequestMapping ("/start2")
    public String start2() {
        return "start";
    }

    @RequestMapping("/dodaj")
    public String dodajemyDane(
            @RequestParam("imie") String imie,
            @RequestParam("nazwisko") String nazwisko,
            @RequestParam("login") String login,
            Model model)
            throws Exception {

        String wynik = this.skontrolujCzas();
        if(wynik.equals("podczas pierwszej tury")
            || wynik.equals("podczas drugiej tury")) {
            return "widok_blad_dodawania";
        }

        if(imie.trim().isEmpty() || nazwisko.trim().isEmpty() || login.trim().isEmpty())
            return "widok_blad_pusteDane";

        Wyborca wyborca = new Wyborca(imie, nazwisko, login,  "123", true, false,
                false, 0, 0, true);
        wyborcaRepo.save(wyborca);
        model.addAttribute("wyborca", wyborca);
        return "Widok";
    }

    @RequestMapping("/lista")
    public String lista(Model model) {
        model.addAttribute("wyborca", wyborcaRepo.findAll());
        return "lista";
    }

    @RequestMapping("/kasuj")
    public String kasuj(@RequestParam("id") Integer id, Model model) {
        String wynik = this.skontrolujCzas();
        if(wynik.equals("podczas pierwszej tury")
                || wynik.equals("podczas drugiej tury")) {
            return "widok_blad_dodawania";
        }
        wyborcaRepo.deleteById(id);
        model.addAttribute("wyborca", wyborcaRepo.findAll());
        return "lista";
    }

    @RequestMapping("/wyszukaj")
    public String wyszukaj(@RequestParam("kryterium") String kryterium, Model model) {
        model.addAttribute("wyborca", wyborcaRepo.findAllBynazwisko(kryterium));
        return "lista";
    }

    @PostMapping("/aktualizuj")
    public String update(
            @RequestParam("id") Integer id,
            @RequestParam("imie") String imie,
            @RequestParam("nazwisko") String nazwisko,
            @RequestParam("login") String login,
            Model model)
            throws Exception {

        if(imie.trim().isEmpty() || nazwisko.trim().isEmpty() || login.trim().isEmpty())
            return "widok_blad_pusteDane";

        Wyborca wyborca_zmieniany = this.znajdz_po_id(id);
        Wyborca wyborca = new Wyborca(id, imie, nazwisko, login,
                wyborca_zmieniany.getHaslo(),
                wyborca_zmieniany.isMozeZaglosowac(),
                wyborca_zmieniany.isKandyduje(),
                wyborca_zmieniany.isKandyduje2tura(),
                wyborca_zmieniany.getGlosow(),
                wyborca_zmieniany.getGlosow2tura(),
                true); // true -> że nowy

        wyborcaRepo.save(wyborca);
        model.addAttribute("wyborca", wyborca);
        return "Widok";
    }

    @RequestMapping("/przekieruj")
    public String przekieruj(@RequestParam("id") Integer id, Model model)
            throws Exception
    {
        System.out.println(wyborcaRepo.findById(id));
        model.addAttribute("wyborca", wyborcaRepo.findById(id));
        return "aktualizuj";
    }

    @RequestMapping("/przekieruj_glosuj")
    public String przekieruj_glosuj(@RequestParam("id") Integer id, Model model)
            throws Exception
    {
        model.addAttribute("wyborca", wyborcaRepo.findById(id));
        return "glosowanie";
    }

    @RequestMapping("/wylon")
    public String wylon(@RequestParam("id") Integer id, Model model){
        // wyłaniać kandydatów można dopiero po zarządzeniu wyborów,
        // ale przed ich rozpoczęciem
        this.skontrolujCzas();
        if(mozna_zmieniac_liste_kandydatow) {
            List<Wyborca> listaWyborcow = wyborcaRepo.findAll();
            Wyborca wyborca = this.znajdz_po_id(id);
            boolean czyKandyduje = wyborca.isKandyduje();
            wyborca.setKandyduje(!czyKandyduje);
            wyborcaRepo.save(wyborca);
            model.addAttribute("wyborca", wyborcaRepo.findAll());
            return "lista";
        } else {
            return "blad_wylanianiaKandydata";
        }
    }

    /**
     *
     * @param kandydaci lista kandydatów
     * @return posortowana alfabetycznie wg nazwisk lista kandydatów
     */
    List<Wyborca> posortuj_kandydatow_nazwiskami(List<Wyborca> kandydaci) {

        List<String> nazwiska = new ArrayList<String>();
        List<Wyborca> posortowani = new ArrayList<Wyborca>();

        for(Wyborca kandydat : kandydaci)
            nazwiska.add(kandydat.getNazwisko());

        Collections.sort(nazwiska);
        Collections.reverse(nazwiska);  // odwrócenie kolejności nazwisk

        for (String nazwisko : nazwiska) {
            for (Wyborca kandydat : kandydaci) {
                if (kandydat.getNazwisko().equals(nazwisko)) {
                    posortowani.add(kandydat);
                    break;
                }
            }
        }

        return posortowani;
    }

    @RequestMapping("/zaglosuj")
    public String glosowanie(Model model){

        String wynik = this.skontrolujCzas();
        List<Wyborca> wyborcy = wyborcaRepo.findAll();
        List<Wyborca> kandydaci = new ArrayList<Wyborca>();

        if (wynik.equals("podczas pierwszej tury")) {
            for(Wyborca wyborca : wyborcy) {
                if (wyborca.isKandyduje() == true) {
                    kandydaci.add(wyborca);
                }
            }
            kandydaci = this.posortuj_kandydatow_nazwiskami(kandydaci);
            model.addAttribute("kandydat", kandydaci);
            return "karta_wyborcza";
        }
        else if (wynik.equals("podczas drugiej tury")) {
            for(Wyborca wyborca : wyborcy) {
                if (wyborca.isKandyduje2tura() == true) {
                    kandydaci.add(wyborca);
                }
            }
            String nazwisko0 = kandydaci.get(0).getNazwisko();
            String nazwisko1 = kandydaci.get(1).getNazwisko();
            if(nazwisko0.compareTo(nazwisko1) > 0) {
                Wyborca kandydat0 = kandydaci.get(0);
                Wyborca kandydat1 = kandydaci.get(1);
                kandydaci.clear();
                kandydaci.add(kandydat1);
                kandydaci.add(kandydat0);
            }
            model.addAttribute("kandydat", kandydaci);
            return "karta_wyborcza";
        }
        else {
            return "glosowanie_blad_glosowanieNieTrwa";
        }
    }

    @PostMapping("/dolicz")
    public String dolicz(@RequestParam("id") Integer id,
                         @RequestParam("login") String login,
                         @RequestParam("haslo") String haslo,
                         Model model){

        String wynik = this.skontrolujCzas();
        if(     wynik.equals("brak zarządzonych wyborów")
            ||  wynik.equals("przed pierwszą turą")
            ||  wynik.equals("przed drugą turą")
            ||  wynik.equals("po wyborach")
        ) {
            return "glosowanie_blad_glosowanieNieTrwa";
        }

        List<Wyborca> listaWyborcow = wyborcaRepo.findAll();

        for(Wyborca wyborca : listaWyborcow) {
            if(wyborca.getLogin().equals(login)) {
                if(!wyborca.getHaslo().equals(haslo)) {
                    model.addAttribute("wyborca", wyborcaRepo.findById(id));
                    return "glosowanie_blad_zleHaslo";
                }
                else if (wyborca.isMozeZaglosowac() == false) {
                    model.addAttribute("wyborca", wyborcaRepo.findById(id));
                    return "glosowanie_blad_ponowneGlosowanie";
                } else {
                    // znalezienie w bazie danych kandydata, któremu należy doliczyć głos
                    Wyborca kandydat = this.znajdz_po_id(id);
                    // doliczenie głosu dla określonego kandydata
                    if(wynik.equals("podczas pierwszej tury")) {
                        uprawnionych1tura = listaWyborcow.size();
                        zaglosowalo1tura++;
                        Integer iloscGlosow = kandydat.getGlosow();
                        kandydat.setGlosow(++iloscGlosow);
                    } else if (wynik.equals("podczas drugiej tury")) {
                        uprawnionych2tura = listaWyborcow.size();
                        zaglosowalo2tura++;
                        Integer iloscGlosow = kandydat.getGlosow2tura();
                        kandydat.setGlosow2tura(++iloscGlosow);
                    }
                    wyborcaRepo.save(kandydat);

                    // ustawienie informacji w bazie danych, że wyborca już zagłosował
                    wyborca.setMozeZaglosowac(false);
                    wyborcaRepo.save(wyborca);

                    return "glosowanie_glos_zostal_oddany";
                }
            }
        }
        model.addAttribute("wyborca", wyborcaRepo.findById(id));
        return "glosowanie_blad_brakLoginu";
    }

    @RequestMapping("/zmien_haslo")
    public String zmien_haslo(Model model) {
        return "zmien_haslo";
    }

    @PostMapping("/zatwierdz_zmiane_hasla")
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
                    return "zmien_haslo_blad_zleHaslo";
                } else if (nowe_haslo.equals(nowe_haslo_powtorzone)) {
                    wyborca.setHaslo(nowe_haslo);
                    wyborcaRepo.save(wyborca);
                    return "zmien_haslo_haslo_zostalo_zmienione";
                } else {
                    return "zmien_haslo_blad_noweHaslaRozne";
                }
            }
        }
        return "zmien_haslo_blad_brakLoginu";
    }

    @RequestMapping("/zobacz_frekwencje")
    public String zobacz_frekwencje(Model model) {
        String wynik = this.skontrolujCzas();
        List<Wyborca> wyborcy = wyborcaRepo.findAll();
        Integer uprawnionych = wyborcy.size();
        Integer zaglosowalo = 0;
        if(     wynik.equals("brak zarządzonych wyborów")
            ||  wynik.equals("przed pierwszą turą")) {
            /* zaglosowalo1tura = 0;
            zaglosowalo2tura = 0;
            uprawnionych1tura = 0;
            uprawnionych2tura = 0;
            frekwencja1tura = 0.0;
            frekwencja2tura = 0.0; */
        } else if(wynik.equals("podczas pierwszej tury")) {
            for(Wyborca wyborca : wyborcy) {
                if(wyborca.isMozeZaglosowac() == false) {
                    zaglosowalo++;
                }
            }
            zaglosowalo1tura = zaglosowalo;
            uprawnionych1tura = uprawnionych;
            frekwencja1tura = (double)zaglosowalo * 100 / (double)uprawnionych;
            /*zaglosowalo2tura = 0;
            uprawnionych2tura = 0;
            frekwencja2tura = 0.0;*/
        } else if(wynik.equals("przed drugą turą")) {
            /*zaglosowalo2tura = 0;
            uprawnionych2tura = 0;
            frekwencja2tura = 0.0;*/
        } else if(wynik.equals("podczas drugiej tury")) {
            for(Wyborca wyborca : wyborcy) {
                if(wyborca.isMozeZaglosowac() == false) {
                    zaglosowalo++;
                }
            }
            zaglosowalo2tura = zaglosowalo;
            uprawnionych2tura = uprawnionych;
            frekwencja2tura = (double)zaglosowalo * 100 / (double)uprawnionych;
        }
        model.addAttribute("zaglosowalo1tura"   , zaglosowalo1tura);
        model.addAttribute("zaglosowalo2tura"   , zaglosowalo2tura);
        model.addAttribute("uprawnionych1tura"  , uprawnionych1tura);
        model.addAttribute("uprawnionych2tura"  , uprawnionych2tura);
        model.addAttribute("frekwencja1tura"    , frekwencja1tura);
        model.addAttribute("frekwencja2tura"    , frekwencja2tura);
        return "zobacz_frekwencje";
    }

    @RequestMapping("/wynik")
    public String wynik( Model model){

        List<Wyborca> wyborcy = wyborcaRepo.findAll();
        List<Wyborca> kandydaci = new ArrayList<>();
        List<Wyborca> kandydaci2tura = new ArrayList<>();
        for(Wyborca wyborca : wyborcy) {
            if (wyborca.isKandyduje() == true) {
                kandydaci.add(wyborca);
            }
            if (wyborca.isKandyduje2tura() == true) {
                kandydaci2tura.add(wyborca);
                System.out.println("w drugiej turze: " + wyborca);
            }
        }

        // zobaczyć można po pierwszej turze wyborów.
        String wynik = this.skontrolujCzas();
        if (    wynik.equals("brak zarządzonych wyborów")
                || wynik.equals("przed pierwszą turą")
                || wynik.equals("podczas pierwszej tury")
        )
        {
            return "wynik_blad_brakWyniku";
        } else if (wynik.equals("przed drugą turą")
        || (wynik.equals("podczas drugiej tury"))) {
            System.out.println("Wybory nierozstrzygnięte.");
            System.out.println("Ilość kandydatów w drugiej turze: " + kandydaci2tura.size());
            model.addAttribute("kandydat", kandydaci);
            model.addAttribute("kandydaci2tura", kandydaci2tura);
            return "wynik";
        } else if ((wynik.equals("po wyborach") && brak_drugiej_tury == true)) {
            System.out.println("Wybory rozstrzygnięte w pierwszej turze.");
            Wyborca zwyciezca = this.pokazZwyciezce(kandydaci, 1);
            System.out.println("Zwycięzca wyborów: " + zwyciezca );
            model.addAttribute("zwyciezca", zwyciezca);
            model.addAttribute("kandydat", kandydaci);
            return "wynik1tura";    // wybory są rozstrzygnięte w pierwszej turze
        } else { // wybory są rozstrzygnięte w drugiej turze
            System.out.println("Wybory rozstrzygnięte w drugiej turze.");
            Wyborca zwyciezca = this.pokazZwyciezce(kandydaci2tura, 2);
            System.out.println("Zwycięzca wyborów: " + zwyciezca );
            model.addAttribute("zwyciezca", zwyciezca);
            model.addAttribute("kandydat", kandydaci);
            model.addAttribute("kandydat2tura", kandydaci2tura);
            return "wynik2tura";
        }

    }

    @RequestMapping("/zarzadzWybory")
    public String zarzadzWybory(Model model){

        this.skontrolujCzas();
        if(mozna_zarzadzic_wybory) {
            // wyzeruj kandydowanie i ilości głosów
            List<Wyborca> wyborcy = wyborcaRepo.findAll();
            for (Wyborca wyborca : wyborcy) {
                wyborca.setMozeZaglosowac(true);
                wyborca.setGlosow(0);
                wyborca.setGlosow2tura(0);
                wyborcaRepo.save(wyborca);
            }
            zaglosowalo1tura = 0;
            zaglosowalo2tura = 0;
            uprawnionych1tura = 0;
            uprawnionych2tura = 0;
            frekwencja1tura = 0.0;
            frekwencja2tura = 0.0;
            return "zarzadzWybory";
        } else {
            return "zarzadzWybory_blad_zarzadzaniaWyborow";
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

        // sprawdzenie, czy wartości mogą dać sensowną datę
        if (this.wlasciweParametryCzasu(rok1, miesiac1, dzien1, godzina1, minuta1,
                rok2, miesiac2, dzien2, godzina2, minuta2,
                trwanie) == false)
        {
            return "zarzadzWybory_blad_parametryCzasu";
        }

        // terminy są zapisane jako zmienne statyczne
        pocz1tury   = new GregorianCalendar(rok1, miesiac1 - 1, dzien1, godzina1, minuta1).getTime();
        koniec1tury = new GregorianCalendar(rok1, miesiac1 - 1, dzien1, godzina1, minuta1 + trwanie).getTime();
        pocz2tury   = new GregorianCalendar(rok2, miesiac2 - 1, dzien2, godzina2, minuta2).getTime();
        koniec2tury = new GregorianCalendar(rok2, miesiac2 - 1, dzien2, godzina2, minuta2 + trwanie).getTime();

        Date teraz = new Date();

        // sprawdzenie, czy terminy są w odpowiedniej kolejności
        if (    teraz.before(pocz1tury)
            &&  pocz1tury.before(koniec1tury)
            &&  koniec1tury.before(pocz2tury)
            &&  pocz2tury.before(koniec2tury))
        {
            List<Wyborca> wyborcy = wyborcaRepo.findAll();
            for(Wyborca wyborca : wyborcy) {
                wyborca.setKandyduje(false);
                wyborca.setKandyduje2tura(false);
                wyborca.setGlosow(0);
                wyborca.setGlosow2tura(0);
                wyborcaRepo.save(wyborca);
            }
            model.addAttribute("wyborca", wyborcaRepo.findAll());
            return "lista";
        }
        else    // jeżeli kolejność terminów jest zła, to następuje ich anulowanie
        {
            wyzerujTerminy();
            return "zarzadzWybory_blad_zarzadzaniaWyborow";
        }
    }

    /**
     * Możliwe w każdym czasie, zeruje głosy dla wszystkich kandydatów.
     * Nie ma zarządzonych wyborów - nic się nie zmienia.
     * @param model
     * @return -> spis wyborców
     */
    @RequestMapping("/odwolajWybory")
    public String odwolajWybory(Model model){

        String wynik = this.skontrolujCzas();
        if(wynik.equals("przed pierwszą turą") == false) {
            return "blad_wylanianiaKandydata";
        }

        wyzerujTerminy();
        brak_drugiej_tury = false;

        List<Wyborca> wyborcy = wyborcaRepo.findAll();
        for (Wyborca wyborca : wyborcy) {
            wyborca.setMozeZaglosowac(true);
            wyborca.setKandyduje(false);
            wyborca.setKandyduje2tura(false);
            wyborca.setGlosow(0);
            wyborca.setGlosow2tura(0);
            wyborcaRepo.save(wyborca);
        }

        model.addAttribute("wyborca", wyborcy);
        return "lista";
    }

}