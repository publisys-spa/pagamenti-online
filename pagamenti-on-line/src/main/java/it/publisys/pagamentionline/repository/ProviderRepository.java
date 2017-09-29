package it.publisys.pagamentionline.repository;

import it.publisys.pagamentionline.domain.impl.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Francesco A. Tabino
 */
@Transactional
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Page<Provider> findByLogdDateIsNull(Pageable pageable);

    Provider findByName(String name);

}
