alter table artikal_normativi change column ID ID varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table artikal_normativi change column ARTIKAL_KLJUC ARTIKAL_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table artikal_normativi change column NORMATIV_KLJUC NORMATIV_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table grupe_artikala change column kljuc kljuc varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table kalkulacija change column kljuc kljuc varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table kalkulacija_stavka change column kljuc kljuc varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table kalkulacija_stavka change column KALKULACIJA_kljuc KALKULACIJA_kljuc varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table kalkulacija_stavka change column ARTIKAL_KLJUC ARTIKAL_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table kalkulacija_stavka_kartica change column STAVKA_kljuc STAVKA_kljuc varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table kalkulacija_stavka_kartica change column ROBA_KLJUC ROBA_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;


alter table kartica_stavke_racuna change column ROBA_KLJUC ROBA_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table korisnik change column kljuc kljuc varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table porezna_stopa change column kljuc kljuc varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table racun change column KORISNIK_KLJUC KORISNIK_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table racun change column template template varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table racun_naplata change column ID ID varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table racun_naplata change column RACUN_KLJUC RACUN_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table roba change column kljuc kljuc varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table roba change column grupa grupa varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table roba change column pdv pdv varchar(50) character set 'utf8' collate 'utf8_bin';
alter table roba change column pot pot varchar(50) character set 'utf8' collate 'utf8_bin';
alter table roba change column pdv_nabava pdv_nabava varchar(50) character set 'utf8' collate 'utf8_bin';

alter table roba_kartica change column ROBA_KLJUC ROBA_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table roba_kartica_normativa change column ROBA_KLJUC ROBA_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table roba_kartica_normativa change column normativ_id normativ_id varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table roba_promjena_cijene change column ID ID varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table roba_promjena_cijene change column ARTIKAL_KLJUC ARTIKAL_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table stavke change column RACUN_KLJUC RACUN_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table stavke change column ROBA_KLJUC ROBA_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table stavke change column grupa grupa varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table stavke_normativi change column ID ID varchar(50) character set 'utf8' collate 'utf8_bin' not null;
alter table stavke_normativi change column ARTIKAL_KLJUC ARTIKAL_KLJUC varchar(50) character set 'utf8' collate 'utf8_bin' not null;

alter table template change column oznaka oznaka varchar(50) character set 'utf8' collate 'utf8_bin' not null;


