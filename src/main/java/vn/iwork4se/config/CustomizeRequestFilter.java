package vn.iwork4se.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.iwork4se.common.TokenType;
import vn.iwork4se.model.Permission;
import vn.iwork4se.model.User;
import vn.iwork4se.repository.PermissionRepository;
import vn.iwork4se.service.JwtService;
import vn.iwork4se.service.UserServiceDetail;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j(topic = "CUSTOMIZE-FILTER")

public class CustomizeRequestFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserServiceDetail userServiceDetail;
    private final PermissionRepository permissionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("{} {}", request.getMethod(), request.getRequestURI());

        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.split(" ")[1].trim();
            log.info("Token: {}", token.substring(0,20));
            String userName = "";
            try {
                userName = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
                log.info("UserName from token: {}", userName);
            } catch (AccessDeniedException e) {
                log.info(e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(errorResponse(e.getMessage()));
                return;
            }

            UserDetails userDetails =  userServiceDetail.UserServiceDetail().loadUserByUsername(userName);

            if (userDetails instanceof User) {
                if (!hasPermission(userDetails, requestPath, requestMethod)) {
                    log.warn("User {} does not have permission to access {} {}", userName, requestMethod, requestPath);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(errorResponse("Bạn không có quyền truy cập tài nguyên này"));
                    return;
                }
            }
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);
            filterChain.doFilter(request, response);

        } else {
            filterChain.doFilter(request, response);
        }
    }

    private boolean hasPermission(UserDetails userDetails, String requestPath, String requestMethod) {
        try {
            User user = (User) userDetails;
            if (user.getRole() == null) {
                log.warn("User {} has no role assigned", user.getUsername());
                return false;
            }

            Long roleId = user.getRole().getId();
            log.info("Checking permissions for user {} with role ID {} for {} {}",
                    user.getUsername(), roleId, requestMethod, requestPath);


            List<Permission> exactPermissions = permissionRepository.findByRoleIdAndPathAndMethod(roleId, requestPath, requestMethod);
            if (!exactPermissions.isEmpty()) {
                log.info("Exact permission found for {} {}", requestMethod, requestPath);
                return true;
            }

            List<Permission> allPermissions = permissionRepository.findByRoleId(roleId);
            for (Permission permission : allPermissions) {
                if (permission.getMethod().equalsIgnoreCase(requestMethod) || permission.getMethod().equals("*")) {
                    String permissionPath = permission.getPath();
                    if (matchesPath(permissionPath, requestPath)) {
                        log.info("Wildcard permission found: {} matches {}", permissionPath, requestPath);
                        return true;
                    }
                }
            }

            log.warn("No matching permission found for user {} to access {} {}",
                    user.getUsername(), requestMethod, requestPath);
            return false;

        } catch (Exception e) {
            log.error("Error checking permissions for user {}: {}", userDetails.getUsername(), e.getMessage());
            return false;
        }
    }

    private boolean matchesPath(String permissionPath, String requestPath) {
        if (permissionPath.equals("*")) {
            return true;
        }

        if (permissionPath.endsWith("/*")) {
            String basePath = permissionPath.substring(0, permissionPath.length() - 2);
            return requestPath.startsWith(basePath);
        }

        if (permissionPath.contains("*")) {
            String regex = permissionPath.replace("*", ".*");
            return requestPath.matches(regex);
        }

        return permissionPath.equals(requestPath);
    }
    private String errorResponse(String message) {
        try {
            ErrorResponse error = new ErrorResponse();
            error.setTimestamp(new Date());
            error.setError("Forbidden");
            error.setStatus(HttpServletResponse.SC_FORBIDDEN);
            error.setMessage(message);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(error);
        } catch (Exception e) {
            return ""; // Return an empty string if serialization fails
        }
    }

    @Setter
    @Getter
    private class ErrorResponse {
        private Date timestamp;
        private int status;
        private String error;
        private String message;
    }
}
