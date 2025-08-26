package br.com.inoue.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.inoue.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        var servletPath = request.getServletPath();
        
        if(servletPath.startsWith("/tasks/")){
            var authorization = request.getHeader("Authorization");

            System.out.println(authorization);

            var user_password = authorization.substring("Basic".length()).trim();
            System.out.println(user_password);

            var decode = Base64.getDecoder().decode(user_password);
            System.out.println(decode);

            var auth = new String(decode);
            System.out.println(auth);

            String credentials[] = auth.split(":");

            String usuario = credentials[0];
            String senha = credentials[1];

            System.out.println("Usuário: " + usuario);
            System.out.println("Senha: " + senha);

            var user = this.userRepository.findByUserName(usuario);

            if(user == null){
                response.sendError(401, "Usuário sem autorização 1");
            } else {
                var passwordVerify = BCrypt.verifyer().verify(senha.toCharArray(), user.getPassword());

                if(passwordVerify.verified){
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401, "Usuário sem autorização 2");
                }

            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
        
}
