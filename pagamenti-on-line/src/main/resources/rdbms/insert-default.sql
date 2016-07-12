INSERT INTO `users` (`id`, `version`, `email`, `enabled`, `firstname`, `lastname`, `password`, `username`, `fiscalcode`) VALUES (-1, 0, 'utente@publisys.it', 1, 'Admin', 'User', '21232f297a57a5a743894a0e4a801fc3', 'admin', 'DMNDMN00D00M000N');
INSERT INTO `users` (`id`, `version`, `email`, `enabled`, `firstname`, `lastname`, `password`, `username`, `fiscalcode`) VALUES (-2, 0, 'utente@gmail.com', 1, 'Demo', 'User', 'fe01ce2a7fbac8fafaed7c982a04e229', 'demo', 'DDDDDD0D00D000D');

INSERT INTO `users_roles` (`id`, `version`, `authority`, `user_id`) VALUES (-1, 0, 'ROLE_ADMIN', -1);
INSERT INTO `users_roles` (`id`, `version`, `authority`, `user_id`) VALUES (-2, 0, 'ROLE_USER', -1);
INSERT INTO `users_roles` (`id`, `version`, `authority`, `user_id`) VALUES (-3, 0, 'ROLE_USER', -2);

INSERT INTO `pagamenti_online`.`tipologie_tributi` (`nome`,`descrizione`,`tipo`,`codiceRadice`,`version`) VALUES ('IMU','IMU','PATRIMONIALE','IMU-','0');
INSERT INTO `pagamenti_online`.`tipologie_tributi` (`nome`,`descrizione`,`tipo`,`codiceRadice`,`version`) VALUES ('TASI','TASI','PATRIMONIALE','TAS-','0');
INSERT INTO `pagamenti_online`.`tipologie_tributi` (`nome`,`descrizione`,`tipo`,`codiceRadice`,`version`) VALUES ('MENSA_SCOLASTICA','MENSA SCOLASTICA','TRIBUTO','MS-','0');

INSERT INTO `pagamenti` (`id`, `logc_date`, `logc_user`, `logd_date`, `logd_user`, `logu_date`, `logu_user`, `version`, `atto_accertamento`, `causale`, `data_pagamento`, `date_processed`, `importo`, `importo_commissione`, `pid`, `refnumber`, `status_response`, `tipologia`, `beneficiario`, `esecutore`, `rata`, `tributo`, `ente`, `iur`, `stato_pagamento`, `key_wisp`, `ccp`) VALUES (66, '2016-02-01 12:17:26', 'demo', NULL, NULL, '2016-02-01 12:18:00', 'govpay', 2, NULL, 'Esempio Rata 1', '2016-02-01 12:17:26', NULL, 22.2, NULL, 'PAG-f5912b44-0a48-433d-b6ab-7ce6363cdb8c', 'RF14000000000000111', NULL, NULL, NULL, -2, 3, 3, 2, 'idRisc-RF14000000000000111-0', 'PAGATO', NULL, NULL);
