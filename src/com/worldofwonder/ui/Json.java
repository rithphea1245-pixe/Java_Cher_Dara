package com.worldofwonder.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class Json {

    private final String text;
    private int pos;

    private Json(String text) {
        this.text = text;
    }

    static Object parse(String text) {
        Json parser = new Json(text);
        Object value = parser.parseValue();
        parser.skipWs();
        if (parser.pos < text.length()) {
            throw new IllegalArgumentException("Trailing content at offset " + parser.pos);
        }
        return value;
    }

    private Object parseValue() {
        skipWs();
        if (pos >= text.length()) {
            throw new IllegalArgumentException("Unexpected end of input");
        }
        char c = text.charAt(pos);
        switch (c) {
            case '{':
                return parseObject();
            case '[':
                return parseArray();
            case '"':
                return parseString();
            case 't':
                expect("true");
                return Boolean.TRUE;
            case 'f':
                expect("false");
                return Boolean.FALSE;
            case 'n':
                expect("null");
                return null;
            default:
                if (c == '-' || (c >= '0' && c <= '9')) {
                    return parseNumber();
                }
                throw new IllegalArgumentException("Unexpected character '" + c + "' at offset " + pos);
        }
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        pos++;
        skipWs();
        if (peek('}')) {
            pos++;
            return map;
        }
        while (true) {
            skipWs();
            if (pos >= text.length() || text.charAt(pos) != '"') {
                throw new IllegalArgumentException("Expected string key at offset " + pos);
            }
            String key = parseString();
            skipWs();
            if (pos >= text.length() || text.charAt(pos) != ':') {
                throw new IllegalArgumentException("Expected ':' at offset " + pos);
            }
            pos++;
            map.put(key, parseValue());
            skipWs();
            if (peek('}')) {
                pos++;
                return map;
            }
            if (pos >= text.length() || text.charAt(pos) != ',') {
                throw new IllegalArgumentException("Expected ',' or '}' at offset " + pos);
            }
            pos++;
        }
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        pos++;
        skipWs();
        if (peek(']')) {
            pos++;
            return list;
        }
        while (true) {
            list.add(parseValue());
            skipWs();
            if (peek(']')) {
                pos++;
                return list;
            }
            if (pos >= text.length() || text.charAt(pos) != ',') {
                throw new IllegalArgumentException("Expected ',' or ']' at offset " + pos);
            }
            pos++;
        }
    }

    private String parseString() {
        pos++;
        StringBuilder sb = new StringBuilder();
        while (pos < text.length()) {
            char c = text.charAt(pos++);
            if (c == '"') {
                return sb.toString();
            }
            if (c == '\\') {
                if (pos >= text.length()) {
                    throw new IllegalArgumentException("Unterminated escape sequence");
                }
                char e = text.charAt(pos++);
                switch (e) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'u':
                        if (pos + 4 > text.length()) {
                            throw new IllegalArgumentException("Invalid unicode escape");
                        }
                        sb.append((char) Integer.parseInt(text.substring(pos, pos + 4), 16));
                        pos += 4;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid escape '\\" + e + "'");
                }
            } else {
                sb.append(c);
            }
        }
        throw new IllegalArgumentException("Unterminated string");
    }

    private Object parseNumber() {
        int start = pos;
        while (pos < text.length()) {
            char c = text.charAt(pos);
            if (Character.isDigit(c) || c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E') {
                pos++;
            } else {
                break;
            }
        }
        String raw = text.substring(start, pos);
        if (raw.isEmpty()) {
            throw new IllegalArgumentException("Invalid number at offset " + start);
        }
        if (raw.indexOf('.') >= 0 || raw.indexOf('e') >= 0 || raw.indexOf('E') >= 0) {
            return Double.parseDouble(raw);
        }
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return Long.parseLong(raw);
        }
    }

    private void expect(String literal) {
        if (!text.startsWith(literal, pos)) {
            throw new IllegalArgumentException("Expected '" + literal + "' at offset " + pos);
        }
        pos += literal.length();
    }

    private boolean peek(char c) {
        return pos < text.length() && text.charAt(pos) == c;
    }

    private void skipWs() {
        while (pos < text.length()) {
            char c = text.charAt(pos);
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                pos++;
            } else {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> asObject(Object value) {
        return (Map<String, Object>) value;
    }

    @SuppressWarnings("unchecked")
    static List<Object> asArray(Object value) {
        return (List<Object>) value;
    }

    static String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    static int num(Object value) {
        if (value == null) {
            return 0;
        }
        return ((Number) value).intValue();
    }
}
