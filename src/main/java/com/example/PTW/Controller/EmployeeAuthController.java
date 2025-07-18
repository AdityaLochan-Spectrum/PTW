package com.example.PTW.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class EmployeeAuthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final boolean USE_HASHING = true; // true to match C# behavior

    // @PostMapping("/login")
    // public String login(@RequestParam String em_emp_code, @RequestParam String em_password) {
    //     try {
    //         System.out.println("Original password: " + em_password);
    //         System.out.println("Using key (emp_code): " + em_emp_code);

    //         String encryptedPassword = encrypt3DESWithKey(em_password, em_emp_code);
    //         System.out.println("Java Encrypted: " + encryptedPassword);

    //         String sql = "SELECT COUNT(*) FROM qbo.employeemaster WHERE em_emp_code = ? AND em_password = ?";
    //         Integer count = jdbcTemplate.queryForObject(sql, Integer.class, em_emp_code, encryptedPassword);

    //         return (count != null && count > 0)
    //                 ? "Login Successful!"
    //                 : "Unauthorized: Invalid credentials.";
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return "Error during login: " + e.getMessage();
    //     }
    // }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestParam String em_emp_code,
                                               @RequestParam String em_password) {
        try {
            // ——— 1. Encrypt the incoming password exactly as before ———
            String encryptedPassword = encrypt3DESWithKey(em_password, em_emp_code);
    
            // ——— 2. Authenticate ———
            String authSql = """
                SELECT COUNT(*) 
                FROM qbo.employeemaster
                WHERE em_emp_code = ? 
                  AND em_password = ?
            """;
            Integer found = jdbcTemplate.queryForObject(authSql, Integer.class, em_emp_code, encryptedPassword);
    
            if (found == null || found == 0) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse("Unauthorized: Invalid credentials.", List.of(), null, null, null, null));
            }
    
            // ——— 3. Authorize ———
            String menuSql = """
                SELECT DISTINCT rmenu.MenuId
                FROM   qbo.employeeRole  er
                JOIN   qbo.rolemenu      rmenu ON rmenu.RoleID = er.em_RoleId
                WHERE  er.em_emp_code = ?
            """;
    
            List<Long> menuIds = jdbcTemplate.query(
                    menuSql,
                    (rs, row) -> rs.getLong("MenuId"),
                    em_emp_code);
    
            // ——— 4. Fetch dimension and user details ———
            String userDetailsSql = """
                SELECT Dimension1, Dimension2, em_MobileNo, em_fullname
                FROM qbo.employeemaster
                WHERE em_emp_code = ?
            """;
    
            Map<String, Object> userDetailsMap = jdbcTemplate.queryForMap(userDetailsSql, em_emp_code);
    
            String dimension1 = (String) userDetailsMap.get("Dimension1");
            String dimension2 = (String) userDetailsMap.get("Dimension2");
            String mobileNo = (String) userDetailsMap.get("em_MobileNo");
            String fullName = (String) userDetailsMap.get("em_fullname");
    
            // ——— 5. Return the combined response ———
            return ResponseEntity.ok(
                    new LoginResponse("Login Successful!", menuIds, dimension1, dimension2, mobileNo, fullName)
            );
    
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse("Error during login: " + ex.getMessage(), List.of(), null, null, null, null));
        }
    }
    
    /**
     * Encrypts using 3DES compatible with C# (keyed by employee code).
     */
    public static String encrypt3DESWithKey(String plaintext, String dynamicKey) throws Exception {
        byte[] keyBytes;

        if (USE_HASHING) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            keyBytes = md.digest(dynamicKey.getBytes(StandardCharsets.UTF_8)); // 16 bytes
        } else {
            keyBytes = dynamicKey.getBytes(StandardCharsets.UTF_8);
        }

        // Extend to 24 bytes
        byte[] key24 = new byte[24];
        System.arraycopy(keyBytes, 0, key24, 0, 16);
        System.arraycopy(keyBytes, 0, key24, 16, 8); // repeat first 8 bytes

        SecretKeySpec key = new SecretKeySpec(key24, "DESede");

        Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = cipher.doFinal(plaintextBytes);

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record LoginResponse(
        String message,
        List<Long> allowedMenus,
        String dimension1,
        String dimension2,
        String mobileNo,
        String fullName
    ) {}
}
