package com.devnaza.pumpwatch.config.filters;

import com.devnaza.pumpwatch.exception.InvalidTokenException;
import com.devnaza.pumpwatch.modules.auth.service.impl.AuthServiceImpl;
import com.devnaza.pumpwatch.modules.auth.service.impl.JWTServiceImpl;
import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
//@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTServiceImpl jwtServiceImpl;
    private final AuthServiceImpl authServiceImpl;


    private final HandlerExceptionResolver exceptionResolver;

    public JWTFilter(JWTServiceImpl jwtServiceImpl,
                     AuthServiceImpl authServiceImpl,
                     @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver){
        this.jwtServiceImpl = jwtServiceImpl;
        this.authServiceImpl = authServiceImpl;
        this.exceptionResolver = exceptionResolver;
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
            throw new ServletException("No Bearer Token Found");
        }

        String token = header.substring(7);
        if(!processToken(token, response, request)){
            return;
        }

        filterChain.doFilter(request, response);

    }

    boolean processToken(String token, HttpServletResponse res,
                         HttpServletRequest req) throws IOException {
        if(token != null){
          try{
              Jws<Claims> claimsJws = jwtServiceImpl.parseToken(token);
              Claims claims = claimsJws.getBody();

              String email = claims.getSubject();

              User user = authServiceImpl.loadUserByEmail(email);
              UserDto userDetails =
                      UserDto.builder().userId(user.getUserId()).firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail()).phoneNumber(user.getPhoneNumber()).build();

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
//              requestUnauthorized(res, "Token expired");
              exceptionResolver.resolveException(req, res, null,
                      new InvalidTokenException("Token Expired. Please Log " +
                                                        "in again."));
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
        res.getWriter().write("{\"message\":\"" + msg + "\"}");
    }
}
