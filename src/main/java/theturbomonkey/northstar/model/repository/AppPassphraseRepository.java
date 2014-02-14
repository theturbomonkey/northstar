package theturbomonkey.northstar.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import theturbomonkey.northstar.model.entity.AppPassphrase;

public interface AppPassphraseRepository extends JpaRepository<AppPassphrase, Integer>
{

} // End AppPassphraseRepository Interface
