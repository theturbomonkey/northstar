package theturbomonkey.northstar.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import theturbomonkey.northstar.model.entity.Authorization;

public interface AuthorizationRepository  extends JpaRepository<Authorization, Integer>
{

} // End AuthorizationRepository Interface
