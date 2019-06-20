INSERT INTO `db_xml_auth`.`admin` (`id_admina`, `email`, `ime`, `lozinka`, `prezime`) VALUES ('1', 'aaa', 'aaa', 'aaa', 'aaa');
INSERT INTO `db_xml_auth`.`korisnik` (`id_korisnik`, `blokiran`, `datum_clanstva`, `email`, `ime`, `lozinka`, `prezime`, `registrovan`, `rola`) VALUES ('1', b'0', '2019-01-01 00:00:00', 'aaa', 'aaa', 'aaa', 'aaa', b'1', 'ROLE_KORISNIK');
INSERT INTO `db_xml_auth`.`agent` (`id_agenta`, `datum_clanstva`, `email`, `ime`, `lozinka`, `poslovni_maticni_broj`, `prezime`) VALUES ('1', '2019-1-1', 'aaa', 'aaa', 'aaa', '1234', 'aaa');
INSERT INTO `db_xml_auth`.`rola` (`id`, `naziv_role`) VALUES ('1', 'ROLE_ADMIN');
INSERT INTO `db_xml_auth`.`rola` (`id`, `naziv_role`) VALUES ('2', 'ROLE_AGENT');
INSERT INTO `db_xml_auth`.`rola` (`id`, `naziv_role`) VALUES ('3', 'ROLE_KORISNIK');
