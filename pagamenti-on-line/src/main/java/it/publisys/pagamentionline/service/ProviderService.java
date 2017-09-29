package it.publisys.pagamentionline.service;

import it.publisys.pagamentionline.PagamentiOnlineKey;
import it.publisys.pagamentionline.domain.impl.Ente;
import it.publisys.pagamentionline.domain.impl.Provider;
import it.publisys.pagamentionline.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

/**
 * @author Francesco A. Tabino
 */
@Service
public class ProviderService {

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private EnteService enteService;

    public Page<Provider> findAll(Pageable pageable) {
        return providerRepository.findByLogdDateIsNull(pageable);
    }

    public Provider getOne(Long id) {
        return providerRepository.findOne(id);
    }

    public boolean exists(Long id) {
        return providerRepository.exists(id);
    }

    public Provider findByName(String name){
        return providerRepository.findByName(name);
    }

    public Provider delete(Long id, String username) {
        Provider provider = this.getOne(id);
        provider.setLogdDate(new Date());
        provider.setLogdUser(username);
        return providerRepository.saveAndFlush(provider);

    }

    public Provider save(Provider provider, String username) {
        Ente ente = provider.getEnte();
        if (provider.getId() != null) {
            Provider one = this.getOne(provider.getId());
            one.setName(provider.getName());
            one.setProperties(provider.getProperties());
            one.setEnte(enteService.getOne(ente.getId()));
            one.setLoguUser(username);
            one.setLoguDate(new Date());
            return providerRepository.save(one);
        } else {
            provider.setEnte(enteService.getOne(ente.getId()));
            provider.setLogcUser(username);
            provider.setLogcDate(new Date());
            return providerRepository.save(provider);
        }
    }

    public Properties loadPropertiesGovPay() {
        Provider provider = this.findByName(PagamentiOnlineKey.GOVPAY);
        Properties prop = new Properties();
        try {
            prop.load(new ByteArrayInputStream(provider.getProperties().getBytes(StandardCharsets.UTF_8)));
        } catch (IOException ignored) {
        }
        return prop;
    }

}
