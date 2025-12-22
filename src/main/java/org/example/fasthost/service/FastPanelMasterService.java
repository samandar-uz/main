package org.example.fasthost.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.fasthost.entity.Tariffs;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FastPanelMasterService {

    private final RestTemplate restTemplate;

    public void createMaster(String token,
                             String username,
                             String password,
                             String domain,
                             Tariffs tariffs) {

        String url = "https://panel.fasthost.uz:8888/api/master";

        log.info("FastPanel hosting yaratish boshlandi");
        log.info("Domain: {}, Username: {}, Tariff: {}", domain, username, tariffs.getName());

        // ================= ROOT BODY =================
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

        // ================= ALIASES =================
        List<Map<String, Object>> aliases = new ArrayList<>();
        Map<String, Object> alias = new HashMap<>();
        alias.put("name", "www." + domain);
        aliases.add(alias);
        body.put("aliases", aliases);

        // ================= IPS =================
        List<Map<String, Object>> ips = new ArrayList<>();
        Map<String, Object> ip = new HashMap<>();
        ip.put("ip", "151.243.169.216");
        ips.add(ip);
        body.put("ips", ips);

        // ================= LIMITS =================
        Map<String, Object> limits = new HashMap<>();
        limits.put("template_id", null);
        limits.put("databases", tariffs.getLimitDb());
        limits.put("dns_domains", tariffs.getLimitDomains());
        limits.put("email_domains", tariffs.getLimitEmails());
        limits.put("ftp_accounts", tariffs.getLimitFtpUsers());
        limits.put("sftp_accounts", 1); // SFTP default 1
        limits.put("sites", 1); // Bitta master uchun 1 ta site
        limits.put("users", 1); // Bitta user

        // ================= USER =================
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("password", password);

        user.put("quota", tariffs.getLimitQuota()* 1024);

        user.put("ssh_access", null);
        user.put("limits", limits);

        body.put("user", user);

        // ================= HEADERS =================
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // ================= REQUEST =================
        try {
            log.info("FastPanel API ga PUT so'rov yuborilmoqda...");
            log.debug("Tariff limitleri - DB: {}, Domains: {}, Emails: {}, FTP: {}, Quota: {} MB",
                    tariffs.getLimitDb(),
                    tariffs.getLimitDomains(),
                    tariffs.getLimitEmails(),
                    tariffs.getLimitFtpUsers(),
                    tariffs.getLimitQuota());

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("FastPanel xato qaytardi. Status: {}, Body: {}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException(
                        "FastPanel API xato: " + response.getStatusCode()
                );
            }

            log.info("Hosting muvaffaqiyatli yaratildi. Domain: {}", domain);
            log.debug("FastPanel response: {}", response.getBody());

        } catch (RestClientException e) {
            log.error("FastPanel API bilan bog'lanishda xatolik: {}", e.getMessage(), e);
            throw new RuntimeException(
                    "FastPanel bilan bog'lanib bo'lmadi: " + e.getMessage(), e
            );
        }
    }
}