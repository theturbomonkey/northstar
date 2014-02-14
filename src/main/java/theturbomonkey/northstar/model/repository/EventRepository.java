package theturbomonkey.northstar.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import theturbomonkey.northstar.model.entity.Event;

public interface EventRepository extends JpaRepository<Event, Integer>
{

} // End EventRepository Interface
