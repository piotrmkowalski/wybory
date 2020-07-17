package com.example.wybory1;

import javax.persistence.*;

@Entity
@Table(name="Kandydat")
public class Kandydat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String imie;
    private String nazwisko;

    @Transient
    boolean  nowy;

    public Kandydat(String imie, String nazwisko, boolean nowy){
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nowy = nowy;
    }

    public Kandydat(Integer id, String imie, String nazwisko, boolean nowy){
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nowy = nowy;
    }

    public Kandydat() {
    }

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

    public boolean isNowy() {
        return nowy;
    }

    public void setNowy(boolean nowy) {
        this.nowy = nowy;
    }

    @Override
    public String toString() {
        return "Kandydat{" +
                "id=" + id +
                ", imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'' +
                ", nowy=" + nowy +
                '}';
    }
}