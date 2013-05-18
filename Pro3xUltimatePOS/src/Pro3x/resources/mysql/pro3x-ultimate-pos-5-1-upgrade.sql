CREATE TABLE kartice_artikala (
    sid BIGINT AUTO_INCREMENT NOT NULL, 
    DTYPE VARCHAR(31), 
    izlazna_cijena DOUBLE, 
    ulaz_kolicina DOUBLE, 
    opis VARCHAR(255), 
    datum DATETIME, 
    izlaz_kolicina DOUBLE, 
    ulazna_cijena DOUBLE, 
    ROBA_KLJUC VARCHAR(50) COLLATE utf8_bin , 
    stavka_racuna BIGINT, 
    stavka_kalkulacije VARCHAR(50) COLLATE utf8_bin , 
    PRIMARY KEY (sid));

ALTER TABLE kartice_artikala ADD CONSTRAINT FK_kartice_artikala_stavka_racuna FOREIGN KEY (stavka_racuna) REFERENCES STAVKE (KLJUC);
ALTER TABLE kartice_artikala ADD CONSTRAINT FK_kartice_artikala_roba_kljuc FOREIGN KEY (roba_kljuc) REFERENCES ROBA (kljuc);
ALTER TABLE kartice_artikala ADD CONSTRAINT FK_kartice_artikala_stavka_kalkulacije FOREIGN KEY (stavka_kalkulacije) REFERENCES kalkulacija_stavka (kljuc);

insert into kartice_artikala(dtype, izlazna_cijena, ulaz_kolicina, opis, datum, izlaz_kolicina, ulazna_cijena, roba_kljuc, stavka_racuna, stavka_kalkulacije)
select 'racun', izlazna_cijena, ulaz_kolicina, opis, datum, izlaz_kolicina, ulazna_cijena, roba_kljuc, stavka_kljuc, null
from kartica_stavke_racuna;

drop table kartica_stavke_racuna;

insert into kartice_artikala(dtype, izlazna_cijena, ulaz_kolicina, opis, datum, izlaz_kolicina, ulazna_cijena, roba_kljuc, stavka_racuna, stavka_kalkulacije)
select 'normativ', k.izlazna_cijena, k.ulaz_kolicina, k.opis, k.datum, k.izlaz_kolicina, k.ulazna_cijena, k.roba_kljuc, sn.stavka_kljuc, null
from roba_kartica_normativa k, stavke_normativi sn
where normativ_id = sn.id;

drop table roba_kartica_normativa;
drop table stavke_normativi;

insert into kartice_artikala(dtype, izlazna_cijena, ulaz_kolicina, opis, datum, izlaz_kolicina, ulazna_cijena, roba_kljuc, stavka_racuna, stavka_kalkulacije)
select 'kalkulacija', k.izlazna_cijena, k.ulaz_kolicina, k.opis, k.datum, k.izlaz_kolicina, k.ulazna_cijena, k.roba_kljuc, null, k.stavka_kljuc
from kalkulacija_stavka_kartica k;

drop table kalkulacija_stavka_kartica;

drop table roba_kartica;

CREATE TABLE blagajna (
    ID BIGINT AUTO_INCREMENT NOT NULL, 
    IZLAZ DOUBLE, 
    OPIS VARCHAR(255), 
    DATUM DATETIME, 
    ULAZ DOUBLE, 
    RACUN_KLJUC VARCHAR(50) COLLATE utf8_bin, 
    PRIMARY KEY (ID));

ALTER TABLE blagajna ADD CONSTRAINT FK_blagajna_RACUN_KLJUC FOREIGN KEY (RACUN_KLJUC) REFERENCES RACUN (KLJUC);


