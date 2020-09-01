package com.example.wybory1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class Controler {

    private WyborcaRepo wyborcaRepo;

    @Autowired
    public Controler(WyborcaRepo wyborcaRepo) {
        this.wyborcaRepo = wyborcaRepo;
    }

    @RequestMapping ("/")
    public String glowna() {
        List<Wyborca> wyborcy = wyborcaRepo.findAll();
        for (Wyborca wyborca : wyborcy) {
            wyborca.setMozeZaglosowac(true);
            wyborca.setGlosow(0);
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
        List<Wyborca> wyborcy = wyborcaRepo.findAll();
        List<Wyborca> kandydaci = new ArrayList<Wyborca>();
        for(Wyborca wyborca : wyborcy) {
            if (wyborca.isKandyduje() == true) {
                kandydaci.add(wyborca);
            }
        }
        model.addAttribute("kandydat", kandydaci);
        return "glosowanie";
    }

    @RequestMapping("/wyniki")
    public String wyniki( Model model){
        List<Wyborca> wyborcy = wyborcaRepo.findAll();
        List<Wyborca> kandydaci = new ArrayList<Wyborca>();
        for(Wyborca wyborca : wyborcy) {
            if (wyborca.isKandyduje() == true) {
                kandydaci.add(wyborca);
            }
        }
        model.addAttribute("kandydat", kandydaci);
        return "wynik";
    }

    @RequestMapping("/dodaj")
    public String dodajWyborce(
            @RequestParam("imie") String imie,
            @RequestParam("nazwisko") String nazwisko,
            @RequestParam("login") String login,
            @RequestParam("haslo") String haslo,
            Model model)
            throws Exception {

        Wyborca wyb = new Wyborca(imie, nazwisko, login, haslo, true, false, 0, true);
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
    }

    @RequestMapping("/glosuj")
    public String glosuj(@RequestParam("id") Integer id, Model model){
        List<Wyborca> listaWyborcow = wyborcaRepo.findAll();
        List<Wyborca> kandydaci = new ArrayList<Wyborca>();
        for(Wyborca wyborca : listaWyborcow) {
            if (wyborca.isKandyduje() == true) {
                kandydaci.add(wyborca);
            }
        }
        model.addAttribute("kandydat", kandydaci);
        return "zatwierdz";
    }

    @RequestMapping("/dolicz")
    public String dolicz(@RequestParam("id") Integer id,
                         @RequestParam("login") String login,
                         @RequestParam("haslo") String haslo,
                         Model model){

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
                    for(Wyborca wyb : listaWyborcow) {
                        if (wyb.isKandyduje() == true) {
                            kandydaci.add(wyb);
                        }
                    }
                    model.addAttribute("kandydat", kandydaci);
                    return "wynik";
                }
            }
        }
        model.addAttribute("wyborca", listaWyborcow);
        return "blad_brakLoginu";
    }

}