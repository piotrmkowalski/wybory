package com.example.wybory1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Controler {

    private KandydatRepo kandydatRepo;

    @Autowired
    public Controler(KandydatRepo kandydatRepo) {
        this.kandydatRepo = kandydatRepo;
    }

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("kandydat", kandydatRepo.findAll());
        return "lista";
    }


    @RequestMapping("/dodaj")
    public String dodajKandydata(
            @RequestParam("imie") String imie,
            @RequestParam("nazwisko") String nazwisko,
            Model model)
            throws Exception {

        Kandydat kandydat = new Kandydat(imie, nazwisko, true);
        kandydatRepo.save(kandydat);
        model.addAttribute("kandydat", kandydat);
        return "wynik";
    }

    @RequestMapping("/lista")
    public String lista( Model model){
        model.addAttribute("kandydat", kandydatRepo.findAll());
        return "lista";
    }

    @RequestMapping("/glosowanie")
    public String glosuj(
            @RequestParam("id") Integer id, Model model
    )
            throws Exception {
        System.out.println(kandydatRepo.findById(id));
        model.addAttribute("kandydat", kandydatRepo.findById(id));
        return "wynik";
    }

    @RequestMapping("/kasuj")
    public String usunKandydata(@RequestParam("id") Integer id, Model model){
        kandydatRepo.deleteById(id);
        model.addAttribute("kandydat", kandydatRepo.findAll());
        return "lista";
    }

}