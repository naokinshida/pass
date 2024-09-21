package com.example.nagoyameshi.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.form.PasswordResetForm;
import com.example.nagoyameshi.service.PasswordResetTokenService;

@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @GetMapping("/passwordreset")
    public String showForgotPasswordForm() {
        return "auth/passwordwasure";
    }

    @PostMapping("/passwordreset")
    public String processForgotPassword(
            @RequestParam("mailaddress") String mailaddress,
            HttpServletRequest request,
            Model model) {
        String serverUrl = request.getRequestURL().toString().replace(request.getServletPath(), "");
        try {
            passwordResetTokenService.createPasswordResetTokenForMemberinfo(mailaddress, serverUrl);
            model.addAttribute("successMessage", "パスワードリセット用のメールを送信しました。");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "そのメールアドレスは存在しません。");
        }
        return "auth/passwordwasure";
    }

    @GetMapping("/auth/passwordreset")
    public String showPasswordResetForm(@RequestParam("token") String token, Model model) {
        var resetToken = passwordResetTokenService.getPasswordResetToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            model.addAttribute("errorMessage", "トークンが無効または期限切れです。");
            return "auth/passwordreset";
        }
        model.addAttribute("token", token);
        model.addAttribute("passwordResetForm", new PasswordResetForm());
        return "auth/passwordreset";
    }

    @PostMapping("/auth/passwordreset")
    public String processPasswordReset(PasswordResetForm form, Model model) {
        boolean isSuccess = passwordResetTokenService.updatePassword(form.getToken(), form.getPassword());
        if (isSuccess) {
            model.addAttribute("successMessage", "パスワードが再設定されました。");
        } else {
            model.addAttribute("errorMessage", "パスワードの再設定に失敗しました。トークンが無効または期限切れです。");
        }
        return "auth/passwordreset";
    }
}