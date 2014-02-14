package theturbomonkey.northstar.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import theturbomonkey.northstar.model.entity.AppTelemetry;

public interface AppTelemetryRepository extends JpaRepository<AppTelemetry, Integer>
{

} // End AppTelemetryRepository Interface
