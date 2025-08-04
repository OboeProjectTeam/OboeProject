package com.example.Oboe.aspect;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.Entity.AccountType;
import com.example.Oboe.Entity.User;
import com.example.Oboe.Repository.UserRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.Map;

@Aspect
@Component
public class PremiumAccessAspect {

    @Autowired
    private UserRepository userRepository;

    @Around("@annotation(com.example.Oboe.annotation.PremiumOnly)")
    public Object enforcePremiumAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails)) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", 401,
                    "error", "Bạn chưa đăng nhập"
            ));
        }

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        User user = userRepository.findById(userDetails.getUserID()).orElse(null);

        if (user == null || user.getAccountType() != AccountType.PREMIUM) {
            return ResponseEntity.status(403).body(Map.of(
                    "status", 403,
                    "error", "Tính năng này chỉ dành cho tài khoản Premium"
            ));
        }

        return joinPoint.proceed();
    }
}

