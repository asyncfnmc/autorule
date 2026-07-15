package com.autorule;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AutoRuleClient implements ClientModInitializer {
    private static final Pattern AUTOPET_PATTERN = Pattern.compile(
            ".*\\bAutopet\\b\\s+equipped\\s+your\\s+\\[\\s*Lvl\\s+(\\d+)\\s*]\\s+(.+?)\\s*!.*",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern TRAILING_PET_MARKERS = Pattern.compile(
            "(?:\\s*[✦★⭐✪✯✰✩✫✬✭✮✶✷✸✹✺✻✼✽✾✿❁❂❃❋]+\\s*)+$"
    );

    private static final Pattern COSMETIC_LEVEL_PATTERN = Pattern.compile(
            "^\\[(\\d+)([✦★⭐✪✯✰✩✫✬✭✮✶✷✸✹✺✻✼✽✾✿❁❂❃❋])]\s+(.+)$"
    );

    @Override
    public void onInitializeClient() {
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> {
            Component formattedMessage = formatAutopetMessage(message);
            return formattedMessage == null ? message : formattedMessage;
        });
    }

    public static Component formatAutopetMessage(Component message) {
        String rawMessage = message.getString();
        String plainMessage = stripLegacyFormatting(rawMessage);
        Matcher matcher = AUTOPET_PATTERN.matcher(plainMessage);

        if (!matcher.matches()) {
            return null;
        }

        String level = matcher.group(1);
        PetDisplay pet = parsePetDisplay(cleanPetName(matcher.group(2)));
        TextColor petColor = findLegacyPetNameColor(rawMessage, pet.baseName());
        if (petColor == null) {
            petColor = findLegacyPetNameColor(rawMessage, pet.fullName());
        }
        if (petColor == null) {
            petColor = findPetNameColor(message, pet.colorLookupName());
        }
        if (petColor == null) {
            petColor = TextColor.fromRgb(0xFFFFFF);
        }

        return Component.empty()
                .append(Component.literal("Petswap! >> ").withStyle(ChatFormatting.RED))
                .append(Component.literal("lvl " + level + " ").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xE7E7E7))))
                .append(pet.toText(petColor));
    }

    private static String cleanPetName(String rawPetName) {
        String petName = rawPetName
                .replaceAll("(?i)\\s*VIEW\\s+RULE.*$", "")
                .trim();

        petName = TRAILING_PET_MARKERS.matcher(petName).replaceAll("").trim();

        return petName;
    }

    private static PetDisplay parsePetDisplay(String petName) {
        Matcher matcher = COSMETIC_LEVEL_PATTERN.matcher(petName);
        if (!matcher.matches()) {
            return new PetDisplay(petName, null, null, null);
        }

        return new PetDisplay(petName, matcher.group(1), matcher.group(2), matcher.group(3));
    }

    private static String stripLegacyFormatting(String text) {
        return text.replaceAll("§[0-9A-FK-ORa-fk-or]", "");
    }

    private static TextColor findLegacyPetNameColor(String rawMessage, String petName) {
        if (rawMessage == null || petName == null || petName.isEmpty()) {
            return null;
        }

        int petNameStart = rawMessage.indexOf(petName);
        if (petNameStart < 0) {
            return null;
        }

        for (int i = petNameStart - 2; i >= 0; i--) {
            if (rawMessage.charAt(i) == '§' && i + 1 < rawMessage.length()) {
                ChatFormatting formatting = ChatFormatting.getByCode(rawMessage.charAt(i + 1));
                if (formatting != null && TextColor.fromLegacyFormat(formatting) != null) {
                    return TextColor.fromLegacyFormat(formatting);
                }
            }
        }

        return null;
    }

    private static TextColor findPetNameColor(Component message, String petName) {
        if (message == null || petName == null || petName.isEmpty()) {
            return null;
        }

        String plainMessage = message.getString();
        int petNameStart = plainMessage.indexOf(petName);

        if (petNameStart < 0) {
            return TextColor.fromRgb(0xFFFFFF);
        }

        int petNameEnd = petNameStart + petName.length();
        List<StyledPart> parts = new ArrayList<>();
        int[] cursor = {0};

        message.visit((style, text) -> {
            int start = cursor[0];
            int end = start + text.length();
            parts.add(new StyledPart(start, end, style));
            cursor[0] = end;
            return java.util.Optional.empty();
        }, Style.EMPTY);

        for (StyledPart part : parts) {
            if (part.end > petNameStart && part.start < petNameEnd && part.style.getColor() != null) {
                return part.style.getColor();
            }
        }

        return TextColor.fromRgb(0xFFFFFF);
    }

    private record PetDisplay(String fullName, String cosmeticLevel, String cosmeticSymbol, String baseName) {
        private String colorLookupName() {
            return baseName == null ? fullName : baseName;
        }

        private Component toText(TextColor petColor) {
            TextColor safePetColor = petColor == null ? TextColor.fromRgb(0xFFFFFF) : petColor;

            if (cosmeticLevel == null) {
                return Component.literal(fullName == null ? "" : fullName).setStyle(Style.EMPTY.withColor(safePetColor));
            }

            return Component.empty()
                    .append(Component.literal("[").setStyle(Style.EMPTY.withColor(safePetColor)))
                    .append(Component.literal(cosmeticLevel == null ? "" : cosmeticLevel).setStyle(Style.EMPTY.withColor(safePetColor)))
                    .append(Component.literal(cosmeticSymbol == null ? "" : cosmeticSymbol).withStyle(ChatFormatting.DARK_RED))
                    .append(Component.literal("] ").setStyle(Style.EMPTY.withColor(safePetColor)))
                    .append(Component.literal(baseName == null ? "" : baseName).setStyle(Style.EMPTY.withColor(safePetColor)));
        }
    }

    private record StyledPart(int start, int end, Style style) {
    }
}
