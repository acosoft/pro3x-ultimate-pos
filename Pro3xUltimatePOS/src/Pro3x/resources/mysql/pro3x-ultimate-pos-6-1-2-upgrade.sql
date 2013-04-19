alter table template 
    add column opis_placanja varchar(255) not null default '';

update template set opis_placanja = 'Transakcijski račun' where oznaka='094d590f-d769-4ca7-b2a8-6a556796cf09';
update template set opis_placanja = 'Diners' where oznaka='188bc6f7-f1c7-4d07-a280-4b4a409c1148';
update template set opis_placanja = 'Maestro' where oznaka='48b0d540-af70-11de-8a39-0800200c9a66';
update template set opis_placanja = 'American Express' where oznaka='61db2855-1a0e-408a-a645-973a9566c6f4';
update template set opis_placanja = 'Mastercard' where oznaka='668aedb2-6a6b-4376-ad7e-869ef3da1d98';
update template set opis_placanja = 'Visa' where oznaka='8ec2ccfc-9578-401c-99c6-78c56d4c3e28';
update template set opis_placanja = 'Transakcijski račun' where oznaka='ab5285cb-fbf7-42fd-86c5-ed58a4e9fa1c';
update template set opis_placanja = 'Ponuda' where oznaka='e1d758d5-15a6-43dd-a66a-1612ad25e378';
update template set opis_placanja = 'Gotovina' where oznaka='e454cd56-20d9-4921-a5d5-769888edf347';

update template set putanja = 'reports/invoice-v2.jasper';

alter table racun
    add column putanja_predloska varchar(255) not null default '';

update racun r
    set r.putanja_predloska = 'reports/invoice.jasper'
where r.verzija = 0;

update racun r
    set r.putanja_predloska = 'reports/invoice-v1.jasper'
where r.verzija = 1;