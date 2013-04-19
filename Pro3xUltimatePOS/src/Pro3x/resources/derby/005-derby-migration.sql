ALTER TABLE template 
    ADD COLUMN putanja VARCHAR(1000) DEFAULT NULL;

ALTER TABLE template 
    ADD COLUMN rok_placanja INT DEFAULT NULL;

ALTER TABLE racun_porezne_stavke 
	ADD COLUMN stopa DOUBLE  DEFAULT NULL;

ALTER TABLE stavke 
    ADD COLUMN maloprodajnacijena DOUBLE DEFAULT NULL;

ALTER TABLE stavke 
    ADD COLUMN tip VARCHAR(45) DEFAULT NULL;

ALTER TABLE roba
    ADD COLUMN pot VARCHAR(255) DEFAULT NULL;

ALTER TABLE roba
    ADD COLUMN pdv VARCHAR(255) DEFAULT NULL;

ALTER TABLE roba
    ADD COLUMN grupa VARCHAR(255) DEFAULT NULL;

ALTER TABLE roba
    ADD COLUMN sifra VARCHAR(255) DEFAULT NULL;

ALTER TABLE roba
    ADD COLUMN pdv_nabava VARCHAR(255) DEFAULT NULL;

UPDATE roba
SET roba.pdv = (SELECT roba_porez.porezna_stopa FROM roba_porez WHERE roba_porez.roba = roba.KLJUC);

UPDATE roba
SET roba.grupa = (SELECT artikal_grupa.grupa_kljuc FROM artikal_grupa WHERE artikal_grupa.artikal_kljuc = roba.KLJUC);

UPDATE roba
SET roba.sifra = (SELECT artikal_sifre.oznaka FROM artikal_sifre WHERE artikal_sifre.artikal_kljuc = roba.KLJUC);

UPDATE roba r
SET roba.pdv_nabava = (SELECT n.pdv FROM roba n WHERE n.kljuc = r.kljuc);


drop table roba_porez;
drop table artikal_grupa;
drop table artikal_sifre;

update stavke
    set tip = 'v1'
where 1=1;

alter table racun
    alter column napomena set data type varchar(4000);

drop table barkod_stavke;
drop table gotovinska_blagajna;

drop table privremene_kalkulacije;
drop table privremeni_barkodovi;
drop table privremeni_barkod_stavke;
drop table privremeni_normativi;

alter table racun
    add column template varchar(255);

update racun r
    set r.template = (SELECT rt.template FROM racun_template rt WHERE rt.racun = r.kljuc);

alter table template
    add column opis varchar(1000);

update template t
    set t.opis = (SELECT tpo.opis FROM template_description tpo WHERE tpo.template_oznaka = t.oznaka);

drop table racun_template;
drop table template_description;

alter table template
    add column nacin_placanja varchar(1);

update template
    set nacin_placanja = 'T', prioritet = 600
    where oznaka = '094d590f-d769-4ca7-b2a8-6a556796cf09';

update template
    set nacin_placanja = 'T', prioritet = 700
    where oznaka = 'ab5285cb-fbf7-42fd-86c5-ed58a4e9fa1c';

update template
    set nacin_placanja = 'G', prioritet = 800
    where oznaka = 'e454cd56-20d9-4921-a5d5-769888edf347';

update template
    set nacin_placanja = 'K', prioritet = 500
    where oznaka in (
        '668aedb2-6a6b-4376-ad7e-869ef3da1d98', 
        '8ec2ccfc-9578-401c-99c6-78c56d4c3e28', 
        '188bc6f7-f1c7-4d07-a280-4b4a409c1148', 
        '48b0d540-af70-11de-8a39-0800200c9a66', 
        '61db2855-1a0e-408a-a645-973a9566c6f4');

update template
    set nacin_placanja = 'G', prioritet = 100
    where oznaka = '3b0c2242-98ba-41a9-a062-12e8641870ac';

update template
    set nacin_placanja = null, prioritet = 200
    where oznaka = 'e1d758d5-15a6-43dd-a66a-1612ad25e378';


alter table template
    add column prioritet int default 0;

alter table racun
    add column operater BIGINT default null;

alter table racun_porezne_stavke
    add column tip varchar(10) default 'pdv';

alter table racun
    add column tip varchar(100) default 'racun-vr6';

alter table racun 
    add column jir varchar(255) default null;

alter table racun
    add column zki varchar(255) default null;

rename table racunnaplata to racun_naplata;

delete from ROOT.TEMPLATE where oznaka = '3b0c2242-98ba-41a9-a062-12e8641870ac';

alter table racun_porezne_stavke
    drop column stopa;

alter table racun_porezne_stavke
    add column stopa decimal(20, 2);

update racun_porezne_stavke
set stopa = cast(iznos / osnovica + 0.005 as decimal(10,2));

alter table racun
    add column fiskalna_lokacija varchar(250);

alter table racun
    add column fiskalni_uredaj varchar(250);

alter table racun
    add column verzija int;

alter table racun
    alter column fiskalna_lokacija set default null;

alter table racun 
    alter column fiskalni_uredaj set default null;

alter table racun
    alter column verzija set default null;

alter table racun
    add column zaglavlje varchar(2000) default null;

update template
set tip = 'Plaćeno na transakcijski račun'
where oznaka = '094d590f-d769-4ca7-b2a8-6a556796cf09';


update template
set tip = 'Transakcijski račun'
where oznaka = 'ab5285cb-fbf7-42fd-86c5-ed58a4e9fa1c';

drop table stavka_dostavnice;

alter table stavke 
    add column grupa varchar(255) default null;

alter table stavke
    add foreign key (grupa)
    references grupe_artikala(kljuc);
    






