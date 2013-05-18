-- MySQL dump 10.13  Distrib 5.5.31, for debian-linux-gnu (i686)
--
-- Host: localhost    Database: hadria
-- ------------------------------------------------------
-- Server version	5.5.31-0ubuntu0.12.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `artikal_normativi`
--

DROP TABLE IF EXISTS `artikal_normativi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `artikal_normativi` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `KOLICINA` double DEFAULT NULL,
  `ARTIKAL_KLJUC` varchar(50) DEFAULT NULL,
  `NORMATIV_KLJUC` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_artikal_normativi_ARTIKAL_KLJUC` (`ARTIKAL_KLJUC`),
  KEY `FK_artikal_normativi_NORMATIV_KLJUC` (`NORMATIV_KLJUC`),
  CONSTRAINT `FK_artikal_normativi_ARTIKAL_KLJUC` FOREIGN KEY (`ARTIKAL_KLJUC`) REFERENCES `roba` (`KLJUC`),
  CONSTRAINT `FK_artikal_normativi_NORMATIV_KLJUC` FOREIGN KEY (`NORMATIV_KLJUC`) REFERENCES `roba` (`KLJUC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `artikal_normativi`
--

LOCK TABLES `artikal_normativi` WRITE;
/*!40000 ALTER TABLE `artikal_normativi` DISABLE KEYS */;
/*!40000 ALTER TABLE `artikal_normativi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blagajna`
--

DROP TABLE IF EXISTS `blagajna`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `blagajna` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `IZLAZ` double DEFAULT NULL,
  `OPIS` varchar(255) DEFAULT NULL,
  `DATUM` datetime DEFAULT NULL,
  `ULAZ` double DEFAULT NULL,
  `RACUN_KLJUC` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_blagajna_RACUN_KLJUC` (`RACUN_KLJUC`),
  CONSTRAINT `FK_blagajna_RACUN_KLJUC` FOREIGN KEY (`RACUN_KLJUC`) REFERENCES `racun` (`KLJUC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blagajna`
--

LOCK TABLES `blagajna` WRITE;
/*!40000 ALTER TABLE `blagajna` DISABLE KEYS */;
/*!40000 ALTER TABLE `blagajna` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dobavljac`
--

DROP TABLE IF EXISTS `dobavljac`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dobavljac` (
  `KLJUC` int(11) NOT NULL AUTO_INCREMENT,
  `ZIRO` varchar(255) DEFAULT NULL,
  `MATICNI_BROJ` varchar(255) DEFAULT NULL,
  `ADRESA` varchar(255) DEFAULT NULL,
  `NAZIV` varchar(255) DEFAULT NULL,
  `LOKACIJA` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`KLJUC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dobavljac`
--

LOCK TABLES `dobavljac` WRITE;
/*!40000 ALTER TABLE `dobavljac` DISABLE KEYS */;
/*!40000 ALTER TABLE `dobavljac` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grupe_artikala`
--

DROP TABLE IF EXISTS `grupe_artikala`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `grupe_artikala` (
  `kljuc` varchar(50) NOT NULL,
  `oznaka` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`kljuc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grupe_artikala`
--

LOCK TABLES `grupe_artikala` WRITE;
/*!40000 ALTER TABLE `grupe_artikala` DISABLE KEYS */;
/*!40000 ALTER TABLE `grupe_artikala` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kalkulacija`
--

DROP TABLE IF EXISTS `kalkulacija`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kalkulacija` (
  `kljuc` varchar(50) NOT NULL,
  `IZRADIO` varchar(255) DEFAULT NULL,
  `datum_placenja` datetime DEFAULT NULL,
  `datum_dospijeca` datetime DEFAULT NULL,
  `oznaka_dokumenta` varchar(255) DEFAULT NULL,
  `datum_dokumenta` datetime DEFAULT NULL,
  `oznaka_kalkulacije` varchar(255) DEFAULT NULL,
  `datum_izrade` datetime DEFAULT NULL,
  `DOBAVLJAC_KLJUC` int(11) DEFAULT NULL,
  PRIMARY KEY (`kljuc`),
  KEY `FK_kalkulacija_DOBAVLJAC_KLJUC` (`DOBAVLJAC_KLJUC`),
  CONSTRAINT `FK_kalkulacija_DOBAVLJAC_KLJUC` FOREIGN KEY (`DOBAVLJAC_KLJUC`) REFERENCES `dobavljac` (`KLJUC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kalkulacija`
--

LOCK TABLES `kalkulacija` WRITE;
/*!40000 ALTER TABLE `kalkulacija` DISABLE KEYS */;
/*!40000 ALTER TABLE `kalkulacija` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kalkulacija_stavka`
--

DROP TABLE IF EXISTS `kalkulacija_stavka`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kalkulacija_stavka` (
  `kljuc` varchar(50) NOT NULL,
  `UKUPNO` double DEFAULT NULL,
  `mjerna_jedinica` varchar(255) DEFAULT NULL,
  `IZNOS` double DEFAULT NULL,
  `porezna_stopa` double DEFAULT NULL,
  `fakturna_sa_porezom` double DEFAULT NULL,
  `iznos_poreza` double DEFAULT NULL,
  `zavisni_troskovi` double DEFAULT NULL,
  `KOLICINA` double DEFAULT NULL,
  `MARZA` double DEFAULT NULL,
  `iznos_rabata` double DEFAULT NULL,
  `cijena_sa_porezom` double DEFAULT NULL,
  `RABAT` double DEFAULT NULL,
  `fakturna_vrijednost_bez_poreza` double DEFAULT NULL,
  `OSNOVICA` double DEFAULT NULL,
  `CIJENA` double DEFAULT NULL,
  `cijena_bez_poreza` double DEFAULT NULL,
  `iznos_marze` double DEFAULT NULL,
  `KALKULACIJA_kljuc` varchar(50) DEFAULT NULL,
  `ARTIKAL_KLJUC` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`kljuc`),
  KEY `FK_kalkulacija_stavka_ARTIKAL_KLJUC` (`ARTIKAL_KLJUC`),
  KEY `FK_kalkulacija_stavka_KALKULACIJA_kljuc` (`KALKULACIJA_kljuc`),
  CONSTRAINT `FK_kalkulacija_stavka_ARTIKAL_KLJUC` FOREIGN KEY (`ARTIKAL_KLJUC`) REFERENCES `roba` (`KLJUC`),
  CONSTRAINT `FK_kalkulacija_stavka_KALKULACIJA_kljuc` FOREIGN KEY (`KALKULACIJA_kljuc`) REFERENCES `kalkulacija` (`kljuc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kalkulacija_stavka`
--

LOCK TABLES `kalkulacija_stavka` WRITE;
/*!40000 ALTER TABLE `kalkulacija_stavka` DISABLE KEYS */;
/*!40000 ALTER TABLE `kalkulacija_stavka` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kartice_artikala`
--

DROP TABLE IF EXISTS `kartice_artikala`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kartice_artikala` (
  `sid` bigint(20) NOT NULL AUTO_INCREMENT,
  `DTYPE` varchar(31) DEFAULT NULL,
  `izlazna_cijena` double DEFAULT NULL,
  `ulaz_kolicina` double DEFAULT NULL,
  `opis` varchar(255) DEFAULT NULL,
  `datum` datetime DEFAULT NULL,
  `izlaz_kolicina` double DEFAULT NULL,
  `ulazna_cijena` double DEFAULT NULL,
  `ROBA_KLJUC` varchar(50) DEFAULT NULL,
  `stavka_racuna` bigint(20) DEFAULT NULL,
  `stavka_kalkulacije` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`sid`),
  KEY `FK_kartice_artikala_stavka_racuna` (`stavka_racuna`),
  KEY `FK_kartice_artikala_roba_kljuc` (`ROBA_KLJUC`),
  KEY `FK_kartice_artikala_stavka_kalkulacije` (`stavka_kalkulacije`),
  CONSTRAINT `FK_kartice_artikala_stavka_kalkulacije` FOREIGN KEY (`stavka_kalkulacije`) REFERENCES `kalkulacija_stavka` (`kljuc`),
  CONSTRAINT `FK_kartice_artikala_roba_kljuc` FOREIGN KEY (`ROBA_KLJUC`) REFERENCES `roba` (`KLJUC`),
  CONSTRAINT `FK_kartice_artikala_stavka_racuna` FOREIGN KEY (`stavka_racuna`) REFERENCES `stavke` (`KLJUC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kartice_artikala`
--

LOCK TABLES `kartice_artikala` WRITE;
/*!40000 ALTER TABLE `kartice_artikala` DISABLE KEYS */;
/*!40000 ALTER TABLE `kartice_artikala` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `korisnik`
--

DROP TABLE IF EXISTS `korisnik`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `korisnik` (
  `kljuc` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `maticni_broj` varchar(255) DEFAULT NULL,
  `adresa` varchar(255) DEFAULT NULL,
  `naziv` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `lokacija` varchar(255) DEFAULT NULL,
  `mobitel` varchar(255) DEFAULT NULL,
  `telefon` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`kljuc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `korisnik`
--

LOCK TABLES `korisnik` WRITE;
/*!40000 ALTER TABLE `korisnik` DISABLE KEYS */;
/*!40000 ALTER TABLE `korisnik` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operater`
--

DROP TABLE IF EXISTS `operater`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operater` (
  `ID` bigint(20) NOT NULL,
  `OIB` varchar(255) DEFAULT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `PASSWORD` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operater`
--

LOCK TABLES `operater` WRITE;
/*!40000 ALTER TABLE `operater` DISABLE KEYS */;
/*!40000 ALTER TABLE `operater` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `porezna_stopa`
--

DROP TABLE IF EXISTS `porezna_stopa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `porezna_stopa` (
  `kljuc` varchar(50) NOT NULL,
  `iznos` double DEFAULT NULL,
  `opis` varchar(255) DEFAULT NULL,
  `pocetak` datetime DEFAULT NULL,
  `kraj` datetime DEFAULT NULL,
  PRIMARY KEY (`kljuc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `porezna_stopa`
--

LOCK TABLES `porezna_stopa` WRITE;
/*!40000 ALTER TABLE `porezna_stopa` DISABLE KEYS */;
/*!40000 ALTER TABLE `porezna_stopa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `racun`
--

DROP TABLE IF EXISTS `racun`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `racun` (
  `KLJUC` varchar(255) NOT NULL,
  `tip` varchar(31) DEFAULT NULL,
  `MATICNI_BROJ` varchar(255) DEFAULT NULL,
  `verzija` int(11) DEFAULT NULL,
  `jir` varchar(255) DEFAULT NULL,
  `POREZ` double DEFAULT NULL,
  `NAZIV` varchar(255) DEFAULT NULL,
  `STORNIRAN` datetime DEFAULT NULL,
  `KORISNIK_KLJUC` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `IZDAN` datetime DEFAULT NULL,
  `zki` varchar(255) DEFAULT NULL,
  `fiskalni_uredaj` varchar(255) DEFAULT NULL,
  `PLACEN` datetime DEFAULT NULL,
  `UKUPNO` double DEFAULT NULL,
  `ADRESA` varchar(255) DEFAULT NULL,
  `OZNAKA` varchar(255) DEFAULT NULL,
  `fiskalna_lokacija` varchar(255) DEFAULT NULL,
  `TELEFON` varchar(255) DEFAULT NULL,
  `VALUTA` datetime DEFAULT NULL,
  `NAPOMENA` varchar(4000) DEFAULT NULL,
  `UPLATA` varchar(255) DEFAULT NULL,
  `LOKACIJA` varchar(255) DEFAULT NULL,
  `MOBITEL` varchar(255) DEFAULT NULL,
  `OTPREMNICA` varchar(255) DEFAULT NULL,
  `zaglavlje` varchar(2000) DEFAULT NULL,
  `operater` bigint(20) DEFAULT NULL,
  `template` varchar(50) DEFAULT NULL,
  `putanja_predloska` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`KLJUC`),
  KEY `FK_RACUN_operater` (`operater`),
  KEY `FK_RACUN_template` (`template`),
  CONSTRAINT `FK_RACUN_operater` FOREIGN KEY (`operater`) REFERENCES `operater` (`ID`),
  CONSTRAINT `FK_RACUN_template` FOREIGN KEY (`template`) REFERENCES `template` (`oznaka`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `racun`
--

LOCK TABLES `racun` WRITE;
/*!40000 ALTER TABLE `racun` DISABLE KEYS */;
/*!40000 ALTER TABLE `racun` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `racun_naplata`
--

DROP TABLE IF EXISTS `racun_naplata`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `racun_naplata` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `IZNOS` double NOT NULL,
  `DATUMPLACANJA` date DEFAULT NULL,
  `DATUMDOSPIJECA` datetime NOT NULL,
  `RACUN_KLJUC` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_racun_naplata_RACUN_KLJUC` (`RACUN_KLJUC`),
  CONSTRAINT `FK_racun_naplata_RACUN_KLJUC` FOREIGN KEY (`RACUN_KLJUC`) REFERENCES `racun` (`KLJUC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `racun_naplata`
--

LOCK TABLES `racun_naplata` WRITE;
/*!40000 ALTER TABLE `racun_naplata` DISABLE KEYS */;
/*!40000 ALTER TABLE `racun_naplata` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `racun_porezne_stavke`
--

DROP TABLE IF EXISTS `racun_porezne_stavke`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `racun_porezne_stavke` (
  `OZNAKA` bigint(20) NOT NULL,
  `tip` varchar(31) DEFAULT NULL,
  `naziv` varchar(255) DEFAULT NULL,
  `iznos` decimal(20,6) DEFAULT NULL,
  `osnovica` decimal(20,6) DEFAULT NULL,
  `stopa` decimal(3,2) DEFAULT NULL,
  `racun` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`OZNAKA`),
  KEY `FK_racun_porezne_stavke_racun` (`racun`),
  CONSTRAINT `FK_racun_porezne_stavke_racun` FOREIGN KEY (`racun`) REFERENCES `racun` (`KLJUC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `racun_porezne_stavke`
--

LOCK TABLES `racun_porezne_stavke` WRITE;
/*!40000 ALTER TABLE `racun_porezne_stavke` DISABLE KEYS */;
/*!40000 ALTER TABLE `racun_porezne_stavke` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roba`
--

DROP TABLE IF EXISTS `roba`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roba` (
  `KLJUC` varchar(50) NOT NULL,
  `MJERA` varchar(255) DEFAULT NULL,
  `NAZIV` varchar(255) DEFAULT NULL,
  `povratna` tinyint(1) DEFAULT '0',
  `NABAVNA_CIJENA` double DEFAULT NULL,
  `sifra` varchar(255) DEFAULT NULL,
  `CIJENA` double DEFAULT NULL,
  `deklaracija` varchar(4000) DEFAULT NULL,
  `grupa` varchar(50) DEFAULT NULL,
  `pdv` varchar(50) DEFAULT NULL,
  `pot` varchar(50) DEFAULT NULL,
  `pdv_nabava` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`KLJUC`),
  KEY `FK_roba_grupa` (`grupa`),
  KEY `FK_roba_pdv` (`pdv`),
  KEY `FK_roba_pdv_nabava` (`pdv_nabava`),
  KEY `FK_roba_pot` (`pot`),
  CONSTRAINT `FK_roba_grupa` FOREIGN KEY (`grupa`) REFERENCES `grupe_artikala` (`kljuc`),
  CONSTRAINT `FK_roba_pdv` FOREIGN KEY (`pdv`) REFERENCES `porezna_stopa` (`kljuc`),
  CONSTRAINT `FK_roba_pdv_nabava` FOREIGN KEY (`pdv_nabava`) REFERENCES `porezna_stopa` (`kljuc`),
  CONSTRAINT `FK_roba_pot` FOREIGN KEY (`pot`) REFERENCES `porezna_stopa` (`kljuc`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roba`
--

LOCK TABLES `roba` WRITE;
/*!40000 ALTER TABLE `roba` DISABLE KEYS */;
/*!40000 ALTER TABLE `roba` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roba_promjena_cijene`
--

DROP TABLE IF EXISTS `roba_promjena_cijene`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roba_promjena_cijene` (
  `ID` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `MALOPRODAJNACIJENA` double DEFAULT NULL,
  `POCETAK` datetime DEFAULT NULL,
  `ARTIKAL_KLJUC` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_roba_promjena_cijene_ARTIKAL_KLJUC` (`ARTIKAL_KLJUC`),
  CONSTRAINT `FK_roba_promjena_cijene_ARTIKAL_KLJUC` FOREIGN KEY (`ARTIKAL_KLJUC`) REFERENCES `roba` (`KLJUC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roba_promjena_cijene`
--

LOCK TABLES `roba_promjena_cijene` WRITE;
/*!40000 ALTER TABLE `roba_promjena_cijene` DISABLE KEYS */;
/*!40000 ALTER TABLE `roba_promjena_cijene` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sequence`
--

DROP TABLE IF EXISTS `sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence` (
  `SEQ_NAME` varchar(50) NOT NULL,
  `SEQ_COUNT` decimal(38,0) DEFAULT NULL,
  PRIMARY KEY (`SEQ_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence`
--

LOCK TABLES `sequence` WRITE;
/*!40000 ALTER TABLE `sequence` DISABLE KEYS */;
INSERT INTO `sequence` VALUES ('SEQ_GEN',0);
/*!40000 ALTER TABLE `sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stavke`
--

DROP TABLE IF EXISTS `stavke`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stavke` (
  `KLJUC` bigint(20) NOT NULL AUTO_INCREMENT,
  `tip` varchar(31) DEFAULT NULL,
  `UKUPNO` double NOT NULL,
  `MJERA` varchar(255) DEFAULT NULL,
  `IZNOS` double DEFAULT NULL,
  `POPUST` double DEFAULT NULL,
  `KOLICINA` double NOT NULL,
  `MALOPRODAJNACIJENA` double DEFAULT NULL,
  `ROBA` varchar(255) NOT NULL,
  `CIJENA` double NOT NULL,
  `RACUN_KLJUC` varchar(255) DEFAULT NULL,
  `ROBA_KLJUC` varchar(50) NOT NULL,
  `grupa` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`KLJUC`),
  KEY `FK_STAVKE_RACUN_KLJUC` (`RACUN_KLJUC`),
  KEY `FK_STAVKE_grupa` (`grupa`),
  KEY `FK_STAVKE_ROBA_KLJUC` (`ROBA_KLJUC`),
  CONSTRAINT `FK_STAVKE_grupa` FOREIGN KEY (`grupa`) REFERENCES `grupe_artikala` (`kljuc`),
  CONSTRAINT `FK_STAVKE_ROBA_KLJUC` FOREIGN KEY (`ROBA_KLJUC`) REFERENCES `roba` (`KLJUC`),
  CONSTRAINT `FK_STAVKE_RACUN_KLJUC` FOREIGN KEY (`RACUN_KLJUC`) REFERENCES `racun` (`KLJUC`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stavke`
--

LOCK TABLES `stavke` WRITE;
/*!40000 ALTER TABLE `stavke` DISABLE KEYS */;
/*!40000 ALTER TABLE `stavke` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `template`
--

DROP TABLE IF EXISTS `template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `template` (
  `oznaka` varchar(50) NOT NULL,
  `putanja` varchar(1000) DEFAULT NULL,
  `naziv` varchar(255) DEFAULT NULL,
  `OPIS` varchar(1000) DEFAULT NULL,
  `nacin_placanja` varchar(255) DEFAULT NULL,
  `prioritet` int(11) DEFAULT NULL,
  `format` mediumtext,
  `rok_placanja` int(11) DEFAULT NULL,
  `tip` varchar(255) DEFAULT NULL,
  `opis_placanja` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`oznaka`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `template`
--

LOCK TABLES `template` WRITE;
/*!40000 ALTER TABLE `template` DISABLE KEYS */;
INSERT INTO `template` VALUES ('094d590f-d769-4ca7-b2a8-6a556796cf09','reports/invoice-v2.jasper','Račun R-1','Racun {0}. Nije plaćen. {1}','T',600,'Molimo vas da gore navedeni iznos uplatite na naš žiro račun {žiro račun} sa pozivom na broj {oznaka računa}{napomena}{dodatne informacije}',7,'Plaćeno na transakcijski račun','Transakcijski račun'),('188bc6f7-f1c7-4d07-a280-4b4a409c1148','reports/invoice-v2.jasper','Račun R-1','Racun {0}. Diners. {1}','K',500,'Račun je plaćen Diners karticom.{napomena}{dodatne informacije}',NULL,'Diners kartica','Diners'),('48b0d540-af70-11de-8a39-0800200c9a66','reports/invoice-v2.jasper','Račun R-1','Racun {0}. Maestro. {1}','K',500,'Račun je plaćen Maestro karticom.{napomena}{dodatne informacije}',NULL,'Maestro kartica','Maestro'),('61db2855-1a0e-408a-a645-973a9566c6f4','reports/invoice-v2.jasper','Račun R-1','Racun {0}. American Express. {1}','K',500,'Račun je plaćen American Express karticom.{napomena}{dodatne informacije}',NULL,'American Express','American Express'),('668aedb2-6a6b-4376-ad7e-869ef3da1d98','reports/invoice-v2.jasper','Račun R-1','Racun {0}. Mastercard. {1}','K',500,'Račun je plaćen Mastercard karticom.{napomena}{dodatne informacije}',NULL,'Mastercard','Mastercard'),('8ec2ccfc-9578-401c-99c6-78c56d4c3e28','reports/invoice-v2.jasper','Račun R-1','Racun {0}. Visa. {1}','K',500,'Račun je plaćen Visa karticom.{napomena}{dodatne informacije}',NULL,'Visa kartica','Visa'),('ab5285cb-fbf7-42fd-86c5-ed58a4e9fa1c','reports/invoice-v2.jasper','Račun R-1','Racun {0}. Bez gotovinska uplata. {1}','T',700,'Molimo vas da gore navedeni iznos uplatite na naš žiro račun {žiro račun} sa pozivom na broj {oznaka računa}{napomena}{dodatne informacije}',NULL,'Transakcijski račun','Transakcijski račun'),('e1d758d5-15a6-43dd-a66a-1612ad25e378','reports/invoice-v2.jasper','Ponuda','Racun {0}. Ponuda. {1}',NULL,200,'Molimo vas da gore navedeni iznos uplatite na naš žiro račun {žiro račun} sa pozivom na broj {oznaka računa}{napomena}{dodatne informacije}',NULL,'Samo otvori novu ponudu','Ponuda'),('e454cd56-20d9-4921-a5d5-769888edf347','reports/invoice-v2.jasper','Račun R-1','Racun {0}. Gotovinska uplata. {1}','G',800,'Račun je naplaćen u gotovini.{napomena}{dodatne informacije}',NULL,'Gotovinska uplata','Gotovina');
/*!40000 ALTER TABLE `template` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-05-11 17:36:48
