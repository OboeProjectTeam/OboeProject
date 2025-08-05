package com.example.Oboe.aspect;

import com.example.Oboe.Config.CustomUserDetails;
import com.example.Oboe.Entity.AccountType;
import com.example.Oboe.Exception.AccessDeniedException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PremiumAspect {

    @Before("@annotation(com.example.Oboe.annotation.PremiumOnly)")
    public void checkPremiumUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            System.out.println(">> Checking premium access for: " + userDetails.getUsername());
            System.out.println(">> Account type: " + userDetails.getAccountType());

            if (userDetails.getAccountType() != AccountType.PREMIUM) {
                throw new AccessDeniedException("Bạn cần nâng cấp tài khoản Premium để sử dụng chức năng này.");
            }
        } else {
            throw new AccessDeniedException("Không thể xác định tài khoản.");
        }
    }
}
