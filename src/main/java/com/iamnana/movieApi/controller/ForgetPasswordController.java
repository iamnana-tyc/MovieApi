package com.iamnana.movieApi.controller;

import com.iamnana.movieApi.auth.entities.ForgetPassword;
import com.iamnana.movieApi.auth.entities.User;
import com.iamnana.movieApi.auth.repository.ForgetPasswordRepository;
import com.iamnana.movieApi.auth.repository.UserRepository;
import com.iamnana.movieApi.auth.utils.ChangePassword;
import com.iamnana.movieApi.dto.MailBody;
import com.iamnana.movieApi.exception.InvalidOtpException;
import com.iamnana.movieApi.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgetPassword/")
@RequiredArgsConstructor
public class ForgetPasswordController {
    private final UserRepository userRepository;
    private final MailService mailService;
    private final ForgetPasswordRepository forgetPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Please provide a valid email address"));

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("Please this is the otp for your forget password request: " + otp)
                .subject("OTP for forget password")
                .build();
        ForgetPassword fp = ForgetPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 70 * 1000))
                .user(user)
                .build();

        mailService.sendSimpleMessage(mailBody);
        forgetPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for verification!");
    }

    @PostMapping("verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Please provide a valid email address"));

        // we need to check that otp matches with the user email in our database
        ForgetPassword fp = forgetPasswordRepository.findByOtpAndUser(otp, user).
                orElseThrow(()-> new InvalidOtpException("Invalid OTP for the email: " + email));

        if(fp.getExpirationTime().before(Date.from(Instant.now()))){
            forgetPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired!", HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok("OTP verified!");
    }

    @PostMapping("changePassword/{email}")
    public ResponseEntity<String> changePassword(@RequestBody ChangePassword changePassword,
                                                 @PathVariable String email){

        // check if password and repeatPassword are the same
        if(!Objects.equals(changePassword.password(), changePassword.repeatPassword())){
            return new ResponseEntity<>("Please password don't match!", HttpStatus.EXPECTATION_FAILED);
        }
        // If password matches we need to encode our password
        String encodePassword = passwordEncoder.encode(changePassword.password());

        userRepository.updatePassword(email, encodePassword);

        return ResponseEntity.ok("Password has been successfully changed");
    }

    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
