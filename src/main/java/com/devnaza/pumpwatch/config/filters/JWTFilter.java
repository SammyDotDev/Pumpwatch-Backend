package com.devnaza.pumpwatch.config.filters;

import com.devnaza.pumpwatch.modules.auth.service.JWTService;
import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.service.UserServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserServiceImpl userService;


    public JWTFilter(JWTService jwtService, UserServiceImpl userService){
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);


        if(header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            logger.info("No Bearer Token found");
            return;
        }

        String token = header.substring(7);
        if(!processToken(token, response)){
            return;
        }

        filterChain.doFilter(request, response);

    }

    boolean processToken(String token, HttpServletResponse res) throws IOException {
        if(token != null){
          try{
              Jws<Claims> claimsJws = jwtService.parseToken(token);
              Claims claims = claimsJws.getBody();

              String email = claims.getSubject();

              UserDto userDetails = userService.loadUserByEmail(email);
              List<String> roles = claims.get("roles", List.class);
              if(roles == null){
                  roles = List.of();
              }
              List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

              UsernamePasswordAuthenticationToken authenticationToken =
                      new UsernamePasswordAuthenticationToken(userDetails, null
                              , authorities);
              SecurityContextHolder.getContext().setAuthentication(authenticationToken);
              return true;
          } catch (ExpiredJwtException eje) {
              requestUnauthorized(res, "Token expired");
          } catch (JwtException | IllegalArgumentException exception){
              requestUnauthorized(res, "Invalid token");
          }

        }
        SecurityContextHolder.clearContext();
        return false;

    }

    private void requestUnauthorized(HttpServletResponse res, String msg) throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.getWriter().write("{\"error\":\"" + msg + "\"}");
    }
}
