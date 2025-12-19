package org.example.fasthost.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class FastPanelMasterService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String createMaster(String token,
                               String username,
                               String password,
                               String domain) {

        String url = "https://panel.fasthost.uz:8888/api/master";

        Map<String, Object> body = new HashMap<>();

        body.put("domain", domain);
        body.put("email_domain", false);
        body.put("dns_domain", null);
        body.put("owner", null);
        body.put("ssh_access", true);
        body.put("database", null);
        body.put("type", "php");
        body.put("handler", "mpm_itk");
        body.put("handler_version", "81");
        body.put("ftp_account", null);
        body.put("sftp_account", null);
        body.put("backup_plan_id", null);

        body.put("aliases", List.of(
                Map.of("name", "www." + domain)
        ));

        body.put("ips", List.of(
                Map.of("ip", "151.243.169.216")
        ));

        Map<String, Object> limits = new HashMap<>();
        limits.put("template_id", 2);
        limits.put("databases", 1);
        limits.put("dns_domains", 1);
        limits.put("email_domains", 1);
        limits.put("ftp_accounts", 1);
        limits.put("sftp_accounts", 1);
        limits.put("sites", 1);
        limits.put("users", 1);

        body.put("user", Map.of(
                "username", username,
                "password", password,
                "quota", 1024000,
                "ssh_access", null,
                "limits", limits
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(body, headers),
                String.class
        );

        return response.getBody();
    }
}
