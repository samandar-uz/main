package org.example.fasthost.entity.enums;

import lombok.Getter;

@Getter
public enum ParseMode {
    HTML("HTML"),
    MARKDOWN("Markdown"),
    MARKDOWN_V2("MarkdownV2"),
    NONE("");

    private final String value;

    ParseMode(String value) {
        this.value = value;
    }

}