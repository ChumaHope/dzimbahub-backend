package zw.ac.msu.dzimbahub.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import zw.ac.msu.dzimbahub.security.JwtService;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
 private final AppUserRepository users; private final PasswordEncoder encoder; private final JwtService jwt;
 public AuthController(AppUserRepository users,PasswordEncoder encoder,JwtService jwt){this.users=users;this.encoder=encoder;this.jwt=jwt;}
 public record SignupRequest(@NotBlank @Size(min=2,max=100) String fullName,@NotBlank @Email @Size(max=180) String email,@NotBlank @Size(min=8,max=72) String password, UserRole role){}
 public record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
 public record UserResponse(Long id,String fullName,String email,String role){}
 public record AuthResponse(String token,UserResponse user){}
 private UserResponse toResponse(AppUser u){return new UserResponse(u.getId(),u.getFullName(),u.getEmail(),u.getRole().name());}
 @PostMapping("/signup") public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest body){
  String email=body.email().trim().toLowerCase(Locale.ROOT);
  if(users.existsByEmailIgnoreCase(email)) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","An account with this email already exists."));
  UserRole role=body.role()==null?UserRole.TENANT:body.role();
  if(role==UserRole.ADMIN) return ResponseEntity.badRequest().body(Map.of("message","Administrator accounts can only be created by an existing administrator."));
  AppUser user=users.save(new AppUser(body.fullName().trim(),email,encoder.encode(body.password()),role));
  return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(jwt.generate(user.getEmail(),user.getRole().name()),toResponse(user)));
 }
 @PostMapping("/login") public ResponseEntity<?> login(@Valid @RequestBody LoginRequest body){
  AppUser user=users.findByEmailIgnoreCase(body.email().trim()).orElse(null);
  if(user==null||!encoder.matches(body.password(),user.getPasswordHash())) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message","Email or password is incorrect."));
  return ResponseEntity.ok(new AuthResponse(jwt.generate(user.getEmail(),user.getRole().name()),toResponse(user)));
 }
 @GetMapping("/me") public UserResponse me(Authentication authentication){
  if(authentication==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Please sign in.");
  AppUser user=users.findByEmailIgnoreCase(authentication.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Account not found."));
  return toResponse(user);
 }
}
