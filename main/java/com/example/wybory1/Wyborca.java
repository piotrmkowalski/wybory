package com.example.wybory1;

import javax.persistence.*;

@Entity
@Table(name="Wyborca")
public class Wyborca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String imie;
    private String nazwisko;
    private String login;
    private String haslo;
    private boolean mozeZaglosowac;
    private boolean kandyduje;
    private Integer glosow;

    @Transient
    boolean  nowy;

    public Wyborca(Integer id, String imie, String nazwisko, String login, String haslo,
                    boolean mozeZaglosowac, boolean kandyduje, Integer glosow, boolean nowy) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.login = login;
        this.haslo = haslo;
        this.mozeZaglosowac = mozeZaglosowac;
        this.kandyduje = kandyduje;
        this.glosow = glosow;
        this.nowy = nowy;
    }

    public Wyborca(String imie, String nazwisko, String login, String haslo,
                   boolean mozeZaglosowac, boolean kandyduje,  Integer glosow, boolean nowy) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.login = login;
        this.haslo = haslo;
        this.mozeZaglosowac = mozeZaglosowac;
        this.kandyduje = kandyduje;
        this.glosow = glosow;
        this.nowy = nowy;
    }

    public Wyborca(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public boolean isMozeZaglosowac() {
        return mozeZaglosowac;
    }

    public void setMozeZaglosowac(boolean mozeZaglosowac) {
        this.mozeZaglosowac = mozeZaglosowac;
    }

    public boolean isKandyduje() {
        return kandyduje;
    }

    public void setKandyduje(boolean kandyduje) {this.kandyduje = kandyduje;}

    public Integer getGlosow() {
        return glosow;
    }

    public void setGlosow(Integer glosow) {
        this.glosow = glosow;
    }

    public boolean isNowy() {
        return nowy;
    }

    public void setNowy(boolean nowy) {
        this.nowy = nowy;
    }

    @Override
    public String toString() {
        return "Wyborca{" +
                "id=" + id +
                ", imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'' +
                ", login='" + login + '\'' +
                ", haslo='" + haslo + '\'' +
                ", mozeZaglosowac=" + mozeZaglosowac +
                ", kandyduje=" + kandyduje +
                ", głosów=" + glosow +
                ", nowy=" + nowy +
                '}';
    }
}
