package theturbomonkey.northstar.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import theturbomonkey.northstar.model.entity.AppConfig;

public interface AppConfigRepository extends JpaRepository<AppConfig, Integer>
{

} // End AppConfigRepository Interface
