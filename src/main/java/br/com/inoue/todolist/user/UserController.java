package br.com.inoue.todolist.user;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;
 
    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel){
        
        var user = this.userRepository.findByUserName(userModel.getUserName());

        if(user != null){
            System.out.println("Usuário existente...");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário existente");
        }

        var passwordHash = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());

        userModel.setPassword(passwordHash);

        var userCreated = this.userRepository.save(userModel);

        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    } 

    @GetMapping("/")
    public void get(){
        System.out.println(Calendar.getInstance().getTime());
    } 
}
