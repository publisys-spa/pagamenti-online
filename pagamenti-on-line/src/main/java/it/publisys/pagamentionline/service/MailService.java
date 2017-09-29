package it.publisys.pagamentionline.service;

import it.publisys.pagamentionline.domain.enumeration.MailStatusEnum;
import it.publisys.pagamentionline.domain.enumeration.MailTypeEnum;
import it.publisys.pagamentionline.domain.user.Mail;
import it.publisys.pagamentionline.repository.MailRepository;
import it.publisys.pagamentionline.util.sec.SecurityUtil;
import it.publisys.spring.mail.MailEngine;
import java.util.List;
import java.util.logging.Level;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 *
 * @author mcolucci
 */
@Service("mailService")
public class MailService {

    private static final Logger _log = LoggerFactory.getLogger(MailService.class);
    //
    @Autowired
    private MailRepository mailRepository;
    @Autowired
    private MailEngine mailEngine;
    @Autowired
    private Environment env;

    public void create(Mail mail) {
        mail.setLogCreate(SecurityUtil.getPrincipalName());
        mailRepository.save(mail);
    }
    public void send(Mail _mail, String oggetto) {
        try {
            _log.info("Sto inviando la mail");
            mailEngine.send(_mail.getEmail(), oggetto, _mail.getText());
            _mail.setStatus(MailStatusEnum.LIVE);
            mailRepository.saveAndFlush(_mail);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MailService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void sendMailReady() {
        List<Mail> _list = mailRepository.findByStatus(MailStatusEnum.READY);
        _log.info("Mail da inviare: " + _list.size());

        for (Mail _mail : _list) {
            try {
                if (_mail.getType() == MailTypeEnum.SUPPORT) {
                    mailEngine.send(env.getProperty("mail.support"), "Richiesta supporto", _mail.getText());
                } else {
                    throw new IllegalArgumentException("Tipo di mail [" + _mail.getType() + "] non supportata.");
                }
                _mail.setStatus(MailStatusEnum.LIVE);
                mailRepository.saveAndFlush(_mail);
            } catch (Exception e) {
                _log.warn("Non sono riuscito ad inviare/salvare la mail [" + _mail + "]", e);
            }

        }
    }

}
