INSERT INTO `users` (`id`, `version`, `email`, `enabled`, `firstname`, `lastname`, `password`, `username`, `fiscalcode`) VALUES (-1, 0, 'mcolucci@publisys.it', 1, 'Admin', 'User', '21232f297a57a5a743894a0e4a801fc3', 'admin', 'DMNDMN00D00M000N');
INSERT INTO `users` (`id`, `version`, `email`, `enabled`, `firstname`, `lastname`, `password`, `username`, `fiscalcode`) VALUES (-2, 0, 'mcolucci82@gmail.com', 1, 'Demo', 'User', 'fe01ce2a7fbac8fafaed7c982a04e229', 'demo', 'DDDDDD0D00D000D');

INSERT INTO `users_roles` (`id`, `version`, `authority`, `user_id`) VALUES (-1, 0, 'ROLE_ADMIN', -1);
INSERT INTO `users_roles` (`id`, `version`, `authority`, `user_id`) VALUES (-2, 0, 'ROLE_USER', -1);
INSERT INTO `users_roles` (`id`, `version`, `authority`, `user_id`) VALUES (-3, 0, 'ROLE_USER', -2);

INSERT INTO `pagamenti_online`.`tipologie_tributi` (`nome`,`descrizione`,`tipo`,`codiceRadice`,`version`) VALUES ('IMU','IMU','PATRIMONIALE','IMU-','0');
INSERT INTO `pagamenti_online`.`tipologie_tributi` (`nome`,`descrizione`,`tipo`,`codiceRadice`,`version`) VALUES ('TASI','TASI','PATRIMONIALE','TAS-','0');
INSERT INTO `pagamenti_online`.`tipologie_tributi` (`nome`,`descrizione`,`tipo`,`codiceRadice`,`version`) VALUES ('MENSA_SCOLASTICA','MENSA SCOLASTICA','TRIBUTO','MS-','0');
