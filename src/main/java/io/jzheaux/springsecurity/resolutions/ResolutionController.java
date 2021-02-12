package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ResolutionController {
	private final ResolutionRepository resolutions;

	public ResolutionController(ResolutionRepository resolutions) {
		this.resolutions = resolutions;
	}

	@GetMapping("/resolutions")
	//@CrossOrigin(allowCredentials = "true") Doesn't run!
	@CrossOrigin //(maxAge = 0) if locally verifying
	@PreAuthorize("hasAuthority('resolution:read')")
	//@PostFilter("filterObject.owner == authentication.name")
	//@PostFilter("filterObject.owner == authentication.name || hasRole('ADMIN')")
	@PostFilter("@post.filter(#root)")
	public Iterable<Resolution> read() {
		return this.resolutions.findAll();
	}

	@GetMapping("/resolution/{id}")
	@PreAuthorize("hasAuthority('resolution:read')")
	//@PostAuthorize("returnObject.orElse(null)?.owner == authentication.name")
	//@PostAuthorize("returnObject.orElse(null)?.owner == authentication.name || hasRole('ADMIN')")
	@PostAuthorize("@post.authorize(#root)")
	public Optional<Resolution> read(@PathVariable("id") UUID id) {
		return this.resolutions.findById(id);
	}

//	@GetMapping("/resolution/{id}")
//	public Optional<Resolution> read(@PathVariable("id") String id) {
//		return this.resolutions.findById(UUID.fromString(id));
//	}

//	@PostMapping("/resolution")
//	public Resolution make( @RequestBody String text) {
//		String owner = "user";
//		Resolution resolution = new Resolution(text, owner);
//		return this.resolutions.save(resolution);
//	}

	@PostMapping("/resolution")
	@PreAuthorize("hasAuthority('resolution:write')")
	public Resolution make(@CurrentUsername String owner, @RequestBody String text) {
		Resolution resolution = new Resolution(text, owner);
		return this.resolutions.save(resolution);
	}

	@PutMapping(path="/resolution/{id}/revise")
	@PreAuthorize("hasAuthority('resolution:write')")
	//@PostAuthorize("returnObject.orElse(null)?.owner == authentication.name")
	@PostAuthorize("@post.authorize(#root)")
	@Transactional
	public Optional<Resolution> revise(@PathVariable("id") UUID id, @RequestBody String text) {
		this.resolutions.revise(id, text);
		return read(id);
	}

	@PutMapping("/resolution/{id}/complete")
	@PreAuthorize("hasAuthority('resolution:write')")
	//@PostAuthorize("returnObject.orElse(null)?.owner == authentication.name")
	@PostAuthorize("@post.authorize(#root)")
	@Transactional
	public Optional<Resolution> complete(@PathVariable("id") UUID id) {
		this.resolutions.complete(id);
		return read(id);
	}
}
